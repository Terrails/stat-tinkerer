package terrails.stattinkerer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

public class LoaderExpectPlatform {

    /**
     * Applies changes to night-config's FileConfig
     * Technically not needed if autosave were to be enabled on both loaders
     */
    @ExpectPlatform
    public static void applyConfig() { throw new AssertionError(); }

    @ExpectPlatform
    public static boolean inDevEnvironment() { return false; }

    @ExpectPlatform
    public static String getLoader() { throw new AssertionError(); }

    @ExpectPlatform
    public static Optional<HealthManager> getHealthManager(ServerPlayer player) {
        throw new AssertionError();
    }

    // TODO: Remove as soon as forge implements a proper solution
    // Forge currently has an invalidated capability in Clone event. Gotta revive it, copy data over and then invalidate again.
    @ExpectPlatform
    public static void reviveInvalidateForgeCapability(Player player, boolean revive) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static ResourceLocation getItemRegistryName(Item item) {
        throw new AssertionError();
    }

}
