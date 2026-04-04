package mod.adrenix.nostalgic.util.common.function;

import java.util.function.Supplier;

/**
 * Represents a supplier of {@code boolean} valued results. This is the {@code boolean} producing primitive
 * specialization of {@link Supplier}. There is no requirement that a distinct result be returned each time the supplier
 * is invoked. This is a {@code functional interface} whose functional method is {@link #getAsBoolean()}.
 *
 * @see Supplier
 */
@FunctionalInterface
public interface BooleanSupplier {
    /**
     * A {@link BooleanSupplier} that always returns {@code true}.
     */
    BooleanSupplier ALWAYS = () -> true;

    /* Static */
    /**
     * A {@link BooleanSupplier} that always returns {@code false}.
     */
    BooleanSupplier NEVER = () -> false;

    /**
     * Gets a result.
     *
     * @return A {@code boolean} result.
     */
    boolean getAsBoolean();
}
