package terrails.stattinkerer.forge;

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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.config.ConfigOption;
import terrails.stattinkerer.forge.feature.TANFeature;
import terrails.stattinkerer.forge.mobeffect.NoAppetiteMobEffect;

import java.lang.reflect.Field;
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
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MOB_EFFECTS.register(bus);
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        STMobEffects.NO_APPETITE = NO_APPETITE.get();
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(HealthManager.class);
    }

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        for (Object object : CStatTinkerer.CONFIGURATION_INSTANCES) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption option) {

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

                        ForgeConfigSpec.ConfigValue<Object> value = builder.comment(comment).define(option.getPath(), option.getDefault(), option.getOptionValidator());
                        option.initialize(value::get, value::set);
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process value for {} in {}", field.getName(), object.getClass().getName(), e);
                }
            }
        }

        if (ModList.get().isLoaded("toughasnails")) {
            TANFeature.initialize(builder);
        }

        CONFIG_SPEC = builder.build();
    }
}
