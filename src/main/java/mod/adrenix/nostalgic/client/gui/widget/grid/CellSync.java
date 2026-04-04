package mod.adrenix.nostalgic.client.gui.widget.grid;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.data.CacheValue;

import java.util.List;

public class CellSync implements DynamicFunction<CellBuilder, Cell> {
    @Override
    public void apply(Cell cell, CellBuilder builder) {
        builder.grid.ifPresent(grid -> grid.isRealignNeeded = true);

        cell.cache.visible.update();
        cell.cache.active.update();
    }

    @Override
    public boolean isReapplyNeeded(Cell cell, CellBuilder builder, WidgetCache cache) {
        DynamicWidget<?, ?> widget = cell.getWidget();

        if (widget.getBuilder() instanceof LayoutBuilder<?, ?> layoutBuilder) {
            if (builder.grid.isPresent() && builder.grid.getOrThrow().isAnchored())
                layoutBuilder.anchor();
            else
                layoutBuilder.removeAnchor();
        }

        return CacheValue.isAnyExpired(cache.visible, cache.active, widget.cache.visible, widget.cache.active);
    }

    @Override
    public List<DynamicField> getManaging(CellBuilder builder) {
        return List.of();
    }
}
