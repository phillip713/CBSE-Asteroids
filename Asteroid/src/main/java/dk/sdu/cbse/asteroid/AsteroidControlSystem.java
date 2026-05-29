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
                        // Spawn two smaller split segments
                        world.addEntity(createChildAsteroid(asteroid, asteroid.getSize() - 1));
                        world.addEntity(createChildAsteroid(asteroid, asteroid.getSize() - 1));
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

    private Entity createChildAsteroid(Asteroid parent, int newSize) {
        Asteroid child = new Asteroid();
        child.setType(EntityType.ASTEROID);
        child.setLife(1);
        child.setSize(newSize);

        // Scale physics properties based on split target tier
        if (newSize == 2) child.setRadius(15);
        if (newSize == 1) child.setRadius(7);

        // Inherit parent location context
        child.setX(parent.getX());
        child.setY(parent.getY());

        // Diverge velocity paths outwards dynamically
        double randomAngle = Math.random() * Math.PI * 2;
        double speed = 60.0 + Math.random() * 40.0; // Smaller chunks drift faster
        child.setDx(Math.cos(randomAngle) * speed);
        child.setDy(Math.sin(randomAngle) * speed);

        return child;
    }
}