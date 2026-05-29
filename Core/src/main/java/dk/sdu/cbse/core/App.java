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
        // Clean up visual shapes for entities removed from the world context
        for (String entityId : polygons.keySet()) {
            if (world.getEntities().stream().noneMatch(e -> e.getID().equals(entityId))) {
                gameWindow.getChildren().remove(polygons.get(entityId));
                polygons.remove(entityId);
            }
        }

        // Render or update active entities
        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity.getID());

            if (polygon == null) {
                polygon = new Polygon();
                // Placeholder triangle shape if the component doesn't define specific polygon coordinates yet
                polygon.getPoints().addAll(-6.0, -6.0, 10.0, 0.0, -6.0, 6.0);
                polygons.put(entity.getID(), polygon);
                gameWindow.getChildren().add(polygon);
            }

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