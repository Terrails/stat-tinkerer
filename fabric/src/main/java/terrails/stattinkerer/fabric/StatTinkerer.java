package terrails.stattinkerer.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import terrails.stattinkerer.api.STMobEffects;
import terrails.stattinkerer.config.Configuration;
import terrails.stattinkerer.fabric.config.FabConfig;
import terrails.stattinkerer.fabric.mobeffect.NoAppetiteMobEffect;
import terrails.stattinkerer.feature.ExperienceFeature;
import terrails.stattinkerer.feature.HungerFeature;
import terrails.stattinkerer.feature.health.HealthFeature;

public class StatTinkerer implements ModInitializer {

    public static FabConfig CONFIG;

    @Override
    public void onInitialize() {
        CONFIG = new FabConfig(Configuration.EXPERIENCE, Configuration.HUNGER, Configuration.HEALTH);
        STMobEffects.NO_APPETITE = NoAppetiteMobEffect.registerEffect();
        registerEvents();

    }

    private static void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            HealthFeature.INSTANCE.onPlayerJoinServer(handler.player);
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
        EventHandler.ITEM_INTERACTION_USE.register(HungerFeature.INSTANCE);
        EventHandler.ITEM_INTERACTION_USE.register(HealthFeature.INSTANCE);
        EventHandler.ITEM_INTERACTION_COMPLETED.register(HealthFeature.INSTANCE);
        EventHandler.BLOCK_INTERACTION.register(HungerFeature.INSTANCE);
        EventHandler.EXPERIENCE_DROP.register(ExperienceFeature.INSTANCE);
    }
}
