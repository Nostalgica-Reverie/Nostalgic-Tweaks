package mod.adrenix.nostalgic.util.client.animate;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;

import java.util.concurrent.TimeUnit;

public interface Animate {
    /* Linear Functions */

    /**
     * Constant movement along a line.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation linear(long duration, TimeUnit timeUnit) {
        return new Animator(v -> Mth.clamp(v, 0.0D, 1.0D), duration, timeUnit);
    }

    /**
     * Constant movement along a line that lasts one second.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation linear() {
        return linear(1L, TimeUnit.SECONDS);
    }

    /* Ease-In Functions */

    /**
     * Begins slow and then moves with linear motion.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInSine(long duration, TimeUnit timeUnit) {
        return new Animator(v -> 1.0D - Math.cos((v * Math.PI) / 2.0D), duration, timeUnit);
    }

    /**
     * Begins slow and then moves with linear motion.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInSine() {
        return easeInSine(1L, TimeUnit.SECONDS);
    }

    /**
     * Begins slow and then gets faster for the rest of the animation.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInCubic(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v * v * v, duration, timeUnit);
    }

    /**
     * Begins slow and then gets faster for the rest of the animation.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInCubic() {
        return easeInCubic(1L, TimeUnit.SECONDS);
    }

    /**
     * Begins slow and then ends really fast.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInCircular(long duration, TimeUnit timeUnit) {
        return new Animator(v -> 1.0D - Math.sqrt(1.0D - Math.pow(v, 2.0D)), duration, timeUnit);
    }

    /**
     * Begins slow and then ends really fast.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInCircular() {
        return easeInCircular(1L, TimeUnit.SECONDS);
    }

    /**
     * Begins slowly and then exponentially gets faster.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInExpo(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v == 0.0D ? 0.D : Math.pow(2.0D, 10.0D * v - 10.0D), duration, timeUnit);
    }

    /**
     * Begins slowly and then exponentially gets faster.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInExpo() {
        return easeInExpo(1L, TimeUnit.SECONDS);
    }

    /* Ease-Out Functions */

    /**
     * Begins fast and ends slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutSine(long duration, TimeUnit timeUnit) {
        return new Animator(v -> Math.sin((v * Math.PI) / 2.0D), duration, timeUnit);
    }

    /**
     * Begins linear and then ends slowly.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutSine() {
        return easeOutSine(1L, TimeUnit.SECONDS);
    }

    /**
     * Begins fast and then gets slower for the rest of the animation.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutCubic(long duration, TimeUnit timeUnit) {
        return new Animator(v -> 1.0D - Math.pow(1.0D - v, 3.0D), duration, timeUnit);
    }

    /**
     * Begins fast and then gets slower for the rest of the animation.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutCubic() {
        return easeOutCubic(1L, TimeUnit.SECONDS);
    }

    /**
     * Begins fast and then ends slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutCircular(long duration, TimeUnit timeUnit) {
        return new Animator(v -> Math.sqrt(1.0D - Math.pow(v - 1.0D, 2.0D)), duration, timeUnit);
    }

    /**
     * Begins fast and then ends slowly.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutCircular() {
        return easeOutCircular(1L, TimeUnit.SECONDS);
    }

    /**
     * Starts fast, begins to slow in the middle, and ends very slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeOutExpo(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v == 1 ? 1 : 1 - Math.pow(2, -10 * v), duration, timeUnit);
    }

    /**
     * Starts fast, begins to slow in the middle, and ends very slowly.
     *
     * @return A new animator instance that lasts for one second.
     */
    @PublicAPI
    static Animation easeOutExpo() {
        return easeOutExpo(1L, TimeUnit.SECONDS);
    }

    /* Ease-In-Out Functions */

    /**
     * Starts slow, moves linear in the middle, and ends slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutSine(long duration, TimeUnit timeUnit) {
        return new Animator(v -> -(Math.cos(Math.PI * v) - 1.0D) / 2.0D, duration, timeUnit);
    }

    /**
     * Starts slow, moves linear in the middle, and ends slowly.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutSine() {
        return easeInOutSine(1L, TimeUnit.SECONDS);
    }

    /**
     * Begins slow, smoothly moves through the middle, and ends slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutCubic(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v, duration, timeUnit);
    }

    /**
     * Begins slow, smoothly moves through the middle, and ends slowly.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutCubic() {
        return easeInOutCubic(1L, TimeUnit.SECONDS);
    }

    /**
     * Similar to ease-in-out-quartic but with smoother start/end transitions and a more aggressive transition in the
     * middle of the animation.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutCircular(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * v, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * v + 2, 2)) + 1) / 2, duration, timeUnit);
    }

    /**
     * Similar to ease-in-out-quartic but with smoother start/end transitions and a more aggressive transition in the
     * middle of the animation.
     *
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutCircular() {
        return easeInOutCircular(1L, TimeUnit.SECONDS);
    }

    /**
     * Starts very slowly, moves quickly through the middle, and ends very slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutExpo(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v == 0 ? 0 : v == 1 ? 1 : v < 0.5 ? Math.pow(2, 20 * v - 10) / 2 : (2 - Math.pow(2, -20 * v + 10)) / 2, duration, timeUnit);
    }

    /**
     * Starts very slowly, moves quickly through the middle, and ends very slowly.
     *
     * @return A new animator instance that lasts for one second.
     */
    @PublicAPI
    static Animation easeInOutExpo() {
        return easeInOutExpo(1L, TimeUnit.SECONDS);
    }

    /**
     * Starts slow, speeds up quickly in the middle, and ends slowly.
     *
     * @param duration The duration of the given time unit enumeration.
     * @param timeUnit A {@link TimeUnit} enumeration.
     * @return A new animator instance.
     */
    @PublicAPI
    static Animation easeInOutQuart(long duration, TimeUnit timeUnit) {
        return new Animator(v -> v < 0.5 ? 8 * v * v * v * v : 1 - Math.pow(-2 * v + 2, 4) / 2, duration, timeUnit);
    }

    /**
     * Starts slow, speeds up quickly in the middle, and ends slowly.
     *
     * @return A new animator instance that lasts for one second.
     */
    @PublicAPI
    static Animation easeInOutQuart() {
        return easeInOutQuart(1L, TimeUnit.SECONDS);
    }
}
