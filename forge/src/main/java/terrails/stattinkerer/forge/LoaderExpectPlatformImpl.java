package terrails.stattinkerer.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.forge.capability.HealthCapability;

import java.util.Optional;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        StatTinkerer.CONFIG_SPEC.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CStatTinkerer.MOD_ID + ".toml");
    }

    public static boolean inDevEnvironment() { return !FMLLoader.isProduction(); }

    public static Optional<HealthManager> getHealthManager(ServerPlayer player) {
        return player.getCapability(HealthCapability.CAPABILITY).resolve();
    }

    public static void reviveInvalidateForgeCapability(Player player, boolean revive) {
        if (revive) {
            player.reviveCaps();
        } else {
            player.invalidateCaps();
        }
    }

    public static ResourceLocation getItemRegistryName(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }
}
