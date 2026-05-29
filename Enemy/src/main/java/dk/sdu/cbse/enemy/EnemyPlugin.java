package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;

public class EnemyPlugin implements IGamePluginService {

    private Entity enemy;

    @Override
    public void start(GameData gameData, World world) {
        enemy = createEnemyShip(gameData);
        world.addEntity(enemy);
    }

    private Entity createEnemyShip(GameData gameData) {
        Entity enemyShip = new Enemy();
        enemyShip.setType(EntityType.ENEMY);
        enemyShip.setLife(3);

        // Spawn at random position and rotation
        enemyShip.setX(Math.random() * gameData.getDisplayWidth());
        enemyShip.setY(Math.random() * gameData.getDisplayHeight());
        enemyShip.setRadius(8);
        enemyShip.setRotation(Math.random() * Math.PI * 2);

        // Hexagonal flying saucer geometry
        enemyShip.setShapeX(new double[]{10, 5, -5, -10, -5, 5});
        enemyShip.setShapeY(new double[]{0, 5, 5, 0, -5, -5});

        return enemyShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Clean up if unloaded
        world.removeEntity(enemy);
    }
}