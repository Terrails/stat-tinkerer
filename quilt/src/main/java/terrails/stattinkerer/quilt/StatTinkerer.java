package terrails.stattinkerer.quilt;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.base.api.util.TriState;
import org.quiltmc.qsl.entity.effect.api.StatusEffectEvents;
import org.quiltmc.qsl.entity.effect.api.StatusEffectRemovalReason;
import org.quiltmc.qsl.entity.event.api.ServerPlayerEntityCopyCallback;
import org.quiltmc.qsl.networking.api.ServerPlayConnectionEvents;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.config.ConfigOption;
import terrails.stattinkerer.feature.ExperienceFeature;
import terrails.stattinkerer.feature.HungerFeature;
import terrails.stattinkerer.feature.health.HealthFeature;
import terrails.stattinkerer.quilt.mobeffect.NoAppetiteMobEffect;

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
    public void onInitialize(ModContainer mod) {
        STMobEffects.NO_APPETITE = NoAppetiteMobEffect.registerEffect();
        setupConfig();
        registerEvents();
    }

    private static void setupConfig() {
        final String fileName = CStatTinkerer.MOD_ID + ".toml";

        final Path configDir = QuiltLoader.getConfigDir();
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
        ServerPlayerEntityCopyCallback.EVENT.register((newPlayer, oldPlayer, wasDeath) -> {
            ExperienceFeature.INSTANCE.onPlayerClone(wasDeath, newPlayer, oldPlayer);
            HungerFeature.INSTANCE.onPlayerClone(wasDeath, newPlayer, oldPlayer);
            HealthFeature.INSTANCE.onPlayerClone(wasDeath, newPlayer, oldPlayer);
        });
        // Deprecated but has to be used since Quilt has no alternative
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
        StatusEffectEvents.SHOULD_REMOVE.register((entity, effectInstance, reason) -> {
            if (reason == StatusEffectRemovalReason.DRANK_MILK && effectInstance.getEffect() == STMobEffects.NO_APPETITE) {
                return TriState.FALSE;
            }
            return TriState.DEFAULT;
        });
    }
}
