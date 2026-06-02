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
            double x = Math.random() * gameData.getDisplayWidth();
            double y = Math.random() * gameData.getDisplayHeight();

            world.addEntity(AsteroidFactory.createAsteroid(3, x, y)); // Large asteroid
        }
    }


    @Override
    public void stop(GameData gameData, World world) {
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.ASTEROID) {
                world.removeEntity(entity);
            }
        }
    }
}