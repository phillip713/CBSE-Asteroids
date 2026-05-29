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

        // This single line now handles size, radius, and wireframe points!
        asteroid.setSize(3);

        asteroid.setX(Math.random() * gameData.getDisplayWidth());
        asteroid.setY(Math.random() * gameData.getDisplayHeight());

        double speed = 40.0 + Math.random() * 30.0;
        double heading = Math.random() * Math.PI * 2;
        asteroid.setDx(Math.cos(heading) * speed);
        asteroid.setDy(Math.sin(heading) * speed);
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