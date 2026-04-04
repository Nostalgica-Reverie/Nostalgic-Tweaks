package mod.adrenix.nostalgic.util.client.animate;

import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Animator implements Animation {
    /* Tick Loop */

    /**
     * Holds a collection of animation instances to be ticked.
     */
    static final HashSet<Animation> INSTANCES = new HashSet<>();
    Object identifier;

    /* Fields */
    private Function<Double, Double> animator;
    private double currentValue;
    private double lastValue;
    private double minValue;
    private double maxValue;
    private double tickProgress;
    private double durationInTicks;
    private boolean reverse;
    private boolean finished;
    private boolean wasForward;
    private boolean wasBackward;

    /**
     * Create a new animator instance. It is recommended to use the pre-built animators, such as
     * {@link Animate#easeInOutQuart(long, TimeUnit)}.
     *
     * <p><br>
     * If you have a special animation formula, then you can define your own animator using
     * {@link Animation#animateWith(Function)}.
     *
     * <p><br>
     * <b color=red>Important:</b> Formulas receive a normalized value [0.0D, 1.0D] which is calculated by the number
     * of ticks the animation has moved out of the maximum number of ticks the animation can move.
     *
     * @see Animation#animateWith(Function)
     * @see Animation#setDuration(long, TimeUnit)
     */
    public Animator() {
        this.animator = value -> 1.0D;
        this.tickProgress = 0.0D;
        this.currentValue = 0.0D;
        this.lastValue = 0.0D;
        this.minValue = Double.MIN_VALUE;
        this.maxValue = Double.MAX_VALUE;
        this.reverse = false;
        this.finished = true;

        this.setDuration(1L, TimeUnit.SECONDS);
    }

    /* Constructor */

    /**
     * Create a new animator instance using the given arguments.
     *
     * @param animator A function that accepts a double between 0.0D and 1.0D and returns a value that should be between
     *                 0.0D and 1.0D. This bound is not enforced, and some animators may find it useful to go outside of
     *                 these bounds depending on the goal of the animation.
     * @param duration The amount of time this animation runs for.
     * @param timeUnit A {@link TimeUnit} enumeration value.
     */
    public Animator(Function<Double, Double> animator, long duration, TimeUnit timeUnit) {
        this();

        this.animateWith(animator);
        this.setDuration(duration, timeUnit);
    }

    /**
     * This will call {@link Animation#tick()} for all animation instances every tick.
     */
    public static void onTick() {
        INSTANCES.removeIf(Animation::isFinished);
        INSTANCES.forEach(Animation::tick);
    }

    /* Methods */

    @Override
    public Animation copy() {
        Animator copy = new Animator();

        copy.animator = this.animator;
        copy.durationInTicks = this.durationInTicks;
        copy.minValue = this.minValue;
        copy.maxValue = this.maxValue;

        return copy;
    }

    @Override
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    @Override
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public double getValue() {
        return Mth.lerp(PartialTick.legacy(), this.lastValue, this.currentValue);
    }

    private boolean isReverseFinished() {
        boolean areValuesEqual = this.lastValue == this.currentValue;
        boolean isTickFinished = this.reverse && this.tickProgress <= 0 && areValuesEqual;
        boolean isMinReached = this.reverse && this.currentValue <= this.minValue && areValuesEqual;

        return isTickFinished || isMinReached;
    }

    private boolean isForwardFinished() {
        boolean areValuesEqual = this.lastValue == this.currentValue;
        boolean isTickFinished = !this.reverse && this.tickProgress >= this.durationInTicks && areValuesEqual;
        boolean isMaxReached = !this.reverse && this.currentValue >= this.maxValue && areValuesEqual;

        return isTickFinished || isMaxReached;
    }

    @Override
    public void tick() {
        if (this.durationInTicks <= 0 || this.isReverseFinished() || this.isForwardFinished()) {
            this.stop();
            return;
        }

        if (this.reverse) {
            this.wasForward = false;
            this.wasBackward = true;

            this.tickProgress--;
        } else {
            this.wasForward = true;
            this.wasBackward = false;

            this.tickProgress++;
        }

        this.tickProgress = Mth.clamp(this.tickProgress, 0.0D, this.durationInTicks);

        this.lastValue = this.currentValue;
        this.currentValue = Mth.clamp(this.animator.apply(this.tickProgress / this.durationInTicks), this.minValue, this.maxValue);
    }

    @Override
    public double getTickProgress() {
        return this.tickProgress;
    }

    @Override
    public void setTickProgress(float progress) {
        this.lastValue = 0.0D;
        this.tickProgress = (int) Math.round(this.durationInTicks * Mth.clamp(progress, 0.0F, 1.0F));
        this.currentValue = Mth.clamp(this.animator.apply(this.tickProgress / this.durationInTicks), this.minValue, this.maxValue);
    }

    @Override
    public void reset() {
        this.tickProgress = 0.0D;
        this.currentValue = 0.0D;
        this.lastValue = 0.0D;
        this.reverse = false;
    }

    @Override
    public void stop() {
        if (this.reverse)
            this.reverse = false;

        this.finished = true;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public boolean isMoving() {
        return this.tickProgress > 0.0D && this.tickProgress < this.durationInTicks;
    }

    @Override
    public boolean wentForward() {
        if (!this.wasForward && !this.wasBackward)
            return true;

        return this.wasForward;
    }

    @Override
    public boolean wentBackward() {
        if (!this.wasForward && !this.wasBackward)
            return true;

        return this.wasBackward;
    }

    @Override
    public void rewind() {
        if (this.tickProgress > 0) {
            this.reverse = true;
            this.finished = false;

            INSTANCES.add(this);
        }
    }

    @Override
    public void play() {
        this.finished = false;
        INSTANCES.add(this);
        this.reverse = false;
    }

    @Override
    public void animateWith(Function<Double, Double> animator) {
        this.animator = animator;
    }

    @Override
    public Function<Double, Double> getAnimation() {
        return this.animator;
    }

    @Override
    public void setDuration(long duration, TimeUnit timeUnit) {
        this.durationInTicks = (double) timeUnit.toMillis(duration) / 50.0D;
    }

    @Override
    public Object getIdentifier() {
        return this.identifier;
    }

    @Override
    public void setIdentifier(Object identifier) {
        this.identifier = identifier;
    }
}
