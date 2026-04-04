package mod.adrenix.nostalgic.util.common.math;

import java.util.function.ToIntFunction;

/**
 * A record that dynamically defines the x/y positions of a rectangular shape.
 *
 * @param startX The {@link ToIntFunction} that indicates the starting x-position of the rectangle.
 * @param startY The {@link ToIntFunction} that indicates the starting y-position of the rectangle.
 * @param endX   The {@link ToIntFunction} that indicates the ending x-position of the rectangle.
 * @param endY   The {@link ToIntFunction} that indicates the ending y-position of the rectangle.
 * @param <T>    The type of the input to the integer functions.
 */
public record DynamicRectangle<T>(ToIntFunction<T> startX, ToIntFunction<T> startY, ToIntFunction<T> endX,
                                  ToIntFunction<T> endY) {
    /**
     * Get a standard rectangle from a dynamic rectangle.
     *
     * @param t The {@link T} to apply.
     * @return A new {@link Rectangle} instance.
     */
    public Rectangle getRectangle(T t) {
        return new Rectangle(this.startX.applyAsInt(t), this.startY.applyAsInt(t), this.endX.applyAsInt(t), this.endY.applyAsInt(t));
    }

    /**
     * @param t The {@link T} to apply.
     * @return The absolute width of this rectangle.
     */
    public int getWidth(T t) {
        return Math.abs(this.endX.applyAsInt(t) - this.startX.applyAsInt(t));
    }

    /**
     * @param t The {@link T} to apply.
     * @return The absolute height of this rectangle.
     */
    public int getHeight(T t) {
        return Math.abs(this.endY.applyAsInt(t) - this.startY.applyAsInt(t));
    }

    /**
     * Check if the given mouse point is over this rectangle.
     *
     * @param t      The {@link T} to apply.
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @return Whether the given mouse point is over this rectangle.
     */
    public boolean isMouseOver(T t, double mouseX, double mouseY) {
        return MathUtil.isWithinBox(mouseX, mouseY, this.startX.applyAsInt(t), this.startY.applyAsInt(t), this.getWidth(t), this.getHeight(t));
    }
}
