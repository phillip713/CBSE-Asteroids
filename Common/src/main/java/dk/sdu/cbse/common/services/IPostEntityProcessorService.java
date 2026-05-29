package dk.sdu.cbse.common.services;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

public interface IPostEntityProcessorService {
    /**
     * Pre-condition: This pass executes *after* all baseline IEntityProcessorServices complete their mutations.
     * Post-condition: Resolves global environmental constraints across entities (e.g. tracking boundary overlaps, enforcing structural screen wrapping, or updating structural state damage flags).
     */
    void process(GameData gameData, World world);
}