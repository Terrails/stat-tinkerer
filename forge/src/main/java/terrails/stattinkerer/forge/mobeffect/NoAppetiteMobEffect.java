package terrails.stattinkerer.forge.mobeffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NoAppetiteMobEffect extends MobEffect {

    public NoAppetiteMobEffect() {
        super(MobEffectCategory.HARMFUL, new Color(72, 120, 68).getRGB());
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }
}
