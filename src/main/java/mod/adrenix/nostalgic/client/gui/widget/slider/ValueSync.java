package mod.adrenix.nostalgic.client.gui.widget.slider;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.util.common.data.CacheValue;

import java.util.List;

record ValueSync<Builder extends AbstractSliderMaker<Builder, Slider>, Slider extends AbstractSlider<Builder, Slider>>(
        CacheValue<Double> value)
        implements DynamicFunction<Builder, Slider> {
    ValueSync(Slider value) {
        this(CacheValue.create(value::getValue));
    }

    @Override
    public void apply(Slider slider, Builder builder) {
        if (!slider.dragging)
            slider.setValue(slider.getValue());

        slider.applyTitle();

        this.value.update();
    }

    @Override
    public boolean isReapplyNeeded(Slider slider, Builder builder, WidgetCache cache) {
        return this.value.isExpired();
    }

    @Override
    public List<DynamicField> getManaging(Builder builder) {
        return List.of();
    }
}
