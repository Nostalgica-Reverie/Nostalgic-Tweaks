package mod.adrenix.nostalgic.client.gui.widget.dynamic;

public interface RelativeLayout {
    /**
     * @return The relative parent's current x-coordinate.
     */
    int getRelativeX(DynamicWidget<?, ?> widget);

    /**
     * @return The relative parent's current y-coordinate.
     */
    int getRelativeY(DynamicWidget<?, ?> widget);

    /**
     * Use in situations that need a static relative position. For example, widgets that respond to a scrollbar.
     *
     * @return The relative parent's anchored (static) x-coordinate.
     */
    int getAnchoredX(DynamicWidget<?, ?> widget);

    /**
     * Use in situations that need a static relative position. For example, widgets that respond to a scrollbar.
     *
     * @return The relative parent's anchored (static) y-coordinate.
     */
    int getAnchoredY(DynamicWidget<?, ?> widget);
}
