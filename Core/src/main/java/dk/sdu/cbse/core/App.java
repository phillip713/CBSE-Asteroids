package dk.sdu.cbse.core;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessorService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;

import dk.sdu.cbse.common.services.ServiceLocator;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class App extends Application {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Pane gameWindow = new Pane();
    private final Map<String, Polygon> polygons = new ConcurrentHashMap<>();
    private final Text scoreUiText = new Text();

    private Game game;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

        // 💡 Style and position our scoreboard overlay
        scoreUiText.setFont(new Font("Courier New", 20));
        scoreUiText.setFill(Color.GREENYELLOW);
        scoreUiText.setX(20);
        scoreUiText.setY(40);
        gameWindow.getChildren().add(scoreUiText);

        // 💡 1. Initialize Spring container and retrieve the DI-wired Game bean
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ModuleConfig.class);
        this.game = context.getBean(Game.class);

        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

        Scene scene = new Scene(gameWindow);
        registerInputHandlers(scene);

        stage.setScene(scene);
        stage.setTitle("Asteroids - CBSE - Spring DI");
        stage.setResizable(false);
        stage.show();

        // 💡 2. Use Spring's injected plugins instead of ServiceLocator
        for (IGamePluginService plugin : game.getPluginServices()) {
            plugin.start(gameData, world);
        }

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
        // 💡 3. Use Spring's injected processors
        for (IEntityProcessorService processor : game.getEntityProcessors()) {
            processor.process(gameData, world);
        }

        // 💡 4. Use Spring's injected post-processors
        for (IPostEntityProcessorService postProcessor : game.getPostEntityProcessors()) {
            postProcessor.process(gameData, world);
        }
    }

    private void draw() {
        // 💡 Update the text UI using our thread-safe local cache variable
        scoreUiText.setText(String.format("SCORE: %05d   LIVES: %d",
                ScoreSyncPostProcessor.currentScore,
                ScoreSyncPostProcessor.currentHealth));
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
}