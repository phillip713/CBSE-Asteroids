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
    INSTANCE; // Thread-safe singleton instance

    private ModuleLayer pluginLayer;

    // The constructor runs automatically exactly once when the enum is first accessed
    ServiceLocator() {
        try {
            // 1. Point to your physical plugins directory in the root folder
            Path pluginsDir = Paths.get("plugins");

            // Fallback safety: Create the folder if it doesn't exist yet
            if (!java.nio.file.Files.exists(pluginsDir)) {
                java.nio.file.Files.createDirectories(pluginsDir);
            }

            // 2. Scan the physical folder for any plugin JAR files
            ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);

            // 3. Extract the module names from the detected JARs
            List<String> pluginModuleNames = pluginsFinder.findAll().stream()
                    .map(mref -> mref.descriptor().name())
                    .collect(Collectors.toList());

            // 4. Resolve dependencies against the default system Boot Layer
            Configuration pluginsConfiguration = ModuleLayer.boot().configuration().resolve(
                    pluginsFinder,
                    ModuleFinder.of(),
                    pluginModuleNames
            );

            // 5. Build the custom layer with a dedicated ClassLoader infrastructure
            // This isolated ClassLoader boundary is what stops the Split Package crash!
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