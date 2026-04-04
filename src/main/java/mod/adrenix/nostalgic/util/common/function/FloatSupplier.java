package mod.adrenix.nostalgic.util.common.function;

import java.util.function.Supplier;

/**
 * Represents a supplier of {@code float} valued results. This is the {@code float} producing primitive specialization
 * of {@link Supplier}. There is no requirement that a distinct result be returned each time the supplier is invoked.
 * This is a {@code functional interface} whose functional method is {@link #getAsFloat()}.
 *
 * @see Supplier
 */
@FunctionalInterface
public interface FloatSupplier {
    /**
     * Gets a result.
     *
     * @return A {@code float} result.
     */
    float getAsFloat();
}