package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.BulletSPI;
import dk.sdu.cbse.common.services.IEntityProcessorService;
import java.util.ServiceLoader;

public class EnemyControlSystem implements IEntityProcessorService {

    private final double rotationSpeed = 3.0;
    private final double acceleration = 80.0; // Slightly slower than player
    private final double friction = 0.99;

    @Override
    public void process(GameData gameData, World world) {
        double dt = gameData.getDeltaTime();

        for (Entity entity : world.getEntities()) {
            if (entity instanceof Enemy enemy) {
                // Remove entity if dead
                if (enemy.isDead()) {
                    world.removeEntity(entity);
                    continue;
                }
                // Handle Cooldowns
                if (enemy.getFireCooldown() > 0) {
                    enemy.setFireCooldown(enemy.getFireCooldown() - dt);
                }

                // --- Random Movement AI ---
                // 10% chance per frame step to alter rotation trajectory randomly
                double randomMove = Math.random();
                if (randomMove < 0.05) {
                    enemy.setRotation(enemy.getRotation() - rotationSpeed * dt);
                } else if (randomMove < 0.10) {
                    enemy.setRotation(enemy.getRotation() + rotationSpeed * dt);
                }

                // Constant ambient forward thrust acceleration
                enemy.setDx(enemy.getDx() + Math.cos(enemy.getRotation()) * acceleration * dt);
                enemy.setDy(enemy.getDy() + Math.sin(enemy.getRotation()) * acceleration * dt);

                // --- Random Shooting AI ---
                // If cooldown is clear, 2% chance per frame step to execute weapon firing operations
                if (enemy.getFireCooldown() <= 0 && Math.random() < 0.02) {
                    ServiceLoader.load(BulletSPI.class).stream()
                            .map(ServiceLoader.Provider::get)
                            .forEach(bulletSPI -> {
                                Entity bullet = bulletSPI.createBullet(enemy, gameData);
                                world.addEntity(bullet);
                            });
                    enemy.setFireCooldown(0.5); // Half-second fire cooldown for enemy
                }

                // Physics Updates
                enemy.setX(enemy.getX() + enemy.getDx() * dt);
                enemy.setY(enemy.getY() + enemy.getDy() * dt);
                enemy.setDx(enemy.getDx() * friction);
                enemy.setDy(enemy.getDy() * friction);

                // Screen Wrapping
                if (enemy.getX() < 0) enemy.setX(gameData.getDisplayWidth());
                if (enemy.getX() > gameData.getDisplayWidth()) enemy.setX(0);
                if (enemy.getY() < 0) enemy.setY(gameData.getDisplayHeight());
                if (enemy.getY() > gameData.getDisplayHeight()) enemy.setY(0);
            }
        }
    }
}