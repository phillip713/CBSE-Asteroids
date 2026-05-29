package dk.sdu.cbse.player;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessorService;

public class PlayerControlSystem implements IEntityProcessorService {

    private final double rotationSpeed = 4.0; // Radians per second
    private final double acceleration = 150.0; // Pixels per second squared
    private final double friction = 0.99; // Simple drag coefficient

    @Override
    public void process(GameData gameData, World world) {
        double dt = gameData.getDeltaTime();

        for (Entity player : world.getEntities()) {
            if (player instanceof Player) {

                // Handle Rotational Input
                if (gameData.isKeyPressed(GameData.LEFT)) {
                    player.setRotation(player.getRotation() - rotationSpeed * dt);
                }
                if (gameData.isKeyPressed(GameData.RIGHT)) {
                    player.setRotation(player.getRotation() + rotationSpeed * dt);
                }

                // Handle Thrust Input (Accelerate along forward vector)
                if (gameData.isKeyPressed(GameData.UP)) {
                    double changeX = Math.cos(player.getRotation()) * acceleration * dt;
                    double changeY = Math.sin(player.getRotation()) * acceleration * dt;
                    player.setDx(player.getDx() + changeX);
                    player.setDy(player.getDy() + changeY);
                }

                // Apply simple kinetic updates & ambient environment drag
                player.setX(player.getX() + player.getDx() * dt);
                player.setY(player.getY() + player.getDy() * dt);

                player.setDx(player.getDx() * friction);
                player.setDy(player.getDy() * friction);

                // Screen boundaries fallback (so you don't fly away instantly)
                if (player.getX() < 0) player.setX(gameData.getDisplayWidth());
                if (player.getX() > gameData.getDisplayWidth()) player.setX(0);
                if (player.getY() < 0) player.setY(gameData.getDisplayHeight());
                if (player.getY() > gameData.getDisplayHeight()) player.setY(0);
            }
        }
    }
}