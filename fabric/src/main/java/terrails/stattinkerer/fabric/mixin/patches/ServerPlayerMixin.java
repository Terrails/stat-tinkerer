package terrails.stattinkerer.fabric.mixin.patches;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.fabric.mixin.interfaces.HealthManagerAccessor;
import terrails.stattinkerer.feature.health.HealthHelper;
import terrails.stattinkerer.feature.health.HealthManagerImpl;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements HealthManagerAccessor {

    @Unique
    private final HealthManagerImpl stattinkerer$healthManager = new HealthManagerImpl();

    @Override
    public Optional<HealthManager> stattinkerer$getHealthManager() {
        return Optional.of(this.stattinkerer$healthManager);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "RETURN"))
    public void stattinkerer$addAdditionalSaveData(ValueOutput output, CallbackInfo cbi) {
        output.store(HealthHelper.TAG_GROUP, HealthManagerImpl.CODEC, this.stattinkerer$healthManager);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "RETURN"))
    public void stattinkerer$readAdditionalSaveData(ValueInput input, CallbackInfo cbi) {
        input.read(HealthHelper.TAG_GROUP, HealthManagerImpl.CODEC).ifPresent(this.stattinkerer$healthManager::copyFrom);
    }
}