package mod.adrenix.nostalgic.client.gui.widget.embed;

import mod.adrenix.nostalgic.client.gui.tooltip.TooltipManager;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.client.gui.widget.scrollbar.Scrollbar;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.renderer.MatrixUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;
import mod.adrenix.nostalgic.util.common.math.DynamicRectangle;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Embed extends DynamicWidget<EmbedBuilder, Embed>
    implements WidgetHolder, TooltipManager, RelativeLayout, ContainerEventHandler
{
    /* Builder */

    /**
     * Create a new {@link Embed} instance. An embed is a convenient way to clump widgets into individual distinct
     * sections within a single screen. Each embed comes with built-in horizontal and vertical overflow scrollbars.
     *
     * @return A new {@link EmbedBuilder} instance.
     */
    public static EmbedBuilder create()
    {
        return new EmbedBuilder();
    }

    /* Fields */

    protected final RecursionAvoidance pathFinder;
    protected GuiEventListener focusedListener;
    protected DynamicRectangle<Embed> scissor;

    protected final EmbedBuilder builder;
    protected final EmbedWidgets widgets;
    protected final BlankWidget relativeTop;
    protected final BlankWidget relativeLeft;
    protected final Scrollbar verticalScrollbar;
    protected final Scrollbar horizontalScrollbar;
    protected final int scrollbarSize;

    protected boolean isEmbeddedDragging;
    protected boolean isResizingPaused;

    /* Constructor */

    protected Embed(EmbedBuilder builder)
    {
        super(builder);

        this.pathFinder = RecursionAvoidance.create();
        this.widgets = new EmbedWidgets();
        this.builder = builder;
        this.scrollbarSize = builder.scrollbarSize;

        this.relativeTop = BlankWidget.create()
            .size(0)
            .relativeTo(this)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add, this.widgets.relatives::add));

        this.relativeLeft = BlankWidget.create()
            .size(0)
            .relativeTo(this)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add, this.widgets.relatives::add));

        this.verticalScrollbar = Scrollbar.vertical(this::getContentHeight, this::getAverageWidgetHeight)
            .animation(Animate.easeInOutCircular(1L, TimeUnit.SECONDS))
            .size(this.scrollbarSize)
            .pos(this::getVerticalScrollbarStartX, this::getVerticalScrollbarStartY)
            .height(this::getScrollbarHeight)
            .visibleIf(this::isVerticalScrollable)
            .onVisibleChange(this::updateWidgets)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add, this.widgets.scrollbars::add));

        this.horizontalScrollbar = Scrollbar.horizontal(this::getContentWidth, this::getAverageWidgetWidth)
            .animation(Animate.easeInOutCircular(1L, TimeUnit.SECONDS))
            .size(this.scrollbarSize)
            .pos(this::getHorizontalScrollbarStartX, this::getHorizontalScrollbarStartY)
            .width(this::getScrollbarWidth)
            .visibleIf(this::isHorizontalScrollable)
            .onVisibleChange(this::updateWidgets)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add, this.widgets.scrollbars::add));

        this.setDefaultScissor();
    }

    /* Container Event Handler */

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends GuiEventListener> children()
    {
        return this.widgets.all.stream().filter(DynamicWidget::isVisible).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDragging()
    {
        return this.isEmbeddedDragging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDragging(boolean isDragging)
    {
        this.isEmbeddedDragging = isDragging;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public GuiEventListener getFocused()
    {
        return this.focusedListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(@Nullable GuiEventListener focused)
    {
        this.setFocused(focused, true);
    }

    /**
     * Sets the focus state of the GUI element.
     *
     * @param focused               The focused GUI element.
     * @param smoothScrollToFocused Whether to smooth scroll to the focused element.
     */
    @PublicAPI
    public void setFocused(@Nullable GuiEventListener focused, boolean smoothScrollToFocused)
    {
        this.widgets.all.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);

        if (this.focusedListener != null)
            this.focusedListener.setFocused(false);

        if (focused instanceof DynamicWidget<?, ?> dynamic && dynamic.canFocus())
        {
            dynamic.setFocused(true);
            this.setScrollOn(dynamic);
        }

        this.focusedListener = focused;
    }

    /**
     * Set the scrollbar(s) so that the given widget is visible within the embed.
     *
     * @param widget A {@link DynamicWidget} instance to center the scrollbar(s) on.
     */
    @PublicAPI
    public void setScrollOn(DynamicWidget<?, ?> widget)
    {
        if (this.widgets.internal.contains(widget) || widget.isAnchored())
            return;

        this.getVisibleWidgets().filter(visible -> visible.equals(widget)).findFirst().ifPresent(this::scrollTo);
    }

    /**
     * Set the scrollbar(s) so that the given widget is visible within the embed.
     *
     * @param widget A {@link DynamicWidget} to center the scrollbar(s) on.
     */
    protected void scrollTo(DynamicWidget<?, ?> widget)
    {
        int relX = widget.getX() - (this.getScrollOffsetX() + this.getPaddingLeft());
        int relY = widget.getY() - (this.getScrollOffsetY() + this.getPaddingTop());
        int width = widget.getWidth();
        int height = widget.getHeight();

        if (this.isVerticalScrollbarVisible())
            this.verticalScrollbar.setScrollAmount(relY + (height / 2.0D) - (this.getInsideHeight() / 2.0D));

        if (this.isHorizontalScrollbarVisible())
            this.horizontalScrollbar.setScrollAmount(relX + (width / 2.0D) - (this.getInsideWidth() / 2.0D));
    }

    /**
     * Get the next {@link ComponentPath} based on current focus context.
     *
     * @param event A {@link FocusNavigationEvent} instance.
     * @return The {@link ComponentPath} for the parent holder of this row list.
     */
    protected @Nullable ComponentPath getNextPath(FocusNavigationEvent event)
    {
        if (this.getScreen() == null)
            return null;

        ComponentPath nextPath = ContainerEventHandler.super.nextFocusPath(event);

        if (this.focusedListener != null && nextPath == null)
        {
            this.focusedListener.setFocused(false);
            this.focusedListener = null;

            return null;
        }

        return nextPath;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if (!this.canFocus() || this.isInactive() || this.isInvisible() || this.pathFinder.isProcessing())
            return null;

        return this.pathFinder.process(() -> this.getNextPath(event));
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets()
    {
        return this.widgets.all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getTooltipWidgets()
    {
        return this.widgets.all;
    }

    /**
     * Updates only embedded widgets x/y positions and sizes as needed.
     */
    protected void syncEmbeddedWithoutCache()
    {
        int prevWidth = this.width;
        int prevHeight = this.height;

        int widthOffset = this.verticalScrollbar.isVisible() ? this.scrollbarSize : 0;
        int heightOffset = this.horizontalScrollbar.isVertical() ? this.scrollbarSize : 0;

        this.setWidth(this.width - widthOffset - this.getPaddingRight());
        this.setHeight(this.height - heightOffset - this.getPaddingBottom());

        DynamicWidget.syncWithoutCache(this.widgets.embedded);

        this.setWidth(prevWidth);
        this.setHeight(prevHeight);

        DynamicWidget.sync(List.of(this));
    }

    /**
     * Updates all relative and embedded widgets x/y positions and sizes as needed.
     */
    @PublicAPI
    public void updateWidgets()
    {
        DynamicWidget.syncWithoutCache(this.widgets.relatives);
        this.syncEmbeddedWithoutCache();
        DynamicWidget.syncWithoutCache(this.widgets.relatives);
    }

    /**
     * Add a widget to this embed widget.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public void addWidget(DynamicWidget<?, ?> widget)
    {
        if (widget.getBuilder() instanceof LayoutBuilder<?, ?> layout)
            layout.relativeTo(this);

        this.widgets.addScissored(widget);

        this.resizeIfNeeded();
    }

    /**
     * Add widgets to this embed widget.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    public void addWidgets(DynamicWidget<?, ?>... widgets)
    {
        for (DynamicWidget<?, ?> widget : widgets)
            this.addWidget(widget);
    }

    /**
     * Add a projected widget to this embed. A projected widget is a widget not cut out by the embed scissor bounds. It
     * is always rendered in the embed.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public void addProjectedWidget(DynamicWidget<?, ?> widget)
    {
        this.addWidget(widget);
        this.widgets.addProjected(widget);
    }

    /**
     * Add a varargs number of projected widgets to this embed. A projected widget is a widget not cut out by the embed
     * scissor bounds. It is always rendered to the embed.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    public void addProjectedWidgets(DynamicWidget<?, ?>... widgets)
    {
        for (DynamicWidget<?, ?> widget : widgets)
            this.addProjectedWidget(widget);
    }

    /**
     * Remove a widget from this embed.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @Override
    public void removeWidget(@Nullable DynamicWidget<?, ?> widget)
    {
        if (widget == null)
            return;

        this.widgets.removeAll(widget);
    }

    /**
     * Remove all embedded widgets from this embed widget.
     */
    @PublicAPI
    public void removeAllWidgets()
    {
        UniqueArrayList<DynamicWidget<?, ?>> embedded = new UniqueArrayList<>(this.widgets.embedded);

        embedded.forEach(this.widgets::removeAll);
        embedded.clear();
    }

    /**
     * Change the dynamic scissoring region of the embed.
     *
     * @param scissor A {@link DynamicRectangle} that accepts this {@link Embed} instance to define a scissoring
     *                region.
     */
    @PublicAPI
    public void setCustomScissor(DynamicRectangle<Embed> scissor)
    {
        this.scissor = scissor;
    }

    /**
     * Get the default scissoring position on the x-axis.
     *
     * @param embed The {@link Embed} instance.
     * @return The x-axis scissoring position.
     */
    @PublicAPI
    public int getScissorX(Embed embed)
    {
        return embed.getInsideX() + embed.builder.scissorPadding;
    }

    /**
     * Get the default scissoring position on the y-axis.
     *
     * @param embed The {@link Embed} instance.
     * @return The y-axis scissoring position.
     */
    @PublicAPI
    public int getScissorY(Embed embed)
    {
        return embed.getInsideY() + embed.builder.scissorPadding;
    }

    /**
     * Get the default scissoring ending position on the x-axis.
     *
     * @param embed The {@link Embed} instance.
     * @return The ending x-axis scissoring position.
     */
    @PublicAPI
    public int getScissorEndX(Embed embed)
    {
        return embed.getInsideEndX() - embed.builder.scissorPadding;
    }

    /**
     * Get the default scissoring ending position on the y-axis.
     *
     * @param embed The {@link Embed} instance.
     * @return The ending y-axis scissoring position.
     */
    @PublicAPI
    public int getScissorEndY(Embed embed)
    {
        return embed.getInsideEndY() - embed.builder.scissorPadding;
    }

    /**
     * Reset the embed's scissored region back to its default state.
     */
    @PublicAPI
    public void setDefaultScissor()
    {
        this.scissor = new DynamicRectangle<>(this::getScissorX, this::getScissorY, this::getScissorEndX, this::getScissorEndY);
    }

    /**
     * @return The starting x-position for the inside of the embed.
     */
    @PublicAPI
    public int getInsideX()
    {
        return this.getX() + this.getOutlineThickness();
    }

    /**
     * @return The starting y-position for the inside of the embed.
     */
    @PublicAPI
    public int getInsideY()
    {
        return this.getY() + this.getOutlineThickness();
    }

    /**
     * @return The width of the inside embed.
     */
    @PublicAPI
    public int getInsideWidth()
    {
        return this.width - (this.getOutlineThickness() * 2);
    }

    /**
     * @return The height of the inside embed.
     */
    @PublicAPI
    public int getInsideHeight()
    {
        return this.height - (this.getOutlineThickness() * 2);
    }

    /**
     * @return The ending x-position for the inside embed.
     */
    @PublicAPI
    public int getInsideEndX()
    {
        return this.x + this.width - this.getOutlineThickness();
    }

    /**
     * @return The ending y-position for the inside embed.
     */
    @PublicAPI
    public int getInsideEndY()
    {
        return this.y + this.height - this.getOutlineThickness();
    }

    /**
     * @return The inside scroll offset x-position from horizontal scrolling.
     */
    @PublicAPI
    public int getScrollOffsetX()
    {
        return (int) (this.getInsideX() - this.getScrollAmountX());
    }

    /**
     * @return The inside scroll offset y-position from vertical scrolling.
     */
    @PublicAPI
    public int getScrollOffsetY()
    {
        return (int) (this.getInsideY() - this.getScrollAmountY());
    }

    /**
     * @return The padding from the top of the embed.
     */
    @PublicAPI
    public int getPaddingTop()
    {
        return Math.max(this.builder.paddingTop, this.builder.scissorPadding);
    }

    /**
     * @return The padding from the right of the embed.
     */
    @PublicAPI
    public int getPaddingRight()
    {
        return Math.max(this.builder.paddingRight, this.builder.scissorPadding);
    }

    /**
     * @return The padding from the bottom of the embed.
     */
    @PublicAPI
    public int getPaddingBottom()
    {
        return Math.max(this.builder.paddingBottom, this.builder.scissorPadding);
    }

    /**
     * @return The padding from the left of the embed.
     */
    @PublicAPI
    public int getPaddingLeft()
    {
        return Math.max(this.builder.paddingLeft, this.builder.scissorPadding);
    }

    /**
     * @return The outline thickness of the embed that was defined during widget building.
     */
    @PublicAPI
    public int getOutlineThickness()
    {
        return this.builder.borderColor.isPresent() ? this.builder.borderThickness : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelativeX(DynamicWidget<?, ?> widget)
    {
        boolean isInternal = this.widgets.internal.contains(widget) && !this.widgets.relatives.contains(widget);

        return isInternal ? this.getInsideX() : this.getScrollOffsetX() + this.getPaddingLeft();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRelativeY(DynamicWidget<?, ?> widget)
    {
        boolean isInternal = this.widgets.internal.contains(widget) && !this.widgets.relatives.contains(widget);

        return isInternal ? this.getInsideY() : this.getScrollOffsetY() + this.getPaddingTop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnchoredX(DynamicWidget<?, ?> widget)
    {
        return this.getX() + this.getPaddingLeft();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnchoredY(DynamicWidget<?, ?> widget)
    {
        return this.getY() + this.getPaddingTop();
    }

    /**
     * @return Whether the horizontal scrollbar is visible.
     */
    @PublicAPI
    public boolean isHorizontalScrollbarVisible()
    {
        if (this.horizontalScrollbar == null)
            return false;

        return this.horizontalScrollbar.isVisible();
    }

    /**
     * @return Whether the vertical scrollbar is visible.
     */
    @PublicAPI
    public boolean isVerticalScrollbarVisible()
    {
        if (this.verticalScrollbar == null)
            return false;

        return this.verticalScrollbar.isVisible();
    }

    /**
     * @return A {@code true} value when both scrollbars are visible.
     */
    @PublicAPI
    public boolean areScrollbarsVisible()
    {
        return this.isHorizontalScrollbarVisible() && this.isVerticalScrollbarVisible();
    }

    /**
     * @return Whether either scrollbar is currently being scrolled.
     */
    @PublicAPI
    public boolean isScrollbarHeld()
    {
        return this.verticalScrollbar.isDragging() || this.horizontalScrollbar.isDragging();
    }

    /**
     * Resets the horizontal and vertical scrollbars back to their default positions.
     */
    @PublicAPI
    public void resetScrollAmount()
    {
        this.verticalScrollbar.setScrollAmount(0.0D);
        this.horizontalScrollbar.setScrollAmount(0.0D);
    }

    /**
     * @return A {@link Stream} of {@code visible} scrollable {@link DynamicWidget}.
     */
    protected Stream<DynamicWidget<?, ?>> getScrollableWidgets()
    {
        return this.widgets.embedded.stream().filter(DynamicWidget::isNotAnchored).filter(DynamicWidget::isVisible);
    }

    /**
     * @return The starting x-position for the vertical scrollbar.
     */
    protected int getVerticalScrollbarStartX()
    {
        return this.getInsideEndX() - this.scrollbarSize;
    }

    /**
     * @return The starting y-position for the vertical scrollbar.
     */
    protected int getVerticalScrollbarStartY()
    {
        return this.getInsideY();
    }

    /**
     * @return The starting x-position for the horizontal scrollbar.
     */
    protected int getHorizontalScrollbarStartX()
    {
        return this.getInsideX();
    }

    /**
     * @return The starting y-position for the horizontal scrollbar.
     */
    protected int getHorizontalScrollbarStartY()
    {
        return this.getInsideEndY() - this.scrollbarSize;
    }

    /**
     * @return The width for a horizontal scrollbar.
     */
    protected int getScrollbarWidth()
    {
        return this.getInsideWidth() - (this.areScrollbarsVisible() ? this.scrollbarSize : 0);
    }

    /**
     * @return The height for a vertical scrollbar.
     */
    protected int getScrollbarHeight()
    {
        return this.getInsideHeight() - (this.areScrollbarsVisible() ? this.scrollbarSize : 0);
    }

    /**
     * @return The current scroll amount for the horizontal scrollbar.
     */
    @PublicAPI
    public double getScrollAmountX()
    {
        return this.horizontalScrollbar.getScrollAmount();
    }

    /**
     * @return The current scroll amount for the vertical scrollbar.
     */
    @PublicAPI
    public double getScrollAmountY()
    {
        return this.verticalScrollbar.getScrollAmount();
    }

    /**
     * @return A calculation that determines the embed width taken up by embedded widgets.
     */
    protected int getContentWidth()
    {
        int endX = this.isVerticalScrollbarVisible() ? this.getVerticalScrollbarStartX() : this.getInsideEndX();
        int x1 = this.getScrollableWidgets().mapToInt(DynamicWidget::getEndX).max().orElse(endX);
        int x0 = this.relativeLeft.getX();

        return Math.abs(x1 - x0) + this.getPaddingLeft() + this.getPaddingRight();
    }

    /**
     * @return A calculation that determines the embed height taken up by embedded widgets.
     */
    protected int getContentHeight()
    {
        int endY = this.isHorizontalScrollbarVisible() ? this.getHorizontalScrollbarStartY() : this.getInsideEndY();
        int y1 = this.getScrollableWidgets().mapToInt(DynamicWidget::getEndY).max().orElse(endY);
        int y0 = this.relativeTop.getY();

        return Math.abs(y1 - y0) + this.getPaddingTop() + this.getPaddingBottom();
    }

    /**
     * @return The average width of each widget assigned to this embed.
     */
    protected double getAverageWidgetWidth()
    {
        return (double) this.getScrollableWidgets()
            .mapToInt(DynamicWidget::getWidth)
            .sum() / (double) this.getContentWidth();
    }

    /**
     * @return The average height of each widget assigned to this embed.
     */
    protected double getAverageWidgetHeight()
    {
        return (double) this.getScrollableWidgets()
            .mapToInt(DynamicWidget::getHeight)
            .sum() / (double) this.getContentHeight();
    }

    /**
     * Check if content can be scrolled vertically.
     *
     * @param scrollbar The vertical {@link Scrollbar}.
     * @return Whether the vertical scrollbar should be visible
     */
    protected boolean isVerticalScrollable(Scrollbar scrollbar)
    {
        if (this.getContentHeight() <= this.getHeight())
            return false;

        return scrollbar.getMaxScrollAmount() > 0;
    }

    /**
     * Check if content can be scrolled horizontally.
     *
     * @param scrollbar The horizontal {@link Scrollbar}.
     * @return Whether the horizontal scrollbar should be visible
     */
    protected boolean isHorizontalScrollable(Scrollbar scrollbar)
    {
        if (this.getContentWidth() <= this.getWidth())
            return false;

        return scrollbar.getMaxScrollAmount() > 0;
    }

    /**
     * @return The current size used by both the vertical and horizontal scrollbars.
     */
    @PublicAPI
    public int getScrollbarSize()
    {
        return this.getBuilder().scrollbarSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick()
    {
        this.widgets.all.forEach(DynamicWidget::tick);

        if (this.builder.onTick != null)
            this.builder.onTick.run();
    }

    /**
     * Get a stream of all embedded widgets.
     *
     * @return A {@link Stream} of {@code visible} embedded {@link DynamicWidget}.
     */
    @Override
    public Stream<DynamicWidget<?, ?>> getWidgetStream()
    {
        return this.widgets.embedded.stream();
    }

    /**
     * Get a stream of visible embedded widgets.
     *
     * @return A {@link Stream} of {@code visible} embedded {@link DynamicWidget}.
     */
    @Override
    public Stream<DynamicWidget<?, ?>> getVisibleWidgets()
    {
        return this.widgets.embedded.stream().filter(DynamicWidget::isVisible);
    }

    /**
     * Get a stream of visible widgets from the given collection.
     *
     * @param widgets A {@link UniqueArrayList} of {@link DynamicWidget}.
     * @return A {@link Stream} of {@code visible} {@link DynamicWidget}.
     */
    @PublicAPI
    public Stream<DynamicWidget<?, ?>> getVisibleWidgets(UniqueArrayList<DynamicWidget<?, ?>> widgets)
    {
        return widgets.stream().filter(DynamicWidget::isVisible);
    }

    /**
     * Get an optional embedded widget instance that may be located at the given mouse point.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return An optional that may contain an embed widget instance.
     */
    @PublicAPI
    public Optional<DynamicWidget<?, ?>> getWidgetAtPoint(double mouseX, double mouseY)
    {
        return this.getVisibleWidgets()
            .filter(DynamicWidget::isVisible)
            .filter(widget -> widget.isMouseOver(mouseX, mouseY))
            .findFirst();
    }

    /**
     * Check if the given embedded widget is completely outside the embed.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return Whether the given widget can't be seen.
     */
    @PublicAPI
    public boolean isWidgetOutside(DynamicWidget<?, ?> widget)
    {
        boolean isOutsideX = widget.getEndX() < this.getInsideX() || widget.getX() > this.getInsideEndX();
        boolean isOutsideY = widget.getEndY() < this.getInsideY() || widget.getY() > this.getInsideEndY();

        return isOutsideX || isOutsideY;
    }

    /**
     * Check if the given widget is visible to the embed.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return Whether the given widget can be seen.
     */
    @PublicAPI
    public boolean isWidgetInside(DynamicWidget<?, ?> widget)
    {
        return !this.isWidgetOutside(widget);
    }

    /**
     * Check if the given mouse point is inside the embed widget viewing area.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the given mouse point is within the embed widget window.
     */
    @PublicAPI
    public boolean isMouseInsideWindow(double mouseX, double mouseY)
    {
        double dx = this.isHorizontalScrollbarVisible() ? this.scrollbarSize : 0.0D;
        double dy = this.isVerticalScrollbarVisible() ? this.scrollbarSize : 0.0D;

        double embedX = this.getInsideX();
        double embedY = this.getInsideY();
        double embedW = this.getInsideWidth() - dx;
        double embedH = this.getInsideHeight() - dy;

        return MathUtil.isWithinBox(mouseX, mouseY, embedX, embedY, embedW, embedH);
    }

    /**
     * Functional method that assists with finding any widgets within the embed that accepted an event. The results of
     * the function applied to each embed widget are kept within an array list cache to prevent concurrent modification
     * exceptions. Some events may modify the embed widgets list; therefore, caching runnables avoids that problem so
     * that an event can be sent to all embed widgets without interfering with the embed widget stream.
     *
     * @param predicate A {@link Predicate} that tests if a widget acknowledged an event.
     * @return Whether a widget within this embed accepted an event emission.
     */
    protected boolean isEventListened(Predicate<DynamicWidget<?, ?>> predicate)
    {
        if (this.isInvisible())
            return false;

        return CollectionUtil.test(this.getVisibleWidgets(this.widgets.all), predicate);
    }

    /**
     * Check if the mouse click was valid for this embed and check if the mouse was over an embedded widget. If the
     * mouse was over a scrollbar, then {@code false} is returned.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the mouse click context is valid for an embedded widget.
     */
    @PublicAPI
    public boolean isWidgetClicked(double mouseX, double mouseY, int button)
    {
        if (this.isInvisible() || button != 0)
            return false;

        boolean isMouseOverScrollbar = this.widgets.scrollbars.stream()
            .filter(DynamicWidget::isVisible)
            .anyMatch(scrollbar -> scrollbar.isMouseOver(mouseX, mouseY));

        if (this.isScrollbarHeld() || isMouseOverScrollbar)
            return false;

        return this.widgets.embedded.stream().anyMatch(widget -> widget.isMouseOver(mouseX, mouseY));
    }

    /**
     * Check if the mouse click was valid for this embed. This only checks if the left mouse button was pressed and if
     * the given mouse coordinate was within the bounds of this embed. To check if an internal widget was actually
     * clicked, then use {@link #isWidgetClicked(double, double, int)}.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the mouse click context is valid for this embed.
     */
    @PublicAPI
    public boolean isValidClick(double mouseX, double mouseY, int button)
    {
        if (this.isInvisible())
            return false;

        return button == 0 && MathUtil.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height);
    }

    /**
     * Handler method for when the mouse clicks on this embed.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.mouseClicked != null && this.builder.mouseClicked.accept(mouseX, mouseY, button))
            return true;

        if (this.widgets.scrollbars.stream().anyMatch(scrollbar -> scrollbar.mouseClicked(mouseX, mouseY, button)))
            return true;

        boolean isWidgetClicked = this.isEventListened(widget -> {
            if (this.widgets.embedded.contains(widget) && !this.isMouseInsideWindow(mouseX, mouseY))
                return false;

            boolean isClicked = widget.mouseClicked(mouseX, mouseY, button);

            if (isClicked)
            {
                this.widgets.all.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);
                widget.setClickFocus();
            }

            return isClicked;
        });

        if (isWidgetClicked)
            return true;

        if (this.isMouseInsideWindow(mouseX, mouseY))
        {
            if (this.isValidClick(mouseX, mouseY, button))
                this.widgets.embedded.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);
        }

        return false;
    }

    /**
     * Handler method for when the mouse is released.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.mouseReleased != null && this.builder.mouseReleased.accept(mouseX, mouseY, button))
            return true;

        if (this.isEventListened(widget -> widget.mouseReleased(mouseX, mouseY, button)))
            return true;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse drags on the embed.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX  The x-distance of the drag.
     * @param dragY  The y-distance of the drag.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.mouseDragged != null && this.builder.mouseDragged.accept(mouseX, mouseY, button, dragX, dragY))
            return true;

        if (this.isEventListened(widget -> widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)))
            return true;

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * Handler method for when the mouse scrolls in this embed.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param deltaY The change in scroll in the y-direction. A delta of -1.0D (scroll down) moves rows up while a delta
     *               of 1.0D (scroll up) moves rows back down.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaY)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.mouseScrolled != null && this.builder.mouseScrolled.accept(mouseX, mouseY, deltaY))
            return true;

        boolean isWidgetScrolled = this.getWidgetAtPoint(mouseX, mouseY)
            .stream()
            .anyMatch(widget -> widget.mouseScrolled(mouseX, mouseY, deltaY));

        if (isWidgetScrolled)
            return true;

        if (this.isEventListened(widget -> widget.mouseScrolled(mouseX, mouseY, deltaY)))
            return true;

        return super.mouseScrolled(mouseX, mouseY, deltaY);
    }

    /**
     * Handler method for when a key is pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.keyPressed != null && this.builder.keyPressed.accept(keyCode, scanCode, modifiers))
            return true;

        if (this.isEventListened(widget -> widget.keyPressed(keyCode, scanCode, modifiers)))
            return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Handler method for when a key is released after being pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scancode.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.keyReleased != null && this.builder.keyReleased.accept(keyCode, scanCode, modifiers))
            return true;

        if (this.isEventListened(widget -> widget.keyReleased(keyCode, scanCode, modifiers)))
            return true;

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * Handler method for when a char is typed.
     *
     * @param codePoint The char that was typed.
     * @param modifiers Any held modifiers.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (this.isInvisible())
            return false;

        if (this.builder.charTyped != null && this.builder.charTyped.accept(codePoint, modifiers))
            return true;

        if (this.isEventListened(widget -> widget.charTyped(codePoint, modifiers)))
            return true;

        return super.charTyped(codePoint, modifiers);
    }

    /**
     * @return The calculated width of the embed based on the widget with the largest ending x-position.
     */
    protected int getInsideWidgetWidth()
    {
        int minX = this.getVisibleWidgets().mapToInt(DynamicWidget::getX).min().orElse(this.getInsideX());
        int maxX = this.getVisibleWidgets().mapToInt(DynamicWidget::getEndX).max().orElse(this.getInsideX() + 20);
        int margin = Math.abs(this.relativeLeft.getX() - minX) * 2;

        return Math.abs(maxX - minX) + margin;
    }

    /**
     * @return The calculated width of the embed based on the widget with the largest ending y-position.
     */
    protected int getInsideWidgetHeight()
    {
        int minY = this.getVisibleWidgets().mapToInt(DynamicWidget::getY).min().orElse(this.getInsideY());
        int maxY = this.getVisibleWidgets().mapToInt(DynamicWidget::getEndY).max().orElse(this.getInsideY() + 20);
        int margin = Math.abs(this.relativeTop.getY() - minY) * 2;

        return Math.abs(maxY - minY) + margin;
    }

    /**
     * Pause embed resizing. Use this to prevent lag spikes if a bunch of widgets are about to be added to this embed.
     *
     * @see #setResizerToResume()
     */
    @PublicAPI
    public void setResizerToPaused()
    {
        this.isResizingPaused = true;
    }

    /**
     * Unpause embed resizing.
     *
     * @see #setResizerToPaused()
     */
    @PublicAPI
    public void setResizerToResume()
    {
        this.isResizingPaused = false;
    }

    /**
     * Resize the embed based on builder context.
     */
    protected void resizeIfNeeded()
    {
        if (this.isResizingPaused)
            return;

        boolean isResized = false;

        if (this.builder.resizeWidthForWidgets || this.builder.resizeForWidgets)
        {
            boolean isWidthChanged = this.widgets.embedded.stream()
                .map(DynamicWidget::getCache)
                .map(WidgetCache::getWidth)
                .anyMatch(CacheValue::isExpired);

            if (isWidthChanged)
                isResized = true;
        }

        if (this.builder.resizeHeightForWidgets || this.builder.resizeForWidgets)
        {
            boolean isHeightChanged = this.widgets.embedded.stream()
                .map(DynamicWidget::getCache)
                .map(WidgetCache::getHeight)
                .anyMatch(CacheValue::isExpired);

            if (isHeightChanged)
                isResized = true;
        }

        if (isResized)
        {
            this.syncBeforeRender();
            this.updateSize();
        }
    }

    /**
     * Update the embed size.
     */
    protected void updateSize()
    {
        this.verticalScrollbar.setScrollAmount(0.0D);
        this.horizontalScrollbar.setSmoothScrollAmount(0.0D);

        this.updateWidgets();

        if (this.builder.resizeForWidgets)
        {
            this.resizeWidthToFitContent();
            this.resizeHeightToFitContent();
        }
        else if (this.builder.resizeWidthForWidgets)
            this.resizeWidthToFitContent();
        else if (this.builder.resizeHeightForWidgets)
            this.resizeHeightToFitContent();

        this.updateWidgets();
    }

    /**
     * Resize the embed to contain overflowing widgets. This is useful if a widget has changed in width and the inner
     * window needs resized to fit the new widget width.
     */
    @PublicAPI
    public void resizeForOverflow()
    {
        int width = this.getInsideWidgetWidth();
        int height = this.getInsideWidgetHeight();

        if (this.width != width)
            this.setWidth(width + this.getPaddingLeft() + this.getPaddingRight());

        if (this.height != height)
            this.setHeight(height + this.getPaddingTop() + this.getPaddingBottom());
    }

    /**
     * Resize the embed width so that it fits widget content.
     */
    @PublicAPI
    public void resizeWidthToFitContent()
    {
        this.setWidth(0);

        int width = this.getInsideWidgetWidth();

        if (width != this.width)
            this.setWidth(width + this.getPaddingLeft() + this.getPaddingRight());
    }

    /**
     * Resize the embed height so that it fits widget content.
     */
    @PublicAPI
    public void resizeHeightToFitContent()
    {
        this.setHeight(0);

        int height = this.getInsideWidgetHeight();

        if (height != this.height)
            this.setHeight(height + this.getPaddingTop() + this.getPaddingBottom());
    }

    /**
     * Handler method for rendering this embed.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.isInvisible())
            return;

        this.renderBackground(graphics);
        this.renderWidgets(graphics, mouseX, mouseY, partialTick);

        if (this.getBuilder().borderRenderer != null)
            this.getBuilder().borderRenderer.accept(this, graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Handler method for rendering this embed's background.
     *
     * @param graphics A {@link GuiGraphics} instance.
     */
    protected void renderBackground(GuiGraphics graphics)
    {
        this.resizeIfNeeded();

        RenderUtil.beginBatching();

        int startX = this.getInsideX();
        int startY = this.getInsideY();
        int endX = this.getInsideEndX();
        int endY = this.getInsideEndY();

        if (this.builder.backgroundGradient == null)
            RenderUtil.fill(graphics, startX, startY, endX, endY, this.builder.backgroundColor);
        else
            RenderUtil.gradient(this.builder.backgroundGradient, graphics, startX, startY, endX, endY);

        if (this.builder.borderColor.isPresent())
            RenderUtil.outline(graphics, this.x, this.y, this.width, this.height, this.builder.borderThickness, this.builder.borderColor);

        RenderUtil.endBatching();
    }

    /**
     * Syncs, without applying dynamic cache, all widgets that influence the positions of other widgets before the embed
     * renders widgets.
     */
    protected void syncBeforeRender()
    {
        this.syncEmbeddedWithoutCache();
        DynamicWidget.syncWithoutCache(this.widgets.relatives);
        DynamicWidget.syncWithoutCache(this.widgets.scrollbars);
    }

    /**
     * Handler method for rendering the embed's subscribed widgets.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.verticalScrollbar.isInvisible() && this.verticalScrollbar.getScrollAmount() > 0.0D)
            this.verticalScrollbar.setScrollAmount(0.0D);

        if (this.horizontalScrollbar.isInvisible() && this.horizontalScrollbar.getScrollAmount() > 0.0D)
            this.horizontalScrollbar.setScrollAmount(0.0D);

        boolean isVerticalVisible = this.verticalScrollbar.isVisible();
        boolean isHorizontalVisible = this.horizontalScrollbar.isVisible();

        this.syncBeforeRender();

        boolean isVerticalChanged = isVerticalVisible != this.verticalScrollbar.isVisible();
        boolean isHorizontalChanged = isHorizontalVisible != this.horizontalScrollbar.isVisible();
        boolean isBatchRendering = RenderUtil.isBatching();

        if (isVerticalChanged || isHorizontalChanged)
            this.syncBeforeRender();

        if (isBatchRendering)
            RenderUtil.endBatching();

        RenderUtil.pushZoneScissor(this.scissor.getRectangle(this));

        RenderUtil.batch(() -> {
            if (this.getBuilder().preRenderer != null)
                this.getBuilder().preRenderer.accept(this, graphics, mouseX, mouseY, partialTick);

            DynamicWidget.renderWithoutSync(this.widgets.scissored, graphics, mouseX, mouseY, partialTick);

            if (this.getBuilder().postRenderer != null)
                this.getBuilder().postRenderer.accept(this, graphics, mouseX, mouseY, partialTick);
        });

        RenderUtil.popScissor();

        if (isBatchRendering)
            RenderUtil.beginBatching();

        DynamicWidget.renderWithoutSync(this.widgets.projected, graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, MatrixUtil.getZ(graphics.pose()) + 20.0F);

        DynamicWidget.render(this.widgets.internal, graphics, mouseX, mouseY, partialTick);

        if (this.areScrollbarsVisible())
        {
            int sX = this.getInsideEndX() - this.scrollbarSize;
            int sY = this.getInsideEndY() - this.scrollbarSize;
            int eX = this.getInsideEndX();
            int eY = this.getInsideEndY();

            RenderUtil.fill(graphics, sX, sY, eX, eY, Color.SONIC_SILVER);
        }

        graphics.pose().popPose();

        DynamicWidget.applyCache(this.widgets.embedded);
        DynamicWidget.applyCache(this.widgets.relatives);
    }
}
