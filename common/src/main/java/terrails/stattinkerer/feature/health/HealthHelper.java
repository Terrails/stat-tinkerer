package terrails.stattinkerer.feature.health;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import terrails.stattinkerer.CStatTinkerer;

import java.util.UUID;

public class HealthHelper {

    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("b4720be1-df42-4347-9625-34152fb82b3f");

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
        attribute.removeModifier(HEALTH_MODIFIER_UUID);
        attribute.addPermanentModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, CStatTinkerer.MOD_ID, amount - attribute.getBaseValue(), AttributeModifier.Operation.ADDITION));
    }

    public static boolean hasModifier(Player player) {
        return getAttribute(player).getModifier(HEALTH_MODIFIER_UUID) != null;
    }

    public static void removeModifier(Player player) {
        getAttribute(player).removeModifier(HEALTH_MODIFIER_UUID);
    }

    public static void playerMessage(Player player, String key, double health) {
        if (health == 0) return;
        double messageAmount = health / 2.0;
        Component component = messageAmount % 1 != 0 ? Component.translatable(key, messageAmount) : Component.translatable(key, (int) messageAmount);
        player.displayClientMessage(component, true);
    }
}
