package terrails.stattinkerer.fabric.mobeffect;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import terrails.stattinkerer.CStatTinkerer;

import java.awt.*;

public class NoAppetiteMobEffect extends MobEffect {

    public NoAppetiteMobEffect() {
        super(MobEffectCategory.HARMFUL, new Color(72, 120, 68).getRGB());
    }

    public static MobEffect registerEffect() {
        return Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(CStatTinkerer.MOD_ID, "no_appetite"), new NoAppetiteMobEffect());
    }
}
