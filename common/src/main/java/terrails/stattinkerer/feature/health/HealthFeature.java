package terrails.stattinkerer.feature.health;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.health.HealthManager;
import terrails.stattinkerer.config.Configuration;
import terrails.stattinkerer.feature.CommonHelpers;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;

public class HealthFeature {

    public static final HealthFeature INSTANCE = new HealthFeature();

    public void onPlayerJoinServer(ServerPlayer player) {
        if (Configuration.HEALTH.systemEnabled.get()) {
            CStatTinkerer.PLATFORM.getHealthManager(player).ifPresent(manager -> manager.update(player));
        } else {
            HealthHelper.removeModifier(player);
        }
    }

    public void onPlayerClone(boolean wasDeath, ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        if (!wasDeath) return;

        if (Configuration.HEALTH.systemEnabled.get()) {

            Optional<HealthManager> optional = CStatTinkerer.PLATFORM.getHealthManager(newPlayer);
            if (optional.isPresent()) {
                HealthManager manager = optional.get();

                if (!CStatTinkerer.PLATFORM.getLoader().equals("neoforge")) {
                    CStatTinkerer.PLATFORM.getHealthManager(oldPlayer).ifPresent(oldManager -> {
                        manager.copyFrom(oldManager);
                        manager.setHealth(newPlayer, manager.getHealth());
                    });
                }

                if (Objects.equals(Configuration.HEALTH.startingHealth.get(), Configuration.HEALTH.maxHealth.get()) && Configuration.HEALTH.minHealth.get() == 0 && !Configuration.HEALTH.hardcoreMode.get()) {
                    manager.update(newPlayer);
                } else {
                    int decrease = Configuration.HEALTH.decreasedOnDeath.get();
                    if (!oldPlayer.isCreative() && !oldPlayer.isSpectator() && decrease > 0 && manager.isHealthRemovable()) {
                        int prevHealth = manager.getHealth();
                        manager.addHealth(newPlayer, -decrease);
                        newPlayer.setHealth(manager.getHealth());
                        double removedAmount = manager.getHealth() - prevHealth;

                        if (Configuration.HEALTH.healthChangeMessage.get() && removedAmount > 0) {
                            HealthHelper.playerMessage(newPlayer, "health.stattinkerer.death_remove", removedAmount);
                        }

                        if (Configuration.HEALTH.hardcoreMode.get() && manager.getHealth() <= 0) {
                            manager.reset(newPlayer);
                            newPlayer.setGameMode(GameType.SPECTATOR);
                        }
                    }
                }
            }
        }

        if (Configuration.HEALTH.respawnAmount.get() != 0) {
            newPlayer.setHealth(Configuration.HEALTH.respawnAmount.get());
        }
    }

    public InteractionResult onItemUseInteraction(Level level, Player _player, ItemStack stack, InteractionHand hand) {
        InteractionResult result = InteractionResult.PASS;
        if (Configuration.HEALTH.systemEnabled.get() && (_player instanceof ServerPlayer player) && !player.isCreative() && !player.isSpectator()) {

            /*
                Using Player#isCrouching has an edge case that happens when a player is stuck under 1,5 block height when a slab is above the head,
                so Player#isShiftKeyDown is better in this case
             */

            if (player.isShiftKeyDown() && Configuration.HEALTH.regenerativeItemsConsumptionMode.get() == Configuration.RegenerativeItemsConsumptionMode.NOT_CROUCHING) {
                return result;
            }

            if (!player.isShiftKeyDown() && Configuration.HEALTH.regenerativeItemsConsumptionMode.get() == Configuration.RegenerativeItemsConsumptionMode.CROUCHING) {
                return result;
            }

            Optional<HealthManager> optional = CStatTinkerer.PLATFORM.getHealthManager(player);
            if (optional.isPresent()) {
                HealthManager manager = optional.get();

                FoodProperties food = CommonHelpers.getFoodProperties(stack);
                if (food != null && player.canEat(food.canAlwaysEat())) {
                    return result;
                }

                if (stack.getUseAnimation() == ItemUseAnimation.DRINK) {
                    return result;
                }

                for (String itemString : Configuration.HEALTH.regenerativeItems.get()) {
                    Matcher matcher = CStatTinkerer.REGENERATIVE_ITEM_REGEX.matcher(itemString);

                    if (!matcher.find()) continue;

                    Identifier regName = Identifier.parse(matcher.group(1));
                    if (!Objects.equals(CStatTinkerer.PLATFORM.getItemRegistryName(stack.getItem()), regName)) {
                        continue;
                    }

                    int amount = Integer.parseInt(matcher.group(2));
                    boolean bypass = itemString.endsWith(":");

                    if (manager.addHealth(player, amount, bypass)) {
                        ItemStack resultStack = stack.copy();
                        resultStack.shrink(1);
                        result = InteractionResult.SUCCESS;
                    }
                    break;
                }
            }
        }
        return result;
    }

    public ItemStack onItemUseInteractionCompleted(Level level, Player _player, ItemStack startStack, ItemStack endStack) {
        if (Configuration.HEALTH.systemEnabled.get() && (_player instanceof ServerPlayer player) && !player.isCreative() && !player.isSpectator()) {

            for (String itemString : Configuration.HEALTH.regenerativeItems.get()) {
                Matcher matcher = CStatTinkerer.REGENERATIVE_ITEM_REGEX.matcher(itemString);

                if (!matcher.find()) continue;

                Identifier regName = Identifier.parse(matcher.group(1));
                if (!Objects.equals(CStatTinkerer.PLATFORM.getItemRegistryName(startStack.getItem()), regName)) {
                    continue;
                }

                int amount = Integer.parseInt(matcher.group(2));
                boolean bypass = itemString.endsWith(":");

                CStatTinkerer.PLATFORM.getHealthManager(player).ifPresent(manager -> manager.addHealth(player, amount, bypass));

                if (ItemStack.matches(startStack, endStack)) {
                    ItemStack resultStack = endStack.copy();
                    resultStack.shrink(1);
                    return resultStack;
                }
                break;
            }
        }
        return null;
    }
}
