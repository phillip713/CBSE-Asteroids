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

        // Spawn randomly somewhere on screen
        enemyShip.setX(Math.random() * gameData.getDisplayWidth());
        enemyShip.setY(Math.random() * gameData.getDisplayHeight());

        enemyShip.setRadius(8);
        enemyShip.setRotation(Math.random() * Math.PI * 2); // Random initial heading

        return enemyShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.removeEntity(enemy);
    }
}