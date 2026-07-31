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
        var noAppetite = entity.getActiveEffectsMap().get(STMobEffects.NO_APPETITE);
        if (original.call(level, stack, entity)) {
            if (noAppetite != null) {
                // re-add effect for easier compat with anything else that modifies this behavior.
                // Better than recreating the whole clear effect function that skips just NO_APPETITE
                entity.addEffect(noAppetite);
            }
            return true;
        }
        return false;
    }
}
