package mod.adrenix.nostalgic.client.gui.widget.scrollbar;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.util.common.data.CacheValue;

import java.util.List;

class ScrollbarContent implements DynamicFunction<ScrollbarBuilder, Scrollbar> {
    private final CacheValue<Integer> sizeCache;

    ScrollbarContent(CacheValue<Integer> sizeCache) {
        this.sizeCache = sizeCache;
    }

    public int getSize() {
        return this.sizeCache.last();
    }

    public void update() {
        this.sizeCache.update();
    }

    @Override
    public void apply(Scrollbar widget, ScrollbarBuilder builder) {
    }

    @Override
    public boolean isReapplyNeeded(Scrollbar widget, ScrollbarBuilder builder, WidgetCache cache) {
        this.sizeCache.update();

        return false;
    }

    @Override
    public List<DynamicField> getManaging(ScrollbarBuilder builder) {
        return List.of();
    }
}
