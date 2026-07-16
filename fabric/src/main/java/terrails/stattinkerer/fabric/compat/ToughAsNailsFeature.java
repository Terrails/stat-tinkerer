package terrails.stattinkerer.fabric.compat;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import toughasnails.api.temperature.TemperatureHelper;
import toughasnails.api.temperature.TemperatureLevel;
import toughasnails.api.thirst.ThirstHelper;

import terrails.stattinkerer.config.ConfigOption;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ToughAsNailsFeature {

    private static final Map<String, TemperatureLevel> TEMPERATURE_LEVEL_NAME_MAP = Arrays.stream(TemperatureLevel.values()).collect(Collectors.toMap(Enum::name, l -> l));

    public final ConfigOption<Boolean> keepThirst;
    public final ConfigOption<Integer> lowestThirst;

    public final ConfigOption<Boolean> keepHydration;
    public final ConfigOption<Integer> lowestHydration;
    public final ConfigOption<Boolean> keepHydrationRestricted;

    public final ConfigOption<Boolean> keepTemperature;
    public final ConfigOption<List<String>> keepTempBetweenLevels;

    public ToughAsNailsFeature() {
        ServerPlayerEvents.COPY_FROM.register(this::onCopyFrom);

        this.keepThirst = ConfigOption.define(
                "toughasnails.thirst.keepThirst",
                "Make the player keep thirst when respawning",
                false
        );

        this.lowestThirst = ConfigOption.defineInRange(
                "toughasnails.thirst.lowestThirst",
                "The lowest thirst value the player can have when respawning, must be used with keepThirst",
                6, 0, 20
        );

        this.keepHydration = ConfigOption.define(
                "toughasnails.hydration.keepHydration",
                "Make the player keep hydration when respawning",
                false
        );

        this.lowestHydration = ConfigOption.defineInRange(
                "toughasnails.hydration.lowestHydration",
                "The lowest hydration value the player can have when respawning, must be used with keepHydration",
                6, 0, 20
        );

        this.keepHydrationRestricted = ConfigOption.define(
                "toughasnails.hydration.whenThirstFull",
                "Make the player keep hydration when respawning only when thirst is full. Only usable with the other two options",
                true
        );

        this.keepTemperature = ConfigOption.define(
                "toughasnails.temperature.keepTemperature",
                "Make the player keep their temperature when respawning",
                false
        );

        this.keepTempBetweenLevels = ConfigOption.define(
                "toughasnails.temperature.clampTemperatureLevels",
                "Keep temperature between two levels\nAcceptable values: [ICY, COLD, NEUTRAL, WARM, HOT]",
                List.of("COLD", "WARM"),
                o -> {
                    if (o instanceof List<?> list && list.size() == 2 && list.get(0) instanceof String first && list.get(1) instanceof String second) {
                        first = first.toUpperCase(Locale.ROOT);
                        second = second.toUpperCase(Locale.ROOT);

                        return TEMPERATURE_LEVEL_NAME_MAP.containsKey(first) && TEMPERATURE_LEVEL_NAME_MAP.containsKey(second);
                    }
                    return false;
                }
        );
    }

    public void onCopyFrom(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (!alive) return;

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
