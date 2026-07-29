package terrails.stattinkerer;

import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import terrails.stattinkerer.config.Configuration;

import java.util.regex.Pattern;

public class CStatTinkerer {

    public static final String MOD_ID = "stattinkerer";
    public static final String MOD_NAME = "Stat Tinkerer";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);
    public static PlatformFunctions PLATFORM;

    public static final Pattern REGENERATIVE_ITEM_REGEX = Pattern.compile("^([a-z0-9_.-]+:[a-z0-9_.-]+)\\s=\\s(-?\\d+):?$");
    public static final Object[] CONFIGURATION_INSTANCES = {Configuration.EXPERIENCE, Configuration.HUNGER, Configuration.HEALTH};

    public static void setup(PlatformFunctions platform) {
        PLATFORM = platform;
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
