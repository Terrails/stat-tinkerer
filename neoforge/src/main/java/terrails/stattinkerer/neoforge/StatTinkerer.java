package terrails.stattinkerer.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.config.Configuration;
import terrails.stattinkerer.neoforge.config.NeoConfig;
import terrails.stattinkerer.neoforge.mobeffect.NoAppetiteMobEffect;

@Mod(CStatTinkerer.MOD_ID)
public class StatTinkerer {

    public static ModConfigSpec CONFIG;

    public StatTinkerer(final ModContainer container, final IEventBus bus) {
        CONFIG = new ModConfigSpec.Builder().configure(builder -> new NeoConfig(bus, builder, Configuration.EXPERIENCE, Configuration.HUNGER, Configuration.HEALTH)).getValue();
        container.registerConfig(ModConfig.Type.SERVER, CONFIG);

        DeferredRegister<MobEffect> mobEffects = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, CStatTinkerer.MOD_ID);
        STMobEffects.NO_APPETITE = mobEffects.register("no_appetite", NoAppetiteMobEffect::new);
        mobEffects.register(bus);

        STAttachments.DATA_ATTACHMENTS.register(bus);
        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.register(new EventHandler());
    }
}
