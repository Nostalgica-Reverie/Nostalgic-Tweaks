package mod.adrenix.nostalgic.client.gui.overlay;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface that provides custom rendering instructions that will be performed when an overlay is
 * rendered.
 */
public interface OverlayRenderer {
    /**
     * Empty renderer function.
     */
    OverlayRenderer EMPTY = (overlay, graphics, mouseX, mouseY, partialTick) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param overlay     The {@link Overlay} instance.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(Overlay overlay, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
