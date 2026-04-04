package mod.adrenix.nostalgic.util.common.data;

/**
 * A holder is useful in situations where there is a need to avoid Java's pass-by-value for primitive data types. For
 * example, if a boolean will be passed to various methods, and is changed in one of those methods, then those changes
 * will not be seen by the original method that passed in the boolean. To create a new instance, you can use the default
 * constructor or use {@link Holder#create(Object)}.
 *
 * @param <T> The class type of the value held.
 * @see Holder#set(Object)
 * @see Holder#get()
 */
public class Holder<T> {
    /* Fields */

    protected T value;

    /* Constructor */

    /**
     * Reference {@code see also}.
     *
     * @param value The value to create an object ({@link Holder}) reference for.
     * @see Holder#create(Object)
     */
    public Holder(T value) {
        this.value = value;
    }

    /* Static */

    /**
     * Create a new {@link Holder} instance using the given value. Useful in situations where there is a need to bypass
     * Java's pass-by-value for primitive data types; for example, a {@code boolean} value. This utility provides
     * functional setters/getters for the provided value.
     *
     * @param <V>   The class type of the value.
     * @param value The value to create an object ({@link Holder}) reference for.
     * @return A new {@link Holder} instance.
     * @see Holder#set(Object)
     * @see Holder#get()
     */
    public static <V> Holder<V> create(V value) {
        return new Holder<>(value);
    }

    /* Methods */

    /**
     * Change the value that is being stored in this {@link Holder}.
     *
     * @param value A new value to store.
     * @see #get()
     */
    public void set(T value) {
        this.value = value;
    }

    /**
     * @return The current state of the value being held by this {@link Holder}.
     * @see #set(Object)
     */
    public T get() {
        return this.value;
    }
}
