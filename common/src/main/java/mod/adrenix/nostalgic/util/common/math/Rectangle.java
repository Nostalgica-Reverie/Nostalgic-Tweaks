package mod.adrenix.nostalgic.util.common.math;

/**
 * A simple record that defines the x/y positions of a rectangular shape.
 *
 * @param startX The starting x-position of the rectangle.
 * @param startY The starting y-position of the rectangle.
 * @param endX   The ending x-position of the rectangle.
 * @param endY   The ending y-position of the rectangle.
 */
public record Rectangle(int startX, int startY, int endX, int endY)
{
    /**
     * @return The absolute width of this rectangle.
     */
    public int getWidth()
    {
        return Math.abs(this.endX - this.startX);
    }

    /**
     * @return The absolute height of this rectangle.
     */
    public int getHeight()
    {
        return Math.abs(this.endY - this.startY);
    }

    /**
     * Check if the given mouse point is over this rectangle.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @return Whether the given mouse point is over this rectangle.
     */
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return MathUtil.isWithinBox(mouseX, mouseY, this.startX, this.startY, this.getWidth(), this.getHeight());
    }

    /**
     * Check if two rectangles intersect at any point.
     *
     * @param rect1 The first {@link Rectangle} to check.
     * @param rect2 The second {@link Rectangle} to check.
     * @return Whether the two given rectangles intersect anywhere.
     */
    public static boolean intersect(Rectangle rect1, Rectangle rect2)
    {
        return rect1.endX >= rect2.startX && rect2.endX >= rect1.startX && rect1.endY >= rect2.startY && rect2.endY >= rect1.startY;
    }
}
