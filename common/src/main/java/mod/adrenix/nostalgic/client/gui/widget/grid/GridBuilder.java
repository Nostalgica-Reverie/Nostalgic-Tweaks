package mod.adrenix.nostalgic.client.gui.widget.grid;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

public class GridBuilder extends DynamicBuilder<GridBuilder, Grid>
    implements LayoutBuilder<GridBuilder, Grid>, ActiveBuilder<GridBuilder, Grid>, VisibleBuilder<GridBuilder, Grid>
{
    /* Fields */

    final WidgetHolder parent;
    final ToIntFunction<Grid> cellsPerRow;
    final UniqueArrayList<Cell> cells;

    ToIntFunction<Grid> rowSpacing = (grid) -> 1;
    ToIntFunction<Grid> columnSpacing = (grid) -> 1;

    boolean extendLastCell = false;
    boolean alignRowHeights = false;
    boolean alignAllCells = false;
    boolean useWidgetHeight = false;
    int cellPadding = 0;

    /* Constructor */

    protected GridBuilder(WidgetHolder parent, ToIntFunction<Grid> cellsPerRow)
    {
        this.parent = parent;
        this.cellsPerRow = cellsPerRow;

        this.cells = new UniqueArrayList<>();
        this.canFocus = BooleanSupplier.NEVER;

        this.addFunction(new GridSync());
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public GridBuilder self()
    {
        return this;
    }

    /**
     * Add a cell to the grid.
     *
     * @param cell A {@link Cell} instance.
     */
    @PublicAPI
    public GridBuilder addCell(Cell cell)
    {
        this.cells.add(cell);
        this.followers.add(cell);
        this.parent.addWidgets(cell.widget);

        cell.getBuilder().grid = this.widget;

        if (this.widget.isPresent())
            this.widget.getOrThrow().alignCells();

        return this;
    }

    /**
     * Add widgets to the grid. The padding within the cells will use the properties set by {@link #cellPadding(int)}.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    public GridBuilder addCells(DynamicWidget<?, ?>... widgets)
    {
        for (DynamicWidget<?, ?> widget : widgets)
            this.addCell(widget);

        return this;
    }

    /**
     * Begin the process of making a new cell.
     *
     * @param widget The {@link DynamicWidget} this cell will maintain.
     * @return A new {@link CellBuilder} instance.
     */
    private CellBuilder makeCell(DynamicWidget<?, ?> widget)
    {
        CellBuilder builder = Cell.create(widget);

        if (this.useWidgetHeight)
            builder.height(() -> widget.getHeight() + builder.paddingTop + builder.paddingBottom);

        return builder;
    }

    /**
     * Add a widget to the grid. The padding within the cell will use the properties set by {@link #cellPadding(int)}.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public GridBuilder addCell(DynamicWidget<?, ?> widget)
    {
        return this.addCell(this.makeCell(widget).padding(this.cellPadding).build());
    }

    /**
     * Add a widget to the grid with the given inner-cell padding.
     *
     * @param widget  A {@link DynamicWidget} instance.
     * @param padding The cell's inner-padding.
     */
    @PublicAPI
    public GridBuilder addCell(DynamicWidget<?, ?> widget, int padding)
    {
        return this.addCell(this.makeCell(widget).padding(padding).build());
    }

    /**
     * Add a widget to the grid with the given inner-cell padding dimensions.
     *
     * @param widget        A {@link DynamicWidget} instance.
     * @param paddingLeft   The padding on the left.
     * @param paddingTop    The padding from the top.
     * @param paddingRight  The padding on the right.
     * @param paddingBottom The padding from the bottom.
     */
    @PublicAPI
    public GridBuilder addCell(DynamicWidget<?, ?> widget, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom)
    {
        return this.addCell(this.makeCell(widget)
            .padding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            .build());
    }

    /**
     * Use the widget's height for the cell. This builder property must be defined before adding cells/widgets.
     */
    @PublicAPI
    public GridBuilder useWidgetHeight()
    {
        this.useWidgetHeight = true;

        return this;
    }

    /**
     * Define the padding applied to the inner-cell. This builder property must be defined before adding cells/widgets.
     *
     * @param padding All cell's inner-padding.
     */
    @PublicAPI
    public GridBuilder cellPadding(int padding)
    {
        this.cellPadding = padding;

        return this;
    }

    /**
     * Align every cell's height within the grid so that the cell's height is the same as the cell with the greatest
     * height.
     */
    @PublicAPI
    public GridBuilder alignAllCells()
    {
        this.alignAllCells = true;

        return this;
    }

    /**
     * Align every cell's height within a row so that all cells are flush with the cell that has the greatest height.
     */
    @PublicAPI
    public GridBuilder alignRowHeights()
    {
        this.alignRowHeights = true;

        return this;
    }

    /**
     * If the last cell in the grid is not in the last grid column, then extend the cell's width so that it is flush
     * with the grid's width.
     */
    @PublicAPI
    public GridBuilder extendLastCell()
    {
        this.extendLastCell = true;

        return this;
    }

    /**
     * Change the spacing between rows.
     *
     * @param spacing A {@link ToIntFunction} that accepts the built {@link Grid} and provides row spacing.
     */
    @PublicAPI
    public GridBuilder rowSpacing(ToIntFunction<Grid> spacing)
    {
        this.rowSpacing = spacing;

        return this;
    }

    /**
     * Change the spacing between rows.
     *
     * @param spacing A {@link IntSupplier} that provides row spacing.
     */
    @PublicAPI
    public GridBuilder rowSpacing(IntSupplier spacing)
    {
        return this.rowSpacing(grid -> spacing.getAsInt());
    }

    /**
     * Change the spacing between rows.
     *
     * @param spacing The spacing between rows.
     */
    @PublicAPI
    public GridBuilder rowSpacing(int spacing)
    {
        return this.rowSpacing(() -> spacing);
    }

    /**
     * Change the spacing between columns.
     *
     * @param spacing A {@link ToIntFunction} that accepts the built {@link Grid} and provides column spacing.
     */
    @PublicAPI
    public GridBuilder columnSpacing(ToIntFunction<Grid> spacing)
    {
        this.columnSpacing = spacing;

        return this;
    }

    /**
     * Change the spacing between columns.
     *
     * @param spacing A {@link IntSupplier} that provides column spacing.
     */
    @PublicAPI
    public GridBuilder columnSpacing(IntSupplier spacing)
    {
        return this.columnSpacing(grid -> spacing.getAsInt());
    }

    /**
     * Change the spacing between columns.
     *
     * @param spacing The spacing between columns.
     */
    @PublicAPI
    public GridBuilder columnSpacing(int spacing)
    {
        return this.columnSpacing(() -> spacing);
    }

    /**
     * Change the spacing between rows and columns.
     *
     * @param spacing A {@link ToIntFunction} that accepts the built {@link Grid} and provides row and column spacing.
     */
    @PublicAPI
    public GridBuilder spacing(ToIntFunction<Grid> spacing)
    {
        return this.rowSpacing(spacing).columnSpacing(spacing);
    }

    /**
     * Change the spacing between rows and columns.
     *
     * @param spacing A {@link IntSupplier} that provides row and column spacing.
     */
    @PublicAPI
    public GridBuilder spacing(IntSupplier spacing)
    {
        return this.rowSpacing(spacing).columnSpacing(spacing);
    }

    /**
     * Change the spacing between rows and columns.
     *
     * @param spacing The spacing between rows and columns.
     */
    @PublicAPI
    public GridBuilder spacing(int spacing)
    {
        return this.rowSpacing(spacing).columnSpacing(spacing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Grid construct()
    {
        return new Grid(this);
    }
}
