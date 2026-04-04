package mod.adrenix.nostalgic.util.common.timer;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * This type of timer updates every tick.
 */
public class TickTimer {
    private final SimpleTimer timer;

    /* Fields */
    private Runnable whenFinished;
    private boolean finished;

    /**
     * Create a new common timer instance. To subscribe the timer to a tick-loop. Use {@code ClientTimer} or
     * {@code ServerTimer} utility classes.
     *
     * @param timer        A {@link SimpleTimer} instance.
     * @param whenFinished A {@link Runnable} instance.
     */
    private TickTimer(SimpleTimer timer, Runnable whenFinished) {
        this.whenFinished = whenFinished;
        this.timer = timer;
        this.timer.waitFirst();
    }

    /* Constructor */

    /**
     * @return The {@link Runnable} instructions to run when the timer is finished.
     */
    public Runnable getInstructions() {
        return this.whenFinished;
    }

    /* Methods */

    /**
     * Change the instructions to run when the timer is finished.
     *
     * @param runnable A {@link Runnable} instance.
     */
    public void setInstructions(Runnable runnable) {
        this.whenFinished = runnable;
    }

    /**
     * @return A normalized progress (0.0D-1.0D) amount until the timer is finished.
     */
    public double getProgress() {
        return this.timer.getProgress();
    }

    /**
     * @return Whether the progress is greater than or equal to 1.0D, or if the timer is considered finished because its
     * given instructions were run.
     */
    public boolean isDone() {
        return this.getProgress() >= 1.0D || this.finished;
    }

    /**
     * @return Whether the progress is less than 1.0D, or if the timer is not considered finished.
     */
    public boolean isTicking() {
        return !this.isDone();
    }

    /**
     * @return Whether the timer has completed and the instructions were run.
     */
    private boolean isFinished() {
        return this.finished;
    }

    /**
     * Resets the timer for re-use.
     */
    public void reset() {
        this.timer.reset();
        this.finished = false;
    }

    /**
     * Checks if the timer is finished, and if so, executes the assigned runnable.
     */
    public void tick() {
        if (!this.finished && this.timer.hasElapsed()) {
            this.finished = true;
            this.whenFinished.run();
        }
    }

    /**
     * Each sided environment needs its own manager since ticking is handled by the client or server.
     */
    public interface Manager {
        /**
         * Each logical side has a different tick loop. Therefore, the sided implementations of the common timer will
         * have their own cache of timers.
         *
         * @return A {@link HashSet} of {@link TickTimer} timers.
         */
        HashSet<TickTimer> getTimers();

        /**
         * Create a new {@link TickTimer} instance using a time unit and predefined runnable.
         *
         * @param delay        The time delay.
         * @param timeUnit     The {@link TimeUnit} to use.
         * @param whenFinished The {@link Runnable} to run when finished.
         * @return A new {@link TickTimer} instance.
         */
        default TickTimer create(long delay, TimeUnit timeUnit, Runnable whenFinished) {
            return new TickTimer(new SimpleTimer(timeUnit.toMillis(delay), 1), whenFinished);
        }

        /**
         * Create a new {@link TickTimer} instance using a time unit and an empty runnable.
         *
         * @param delay    The time delay.
         * @param timeUnit The {@link TimeUnit} to use.
         * @return A new {@link TickTimer} instance.
         */
        default TickTimer create(long delay, TimeUnit timeUnit) {
            return new TickTimer(new SimpleTimer(timeUnit.toMillis(delay), 1), () -> {
            });
        }

        /**
         * Create a new {@link TickTimer} using milliseconds and with a predefined runnable.
         *
         * @param delayInMillis The time to wait for in milliseconds.
         * @param whenFinished  The {@link Runnable} to run when finished.
         * @return A new {@link TickTimer} instance.
         */
        default TickTimer create(long delayInMillis, Runnable whenFinished) {
            return new TickTimer(new SimpleTimer(delayInMillis, 1), whenFinished);
        }

        /**
         * Create a new {@link TickTimer} using milliseconds and an empty runnable.
         *
         * @param delayInMillis The time to wait for in milliseconds.
         * @return A new {@link TickTimer} instance.
         */
        default TickTimer create(long delayInMillis) {
            return new TickTimer(new SimpleTimer(delayInMillis, 1), () -> {
            });
        }

        /**
         * Instructions to run at the start of each tick.
         */
        default void onTick() {
            this.getTimers().removeIf(TickTimer::isFinished);
            this.getTimers().forEach(TickTimer::tick);
        }

        /**
         * Cancel the provided timer.
         *
         * @param timer A {@link TickTimer} instance.
         */
        default void cancel(TickTimer timer) {
            this.getTimers().remove(timer);
            timer.finished = true;
        }

        /**
         * Resets the given timer and subscribes it back into the tick-loop.
         *
         * @param timer A {@link TickTimer} instance.
         */
        default void run(TickTimer timer) {
            timer.reset();
            this.getTimers().add(timer);
        }

        /**
         * Changes the timer's {@code whenFinished} instructions, resets the timer, and subscribes the timer back into
         * the tick-loop.
         *
         * @param timer        The {@link TickTimer} instance.
         * @param whenFinished A {@link Runnable} to run when the timer is finished.
         */
        default void run(TickTimer timer, Runnable whenFinished) {
            timer.setInstructions(whenFinished);
            this.run(timer);
        }

        /**
         * Run the given instructions after the given {@code timeInterval}, in milliseconds, has elapsed. If the given
         * {@link Runnable} is already in the tick-loop, then it will <b>not</b> be subscribed again.
         *
         * @param delayInMillis The amount of time in milliseconds to wait before executing the given runnable.
         * @param whenFinished  A {@link Runnable} with instructions to run.
         */
        default void runAfter(long delayInMillis, Runnable whenFinished) {
            boolean isRunning = this.getTimers()
                    .stream()
                    .map(TickTimer::getInstructions)
                    .anyMatch(runnable -> runnable.equals(whenFinished));

            if (isRunning)
                return;

            this.getTimers().add(this.create(delayInMillis, whenFinished));
        }

        /**
         * Run the given instructions after the given {@code timeInterval}, in milliseconds, has elapsed. If the given
         * {@link Runnable} is already in the tick-loop, then it will <b>not</b> be subscribed again.
         *
         * @param delay        The delay to use for the time unit.
         * @param timeUnit     The {@link TimeUnit} to use for converting to milliseconds.
         * @param whenFinished The {@link Runnable} to run after the set time has elapsed.
         */
        default void runAfter(long delay, TimeUnit timeUnit, Runnable whenFinished) {
            this.runAfter(timeUnit.toMillis(delay), whenFinished);
        }
    }
}
