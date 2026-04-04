package mod.adrenix.nostalgic.util.common.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class NullableResult {
    /**
     * Get a {@code nullable} result using the given {@code nullable} value and mapper function.
     *
     * @param value The {@code nullable} to apply to the map function if the value is not {@code null}.
     * @param map   The {@link Function} to get a {@code nullable} result from.
     * @param <T>   The class type of the given value.
     * @param <R>   The class type of the map result.
     * @return A {@code nullable} {@link R} value.
     */
    @Nullable
    public static <T, R> R get(@Nullable T value, Function<? super T, ? extends R> map) {
        if (value == null)
            return null;

        return map.apply(value);
    }

    /**
     * Get a guaranteed result using the given {@code nullable} value, the or-else value, and the mapper function.
     *
     * @param value  The {@code nullable} to apply to the map function if the value is not {@code null}.
     * @param orElse The value to return if the result is {@code null}.
     * @param map    The {@link Function} to get a {@code nullable} result from.
     * @param <T>    The class type of the given value.
     * @param <R>    The class type of the map result.
     * @return A {@code not-null} {@link R} value.
     */
    @NotNull
    public static <T, R> R getOrElse(@Nullable T value, R orElse, Function<? super T, ? extends R> map) {
        R result = get(value, map);

        return result == null ? orElse : result;
    }
}
