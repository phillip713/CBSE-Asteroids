package dk.sdu.cbse.player;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IBulletSPI;
import dk.sdu.cbse.common.services.IEntityProcessorService;
import java.util.ServiceLoader;

public class PlayerControlSystem implements IEntityProcessorService {

    private final double rotationSpeed = 4.0;
    private final double acceleration = 150.0;
    private final double friction = 0.99;

    @Override
    public void process(GameData gameData, World world) {
        double dt = gameData.getDeltaTime();

        for (Entity entity : world.getEntities()) {

            if (entity instanceof Player player) {
                // Remove entity if dead
                if (player.isDead()) {
                    world.removeEntity(entity);
                    continue;
                }

                // Fire cooldown
                if (player.getFireCooldown() > 0) {
                    player.setFireCooldown(player.getFireCooldown() - dt);
                }

                // Movement inputs
                if (gameData.isKeyPressed(GameData.LEFT)) {
                    player.setRotation(player.getRotation() - rotationSpeed * dt);
                }
                if (gameData.isKeyPressed(GameData.RIGHT)) {
                    player.setRotation(player.getRotation() + rotationSpeed * dt);
                }
                if (gameData.isKeyPressed(GameData.UP)) {
                    player.setDx(player.getDx() + Math.cos(player.getRotation()) * acceleration * dt);
                    player.setDy(player.getDy() + Math.sin(player.getRotation()) * acceleration * dt);
                }

                // Weapon firing input
                if (gameData.isKeyPressed(GameData.SPACE) && player.getFireCooldown() <= 0) {
                    // Look up bullet factories
                    ServiceLoader.load(IBulletSPI.class).stream()
                            .map(ServiceLoader.Provider::get)
                            .forEach(bulletSPI -> {
                                Entity bullet = bulletSPI.createBullet(player, gameData);
                                world.addEntity(bullet);
                            });
                    player.setFireCooldown(0.25); // Set a quarter-second weapon cooldown
                }

                player.setX(player.getX() + player.getDx() * dt);
                player.setY(player.getY() + player.getDy() * dt);
                player.setDx(player.getDx() * friction);
                player.setDy(player.getDy() * friction);

                // Boundaries
                if (player.getX() < 0) player.setX(gameData.getDisplayWidth());
                if (player.getX() > gameData.getDisplayWidth()) player.setX(0);
                if (player.getY() < 0) player.setY(gameData.getDisplayHeight());
                if (player.getY() > gameData.getDisplayHeight()) player.setY(0);
            }
        }
    }
}