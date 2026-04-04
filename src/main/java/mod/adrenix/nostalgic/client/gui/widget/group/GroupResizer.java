package mod.adrenix.nostalgic.client.gui.widget.group;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;

import java.util.List;

class GroupResizer implements DynamicFunction<GroupBuilder, Group> {
    private final RecursionAvoidance sync = RecursionAvoidance.create();
    private int resizedCache = 0;

    private int getHeight(Group group) {
        DynamicWidget.syncWithoutCache(group.widgets);

        int padding = group.isBordered() ? 9 : 0;
        int maxEndY = group.getVisibleWidgets().mapToInt(DynamicWidget::getEndY).max().orElse(0);
        int offset = group.getBuilder().bottomOffset + group.getBuilder().paddingBottom;

        return Math.max(Math.abs(maxEndY + padding + offset - group.getY()), 20);
    }

    private boolean isResizeNeeded(Group group, WidgetCache cache) {
        this.resizedCache = this.getHeight(group);

        if (group.getBuilder().isHeightOverridden() && group.getHeight() >= this.resizedCache)
            return false;

        boolean isHeightChanged = group.getHeight() != this.resizedCache;
        boolean isCacheChanged = CacheValue.isAnyExpired(cache.width, cache.height);

        return isHeightChanged || isCacheChanged;
    }

    @Override
    public void apply(Group group, GroupBuilder builder) {
        group.setHeight(this.resizedCache);
        sync.process(builder::sync);
    }

    @Override
    public boolean isReapplyNeeded(Group group, GroupBuilder builder, WidgetCache cache) {
        if (group.getVisibleWidgets().findAny().isEmpty())
            return false;

        return this.isResizeNeeded(group, cache);
    }

    @Override
    public List<DynamicField> getManaging(GroupBuilder builder) {
        return List.of(DynamicField.HEIGHT);
    }

    @Override
    public DynamicPriority priority() {
        return DynamicPriority.HIGH;
    }
}
