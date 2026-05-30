module Core {
    requires Common;
    requires javafx.controls;
    requires spring.context;
    requires spring.web;

    exports dk.sdu.cbse.core to javafx.graphics;
    uses dk.sdu.cbse.common.services.IEntityProcessorService;
    uses dk.sdu.cbse.common.services.IGamePluginService;
    uses dk.sdu.cbse.common.services.IPostEntityProcessorService;

    opens dk.sdu.cbse.core to spring.core, spring.beans, spring.context;
}