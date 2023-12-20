package terrails.stattinkerer.fabric;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.quiltmc.loader.api.QuiltLoader;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.quilt.StatTinkerer;
import terrails.stattinkerer.quilt.mixin.interfaces.HealthManagerAccessor;

import java.util.Optional;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        StatTinkerer.FILE_CONFIG.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CStatTinkerer.MOD_ID + ".toml");
    }

    public static boolean inDevEnvironment() {
        return QuiltLoader.isDevelopmentEnvironment();
    }

    public static String getLoader() {
        return "quilt";
    }

    public static Optional<HealthManager> getHealthManager(ServerPlayer player) {
        if (player instanceof HealthManagerAccessor accessor) {
            return accessor.stattinkerer$getHealthManager();
        } else return Optional.empty();
    }

    public static void reviveInvalidateForgeCapability(Player player, boolean revive) {}

    public static ResourceLocation getItemRegistryName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
