package terrails.stattinkerer.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.feature.health.HealthHelper;
import terrails.stattinkerer.feature.health.HealthManagerImpl;
import terrails.stattinkerer.mobeffect.NoAppetiteMobEffect;

import java.util.function.Supplier;

public class RegistryHandler {

    private static final DeferredRegister<MobEffect> MOB_EFFECTS;
    private static final DeferredRegister<AttachmentType<?>> DATA_ATTACHMENTS;

    public static final Supplier<AttachmentType<HealthManagerImpl>> HEALTH_DATA;

    static {
        MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CStatTinkerer.MOD_ID);
        STMobEffects.NO_APPETITE = MOB_EFFECTS.register("no_appetite", NoAppetiteMobEffect::new);

        DATA_ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, CStatTinkerer.MOD_ID);
        HEALTH_DATA = DATA_ATTACHMENTS.register(
                "health",
                () -> AttachmentType.builder(HealthManagerImpl::new)
                        .serialize(HealthManagerImpl.CODEC.fieldOf(HealthHelper.TAG_GROUP))
                        .copyOnDeath()
                        .build()
        );
    }

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
        DATA_ATTACHMENTS.register(bus);
    }
}
