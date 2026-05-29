package dk.sdu.cbse.bullet;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessorService;

public class BulletControlSystem implements IEntityProcessorService {

    @Override
    public void process(GameData gameData, World world) {
        double dt = gameData.getDeltaTime();

        for (Entity entity : world.getEntities()) {
            if (entity instanceof Bullet) {
                Bullet bullet = (Bullet) entity;

                // Decay lifespan
                bullet.setExpiration(bullet.getExpiration() - dt);
                if (bullet.getExpiration() <= 0) {
                    world.removeEntity(bullet);
                    continue;
                }

                // Move bullet
                bullet.setX(bullet.getX() + bullet.getDx() * dt);
                bullet.setY(bullet.getY() + bullet.getDy() * dt);

                // Screen wrapping fallback
                if (bullet.getX() < 0) bullet.setX(gameData.getDisplayWidth());
                if (bullet.getX() > gameData.getDisplayWidth()) bullet.setX(0);
                if (bullet.getY() < 0) bullet.setY(gameData.getDisplayHeight());
                if (bullet.getY() > gameData.getDisplayHeight()) bullet.setY(0);
            }
        }
    }
}