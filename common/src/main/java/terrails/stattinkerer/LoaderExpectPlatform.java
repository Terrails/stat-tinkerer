package terrails.stattinkerer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Contract;
import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

public class LoaderExpectPlatform {

    /**
     * Applies changes to night-config's FileConfig
     * Technically not needed if autosave were to be enabled on both loaders
     */
    @Contract
    @ExpectPlatform
    public static void applyConfig() { throw new AssertionError(); }

    @Contract
    @ExpectPlatform
    public static boolean inDevEnvironment() { return false; }

    @Contract
    @ExpectPlatform
    public static String getLoader() { throw new AssertionError(); }

    @Contract
    @ExpectPlatform
    public static Optional<HealthManager> getHealthManager(ServerPlayer player) {
        throw new AssertionError();
    }
}
