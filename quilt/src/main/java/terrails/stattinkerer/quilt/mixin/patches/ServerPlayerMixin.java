package terrails.stattinkerer.quilt.mixin.patches;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.feature.health.HealthHelper;
import terrails.stattinkerer.quilt.mixin.interfaces.HealthManagerAccessor;
import terrails.stattinkerer.feature.health.HealthManagerImpl;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements HealthManagerAccessor {

    private final HealthManager healthManager = new HealthManagerImpl();

    @Override
    public Optional<HealthManager> getHealthManager() {
        return Optional.of(this.healthManager);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo cbi) {
        CompoundTag tag = new CompoundTag();
        this.healthManager.serialize(tag);
        compound.put(HealthHelper.TAG_GROUP, tag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo cbi) {
        if (compound.contains(HealthHelper.TAG_GROUP, Tag.TAG_COMPOUND)) {
            CompoundTag tag = compound.getCompound(HealthHelper.TAG_GROUP);
            this.healthManager.deserialize(tag);
        }
    }
}