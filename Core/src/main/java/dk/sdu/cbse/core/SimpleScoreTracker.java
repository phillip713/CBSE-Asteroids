package dk.sdu.cbse.core;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;
import org.springframework.web.client.RestTemplate;

public class SimpleScoreTracker implements IPostEntityProcessorService {

    private final RestTemplate restTemplate = new RestTemplate();
    private int lastAsteroidCount = -1;

    public static int currentScore = 0;

    @Override
    public void process(GameData gameData, World world) {
        int currentAsteroidCount = 0;

        // Count how many asteroids are currently alive
        for (Entity e : world.getEntities()) {
            if (e.getClass().getSimpleName().toLowerCase().contains("asteroid")) {
                currentAsteroidCount++;
            }
        }

        // Establish baseline
        if (lastAsteroidCount == -1) {
            lastAsteroidCount = currentAsteroidCount;
            return;
        }

        // Count will drop on astroid destruction
        if (currentAsteroidCount < lastAsteroidCount) {
            int destroyed = lastAsteroidCount - currentAsteroidCount;
            int pointsEarned = destroyed * 10;

            String url = "http://localhost:8081/score?point=" + pointsEarned;

            try {
                // Read the response as String to bypass JSON converter issue
                String response = restTemplate.getForObject(url, String.class);

                if (response != null) {
                    // Convert text into score
                    currentScore = Integer.parseInt(response.trim());
                }
            } catch (Exception e) {
                System.err.println("Failed to reach scoring microservice: " + e.getMessage());
            }
        }
        lastAsteroidCount = currentAsteroidCount;
    }
}