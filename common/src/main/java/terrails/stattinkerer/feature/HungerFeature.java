package terrails.stattinkerer.feature;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.phys.BlockHitResult;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.config.Configuration;
import terrails.stattinkerer.feature.event.BlockInteractionEvent;
import terrails.stattinkerer.feature.event.ItemInteractionEvents;
import terrails.stattinkerer.feature.event.PlayerStateEvents;

public class HungerFeature implements PlayerStateEvents.Clone, PlayerStateEvents.Respawn, ItemInteractionEvents.Use, BlockInteractionEvent {

    public static final HungerFeature INSTANCE = new HungerFeature();

    @Override
    public void onPlayerClone(boolean isEnd, ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        if (!isEnd) {

            if (Configuration.HUNGER.keepHunger.get()) {
                int value = Math.max(Configuration.HUNGER.lowestHunger.get(), oldPlayer.getFoodData().getFoodLevel());
                newPlayer.getFoodData().setFoodLevel(value);
            }

            if (Configuration.HUNGER.keepSaturation.get() && (!Configuration.HUNGER.keepSaturationRestricted.get() || !oldPlayer.getFoodData().needsFood())) {
                float value = Math.max(Configuration.HUNGER.lowestSaturation.get(), oldPlayer.getFoodData().getSaturationLevel());
                newPlayer.getFoodData().setSaturation(value);
            }
        }
    }

    @Override
    public void onPlayerRespawn(ServerPlayer player) {
        if (Configuration.HUNGER.noAppetiteDuration.get() > 0 && !player.isCreative() && !player.isSpectator()) {
            player.addEffect(new MobEffectInstance(STMobEffects.NO_APPETITE, Configuration.HUNGER.noAppetiteDuration.get() * 20, 0, false, false, true));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> onItemUseInteraction(Level level, Player player, ItemStack stack, InteractionHand hand) {
        if (player.hasEffect(STMobEffects.NO_APPETITE)) {
            FoodProperties food = stack.getItem().getFoodProperties();
            if (food != null && player.canEat(food.canAlwaysEat())) {
                return InteractionResultHolder.fail(stack);
            }
        }
        return InteractionResultHolder.pass(ItemStack.EMPTY);
    }

    @Override
    public InteractionResult onBlockInteraction(Level level, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.hasEffect(STMobEffects.NO_APPETITE)) {
            Block block = level.getBlockState(hitResult.getBlockPos()).getBlock();
            // TODO: Add a way to manually define blocks in config and compare with registry name
            if (block instanceof CakeBlock) {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }
}
