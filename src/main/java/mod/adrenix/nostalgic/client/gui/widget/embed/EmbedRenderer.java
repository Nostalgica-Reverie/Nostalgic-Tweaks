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
    EmbedRenderer EMPTY = (_, _) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param embed    The {@link Embed} instance.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    void accept(Embed embed, GuiGraphicsExtractor graphics);
}
