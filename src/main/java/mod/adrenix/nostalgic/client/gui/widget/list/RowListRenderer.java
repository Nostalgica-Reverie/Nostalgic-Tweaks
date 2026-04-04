package mod.adrenix.nostalgic.client.gui.widget.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a row list widget is
 * rendered.
 */
public interface RowListRenderer {
    /**
     * Empty renderer function.
     */
    RowListRenderer EMPTY = (rowList, graphics, mouseX, mouseY, partialTick) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param rowList     The {@link RowList} being rendered.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(RowList rowList, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
