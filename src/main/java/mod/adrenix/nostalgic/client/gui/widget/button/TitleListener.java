package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicPriority;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.util.common.data.CacheValue;

import java.util.List;

class TitleListener<Builder extends AbstractButtonMaker<Builder, Button>, Button extends AbstractButton<Builder, Button>>
        implements DynamicFunction<Builder, Button> {
    @Override
    public void apply(Button button, Builder builder) {
        button.setTextAndIconPos();
        button.cacheTitle.update();
    }

    @Override
    public boolean isReapplyNeeded(Button button, Builder builder, WidgetCache cache) {
        boolean isLayoutChanged = CacheValue.isAnyExpired(cache.x, cache.y, cache.width, cache.height);
        boolean isTitleChanged = button.cacheTitle.isExpired();

        return isLayoutChanged || isTitleChanged;
    }

    @Override
    public List<DynamicField> getManaging(Builder builder) {
        return List.of();
    }

    @Override
    public DynamicPriority priority() {
        return DynamicPriority.HIGH;
    }
}
