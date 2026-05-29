package dk.sdu.cbse.asteroid;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessorService;

public class AsteroidControlSystem implements IEntityProcessorService {

    private final double rotationSpeed = 0.5; // Slow visual tumble rate

    @Override
    public void process(GameData gameData, World world) {
        double dt = gameData.getDeltaTime();

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.ASTEROID) {
                Asteroid asteroid = (Asteroid) entity;

                // Step position coordinates by constant velocity vector
                asteroid.setX(asteroid.getX() + asteroid.getDx() * dt);
                asteroid.setY(asteroid.getY() + asteroid.getDy() * dt);

                // Continuous visual spin rotation step
                asteroid.setRotation(asteroid.getRotation() + rotationSpeed * dt);

                // Screen boundaries wrapping
                if (asteroid.getX() < 0) asteroid.setX(gameData.getDisplayWidth());
                if (asteroid.getX() > gameData.getDisplayWidth()) asteroid.setX(0);
                if (asteroid.getY() < 0) asteroid.setY(gameData.getDisplayHeight());
                if (asteroid.getY() > gameData.getDisplayHeight()) asteroid.setY(0);
            }
        }
    }
}