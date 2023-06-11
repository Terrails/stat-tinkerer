package terrails.stattinkerer.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import terrails.stattinkerer.CStatTinkerer;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;

public class Configuration {

    public static final Experience EXPERIENCE = new Experience();
    public static final Hunger HUNGER = new Hunger();
    public static final Health HEALTH = new Health();

    public static class Experience {

        public final ConfigOption<Boolean> keep = ConfigOption.define(
                "experience.keep",
                "Keep experience on death.",
                false
        );

        public final ConfigOption<Boolean> drop = ConfigOption.define(
                "experience.drop",
                """
                        Drop experience on death.
                        Make sure to disable this while using the keep option due to XP dupes.""",
                true
        );

    }

    public static class Hunger {

        public final ConfigOption<Boolean> keepHunger = ConfigOption.define(
                "hunger.keep",
                "Keep hunger on death.",
                false
        );

        public final ConfigOption<Integer> lowestHunger = ConfigOption.defineInRange(
                "hunger.lowest",
                "Lowest hunger value kept on death.",
                6, 0, 20);

        public final ConfigOption<Boolean> keepSaturation = ConfigOption.define(
                "hunger.saturation.keep",
                "Keep saturation on death",
                false
        );

        public final ConfigOption<Integer> lowestSaturation = ConfigOption.defineInRange(
                "hunger.saturation.lowest",
                "Lowest saturation value kept on death.",
                6, 0, 20
        );

        public final ConfigOption<Boolean> keepSaturationRestricted = ConfigOption.define(
                "hunger.saturation.whenHungerFull",
                "Keep saturation only when hunger is full.",
                false
        );

        public final ConfigOption<Integer> noAppetiteDuration = ConfigOption.defineInRange(
                "hunger.no_appetite.duration",
                """
                        Duration of time in seconds that the effect will be active for after respawning.
                        While the effect is active, the player cannot eat anything that would replenish hunger.""",
                0, 0, Integer.MAX_VALUE
        );

    }

    public static class Health {

        public final ConfigOption<Integer> respawnAmount = ConfigOption.defineInRange(
                "health.respawnHealth",
                "Amount of health to respawn with. Disabled if set to 0.",
                0, 0, Integer.MAX_VALUE
        );

        public final ConfigOption<Boolean> systemEnabled = ConfigOption.define(
                "health.system.enabled",
                "Enable all health system related modifications",
                false
        );

        public final ConfigOption<Integer> maxHealth = ConfigOption.defineInRange(
                "health.system.maxHealth",
                """
                        Highest amount of health.
                        Please note that this value is limited to 1024 in vanilla, but a higher limit is given in case of mods that modify this limitation.""",
                20, 1, Integer.MAX_VALUE
        );

        public final ConfigOption<Integer> minHealth = ConfigOption.defineInRange(
                "health.system.minHealth",
                """
                        Lowest amount of health.
                        If set to 0, only maxHealth is used and any other option inside the category is ignored as they require this to be functional.""",
                0, 0, Integer.MAX_VALUE
        );

        public final ConfigOption<Integer> decreasedOnDeath = ConfigOption.defineInRange(
                "health.system.deathDecreasedHealth",
                """
                        Amount of health lost on each death.
                        Requires minHealth to be higher than 0.""",
                0, 0, Integer.MAX_VALUE
        );

        public final ConfigOption<Integer> startingHealth = ConfigOption.defineInRange(
                "health.system.startingHealth",
                """
                        Amount of health that a player starts with.
                        Requires minHealth to be higher than 0.""",
                20, 1, Integer.MAX_VALUE
        );

        public final ConfigOption<List<OnChangeReset>> onChangeReset = ConfigOption.defineRestrictedList(
                "health.system.additional.configChangeReset",
                "Config options which when changed should be considered for max health reset in an already created world",
                Arrays.stream(OnChangeReset.values()).toList(), Arrays.stream(OnChangeReset.values()).toList(),
                o -> o instanceof OnChangeReset
        );

        public final ConfigOption<Boolean> healthChangeMessage = ConfigOption.define(
                "health.system.additional.healthChangeMessage",
                "Show a message when a threshold is reached and when health is gained or lost.",
                true
        );

        public final ConfigOption<Boolean> hardcoreMode = ConfigOption.define(
                "health.system.additional.hardcoreMode",
                """
                        Enabled hardcore mode which makes the player a spectator when 0 maximal health is reached.
                        Setting minHealth to 0 and removing all healthThresholds is required or unexpected behaviour might occur.""",
                false
        );

        public final ConfigOption<NavigableSet<Integer>> thresholds = ConfigOption.defineList(
                "health.system.additional.healthThresholds",
                """
                        Values which, when reached, move the lowest health of the player to the achieved value. Requires the use of deathDecreasedHealth.
                        Example: If a player starts at 10 health and reaches a threshold of 16 using regenerative items or similar, the lowest max health will move to 16 health.
                        Lowest threshold value can be non-removable, meaning that max health will not decrease until a player reaches health that is over the lowest threshold.
                        To use it make the lowest value negative.""",
                ImmutableSortedSet.of(-8, 16), o -> o instanceof Integer
        );

        public final ConfigOption<RegenerativeItemsConsumptionMode> regenerativeItemsConsumptionMode = ConfigOption.defineEnum(
                "health.system.additional.regenerativeItemsConsumptionMode",
                """
                        Condition for consumption of regenerative items.
                        These values only apply on items without any use animations as to not consume them unintentionally.
                        Acceptable values: [ NOT_CROUCHING, CROUCHING, BOTH ]""",
                RegenerativeItemsConsumptionMode.NOT_CROUCHING, EnumGetMethod.NAME_IGNORECASE
        );

        public final ConfigOption<List<String>> regenerativeItems = ConfigOption.defineList(
                "health.system.additional.regenerativeItems",
                """
                        Items that increase/decrease current maximal health when used.
                        Format: "modid:item = N" with N being the health amount.
                        Appending a colon ':' after a negative N will make an item bypass healthThresholds, meaning that maximal health can go below a threshold until minHealth is reached""",
                Lists.newArrayList("minecraft:nether_star = 1", "minecraft:enchanted_golden_apple = 1", "minecraft:dragon_egg = 1"),
                o -> o instanceof String s && CStatTinkerer.REGENERATIVE_ITEM_REGEX.matcher(s.toLowerCase(Locale.ROOT)).matches()
        );

    }

    public enum OnChangeReset {
        MIN_HEALTH, MAX_HEALTH, STARTING_HEALTH
    }

    public enum RegenerativeItemsConsumptionMode {
        NOT_CROUCHING, CROUCHING, BOTH;
    }
}
