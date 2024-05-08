package terrails.stattinkerer.neoforge.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import terrails.stattinkerer.config.ConfigOption;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class NeoConfig {

    private static final List<ConfigOption<?, ?>> CONFIG_OPTIONS = new ArrayList<>();

    public NeoConfig(IEventBus bus, ModConfigSpec.Builder builder, Object... instances) {
        for (Object instance : instances) {
            for (Field field : instance.getClass().getDeclaredFields()) {
                try {
                    if (field.get(instance) instanceof ConfigOption<?,?> option) {
                        CONFIG_OPTIONS.add(option);

                        if (option.getRawDefault() instanceof List<?> list) {
                            builder.comment(option.getComment()).defineList(option.getPath(), list, option.getOptionValidator());
                        } else {
                            builder.comment(option.getComment()).define(option.getPath(), option.getRawDefault(), option.getOptionValidator());
                        }
                    } else {
                        LOGGER.debug("Skipping {} field in {} as it is not a ConfigOption", field.getName(), instance.getClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not process {} field in {}", field.getName(), instance.getClass().getName(), e);
                }
            }
        }
        builder.build();
        bus.addListener(this::loadConfig);
        bus.addListener(this::reloadConfig);
    }

    private void loadConfig(final ModConfigEvent.Loading event) {
        LOGGER.info("Loading {} config file", event.getConfig().getFileName());
        final CommentedConfig config = event.getConfig().getConfigData();
        for (ConfigOption<?, ?> option : CONFIG_OPTIONS) {
            option.initialize(() -> config.get(option.getPath()), v -> config.set(option.getPath(), v));
            option.reload();
        }
        LOGGER.debug("Loaded {} config file", event.getConfig().getFileName());
    }

    private void reloadConfig(final ModConfigEvent.Reloading event) {
        LOGGER.info("Reloading {} config file", event.getConfig().getFileName());
        CONFIG_OPTIONS.forEach(ConfigOption::reload);
        LOGGER.debug("Reloaded {} config file", event.getConfig().getFileName());
    }
}
