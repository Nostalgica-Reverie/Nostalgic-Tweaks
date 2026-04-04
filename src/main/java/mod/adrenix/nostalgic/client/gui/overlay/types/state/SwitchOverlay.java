package mod.adrenix.nostalgic.client.gui.overlay.types.state;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.List;

/**
 * A {@link SwitchOverlay} contains a {@link List} of {@link SwitchGroup} instances. These groups will have a toggle
 * switch that changes a state. This helper class provides an easy way to quickly define a list of states that need
 * managed by a user interface.
 */
public class SwitchOverlay {
    /* Fields */

    private final Overlay overlay;

    /* Constructor */

    private SwitchOverlay(LayoutElement aboveOrBelow) {
        this.overlay = Overlay.create()
                .setWidth(200)
                .padding(6)
                .aboveOrBelow(aboveOrBelow, 3)
                .backgroundColor(new Color(0, 0, 0, 235))
                .shadowColor(new Color(0, 0, 0, 80))
                .scissorPadding(3)
                .resizeHeightForWidgets()
                .unmovable()
                .borderless()
                .build();
    }

    /* Static */

    /**
     * Create a new {@link Overlay} that will be above the given {@link LayoutElement} and contains the groups that are
     * defined in the given {@link List} of {@link SwitchGroup}.
     *
     * @param aboveOrBelow A {@link LayoutElement} instance.
     * @return A new {@link SwitchOverlay} instance.
     */
    public static SwitchOverlay create(LayoutElement aboveOrBelow) {
        return new SwitchOverlay(aboveOrBelow);
    }

    /* Methods */

    /**
     * Open the filtering overlay.
     */
    public void open() {
        this.overlay.open();
    }

    /**
     * @return The {@link Overlay} instance that controls the {@link SwitchGroup} instances.
     */
    public Overlay get() {
        return this.overlay;
    }

    /**
     * Set the groups being used by this overlay.
     *
     * @param groups A {@link List} of {@link SwitchGroup}.
     */
    public void setGroups(List<SwitchGroup> groups) {
        ForEachWithPrevious.create(groups)
                .applyToFirst(this::subscribe)
                .applyToLast(this::debar)
                .forEach(this::setup)
                .run();
    }

    /**
     * Subscribe a {@link SwitchGroup} to the {@link Overlay}.
     *
     * @param group A {@link SwitchGroup} instance.
     */
    private void subscribe(SwitchGroup group) {
        this.overlay.addWidget(group.getInstance());
    }

    /**
     * Removes the {@link SeparatorWidget} at the bottom of a {@link SwitchGroup} instance.
     *
     * @param group A {@link SwitchGroup} instance.
     */
    private void debar(SwitchGroup group) {
        group.getInstance()
                .getWidgetStream()
                .filter(widget -> widget instanceof SeparatorWidget)
                .findFirst()
                .ifPresent(separator -> group.getInstance().removeWidget(separator));
    }

    /**
     * Sets up groups based on the previous and next group instances.
     *
     * @param previous The last {@link SwitchGroup}.
     * @param next     The next {@link SwitchGroup}.
     */
    private void setup(SwitchGroup previous, SwitchGroup next) {
        this.subscribe(next);
        next.getInstance().getBuilder().below(previous.getInstance(), 4);
    }
}
