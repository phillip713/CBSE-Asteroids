module Bullet {
    requires Common;

    provides dk.sdu.cbse.common.services.IEntityProcessorService with dk.sdu.cbse.bullet.BulletControlSystem;
    provides dk.sdu.cbse.common.services.BulletSPI with dk.sdu.cbse.bullet.BulletPlugin;
    provides dk.sdu.cbse.common.services.IGamePluginService with dk.sdu.cbse.bullet.BulletPlugin; // 💡 Added
}