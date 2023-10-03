package terrails.stattinkerer.fabric.mixin.patches;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import terrails.stattinkerer.fabric.EventHandler;
import terrails.stattinkerer.fabric.mixin.interfaces.NoAppetiteEffectRemoval;
import terrails.stattinkerer.api.STMobEffects;

import java.util.Iterator;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements NoAppetiteEffectRemoval {

    @Shadow @Final private Map<MobEffect, MobEffectInstance> activeEffects;
    @Shadow protected abstract void onEffectRemoved(MobEffectInstance effect);

    public LivingEntityMixin(EntityType<?> entityType, Level level) { super(entityType, level); }

    @Redirect(method = "completeUsingItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack stattinkerer$completeUsingItem(ItemStack startStack, Level level, LivingEntity livingEntity) {
        ItemStack completedStack = startStack.copy().finishUsingItem(level, livingEntity);
        if (livingEntity instanceof Player player) {
            InteractionResultHolder<ItemStack> result = EventHandler.ITEM_INTERACTION_COMPLETED.invoker().onItemUseInteractionCompleted(level, player, startStack, completedStack);

            if (result.getResult() != InteractionResult.PASS) {

                if (result.getResult().consumesAction()) {
                    return result.getObject();
                }

                return startStack;
            }
        }
        return completedStack;
    }

    @Override
    public boolean stattinkerer$removeAllExceptNoAppetite() {
        if (this.level().isClientSide()) {
            return false;
        } else {
            Iterator<MobEffectInstance> iterator = this.activeEffects.values().iterator();

            boolean ret;
            for (ret = false; iterator.hasNext(); ret = true) {
                MobEffectInstance effect = iterator.next();

                if (effect.getEffect() == STMobEffects.NO_APPETITE) {
                    continue;
                }

                this.onEffectRemoved(effect);
                iterator.remove();
            }

            return ret;
        }
    }
}
