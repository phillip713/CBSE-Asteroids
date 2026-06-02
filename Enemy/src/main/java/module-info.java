import dk.sdu.cbse.common.services.IBulletSPI;

module Enemy {
    requires Common;

    uses IBulletSPI;

    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.enemy.EnemyPlugin;
    provides dk.sdu.cbse.common.services.IEntityProcessorService with dk.sdu.cbse.enemy.EnemyControlSystem;
}