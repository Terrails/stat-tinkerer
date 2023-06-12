package terrails.stattinkerer.fabric.mixin.patches;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import terrails.stattinkerer.fabric.mixin.interfaces.NoAppetiteEffectRemoval;
import terrails.stattinkerer.api.STMobEffects;
@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {
    @Redirect(method = "finishUsingItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z"))
    private boolean removeEffects(LivingEntity entity, ItemStack stack, Level level, LivingEntity _entity) {
        if (!level.isClientSide()) {
            if (entity.hasEffect(STMobEffects.NO_APPETITE)) {
                return ((NoAppetiteEffectRemoval) entity).removeAllExceptNoAppetite();
            } else return entity.removeAllEffects();
        }
        return false;
    }
}
