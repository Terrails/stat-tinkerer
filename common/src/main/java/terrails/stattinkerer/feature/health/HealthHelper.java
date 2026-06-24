package terrails.stattinkerer.feature.health;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import terrails.stattinkerer.CStatTinkerer;

import java.util.UUID;

public class HealthHelper {

    public static ResourceLocation HEALTH_MODIFIER = ResourceLocation.fromNamespaceAndPath(CStatTinkerer.MOD_ID, "health");

    public static final String TAG_GROUP = CStatTinkerer.MOD_ID + ":health";
    public static final String TAG_ADDITIONAL_HEALTH = CStatTinkerer.MOD_ID + ":additional_health";
    public static final String TAG_MAX_HEALTH = CStatTinkerer.MOD_ID + ":max_health";
    public static final String TAG_MIN_HEALTH = CStatTinkerer.MOD_ID + ":min_health";
    public static final String TAG_STARTING_HEALTH = CStatTinkerer.MOD_ID + ":starting_health";
    public static final String TAG_HEALTH_THRESHOLD = CStatTinkerer.MOD_ID + ":health_threshold";

    public static AttributeInstance getAttribute(Player player) {
        return player.getAttribute(Attributes.MAX_HEALTH);
    }

    public static void addModifier(Player player, int amount) {
        AttributeInstance attribute = HealthHelper.getAttribute(player);

        attribute.removeModifier(HEALTH_MODIFIER);
        attribute.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER, amount - attribute.getBaseValue(), AttributeModifier.Operation.ADD_VALUE));
    }

    public static boolean hasModifier(Player player) {
        return getAttribute(player).getModifier(HEALTH_MODIFIER) != null;
    }

    public static void removeModifier(Player player) {
        getAttribute(player).removeModifier(HEALTH_MODIFIER);
    }

    public static void playerMessage(Player player, String key, double health) {
        if (health == 0) return;
        double messageAmount = health / 2.0;
        Component component = messageAmount % 1 != 0 ? Component.translatable(key, messageAmount) : Component.translatable(key, (int) messageAmount);
        player.displayClientMessage(component, true);
    }
}
