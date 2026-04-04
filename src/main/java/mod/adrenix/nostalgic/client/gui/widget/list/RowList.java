package mod.adrenix.nostalgic.client.gui.widget.list;

import mod.adrenix.nostalgic.client.gui.tooltip.TooltipManager;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.client.gui.widget.scrollbar.Scrollbar;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import mod.adrenix.nostalgic.tweak.config.ModTweak;

public class RowList extends DynamicWidget<RowListBuilder, RowList> implements ContainerEventHandler, TooltipManager {
    /* Builder */

    protected final Scrollbar scrollbar;

    /* Fields */
    protected final TextWidget emptyMessage;
    protected final UniqueArrayList<DynamicWidget<?, ?>> internal;
    protected final UniqueArrayList<DynamicWidget<?, ?>> children;
    protected final UniqueArrayList<AbstractRow<?, ?>> visibleRows;
    protected final UniqueArrayList<AbstractRow<?, ?>> rows;
    protected final RecursionAvoidance pathFinder;
    protected GuiEventListener focusedListener;
    protected AbstractRow<?, ?> focusedRow;
    protected boolean isDragging;

    protected RowList(RowListBuilder builder) {
        super(builder);

        this.pathFinder = RecursionAvoidance.create();
        this.internal = new UniqueArrayList<>();
        this.children = new UniqueArrayList<>();
        this.visibleRows = new UniqueArrayList<>();
        this.rows = new UniqueArrayList<>();

        this.scrollbar = Scrollbar.vertical(this::getContentHeight, this::getAverageRowHeight)
                .animation(Animate.easeInOutCircular(1L, TimeUnit.SECONDS))
                .backgroundColor(this.getBuilder().scrollbarBackground)
                .onVisibleChange(this::setPositionForRows)
                .pos(this::getScrollbarStartX, this::getY)
                .height(this::getHeight)
                .build(this.internal::add);

        if (builder.emptyMessage == null)
            this.emptyMessage = TextWidget.create(Component.empty()).build();
        else {
            this.emptyMessage = TextWidget.create(builder.emptyMessage)
                    .posX(this::getX)
                    .width(this::getWidth)
                    .centerInWidgetY(this)
                    .centerAligned()
                    .visibleIf(this.visibleRows::isEmpty)
                    .build(this.internal::add);
        }

        this.getBuilder().addFollowers(this.internal);
    }

    /* Constructor */

    /**
     * Create a new row list instance using a factory. Due to the complex render requirements of row lists, these
     * widgets automatically assign themselves to the first render pass. If this is not desired, then use the builder
     * override to change which render pass this widget renders on.
     *
     * @return A {@link RowListBuilder} instance.
     */
    public static RowListBuilder create() {
        return new RowListBuilder();
    }

    /* Container Handling */

