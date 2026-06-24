package terrails.stattinkerer.feature;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class CommonHelpers {

    public static FoodProperties getFoodProperties(ItemStack stack) {
        if (!stack.has(DataComponents.FOOD)) {
            return null;
        }

        return stack.get(DataComponents.FOOD);
    }
}
