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
import javafx.scene.text.Text;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App extends Application {

    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Pane gameWindow = new Pane();
    private final Map<String, Polygon> polygons = new ConcurrentHashMap<>();

    private final Text scoreText = new Text("SCORE: 0");

    private Game game;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Initialize Spring container and retrieve the DI-wired Game bean
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ModuleConfig.class);
        this.game = context.getBean(Game.class);

        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

        scoreText.setX(20);
        scoreText.setY(40);
        scoreText.setFill(javafx.scene.paint.Color.BLACK);
        scoreText.setStyle("-fx-font-size: 20px; -fx-font-family: 'Courier New';");
        gameWindow.getChildren().add(scoreText);

        Scene scene = new Scene(gameWindow);
        registerInputHandlers(scene);

        stage.setScene(scene);
        stage.setTitle("Asteroids - CBSE - Spring DI");
        stage.setResizable(false);
        stage.show();

        // Use Spring's injected plugins
        for (IGamePluginService plugin : game.getPluginServices()) {
            plugin.start(gameData, world);
        }

        new AnimationTimer() {
            private long lastUpdateTime = System.nanoTime();

            // Handle delta time
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
        // Use Spring's injected processors
        for (IEntityProcessorService processor : game.getEntityProcessors()) {
            processor.process(gameData, world);
        }

        // Use Spring's injected post-processors
        for (IPostEntityProcessorService postProcessor : game.getPostEntityProcessors()) {
            postProcessor.process(gameData, world);
        }
    }

    private void draw() {
        scoreText.setText("SCORE: " + SimpleScoreTracker.currentScore);
        scoreText.toFront();
        // Cleanup
        polygons.keySet().removeIf(entityId -> {
            boolean missing = world.getEntities().stream().noneMatch(e -> e.getID().equals(entityId));
            if (missing) {
                gameWindow.getChildren().remove(polygons.get(entityId));
            }
            return missing;
        });

        // Render or update active entities
        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity.getID());

            if (polygon == null) {
                polygon = new Polygon();

                double[] shapeX = entity.getShapeX();
                double[] shapeY = entity.getShapeY();

                if (shapeX != null && shapeX.length > 0) {
                    for (int i = 0; i < shapeX.length; i++) {
                        polygon.getPoints().addAll(shapeX[i], shapeY[i]);
                    }
                } else {
                    // Standard triangle fallback
                    polygon.getPoints().addAll(-6.0, -6.0, 10.0, 0.0, -6.0, 6.0);
                }

                // Stylization
                polygon.setStroke(javafx.scene.paint.Color.WHITE);
                polygon.setFill(javafx.scene.paint.Color.BLACK);
                polygon.setStrokeWidth(1.5);

                polygons.put(entity.getID(), polygon);
                gameWindow.getChildren().add(polygon);
            }

            // Apply spatial transforms
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(Math.toDegrees(entity.getRotation()));
        }
    }

    // Simple keypress setup
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