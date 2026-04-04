package mod.adrenix.nostalgic.client.gui.widget.grid;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicField;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicFunction;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicPriority;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetCache;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;

import java.util.List;

public class GridSync implements DynamicFunction<GridBuilder, Grid> {
    private final RecursionAvoidance aligner = RecursionAvoidance.create();

    @Override
    public void apply(Grid grid, GridBuilder builder) {
        grid.alignCells();

        if (grid.isRealignNeeded)
            grid.realign();

        this.aligner.process(builder::sync);
    }

    @Override
    public boolean isReapplyNeeded(Grid grid, GridBuilder builder, WidgetCache cache) {
        if (this.aligner.isProcessing())
            return false;

        if (grid.cellsPerRow != builder.cellsPerRow.applyAsInt(grid))
            return true;

        return CacheValue.isAnyExpired(cache.width, cache.height, cache.visible);
    }

    @Override
    public List<DynamicField> getManaging(GridBuilder builder) {
        return List.of();
    }

    @Override
    public DynamicPriority priority() {
        return DynamicPriority.HIGH;
    }
}
