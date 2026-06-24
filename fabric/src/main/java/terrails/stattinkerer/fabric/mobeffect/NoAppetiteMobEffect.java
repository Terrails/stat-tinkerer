package terrails.stattinkerer.fabric.mobeffect;

import net.minecraft.core.Holder;
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

    public static Holder<MobEffect> registerEffect() {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(CStatTinkerer.MOD_ID, "no_appetite"), new NoAppetiteMobEffect());
    }
}
