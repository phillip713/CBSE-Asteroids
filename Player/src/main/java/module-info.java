import dk.sdu.cbse.common.services.IBulletSPI;

module Player {
    requires Common;

    uses IBulletSPI;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.player.PlayerPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessorService with dk.sdu.cbse.player.PlayerControlSystem;
}