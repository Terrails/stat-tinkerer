package terrails.stattinkerer.forge.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.feature.health.HealthManagerImpl;

@Mod.EventBusSubscriber
public class HealthCapability {

    private static final ResourceLocation NAME = new ResourceLocation(CStatTinkerer.MOD_ID, "health");
    public static final Capability<HealthManager> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(NAME, new ICapabilitySerializable<CompoundTag>() {

                private final HealthManager instance = new HealthManagerImpl();

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
                    return HealthCapability.CAPABILITY.orEmpty(capability, LazyOptional.of(() -> this.instance));
                }

                @Override
                public CompoundTag serializeNBT() {
                    CompoundTag tag = new CompoundTag();
                    this.instance.serialize(tag);
                    return tag;
                }

                @Override
                public void deserializeNBT(CompoundTag tag) {
                    this.instance.deserialize(tag);
                }
            });
        }
    }
}
