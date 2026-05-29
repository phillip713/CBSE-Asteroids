package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.EntityType;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;

public class CollisionDetector implements IPostEntityProcessorService {

    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity1 : world.getEntities()) {
            for (Entity entity2 : world.getEntities()) {

                // Prevent double-processing identical pairs in the same frame step
                if (entity1.getID().compareTo(entity2.getID()) >= 0) {
                    continue;
                }

                if (entity1.isDead() || entity2.isDead()) {
                    continue;
                }

                if (isColliding(entity1, entity2)) {
                    resolveCollision(entity1, entity2);
                }
            }
        }
    }

    private boolean isColliding(Entity e1, Entity e2) {
        double dx = e1.getX() - e2.getX();
        double dy = e1.getY() - e2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < (e1.getRadius() + e2.getRadius());
    }

    private void resolveCollision(Entity e1, Entity e2) {
        // Rule 1: Bullets hit Asteroids
        if (isTypePair(e1, e2, EntityType.BULLET, EntityType.ASTEROID)) {
            e1.reduceLife(1);
            e2.reduceLife(1);
        }

        // Rule 2: Ships hit Asteroids
        else if (isTypePair(e1, e2, EntityType.PLAYER, EntityType.ASTEROID)) {
            getOfSpecificType(e1, e2, EntityType.PLAYER).reduceLife(1);
            getOfSpecificType(e1, e2, EntityType.ASTEROID).reduceLife(1);
        }
        else if (isTypePair(e1, e2, EntityType.ENEMY, EntityType.ASTEROID)) {
            getOfSpecificType(e1, e2, EntityType.ENEMY).reduceLife(1);
            getOfSpecificType(e1, e2, EntityType.ASTEROID).reduceLife(1);
        }

        // Rule 3: Bullets hit opposing Ships
        else if (isTypePair(e1, e2, EntityType.BULLET, EntityType.PLAYER)) {
            getOfSpecificType(e1, e2, EntityType.BULLET).reduceLife(1);
            getOfSpecificType(e1, e2, EntityType.PLAYER).reduceLife(1);
        }
        else if (isTypePair(e1, e2, EntityType.BULLET, EntityType.ENEMY)) {
            getOfSpecificType(e1, e2, EntityType.BULLET).reduceLife(1);
            getOfSpecificType(e1, e2, EntityType.ENEMY).reduceLife(1);
        }
    }

    // Order-independent helper to see if a pair matches two targeted types
    private boolean isTypePair(Entity e1, Entity e2, EntityType t1, EntityType t2) {
        return (e1.getType() == t1 && e2.getType() == t2) || (e1.getType() == t2 && e2.getType() == t1);
    }

    // Order-independent helper to extract the correct object entity for targeted damage routing
    private Entity getOfSpecificType(Entity e1, Entity e2, EntityType type) {
        return (e1.getType() == type) ? e1 : e2;
    }
}