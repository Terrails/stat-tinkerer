package terrails.stattinkerer.forge.feature;

/*
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import terrails.stattinkerer.feature.event.PlayerStateEvents;
import toughasnails.api.temperature.ITemperature;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
*/

public class TANFeature {} /*implements PlayerStateEvents.Clone {

    public static TANFeature INSTANCE;

    private static final Map<String, TemperatureLevel> TEMPERATURE_LEVEL_NAME_MAP = Arrays.stream(TemperatureLevel.values()).collect(Collectors.toMap(Enum::name, l -> l));

    public ForgeConfigSpec.ConfigValue<Boolean> keepThirst;
    public ForgeConfigSpec.ConfigValue<Integer> lowestThirst;

    public ForgeConfigSpec.ConfigValue<Boolean> keepHydration;
    public ForgeConfigSpec.ConfigValue<Integer> lowestHydration;
    public ForgeConfigSpec.ConfigValue<Boolean> keepHydrationRestricted;

    public ForgeConfigSpec.ConfigValue<Boolean> keepTemperature;
    public ForgeConfigSpec.ConfigValue<List<? extends String>> keepTempBetweenLevels;

    public static void initialize(ForgeConfigSpec.Builder builder) {
        INSTANCE = new TANFeature();

        builder.push("toughasnails");

        builder.push("thirst");

        INSTANCE.keepThirst = builder
                .comment("Make the player keep thirst when respawning")
                .define("keepThirst", false);

        INSTANCE.lowestThirst = builder
                .comment("The lowest thirst value the player can have when respawning, must be used with keepThirst")
                .defineInRange("lowestThirst", 6, 0, 20);

        builder.pop();
        builder.push("hydration");

        INSTANCE.keepHydration = builder
                .comment("Make the player keep hydration when respawning")
                .define("keepHydration", false);

        INSTANCE.lowestHydration = builder
                .comment("The lowest hydration value the player can have when respawning, must be used with keepHydration")
                .defineInRange("lowestHydration", 6, 0, 20);

        INSTANCE.keepHydrationRestricted = builder
                .comment("Make the player keep hydration when respawning only when thirst is full. Only usable with the other two options")
                .define("whenThirstFull", true);

        builder.pop();
        builder.push("temperature");

        INSTANCE.keepTemperature = builder
                .comment("Make the player keep their temperature when respawning")
                .define("keepTemperature", false);

        INSTANCE.keepTempBetweenLevels = builder
                .comment("Keep temperature between two levels\nAcceptable values: [ICY, COLD, NEUTRAL, WARM, HOT]")
                .define("clampTemperatureLevels", List.of("COLD", "WARM"), o -> {
                    if (o instanceof List<?> list && list.size() == 2 && list.get(0) instanceof String first && list.get(1) instanceof String second) {
                        first = first.toUpperCase(Locale.ROOT);
                        second = second.toUpperCase(Locale.ROOT);

                        return TEMPERATURE_LEVEL_NAME_MAP.containsKey(first) && TEMPERATURE_LEVEL_NAME_MAP.containsKey(second);
                    }
                    return false;
                });

        builder.pop(2);
    }

    @Override
    public void onPlayerClone(boolean isEnd, ServerPlayer newPlayer, ServerPlayer oldPlayer) {
        if (!isEnd) {

            if (ThirstHelper.isThirstEnabled()) {

                if (this.keepThirst.get()) {
                    int value = Math.max(this.lowestThirst.get(), ThirstHelper.getThirst(oldPlayer).getThirst());
                    ThirstHelper.getThirst(newPlayer).setThirst(value);
                }

                if (this.keepHydration.get() && (!this.keepHydrationRestricted.get() || !ThirstHelper.getThirst(oldPlayer).isThirsty())) {
                    float value = Math.max(this.lowestHydration.get(), ThirstHelper.getThirst(oldPlayer).getHydration());
                    ThirstHelper.getThirst(newPlayer).setHydration(value);
                }
            }

            if (TemperatureHelper.isTemperatureEnabled() && this.keepTemperature.get()) {
                ITemperature oldData = TemperatureHelper.getTemperatureData(oldPlayer);
                ITemperature newData = TemperatureHelper.getTemperatureData(newPlayer);

                final List<TemperatureLevel> temperatureLevels = this.keepTempBetweenLevels.get().stream().map(TEMPERATURE_LEVEL_NAME_MAP::get).toList();
                int ordinal = Mth.clamp(oldData.getLevel().ordinal(), temperatureLevels.get(0).ordinal(), temperatureLevels.get(1).ordinal());
                newData.setLevel(TemperatureLevel.values()[ordinal]);
            }
        }
    }
}
*/