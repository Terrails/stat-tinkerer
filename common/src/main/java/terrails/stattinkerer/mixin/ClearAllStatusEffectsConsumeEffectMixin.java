package terrails.stattinkerer.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import terrails.stattinkerer.api.STMobEffects;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public class ClearAllStatusEffectsConsumeEffectMixin {

    @WrapMethod(method = "apply")
    private boolean apply$stattinkerer(Level level, ItemStack stack, LivingEntity entity, Operation<Boolean> original) {
        if (entity.hasEffect(STMobEffects.NO_APPETITE)) {
            if (level.isClientSide()) {
                return false;
            }

            var activeEffects = entity.getActiveEffectsMap().keySet();
            if (activeEffects.isEmpty()) {
                return false;
            }

            for (var effect : activeEffects) {
                if (effect == STMobEffects.NO_APPETITE) {
                    continue;
                }

                entity.removeEffect(effect);
            }
            return true;
        } else return original.call(level, stack, entity);
    }
}
