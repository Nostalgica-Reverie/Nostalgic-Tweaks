package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RecursionAvoidance {
    /* Static */

    private boolean processing = false;

    /* Fields */

    private RecursionAvoidance() {
    }

    /* Constructor */

    /**
     * Create an avoidance mechanism that prevents recursion when performing {@link #process(Runnable)}.
     *
     * @return A new {@link RecursionAvoidance} instance.
     */
    public static RecursionAvoidance create() {
        return new RecursionAvoidance();
    }

    /* Methods */

    /**
     * Run the given runnable. The runnable will not be recursively called.
     *
     * @param runnable A {@link Runnable} instance.
     */
    @PublicAPI
    public void process(Runnable runnable) {
        if (this.processing)
            return;

        this.processing = true;
        runnable.run();
        this.processing = false;
    }

    /**
     * Get a result from a supplier. The supplier will not be recursively called.
     *
     * @param supplier A {@link Supplier}.
     * @param <T>      The class type of the result.
     * @return A {@code nullable} result from the given supplier.
     */
    @Nullable
    @PublicAPI
    public <T> T process(Supplier<T> supplier) {
        if (this.processing)
            return null;

        this.processing = true;
        T result = supplier.get();
        this.processing = false;

        return result;
    }

    /**
     * Get a result from a supplier and return a default value if this is called while still processing. The supplier
     * will not be recursively called.
     *
     * @param supplier       A {@link Supplier}.
     * @param whenProcessing The value to return if this is called while the processor is still processing.
     * @param <T>            The class type of the result.
     * @return A {@code non-null} result from the given supplier.
     */
    @PublicAPI
    public <T> T process(Supplier<T> supplier, T whenProcessing) {
        if (this.processing)
            return whenProcessing;

        this.processing = true;
        T result = supplier.get();
        this.processing = false;

        return result;
    }

    /**
     * @return Whether the avoidance mechanism is processing instructions.
     */
    @PublicAPI
    public boolean isProcessing() {
        return this.processing;
    }

    /**
     * @return Whether the avoidance mechanism is waiting for further instructions.
     */
    @PublicAPI
    public boolean isParked() {
        return !this.isProcessing();
    }
}
