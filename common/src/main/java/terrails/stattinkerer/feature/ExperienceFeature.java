package terrails.stattinkerer.feature;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import terrails.stattinkerer.config.Configuration;

public class ExperienceFeature {

    public static final ExperienceFeature INSTANCE = new ExperienceFeature();

    public void onPlayerClone(boolean wasDeath, ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        if (wasDeath && Configuration.EXPERIENCE.keep.get()) {
            boolean keepInventory = newPlayer.level().getGameRules().get(GameRules.KEEP_INVENTORY);
            if (!keepInventory) {
                newPlayer.experienceLevel = oldPlayer.experienceLevel;
                newPlayer.totalExperience = oldPlayer.totalExperience;
                newPlayer.experienceProgress = oldPlayer.experienceProgress;
                newPlayer.setScore(oldPlayer.getScore());
            }
        }
    }

    public boolean playerDropExperience(Player player) {
        return Configuration.EXPERIENCE.drop.get();
    }
}
