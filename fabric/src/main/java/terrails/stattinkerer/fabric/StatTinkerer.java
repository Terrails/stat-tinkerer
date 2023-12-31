package terrails.stattinkerer.fabric;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.config.ConfigOption;
import terrails.stattinkerer.fabric.mobeffect.NoAppetiteMobEffect;
import terrails.stattinkerer.feature.ExperienceFeature;
import terrails.stattinkerer.feature.HungerFeature;
import terrails.stattinkerer.feature.health.HealthFeature;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class StatTinkerer implements ModInitializer {

    public static CommentedFileConfig FILE_CONFIG;

    @Override
    public void onInitialize() {
        STMobEffects.NO_APPETITE = NoAppetiteMobEffect.registerEffect();
        setupConfig();
        registerEvents();
    }

    private static void setupConfig() {
        final String fileName = CStatTinkerer.MOD_ID + ".toml";

        final Path configDir = FabricLoader.getInstance().getConfigDir();
        final Path configPath = configDir.resolve(fileName);

        final ConfigSpec spec = new ConfigSpec();
        for (Object object : CStatTinkerer.CONFIGURATION_INSTANCES) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.get(object) instanceof ConfigOption<?> option) {
                        spec.define(option.getPath(), option.getDefault(), option.getOptionValidator());
                    }
                } catch (InaccessibleObjectException | SecurityException | IllegalAccessException e) {
                    LOGGER.error("Could not process spec for {} in {}", field.getName(), object.getClass().getName(), e);
                }
            }
        }

        while (true) {
            LOGGER.debug("Initializing {} config file", fileName);
            final CommentedFileConfig config = CommentedFileConfig.builder(configPath)
                    .sync()
                    .autoreload()
                    .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                    .writingMode(WritingMode.REPLACE)
                    .build();

            try {
                LOGGER.info("Loading {} config file", fileName);
                config.load();

                // correct multiple times as there is some odd issue where categories are created empty
                // running correct once again on the empty categories fixed the file.
                while (!spec.isCorrect(config)) {
                    int correction = spec.correct(config, (action, path, incorrectValue, correctedValue) -> {
                        String pathString = String.join(".", path);
                        switch (action) {
                            case ADD -> LOGGER.info("Missing entry {} = {} added to {}", pathString, correctedValue, fileName);
                            case REMOVE -> LOGGER.info("Invalid entry {} removed from {}", pathString, fileName);
                            case REPLACE -> LOGGER.info("Invalid entry {}: value {} replaced by {} in {}", pathString, incorrectValue, correctedValue, fileName);
                        }
                    });
                    LOGGER.info("{} correction(s) applied to {} config file", correction, fileName);
                    config.save();
                }

                // Apply comments from ConfigOption objects, also adds a new line with default values
                for (Object object : CStatTinkerer.CONFIGURATION_INSTANCES) {
                    for (Field field : object.getClass().getDeclaredFields()) {
                        try {
                            field.setAccessible(true);
                            // Double check that ConfigOption path is in ConfigSpec
                            if (field.get(object) instanceof ConfigOption<?> option && spec.isDefined(option.getPath())) {

                                String comment = option.getComment().isEmpty() ? "" : option.getComment() + "\n";
                                if (option.getDefault() instanceof List<?> list) {
                                    if (!list.isEmpty()) {
                                        Object element = list.get(0);

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

                                config.setComment(option.getPath(), comment);
                                option.initialize(() -> config.get(option.getPath()), (val) -> config.set(option.getPath(), val));
                            }
                        } catch (InaccessibleObjectException | SecurityException | IllegalAccessException e) {
                            LOGGER.error("Could not process value for {} in {}", field.getName(), object.getClass().getName(), e);
                        }
                    }
                }

                // Leaving it open in order to be able to get updated values and save again
                config.save();
                FILE_CONFIG = config;

                LOGGER.info("Successfully loaded {} config file", fileName);
                break;
            } catch (ParsingException e) {
                config.close();

                LOGGER.error("Failed to load '{}' due to a parsing error.", fileName, e);

                String deformedFile = (CStatTinkerer.MOD_ID + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".toml");
                try {
                    Files.move(configPath, configDir.resolve(deformedFile));
                    LOGGER.error("Deformed config file renamed to '{}'", deformedFile);
                } catch (IOException ee) { // Results in an infinite loop, but considering that everything is broken without a config file, throw an exception and crash
                    LOGGER.error("Moving deformed config file failed...", ee);
                    throw new RuntimeException("Could not initialize '%s' config file.".formatted(fileName));
                }
            }
        }
    }

    private static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> HealthFeature.INSTANCE.onPlayerJoinServer(handler.player));
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            ExperienceFeature.INSTANCE.onPlayerClone(!alive, newPlayer, oldPlayer);
            HungerFeature.INSTANCE.onPlayerClone(!alive, newPlayer, oldPlayer);
            HealthFeature.INSTANCE.onPlayerClone(!alive, newPlayer, oldPlayer);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                HungerFeature.INSTANCE.onPlayerRespawn(newPlayer);
            }
        });
        EventHandler.ITEM_INTERACTION_USE.register(HungerFeature.INSTANCE);
        EventHandler.ITEM_INTERACTION_USE.register(HealthFeature.INSTANCE);
        EventHandler.ITEM_INTERACTION_COMPLETED.register(HealthFeature.INSTANCE);
        EventHandler.BLOCK_INTERACTION.register(HungerFeature.INSTANCE);
        EventHandler.EXPERIENCE_DROP.register(ExperienceFeature.INSTANCE);
    }
}
