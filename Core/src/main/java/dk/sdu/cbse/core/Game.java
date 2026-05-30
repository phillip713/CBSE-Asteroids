package dk.sdu.cbse.core;

import dk.sdu.cbse.common.services.IEntityProcessorService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;
import java.util.List;

public class Game {
    private final List<IGamePluginService> pluginServices;
    private final List<IEntityProcessorService> entityProcessors;
    private final List<IPostEntityProcessorService> postEntityProcessors;

    public Game(List<IGamePluginService> pluginServices,
                List<IEntityProcessorService> entityProcessors,
                List<IPostEntityProcessorService> postEntityProcessors) {
        this.pluginServices = pluginServices;
        this.entityProcessors = entityProcessors;
        this.postEntityProcessors = postEntityProcessors;
    }

    public List<IGamePluginService> getPluginServices() { return pluginServices; }
    public List<IEntityProcessorService> getEntityProcessors() { return entityProcessors; }
    public List<IPostEntityProcessorService> getPostEntityProcessors() { return postEntityProcessors; }
}