package mod.adrenix.nostalgic.util.common.timer;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A timer that controls linear interpolation. The delta for interpolation is controlled by the amount of real time that
 * has elapsed.
 */
public class LerpTimer {
    /* Builders */

    private long lengthInMillis;

    /* Fields */
    private long changedTime = -1L;
    private double targetValue = -1.0D;
    private double previousValue = -1.0D;

    private LerpTimer(long lengthInMillis) {
        this.lengthInMillis = lengthInMillis;
    }

    /* Constructor */

    /**
     * Create a new linear interpolation timer.
     *
     * @param duration The duration of the interpolation.
     * @param timeUnit The {@link TimeUnit} used to convert {@code duration} into milliseconds.
     * @return A new {@link LerpTimer} instance.
     */
    public static LerpTimer create(long duration, TimeUnit timeUnit) {
        return new LerpTimer(timeUnit.toMillis(duration));
    }

    /* Methods */

    /**
     * Set the duration of the linear interpolation.
     *
     * @param duration The duration of the interpolation.
     * @param timeUnit The {@link TimeUnit} used to convert {@code duration} into milliseconds.
     */
    @PublicAPI
    public void setDuration(long duration, TimeUnit timeUnit) {
        this.lengthInMillis = timeUnit.toMillis(duration);
    }

    /**
     * Reset the linear interpolation timer. Note that this will <b color=red>not</b> reset the target value. Use
     * {@link #clear()} to reset the previous value and target value.
     */
    @PublicAPI
    public void reset() {
        this.changedTime = -1L;
    }

    /**
     * This will {@link #reset()} the linear interpolation timer and {@code clear} the previous value and target value.
     */
    @PublicAPI
    public void clear() {
        this.changedTime = -1L;
        this.previousValue = -1.0D;
        this.targetValue = -1.0D;
    }

    /**
     * Set the linear interpolation's ending target value.
     *
     * @param target A number that will be converted to a {@code double} that represents the ending target.
     */
    @PublicAPI
    public void setTarget(Number target) {
        double value = target.doubleValue();
        long time = Util.getMillis();

        if (this.changedTime < 0L) {
            this.targetValue = value;
            this.previousValue = value;
            this.changedTime = time;
        } else if (value != this.targetValue) {
            this.previousValue = this.lerpDouble();
            this.targetValue = value;
            this.changedTime = time;
        }
    }

    /**
     * Set the linear interpolation's ending target value and retrieve the next linear interpolation using the given
     * function.
     *
     * @param target   A number that will be converted to a {@code double} that represents the ending target.
     * @param function A {@link Function} that accepts this {@link LerpTimer} and yields the number type expected.
     * @param <T>      The type of {@link Number}.
     * @return The liner interpolation as the given {@link T}.
     */
    @PublicAPI
    public <T extends Number> T setAndGetTarget(T target, Function<LerpTimer, T> function) {
        this.setTarget(target);

        return function.apply(this);
    }

    /**
     * Stop linear interpolation and set the target to the given target value immediately.
     *
     * @param target A number that will be converted to a {@code double} that represents the ending target.
     */
    @PublicAPI
    public void stopAndSetTarget(Number target) {
        double value = target.doubleValue();

        this.targetValue = value;
        this.previousValue = value;
        this.changedTime = Util.getMillis() - this.lengthInMillis;
    }

    /**
     * If the previous linear interpolation is at the ending target, then the given target will be set immediately.
     * Otherwise, the linear interpolation will restart to the new given target.
     *
     * @param target A number that will be converted to a {@code double} that represents the ending target.
     */
    @PublicAPI
    public void ifEndThenSetTarget(Number target) {
        if (!this.isFinished())
            this.setTarget(target);
        else
            this.stopAndSetTarget(target);
    }

    /**
     * @return A value between zero and one that indicates the percentage of the linear interpolation. Zero will give
     * the start value and one will give the end value.
     */
    @PublicAPI
    public double delta() {
        return Mth.clamp((float) (Util.getMillis() - this.changedTime) / this.lengthInMillis, 0.0F, 1.0F);
    }

    /**
     * @return The linear interpolation as a {@code double}.
     */
    @PublicAPI
    public double lerpDouble() {
        return Mth.lerp(this.delta(), this.previousValue, this.targetValue);
    }

    /**
     * @return The linear interpolation as a {@code float}.
     */
    @PublicAPI
    public float lerpFloat() {
        return (float) this.lerpDouble();
    }

    /**
     * @return The linear interpolation as an {@code integer}.
     */
    @PublicAPI
    public int lerpInt() {
        return (int) this.lerpDouble();
    }

    /**
     * @return The ending value of the linear interpolation as a {@code double}.
     */
    @PublicAPI
    public double endDouble() {
        return this.targetValue;
    }

    /**
     * @return The ending value of the linear interpolation as a {@code float}.
     */
    @PublicAPI
    public float endFloat() {
        return (float) this.targetValue;
    }

    /**
     * @return The ending value of the linear interpolation as an {@code integer}.
     */
    @PublicAPI
    public int endInt() {
        return (int) this.targetValue;
    }

    /**
     * @return The starting value of the linear interpolation as a {@code double}.
     */
    @PublicAPI
    public double startDouble() {
        return this.previousValue;
    }

    /**
     * @return The starting value of the linear interpolation as a {@code float}.
     */
    @PublicAPI
    public float startFloat() {
        return (float) this.previousValue;
    }

    /**
     * @return The starting value of the linear interpolation as an {@code integer}.
     */
    @PublicAPI
    public int startInt() {
        return (int) this.previousValue;
    }

    /**
     * @return Whether the linear interpolation has finished.
     */
    @PublicAPI
    public boolean isFinished() {
        return this.delta() >= 1.0F;
    }
}
