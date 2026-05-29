package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;

public class AsteroidPlugin implements IGamePluginService {

    private final int INITIAL_ASTEROIDS = 5;

    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < INITIAL_ASTEROIDS; i++) {
            world.addEntity(createLargeAsteroid(gameData));
        }
    }

    private Entity createLargeAsteroid(GameData gameData) {
        Asteroid asteroid = new Asteroid();
        asteroid.setType(EntityType.ASTEROID);
        asteroid.setSize(3); // Start as Large
        asteroid.setRadius(25);

        // Spawn randomly across the map layout
        asteroid.setX(Math.random() * gameData.getDisplayWidth());
        asteroid.setY(Math.random() * gameData.getDisplayHeight());

        // Constant drifting velocity vectors
        double speed = 40.0 + Math.random() * 30.0; // Random steady drift speed
        double heading = Math.random() * Math.PI * 2;
        asteroid.setDx(Math.cos(heading) * speed);
        asteroid.setDy(Math.sin(heading) * speed);

        // Give it a slow visual spin rate
        asteroid.setRotation(Math.random() * Math.PI * 2);

        return asteroid;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Purge all asteroids if the module gets dynamically unloaded
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.ASTEROID) {
                world.removeEntity(entity);
            }
        }
    }
}