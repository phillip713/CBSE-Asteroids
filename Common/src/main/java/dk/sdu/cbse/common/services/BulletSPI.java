package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;

public interface BulletSPI {
    /**
     * Pre-condition: shooter entity must exist with valid positional/rotational vectors.
     * Post-condition: Returns a brand new bullet Entity tracked along the shooter's forward trajectory.
     */
    Entity createBullet(Entity shooter, GameData gameData);
}