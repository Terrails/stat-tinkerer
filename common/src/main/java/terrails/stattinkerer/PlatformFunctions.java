package terrails.stattinkerer;

import terrails.stattinkerer.api.health.HealthManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.Optional;

public interface PlatformFunctions {

    String getLoader();

    Optional<HealthManager> getHealthManager(ServerPlayer player);

    ResourceLocation getItemRegistryName(Item item);
}
