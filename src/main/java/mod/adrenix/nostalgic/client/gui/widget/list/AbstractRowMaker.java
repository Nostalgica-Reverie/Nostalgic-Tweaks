package mod.adrenix.nostalgic.client.gui.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.ActiveBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.VisibleBuilder;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import net.minecraft.util.Mth;

public abstract class AbstractRowMaker<Builder extends AbstractRowMaker<Builder, Row>, Row extends AbstractRow<Builder, Row>>
        extends DynamicBuilder<Builder, Row> implements ActiveBuilder<Builder, Row>, VisibleBuilder<Builder, Row> {
    /* Fields */

    protected final RowList rowList;
    protected final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    protected RowRenderer<Builder, Row> preRenderer = null;
    protected RowRenderer<Builder, Row> postRenderer = null;
    protected RowRenderer<Builder, Row> hiddenRenderer = null;
    protected Animation highlightAnimation = null;
    protected Color highlightColor = null;
    protected double highlightAlpha = 0.0D;
    protected boolean ignoreHighlight = false;
    protected int heightOverflowMargin;
    protected int indent = 0;

    /* Constructor */

    protected AbstractRowMaker(RowList rowList) {
        this.rowList = rowList;
        this.widgets = new UniqueArrayList<>();
        this.canFocus = BooleanSupplier.NEVER;
        this.defaultWidth = rowList.getRowWidth();
        this.heightOverflowMargin = rowList.getBuilder().heightOverflowMargin;
    }

    /* Methods */

    /**
     * Define a widget to be added to the row when it is built.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public Builder addWidget(DynamicWidget<?, ?> widget) {
        this.widgets.add(widget);

        return this.self();
    }

    /**
     * Define the indent offset from the starting x-position of the row list.
     *
     * @param indent The indent offset for this row.
     */
    @PublicAPI
    public Builder indent(int indent) {
        this.indent = indent;

        return this.self();
    }

    /**
     * Change the extra margin added to the height of a row when there is a widget with an ending y-position greater
     * than that of the row. The default margin added is zero.
     *
     * @param margin The height overflow margin.
     */
    @PublicAPI
    public Builder heightOverflowMargin(int margin) {
        this.heightOverflowMargin = margin;

        return this.self();
    }

    /**
     * Set highlighting instructions for when the mouse is over the row.
     *
     * @param highlightAlpha     A normalized maximum alpha transparency of the row [0.0D, 1.0D].
     * @param highlightAnimation The {@link Animation} for highlighting.
     */
    @PublicAPI
    public Builder highlight(double highlightAlpha, Animation highlightAnimation) {
        this.highlightAlpha = Mth.clamp(highlightAlpha, 0.0D, 1.0D);
        this.highlightAnimation = highlightAnimation;

        return this.self();
    }

    /**
     * Set the highlight color for this row.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public Builder highlightColor(Color color) {
        this.highlightColor = color;

        return this.self();
    }

    /**
     * Ignore highlighting rules from the row list and this row builder.
     */
    @PublicAPI
    public Builder useEmptyHighlight() {
        this.ignoreHighlight = true;

        return this.self();
    }

    /**
     * Provide custom rendering instructions to perform before the row starts rendering.
     *
     * @param renderer A custom renderer consumer that accepts the required parameters.
     */
    @PublicAPI
    public Builder preRenderer(RowRenderer<Builder, Row> renderer) {
        this.preRenderer = renderer;

        return this.self();
    }

    /**
     * Provide custom rendering instructions to perform after the row finishes rendering.
     *
     * @param renderer A custom renderer consumer that accepts the required parameters.
     */
    @PublicAPI
    public Builder postRenderer(RowRenderer<Builder, Row> renderer) {
        this.postRenderer = renderer;

        return this.self();
    }

    /**
     * Provide custom rendering instructions to perform when the row is no longer rendered by the row list. Useful if
     * rendered visuals continue well outside a row's boundaries.
     *
     * @param renderer A custom renderer consumer that accepts the required parameters.
     */
    @PublicAPI
    public Builder hiddenRenderer(RowRenderer<Builder, Row> renderer) {
        this.hiddenRenderer = renderer;

        return this.self();
    }
}
