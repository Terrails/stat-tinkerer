package terrails.stattinkerer.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.awt.*;

public class NoAppetiteMobEffect extends MobEffect {

    public NoAppetiteMobEffect() {
        super(MobEffectCategory.HARMFUL, new Color(72, 120, 68).getRGB());
    }
}
