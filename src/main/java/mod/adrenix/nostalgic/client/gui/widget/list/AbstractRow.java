package mod.adrenix.nostalgic.client.gui.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.RelativeLayout;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractRow<Builder extends AbstractRowMaker<Builder, Row>, Row extends AbstractRow<Builder, Row>>
        extends DynamicWidget<Builder, Row> implements ContainerEventHandler, RelativeLayout, WidgetHolder {
    /* Fields */

    protected final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    protected final RecursionAvoidance pathFinder;
    protected final RowList rowList;
    protected boolean initialized;

    /* Constructor */

    /**
     * Create a new {@link Row} instance using an {@link AbstractRowMaker}.
     *
     * @param builder The {@link AbstractRowMaker} instance.
     */
    protected AbstractRow(Builder builder) {
        super(builder);

        if (builder.highlightColor == null)
            builder.highlightColor = Color.WHITE;

        this.widgets = builder.widgets;
        this.rowList = builder.rowList;
        this.pathFinder = RecursionAvoidance.create();

        this.setScreen(this.rowList.getScreen());
        this.getBuilder().addFunction(new RowResizer<>());

        this.widgets.stream()
                .map(DynamicWidget::getBuilder)
                .forEach(dynamic -> dynamic.whenFocused(() -> this.rowList.setScrollOn(this)));

        CollectionUtil.fromCast(this.getWidgetStream().map(DynamicWidget::getBuilder), LayoutBuilder.class)
                .forEach(layout -> layout.relativeTo(this));

        this.widgets.forEach(widget -> widget.setScreen(this.rowList.getScreen()));
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets() {
        return this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<? extends GuiEventListener> children() {
        return this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        if (event instanceof FocusNavigationEvent.TabNavigation)
            return this.pathFinder.process(() -> ContainerEventHandler.super.nextFocusPath(event));

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable GuiEventListener getFocused() {
        return this.rowList.focusedListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.rowList.setFocused(focused);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ComponentPath getCurrentFocusPath() {
        return ContainerEventHandler.super.getCurrentFocusPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDragging() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDragging(boolean isDragging) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addWidget(DynamicWidget<?, ?> widget) {
        widget.setScreen(this.rowList.getScreen());

        if (widget.getBuilder() instanceof LayoutBuilder<?, ?> layout)
            layout.relativeTo(this);

        this.widgets.add(widget);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWidget(DynamicWidget<?, ?> widget) {
        this.widgets.remove(widget);
    }

    /**
     * Move the given widget to the front of the row's widget collection.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public void moveToFront(DynamicWidget<?, ?> widget) {
        if (!this.widgets.contains(widget))
            this.addWidget(widget);

        this.widgets.remove(widget);
        this.widgets.addFirst(widget);
    }

    /**
     * Clears the row's widgets from its parent and then the row clears its own widgets list.
     */
    @PublicAPI
    public void clear() {
        this.widgets.clear();
    }

    /**
     * @return The {@link RowList} instance that created this row.
     */
    @PublicAPI
    public RowList getRowList() {
        return this.rowList;
    }

    /**
     * @return An {@link Optional} highlight {@link Animation} for this row.
     */
    @PublicAPI
    public Optional<Animation> getHighlightAnimation() {
        return Optional.ofNullable(this.getBuilder().highlightAnimation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelativeX(DynamicWidget<?, ?> widget) {
        return this.getX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelativeY(DynamicWidget<?, ?> widget) {
        return this.getY();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnchoredX(DynamicWidget<?, ?> widget) {
        return this.getX();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnchoredY(DynamicWidget<?, ?> widget) {
        return this.getY();
    }

    /**
     * @return Get the height of this row with the row list margin added on.
     */
    @PublicAPI
    public int getHeightWithMargin() {
        int heightWithMargin = this.getHeight() + this.rowList.getRowMargin();

        if (this.rowList.getLastVisibleRow().stream().anyMatch(row -> row.equals(this)))
            heightWithMargin -= this.rowList.getRowSeparatorMargin();

        return heightWithMargin;
    }

    /**
     * @return The default row height defined by the {@link RowList}.
     */
    @PublicAPI
    public int getDefaultRowHeight() {
        return this.rowList.getBuilder().defaultRowHeight;
    }

    /**
     * @return The mouse over highlight color.
     */
    @PublicAPI
    public Color getHighlightColor() {
        return this.getBuilder().highlightColor;
    }

    /**
     * A normalized final alpha transparency value of the row [0.0D, 1.0D].
     *
     * @return The maximum alpha transparency of the row when the mouse is over this row.
     */
    @PublicAPI
    public double getHighlightAlpha() {
        return this.getBuilder().highlightAlpha;
    }

    /**
     * @return The indent x-coordinate offset for this row.
     */
    @PublicAPI
    public int getIndent() {
        return this.getBuilder().indent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        boolean isMouseOverList = this.rowList.isMouseOver(mouseX, mouseY);
        boolean isMouseOverRow = MathUtil.isWithinBox(mouseX, mouseY, this.getX(), this.getY(), this.getWidth(), this.getHeight());

        return isMouseOverList && isMouseOverRow;
    }

    /**
     * Find a widget at the given mouse point.
     *
     * @param mouseX The x-position of the mouse.
     * @param mouseY The y-position of the mouse.
     * @return An {@link Optional} {@link DynamicWidget} instance.
     */
    @PublicAPI
    public Optional<DynamicWidget<?, ?>> getWidgetAtPoint(double mouseX, double mouseY) {
        return this.getVisibleWidgets().filter(widget -> widget.isMouseOver(mouseX, mouseY)).findFirst();
    }

    /**
     * @return An {@link Optional} {@code focused} {@link DynamicWidget} instance.
     */
    @PublicAPI
    public Optional<DynamicWidget<?, ?>> getFocusedWidget() {
        return this.getVisibleWidgets().filter(DynamicWidget::isFocused).findFirst();
    }

    /**
     * @return Whether a widget in this row is focused.
     */
    @PublicAPI
    public boolean isWidgetFocused() {
        return this.getVisibleWidgets().anyMatch(DynamicWidget::isFocused);
    }

    /**
     * Focus on the first widget found. If no widgets were eligible for focusing, then nothing if focused.
     */
    @PublicAPI
    public void focusOnFirst() {
        this.getVisibleWidgets().filter(DynamicWidget::canFocus).findFirst().ifPresent(DynamicWidget::setFocused);
    }

    /**
     * @return Whether this row has focusable widgets.
     */
    @PublicAPI
    public boolean hasFocusable() {
        return this.getVisibleWidgets().anyMatch(DynamicWidget::canFocus);
    }

    /**
     * @return Whether this row is outside the bounds of the row list.
     */
    @PublicAPI
    public boolean isOutside() {
        return this.getEndY() < this.rowList.getY() || this.getY() > this.rowList.getEndY();
    }

    /**
     * @return Whether this row is inside the viewing area of the row list.
     */
    @PublicAPI
    public boolean isInsideViewingArea() {
        return !this.isOutside();
    }

    /**
     * Check if this row's dynamic visibility has changed. If all widgets are invisible, then this row should also be
     * invisible and vice versa.
     *
     * @return Whether this row's visibility has changed.
     */
    protected boolean isVisibilityChanged() {
        if (this.isVisible() && this.widgets.stream().noneMatch(DynamicWidget::getVisibleTest)) {
            this.setInvisible();
            return true;
        } else if (this.isInvisible() && this.widgets.stream().anyMatch(DynamicWidget::getVisibleTest)) {
            this.setVisible();
            return true;
        } else
            return this.cache.visible.isExpired();
    }

    /**
     * Update the row's x-coordinate, y-coordinate, and width.
     *
     * @param rowStartX A new row x-coordinate.
     * @param rowStartY A new row y-coordinate.
     * @param rowWidth  A new row width.
     */
    protected void update(int rowStartX, int rowStartY, int rowWidth) {
        int startX = rowStartX + this.getBuilder().indent;
        int width = rowWidth - this.getBuilder().indent;
        int yOffset = rowStartY - this.getY();

        boolean isInactive = this.isInactive() || this.rowList.isInactive();
        boolean isInvisible = this.isInvisible() || this.rowList.isInvisible();
        boolean isHorizontalShift = startX != this.x || width != this.width;
        boolean isVerticalShift = rowStartY != this.y;

        this.setY(rowStartY);
        this.setX(startX);
        this.setWidth(width);

        Consumer<DynamicWidget<?, ?>> setWidgetStateOverride = (widget) -> {
            if (isInactive)
                widget.setInactive();

            if (isInvisible)
                widget.setInvisible();
        };

        if (this.isInsideViewingArea() || isHorizontalShift || !this.initialized) {
            DynamicWidget.sync(this.widgets);

            if (isHorizontalShift || !this.initialized) {
                this.initialized = true;
                this.getBuilder().sync();
            }

            if (this.isInactive() || this.isInvisible() || isInactive || isInvisible)
                this.widgets.forEach(setWidgetStateOverride);
        } else if (isVerticalShift) {
            this.widgets.forEach(widget -> {
                widget.setY(widget.getY() + yOffset);
                setWidgetStateOverride.accept(widget);
            });
        } else if (isInactive || isInvisible)
            this.widgets.forEach(setWidgetStateOverride);
    }

    /**
     * Render any (when row is hidden) instructions.
     *
     * @param graphics    The {@link GuiGraphicsExtractor} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderHidden(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.getBuilder().hiddenRenderer != null)
            this.getBuilder().hiddenRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        if (this.getBuilder().preRenderer != null)
            this.getBuilder().preRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);

        DynamicWidget.renderWithoutSync(this.widgets, graphics, mouseX, mouseY, partialTick);

        if (this.getBuilder().postRenderer != null)
            this.getBuilder().postRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);

        this.renderDebug(graphics);
    }
}
