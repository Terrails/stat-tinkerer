package terrails.stattinkerer.forge.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;

import java.awt.*;
import java.util.Set;

public class NoAppetiteMobEffect extends MobEffect {

    public NoAppetiteMobEffect() {
        super(MobEffectCategory.HARMFUL, new Color(72, 120, 68).getRGB());
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) { }
}
