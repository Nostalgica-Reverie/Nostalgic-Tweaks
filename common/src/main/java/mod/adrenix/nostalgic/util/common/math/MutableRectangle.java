package mod.adrenix.nostalgic.util.common.math;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;

/**
 * Create a rectangle whose position is mutable. Use {@link Rectangle} for an immutable instance. If a rectangle that
 * dynamically defines its x/y positions using a rectangular-like shape is necessary then use {@link DynamicRectangle}.
 */
public class MutableRectangle
{
    /* Fields */

    public final IntegerHolder startX = IntegerHolder.create(0);
    public final IntegerHolder startY = IntegerHolder.create(0);
    public final IntegerHolder endX = IntegerHolder.create(0);
    public final IntegerHolder endY = IntegerHolder.create(0);

    /* Methods */

    /**
     * Get an immutable rectangle of this mutable rectangle at its current state.
     *
     * @return An immutable {@link Rectangle} instance.
     */
    @PublicAPI
    public Rectangle immutable()
    {
        return new Rectangle(this.startX.get(), this.startY.get(), this.endX.get(), this.endY.get());
    }

    /**
     * @return The absolute width of this rectangle.
     */
    @PublicAPI
    public int getWidth()
    {
        return Math.abs(this.endX.get() - this.startX.get());
    }

    /**
     * @return The absolute height of this rectangle.
     */
    @PublicAPI
    public int getHeight()
    {
        return Math.abs(this.endY.get() - this.startY.get());
    }

    /**
     * Check if the given point is over this rectangle.
     *
     * @param pointX The x-position of the point.
     * @param pointY The y-position of the point.
     * @return Whether the given point is within this rectangle.
     */
    @PublicAPI
    public boolean isWithinBox(double pointX, double pointY)
    {
        return MathUtil.isWithinBox(pointX, pointY, this.startX.get(), this.startY.get(), this.getWidth(), this.getHeight());
    }

    /**
     * @return Whether the rectangle has both a width and height of length zero.
     */
    @PublicAPI
    public boolean isEmpty()
    {
        return this.getWidth() == 0 && this.getHeight() == 0;
    }

    /**
     * Reset all rectangle positions back to zero.
     */
    @PublicAPI
    public void clear()
    {
        this.startX.set(0);
        this.startY.set(0);
        this.endX.set(0);
        this.endY.set(0);
    }
}
