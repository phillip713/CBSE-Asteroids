module Enemy {
    requires Common;

    // Consumes the bullet factory interface
    uses dk.sdu.cbse.common.services.BulletSPI;

    // Registers lifecycle and processing loops
    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.enemy.EnemyPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessorService with dk.sdu.cbse.enemy.EnemyControlSystem;
}