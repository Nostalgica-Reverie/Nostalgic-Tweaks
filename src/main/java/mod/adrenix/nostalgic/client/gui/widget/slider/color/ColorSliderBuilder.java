package mod.adrenix.nostalgic.client.gui.widget.slider.color;

import mod.adrenix.nostalgic.client.gui.widget.slider.AbstractSliderMaker;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.ColorElement;

public class ColorSliderBuilder extends AbstractSliderMaker<ColorSliderBuilder, ColorSlider> {
    /* Fields */

    protected final Color color;
    protected final ColorElement element;

    /* Constructor */

    protected ColorSliderBuilder(Color color, ColorElement element) {
        super(element.getMin(), element.getMax());

        this.color = color;
        this.element = element;

        this.valueConsumer = number -> this.element.apply(number, this.color);
        this.valueSupplier = () -> this.element.getValue(this.color);
        this.interval = this.element::getInterval;
    }

    /* Methods */

    @Override
    public ColorSliderBuilder self() {
        return this;
    }

    @Override
    protected ColorSlider construct() {
        return new ColorSlider(this);
    }
}
