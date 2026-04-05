package mod.adrenix.nostalgic.event;

/**
 * An object that stores the result of an event, along with its result value.
 *
 * @param cancelled Whether should the event be cancelled.
 * @param value     The result value of the event.
 * @param <T>       The type of the result value.
 */
@SuppressWarnings("unchecked")
public record EventResult<T>(boolean cancelled, T value) {
    private static final EventResult<?> PASS = new EventResult<>(false, null);
    private static final EventResult<?> STOP = new EventResult<>(true, null);

    /**
     * @param <T> Type of value
     * @return A generic, no value passing event
     */
    public static <T> EventResult<T> pass() {
        return (EventResult<T>) PASS;
    }

    /**
     * Makes a passing result object with a value.
     *
     * @param value The value of the result
     * @param <T>   Type of value
     * @return A passing result object with the given value.
     */
    public static <T> EventResult<T> pass(T value) {
        return new EventResult<>(false, value);
    }

    /**
     * @param <T> Type of value
     * @return A generic, no value stopping event
     */
    public static <T> EventResult<T> stop() {
        return (EventResult<T>) STOP;
    }

    /**
     * Makes a stopping result object with a value.
     *
     * @param value The value of the result
     * @param <T>   Type of value
     * @return A stopping result object with the given value.
     */
    public static <T> EventResult<T> stop(T value) {
        return new EventResult<>(true, value);
    }
}
