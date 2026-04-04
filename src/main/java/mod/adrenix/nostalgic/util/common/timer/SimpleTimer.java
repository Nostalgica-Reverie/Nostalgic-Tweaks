package mod.adrenix.nostalgic.util.common.timer;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.concurrent.TimeUnit;

/**
 * Utility class that tracks how much time has elapsed. This timer tracks time in milliseconds, so a time interval of
 * 1000L is equivalent to 1 second.
 */
public class SimpleTimer {
    /**
     * Any timer that uses this value as its max repeat limit tells the watcher that this timer does not have a max
     * repeat limit. There is an alternative constructor that only accepts an interval, and that constructor will use
     * this value as the maximum repeat limit.
     */
    private static final int NO_REPEAT = -1;

    /* Fields */
    private final long timeInterval;
    private final int maxRepeat;
    private boolean debug;
    private boolean immediate;
    private boolean waitFirst;
    private long timeSinceLast;
    private int repeated;

    /* Constructor */

    /**
     * Create a new {@link SimpleTimer} instance. Time intervals are in milliseconds so 1000L equals 1 second.
     *
     * @param timeInterval The amount of time that needs to elapse before the timer is ready.
     * @param maxRepeat    The number of times to repeat until this timer is manually reset.
     */
    SimpleTimer(long timeInterval, int maxRepeat) {
        this.debug = false;
        this.waitFirst = false;
        this.timeSinceLast = Util.getMillis();
        this.timeInterval = timeInterval;
        this.maxRepeat = maxRepeat;
        this.repeated = 0;
    }

    /* Methods */

    /**
     * Start the building process of a new {@link SimpleTimer} instance.
     *
     * @param duration A duration that will be converted to milliseconds by the given time unit.
     * @param time     A {@link TimeUnit} enumeration.
     * @return A new {@link Builder} instance.
     */
    public static Builder create(long duration, TimeUnit time) {
        return new Builder(duration, time, NO_REPEAT);
    }

    /**
     * Start the building process of a new {@link SimpleTimer} instance.
     *
     * @param duration  A duration that will be converted to milliseconds by the given time unit.
     * @param time      A {@link TimeUnit} enumeration.
     * @param maxRepeat The maximum amount of times this timer is allowed to repeat.
     * @return A new {@link Builder} instance.
     */
    public static Builder create(long duration, TimeUnit time, int maxRepeat) {
        return new Builder(duration, time, maxRepeat);
    }

    /**
     * Set the {@code waitFirst} flag to {@code true}. This will cause the timer to start when {@link #hasElapsed()} is
     * invoked. By default, timers start as soon as their instance is built.
     */
    @PublicAPI
    public void waitFirst() {
        this.waitFirst = true;
    }

    /**
     * Reset the time since the timer was last ready.
     */
    @PublicAPI
    public void reset() {
        this.timeSinceLast = Util.getMillis();
        this.repeated = 0;
    }

    /**
     * Check if the repeat tracker has reached the maximum repeat count.
     *
     * @return Whether this timer has reached its maximum repeat allowance.
     */
    @PublicAPI
    public boolean isMaxReached() {
        return this.repeated == this.maxRepeat;
    }

    /* Builder */

    /**
     * If the timer is waiting to be initialized, then a value of {@code 1.0D} is returned.
     *
     * @return A normalized progress [0.0D-1.0D] amount until the next time interval is reached.
     */
    @PublicAPI
    public double getProgress() {
        if (this.waitFirst)
            return 1.0D;

        return Mth.clamp((double) (Util.getMillis() - this.timeSinceLast) / this.timeInterval, 0.0D, 1.0D);
    }

    /**
     * Checks if enough time has elapsed, and if so, resets the timer.
     *
     * @return Whether the time since last checked has exceeded this timer's interval.
     */
    @PublicAPI
    public boolean hasElapsed() {
        if (this.waitFirst) {
            this.waitFirst = false;
            this.timeSinceLast = Util.getMillis();

            if (this.immediate) {
                this.immediate = false;
                return true;
            }

            return false;
        }

        if (this.timeSinceLast == 0L)
            this.timeSinceLast = Util.getMillis() - this.timeInterval;

        if (this.maxRepeat != NO_REPEAT && this.repeated >= this.maxRepeat)
            return false;

        if (Util.getMillis() - this.timeSinceLast >= this.timeInterval) {
            if (this.maxRepeat != NO_REPEAT)
                this.repeated++;

            this.timeSinceLast = Util.getMillis();

            if (this.debug) {
                String output = "Timer [timerInterval=%s, maxRepeat=%s, repeated=%s, timeSinceLast=%s] is ready";
                NostalgicTweaks.LOGGER.debug(output, this.timeInterval, this.maxRepeat, this.repeated, this.timeSinceLast);
            }

            return true;
        }

        return false;
    }

    public static class Builder {
        private final TimeUnit timeUnit;
        private final long duration;
        private final int maxRepeat;
        private boolean immediate = false;
        private boolean waitFirst = false;
        private boolean debug = false;

        private Builder(long duration, TimeUnit timeUnit, int maxRepeat) {
            this.duration = duration;
            this.timeUnit = timeUnit;
            this.maxRepeat = maxRepeat;
        }

        /**
         * Set the timer's {@code debug} flag to {@code false}.
         */
        @PublicAPI
        public Builder debug() {
            this.debug = true;
            return this;
        }

        /**
         * Set the timer's {@code immediate} flag to {@code true}. The {@code immediate} flag will make
         * {@link SimpleTimer#hasElapsed()} return {@code true} when invoked for the first time.
         */
        @PublicAPI
        public Builder immediate() {
            this.immediate = true;
            this.waitFirst = true;

            return this;
        }

        /**
         * Start the timer when invoking {@link SimpleTimer#hasElapsed()} for the first time. Otherwise, the timer
         * starts when the timer is built.
         */
        @PublicAPI
        public Builder waitFirst() {
            this.waitFirst = true;
            return this;
        }

        /**
         * Finish the building process.
         *
         * @return A new {@link SimpleTimer} instance.
         */
        public SimpleTimer build() {
            SimpleTimer timer = new SimpleTimer(timeUnit.toMillis(this.duration), this.maxRepeat);

            timer.immediate = this.immediate;
            timer.waitFirst = this.waitFirst;
            timer.debug = this.debug;

            return timer;
        }
    }
}
