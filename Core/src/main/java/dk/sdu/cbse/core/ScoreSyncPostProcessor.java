package dk.sdu.cbse.core;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;

public class ScoreSyncPostProcessor implements IPostEntityProcessorService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8081/api/state";

    private int lastAsteroidCount = -1;
    private boolean playerWasAlive = false;

    public static volatile int currentScore = 0;
    public static volatile int currentHealth = 3;

    public ScoreSyncPostProcessor(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        resetMicroservice();
    }

    @Override
    public void process(GameData gameData, World world) {
        int currentAsteroidCount = 0;
        boolean playerIsAlive = false;

        for (Entity e : world.getEntities()) {
            String name = e.getClass().getSimpleName().toLowerCase();
            if (name.contains("asteroid")) currentAsteroidCount++;
            if (name.contains("player")) playerIsAlive = true;
        }

        if (lastAsteroidCount == -1) {
            lastAsteroidCount = currentAsteroidCount;
            playerWasAlive = playerIsAlive;
            return;
        }

        // Event 1: An Asteroid vanished
        if (currentAsteroidCount < lastAsteroidCount) {
            int difference = lastAsteroidCount - currentAsteroidCount;
            CompletableFuture.runAsync(() -> {
                try {
                    // 💡 Uses clean path route, sends empty string body, requests plain String response
                    String response = restTemplate.postForObject(baseUrl + "/score/" + (difference * 10), "", String.class);
                    if (response != null) updateCache(response);
                } catch (Exception ex) {
                    System.err.println("Error updating score microservice: " + ex.getMessage());
                }
            });
        }

        // Event 2: Player vanished
        if (playerWasAlive && !playerIsAlive) {
            CompletableFuture.runAsync(() -> {
                try {
                    String response = restTemplate.postForObject(baseUrl + "/damage", "", String.class);
                    if (response != null) updateCache(response);
                } catch (Exception ex) {
                    System.err.println("Error updating health microservice: " + ex.getMessage());
                }
            });
        }

        lastAsteroidCount = currentAsteroidCount;
        playerWasAlive = playerIsAlive;
    }

    private void resetMicroservice() {
        CompletableFuture.runAsync(() -> {
            try {
                String response = restTemplate.postForObject(baseUrl + "/reset", "", String.class);
                if (response != null) updateCache(response);
            } catch (Exception ex) {
                System.err.println("Error resetting microservice: " + ex.getMessage());
            }
        });
    }

    // 💡 Lightweight custom parser that reads raw JSON strings natively without relying on Jackson!
    private synchronized void updateCache(String json) {
        try {
            // Extracts value after "score":
            String scorePart = json.split("\"score\":")[1].split(",")[0].trim();
            currentScore = Integer.parseInt(scorePart.replaceAll("[^0-9]", ""));

            // Extracts value after "health":
            String healthPart = json.split("\"health\":")[1].split("}")[0].trim();
            currentHealth = Integer.parseInt(healthPart.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            System.err.println("Failed to parse game state JSON string safely: " + json);
        }
    }
}