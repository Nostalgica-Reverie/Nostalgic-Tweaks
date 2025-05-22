package mod.adrenix.nostalgic.util.common.math;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.util.Collection;
import java.util.function.ToIntFunction;

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
    /* Builders */

    /**
     * Create a new instance using a collection and x/y coordinate function mappers.
     *
     * @param collection A {@link Collection} of elements to map coordinates from.
     * @param x          A {@link ToIntFunction}, that when applied to an element, yields an x-coordinate.
     * @param y          A {@link ToIntFunction}, that when applied to an element, yields a y-coordinate.
     * @param <E>        The class type of the elements within the given collection.
     * @return A {@link Rectangle} instance that is a min/max range around the given collection and its coordinates.
     */
    @PublicAPI
    public static <E> Rectangle fromCollection(Collection<E> collection, ToIntFunction<? super E> x, ToIntFunction<? super E> y)
    {
        int minX = collection.stream().mapToInt(x).min().orElse(0);
        int minY = collection.stream().mapToInt(y).min().orElse(0);
        int maxX = collection.stream().mapToInt(x).max().orElse(0);
        int maxY = collection.stream().mapToInt(y).max().orElse(0);

        return new Rectangle(minX, minY, maxX, maxY);
    }

    /**
     * Create a new instance using a collection, startX/startY coordinate function mappers, and endX/endY coordinate
     * function mappers.
     *
     * @param collection A {@link Collection} of elements to map coordinates from.
     * @param startX     A {@link ToIntFunction}, that when applied to an element, yields a starting x-coordinate.
     * @param startY     A {@link ToIntFunction}, that when applied to an element, yields a starting y-coordinate.
     * @param endX       A {@link ToIntFunction}, that when applied to an element, yields an ending x-coordinate.
     * @param endY       A {@link ToIntFunction}, that when applied to an element, yields an ending y-coordinate.
     * @param <E>        The class type of the elements within the given collection.
     * @return A {@link Rectangle} instance that is a min/max range around the given collection and its coordinates.
     */
    @PublicAPI
    public static <E> Rectangle fromCollection(Collection<E> collection, ToIntFunction<? super E> startX, ToIntFunction<? super E> startY, ToIntFunction<? super E> endX, ToIntFunction<? super E> endY)
    {
        int minX = collection.stream().mapToInt(startX).min().orElse(0);
        int minY = collection.stream().mapToInt(startY).min().orElse(0);
        int maxX = collection.stream().mapToInt(endX).max().orElse(0);
        int maxY = collection.stream().mapToInt(endY).max().orElse(0);

        return new Rectangle(minX, minY, maxX, maxY);
    }

    /**
     * Create a new instance using a given point instead of bounds.
     *
     * @param pointX The x-coordinate.
     * @param pointY The y-coordinate.
     * @return A point {@link Rectangle} instance.
     */
    @PublicAPI
    public static Rectangle fromPoint(int pointX, int pointY)
    {
        return new Rectangle(pointX, pointY, pointX, pointY);
    }

    /* Methods */

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

    /* Helpers */

    /**
     * Check if two rectangles intersect at any point.
     *
     * @param rect1 The first {@link Rectangle} to check.
     * @param rect2 The second {@link Rectangle} to check.
     * @return Whether the two given rectangles intersect anywhere.
     */
    @PublicAPI
    public static boolean intersect(Rectangle rect1, Rectangle rect2)
    {
        return rect1.endX >= rect2.startX && rect2.endX >= rect1.startX && rect1.endY >= rect2.startY && rect2.endY >= rect1.startY;
    }
}
