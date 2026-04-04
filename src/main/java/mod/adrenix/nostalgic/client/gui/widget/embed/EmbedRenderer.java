package mod.adrenix.nostalgic.client.gui.widget.embed;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface that provides custom rendering instructions that will be performed when an embed is
 * rendered.
 */
public interface EmbedRenderer {
    /**
     * Empty renderer function.
     */
    EmbedRenderer EMPTY = (overlay, graphics, mouseX, mouseY, partialTick) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param embed       The {@link Embed} instance.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(Embed embed, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
