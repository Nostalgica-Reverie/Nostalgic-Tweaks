package mod.adrenix.nostalgic.client.gui.widget.blank;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a blank widget is
 * rendered.
 */
public interface BlankRenderer {
    /**
     * Empty renderer function.
     */
    BlankRenderer EMPTY = (_, _) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param widget      The {@link BlankWidget} being rendered.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     */
    void accept(BlankWidget widget, GuiGraphicsExtractor graphics);
}
