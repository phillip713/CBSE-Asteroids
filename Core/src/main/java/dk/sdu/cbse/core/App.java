package dk.sdu.cbse.core;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessorService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class App extends Application {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Pane gameWindow = new Pane();
    private final Map<String, Polygon> polygons = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

        Scene scene = new Scene(gameWindow);
        registerInputHandlers(scene);

        stage.setScene(scene);
        stage.setTitle("Asteroids - CBSE");
        stage.setResizable(false);
        stage.show();

        // 1. Discover and launch all active plugins (Player, Enemy, etc.)
        for (IGamePluginService plugin : getServices(IGamePluginService.class)) {
            plugin.start(gameData, world);
        }

        // 2. Start the core high-frequency game loop
        new AnimationTimer() {
            private long lastUpdateTime = System.nanoTime();

            @Override
            public void handle(long now) {
                double delta = (now - lastUpdateTime) / 1000000000.0;
                lastUpdateTime = now;

                gameData.setDeltaTime(delta);

                update();
                draw();
            }
        }.start();
    }

    private void update() {
        // Run standard processors (Movement, AI, Controls)
        for (IEntityProcessorService processor : getServices(IEntityProcessorService.class)) {
            processor.process(gameData, world);
        }
        // Run post-processors (Collisions, Screen-wrapping)
        for (IPostEntityProcessorService postProcessor : getServices(IPostEntityProcessorService.class)) {
            postProcessor.process(gameData, world);
        }
    }

    private void draw() {
        // 1. Safe Cleanup: Remove shapes for entities no longer in the world context
        // Using removeIf avoids ConcurrentModificationExceptions completely
        polygons.keySet().removeIf(entityId -> {
            boolean missing = world.getEntities().stream().noneMatch(e -> e.getID().equals(entityId));
            if (missing) {
                gameWindow.getChildren().remove(polygons.get(entityId));
            }
            return missing;
        });

        // 2. Render or update active entities
        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity.getID());

            if (polygon == null) {
                polygon = new Polygon();

                // Extract our data-driven shape arrays
                double[] shapeX = entity.getShapeX();
                double[] shapeY = entity.getShapeY();

                if (shapeX != null && shapeX.length > 0) {
                    // Zip the relative X and Y coordinates into the JavaFX points sequence [x1, y1, x2, y2...]
                    for (int i = 0; i < shapeX.length; i++) {
                        polygon.getPoints().addAll(shapeX[i], shapeY[i]);
                    }
                } else {
                    // Fallback standard triangle if a module doesn't define custom geometry
                    polygon.getPoints().addAll(-6.0, -6.0, 10.0, 0.0, -6.0, 6.0);
                }

                // Stylize the wireframe to look like an old retro arcade cabinet
                polygon.setStroke(javafx.scene.paint.Color.WHITE);
                polygon.setFill(javafx.scene.paint.Color.BLACK); // or Color.TRANSPARENT
                polygon.setStrokeWidth(1.5);

                polygons.put(entity.getID(), polygon);
                gameWindow.getChildren().add(polygon);
            }

            // 3. Apply spatial transforms using JavaFX's built-in rendering engine
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(Math.toDegrees(entity.getRotation()));
        }
    }

    private void registerInputHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> toggleKey(event.getCode(), true));
        scene.setOnKeyReleased(event -> toggleKey(event.getCode(), false));
    }

    private void toggleKey(KeyCode code, boolean isPressed) {
        if (code == KeyCode.UP || code == KeyCode.W) gameData.setKey(GameData.UP, isPressed);
        if (code == KeyCode.LEFT || code == KeyCode.A) gameData.setKey(GameData.LEFT, isPressed);
        if (code == KeyCode.RIGHT || code == KeyCode.D) gameData.setKey(GameData.RIGHT, isPressed);
        if (code == KeyCode.SPACE) gameData.setKey(GameData.SPACE, isPressed);
    }

    // Generic helper utilizing ServiceLoader to pull implementations out of the mods-mvn folder at runtime
    private <T> Collection<T> getServices(Class<T> serviceType) {
        return ServiceLoader.load(serviceType).stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }
}