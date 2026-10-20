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
    RowListRenderer EMPTY = (rowList, graphics) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param rowList  The {@link RowList} being rendered.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    void accept(RowList rowList, GuiGraphicsExtractor graphics);
}
