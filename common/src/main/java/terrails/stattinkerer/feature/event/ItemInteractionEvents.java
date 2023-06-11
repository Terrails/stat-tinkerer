package terrails.stattinkerer.feature.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ItemInteractionEvents {

    @FunctionalInterface
    interface Use {
        InteractionResultHolder<ItemStack> onItemUseInteraction(Level level, Player player, ItemStack stack, InteractionHand hand);
    }

    @FunctionalInterface
    interface Completed {
        InteractionResultHolder<ItemStack> onItemUseInteractionCompleted(Level level, Player player, ItemStack startStack, ItemStack endStack);
    }
}
