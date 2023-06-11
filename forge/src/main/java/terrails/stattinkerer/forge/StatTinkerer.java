package terrails.stattinkerer.forge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.config.ConfigOption;
import terrails.stattinkerer.forge.feature.TANFeature;
import terrails.stattinkerer.forge.mobeffect.NoAppetiteMobEffect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

@Mod(CStatTinkerer.MOD_ID)
@EventBusSubscriber(bus = Bus.MOD)
public class StatTinkerer {

    public static final ForgeConfigSpec CONFIG_SPEC;

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CStatTinkerer.MOD_ID);
    private static final RegistryObject<MobEffect> NO_APPETITE = MOB_EFFECTS.register("no_appetite", NoAppetiteMobEffect::new);

    public StatTinkerer() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC, CStatTinkerer.MOD_ID + ".toml");
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MOB_EFFECTS.register(bus);
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        StatTinkerer.loadConfig();
        STMobEffects.NO_APPETITE = NO_APPETITE.get();
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(HealthManager.class);
    }

    private static void loadConfig() {
        final Path path = FMLPaths.CONFIGDIR.get().resolve(CStatTinkerer.MOD_ID + ".toml");

        LOGGER.debug("Loading config file {}", path);

        while (true) {
            final CommentedFileConfig config = CommentedFileConfig.builder(path)
                    .sync()
                    .autoreload()
                    .writingMode(WritingMode.REPLACE)
                    .build();

            try {
                config.load();
                LOGGER.debug("Loaded TOML config file {}", path.toString());
                CONFIG_SPEC.setConfig(config);

                // Initialize values from ConfigOption objects
                for (Object object : CStatTinkerer.CONFIGURATION_INSTANCES) {
                    for (Field field : object.getClass().getDeclaredFields()) {
                        try {
                            field.setAccessible(true);

                            if (field.get(object) instanceof ConfigOption<?> option) {
                                option.initialize(() -> config.get(option.getPath()), (val) -> config.set(option.getPath(), val));
                            }

                        } catch (Exception e) {
                            LOGGER.error("Could not process spec for {} in {}", field.getName(), object.getClass().getName());
                            e.printStackTrace();
                        }
                    }
                }
                break;
            } catch (ParsingException e) {
                config.close();

                LOGGER.error("Failed to load '{}' due to a parsing error.", path.toString());
                e.printStackTrace();

                String deformedFile = (CStatTinkerer.MOD_ID + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".toml");
                try {
                    Files.move(path, FMLPaths.CONFIGDIR.get().resolve(deformedFile));
                    LOGGER.error("Deformed config file renamed to '{}'", deformedFile);
                } catch (IOException ee) { // Results in an infinite loop, but considering that everything is broken without a config file, throw an exception and crash
                    LOGGER.error("Moving deformed config file failed...");
                    ee.printStackTrace();
                    throw new RuntimeException("Could not initialize '%s' config file.".formatted(path.toString()));
                }
            }
        }
    }

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        for (Object object : CStatTinkerer.CONFIGURATION_INSTANCES) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption<?> option) {

                        String comment = option.getComment().isEmpty() ? "" : option.getComment() + "\n";
                        if (option.getDefault() instanceof List<?> list) {
                            if (!list.isEmpty()) {
                                Object element = list.get(0);

                                String listStr;
                                if (element instanceof Number) {
                                    listStr = list.stream().map(Object::toString).collect(Collectors.joining(", "));
                                    comment += "Default: [ %s ]".formatted(listStr);
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

                        builder.comment(comment).define(option.getPath(), option.getDefault(), option.getOptionValidator());
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process value for {} in {}", field.getName(), object.getClass().getName());
                    e.printStackTrace();
                }
            }
        }

//        if (ModList.get().isLoaded("toughasnails")) {
//            TANFeature.initialize(builder);
//        }

        CONFIG_SPEC = builder.build();
    }
}
