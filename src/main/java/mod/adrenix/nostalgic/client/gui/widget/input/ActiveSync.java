package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;

import java.util.List;

public class ActiveSync<Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>>
        implements DynamicFunction<Builder, Input> {
    @Override
    public void apply(Input widget, Builder builder) {
        if (widget.isInactive())
            widget.internal.forEach(DynamicWidget::setInactive);
        else
            widget.internal.forEach(DynamicWidget::setActive);
    }

    @Override
    public boolean isReapplyNeeded(Input widget, Builder builder, WidgetCache cache) {
        return cache.active.isExpired();
    }

    @Override
    public List<DynamicField> getManaging(Builder builder) {
        return List.of();
    }
}
