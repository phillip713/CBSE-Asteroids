package dk.sdu.cbse.common.services;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public enum ServiceLocator {
    INSTANCE;

    private ModuleLayer pluginLayer;

    ServiceLocator() {
        try {
            Path pluginsDir = Paths.get("plugins");

            if (!java.nio.file.Files.exists(pluginsDir)) {
                java.nio.file.Files.createDirectories(pluginsDir);
            }

            ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);

            List<String> pluginModuleNames = pluginsFinder.findAll().stream()
                    .map(mref -> mref.descriptor().name())
                    .collect(Collectors.toList());

            Configuration pluginsConfiguration = ModuleLayer.boot().configuration().resolve(
                    pluginsFinder,
                    ModuleFinder.of(),
                    pluginModuleNames
            );

            this.pluginLayer = ModuleLayer.boot().defineModulesWithOneLoader(
                    pluginsConfiguration,
                    ClassLoader.getSystemClassLoader()
            );

            System.out.println("ServiceLocator successfully spun up custom JPMS Layer with: " + pluginModuleNames);
        } catch (Exception e) {
            System.err.println("Failed to scan plugins directory. Falling back to default Boot Layer.");
            e.printStackTrace();
            this.pluginLayer = ModuleLayer.boot();
        }
    }

    /**
     * Generic lookup tool that searches both your custom plugin layer AND
     * the system boot layer automatically.
     */
    public <T> List<T> getServices(Class<T> serviceContract) {
        List<T> services = new ArrayList<>();

        // Force the ServiceLoader to look inside our isolated tracking layer
        ServiceLoader<T> loader = ServiceLoader.load(pluginLayer, serviceContract);
        for (T service : loader) {
            services.add(service);
        }

        return services;
    }
}