package dk.sdu.cbse.core;

import dk.sdu.cbse.common.services.IEntityProcessorService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Configuration
public class ModuleConfig {

    @Bean
    public Game game() {
        return new Game(gamePluginServices(), entityProcessingServiceList(), postEntityProcessingServices());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(); // 💡 Standard HTTP Client
    }

    @Bean
    public List<IEntityProcessorService> entityProcessingServiceList() {
        return ServiceLoader.load(IEntityProcessorService.class).stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    @Bean
    public List<IGamePluginService> gamePluginServices() {
        return ServiceLoader.load(IGamePluginService.class).stream().map(ServiceLoader.Provider::get).collect(Collectors.toList());
    }

    @Bean
    public List<IPostEntityProcessorService> postEntityProcessingServices() {
        List<IPostEntityProcessorService> list = ServiceLoader.load(IPostEntityProcessorService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());

        // 💡 Seamless Hybrid Hook: Combine SPI services with our Spring Network client!
        list.add(scoreSyncPostProcessor());
        return list;
    }

    @Bean
    public IPostEntityProcessorService scoreSyncPostProcessor() {
        return new ScoreSyncPostProcessor(restTemplate());
    }
}