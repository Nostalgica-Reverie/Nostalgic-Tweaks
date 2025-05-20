package mod.adrenix.nostalgic.client.gui.widget.grid;

import mod.adrenix.nostalgic.client.gui.PaddingManager;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

public class CellBuilder extends DynamicBuilder<CellBuilder, Cell>
    implements LayoutBuilder<CellBuilder, Cell>, ActiveBuilder<CellBuilder, Cell>, VisibleBuilder<CellBuilder, Cell>,
               PaddingManager<CellBuilder>
{
    /* Fields */

    final DynamicWidget<?, ?> widget;
    NullableHolder<Grid> grid;

    int paddingTop = 0;
    int paddingBottom = 0;
    int paddingLeft = 0;
    int paddingRight = 0;
    boolean useWidgetHeight = false;

    /* Constructor */

    protected CellBuilder(DynamicWidget<?, ?> widget)
    {
        this.widget = widget;
        this.canFocus = BooleanSupplier.NEVER;
        this.grid = NullableHolder.empty();

        this.addFunction(new CellSync());
    }

    /* Methods */

    /**
     * Set the padding between the widget and the top of this cell.
     *
     * @param padding The padding amount.
     */
    @Override
    public CellBuilder paddingTop(int padding)
    {
        this.paddingTop = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the bottom of this cell.
     *
     * @param padding The padding amount.
     */
    @Override
    public CellBuilder paddingBottom(int padding)
    {
        this.paddingBottom = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the left side of this cell.
     *
     * @param padding The padding amount.
     */
    @Override
    public CellBuilder paddingLeft(int padding)
    {
        this.paddingLeft = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the right side of this cell.
     *
     * @param padding The padding amount.
     */
    @Override
    public CellBuilder paddingRight(int padding)
    {
        this.paddingRight = padding;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellBuilder self()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Cell construct()
    {
        return new Cell(this);
    }
}
