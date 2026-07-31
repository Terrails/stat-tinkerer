package terrails.stattinkerer.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface PlayerInteractionEvents {

    /**
     * <b>Event that is called when an item is about to finish its right-clicking ("using") animation.</b>
     *
     * <p>Provided values are:</p>
     * <ul>
     *     <li>level: Current level</li>
     *     <li>player: Player using the item</li>
     *     <li>startStack: Item in use</li>
     *     <li>completedStack: Item after use</li>
     * </ul>
     *
     * <p>Upon return:</p>
     * <ul>
     *     <li>null executes default behavior where the startStack is replaced by completedStack.</li>
     *     <li>Non-null will replace startStack with the result.</li>
     * </ul>
     */
    Event<ItemUseCompleted> ITEM_USE_COMPLETED = EventFactory.createArrayBacked(ItemUseCompleted.class,
            (listeners) -> (level, player, startStack, completedStack) -> {
                var result = completedStack;
                for (ItemUseCompleted event : listeners) {
                    var stack = event.onItemUseCompleted(level, player, startStack, completedStack);
                    if (stack != null) {
                        result = stack;
                    }
                }
                return result;
            });

    @FunctionalInterface
    interface ItemUseCompleted {

        ItemStack onItemUseCompleted(Level level, Player player, ItemStack originalStack, ItemStack endStack);
    }
}
