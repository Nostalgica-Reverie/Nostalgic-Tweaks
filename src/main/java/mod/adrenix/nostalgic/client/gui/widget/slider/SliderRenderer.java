package mod.adrenix.nostalgic.client.gui.widget.slider;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * This is a functional interface for providing custom rendering instructions to perform when a slider is rendered.
 */
public interface SliderRenderer<Builder extends AbstractSliderMaker<Builder, Slider>, Slider extends AbstractSlider<Builder, Slider>> {
    SliderRenderer<SliderBuilder, SliderWidget> EMPTY = (slider, graphics) -> {
    };

    /* Static */

    /**
     * Performs this operation on the given arguments.
     *
     * @param slider   The {@link Slider} being rendered.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    void accept(Slider slider, GuiGraphicsExtractor graphics);
}
