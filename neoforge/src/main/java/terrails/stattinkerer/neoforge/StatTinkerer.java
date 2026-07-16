package terrails.stattinkerer.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

import terrails.stattinkerer.CStatTinkerer;

@Mod(CStatTinkerer.MOD_ID)
public class StatTinkerer {

    public StatTinkerer(final IEventBus bus, final ModContainer container) {
        CStatTinkerer.setup(new PlatformFunctionsImpl());
        RegistryHandler.register(bus);

        final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ConfigHandler.setupOwnConfiguration(builder);
        CompatibilityHandler.setupCompat(builder, bus);
        container.registerConfig(ModConfig.Type.SERVER, builder.build());

        NeoForge.EVENT_BUS.register(EventHandler.class);
    }
}
