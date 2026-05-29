package dk.sdu.cbse.player;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;

public class PlayerPlugin implements IGamePluginService {

    private Entity player;

    @Override
    public void start(GameData gameData, World world) {
        player = createPlayerShip(gameData);
        world.addEntity(player);
    }

    private Entity createPlayerShip(GameData gameData) {
        Entity playerShip = new Player();
        playerShip.setType(EntityType.PLAYER);
        playerShip.setLife(5);

        // Spawn at center of screen
        playerShip.setX(gameData.getDisplayWidth() / 2.0);
        playerShip.setY(gameData.getDisplayHeight() / 2.0);
        playerShip.setRadius(8);
        playerShip.setRotation(Math.toRadians(270));

        // Define relative offsets for a sleek ship
        playerShip.setShapeX(new double[]{8, -8, -4, -8});
        playerShip.setShapeY(new double[]{0, -6, 0, 6});

        return playerShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Clean up if unloaded
        world.removeEntity(player);
    }
}