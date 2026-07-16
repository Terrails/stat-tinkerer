package terrails.stattinkerer.neoforge;

import net.neoforged.neoforge.common.ModConfigSpec;

import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.config.ConfigOption;

import java.util.List;
import java.util.stream.Collectors;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class ConfigHandler {

    @SuppressWarnings("unchecked")
    public static void setupOwnConfiguration(final ModConfigSpec.Builder builder) {
        for (var object : CStatTinkerer.CONFIGURATION_INSTANCES) {
            for (var field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.get(object) instanceof ConfigOption option) {
                        handleConfigOption(builder, option);
                    }
                } catch (Exception e) {
                    LOGGER.error("Could not process value for {} in {}", field.getName(), object.getClass().getName(), e);
                }
            }
        }
    }

    private static <T> void handleConfigOption(ModConfigSpec.Builder builder, ConfigOption<T> option) {
        String comment = option.getComment().isEmpty() ? "" : option.getComment() + "\n";
        if (option.getDefault() instanceof List<?> list) {
            if (!list.isEmpty()) {
                Object element = list.getFirst();

                String listStr;
                if (element instanceof Number) {
                    listStr = list.stream().map(Object::toString).collect(Collectors.joining(", "));
                } else {
                    listStr = list.stream()
                            .map(o -> '"' + o.toString() + '"')
                            .collect(Collectors.joining(", "));
                }

                comment += "Default: [ %s ]".formatted(listStr);
            }
        } else {
            comment += "Default: %s".formatted(option.getDefault().toString());
        }

        var value = builder.comment(comment).define(option.getPath(), option.getDefault(), option.getOptionValidator());
        option.initialize(value, value::set);
    }
}
