package terrails.stattinkerer.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import terrails.stattinkerer.feature.event.PlayerExpDropEvent;

public interface PlayerDeathEvent {

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
