package mod.adrenix.nostalgic.mixin.duck;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Gradient;

public interface RenderUtil {
    /**
     * Draw an outline (a hollow fill algorithm).
     *
     * @param x         The starting x-coordinate of the outline box.
     * @param y         The starting y-coordinate of the outline box.
     * @param width     The width of the outline box.
     * @param height    The height of the outline box.
     * @param thickness The thickness of the outline box.
     * @param color     The ARGB color of the outline box.
     */
    @PublicAPI
    default void nt$outline(int x, int y, int width, int height, int thickness, int color) {
        throw new AssertionError("Unimplemented duck method.");
    }

    /**
     * Draws a filled gradient rectangle based on the given {@link Gradient} instance.
     *
     * @param gradient A {@link Gradient} instance.
     * @param x0       The left x-coordinate of the fill.
     * @param y0       The top y-coordinate of the fill.
     * @param x1       The right x-coordinate of the fill.
     * @param y1       The bottom y-coordinate of the fill.
     */
    @PublicAPI
    default void nt$fillGradient(Gradient gradient, int x0, int y0, int x1, int y1) {
        throw new AssertionError("Unimplemented duck method.");
    }

    /**
     * Draws a filled gradient rectangle that goes left to right onto the screen.
     *
     * @param x0        The left x-coordinate of the fill.
     * @param y0        The top y-coordinate of the fill.
     * @param x1        The right x-coordinate of the fill.
     * @param y1        The bottom y-coordinate of the fill.
     * @param colorFrom The starting gradient ARGB integer color.
     * @param colorTo   The ending gradient ARGB integer color.
     */
    @PublicAPI
    default void nt$fillHorizontalGradient(int x0, int y0, int x1, int y1, int colorFrom, int colorTo) {
        throw new AssertionError("Unimplemented duck method.");
    }
}
