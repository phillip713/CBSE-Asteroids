module Collision {
    requires Common;

    provides dk.sdu.cbse.common.services.IPostEntityProcessorService with dk.sdu.cbse.collision.CollisionDetector;
}