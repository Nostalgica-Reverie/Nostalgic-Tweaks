package mod.adrenix.nostalgic.client.gui.widget.slider;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a slider is rendered.
 */
public interface SliderRenderer<Builder extends AbstractSliderMaker<Builder, Slider>, Slider extends AbstractSlider<Builder, Slider>> {
    SliderRenderer<SliderBuilder, SliderWidget> EMPTY = (slider, graphics, mouseX, mouseY, partialTick) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param slider      The {@link Slider} being rendered.
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    void accept(Slider slider, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);
}
