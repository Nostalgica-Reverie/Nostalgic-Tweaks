package mod.adrenix.nostalgic.util.common.array;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

public class CycleIndex
{
    /* Fields */

    private final int minIndex;
    private final int maxIndex;
    private int startIndex;
    private int lastIndex;
    private int currentIndex;
    private boolean movingForward;
    private boolean resetAtEnd;

    /* Constructor */

    /**
     * Create a new {@link CycleIndex} instance.
     *
     * @param minIndex   The minimum index.
     * @param maxIndex   The maximum index.
     * @param startIndex The starting index (will be clamped).
     * @param resetAtEnd Reset the cycle back to the minimum index when finished.
     */
    public CycleIndex(int minIndex, int maxIndex, int startIndex, boolean resetAtEnd)
    {
        this.minIndex = minIndex;
        this.maxIndex = maxIndex;
        this.startIndex = Mth.clamp(startIndex, minIndex, maxIndex);
        this.currentIndex = this.startIndex;
        this.lastIndex = this.currentIndex;
        this.resetAtEnd = resetAtEnd;
        this.movingForward = true;
    }

    /**
     * Create a new {@link CycleIndex} instance using an array where the {@code minIndex} is zero and the
     * {@code maxIndex} is the length of the array minus one.
     *
     * @param array      An array.
     * @param resetAtEnd Reset the cycle back to the minimum index when finished.
     * @param <T>        The class type of the values in the array.
     */
    public <T> CycleIndex(T[] array, boolean resetAtEnd)
    {
        this(0, array.length - 1, 0, resetAtEnd);
    }

    /* Methods */

    /**
     * Move the current index forwards or backwards.
     */
    @PublicAPI
    public void cycle()
    {
        this.lastIndex = this.currentIndex;

        int nextIndex = this.movingForward ? this.currentIndex + 1 : this.currentIndex - 1;

        if (nextIndex > this.maxIndex)
        {
            this.movingForward = false;

            if (this.resetAtEnd)
                this.currentIndex = this.minIndex;
            else
                this.currentIndex--;

            return;
        }

        if (nextIndex < this.minIndex)
        {
            this.movingForward = true;
            this.currentIndex++;

            return;
        }

        this.currentIndex = nextIndex;
    }

    /**
     * Get the current index.
     */
    @PublicAPI
    public int get()
    {
        return this.currentIndex;
    }

    /**
     * Get the last index that was cycled.
     */
    @PublicAPI
    public int getLast()
    {
        return this.lastIndex;
    }

    /**
     * Move the current index forwards or backwards and then get the resulting index.
     */
    @PublicAPI
    public int cycleAndGet()
    {
        this.cycle();
        return this.get();
    }

    /**
     * Get the current index and then move forwards or backwards.
     */
    @PublicAPI
    public int getAndCycle()
    {
        int index = this.get();
        this.cycle();

        return index;
    }

    /**
     * Move the index forward by one. If the next index is greater than the maximum index, then the index is reset back
     * to the minimum index.
     */
    @PublicAPI
    public void forward()
    {
        this.lastIndex = this.currentIndex;

        int nextIndex = this.currentIndex + 1;

        if (nextIndex > this.maxIndex)
        {
            nextIndex = this.minIndex;
            this.movingForward = false;
        }

        this.currentIndex = nextIndex;
    }

    /**
     * Move the index backward by one. If the next index is less than the minimum index, then the index is reset back to
     * the maximum index.
     */
    @PublicAPI
    public void backward()
    {
        this.lastIndex = this.currentIndex;

        int nextIndex = this.currentIndex - 1;

        if (nextIndex < this.minIndex)
        {
            nextIndex = this.maxIndex;
            this.movingForward = true;
        }

        this.currentIndex = nextIndex;
    }

    /**
     * Set whether the cycle restarts when it is finished.
     *
     * @param state The new flag state.
     */
    @PublicAPI
    public void setResetAtEnd(boolean state)
    {
        this.resetAtEnd = state;
    }

    /**
     * Change the starting index for the cycle. The index will be clamped between the minimum and maximum index.
     *
     * @param index The new starting index.
     */
    @PublicAPI
    public void setStartIndex(int index)
    {
        this.startIndex = Mth.clamp(index, this.minIndex, this.maxIndex);
    }

    /**
     * Reset the cycle back to the original starting index.
     */
    @PublicAPI
    public void restart()
    {
        this.currentIndex = this.startIndex;
        this.lastIndex = this.currentIndex;
    }
}
