package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

public interface IEntityProcessorService {
    /**
     * Pre-condition: gameData must contain a non-zero deltaTime step, and world must hold current state entities.
     * Post-condition: Systems mutate and step internal data properties (e.g. position vectors) based on controls or AI data streams.
     */
    void process(GameData gameData, World world);
}