module Player {
    requires Common;

    uses dk.sdu.cbse.common.services.BulletSPI;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.player.PlayerPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessorService with dk.sdu.cbse.player.PlayerControlSystem;
}