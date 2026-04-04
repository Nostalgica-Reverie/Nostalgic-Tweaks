package mod.adrenix.nostalgic.util.common.function;

import java.util.function.Function;

/**
 * Represents a function that produces a {@code float} valued result.  This is the {@code float} producing primitive
 * specialization for {@link Function}.
 *
 * <p>
 * This is a functional interface whose functional method is {@link #applyAsFloat(Object)}.
 *
 * @param <T> The type of the input to the function.
 * @see Function
 */
@FunctionalInterface
public interface ToFloatFunction<T> {
    /**
     * Applies this function to the given argument.
     *
     * @param value The function argument.
     * @return The function result.
     */
    float applyAsFloat(T value);
}
