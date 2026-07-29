package terrails.stattinkerer.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.BlockEvents;
import net.fabricmc.fabric.api.event.player.ItemEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import terrails.stattinkerer.CStatTinkerer;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.fabric.event.PlayerDeathEvent;
import terrails.stattinkerer.feature.ExperienceFeature;
import terrails.stattinkerer.feature.HungerFeature;
import terrails.stattinkerer.feature.health.HealthFeature;
import terrails.stattinkerer.mobeffect.NoAppetiteMobEffect;

import java.util.List;

public class StatTinkerer implements ModInitializer {

    @Override
    public void onInitialize() {
        CStatTinkerer.setup(new PlatformFunctionsImpl());
        STMobEffects.NO_APPETITE = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                CStatTinkerer.location("no_appetite"),
                new NoAppetiteMobEffect()
        );
        List<Object> compatObjects = CompatibilityHandler.setupCompat();
        ConfigHandler.setupConfig(compatObjects);
        this.registerEvents();
    }

    private void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            HealthFeature.INSTANCE.onPlayerJoinServer(handler.player);
            ConfigHandler.CONFIG.load();
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            ExperienceFeature.INSTANCE.onPlayerClone(!alive, newPlayer, oldPlayer);
            HungerFeature.INSTANCE.onPlayerClone(!alive, newPlayer, oldPlayer);
            HealthFeature.INSTANCE.onPlayerClone(!alive, newPlayer, oldPlayer);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                HungerFeature.INSTANCE.onPlayerRespawn(newPlayer);
            }
        });
        ItemEvents.USE.register(((level, player, hand) ->
                HungerFeature.INSTANCE.onItemUseInteraction(level, player, player.getItemInHand(hand), hand)));
        BlockEvents.USE_WITHOUT_ITEM.register((
                (state, level, pos, player, result)
                        -> HungerFeature.INSTANCE.onBlockInteraction(state, level, player, result)));
        BlockEvents.USE_ITEM_ON.register((
                (stack, state, level, pos, player, hand, result)
                        -> HungerFeature.INSTANCE.onBlockInteraction(state, level, player, result)));
        PlayerDeathEvent.EXPERIENCE_DROP.register(ExperienceFeature.INSTANCE::playerDropExperience);
    }
}
