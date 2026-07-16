package terrails.stattinkerer.neoforge;

import com.google.common.collect.Lists;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class CompatibilityHandler {

    private static final Map<String, String> COMPAT = Map.of(
            "toughasnails", "ToughAsNailsFeature"
    );

    public static void setupCompat(Builder builder, IEventBus bus) {
        final String basePackage = "terrails.stattinkerer.neoforge.compat";

        for (var entry : COMPAT.entrySet()) {
            var id = entry.getKey();
            if (ModList.get().isLoaded(id)) {
                var className = basePackage + "." + entry.getValue();
                LOGGER.info("Loading compat for mod {}", id);
                try {
                    var compatClass = Class.forName(className);
                    instantiateCompat(bus, builder, compatClass);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Failed to load compat as {} does not exist", className, e);
                } catch (ReflectiveOperationException e) {
                    LOGGER.error("Failed to load compat {}", className, e);
                }
            } else {
                LOGGER.debug("Skipped loading compat for missing mod {}", id);
            }
        }
    }

    private static void instantiateCompat(IEventBus bus, Builder builder, Class<?> compatClass)
            throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        var constructors = Lists.newArrayList(List.of(compatClass.getDeclaredConstructors()));
        constructors.sort(Comparator.comparingInt((Constructor<?> constructor) -> constructor.getParameterCount()).reversed());

        for (var constructor : constructors) {
            var args = resolveArguments(constructor, bus, builder);
            if (args == null) {
                continue;
            }

            constructor.setAccessible(true);
            constructor.newInstance(args);
            return;
        }

        throw new NoSuchMethodException("No supported constructor found for " + compatClass.getName());
    }

    private static Object[] resolveArguments(Constructor<?> constructor, IEventBus bus, Builder builder) {
        var parameterTypes = constructor.getParameterTypes();
        var args = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            var parameterType = parameterTypes[i];
            var matches = new ArrayList<>(2);

            if (parameterType.isInstance(bus)) {
                matches.add(bus);
            }
            if (parameterType.isInstance(builder)) {
                matches.add(builder);
            }

            if (matches.size() != 1) {
                return null;
            }

            args[i] = matches.getFirst();
        }

        return args;
    }
}
