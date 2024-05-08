package terrails.stattinkerer.neoforge;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLLoader;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        LOGGER.info("Saving changes to {} config file.", CStatTinkerer.MOD_ID + ".toml");
        StatTinkerer.CONFIG.save();
        LOGGER.debug("Saved changes to {} config file.", CStatTinkerer.MOD_ID + ".toml");
    }

    public static boolean inDevEnvironment() { return !FMLLoader.isProduction(); }

    public static String getLoader() {
        return "neoforge";
    }

    public static Optional<HealthManager> getHealthManager(ServerPlayer player) {
        return Optional.of(player.getData(STAttachments.HEALTH_DATA));
    }
}
