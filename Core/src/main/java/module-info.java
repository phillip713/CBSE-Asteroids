module Core {
    requires Common;
    requires javafx.controls;
    requires javafx.graphics;

    // Allows JavaFX to instantiate your App class via reflection
    exports dk.sdu.cbse.core;

    // Declares that Core actively consumes these service providers
    uses dk.sdu.cbse.common.services.IGamePluginService;
    uses dk.sdu.cbse.common.services.IEntityProcessorService;
    uses dk.sdu.cbse.common.services.IPostEntityProcessorService;
}