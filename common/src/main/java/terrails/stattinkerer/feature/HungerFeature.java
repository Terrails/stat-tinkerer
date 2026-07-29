package terrails.stattinkerer.feature;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.config.Configuration;

public class HungerFeature {

    public static final HungerFeature INSTANCE = new HungerFeature();

    public void onPlayerRespawn(ServerPlayer player) {
        int duration = Configuration.HUNGER.noAppetiteDuration.get();
        if (duration > 0 && !player.isCreative() && !player.isSpectator()) {
            player.addEffect(new MobEffectInstance(STMobEffects.NO_APPETITE, duration * 20, 0, false, false, true));
        }
    }

    public void onPlayerClone(boolean wasDeath, ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        if (wasDeath) {

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

    public InteractionResult onItemUseInteraction(Level level, Player player, ItemStack stack, InteractionHand hand) {
        if (player.hasEffect(STMobEffects.NO_APPETITE)) {
            FoodProperties food = CommonHelpers.getFoodProperties(stack);
            if (food != null && player.canEat(food.canAlwaysEat())) {
                return InteractionResult.FAIL;
            }
        }
        return null;
    }

    public InteractionResult onBlockInteraction(BlockState blockState, Level level, Player player, BlockHitResult hitResult) {
        if (player.hasEffect(STMobEffects.NO_APPETITE)) {
            Block block = blockState.getBlock();
            // TODO: Add a way to manually define blocks in config and compare with registry name
            if (block instanceof CakeBlock) {
                return InteractionResult.FAIL;
            }
        }
        return null;
    }
}
