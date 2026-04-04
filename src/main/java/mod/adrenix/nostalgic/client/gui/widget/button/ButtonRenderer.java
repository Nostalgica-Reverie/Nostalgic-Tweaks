package mod.adrenix.nostalgic.client.gui.widget.button;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a button is rendered.
 */
public interface ButtonRenderer<Builder extends AbstractButtonMaker<Builder, Button>, Button extends AbstractButton<Builder, Button>> {
    /**
     * Empty renderer function. Useful to prevent certain aspects of the button from rendering. For example, passing
     * this into the factory's background renderer function will prevent the background from displaying, leaving only
     * the icon (if present) and the button's title text.
     */
    ButtonRenderer<ButtonBuilder, ButtonWidget> EMPTY = (button, graphics, mouseX, mouseY, partialTick) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param button      The {@link Button} being rendered.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(Button button, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
