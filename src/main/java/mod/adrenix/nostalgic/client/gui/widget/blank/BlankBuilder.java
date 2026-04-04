package mod.adrenix.nostalgic.client.gui.widget.blank;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.TooltipBuilder;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

public class BlankBuilder extends DynamicBuilder<BlankBuilder, BlankWidget>
        implements LayoutBuilder<BlankBuilder, BlankWidget>, TooltipBuilder<BlankBuilder, BlankWidget> {
    /* Fields */

    protected boolean useClickSound = true;
    BlankRenderer renderer = BlankRenderer.EMPTY;
    Runnable onPress = null;

    /* Constructor */

    protected BlankBuilder() {
        this.defaultWidth = 1;
        this.defaultHeight = 1;
        this.canFocus = BooleanSupplier.NEVER;
    }

    /* Methods */

    @Override
    public BlankBuilder self() {
        return this;
    }

    /**
     * Set custom rendering instructions for this {@link BlankWidget}.
     *
     * @param renderer A {@link BlankRenderer} instance.
     */
    @PublicAPI
    public BlankBuilder renderer(BlankRenderer renderer) {
        this.renderer = renderer;

        return this.self();
    }

    /**
     * Disable the clicking sound that plays when the on-press action is performed.
     *
     * @see #onPress(Runnable)
     */
    @PublicAPI
    public BlankBuilder noClickSound() {
        this.useClickSound = false;

        return this.self();
    }

    /**
     * Run the given instructions when this widget is clicked.
     *
     * @param onPress The {@link Runnable} to run on click.
     */
    @PublicAPI
    public BlankBuilder onPress(Runnable onPress) {
        this.onPress = onPress;

        return this.self();
    }

    @Override
    protected BlankWidget construct() {
        return new BlankWidget(this);
    }
}
