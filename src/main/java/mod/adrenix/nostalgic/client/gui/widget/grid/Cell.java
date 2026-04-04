package mod.adrenix.nostalgic.client.gui.widget.grid;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;

public class Cell extends DynamicWidget<CellBuilder, Cell> {
    /* Builders */

    final DynamicWidget<?, ?> widget;

    /* Fields */

    protected Cell(CellBuilder builder) {
        super(builder);

        this.widget = builder.widget;

        if (this.widget.getBuilder() instanceof LayoutBuilder<?, ?> layout) {
            layout.posX(this::getAlignmentX);
            layout.posY(this::getAlignmentY);
            layout.width(this::getAlignmentWidth);

            if (!builder.useWidgetHeight)
                layout.height(this::getAlignmentHeight);
        }

        builder.visibleIf(this::getVisibility);
        builder.enableIf(this::getActivity);

        builder.addFollowers(this.widget);
    }

    /* Constructor */

    /**
     * Create a new {@link Cell}.
     *
     * @param widget The {@link DynamicWidget} stored in this cell.
     * @return A new {@link CellBuilder} instance.
     */
    public static CellBuilder create(DynamicWidget<?, ?> widget) {
        return new CellBuilder(widget);
    }

    /* Methods */

    /**
     * @return The {@link DynamicWidget} stored in this cell.
     */
    public DynamicWidget<?, ?> getWidget() {
        return this.widget;
    }

    /**
     * @return Widget alignment on the x-axis.
     */
    private int getAlignmentX() {
        return this.getX() + this.builder.paddingRight;
    }

    /**
     * @return Widget alignment on the y-axis.
     */
    private int getAlignmentY() {
        return this.getY() + this.builder.paddingTop;
    }

    /**
     * @return Widget width alignment.
     */
    private int getAlignmentWidth() {
        return this.getWidth() - this.builder.paddingLeft - this.builder.paddingRight;
    }

    /**
     * @return Widget height alignment.
     */
    private int getAlignmentHeight() {
        return this.getHeight() - this.builder.paddingTop - this.builder.paddingBottom;
    }

    /**
     * @return Whether the cell is visible based on grid or widget overrides.
     */
    private boolean getVisibility() {
        if (this.builder.grid.isPresent() && this.builder.grid.getOrThrow().isInvisible()) {
            this.widget.setInvisible();
            return false;
        } else
            this.widget.setVisible();

        this.widget.setVisible(this.widget.getVisibleTest());

        return this.widget.isVisible();
    }

    /**
     * @return Whether the cell is active based on grid or widget overrides.
     */
    private boolean getActivity() {
        if (this.builder.grid.isPresent() && this.builder.grid.getOrThrow().isInactive()) {
            this.widget.setInactive();
            return false;
        } else
            this.widget.setActive();

        this.widget.setActive(this.widget.getActiveTest());

        return this.widget.isActive();
    }
}
