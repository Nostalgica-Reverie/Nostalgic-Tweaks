package mod.adrenix.nostalgic.client.gui.widget.list;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a row is rendered.
 */
public interface RowRenderer<M extends AbstractRowMaker<M, R>, R extends AbstractRow<M, R>> {
    /**
     * Empty renderer function.
     */
    RowRenderer<RowMaker, Row> EMPTY = (row, graphics) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param row      The {@link R} being rendered.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    void accept(R row, GuiGraphicsExtractor graphics);
}
