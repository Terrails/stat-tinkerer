package terrails.stattinkerer.feature.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

@FunctionalInterface
public interface BlockInteractionEvent {

    InteractionResult onBlockInteraction(Level level, Player player, InteractionHand hand, BlockHitResult hitResult);
}
