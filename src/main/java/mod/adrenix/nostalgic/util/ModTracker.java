package mod.adrenix.nostalgic.util;

import com.google.common.base.Suppliers;
import mod.adrenix.nostalgic.util.common.ClassUtil;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum ModTracker {
    DISTANT_HORIZONS("distanthorizons"),
    CONTROLIFY("controlify"),
    APPLE_SKIN("appleskin"),
    SODIUM("sodium", "embeddium"),
    EMBEDDIUM("embeddium"),
    STARLIGHT("starlight"),
    POLYTONE("polytone"),
    EXORDIUM("exordium"),
    OPTIFINE("optifine"),
    FLYWHEEL("flywheel"),
    IRIS("iris");

    /* Fields */

    /**
     * This field tracks whether Optifine is installed. This is a Forge mod, but has a Fabric relative known as
     * Opti-Fabric. A supplier is needed since a hacky class check is required to determine if Optifine is present. This
     * hacky check is needed because Optifine is a closed-source mod.
     */
    private static final Supplier<Boolean> OPTIFINE_SUPPLIER = Suppliers.memoize(ClassUtil::isOptifinePresent);
    private final String[] identifiers;

    /* Constructor */
    private boolean installed;

    /* Methods */

    ModTracker(String... identifiers) {
        this.identifiers = identifiers;
    }

    /**
     * Get a stream of all mod tracker values.
     *
     * @return A {@link Stream} of this enumeration's values.
     */
    public static Stream<ModTracker> stream() {
        return Arrays.stream(ModTracker.values());
    }

    /**
     * Initialize the mod tracker utility using the given mod loader function. Each enumeration will have its
     * {@code installed} flag updated based on the value returned by the given {@code loader} {@link Function}.
     *
     * @param loader A function that accepts a mod id and returns whether the mod id was found in the mod folder.
     */
    public static void init(Function<String, Boolean> loader) {
        ModTracker.stream().forEach(mod -> {
            for (String id : mod.identifiers) {
                if (mod.installed)
                    break;

                mod.installed = loader.apply(id);
            }
        });
    }

    /**
     * Checks if the mod is installed. This will not return a truthful value if the mod scanning process has not yet
     * occurred.
     * <p><br>
     * The Optifine mod needs a special way of checking if it is installed. This may not always return a correct value
     * if the class that is checked is changed by Optifine in the future.
     *
     * @return Whether the mod is installed.
     */
    public boolean isInstalled() {
        if (this.equals(ModTracker.OPTIFINE))
            return OPTIFINE_SUPPLIER.get();

        return this.installed;
    }

    /**
     * Functional shortcut check if the mod is currently not installed.
     *
     * @return Whether the mod is not installed.
     */
    public boolean isNotInstalled() {
        return !this.isInstalled();
    }
}
