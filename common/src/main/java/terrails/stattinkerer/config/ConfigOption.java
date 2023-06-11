package terrails.stattinkerer.config;

import com.electronwill.nightconfig.core.EnumGetMethod;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static terrails.stattinkerer.CStatTinkerer.LOGGER;

public class ConfigOption<T> {

    // Common night-config data
    private final String path;
    private final String comment;
    private final Supplier<T> defaultValue;
    private final Predicate<Object> optionValidator;

    // Mod-Loader dependant values
    private Supplier<T> valueSupplier;
    private Consumer<T> valueSetter;

    ConfigOption(String path, String comment, Supplier<T> defaultValue, Predicate<Object> optionValidator) {
        this.path = path;
        this.comment = comment;
        this.defaultValue = defaultValue;
        this.optionValidator = optionValidator;
    }

    public void initialize(Supplier<T> valueSupplier, Consumer<T> valueSetter) {
        if (this.isInitialized()) {
            LOGGER.error("ConfigOption already initialized...");
            return;
        }

        this.valueSupplier = valueSupplier;
        this.valueSetter = valueSetter;
    }

    private boolean isInitialized() {
        return this.valueSupplier != null && this.valueSetter != null;
    }

    public String getPath() {
        return this.path;
    }

    public String getComment() {
        return this.comment;
    }

    public Predicate<Object> getOptionValidator() {
        return this.optionValidator;
    }

    public T getDefault() {
        return this.defaultValue.get();
    }

    public T get() {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized. Returning default value...", this.path);
            return this.defaultValue.get();
        }
        T value = this.valueSupplier.get();
        // For some odd reason the value will be null if its fetched when config is being saved after using the config screen
        if (value == null) {
            return this.defaultValue.get();
        } else return value;
    }

    public void set(T value) {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized. Doing nothing...", this.path);
            return;
        }
        this.valueSetter.accept(value);
    }


    // STATIC METHODS \\

    public static <T> ConfigOption<T> define(String path, String comment, T defaultValue, Predicate<Object> validator) {
        return new ConfigOption<>(path, comment, () -> defaultValue, validator);
    }

    public static <T> ConfigOption<T> define(String path, String comment, T defaultValue) {
        return define(path, comment, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }

    public static <T> ConfigOption<T> defineInList(String path, String comment, T defaultValue, Collection<? extends T> acceptableValues) {
        return define(path, comment, defaultValue, acceptableValues::contains);
    }

    public static <T extends Comparable<? super T>> ConfigOption<T> defineInRange(String path, String comment, T defaultValue, T min, T max) {
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("The minimum must be less than the maximum");
        }
        return define(path, comment, defaultValue, o -> {
            if (!(o instanceof Comparable)) {
                return false;
            }
            try {
                Comparable<T> c = (Comparable<T>) o;
                return c.compareTo(min) >= 0 && c.compareTo(max) <= 0;
            } catch (ClassCastException e) {
                return false;
            }
        });
    }

    public static <T, C extends Collection<T>> ConfigOption<C> defineList(String path, String comment, C defaultValue, Predicate<Object> validator) {
        return define(path, comment, defaultValue, o -> {
            if (!(o instanceof List)) {
                return false;
            }
            List<?> list = (List<?>) o;
            for (Object element : list) {
                if (!validator.test(element)) {
                    return false;
                }
            }
            return true;
        });
    }

    public static <T> ConfigOption<List<T>> defineRestrictedList(String path, String comment, List<T> defaultValue, Collection<T> acceptableValues, Predicate<Object> validator) {
        return define(path, comment, defaultValue, o -> {
            if (!(o instanceof List)) {
                return false;
            }
            List<?> list = (List<?>) o;
            for (Object element : list) {
                if (!validator.test(element) && acceptableValues.contains(element)) {
                    return false;
                }
            }
            return true;
        });
    }


    public static <T extends Enum<T>> ConfigOption<T> defineEnum(String path, String comment, T defaultValue, EnumGetMethod method) {
        return define(path, comment, defaultValue, o -> o != null && method.validate(o, defaultValue.getDeclaringClass()));
    }

    public static <T extends Enum<T>> ConfigOption<T> defineRestrictedEnum(String path, String comment, T defaultValue, Collection<T> acceptableValues, EnumGetMethod method) {
        return define(path, comment, defaultValue, o -> o != null && method.validate(o, defaultValue.getDeclaringClass()) && acceptableValues.contains(method.get(o, defaultValue.getDeclaringClass())));
    }
}
