package terrails.stattinkerer.quilt.mixin.patches;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrails.stattinkerer.quilt.EventHandler;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

    @Inject(method = "useItem",
            cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
    private void itemInteraction(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        InteractionResultHolder<ItemStack> result = EventHandler.ITEM_INTERACTION_USE.invoker().onItemUseInteraction(level, player, stack, hand);

        if (result.getResult() != InteractionResult.PASS) {

            if (result.getResult().consumesAction()) {
                player.setItemInHand(hand, result.getObject());
            }

            callback.setReturnValue(result.getResult());
            callback.cancel();
        }
    }

    @Inject(method = "useItemOn",
            cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private void itemInteraction(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> callback) {
        InteractionResult result = EventHandler.BLOCK_INTERACTION.invoker().onBlockInteraction(level, player, hand, hitResult);

        if (result != InteractionResult.PASS) {

            if (result.consumesAction()) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(player, hitResult.getBlockPos(), stack.copy());
            }

            callback.setReturnValue(result);
            callback.cancel();
        }
    }
}
