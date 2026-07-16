package terrails.stattinkerer.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class CompatibilityHandler {

    private static final Map<String, String> COMPAT = Map.of(
            "toughasnails", "ToughAsNailsFeature"
    );

    public static List<Object> setupCompat() {
        List<Object> compatInstances = new ArrayList<>();
        String basePackage = "terrails.stattinkerer.fabric.compat";

        for (var entry : COMPAT.entrySet()) {
            String id = entry.getKey();
            if (FabricLoader.getInstance().isModLoaded(id)) {
                String className = basePackage + "." + entry.getValue();
                LOGGER.info("Loading compat for mod {}", id);
                try {
                    Class<?> compatClass = Class.forName(className);
                    Constructor<?> constructor = compatClass.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    compatInstances.add(constructor.newInstance());
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Failed to load compat as {} does not exist", className, e);
                } catch (ReflectiveOperationException e) {
                    LOGGER.error("Failed to load compat {}", className, e);
                }
            } else {
                LOGGER.debug("Skipped loading compat for missing mod {}", id);
            }
        }

        return compatInstances;
    }
}