    /**
     * @return A {@link Stream} of {@code visible} {@link AbstractRow} that is inside the row list viewing area.
     */
    @PublicAPI
    public Stream<AbstractRow<?, ?>> getInsideAndVisibleRows() {
        return this.visibleRows.stream().filter(AbstractRow::isInsideViewingArea);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getTooltipWidgets() {
        return this.getInsideAndVisibleRows()
                .flatMap(WidgetHolder::getWidgetStream)
                .collect(Collectors.toCollection(UniqueArrayList::new));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<DynamicWidget<?, ?>> getMouseTooltipWidgets() {
        if (this.isMouseOutside(this.getMouseX(), this.getMouseY()))
            return new UniqueArrayList<>();

        return this.getTooltipWidgets();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<? extends GuiEventListener> children() {
        return this.children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDragging() {
        return this.isDragging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable GuiEventListener getFocused() {
        return this.focusedListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.setFocused(focused, true);
    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param focused           The focused GUI element.
     * @param smoothScrollToRow Whether to smooth scroll to a focused row element.
     */
    @PublicAPI
    public void setFocused(@Nullable GuiEventListener focused, boolean smoothScrollToRow) {
        if (this.focusedListener != null)
            this.focusedListener.setFocused(false);

        if (focused instanceof AbstractRow<?, ?> row) {
            this.focusedRow = row;

            if (smoothScrollToRow)
                this.setSmoothScrollOn(row);
            else
                this.setScrollOn(row);
        } else if (focused != null) {
            focused.setFocused(true);

            this.visibleRows.stream().filter(AbstractRow::isWidgetFocused).findFirst().ifPresent(row -> {
                this.focusedRow = row;
                this.setScrollOn(row);
            });
        } else
            this.focusedRow = null;

        this.focusedListener = focused;
    }

    /**
     * Calculate the next component path using a tab navigation event.
     *
     * @param event A {@link FocusNavigationEvent.TabNavigation} event instance.
     * @return A {@code nullable} {@link ComponentPath}.
     */
    protected @Nullable ComponentPath nextTabPath(FocusNavigationEvent.TabNavigation event) {
        ComponentPath rowPath;
        GuiEventListener lastListener = this.focusedListener;
        boolean cancelNavigation = false;

        // If a row is focused, then get the next path
        if (this.focusedRow != null)
            rowPath = this.focusedRow.nextFocusPath(event);
        else {
            // Otherwise, get the first or last row based on the event direction
            if (event.forward())
                rowPath = NullableResult.get(this.visibleRows.getFirst(), row -> row.nextFocusPath(event));
            else
                rowPath = NullableResult.get(this.visibleRows.getLast(), row -> row.nextFocusPath(event));
        }

        // Gets path relative to the row's container event handler
        if (rowPath instanceof ComponentPath.Path nextPath)
            this.setFocused(nextPath.childPath().component());

        // If no focus change was detected, then move to the row above or below based on the event direction
        if (this.focusedListener == lastListener && this.focusedRow != null) {
            int index = this.visibleRows.indexOf(this.focusedRow);

            if (index == -1)
                cancelNavigation = true;
            else {
                if (event.forward())
                    rowPath = this.visibleRows.map(index + 1, row -> row.nextFocusPath(event));
                else
                    rowPath = this.visibleRows.map(index - 1, row -> row.nextFocusPath(event));

                if (rowPath instanceof ComponentPath.Path nextPath)
                    this.setFocused(nextPath.childPath().component());
            }
        }

        // If nothing is focused, or navigation is canceled, then get the vanilla tab path
        if (this.focusedListener == null || cancelNavigation)
            return ContainerEventHandler.super.nextFocusPath(event);

        // If focus change was detected, then manually set the path relative to this row list
        if (this.focusedListener != lastListener)
            return ComponentPath.path(this, ComponentPath.leaf(this.focusedListener));

        // Moving forward on the last row requires special handling on the parent screen
        if (this.focusedRow == this.visibleRows.getLast() && this.getScreen() != null) {
            // Try moving forward to the next screen widget first
            ComponentPath screenPath = this.pathFinder.process(() -> {
                this.setFocused(null);
                return this.getScreen().nextFocusPath(event);
            });

            if (screenPath == null) {
                // If no path was found, then reset the screen's focus
                return this.pathFinder.process(() -> {
                    this.getScreen().setFocused(null);
                    return this.getScreen().nextFocusPath(event);
                });
            }

            return screenPath;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        if (this.isInactive() || this.isInvisible() || this.pathFinder.isProcessing())
            return null;

        if (event instanceof FocusNavigationEvent.TabNavigation navigation) {
            ComponentPath tabPath = this.nextTabPath(navigation);

            if (tabPath != null)
                return tabPath;
        }

        return this.pathFinder.process(() -> this.getNextPath(event));
    }

    /**
     * Get the next {@link ComponentPath} based on current focus context.
     *
     * @param event A {@link FocusNavigationEvent} instance.
     * @return The {@link ComponentPath} for the parent holder of this row list.
     */
    protected @Nullable ComponentPath getNextPath(FocusNavigationEvent event) {
        if (this.getScreen() == null)
            return null;

        ComponentPath nextPath = ContainerEventHandler.super.nextFocusPath(event);

        if (this.focusedListener != null && nextPath == null && this.getScreen().nextFocusPath(event) != null) {
            this.focusedListener.setFocused(false);
            this.focusedListener = null;

            return null;
        }

        return nextPath;
    }

    /**
     * Override focus path navigation with manual focusing overrides from a row.
     *
     * @param row The {@link Row} to get override data from.
     */
    protected void setOverrideFocus(AbstractRow<?, ?> row) {
        row.getFocusedWidget().ifPresent(widget -> {
            if (this.focusedListener != null && this.focusedListener != widget)
                this.focusedListener.setFocused(false);

            this.focusedListener = widget;
            this.focusedRow = row;
        });
    }

    /**
     * Remove the focus of any focused widget within the row list.
     */
    @PublicAPI
    public void clearFocus() {
        this.focusedRow = null;
        this.visibleRows.stream()
                .map(AbstractRow::getFocusedWidget)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .ifPresent(focused -> focused.setFocused(false));
    }

    /**
     * Set the focus on the first widget found in the row list.
     */
    @PublicAPI
    public void focusOnFirst() {
        this.visibleRows.stream().filter(AbstractRow::hasFocusable).findFirst().ifPresent(AbstractRow::focusOnFirst);
    }

    /* Methods */

    /**
     * @return The {@link Scrollbar} instance used by this row list.
     */
    @PublicAPI
    public Scrollbar getScrollbar() {
        return this.scrollbar;
    }

    /**
     * @return Get the {@link TextWidget} that represents the row list's empty message.
     */
    @PublicAPI
    public TextWidget getEmptyMessage() {
        return this.emptyMessage;
    }

    /**
     * @return An {@link UniqueArrayList} of {@link AbstractRow}.
     */
    @PublicAPI
    public UniqueArrayList<AbstractRow<?, ?>> getRows() {
        return this.rows;
    }

    /**
     * @return An {@link UniqueArrayList} of {@code visible} {@link AbstractRow}.
     */
    @PublicAPI
    public UniqueArrayList<AbstractRow<?, ?>> getVisibleRows() {
        return this.visibleRows;
    }

    /**
     * @return An {@link Optional} that contains the last visible {@link AbstractRow} in this row list.
     */
    @PublicAPI
    public Optional<AbstractRow<?, ?>> getLastVisibleRow() {
        if (this.visibleRows.isEmpty())
            return Optional.empty();

        return Optional.ofNullable(this.visibleRows.getLast());
    }

    /**
     * Add all of a row's widgets to the list's event listener children list and add the given row to the visible rows
     * list if it is visible.
     *
     * @param row An {@link AbstractRow} instance.
     */
    protected void addVisibleRow(AbstractRow<?, ?> row) {
        if (row.isInvisible())
            return;
        else
            this.visibleRows.add(row);

        this.children.addAll(row.widgets);
    }

    /**
     * Add the given collection of rows to this row list.
     *
     * @param rows A {@link Collection} of {@link AbstractRow}.
     */
    @PublicAPI
    public void addAll(Collection<AbstractRow<?, ?>> rows) {
        for (AbstractRow<?, ?> row : rows)
            this.addBottomRow(row);
    }

    /**
     * Add a row at the top of the list.
     *
     * @param row An {@link AbstractRow} instance.
     */
    @PublicAPI
    public void addTopRow(AbstractRow<?, ?> row) {
        this.rows.addFirst(row);

        if (row.isVisible()) {
            this.visibleRows.addFirst(row);
            this.children.addAll(row.widgets);
        }
    }

    /**
     * Add a row at the bottom of the list.
     *
     * @param row An {@link AbstractRow} instance.
     */
    @PublicAPI
    public void addBottomRow(AbstractRow<?, ?> row) {
        this.rows.add(row);
        this.addVisibleRow(row);
    }

    /**
     * Add a row below a certain row.
     *
     * @param row   The {@link AbstractRow} to add.
     * @param below The {@link AbstractRow} to add the given row below.
     */
    @PublicAPI
    public void addBelowRow(AbstractRow<?, ?> row, AbstractRow<?, ?> below) {
        int index = this.rows.indexOf(below) + 1;

        if (index > this.rows.size()) {
            this.rows.add(row);
            this.addVisibleRow(row);

            return;
        }

        this.rows.add(index, row);

        if (row.isVisible()) {
            this.visibleRows.add(index, row);
            this.children.addAll(row.widgets);
        }
    }

    /**
     * Remove a row from the row list.
     *
     * @param row An {@link AbstractRow} instance.
     */
    @PublicAPI
    public void removeRow(AbstractRow<?, ?> row) {
        this.children.removeAll(row.widgets);
        this.children.remove(row);
        this.rows.remove(row);
        this.visibleRows.remove(row);

        if (row == this.focusedRow)
            this.clearFocus();
    }

    /**
     * Clear all row entries from this row list.
     */
    public void clear() {
        this.children.clear();
        this.rows.clear();
        this.visibleRows.clear();
        this.resetScrollAmount();
        this.clearFocus();
    }

    /**
     * Get an optional row instance that may be located at the given mouse point.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return An optional that may contain a row instance.
     */
    @PublicAPI
    public Optional<AbstractRow<?, ?>> getRowAtPoint(double mouseX, double mouseY) {
        return this.visibleRows.stream().filter(row -> row.isMouseOver(mouseX, mouseY)).findFirst();
    }

    /**
     * @return Get the margin between rows. The margin is applied to all edges of the row.
     */
    @PublicAPI
    public int getRowMargin() {
        return this.getBuilder().verticalMargin + this.getRowSeparatorMargin();
    }

    /**
     * @return Get the margin added by list row separators.
     */
    @PublicAPI
    public int getRowSeparatorMargin() {
        int separatorPadding = this.getBuilder().separatorPadding;
        int separatorHeight = this.getBuilder().separatorHeight;

        return this.getBuilder().useSeparators ? separatorPadding + separatorHeight : 0;
    }

    /**
     * @return Get the starting x-position for rows.
     */
    @PublicAPI
    public int getRowStartX() {
        if (this.getBuilder().centerRows)
            return (int) MathUtil.center(this.getX(), this.getRowWidth(), this.getWidth());

        return this.getX() + this.getBuilder().horizontalMargin;
    }

    /**
     * @return Get the ending x-position for rows.
     */
    @PublicAPI
    public int getRowEndX() {
        return this.getRowStartX() + this.getRowWidth();
    }

    /**
     * @return Get the width of rows for this list. This is a static integer and will only change if the row margin
     * changes or the list itself changes width.
     */
    @PublicAPI
    public int getRowWidth() {
        int defaultWidth = this.getBuilder().defaultRowWidth;

        if (defaultWidth == 0)
            return (this.scrollbar.isVisible() ? this.getScrollbarStartX() : this.getEndX()) - this.getRowStartX() - this.getBuilder().horizontalMargin;
        else
            return defaultWidth;
    }

    /**
     * @return Get the number of rows subscribed to this list.
     */
    @PublicAPI
    public int getRowCount() {
        return this.visibleRows.size();
    }

    /**
     * @return An average calculation based on the height of each visible row subscribed to this list.
     */
    @PublicAPI
    public double getAverageRowHeight() {
        return (double) this.visibleRows.stream().mapToInt(AbstractRow::getHeight).sum() / (double) this.getRowCount();
    }

    /**
     * @return This height is the addition of this list's row margin and the sum of all visible row heights combined
     * with their margin to the next row.
     */
    @PublicAPI
    public int getContentHeight() {
        return this.getBuilder().verticalMargin + (this.getBuilder().topMargin * 2) + this.visibleRows.stream()
                .mapToInt(AbstractRow::getHeightWithMargin)
                .sum();
    }

    /**
     * @return The x-position of where the scrollbar starts relative to the x-axis.
     */
    @PublicAPI
    public int getScrollbarStartX() {
        if (this.getBuilder().leftAlignedScrollbar) {
            int endX = this.getVisibleRows().stream().mapToInt(AbstractRow::getEndX).max().orElse(0);
            return endX + this.getBuilder().scrollbarLeftMargin;
        }

        return this.getEndX() - this.getBuilder().scrollbarWidth;
    }

    /**
     * @return Get the amount this list has been scrolled down.
     */
    @PublicAPI
    public double getScrollAmount() {
        return this.scrollbar.getScrollAmount();
    }

    /**
     * Set the amount this list has been scrolled down manually. The argument will be clamped to acceptable values, so
     * the invoker does not have to implement such checks. If this action needs to be a smooth animation then use
     * {@link RowList#setSmoothScrollAmount(double)}.
     *
     * @param amount A new scroll amount.
     */
    @PublicAPI
    public void setScrollAmount(double amount) {
        this.updateRowsWithoutSync();
        this.scrollbar.setScrollAmount(amount);
    }

    /**
     * Reset the scrollbar back to its original position.
     */
    @PublicAPI
    public void resetScrollAmount() {
        this.scrollbar.setScrollAmount(0.0D);
    }

    /**
     * Manually set the amount this list scrolls down. Using this method will cause a smooth animation to the requested
     * point. If this action needs to be immediate then use {@link RowList#setScrollAmount(double)}.
     *
     * @param amount A new scroll amount.
     */
    @PublicAPI
    public void setSmoothScrollAmount(double amount) {
        this.updateRowsWithoutSync();
        this.scrollbar.setSmoothScrollAmount(amount);
    }

    /**
     * Set the scrollbar so that it is centered on the given row. A smooth animation will be used while the list is
     * centering.
     *
     * @param row A row instance that is to be scrolled to.
     */
    @PublicAPI
    public void setSmoothScrollOn(AbstractRow<?, ?> row) {
        this.centerScrollOn(row, true);
    }

    /**
     * Set the scrollbar so that it is centered on the given row.
     *
     * @param row A row instance that is to be scrolled to.
     */
    @PublicAPI
    public void setScrollOn(AbstractRow<?, ?> row) {
        this.centerScrollOn(row, false);
    }

    /**
     * Center the scrollbar so that is aligned to the center of the given row. If the row is not visible, then no action
     * will occur.
     *
     * @param row      A row instance to center the scrollbar to.
     * @param isSmooth Whether a smooth animation is used while centering.
     */
    private void centerScrollOn(AbstractRow<?, ?> row, boolean isSmooth) {
        if (row.isInvisible())
            return;

        this.updateRowsWithoutSync();

        double amount;

        if (CollectionUtil.last(this.visibleRows).stream().anyMatch(last -> last.equals(row)))
            amount = this.scrollbar.getMaxScrollAmount();
        else {
            int rowHeight = this.getBuilder().verticalMargin + row.getHeightWithMargin();
            int itemHeight = this.visibleRows.stream()
                    .takeWhile(next -> !next.equals(row))
                    .mapToInt(AbstractRow::getHeightWithMargin)
                    .sum();

            amount = itemHeight + rowHeight / 2.0D - this.getHeight() / 2.0D;
        }

        this.scrollbar.updateContentSize();

        if (isSmooth)
            this.setSmoothScrollAmount(amount);
        else
            this.setScrollAmount(amount);
    }

    /**
     * Functional method that assists with finding any widgets within the rows of this list that accepted an event. The
     * results of the predicate test applied to each row widget are kept within an array list cache to prevent
     * concurrent modification exceptions. Some events may modify the rows of this list; therefore, caching runnables
     * avoids that problem so that an event can be sent to all rows without interfering with the stream.
     *
     * @param predicate A {@link Predicate} that tests if a widget acknowledged an event.
     * @return Whether a widget from one of the rows in this list accepted an event emission.
     */
    private boolean isEventListened(Predicate<DynamicWidget<?, ?>> predicate) {
        return CollectionUtil.test(this.getInsideAndVisibleRows()
                .filter(AbstractRow::isInsideViewingArea)
                .flatMap(AbstractRow::getVisibleWidgets), predicate);
    }

    /**
     * Check if the mouse is currently outside this list.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the mouse is currently outside this list.
     */
    @PublicAPI
    public boolean isMouseOutside(double mouseX, double mouseY) {
        return !this.isMouseOver(mouseX, mouseY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();

        int button = event.button();

        if (this.isInvalidPoint(mouseX, mouseY))
            return false;

        this.scrollbar.mouseClicked(event, doubleClick);

        if (this.scrollbar.isDragging())
            return true;

        if (this.isMouseOver(mouseX, mouseY)) {
            boolean isWidgetClicked = this.isEventListened(widget -> {
                boolean isClicked = widget.mouseClicked(event, doubleClick);

                if (isClicked) {
                    this.clearFocus();
                    widget.setClickFocus();
                }

                return isClicked;
            });

            if (isWidgetClicked)
                return true;

            if (this.visibleRows.stream().anyMatch(row -> row.mouseClicked(event, doubleClick)))
                return true;
        }

        this.clearFocus();

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        double mouseX = event.x();
        double mouseY = event.y();

        int button = event.button();

        if (this.isInvalidPoint(mouseX, mouseY))
            return false;

        if (this.scrollbar.mouseReleased(event))
            return true;

        return this.isEventListened(widget -> widget.mouseReleased(event));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();

        int button = event.button();

        if (this.isInvalidPoint(mouseX, mouseY) && !this.scrollbar.isDragging())
            return false;

        if (this.scrollbar.mouseDragged(event, dragX, dragY))
            return true;

        return this.isEventListened(widget -> widget.mouseDragged(event, dragX, dragY));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.isInvalidPoint(mouseX, mouseY))
            return false;

        boolean isEventListened = this.isEventListened(widget -> widget.mouseScrolled(mouseX, mouseY, deltaX, deltaY));

        if (isEventListened)
            return true;

        return this.scrollbar.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isInactive() || this.isInvisible())
            return false;

        return this.isEventListened(widget -> widget.keyPressed(event));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyReleased(KeyEvent event) {
        if (this.isInactive() || this.isInvisible())
            return false;

        return this.isEventListened(widget -> widget.keyReleased(event));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean charTyped(CharacterEvent event) {
        if (this.isInactive() || this.isInvisible())
            return false;

        return this.isEventListened(widget -> widget.charTyped(event));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick() {
        this.rows.stream().map(AbstractRow::getWidgets).forEach(widgets -> widgets.forEach(DynamicWidget::tick));
    }

    /**
     * Set position data for all rows based on the current row list state.
     */
    private void setPositionForRows() {
        DynamicWidget.sync(this.rows);

        int verticalMargin = this.getBuilder().verticalMargin;
        int topMargin = this.getBuilder().topMargin;
        int rowStartX = this.getRowStartX();
        int rowStartY = this.getY() + topMargin + verticalMargin - (int) Math.round(this.getScrollAmount());
        int rowWidth = this.getRowWidth();
        boolean isVisibilityChanged = false;

        for (AbstractRow<?, ?> row : this.rows) {
            if (row.isVisibilityChanged())
                isVisibilityChanged = true;

            if (row.isInvisible())
                continue;

            row.update(rowStartX, rowStartY, rowWidth);
            rowStartY += row.getHeightWithMargin();
        }

        if (isVisibilityChanged) {
            this.visibleRows.clear();
            this.children.clear();
            this.rows.forEach(this::addVisibleRow);

            DynamicWidget.sync(this.rows);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        int x0 = this.getX();
        int y0 = this.getY();
        int x1 = this.getEndX();
        int y1 = this.getEndY();
        int width = this.getWidth();
        int height = this.getHeight();
        int scrollAmount = (int) Math.round(this.getScrollAmount());

        if (this.getBuilder().backgroundRenderer != RowListRenderer.EMPTY)
            this.getBuilder().backgroundRenderer.accept(this, graphics, mouseX, mouseY, partialTick);
        else {
            boolean isLevelAbsent = Minecraft.getInstance().level == null;

            if (isLevelAbsent && this.getBuilder().renderBackgroundDirt) {
                int color = 0xFFFFFFFF;
                if (!this.getBuilder().renderBackgroundOpacity)
                    color = ARGB.colorFromFloat(1.0F, 0.125F, 0.125F, 0.125F);

                graphics.blit(RenderPipelines.GUI_TEXTURED, TextureLocation.DIRT_BACKGROUND, x0, y0, 32, 32, x1, y1 + scrollAmount, width, height, color);
            }

            if (this.getBuilder().renderBackgroundOpacity) {
                int color = 0xDF000000;

                if (this.getBuilder().backgroundOpacity != null)
                    color = new Color(Color.BLACK, this.getBuilder().backgroundOpacity.getAsFloat()).get();

                graphics.fill(x0, y0, x1, y1, color);
            }

            if (this.getBuilder().useMenuBackground) {
                Identifier background = isLevelAbsent ? TextureLocation.MENU_LIST_BACKGROUND : TextureLocation.LEVEL_MENU_LIST_BACKGROUND;
                Identifier header = isLevelAbsent ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR;
                Identifier footer = isLevelAbsent ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR;

                graphics.blit(RenderPipelines.GUI_TEXTURED, background, x0, y0, x1, y1 + scrollAmount, width, height, 32, 32);
                graphics.blit(RenderPipelines.GUI_TEXTURED, header, x0, y0 - 2, 0.0F, 0.0F, width, 2, 32, 2);
                graphics.blit(RenderPipelines.GUI_TEXTURED, footer, x0, y1, 0.0F, 0.0F, width, 2, 32, 2);
            }
        }

        if (this.getBuilder().useScissorRendering) {
            if (this.getBuilder().scissorRectangle == null)
                graphics.enableScissor(x0, y0, x1, y1);
            else {
                Rectangle rectangle = this.getBuilder().scissorRectangle.get();
                graphics.enableScissor(rectangle.startX(), rectangle.startY(), rectangle.endX(), rectangle.endY());
            }
        }

        this.updateAndRenderRows(graphics, mouseX, mouseY, partialTick);

        if (this.getBuilder().useScissorRendering)
            graphics.disableScissor();

        if (this.getBuilder().renderTopAndBottomDirt) {
            float shadow = 127 / 255.0F;
            int screenWidth = GuiUtil.getScreenWidth();
            int screenHeight = GuiUtil.getScreenHeight();

            int color = ARGB.colorFromFloat(1.0F, shadow, shadow, shadow);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TextureLocation.DIRT_BACKGROUND, 0, 0, 0.0F, 0.0F, screenWidth, y0, 32, 32, color);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TextureLocation.DIRT_BACKGROUND, 0, y1, 0.0F, 0.0F, screenWidth, screenHeight - y1, 32, 32, color);
        }

        if (this.getBuilder().renderTopAndBottomShadow || this.getBuilder().renderTopAndBottomDirt) {
            graphics.fillGradient(x0, y0, x1, y0 + 4, Color.BLACK.get(), Color.BLACK.fromAlpha(0).get());
            graphics.fillGradient(x0, y1 - 4, x1, y1, Color.BLACK.fromAlpha(0).get(), Color.BLACK.get());
        }

        DynamicWidget.renderWithoutSync(this.internal, graphics, mouseX, mouseY, partialTick);

        if (this.getBuilder().postRenderer != RowListRenderer.EMPTY)
            this.getBuilder().postRenderer.accept(this, graphics, mouseX, mouseY, partialTick);

        if (this.scrollbar.isInvisible() && this.getScrollAmount() > 0.0D)
            this.setScrollAmount(0.0D);

        this.renderDebug(graphics);
    }

    /**
     * Handler method for rendering the separator between rows when applicable.
     *
     * @param row      The row to get position data from.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    private void renderSeparator(AbstractRow<?, ?> row, GuiGraphicsExtractor graphics) {
        if (CollectionUtil.last(this.visibleRows).orElse(null) == row || !this.getBuilder().useSeparators)
            return;

        int endY = row.getEndY();
        int offset = this.getBuilder().separatorPadding;
        int height = this.getBuilder().separatorHeight + offset;
        Color color = this.getBuilder().separatorColor;

        if (endY + height < this.getY() || endY + offset > this.getEndY())
            return;

        graphics.fill(row.getX(), endY + offset, row.getEndX(), endY + height, color.get());
    }

    /**
     * Render an individual row.
     *
     * @param row         The {@link AbstractRow} to render.
     * @param graphics    The {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The current x-coordinate of the mouse.
     * @param mouseY      The current y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderRow(AbstractRow<?, ?> row, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.setOverrideFocus(row);
        this.renderSeparator(row, graphics);

        if (row.isOutside()) {
            row.renderHidden(graphics, mouseX, mouseY, partialTick);
            return;
        } else
            this.renderRowHighlight(row, graphics, mouseX, mouseY);

        row.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (row.isWidgetFocused() && this.getBuilder().showSelectionBorder)
            graphics.outline(row.getX(), row.getY(), row.getWidth(), row.getHeight(), Color.FRENCH_SKY_BLUE.get());
    }

    /**
     * Render row highlighting.
     *
     * @param row      The {@link AbstractRow} to highlight.
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     * @param mouseX   The current x-coordinate of the mouse.
     * @param mouseY   The current y-coordinate of the mouse.
     */
    private void renderRowHighlight(AbstractRow<?, ?> row, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Optional<Animation> rowAnimator = row.getHighlightAnimation();
        Animation listAnimator = this.getBuilder().rowHighlightAnimation;

        boolean isRowIgnored = rowAnimator.isEmpty() || row.getBuilder().ignoreHighlight;
        boolean isListIgnored = listAnimator == null || !this.getBuilder().useHighlightsWhen.getAsBoolean();
        boolean isFocused = row.isWidgetFocused();

        if (isRowIgnored && isListIgnored)
            return;

        if (rowAnimator.isEmpty())
            row.getBuilder().highlight(this.getBuilder().rowHighlightAlpha, listAnimator.copy());

        Animation animator = row.getHighlightAnimation().orElse(null);

        if (animator == null)
            return;

        if (row.isMouseOver(mouseX, mouseY) || isFocused)
            animator.play();
        else
            animator.rewind();

        Color color = rowAnimator.isPresent() ? row.getHighlightColor() : this.getBuilder().rowHighlightColor;
        double transparency = rowAnimator.isPresent() ? row.getHighlightAlpha() : this.getBuilder().rowHighlightAlpha;
        double override = row.isMouseOver(mouseX, mouseY) ? 1.0D : 0.0D;
        double normalized = this.getBuilder().overrideHighlightOpacity.getAsInt() / 100.0D;
        double opacity = this.getBuilder().useOverrideHighlights.getAsBoolean() ? normalized : transparency;
        double fade = 0.5F;//ModTweak.DISPLAY_ROW_HIGHLIGHT_FADE.fromCache() ? animator.getValue() : override; //TODO

        float alpha = (float) Mth.clamp(opacity * fade, 0.0D, 1.0D);
        Color unfocused = new Color(color, alpha);
        Color focused = new Color(0.53F, 0.68F, 1.0F, alpha);
        Color fill = isFocused ? focused : unfocused;

        if (alpha == 0.0D)
            return;

        graphics.fill(row.getX(), row.getY(), row.getEndX(), row.getEndY(), fill.get());
    }

    /**
     * Updates and the positions of rows without applying synced cache.
     */
    private void updateRowsWithoutSync() {
        this.setPositionForRows();
        DynamicWidget.syncWithoutCache(this.internal);
    }

    /**
     * Update the position for each row and render them with the new changes.
     *
     * @param graphics    The {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The current x-coordinate of the mouse.
     * @param mouseY      The current y-coordinate of the mouse.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void updateAndRenderRows(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.setPositionForRows();
        DynamicWidget.sync(this.internal);
        this.visibleRows.forEach(row -> this.renderRow(row, graphics, mouseX, mouseY, partialTick));
    }
}
