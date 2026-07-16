package terrails.stattinkerer.fabric;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.loader.api.FabricLoader;

import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.config.ConfigOption;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class ConfigHandler {

    private static final String FILE_NAME = CStatTinkerer.MOD_ID + ".toml";

    public static void setupConfig(List<Object> compatConfigSources) {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve(FILE_NAME);

        List<ConfigOption<?>> configOptions = collectConfigOptions(compatConfigSources);
        ConfigSpec spec = new ConfigSpec();
        configOptions.forEach(e -> spec.define(e.getPath(), e.getDefault(), e.getOptionValidator()));

        while (true) {
            LOGGER.debug("Initializing {} config file", FILE_NAME);
            CommentedFileConfig config = CommentedFileConfig.builder(configPath)
                    .sync()
                    .autoreload()
                    .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                    .writingMode(WritingMode.REPLACE)
                    .build();;

            try {
                LOGGER.info("Loading {} config file", FILE_NAME);
                config.load();

                correctConfig(config, spec);
                applyConfigOptions(config, spec, configOptions);

                // Leaving it open in order to be able to get updated values and save again
                config.save();
                LOGGER.info("Successfully loaded {} config file", FILE_NAME);
                break;
            } catch (ParsingException e) {
                LOGGER.error("Failed to load '{}' due to a parsing error.", FILE_NAME, e);
                config.close();

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
                String deformedFile = (CStatTinkerer.MOD_ID + "-" + LocalDateTime.now().format(dateFormatter) + ".toml");
                try {
                    Files.move(configPath, configDir.resolve(deformedFile));
                    LOGGER.error("Deformed config file renamed to '{}'", deformedFile);
                } catch (IOException ee) { // Results in an infinite loop, but considering that everything is broken without a config file, throw an exception and crash
                    LOGGER.error("Moving deformed config file failed...", ee);
                    throw new RuntimeException("Could not initialize '%s' config file.".formatted(FILE_NAME));
                }
            }
        }
    }

    private static void correctConfig(CommentedFileConfig config, ConfigSpec spec) {
        // Running correct twice fixes a NightConfig quirk where empty categories are created incorrectly.
        while (!spec.isCorrect(config)) {
            int correction = spec.correct(config, (action, path, incorrectValue, correctedValue) -> {
                String pathString = String.join(".", path);
                switch (action) {
                    case ADD -> LOGGER.info("Missing entry {} = {} added to {}", pathString, correctedValue, FILE_NAME);
                    case REMOVE -> LOGGER.info("Invalid entry {} removed from {}", pathString, FILE_NAME);
                    case REPLACE -> LOGGER.info("Invalid entry {}: value {} replaced by {} in {}", pathString, incorrectValue, correctedValue, FILE_NAME);
                }
            });
            LOGGER.info("{} correction(s) applied to {} config file", correction, FILE_NAME);
            config.save();
        }
    }

    private static void applyConfigOptions(CommentedFileConfig config, ConfigSpec spec, List<ConfigOption<?>> configOptions) {
        for (var option : configOptions) {
            // Double check that ConfigOption path is in ConfigSpec
            if (spec.isDefined(option.getPath())) {
                config.setComment(option.getPath(), buildConfigOptionComment(option));
                option.initialize(() -> config.get(option.getPath()), val -> config.set(option.getPath(), val));
            }
        }
    }

    private static List<ConfigOption<?>> collectConfigOptions(List<Object> compatConfigSources) {
        final List<Object> configSources = new ArrayList<>(List.of(CStatTinkerer.CONFIGURATION_INSTANCES));
        configSources.addAll(compatConfigSources);

        final List<ConfigOption<?>> configOptions = new ArrayList<>();
        for (var object : configSources) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    if (field.getType().isAssignableFrom(ConfigOption.class)) {
                        field.setAccessible(true);
                        configOptions.add((ConfigOption<?>) field.get(object));
                    }
                } catch (InaccessibleObjectException | SecurityException | IllegalAccessException e) {
                    LOGGER.error("Could not process spec for {} in {}", field.getName(), object.getClass().getName(), e);
                }
            }
        }

        return configOptions;
    }

    private static String buildConfigOptionComment(ConfigOption<?> option) {
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

        return comment;
    }
}
