package mod.adrenix.nostalgic.client.gui.widget.separator;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.VisibleBuilder;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

public class SeparatorBuilder extends DynamicBuilder<SeparatorBuilder, SeparatorWidget>
        implements LayoutBuilder<SeparatorBuilder, SeparatorWidget>, VisibleBuilder<SeparatorBuilder, SeparatorWidget> {
    /* Fields */

    final Color color;

    /* Constructor */

    protected SeparatorBuilder(Color color) {
        this.color = color;
        this.canFocus = BooleanSupplier.NEVER;
        this.defaultHeight = 2;
    }

    /* Methods */

    @Override
    public SeparatorBuilder self() {
        return this;
    }

    @Override
    protected SeparatorWidget construct() {
        return new SeparatorWidget(this);
    }
}
