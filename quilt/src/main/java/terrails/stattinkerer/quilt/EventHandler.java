package terrails.stattinkerer.quilt;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import terrails.stattinkerer.feature.event.BlockInteractionEvent;
import terrails.stattinkerer.feature.event.ItemInteractionEvents;
import terrails.stattinkerer.feature.event.PlayerExpDropEvent;

public interface EventHandler {

    /**
     * <b>Event that is called when an item is right-clicked ("used").</b>
     *
     * <p>Provided values are:</p>
     * <ul>
     *     <li>level: Current level</li>
     *     <li>player: Player using the item</li>
     *     <li>stack: Used item</li>
     *     <li>hand: Hand holding the item</li>
     * </ul>
     *
     * <p>Upon return:</p>
     * <ul>
     *     <li>SUCCESS, CONSUME, CONSUME_PARTIAL replaces the ItemStack in hand with the result.</li>
     *     <li>PASS executes default behaviour.</li>
     *     <li>FAIL cancels item use.</li>
     * </ul>
     */
    Event<ItemInteractionEvents.Use> ITEM_INTERACTION_USE = EventFactory.createArrayBacked(ItemInteractionEvents.Use.class,
            (listeners) -> (level, player, stack, hand) -> {
                for (ItemInteractionEvents.Use event : listeners) {
                    InteractionResultHolder<ItemStack> result = event.onItemUseInteraction(level, player, stack, hand);

                    if (result.getResult() != InteractionResult.PASS) {
                        return result;
                    }

                }
                return InteractionResultHolder.pass(ItemStack.EMPTY);
            });

    /**
     * <b>Event that is called when an item is about to finish its right-clicking ("using") animation.</b>
     *
     * <p>Provided values are:</p>
     * <ul>
     *     <li>level: Current level</li>
     *     <li>player: Player using the item</li>
     *     <li>stack: Item in use</li>
     * </ul>
     *
     * <p>Upon return:</p>
     * <ul>
     *     <li>SUCCESS, CONSUME, CONSUME_PARTIAL triggers the items ItemStack#triggerItemUseEffects and replaces the ItemStack in hand with the result.</li>
     *     <li>PASS executes default behaviour.</li>
     *     <li>FAIL cancels item use.</li>
     * </ul>
     */
    Event<ItemInteractionEvents.Completed> ITEM_INTERACTION_COMPLETED = EventFactory.createArrayBacked(ItemInteractionEvents.Completed.class,
            (listeners) -> (level, player, startStack, completedStack) -> {
                for (ItemInteractionEvents.Completed event : listeners) {
                    InteractionResultHolder<ItemStack> result = event.onItemUseInteractionCompleted(level, player, startStack, completedStack);

                    if (result.getResult() != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResultHolder.pass(ItemStack.EMPTY);
            });

    /**
     * <b>Event that is called when a block is right-clicked ("used").</b>
     *
     * <p>Provided values are:</p>
     * <ul>
     *     <li>level: Current level</li>
     *     <li>player: Player using the item</li>
     *     <li>hand: Hand holding the item</li>
     *     <li>hitResult: Used blocks info</li>
     * </ul>
     *
     * <p>Upon return:</p>
     * <ul>
     *     <li>SUCCESS, CONSUME, CONSUME_PARTIAL returns the result and sends an "item used on" packet to the server.</li>
     *     <li>PASS executes default behaviour.</li>
     *     <li>FAIL cancels item use.</li>
     * </ul>
     */
    Event<BlockInteractionEvent> BLOCK_INTERACTION = EventFactory.createArrayBacked(BlockInteractionEvent.class,
            (listeners) -> (level, player, hand, hitResult) -> {
                for (BlockInteractionEvent event : listeners) {
                    InteractionResult result = event.onBlockInteraction(level, player, hand, hitResult);

                    if (result != InteractionResult.PASS) {
                        return result;
                    }
                }
                return InteractionResult.PASS;
            });

    /**
     * <b>Event that is called when a player is about to drop xp.</b>
     *
     * <p>Provided values are:</p>
     * <ul>
     *     <li>player: Player about to drop xp</li>
     * </ul>
     *
     * <p>Upon return:</p>
     * <ul>
     *     <li>true allows xp drop.</li>
     *     <li>false stops xp drop.</li>
     * </ul>
     */
    Event<PlayerExpDropEvent> EXPERIENCE_DROP = EventFactory.createArrayBacked(PlayerExpDropEvent.class,
            (listeners) -> (player) -> {
                boolean ret = true;
                for (PlayerExpDropEvent event : listeners) {
                    if (!event.playerDropExperience(player) && ret) {
                        ret = false;
                    }
                }
                return ret;
            });
}
