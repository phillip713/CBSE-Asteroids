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

        // Spawn right in the middle of the game window
        playerShip.setX(gameData.getDisplayWidth() / 2.0);
        playerShip.setY(gameData.getDisplayHeight() / 2.0);

        playerShip.setRadius(8); // Used later for physics circle collisions
        playerShip.setRotation(Math.toRadians(270)); // Pointing straight up initially

        return playerShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Clean up the entity if this module gets unloaded dynamically
        world.removeEntity(player);
    }
}