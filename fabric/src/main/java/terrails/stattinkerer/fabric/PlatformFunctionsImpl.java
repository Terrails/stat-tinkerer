package terrails.stattinkerer.fabric;

import terrails.stattinkerer.PlatformFunctions;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.fabric.mixin.interfaces.HealthManagerAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class PlatformFunctionsImpl implements PlatformFunctions {

    @Override
    public String getLoader() {
        return "fabric";
    }

    @Override
    public Optional<HealthManager> getHealthManager(ServerPlayer player) {
        if (player instanceof HealthManagerAccessor accessor) {
            return accessor.stattinkerer$getHealthManager();
        } else return Optional.empty();
    }

    @Override
    public ResourceLocation getItemRegistryName(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
