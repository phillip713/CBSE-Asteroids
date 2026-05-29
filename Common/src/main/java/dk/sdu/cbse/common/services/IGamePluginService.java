package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

public interface IGamePluginService {
    /**
     * Pre-condition: The game engine must initialize gameData and world context instances.
     * Post-condition: The component creates its entities and safely registers them into the world registry.
     */
    void start(GameData gameData, World world);

    /**
     * Pre-condition: The component must have previously execution-registered active entities inside the world context.
     * Post-condition: The component systematically scrubs and deregisters all its associated entities from the active world context.
     */
    void stop(GameData gameData, World world);
}