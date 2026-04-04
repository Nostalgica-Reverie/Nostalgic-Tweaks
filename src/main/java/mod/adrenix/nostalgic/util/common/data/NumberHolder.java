package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

/**
 * Create a new {@link Holder} for a {@link Number}.
 *
 * @param <T> A {@link Number} type.
 */
public class NumberHolder<T extends Number> extends Holder<T> {
    /* Static */

    /**
     * Reference {@code see also}.
     *
     * @param number The number to create an object ({@link Holder}) reference for.
     * @see Holder#create(Object)
     */
    private NumberHolder(T number) {
        super(number);
    }

    /* Constructor */

    /**
     * @param startWith The number to start off with.
     * @param <T>       A {@link Number} instance.
     * @return A new {@link NumberHolder} instance with a starting number.
     */
    public static <T extends Number> NumberHolder<T> create(T startWith) {
        return new NumberHolder<>(startWith);
    }

    /* Methods */

    /**
     * Safely set the value of this hold using a class check before casting.
     *
     * @param value The {@link Number} to safely set.
     */
    @SuppressWarnings("unchecked") // Numbers are class checked before casting
    private void setSafely(Number value) {
        if (this.value instanceof Byte)
            this.set((T) (Object) value.byteValue());
        else if (this.value instanceof Short)
            this.set((T) (Object) value.shortValue());
        else if (this.value instanceof Integer)
            this.set((T) (Object) value.intValue());
        else if (this.value instanceof Long)
            this.set((T) (Object) value.longValue());
        else if (this.value instanceof Float)
            this.set((T) (Object) value.floatValue());
        else if (this.value instanceof Double)
            this.set((T) (Object) value.doubleValue());
    }

    /**
     * The given value is set if it is between the lower and the upper bound. If the value is less than the lower bound,
     * then the lower bound is used. If the value is greater than the upper bound, then the upper bound is used.
     *
     * @param number The value that is to be clamped.
     * @param min    The lower bound for the clamp.
     * @param max    The upper bound for the clamp.
     */
    @PublicAPI
    public void setAndClamp(T number, T min, T max) {
        this.setSafely(Mth.clamp(number.doubleValue(), min.doubleValue(), max.doubleValue()));
    }

    /**
     * Compare the current value using given expected value. If the values match, then the current value is set with
     * value to update to.
     *
     * @param expect The value to expect when comparing.
     * @param update The value to update to if the current value matches the expected value.
     * @return Whether the current value matched the expected value.
     */
    @PublicAPI
    public boolean compareAndSet(T expect, T update) {
        if (this.value.equals(expect)) {
            this.set(update);
            return true;
        }

        return false;
    }

    /**
     * Get the current value and then set the current value with the given number.
     *
     * @param number The new number value.
     * @return The current value before it is set with the given number.
     */
    @PublicAPI
    public T getAndSet(T number) {
        T currentValue = this.value;
        this.set(number);

        return currentValue;
    }

    /**
     * Set the current value with the given number and then get the new value.
     *
     * @param number The new number value.
     * @return The given new number value.
     */
    @PublicAPI
    public T setAndGet(T number) {
        this.set(number);

        return this.value;
    }

    /**
     * Get the current value and then add the current value with the given number.
     *
     * @param number The number to add.
     * @return The current value before it is added with the given number.
     */
    @PublicAPI
    public T getAndAdd(T number) {
        T currentValue = this.value;
        this.setSafely(this.value.doubleValue() + number.doubleValue());

        return currentValue;
    }

    /**
     * Add the given number to the current value and then get the result.
     *
     * @param number The number to add.
     * @return The result of the addition.
     */
    @PublicAPI
    public T addAndGet(T number) {
        this.setSafely(this.value.doubleValue() + number.doubleValue());

        return this.value;
    }

    /**
     * Get the current value and then subtract the current value with the given number.
     *
     * @param number The number to subtract.
     * @return The current value before it is subtracted with the given number.
     */
    @PublicAPI
    public T getAndSubtract(T number) {
        T currentValue = this.value;
        this.setSafely(this.value.doubleValue() - number.doubleValue());

        return currentValue;
    }

    /**
     * Add the given number to the current value and then get the result.
     *
     * @param number The number to subtract.
     * @return The result of the subtraction.
     */
    @PublicAPI
    public T subtractAndGet(T number) {
        this.setSafely(this.value.doubleValue() - number.doubleValue());

        return this.value;
    }

    /**
     * Get the current value and then multiply the current value with the given number.
     *
     * @param number The number to multiply.
     * @return The current value before it is multiplied with the given number.
     */
    @PublicAPI
    public T getAndMultiply(T number) {
        T currentValue = this.value;
        this.setSafely(this.value.doubleValue() * number.doubleValue());

        return currentValue;
    }

    /**
     * Add the given number to the current value and then get the result.
     *
     * @param number The number to multiply.
     * @return The result of the multiplication.
     */
    @PublicAPI
    public T multiplyAndGet(T number) {
        this.setSafely(this.value.doubleValue() * number.doubleValue());

        return this.value;
    }

    /**
     * Get the current value and then divide the current value with the given number. Division by 0 is not handled.
     *
     * @param number The number to divide.
     * @return The current value before it is divided with the given number.
     */
    @PublicAPI
    public T getAndDivide(T number) {
        T currentValue = this.value;
        this.setSafely(this.value.doubleValue() / number.doubleValue());

        return currentValue;
    }

    /**
     * Add the given number to the current value and then get the result. Division by 0 is not handled.
     *
     * @param number The number to divide.
     * @return The result of the division.
     */
    @PublicAPI
    public T divideAndGet(T number) {
        this.setSafely(this.value.doubleValue() / number.doubleValue());

        return this.value;
    }
}
