package mod.adrenix.nostalgic.client.gui.widget.slider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderBuilder extends AbstractSliderMaker<SliderBuilder, SliderWidget> {
    /* Constructor */

    protected SliderBuilder(Number minValue, Number maxValue, Consumer<Number> valueConsumer, Supplier<Number> valueSupplier) {
        super(minValue, maxValue, valueConsumer, valueSupplier);
    }

    /* Methods */

    @Override
    public SliderBuilder self() {
        return this;
    }

    @Override
    protected SliderWidget construct() {
        return new SliderWidget(this);
    }
}
