package mod.adrenix.nostalgic.client.gui.widget.grid;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.function.ForEachWithPrevious;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class Grid extends DynamicWidget<GridBuilder, Grid>
{
    /* Builders */

    /**
     * Create a new {@link Grid} instance.
     *
     * @param parent      The {@link WidgetHolder} parent that will pass events to the {@link DynamicWidget} in cells.
     *                    This grid will add and/or remove widgets from the holder.
     * @param cellsPerRow A {@link ToIntFunction} that accepts the {@link Grid} and provides the number of cells per
     *                    row.
     * @return A new {@link GridBuilder} instance.
     */
    public static GridBuilder create(WidgetHolder parent, ToIntFunction<Grid> cellsPerRow)
    {
        return new GridBuilder(parent, cellsPerRow);
    }

    /**
     * Create a new {@link Grid} instance.
     *
     * @param parent      The {@link WidgetHolder} parent that will pass events to the {@link DynamicWidget} in cells.
     *                    This grid will add and/or remove widgets from the holder.
     * @param cellsPerRow A {@link IntSupplier} that provides the number of cells per row.
     * @return A new {@link GridBuilder} instance.
     */
    public static GridBuilder create(WidgetHolder parent, IntSupplier cellsPerRow)
    {
        return new GridBuilder(parent, grid -> cellsPerRow.getAsInt());
    }

    /**
     * Create a new {@link Grid} instance.
     *
     * @param parent      The {@link WidgetHolder} parent that will pass events to the {@link DynamicWidget} in cells.
     *                    This grid will add and/or remove widgets from the holder.
     * @param cellsPerRow The number of cells per row.
     * @return A new {@link GridBuilder} instance.
     */
    public static GridBuilder create(WidgetHolder parent, int cellsPerRow)
    {
        return new GridBuilder(parent, grid -> cellsPerRow);
    }

    /* Fields */

    private final UniqueArrayList<Cell> cells;

    protected boolean isAlignmentPaused = false;
    protected boolean isRealignNeeded = false;
    protected int cellsPerRow = 0;

    /* Constructor */

    protected Grid(GridBuilder builder)
    {
        super(builder);

        this.cells = builder.cells;
        this.cellsPerRow = builder.cellsPerRow.applyAsInt(this);

        this.alignCells();
    }

    /* Methods */

    /**
     * Add a cell to this grid.
     *
     * @param cell A {@link Cell} instance.
     */
    @PublicAPI
    public void addCell(Cell cell)
    {
        this.builder.addCell(cell);
    }

    /**
     * Add a widget to the grid. The padding within the widget's cell will be set to {@code zero}.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public void addCell(DynamicWidget<?, ?> widget)
    {
        this.builder.addCell(widget);
    }

    /**
     * Add widgets to the grid. The padding within the cells will use the properties set by
     * {@link GridBuilder#cellPadding(int)}.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    public void addCells(DynamicWidget<?, ?>... widgets)
    {
        this.builder.addCells(widgets);
    }

    /**
     * Add a widget to the grid with the given inner-cell padding.
     *
     * @param widget  A {@link DynamicWidget} instance.
     * @param padding The cell's inner-padding.
     */
    @PublicAPI
    public void addCell(DynamicWidget<?, ?> widget, int padding)
    {
        this.builder.addCell(widget, padding);
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
    public void addCell(DynamicWidget<?, ?> widget, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom)
    {
        this.builder.addCell(widget, paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    /**
     * Remove a cell from this grid.
     *
     * @param cell A {@link Cell} instance.
     */
    @PublicAPI
    public void removeCell(Cell cell)
    {
        this.cells.remove(cell);
        this.builder.parent.removeWidget(cell.widget);
    }

    /**
     * Pause cell alignment. This is useful in situations where a lot of cells are being added at a time. For example,
     * run this method add a lot of cells, then {@link #resumeAlignment()}.
     */
    @PublicAPI
    public void pauseAlignment()
    {
        this.isAlignmentPaused = true;
    }

    /**
     * Resume cell alignment.
     *
     * @see #pauseAlignment()
     */
    @PublicAPI
    public void resumeAlignment()
    {
        this.isAlignmentPaused = false;
    }

    /**
     * @return The number of cells subscribed to this grid widget.
     */
    @PublicAPI
    public int size()
    {
        return this.cells.size();
    }

    /**
     * Get a stream of widgets from a row.
     *
     * @param row A {@link List} of {@link Cell}.
     * @return A {@link Stream} of {@link DynamicWidget} from the given row.
     */
    private Stream<DynamicWidget<?, ?>> getWidgetsFromRow(List<Cell> row)
    {
        return row.stream().map(Cell::getWidget);
    }

    /**
     * Set layout properties for widgets stored in the cells in the given row.
     *
     * @param row      A {@link List} of {@link Cell}.
     * @param consumer A {@link Consumer} that accepts a {@link LayoutBuilder}.
     */
    private void setLayoutForCells(List<Cell> row, Consumer<LayoutBuilder<?, ?>> consumer)
    {
        CollectionUtil.fromCast(row.stream().map(DynamicWidget::getBuilder), LayoutBuilder.class)
            .forEach(consumer::accept);
    }

    /**
     * Align all cells stored in this grid.
     */
    void alignCells()
    {
        if (this.isAlignmentPaused)
            return;

        this.cells.stream().map(Cell::getBuilder).forEach(CellBuilder::resetLayout);

        final int cellsPerRow = Math.max(this.builder.cellsPerRow.applyAsInt(this), 1);
        final int rowSpacing = this.builder.rowSpacing.applyAsInt(this);
        final int columnSpacing = this.builder.columnSpacing.applyAsInt(this);
        final int rowWidth = this.getWidth() - (columnSpacing * (cellsPerRow - 1));
        final int cellWidth = Math.round(rowWidth / (float) cellsPerRow);
        final int roundingError = this.getWidth() - (cellWidth * cellsPerRow) - (columnSpacing * (cellsPerRow - 1));

        final List<List<Cell>> rows = CollectionUtil.partition(this.cells.stream()
            .filter(Cell::isVisible), cellsPerRow);

        final int maxCellHeight = rows.stream()
            .flatMap(this::getWidgetsFromRow)
            .mapToInt(DynamicWidget::getHeight)
            .max()
            .orElse(20);

        rows.forEach(row -> {
            int maxRowHeight = row.stream().map(Cell::getWidget).mapToInt(DynamicWidget::getHeight).max().orElse(20);

            if (this.builder.alignAllCells)
                this.setLayoutForCells(row, builder -> builder.height(() -> maxCellHeight));
            else if (this.builder.alignRowHeights)
                this.setLayoutForCells(row, builder -> builder.height(() -> maxRowHeight));

            this.setLayoutForCells(row, builder -> builder.width(() -> cellWidth));

            ForEachWithPrevious.create(row)
                .applyToFirst(cell -> cell.getBuilder().posX(this::getX))
                .applyToLast(cell -> cell.getBuilder().width(() -> cellWidth + roundingError))
                .forEach((lastCell, nextCell) -> nextCell.getBuilder().rightOf(lastCell, columnSpacing))
                .run();
        });

        ForEachWithPrevious.create(rows)
            .forEach((lastRow, nextRow) -> CollectionUtil.first(nextRow)
                .ifPresent(cell -> cell.getBuilder().belowAll(lastRow, rowSpacing)))
            .run();

        CollectionUtil.first(rows)
            .flatMap(CollectionUtil::first)
            .ifPresent(cell -> cell.getBuilder().pos(this::getX, this::getY));

        if (this.builder.extendLastCell)
        {
            CollectionUtil.last(rows).flatMap(CollectionUtil::last).ifPresent(cell -> {
                cell.getBuilder().extendWidthToEnd(this, 0);
                cell.getBuilder().resetWidth();
            });
        }
        else
        {
            CollectionUtil.last(rows).ifPresent(row -> {
                if (row.size() < cellsPerRow)
                    CollectionUtil.last(row).ifPresent(cell -> cell.getBuilder().width(() -> cellWidth));
            });
        }

        DynamicWidget.syncWithoutCache(this.cells.stream().map(Cell::getWidget).toList());
        DynamicWidget.syncWithoutCache(this.cells);

        this.setHeight(this.cells.stream().mapToInt(Cell::getEndY).max().orElse(this.getEndY()) - this.getY());

        this.cellsPerRow = cellsPerRow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean state)
    {
        super.setVisible(state);

        this.cells.forEach(cell -> cell.setVisible(state));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActive(boolean state)
    {
        super.setActive(state);

        this.cells.forEach(cell -> cell.setActive(state));
    }

    /**
     * Realign the grid's cells and subscribed widgets.
     */
    protected void realign()
    {
        DynamicWidget.syncWithoutCache(this.cells.stream().map(Cell::getWidget).toList());
        DynamicWidget.sync(this.cells);

        if (this.isRealignNeeded)
        {
            this.isRealignNeeded = false;
            this.alignCells();

            DynamicWidget.syncWithoutCache(this.cells.stream().map(Cell::getWidget).toList());
            DynamicWidget.sync(this.cells);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        this.realign();
        this.renderDebug(graphics);
    }
}
