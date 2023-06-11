package terrails.stattinkerer.feature;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import terrails.stattinkerer.config.Configuration;
import terrails.stattinkerer.feature.event.PlayerExpDropEvent;
import terrails.stattinkerer.feature.event.PlayerStateEvents;

public class ExperienceFeature implements PlayerStateEvents.Clone, PlayerExpDropEvent {

    public static final ExperienceFeature INSTANCE = new ExperienceFeature();

    @Override
    public void onPlayerClone(boolean isEnd, ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        if (!isEnd && Configuration.EXPERIENCE.keep.get()) {
            boolean keepInventory = newPlayer.getCommandSenderWorld().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
            if (!keepInventory) {
                newPlayer.experienceLevel = oldPlayer.experienceLevel;
                newPlayer.totalExperience = oldPlayer.totalExperience;
                newPlayer.experienceProgress = oldPlayer.experienceProgress;
                newPlayer.setScore(oldPlayer.getScore());
            }
        }
    }

    @Override
    public boolean playerDropExperience(Player player) {
        return Configuration.EXPERIENCE.drop.get();
    }
}
