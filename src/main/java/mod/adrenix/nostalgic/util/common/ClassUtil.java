package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Do <b color=red>not</b> load <i>any</i> Minecraft code into this class. This utility is used by mixin plugins. If a
 * vanilla Minecraft class is loaded, then mixins can't change the Minecraft class since it was loaded by this utility.
 */
public abstract class ClassUtil {
    /**
     * Functional alternative of {@code a instanceof A.class}.
     *
     * @param value     The value to check if it is an instance of the given class type.
     * @param classType The class type to compare.
     * @return Whether the given value is an instance of the given class type.
     */
    @PublicAPI
    public static boolean isInstanceOf(@Nullable Object value, Class<?> classType) {
        if (value == null)
            return false;

        return classType.isInstance(value);
    }

    /**
     * Alternative to using {@code !(a instanceof A.class)}. This utility method makes code easier to read.
     *
     * @param value     The value to check if it is <b>not</b> an instance of the given class type.
     * @param classType The class type to compare.
     * @return Whether the given value is <b>not</b> an instance of the given class type.
     */
    @PublicAPI
    public static boolean isNotInstanceOf(@Nullable Object value, Class<?> classType) {
        return !isInstanceOf(value, classType);
    }

    /**
     * Checks if a class path is loaded.
     *
     * @param path The full-path of the class.
     * @return Whether the class is loaded.
     */
    @PublicAPI
    public static boolean isClassPresent(String path) {
        try {
            Class.forName(path);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    /**
     * Attempt a class cast on the given object. If the given object is not an instance of the given class type, then an
     * empty optional is returned.
     *
     * @param object    An object to cast onto.
     * @param classType The class type to get the cast from.
     * @param <T>       The class.
     * @return An optional object with a class type cast wrapped around it.
     */
    @PublicAPI
    public static <T> Optional<T> cast(Object object, Class<T> classType) {
        return Optional.ofNullable(classType.isInstance(object) ? classType.cast(object) : null);
    }

    /**
     * Do <b>not</b> use this to check for optifine. Use the main mod class' Optifine memoized supplier.
     *
     * @return Whether optifine is installed.
     */
    public static boolean isOptifinePresent() {
        return isClassPresent("net.optifine.Config");
    }
}
