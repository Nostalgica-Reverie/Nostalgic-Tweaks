package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import java.util.List;

public interface DynamicFunction<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>> {
    /**
     * Apply changes to the given widget using data from its builder.
     *
     * @param widget  A {@link Widget} instance.
     * @param builder A {@link Builder} instance.
     */
    void apply(Widget widget, Builder builder);

    /**
     * Apply changes to the given widget.
     *
     * @param widget A {@link Widget} instance.
     */
    default void apply(Widget widget) {
        this.apply(widget, widget.builder);
    }

    /**
     * Check if a re-apply of {@link #apply(DynamicWidget, DynamicBuilder)} is needed.
     *
     * @param widget  A {@link Widget} instance.
     * @param builder A {@link Builder} instance.
     * @param cache   A {@link WidgetCache} instance.
     */
    boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache);

    /**
     * If a reapplication of this function is needed, then the function will be reapplied.
     *
     * @param widget A {@link Widget} instance.
     */
    default void ifReapplyThenApply(Widget widget) {
        if (this.isReapplyNeeded(widget, widget.builder, widget.cache))
            this.apply(widget, widget.builder);
    }

    /**
     * @return A {@link List} of {@link DynamicField} this function manages.
     */
    List<DynamicField> getManaging(Builder builder);

    /**
     * @return A {@link DynamicPriority} that defines if these instructions are performed before or after dynamic layout
     * instructions.
     */
    default DynamicPriority priority() {
        return DynamicPriority.LOW;
    }
}
