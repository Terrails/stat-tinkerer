package terrails.stattinkerer.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import terrails.stattinkerer.PlatformFunctions;
import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

public class PlatformFunctionsImpl implements PlatformFunctions {

    @Override
    public String getLoader() {
        return "neoforge";
    }

    @Override
    public Optional<HealthManager> getHealthManager(ServerPlayer player) {
        return Optional.of(player.getData(RegistryHandler.HEALTH_DATA));
    }

    @Override
    public Identifier getItemRegistryName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
