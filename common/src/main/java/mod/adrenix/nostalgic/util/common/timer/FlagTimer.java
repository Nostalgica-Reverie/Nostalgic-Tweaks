package mod.adrenix.nostalgic.util.common.timer;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.Util;

import java.util.concurrent.TimeUnit;

/**
 * Utility class that toggles a flag at specific time intervals. This interval can specify any time interval it wants
 * using Java's {@link TimeUnit} enumeration class.
 */
public class FlagTimer
{
    /**
     * Any interval timer that uses this value as its max repeat limit instructs this interval timer that the state will
     * always change regardless of the repeat counter.
     */
    private static final int ALWAYS_REPEAT = -1;

    /* Fields */

    private final boolean startFlag;
    private boolean flag;

    private final long timeInterval;
    private final TimeUnit timeUnit;

    private final int maxRepeat;
    private int repeated;
    private long timeSinceLast;

    /* Constructor */

    /**
     * Create a new {@link FlagTimer} instance. Time intervals are in milliseconds so 1000L equals 1 second.
     *
     * @param startFlag    The state of the flag to start the timer with.
     * @param timeInterval The amount of time that needs to elapse before the state is flipped.
     * @param timeUnit     The {@link TimeUnit} enumeration to use.
     * @param maxRepeat    The number of times to repeat until this timer is manually reset.
     */
    FlagTimer(boolean startFlag, long timeInterval, TimeUnit timeUnit, int maxRepeat)
    {
        this.flag = startFlag;
        this.startFlag = startFlag;
        this.maxRepeat = maxRepeat;
        this.timeSinceLast = 0L;
        this.timeInterval = timeInterval;
        this.timeUnit = timeUnit;
        this.repeated = 0;
    }

    /* Methods */

    /**
     * Manually change the current state.
     *
     * @param flag The new value of the time interval state.
     */
    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

    /**
     * Reset the time interval. This sets the number of times repeated back to zero and sets the current timer state
     * back to its starting state.
     */
    public void reset()
    {
        this.timeSinceLast = Util.getMillis();
        this.repeated = 0;
        this.flag = this.startFlag;
    }

    /**
     * @return Whether this timer has reached its maximum repeat limit.
     */
    public boolean hasReachedMax()
    {
        return this.maxRepeat != ALWAYS_REPEAT && this.repeated >= this.maxRepeat;
    }

    /**
     * @return The current state of the timer.
     */
    public boolean getFlag()
    {
        if (this.timeSinceLast == 0L)
            this.timeSinceLast = Util.getMillis();

        if (this.maxRepeat != ALWAYS_REPEAT && this.repeated >= this.maxRepeat)
            return this.flag;

        if (Util.getMillis() - this.timeSinceLast >= this.timeUnit.toMillis(this.timeInterval))
        {
            if (this.maxRepeat != ALWAYS_REPEAT)
                this.repeated++;

            this.timeSinceLast = Util.getMillis();

            this.flag = !this.flag;
        }

        return this.flag;
    }

    /* Builder */

    /**
     * Start the building process of a new time interval instance.
     *
     * @param delay The delay to use with the given time unit.
     * @param time  The time unit to convert the given delay to milliseconds.
     * @return A new builder instance.
     */
    public static Builder create(long delay, TimeUnit time)
    {
        return new Builder(delay, time);
    }

    public static class Builder
    {
        private final long timeInterval;
        private final TimeUnit timeUnit;

        private boolean flag = false;
        private int maxRepeat = ALWAYS_REPEAT;

        private Builder(long timeInterval, TimeUnit timeUnit)
        {
            this.timeInterval = timeInterval;
            this.timeUnit = timeUnit;
        }

        /**
         * Change the starting state of the timer. The default starting state is <code>false</code>.
         *
         * @param state The state to start the timer with.
         */
        @PublicAPI
        public Builder startWith(boolean state)
        {
            this.flag = state;
            return this;
        }

        /**
         * Change the maximum amount of times the timer can switch its state.
         *
         * @param maxRepeat The number of times the state can switch.
         */
        @PublicAPI
        public Builder maxRepeat(int maxRepeat)
        {
            this.maxRepeat = maxRepeat;
            return this;
        }

        /**
         * Only allow this time interval to flip its state once. Resetting the time interval will allow for another
         * repeat.
         */
        @PublicAPI
        public Builder once()
        {
            this.maxRepeat = 1;
            return this;
        }

        /**
         * Finish the time interval building process.
         *
         * @return A new time interval instance.
         */
        public FlagTimer build()
        {
            return new FlagTimer(this.flag, this.timeInterval, this.timeUnit, this.maxRepeat);
        }
    }
}
