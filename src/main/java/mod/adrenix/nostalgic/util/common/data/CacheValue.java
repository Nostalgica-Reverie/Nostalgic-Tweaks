package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A cache value is useful in situations where there is a need to hold a value that may change at a later point in time.
 * This utility will cache a value using the given supplier. A check can be performed in this utility to see if the
 * cache is invalid, if so, then a new cache value will be created using the given supplier.
 *
 * @param <T> The class type of the value held.
 */
public class CacheValue<T> {
    /* Fields */

    private final Supplier<T> supplier;
    private boolean expired;
    private T cache;

    /* Constructor */

    /**
     * Reference {@code see also}.
     *
     * @param supplier A {@link Supplier} that provides a value to cache.
     * @see CacheValue#create(Supplier)
     */
    private CacheValue(Supplier<T> supplier) {
        this.supplier = supplier;
        this.cache = supplier.get();
    }

    /* Static */

    /**
     * Create a new {@link CacheValue} instance using the given supplier.
     *
     * @param supplier A {@link Supplier} that provides a value to cache.
     * @param <V>      The class type of the value.
     * @return A new {@link CacheValue} instance.
     */
    @PublicAPI
    public static <V> CacheValue<V> create(Supplier<V> supplier) {
        return new CacheValue<>(supplier);
    }

    /**
     * Create a new {@link CacheValue} instance from a nullable value.
     *
     * @param nullable A nullable value to check.
     * @param function A {@link Function} that accepts the non-null nullable and provides a value to cache.
     * @param orElse   If the nullable is null, then cache this value.
     * @param <T>      The class type of the nullable.
     * @param <V>      The class type of the value.
     * @return A new {@link CacheValue} instance.
     */
    @PublicAPI
    public static <T, V> CacheValue<V> nullable(@Nullable T nullable, Function<T, V> function, V orElse) {
        if (nullable == null)
            return new CacheValue<>(() -> orElse);

        return new CacheValue<>(() -> function.apply(nullable));
    }

    /**
     * Create a new {@link CacheValue} instance using a {@link NullableHolder}.
     *
     * @param holder   A {@link NullableHolder} to get a value to apply to the given function.
     * @param function A {@link Function} that accepts the non-null value and provides a value to cache.
     * @param orElse   If the holder has a null value, then this value will be cached.
     * @param <T>      The class type stored in the holder.
     * @param <V>      The class type of the cached value.
     * @return A new {@link CacheValue} instance.
     */
    @PublicAPI
    public static <T, V> CacheValue<V> nullable(NullableHolder<T> holder, Function<T, V> function, V orElse) {
        return new CacheValue<>(() -> {
            if (holder.isPresent())
                return function.apply(holder.get());

            return orElse;
        });
    }

    /**
     * Check if any of the given caches have expired. If optimization is desired, then order the given varargs with
     * caches that are likely to expire first.
     *
     * @param caches A varargs of {@link CacheValue}.
     * @return Whether any of the caches have changed.
     */
    @PublicAPI
    public static boolean isAnyExpired(CacheValue<?>... caches) {
        for (CacheValue<?> cache : caches) {
            if (cache.isExpired())
                return true;
        }

        return false;
    }

    /* Methods */

    /**
     * @return The result return from the supplier.
     */
    public T next() {
        return this.supplier.get();
    }

    /**
     * @return The value last cached.
     */
    public T last() {
        return this.cache;
    }

    /**
     * Update the value for this cache using the result returned by the supplier.
     */
    public void update() {
        this.cache = this.supplier.get();
        this.expired = false;
    }

    /**
     * Update the value for this cache and return the result.
     *
     * @return The updated cache value.
     */
    public T getAndUpdate() {
        this.update();

        return this.cache;
    }

    /**
     * @return Whether the current cached value does not match the result provided by the supplier.
     */
    public boolean isExpired() {
        if (!this.expired)
            this.expired = !this.cache.equals(this.supplier.get());

        return this.expired;
    }
}
