package terrails.stattinkerer.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import terrails.stattinkerer.CStatTinkerer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Configuration {

    private static final Pattern REGENERATIVE_ITEM_REGEX = Pattern.compile("^((?:[a-z0-9_.-]+:)?[a-z0-9_.-]+)\\s=\\s(-?\\d+):?$");

    public static final Experience EXPERIENCE = new Experience();
    public static final Hunger HUNGER = new Hunger();
    public static final Health HEALTH = new Health();

    public static class Experience {

        public final SimpleConfigOption<Boolean> keep = SimpleConfigOption.define(
                "experience.keep",
                "Keep experience on death.",
                false
        );

        public final SimpleConfigOption<Boolean> drop = SimpleConfigOption.define(
                "experience.drop",
                """
                        Drop experience on death.
                        Make sure to disable this while using the keep option due to XP dupes.""",
                true
        );
    }

    public static class Hunger {

        public final SimpleConfigOption<Boolean> keepHunger = SimpleConfigOption.define(
                "hunger.keep",
                "Keep hunger on death.",
                false
        );

        public final SimpleConfigOption<Integer> lowestHunger = SimpleConfigOption.defineInRange(
                "hunger.lowest",
                "Lowest hunger value kept on death.",
                6, 0, 20);

        public final SimpleConfigOption<Boolean> keepSaturation = SimpleConfigOption.define(
                "hunger.saturation.keep",
                "Keep saturation on death",
                false
        );

        public final SimpleConfigOption<Integer> lowestSaturation = SimpleConfigOption.defineInRange(
                "hunger.saturation.lowest",
                "Lowest saturation value kept on death.",
                6, 0, 20
        );

        public final SimpleConfigOption<Boolean> keepSaturationRestricted = SimpleConfigOption.define(
                "hunger.saturation.whenHungerFull",
                "Keep saturation only when hunger is full.",
                false
        );

        public final SimpleConfigOption<Integer> noAppetiteDuration = SimpleConfigOption.defineInRange(
                "hunger.no_appetite.duration",
                """
                        Duration of time in seconds that the effect will be active for after respawning.
                        While the effect is active, the player cannot eat anything that would replenish hunger.""",
                0, 0, Integer.MAX_VALUE
        );

        public final ConfigOption<List<String>, List<ResourceLocation>> noAppetiteBlocks = ConfigOption.defineList(
                "hunger.no_appetite.blockedBlocks",
                """
                        Blocks that cannot be interacted with when the effect is active.
                        Majority of cake blocks are already blocked and should work without adding to this list.""",
                new ArrayList<>(), o -> o instanceof String s && ResourceLocation.isValidResourceLocation(s), ResourceLocation::toString, ResourceLocation::new
        );
    }

    public static class Health {

        public final SimpleConfigOption<Integer> respawnAmount = SimpleConfigOption.defineInRange(
                "health.respawnHealth",
                "Amount of health to respawn with. Disabled if set to 0.",
                0, 0, Integer.MAX_VALUE
        );

        public final SimpleConfigOption<Boolean> systemEnabled = SimpleConfigOption.define(
                "health.system.enabled",
                "Enable all health system related modifications",
                false
        );

        public final SimpleConfigOption<Integer> maxHealth = SimpleConfigOption.defineInRange(
                "health.system.maxHealth",
                """
                        Highest amount of health.
                        Please note that this value is limited to 1024 in vanilla, but a higher limit is given in case of mods that modify this limitation.""",
                20, 1, Integer.MAX_VALUE
        );

        public final SimpleConfigOption<Integer> minHealth = SimpleConfigOption.defineInRange(
                "health.system.minHealth",
                """
                        Lowest amount of health.
                        If set to 0, only maxHealth is used and any other option inside the category is ignored as they require this to be functional.""",
                0, 0, Integer.MAX_VALUE
        );

        public final SimpleConfigOption<Integer> decreasedOnDeath = SimpleConfigOption.defineInRange(
                "health.system.deathDecreasedHealth",
                """
                        Amount of health lost on each death.
                        Requires minHealth to be higher than 0.""",
                0, 0, Integer.MAX_VALUE
        );

        public final SimpleConfigOption<Integer> startingHealth = SimpleConfigOption.defineInRange(
                "health.system.startingHealth",
                """
                        Amount of health that a player starts with.
                        Requires minHealth to be higher than 0.""",
                20, 1, Integer.MAX_VALUE
        );

        public final SimpleConfigOption<List<OnChangeReset>> onChangeReset = SimpleConfigOption.defineEnumList(
                "health.system.additional.configChangeReset",
                "Config options which when changed should be considered for max health reset in an already created world",
                OnChangeReset.class, Arrays.stream(OnChangeReset.values()).toList(), EnumGetMethod.NAME_IGNORECASE
        );

        public final SimpleConfigOption<Boolean> healthChangeMessage = SimpleConfigOption.define(
                "health.system.additional.healthChangeMessage",
                "Show a message when a threshold is reached and when health is gained or lost.",
                true
        );

        public final SimpleConfigOption<Boolean> hardcoreMode = SimpleConfigOption.define(
                "health.system.additional.hardcoreMode",
                """
                        Enabled hardcore mode which makes the player a spectator when 0 maximal health is reached.
                        Setting minHealth to 0 and removing all healthThresholds is required or unexpected behaviour might occur.""",
                false
        );

        public final SimpleConfigOption<List<Integer>> thresholds = SimpleConfigOption.defineList(
                "health.system.additional.healthThresholds",
                """
                        Values which, when reached, move the lowest health of the player to the achieved value. Requires the use of deathDecreasedHealth.
                        Example: If a player starts at 10 health and reaches a threshold of 16 using regenerative items or similar, the lowest max health will move to 16 health.
                        Lowest threshold value can be non-removable, meaning that max health will not decrease until a player reaches health that is over the lowest threshold.
                        To use it make the lowest value negative.""",
                Lists.newArrayList(-8, 16), o -> o instanceof Integer
        );

        public final SimpleConfigOption<RegenerativeItemsConsumptionMode> regenerativeItemsConsumptionMode = SimpleConfigOption.defineEnum(
                "health.system.additional.regenerativeItemsConsumptionMode",
                """
                        Condition for consumption of regenerative items.
                        These values only apply on items without any use animations as to not consume them unintentionally.
                        Acceptable values: [ NOT_CROUCHING, CROUCHING, BOTH ]""",
                RegenerativeItemsConsumptionMode.class, RegenerativeItemsConsumptionMode.NOT_CROUCHING, EnumGetMethod.NAME_IGNORECASE
        );

        public final ConfigOption<List<String>, List<RegenerativeItem>> regenerativeItems = ConfigOption.defineList(
                "health.system.additional.regenerativeItems",
                """
                        Items that increase/decrease current maximal health when used.
                        Format: "modid:item = N" with N being the health amount.
                        Appending a colon ':' after a negative N will make an item bypass healthThresholds, meaning that maximal health can go below a threshold until minHealth is reached""",
                Lists.newArrayList("minecraft:nether_star = 1", "minecraft:enchanted_golden_apple = 1"),
                o -> {
                    if (!(o instanceof String s)) return false;
                    Matcher matcher = REGENERATIVE_ITEM_REGEX.matcher(s.toLowerCase(Locale.ROOT));
                    return matcher.matches() && ResourceLocation.isValidResourceLocation(matcher.group(1));
                },
                item -> {
                    String value = item.registryName.toString();
                    value += " = " + item.amount;
                    if (item.bypass) value += ":";
                    return value;
                }, value -> {
                    value = value.toLowerCase(Locale.ROOT);
                    Matcher matcher = REGENERATIVE_ITEM_REGEX.matcher(value);
                    matcher.matches(); // has to be called so that groups get populated. Should always be true since it was checked in the validator above
                    ResourceLocation registryName = new ResourceLocation(matcher.group(1));
                    int amount = Integer.parseInt(matcher.group(2));
                    boolean bypass = value.endsWith(":");
                    return new RegenerativeItem(registryName, amount, bypass);
                }
        );
    }

    public enum OnChangeReset {
        MIN_HEALTH, MAX_HEALTH, STARTING_HEALTH;

        public static final Map<String, OnChangeReset> NAME_MAP = Arrays.stream(OnChangeReset.values()).collect(Collectors.toMap(Enum::name, l -> l));
    }

    public enum RegenerativeItemsConsumptionMode {
        NOT_CROUCHING, CROUCHING, BOTH;
    }

    public record RegenerativeItem(ResourceLocation registryName, int amount, boolean bypass) { }
}
