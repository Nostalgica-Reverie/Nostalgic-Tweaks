package mod.adrenix.nostalgic.client.gui.widget.slider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderWidget extends AbstractSlider<SliderBuilder, SliderWidget> {
    /* Builders */

    protected SliderWidget(SliderBuilder builder) {
        super(builder);
    }

    /* Constructor */

    /**
     * Create a new {@link SliderWidget} instance.
     *
     * @param minValue      The minimum value allowed.
     * @param maxValue      The maximum value allowed.
     * @param valueConsumer A {@link Consumer} that accepts a number from the slider.
     * @param valueSupplier A {@link Supplier} that provides a number for the slider.
     * @return A new {@link SliderBuilder} instance.
     */
    public static SliderBuilder create(Number minValue, Number maxValue, Consumer<Number> valueConsumer, Supplier<Number> valueSupplier) {
        return new SliderBuilder(minValue, maxValue, valueConsumer, valueSupplier);
    }
}
