package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;

import java.util.List;

class IconSync<Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>>
        implements DynamicFunction<Builder, Input> {
    @Override
    public void apply(Input input, Builder builder) {
        input.icon.apply(input::setIconSize);
    }

    @Override
    public boolean isReapplyNeeded(Input input, Builder builder, WidgetCache cache) {
        return cache.height.isExpired();
    }

    @Override
    public List<DynamicField> getManaging(Builder builder) {
        return List.of();
    }
}
