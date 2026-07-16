package terrails.stattinkerer.neoforge.compat;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;

import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ToughAsNailsFeature {

    private static final Map<String, TemperatureLevel> TEMPERATURE_LEVEL_NAME_MAP = Arrays
            .stream(TemperatureLevel.values())
            .collect(Collectors.toMap(Enum::name, l -> l));

    public ConfigValue<Boolean> keepThirst;
    public ConfigValue<Integer> lowestThirst;

    public ConfigValue<Boolean> keepHydration;
    public ConfigValue<Integer> lowestHydration;
    public ConfigValue<Boolean> keepHydrationRestricted;

    public ConfigValue<Boolean> keepTemperature;
    public ConfigValue<List<? extends String>> keepTempBetweenLevels;

    public ToughAsNailsFeature(ModConfigSpec.Builder builder) {
        NeoForge.EVENT_BUS.addListener(this::onPlayerClone);

        builder.push("toughasnails");

        builder.push("thirst");

        this.keepThirst = builder
                .comment("Make the player keep thirst when respawning")
                .define("keepThirst", false);

        this.lowestThirst = builder
                .comment("The lowest thirst value the player can have when respawning, must be used with keepThirst")
                .defineInRange("lowestThirst", 6, 0, 20);

        builder.pop();
        builder.push("hydration");

        this.keepHydration = builder
                .comment("Make the player keep hydration when respawning")
                .define("keepHydration", false);

        this.lowestHydration = builder
                .comment("The lowest hydration value the player can have when respawning, must be used with keepHydration")
                .defineInRange("lowestHydration", 6, 0, 20);

        this.keepHydrationRestricted = builder
                .comment("Make the player keep hydration when respawning only when thirst is full. Only usable with the other two options")
                .define("whenThirstFull", true);

        builder.pop();
        builder.push("temperature");

        this.keepTemperature = builder
                .comment("Make the player keep their temperature when respawning")
                .define("keepTemperature", false);

        this.keepTempBetweenLevels = builder
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

    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        var oldPlayer = event.getOriginal();
        var newPlayer = event.getEntity();

        if (ThirstHelper.isThirstEnabled()) {
            if (this.keepThirst.get()) {
                var value = Math.max(this.lowestThirst.get(), ThirstHelper.getThirst(oldPlayer).getThirst());
                ThirstHelper.getThirst(newPlayer).setThirst(value);
            }

            if (this.keepHydration.get() && (!this.keepHydrationRestricted.get() || !ThirstHelper.getThirst(oldPlayer).isThirsty())) {
                var value = Math.max(this.lowestHydration.get(), ThirstHelper.getThirst(oldPlayer).getHydration());
                ThirstHelper.getThirst(newPlayer).setHydration(value);
            }
        }

        if (TemperatureHelper.isTemperatureEnabled() && this.keepTemperature.get()) {
            var oldData = TemperatureHelper.getTemperatureData(oldPlayer);
            var newData = TemperatureHelper.getTemperatureData(newPlayer);

            var temperatureLevels = this.keepTempBetweenLevels.get().stream().map(TEMPERATURE_LEVEL_NAME_MAP::get).toList();
            var ordinal = Mth.clamp(oldData.getLevel().ordinal(), temperatureLevels.get(0).ordinal(), temperatureLevels.get(1).ordinal());
            newData.setLevel(TemperatureLevel.values()[ordinal]);
        }
    }
}
