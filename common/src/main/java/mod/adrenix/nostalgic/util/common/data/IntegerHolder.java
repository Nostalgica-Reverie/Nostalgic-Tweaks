package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

public class IntegerHolder extends Holder<Integer>
{
    /* Static */

    /**
     * Create a new {@link IntegerHolder} instance.
     *
     * @param startAt The integer to start this holder at.
     * @return A new {@link IntegerHolder} instance.
     */
    public static IntegerHolder create(int startAt)
    {
        return new IntegerHolder(startAt);
    }

    /* Constructor */

    private IntegerHolder(int startAt)
    {
        super(startAt);
    }

    /* Methods */

    /**
     * Increment the held value by the given value.
     *
     * @param by The amount to increment by.
     */
    @PublicAPI
    public void increment(int by)
    {
        this.set(this.value + by);
    }

    /**
     * Increment the held value by one.
     */
    @PublicAPI
    public void increment()
    {
        this.increment(1);
    }

    /**
     * Get the current value and then increment the held value by the given value.
     *
     * @param by The amount to increment by.
     * @return The held value before it is incremented.
     */
    @PublicAPI
    public int getAndIncrement(int by)
    {
        int previousValue = this.value;

        this.increment(by);

        return previousValue;
    }

    /**
     * Get the current value and then increment the held value by one.
     *
     * @return The held value before it is incremented.
     */
    @PublicAPI
    public int getAndIncrement()
    {
        return getAndIncrement(1);
    }

    /**
     * Decrement the held value by the given value.
     *
     * @param by The amount to decrement by.
     */
    @PublicAPI
    public void decrement(int by)
    {
        this.set(this.value - by);
    }

    /**
     * Decrement the held value by one.
     */
    @PublicAPI
    public void decrement()
    {
        this.decrement(1);
    }

    /**
     * Get the current value and then decrement the held value by the given value.
     *
     * @return The held value before it is decremented.
     */
    @PublicAPI
    public int getAndDecrement(int by)
    {
        int previousValue = this.value;

        this.decrement(by);

        return previousValue;
    }

    /**
     * Get the current value and then decrement the held value by one.
     *
     * @return The held value before it is decremented.
     */
    @PublicAPI
    public int getAndDecrement()
    {
        return getAndDecrement(1);
    }
}
