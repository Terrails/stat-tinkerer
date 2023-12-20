package terrails.stattinkerer.forge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.config.ConfigOption;
import terrails.stattinkerer.feature.health.HealthManagerImpl;
import terrails.stattinkerer.forge.mobeffect.NoAppetiteMobEffect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

@Mod(CStatTinkerer.MOD_ID)
public class StatTinkerer {

    public static final ModConfigSpec CONFIG_SPEC;

    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CStatTinkerer.MOD_ID);
    public static final Supplier<NoAppetiteMobEffect> NO_APPETITE = MOB_EFFECTS.register("no_appetite", NoAppetiteMobEffect::new);

    private static final DeferredRegister<AttachmentType<?>> DATA_ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CStatTinkerer.MOD_ID);
    public static final Supplier<AttachmentType<HealthManager>> HEALTH_DATA = DATA_ATTACHMENTS.register(
            "health", () -> AttachmentType
                    .builder(() -> (HealthManager) (new HealthManagerImpl()))
                    .serialize(new IAttachmentSerializer<CompoundTag, HealthManager>() {
                        @Override
                        public HealthManager read(CompoundTag tag) {
                            HealthManager manager = new HealthManagerImpl();
                            manager.deserialize(tag);
                            return manager;
                        }

                        @Override
                        public CompoundTag write(HealthManager data) {
                            CompoundTag tag = new CompoundTag();
                            data.serialize(tag);
                            return tag;
                        }
                    }).copyOnDeath().build()
    );

    public StatTinkerer() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MOB_EFFECTS.register(bus);
        DATA_ATTACHMENTS.register(bus);
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        STMobEffects.NO_APPETITE = NO_APPETITE.get();
    }

    static {
        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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

                        ModConfigSpec.ConfigValue<Object> value = builder.comment(comment).define(option.getPath(), option.getDefault(), option.getOptionValidator());
                        option.initialize(value::get, value::set);
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process value for {} in {}", field.getName(), object.getClass().getName(), e);
                }
            }
        }

        CONFIG_SPEC = builder.build();
    }
}
