module Player {
    requires Common;

    // Registers these classes to be picked up by Core's ServiceLoader calls
    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.player.PlayerPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessorService with dk.sdu.cbse.player.PlayerControlSystem;
}