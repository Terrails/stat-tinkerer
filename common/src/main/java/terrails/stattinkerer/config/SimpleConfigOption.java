package terrails.stattinkerer.config;

import com.electronwill.nightconfig.core.EnumGetMethod;

import java.util.List;
import java.util.function.Predicate;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class SimpleConfigOption<T> extends ConfigOption<T, T> {

    SimpleConfigOption(String path, String comment, T defaultValue, Predicate<Object> validator) {
        super(path, comment, defaultValue, validator, null, null);
    }

    @Override
    public void reload() {
        this.cachedValue = this.getRaw();
    }

    @Override
    public T getDefault() {
        return this.getRawDefault();
    }

    @Override
    public T get() {
        return this.cachedValue;
    }

    @Override
    public void set(T value) {
        this.setRaw(value);
    }

    public static <T> SimpleConfigOption<T> define(String path, String comment, T defaultValue, Predicate<Object> validator) {
        return new SimpleConfigOption<>(path, comment, defaultValue, validator);
    }

    public static <T> SimpleConfigOption<T> define(String path, String comment, T defaultValue) {
        return new SimpleConfigOption<>(path, comment, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }

    public static SimpleConfigOption<Integer> defineInRange(String path, String comment, Integer defaultValue, Integer min, Integer max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("The minimum must be less than the maximum");
        }
        return new SimpleConfigOption<>(path, comment, defaultValue, o -> {
            if (o instanceof Integer i) {
                return i >= min && i <= max;
            } else {
                return false;
            }
        });
    }

    public static <T> SimpleConfigOption<List<T>> defineList(String path, String comment, List<T> defaultValues, Predicate<Object> validator) {
        return new SimpleConfigOption<>(path, comment, defaultValues, o -> {
            if (!(o instanceof List<?> list)) return false;
            for (Object e : list) {
                if (!validator.test(e)) {
                    LOGGER.warn("Invalid value {} in list entry {}", e, path);
                    return false;
                }
            }
            return true;
        });
    }

    public static <T extends Enum<T>> SimpleConfigOption<List<T>> defineEnumList(String path, String comment, Class<T> enumType, List<T> defaultValues, EnumGetMethod validator) {
        return defineList(path, comment, defaultValues, o -> o != null && validator.validate(o, enumType));
    }

    public static <T extends Enum<T>> SimpleConfigOption<T> defineEnum(String path, String comment, Class<T> enumType, T defaultValue, EnumGetMethod validator) {
        return new SimpleConfigOption<>(path, comment, defaultValue, o -> o != null && validator.validate(o, enumType));
    }
}
