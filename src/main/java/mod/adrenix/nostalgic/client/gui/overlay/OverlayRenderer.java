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
    OverlayRenderer EMPTY = (overlay, graphics) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param overlay  The {@link Overlay} instance.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    void accept(Overlay overlay, GuiGraphicsExtractor graphics);
}
