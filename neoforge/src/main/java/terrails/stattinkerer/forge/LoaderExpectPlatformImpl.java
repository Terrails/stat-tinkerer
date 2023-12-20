package terrails.stattinkerer.forge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.fml.loading.FMLLoader;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        StatTinkerer.CONFIG_SPEC.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CStatTinkerer.MOD_ID + ".toml");
    }

    public static boolean inDevEnvironment() { return !FMLLoader.isProduction(); }

    public static String getLoader() {
        return "neoforge";
    }

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
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
