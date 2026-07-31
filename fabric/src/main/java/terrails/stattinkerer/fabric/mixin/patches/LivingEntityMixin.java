package terrails.stattinkerer.fabric.mixin.patches;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import terrails.stattinkerer.fabric.event.PlayerInteractionEvents;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapOperation(method = "completeUsingItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack completeUsingItem$stattinkerer(ItemStack useItem, Level level, LivingEntity entity, Operation<ItemStack> original) {
        ItemStack copy = useItem.copy();
        ItemStack completedStack = original.call(useItem, level, entity);
        if (entity instanceof Player player) {
            return PlayerInteractionEvents.ITEM_USE_COMPLETED.invoker().onItemUseCompleted(level, player, copy, completedStack);
        }
        return completedStack;
    }
}
