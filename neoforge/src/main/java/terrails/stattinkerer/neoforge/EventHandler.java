package terrails.stattinkerer.neoforge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import terrails.stattinkerer.feature.ExperienceFeature;
import terrails.stattinkerer.feature.HungerFeature;
import terrails.stattinkerer.feature.health.HealthFeature;

public class EventHandler {

    @SubscribeEvent
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        HealthFeature.INSTANCE.onPlayerJoinServer(player);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        ServerPlayer newPlayer = (ServerPlayer) event.getEntity();
        ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();

        ExperienceFeature.INSTANCE.onPlayerClone(event.isWasDeath(), newPlayer, oldPlayer);
        HungerFeature.INSTANCE.onPlayerClone(event.isWasDeath(), newPlayer, oldPlayer);
        HealthFeature.INSTANCE.onPlayerClone(event.isWasDeath(), newPlayer, oldPlayer);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.isEndConquered()) {
            HungerFeature.INSTANCE.onPlayerRespawn((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onItemUseInteraction(PlayerInteractEvent.RightClickItem event) {
        if (!event.isCanceled()) {
            InteractionResult result = HungerFeature.INSTANCE.onItemUseInteraction(event.getLevel(), event.getEntity(), event.getItemStack(), event.getHand());
            if (result != null) {
                event.setCancellationResult(result);
                event.setCanceled(true);
                return;
            }

            result = HealthFeature.INSTANCE.onItemUseInteraction(event.getLevel(), event.getEntity(), event.getItemStack(), event.getHand());
            if (result != null) {
                event.setCancellationResult(result);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemUseInteractionCompleted(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack result = HealthFeature.INSTANCE.onItemUseInteractionCompleted(player.level(), player, event.getItem(), event.getResultStack());
            if (result != null) {
                event.setResultStack(result);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        var blockState = level.getBlockState(event.getPos());
        var result = HungerFeature.INSTANCE.onBlockInteraction(blockState, level, event.getEntity(), event.getHitVec());
        if (result == InteractionResult.FAIL) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Player player) {
            event.setCanceled(!ExperienceFeature.INSTANCE.playerDropExperience(player));
        }
    }
}
