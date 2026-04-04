package mod.adrenix.nostalgic.client.gui.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.data.CacheValue;

import java.util.List;

class RowResizer<Builder extends AbstractRowMaker<Builder, Row>, Row extends AbstractRow<Builder, Row>>
        implements DynamicFunction<Builder, Row> {
    private int resizedCache = 0;

    private int getHeight(Row row) {
        int defaultHeight = row.getRowList().getBuilder().defaultRowHeight;
        int margin = row.getBuilder().heightOverflowMargin;
        int maxEndY = row.getVisibleWidgets()
                .filter(widget -> !widget.has(RowData.WIDGET_SYNCED_WITH_HEIGHT))
                .mapToInt(DynamicWidget::getEndY)
                .max()
                .orElse(0);

        int widgetHeight = Math.abs(row.getY() - maxEndY) + margin;

        if (widgetHeight == 0 || widgetHeight <= defaultHeight)
            return defaultHeight;

        return widgetHeight;
    }

    private boolean isResizeNeeded(Row row, WidgetCache cache) {
        if (row.isInvisible() || !row.initialized)
            return false;

        this.resizedCache = this.getHeight(row);
        boolean isHeightChanged = row.getHeight() != this.resizedCache;
        boolean isCacheChanged = CacheValue.isAnyExpired(cache.width, cache.height);

        return isHeightChanged || isCacheChanged;
    }

    @Override
    public void apply(Row row, Builder builder) {
        row.setHeight(this.resizedCache);
    }

    @Override
    public boolean isReapplyNeeded(Row row, Builder builder, WidgetCache cache) {
        return this.isResizeNeeded(row, cache);
    }

    @Override
    public List<DynamicField> getManaging(Builder builder) {
        return List.of(DynamicField.HEIGHT);
    }

    @Override
    public DynamicPriority priority() {
        return DynamicPriority.HIGH;
    }
}
