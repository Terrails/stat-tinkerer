package terrails.stattinkerer.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import terrails.stattinkerer.feature.ExperienceFeature;
import terrails.stattinkerer.feature.HungerFeature;
import terrails.stattinkerer.feature.health.HealthFeature;

@Mod.EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        HealthFeature.INSTANCE.onPlayerJoinServer(player);
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        boolean isEnd = !event.isWasDeath();
        ServerPlayer newPlayer = (ServerPlayer) event.getEntity();
        ServerPlayer oldPlayer = (ServerPlayer) event.getOriginal();

        ExperienceFeature.INSTANCE.onPlayerClone(isEnd, newPlayer, oldPlayer);
        HungerFeature.INSTANCE.onPlayerClone(isEnd, newPlayer, oldPlayer);
        HealthFeature.INSTANCE.onPlayerClone(isEnd, newPlayer, oldPlayer);

//        if (ModList.get().isLoaded("toughasnails")) {
//            TANFeature.INSTANCE.onPlayerClone(isEnd, newPlayer, oldPlayer);
//        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        HungerFeature.INSTANCE.onPlayerRespawn(player);
        HealthFeature.INSTANCE.onPlayerRespawn(player);
    }

    @SubscribeEvent
    public static void onItemUseInteraction(PlayerInteractEvent.RightClickItem event) {
        if (!event.isCanceled()) {
            InteractionResultHolder<ItemStack> result = HungerFeature.INSTANCE.onItemUseInteraction(event.getLevel(), event.getEntity(), event.getItemStack(), event.getHand());
            if (result.getResult() != InteractionResult.PASS) {

                if (result.getResult().consumesAction()) {
                    event.getEntity().setItemInHand(event.getHand(), result.getObject());
                }

                event.setCanceled(true);
                return;
            }

            result = HealthFeature.INSTANCE.onItemUseInteraction(event.getLevel(), event.getEntity(), event.getItemStack(), event.getHand());
            if (result.getResult() != InteractionResult.PASS) {

                if (result.getResult().consumesAction()) {
                    event.getEntity().setItemInHand(event.getHand(), result.getObject());
                }

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemUseInteractionCompleted(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {

            InteractionResultHolder<ItemStack> result = HealthFeature.INSTANCE.onItemUseInteractionCompleted(player.level(), player, event.getItem(), event.getResultStack());
            if (result.getResult() != InteractionResult.PASS) {

                if (result.getResult().consumesAction()) {
                    event.setResultStack(result.getObject());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockInteraction(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = HungerFeature.INSTANCE.onBlockInteraction(event.getLevel(), event.getEntity(), event.getHand(), event.getHitVec());
        event.setCanceled(result == InteractionResult.FAIL);
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Player player) {
            event.setCanceled(!ExperienceFeature.INSTANCE.playerDropExperience(player));
        }
    }
}
