package mod.adrenix.nostalgic.util.client.renderer;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class RenderUtil {
    /**
     * Draw an outline (a hollow fill algorithm).
     *
     * @param graphics  A {@link GuiGraphicsExtractor} instance.
     * @param x         The starting x-coordinate of the outline box.
     * @param y         The starting y-coordinate of the outline box.
     * @param width     The width of the outline box.
     * @param height    The height of the outline box.
     * @param thickness The thickness of the outline box.
     * @param color     The ARGB color of the outline box.
     */
    @PublicAPI
    public static void outline(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int thickness, int color) {
        graphics.fill(x, y, x + width, y + thickness, color);
        graphics.fill(x, y + height - thickness, x + width, y + height, color);
        graphics.fill(x, y + thickness, x + thickness, y + height - thickness, color);
        graphics.fill(x + width - thickness, y + thickness, x + width, y + height - thickness, color);
    }
}
