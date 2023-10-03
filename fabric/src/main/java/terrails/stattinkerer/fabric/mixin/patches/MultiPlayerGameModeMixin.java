package terrails.stattinkerer.fabric.mixin.patches;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import terrails.stattinkerer.fabric.EventHandler;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow @Final private Minecraft minecraft;

    @Redirect(method = "method_41929",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
    private InteractionResultHolder<ItemStack> stattinkerer$useInteractionItem(ItemStack instance, Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = EventHandler.ITEM_INTERACTION_USE.invoker().onItemUseInteraction(this.minecraft.level, player, player.getItemInHand(hand), hand);

        if (result.getResult() != InteractionResult.PASS) {
            return result;
        } else return instance.use(this.minecraft.level, player, hand);
    }

    @Inject(method = "performUseItemOn",
            cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private void stattinkerer$useInteractionBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> callback) {
        InteractionResult result = EventHandler.BLOCK_INTERACTION.invoker().onBlockInteraction(this.minecraft.level, player, hand, hitResult);

        if (result != InteractionResult.PASS) {
            callback.setReturnValue(result);
            callback.cancel();
        }
    }
}
