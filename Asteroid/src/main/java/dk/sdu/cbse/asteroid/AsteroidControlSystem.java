package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessorService;

public class AsteroidControlSystem implements IEntityProcessorService {

    private final double rotationSpeed = 0.5;

    @Override
    public void process(GameData gameData, World world) {
        double dt = gameData.getDeltaTime();

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.ASTEROID) {
                Asteroid asteroid = (Asteroid) entity;

                // Handle Splitting and Destruction
                if (asteroid.isDead()) {
                    if (asteroid.getSize() > 1) {
                        int newSize = asteroid.getSize() - 1;
                        // Spawn chunks directly via factory at current coordinates
                        world.addEntity(AsteroidFactory.createAsteroid(newSize, asteroid.getX(), asteroid.getY()));
                        world.addEntity(AsteroidFactory.createAsteroid(newSize, asteroid.getX(), asteroid.getY()));
                    }
                    world.removeEntity(asteroid);
                    continue;
                }

                // Standard Movement Updates
                asteroid.setX(asteroid.getX() + asteroid.getDx() * dt);
                asteroid.setY(asteroid.getY() + asteroid.getDy() * dt);
                asteroid.setRotation(asteroid.getRotation() + rotationSpeed * dt);

                if (asteroid.getX() < 0) asteroid.setX(gameData.getDisplayWidth());
                if (asteroid.getX() > gameData.getDisplayWidth()) asteroid.setX(0);
                if (asteroid.getY() < 0) asteroid.setY(gameData.getDisplayHeight());
                if (asteroid.getY() > gameData.getDisplayHeight()) asteroid.setY(0);
            }
        }
    }
}