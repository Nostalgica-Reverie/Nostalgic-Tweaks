package mod.adrenix.nostalgic.client.gui.widget.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a row is rendered.
 */
public interface RowRenderer<M extends AbstractRowMaker<M, R>, R extends AbstractRow<M, R>> {
    /**
     * Empty renderer function.
     */
    RowRenderer<RowMaker, Row> EMPTY = (row, graphics, mouseX, mouseY, partialTick) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param row         The {@link R} being rendered.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(R row, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
