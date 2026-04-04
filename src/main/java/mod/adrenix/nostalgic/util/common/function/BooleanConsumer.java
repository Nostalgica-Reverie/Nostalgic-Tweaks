package mod.adrenix.nostalgic.util.common.function;

/**
 * Represents an operation that accepts a single {@code boolean} argument and returns no result. This is a functional
 * interface whose functional method is {@link #accept(boolean)}.
 */
@FunctionalInterface
public interface BooleanConsumer {
    /**
     * Performs this operation on the given argument.
     *
     * @param bool The {@code boolean} argument.
     */
    void accept(boolean bool);

    /**
     * Chain a boolean consumer.
     *
     * @param after The {@link BooleanConsumer} to call after this instance is called.
     * @return A new {@link BooleanConsumer} instance.
     */
    default BooleanConsumer andThen(BooleanConsumer after) {
        return (bool) -> {
            this.accept(bool);
            after.accept(bool);
        };
    }
}
