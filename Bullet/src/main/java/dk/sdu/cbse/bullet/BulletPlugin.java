package dk.sdu.cbse.bullet;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.BulletSPI;
import dk.sdu.cbse.common.services.IGamePluginService;

public class BulletPlugin implements BulletSPI, IGamePluginService {

    @Override
    public void start(GameData gameData, World world) {
        // The bullet module doesn't need to spawn anything at game boot.
        // It waits around until the Player or Enemy requests a bullet via BulletSPI.
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData) {
        Entity bullet = new Bullet();
        bullet.setLife(1);
        double speed = 350.0;
        bullet.setType(EntityType.BULLET);

        // Spawn slightly ahead of the shooter's nose
        bullet.setX(shooter.getX() + Math.cos(shooter.getRotation()) * shooter.getRadius());
        bullet.setY(shooter.getY() + Math.sin(shooter.getRotation()) * shooter.getRadius());

        // Match trajectory orientation
        bullet.setRotation(shooter.getRotation());
        bullet.setDx(Math.cos(shooter.getRotation()) * speed);
        bullet.setDy(Math.sin(shooter.getRotation()) * speed);
        bullet.setRadius(2);

        return bullet;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Lifecycle Cleanup: If the bullet component is unloaded,
        // cleanly purge all lingering projectiles from the active simulation.
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.BULLET) {
                world.removeEntity(entity);
            }
        }
    }
}