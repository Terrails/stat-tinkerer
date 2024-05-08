package terrails.stattinkerer.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.fabric.mixin.interfaces.HealthManagerAccessor;

import java.util.Optional;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        StatTinkerer.CONFIG.save();
    }

    public static boolean inDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static String getLoader() {
        return "fabric";
    }

    public static Optional<HealthManager> getHealthManager(ServerPlayer player) {
        if (player instanceof HealthManagerAccessor accessor) {
            return accessor.stattinkerer$getHealthManager();
        } else return Optional.empty();
    }
}
