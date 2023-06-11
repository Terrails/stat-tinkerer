package terrails.stattinkerer.feature.event;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface PlayerExpDropEvent {

    boolean playerDropExperience(Player player);
}