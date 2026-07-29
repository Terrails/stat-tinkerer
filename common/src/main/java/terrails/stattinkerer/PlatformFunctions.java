package terrails.stattinkerer;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import terrails.stattinkerer.api.health.HealthManager;

import java.util.Optional;

public interface PlatformFunctions {

    String getLoader();

    Optional<HealthManager> getHealthManager(ServerPlayer player);

    Identifier getItemRegistryName(Item item);
}
