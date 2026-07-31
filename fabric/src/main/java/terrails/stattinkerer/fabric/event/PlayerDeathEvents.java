package terrails.stattinkerer.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public interface PlayerDeathEvents {

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
    Event<ExperienceDropEvent> EXPERIENCE_DROP = EventFactory.createArrayBacked(ExperienceDropEvent.class,
            (listeners) -> (player) -> {
                boolean ret = true;
                for (ExperienceDropEvent event : listeners) {
                    if (!event.drop(player) && ret) {
                        ret = false;
                    }
                }
                return ret;
            });

    @FunctionalInterface
    interface ExperienceDropEvent {

        boolean drop(Player player);
    }
}
