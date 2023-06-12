package terrails.stattinkerer.fabric.mixin.patches;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import terrails.stattinkerer.fabric.EventHandler;
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) { super(entityType, level); }

    /**
     * @author Terrails
     * @reason Experience drop event
     */
    @Overwrite
    public boolean isAlwaysExperienceDropper() {
        return EventHandler.EXPERIENCE_DROP.invoker().playerDropExperience((Player) (Object) this);
    }

    /**
     * Always depend on the above overwritten method.
     * The event would have to be run twice in a row otherwise because this method is run if the above method returns false
     */
    @Override
    public boolean shouldDropExperience() {
        return false;
    }
}
