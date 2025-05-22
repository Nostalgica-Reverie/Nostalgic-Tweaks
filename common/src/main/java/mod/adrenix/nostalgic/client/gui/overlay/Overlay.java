package mod.adrenix.nostalgic.client.gui.overlay;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.MouseManager;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageOverlay;
import mod.adrenix.nostalgic.client.gui.overlay.types.info.MessageType;
import mod.adrenix.nostalgic.client.gui.screen.ParentHolder;
import mod.adrenix.nostalgic.client.gui.tooltip.TooltipManager;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.scrollbar.Scrollbar;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.gui.GuiOffset;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.asset.ModSprite;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.math.DynamicRectangle;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Overlay extends Screen implements RelativeLayout, WidgetHolder, ParentHolder, TooltipManager, GuiOffset
{
    /* Builders */

    /**
     * Start the creation of a new overlay instance without a title.
     *
     * @return A new {@link OverlayBuilder} instance.
     */
    public static OverlayBuilder create()
    {
        return new OverlayBuilder(Lang.EMPTY::get);
    }

    /**
     * Start the creation of a new overlay instance.
     *
     * @param title The title {@link Component} for this overlay.
     * @return A new {@link OverlayBuilder} instance.
     */
    public static OverlayBuilder create(Component title)
    {
        return new OverlayBuilder(() -> title);
    }

    /**
     * Start the creation of a new overlay instance.
     *
     * @param langKey The {@link Translation} to get a translation component from.
     * @return A new {@link OverlayBuilder} instance.
     */
    public static OverlayBuilder create(Translation langKey)
    {
        return new OverlayBuilder(langKey::get);
    }

    /**
     * Start the creation of a new overlay instance.
     *
     * @param title The {@link Supplier} that provides the {@link Component} to show on overlay header.
     * @return A new {@link OverlayBuilder} instance.
     */
    public static OverlayBuilder create(Supplier<Component> title)
    {
        return new OverlayBuilder(title);
    }

    /* Fields */

    protected Screen parentScreen;
    protected final OverlayBuilder builder;
    protected final OverlayWidgets widgets;
    protected final BlankWidget relativeTop;
    protected final BlankWidget relativeLeft;
    protected final Scrollbar horizontalScrollbar;
    protected final Scrollbar verticalScrollbar;
    protected DynamicRectangle<Overlay> scissor;
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected final int padding;
    protected final int scrollbarSize;
    protected final boolean hasBorder;
    protected final boolean canDrag;
    protected boolean isMoving;

    /* Constructor */

    Overlay(OverlayBuilder builder)
    {
        super(builder.title.get());

        this.builder = builder;
        this.widgets = builder.widgets;
        this.x = builder.x;
        this.y = builder.y;
        this.width = builder.width;
        this.height = builder.height;
        this.padding = builder.padding;
        this.canDrag = builder.canDrag;
        this.hasBorder = builder.hasBorder;
        this.scrollbarSize = 4;

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
            .pos(this::getScrollbarStartX, this::getInsideY)
            .height(this::getScrollbarHeight)
            .onVisibleChange(this::updateWidgets)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add, this.widgets.scrollbars::add));

        this.horizontalScrollbar = Scrollbar.horizontal(this::getContentWidth, this::getAverageWidgetWidth)
            .animation(Animate.easeInOutCircular(1L, TimeUnit.SECONDS))
            .size(this.scrollbarSize)
            .pos(this::getInsideX, this::getScrollbarStartY)
            .width(this::getScrollbarWidth)
            .onVisibleChange(this::updateWidgets)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add, this.widgets.scrollbars::add));

        if (this.hasBorder)
            this.createInternalWidgets();

        CollectionUtil.fromCast(this.widgets.all.stream().map(DynamicWidget::getBuilder), LayoutBuilder.class)
            .forEach(layout -> layout.relativeTo(this));

        this.widgets.external.forEach(widget -> widget.setScreen(this));
        this.setDefaultScissor();
    }

    /* Methods */

    /**
     * Creates the internal widgets that are used by every overlay instance.
     */
    private void createInternalWidgets()
    {
        IconWidget iconWidget = IconWidget.create(this.getBuilder().icon)
            .size(9)
            .posX(() -> (int) this.getX() + 7)
            .posY(() -> (int) this.getY() + 4)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add));

        IconWidget closeWidget = IconTemplate.close()
            .onPress(this::close)
            .tabOrderGroup(-1)
            .posX(() -> (int) this.getEndX() - 16)
            .posY(() -> (int) this.getY() + 4)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add));

        IconWidget infoWidget = IconTemplate.info()
            .visibleIf(this::isInformative)
            .onPress(this::showInformation)
            .leftOf(closeWidget, 2)
            .tabOrderGroup(-2)
            .build(List.of(this.widgets.all::add, this.widgets.internal::add));

        BlankWidget pressArea = BlankWidget.create()
            .rightOf(iconWidget, 3)
            .extendWidthTo(this.isInformative() ? infoWidget : closeWidget, 3)
            .height(GuiUtil.textHeight() + 2)
            .posY(() -> (int) this.getY())
            .build(List.of(this.widgets.all::add, this.widgets.internal::add));

        TextWidget.create(this.builder.title)
            .tooltip(Lang.Overlay.DRAG_TIP, 30, 1L, TimeUnit.SECONDS)
            .rightOf(iconWidget, 5)
            .pressArea(pressArea)
            .extendWidthTo(this.isInformative() ? infoWidget : closeWidget, 3)
            .onPress(this::move, Color.LEMON_YELLOW)
            .disableUnderline()
            .cannotFocus()
            .shorten()
            .build(List.of(this.widgets.all::add, this.widgets.internal::add));
    }

    /**
     * @return The {@link OverlayBuilder} that constructed this {@link Overlay} instance.
     */
    public OverlayBuilder getBuilder()
    {
        return this.builder;
    }

    /**
     * @return The parent screen that spawned this overlay.
     */
    @Nullable
    @Override
    public Screen getParentScreen()
    {
        return this.parentScreen;
    }

    /**
     * @return A {@link UniqueArrayList} of all {@link DynamicWidget} subscribed to this overlay.
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
     * @return A {@link UniqueArrayList} of all external {@link DynamicWidget} that were subscribed to this overlay.
     */
    @PublicAPI
    public UniqueArrayList<DynamicWidget<?, ?>> getExternalWidgets()
    {
        return this.widgets.external;
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
     * Add a widget to this overlay.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @Override
    public void addWidget(DynamicWidget<?, ?> widget)
    {
        widget.setScreen(this);

        if (widget.getBuilder() instanceof LayoutBuilder<?, ?> layout)
            layout.relativeTo(this);

        this.widgets.addScissoredExternal(widget);
    }

    /**
     * Add a projected widget to this overlay. A projected widget is a widget not cut out by the overlay scissor bounds.
     * It is always rendered to the screen.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public void addProjectedWidget(DynamicWidget<?, ?> widget)
    {
        this.addWidget(widget);
        this.widgets.addProjectedExternal(widget);
    }

    /**
     * Add a varargs number of projected widgets to this overlay. A projected widget is a widget not cut out by the
     * overlay scissor bounds. It is always rendered to the screen.
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
     * Remove a widget from this overlay.
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
     * Define a widget that will listen to overlay events but will not be updated nor rendered by the overlay.
     *
     * @param widget A {@link DynamicWidget} to pass events to.
     */
    @PublicAPI
    public void addListener(DynamicWidget<?, ?> widget)
    {
        this.widgets.listeners.add(widget);
    }

    /**
     * Remove a widget from the overlay's listeners list.
     *
     * @param widget The {@link DynamicWidget} to remove.
     */
    @PublicAPI
    public void removeListener(DynamicWidget<?, ?> widget)
    {
        this.widgets.listeners.remove(widget);
    }

    /**
     * Change the dynamic scissoring region of the overlay.
     *
     * @param scissor A {@link DynamicRectangle} that accepts this {@link Overlay} instance to define a scissoring
     *                region.
     */
    @PublicAPI
    public void setCustomScissor(DynamicRectangle<Overlay> scissor)
    {
        this.scissor = scissor;
    }

    /**
     * Get the default scissoring position on the x-axis.
     *
     * @param overlay The {@link Overlay} instance.
     * @return The x-axis scissoring position.
     */
    @PublicAPI
    public int getScissorX(Overlay overlay)
    {
        return overlay.getInsideX() + overlay.getScissorPadding();
    }

    /**
     * Get the default scissoring position on the y-axis.
     *
     * @param overlay The {@link Overlay} instance.
     * @return The y-axis scissoring position.
     */
    @PublicAPI
    public int getScissorY(Overlay overlay)
    {
        return overlay.getInsideY() + overlay.getScissorPadding();
    }

    /**
     * Get the default scissoring ending position on the x-axis.
     *
     * @param overlay The {@link Overlay} instance.
     * @return The ending x-axis scissoring position.
     */
    @PublicAPI
    public int getScissorEndX(Overlay overlay)
    {
        return overlay.getInsideEndX() - overlay.getScissorPadding();
    }

    /**
     * Get the default scissoring ending position on the y-axis.
     *
     * @param overlay The {@link Overlay} instance.
     * @return The ending y-axis scissoring position.
     */
    @PublicAPI
    public int getScissorEndY(Overlay overlay)
    {
        return overlay.getInsideEndY() - overlay.getScissorPadding();
    }

    /**
     * Reset the overlay's scissor region back to its default state.
     */
    @PublicAPI
    public void setDefaultScissor()
    {
        this.scissor = new DynamicRectangle<>(this::getScissorX, this::getScissorY, this::getScissorEndX, this::getScissorEndY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(@Nullable GuiEventListener focused)
    {
        if (focused instanceof DynamicWidget<?, ?> dynamic)
        {
            if (!dynamic.canFocus())
                return;

            this.setScrollOn(dynamic);
        }

        this.widgets.all.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);

        super.setFocused(focused);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends GuiEventListener> children()
    {
        return this.getWidgets();
    }

    /**
     * Add instructions to perform each tick.
     *
     * @param consumer A {@link Consumer} that accepts this {@link Overlay}.
     */
    @PublicAPI
    public void runOnTick(Consumer<Overlay> consumer)
    {
        this.builder.onTickInstructions.add(consumer);
    }

    /**
     * Add instructions to perform each tick.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    public void runOnTick(Runnable runnable)
    {
        this.runOnTick(overlay -> runnable.run());
    }

    /**
     * Add instructions to run before the overlay's {@link #onClose()} instructions are run.
     *
     * @param consumer A {@link Consumer} that accepts this {@link Overlay}.
     */
    @PublicAPI
    public void runOnClose(Consumer<Overlay> consumer)
    {
        this.builder.closingInstructions.add(consumer);
    }

    /**
     * Add instructions to run before the overlay's {@link #onClose()} instructions are run.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    public void runOnClose(Runnable runnable)
    {
        this.runOnClose((overlay) -> runnable.run());
    }

    /**
     * @return The ending x-coordinate of the inside window from true 0 with scrollbar visibility adjustment.
     */
    @PublicAPI
    public int getScreenEndX()
    {
        return this.getInsideEndX() - (this.isVerticalScrollbarVisible() ? this.scrollbarSize : 0) - this.padding;
    }

    /**
     * @return The ending y-coordinate of the inside window from true 0 with scrollbar visibility adjustment.
     */
    @PublicAPI
    public int getScreenEndY()
    {
        return this.getInsideEndY() - (this.isHorizontalScrollbarVisible() ? this.scrollbarSize : 0) - this.padding;
    }

    /**
     * @return The x-position of this overlay.
     */
    @PublicAPI
    public double getX()
    {
        return this.x;
    }

    /**
     * Change the overlay's x-position.
     *
     * @param x The new x-position.
     */
    @PublicAPI
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * @return The y-position of this overlay.
     */
    @PublicAPI
    public double getY()
    {
        return this.y;
    }

    /**
     * Change the overlay's y-position.
     *
     * @param y The new x-position.
     */
    @PublicAPI
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * @return The ending x-position of this overlay.
     */
    @PublicAPI
    public double getEndX()
    {
        return this.x + this.width;
    }

    /**
     * @return The ending y-position of this overlay.
     */
    @PublicAPI
    public double getEndY()
    {
        return this.y + this.height;
    }

    /**
     * Change the overlay's width.
     *
     * @param width The new width.
     */
    @PublicAPI
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return The width of this overlay.
     */
    @PublicAPI
    public int getWidth()
    {
        return this.width;
    }

    /**
     * Change the overlay's height.
     *
     * @param height The new height.
     */
    @PublicAPI
    public void setHeight(int height)
    {
        this.height = height;
    }

    /**
     * @return The height of this overlay.
     */
    @PublicAPI
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return The padding that surrounds the inside viewing area of the overlay.
     */
    @PublicAPI
    public int getPadding()
    {
        return this.padding;
    }

    /**
     * @return The padding that shrinks the scissoring applied to the inside viewing area of the overlay.
     */
    @PublicAPI
    public int getScissorPadding()
    {
        return this.builder.scissorPadding;
    }

    /**
     * @return The starting x-position for the inside window of the overlay.
     */
    @PublicAPI
    public int getInsideX()
    {
        return (int) this.getX() + (this.hasBorder ? 8 : 0);
    }

    /**
     * @return The starting y-position for the inside window of the overlay.
     */
    @PublicAPI
    public int getInsideY()
    {
        return (int) this.getY() + (this.hasBorder ? 15 : 0);
    }

    /**
     * @return The width of the inside window.
     */
    @PublicAPI
    public int getInsideWidth()
    {
        return this.width - (this.hasBorder ? 16 : 0);
    }

    /**
     * @return The height of the inside window.
     */
    @PublicAPI
    public int getInsideHeight()
    {
        return this.height - (this.hasBorder ? 23 : 0);
    }

    /**
     * @return The ending x-position for the inside window of the overlay.
     */
    @PublicAPI
    public int getInsideEndX()
    {
        return (int) this.getX() + this.width - (this.hasBorder ? 8 : 0);
    }

    /**
     * @return The ending y-position for the inside window of the overlay.
     */
    @PublicAPI
    public int getInsideEndY()
    {
        return (int) this.getY() + this.height - (this.hasBorder ? 8 : 0);
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

    @Override
    public int getRelativeX(DynamicWidget<?, ?> widget)
    {
        boolean isInternal = this.widgets.internal.contains(widget) && !this.widgets.relatives.contains(widget);

        return isInternal ? this.getInsideX() : this.getScrollOffsetX() + this.padding;
    }

    @Override
    public int getRelativeY(DynamicWidget<?, ?> widget)
    {
        boolean isInternal = this.widgets.internal.contains(widget) && !this.widgets.relatives.contains(widget);

        return isInternal ? this.getInsideY() : this.getScrollOffsetY() + this.padding;
    }

    @Override
    public int getAnchoredX(DynamicWidget<?, ?> widget)
    {
        return this.getInsideX() + this.padding;
    }

    @Override
    public int getAnchoredY(DynamicWidget<?, ?> widget)
    {
        return this.getInsideY() + this.padding;
    }

    /**
     * @return The size of this overlay's scrollbars.
     */
    @PublicAPI
    public int getScrollbarSize()
    {
        return this.scrollbarSize;
    }

    /**
     * @return The starting x-position for the scrollbar.
     */
    private int getScrollbarStartX()
    {
        return this.getInsideEndX() - this.scrollbarSize;
    }

    /**
     * @return The starting y-position for the scrollbar.
     */
    private int getScrollbarStartY()
    {
        return this.getInsideEndY() - this.scrollbarSize;
    }

    /**
     * @return The width for a horizontal scrollbar.
     */
    private int getScrollbarWidth()
    {
        return this.getInsideWidth() - (this.areScrollbarsVisible() ? this.scrollbarSize : 0);
    }

    /**
     * @return The height for a vertical scrollbar.
     */
    private int getScrollbarHeight()
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
     * @return Whether either scrollbar is currently being scroll`ed.
     */
    @PublicAPI
    public boolean isScrollbarHeld()
    {
        return this.verticalScrollbar.isDragging() || this.horizontalScrollbar.isDragging();
    }

    /**
     * Set the scrollbar(s) so that the given widget is visible within the overlay.
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
     * Set the scrollbar(s) so that the given widget is visible within the overlay.
     *
     * @param widget A {@link DynamicWidget} to center the scrollbar(s) on.
     */
    private void scrollTo(DynamicWidget<?, ?> widget)
    {
        int relX = widget.getX() - (this.getScrollOffsetX() + this.padding);
        int relY = widget.getY() - (this.getScrollOffsetY() + this.padding);
        int width = widget.getWidth();
        int height = widget.getHeight();

        if (this.isVerticalScrollbarVisible())
            this.verticalScrollbar.setScrollAmount(relY + (height / 2.0D) - (this.getInsideHeight() / 2.0D));

        if (this.isHorizontalScrollbarVisible())
            this.horizontalScrollbar.setScrollAmount(relX + (width / 2.0D) - (this.getInsideWidth() / 2.0D));
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
    private Stream<DynamicWidget<?, ?>> getScrollableWidgets()
    {
        return this.widgets.external.stream().filter(DynamicWidget::isNotAnchored).filter(DynamicWidget::isVisible);
    }

    /**
     * @return A calculation that determines the window width taken up by overlay widgets.
     */
    private int getContentWidth()
    {
        int endX = this.isVerticalScrollbarVisible() ? this.getScrollbarStartX() : this.getInsideEndX();
        int x1 = this.getScrollableWidgets().mapToInt(DynamicWidget::getEndX).max().orElse(endX);
        int x0 = this.relativeLeft.getX();
        int padding = this.builder.resizeForWidgets ? this.padding : this.padding * 2;

        return Math.abs(x1 - x0) + padding;
    }

    /**
     * @return A calculation that determines the window height taken up by overlay widgets.
     */
    private int getContentHeight()
    {
        int endY = this.isHorizontalScrollbarVisible() ? this.getScrollbarStartY() : this.getInsideEndY();
        int y1 = this.getScrollableWidgets().mapToInt(DynamicWidget::getEndY).max().orElse(endY);
        int y0 = this.relativeTop.getY();
        int padding = this.builder.resizeForWidgets ? this.padding : this.padding * 2;

        return Math.abs(y1 - y0) + padding;
    }

    /**
     * @return The average width of each widget assigned to this overlay.
     */
    private double getAverageWidgetWidth()
    {
        return (double) this.getScrollableWidgets()
            .mapToInt(DynamicWidget::getWidth)
            .sum() / (double) this.getContentWidth();
    }

    /**
     * @return The average height of each widget assigned to this overlay.
     */
    private double getAverageWidgetHeight()
    {
        return (double) this.getScrollableWidgets()
            .mapToInt(DynamicWidget::getHeight)
            .sum() / (double) this.getContentHeight();
    }

    /**
     * @return Whether this overlay has an informational message.
     */
    private boolean isInformative()
    {
        return this.builder.infoMessage != null;
    }

    /**
     * Show an information overlay with assistive details for this overlay.
     */
    private void showInformation()
    {
        if (this.builder.infoMessage == null)
            return;

        MessageOverlay.create(MessageType.HELP, Lang.Button.HELP.get(), this.builder.infoMessage)
            .setResizePercentage(0.65D)
            .build()
            .open();
    }

    /**
     * @return Whether this overlay does not have a texture border.
     */
    @PublicAPI
    public boolean borderless()
    {
        return !this.hasBorder;
    }

    /**
     * Set the {@link Overlay#isMoving} flag to {@code true}.
     */
    @PublicAPI
    public void move()
    {
        if (!this.canDrag)
            return;

        this.isMoving = true;
    }

    /**
     * Handler method for when the game window resizes.
     *
     * @param minecraft The {@link Minecraft} singleton instance.
     * @param width     The new screen width.
     * @param height    The new screen height.
     */
    @Override
    public void resize(Minecraft minecraft, int width, int height)
    {
        if (this.parentScreen != null)
        {
            Minecraft.getInstance().screen = this.parentScreen;

            this.parentScreen.resize(minecraft, width, height);
        }

        Minecraft.getInstance().screen = this;

        super.resize(minecraft, width, height);

        if (this.builder.aboveOrBelow != null)
            this.close();

        this.updateSize();
        this.center();
        this.resetScrollAmount();
    }

    /**
     * Runs the tick method on all overlay widgets.
     */
    @Override
    public void tick()
    {
        this.widgets.all.forEach(DynamicWidget::tick);
        this.builder.onTickInstructions.forEach(consumer -> consumer.accept(this));
    }

    /**
     * Get an optional overlay widget instance that may be located at the given mouse point.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return An optional that may contain an overlay widget instance.
     */
    @PublicAPI
    public Optional<DynamicWidget<?, ?>> getWidgetAtPoint(double mouseX, double mouseY)
    {
        return this.getVisibleWidgets(this.widgets.external)
            .filter(widget -> widget.isMouseOver(mouseX, mouseY))
            .findFirst();
    }

    /**
     * Check if the given overlay widget is completely outside the overlay.
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
     * Check if the given widget is visible to the overlay.
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
     * Check if the given mouse point is inside the overlay widget viewing area.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @return Whether the given mouse point is within the overlay widget window.
     */
    @PublicAPI
    public boolean isMouseInsideWindow(double mouseX, double mouseY)
    {
        double dx = this.isHorizontalScrollbarVisible() ? this.scrollbarSize : 0.0D;
        double dy = this.isVerticalScrollbarVisible() ? this.scrollbarSize : 0.0D;

        double overlayX = this.getInsideX();
        double overlayY = this.getInsideY();
        double overlayW = this.getInsideWidth() + dx;
        double overlayH = this.getInsideHeight() + dy;

        return MathUtil.isWithinBox(mouseX, mouseY, overlayX, overlayY, overlayW, overlayH);
    }

    /**
     * Functional method that assists with finding any widgets within the overlay that accepted an event. The results of
     * the function applied to each overlay widget are kept within an array list cache to prevent concurrent
     * modification exceptions. Some events may modify the overlay widgets list; therefore, caching runnables avoids
     * that problem so that an event can be sent to all overlay widgets without interfering with the overlay widget
     * stream.
     *
     * @param predicate @param predicate A {@link Predicate} that tests if a widget acknowledged an event.
     * @return Whether a widget within this overlay accepted an event emission.
     */
    private boolean isEventListened(Predicate<DynamicWidget<?, ?>> predicate)
    {
        if (!this.widgets.listeners.isEmpty() && CollectionUtil.test(this.widgets.listeners, predicate))
            return true;

        return CollectionUtil.test(this.getVisibleWidgets(), predicate);
    }

    /**
     * Check if the mouse click was valid for this overlay.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the mouse click context is valid for this widget.
     */
    @PublicAPI
    public boolean isValidClick(double mouseX, double mouseY, int button)
    {
        return button == 0 && MathUtil.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height);
    }

    /**
     * Handler method for when the mouse clicks on this overlay.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse click event.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        NostalgicTweaks.LOGGER.debug(String.format("mouseX: %s | mouseY: %s", mouseX, mouseY));

        boolean isWidgetClicked = this.isEventListened(widget -> {
            if (this.widgets.external.contains(widget) && !this.isMouseInsideWindow(mouseX, mouseY))
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
                this.widgets.external.stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);
        }
        else if (this.borderless())
            this.close();

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
        if (this.isMoving)
        {
            this.offScreenCheck();
            this.isMoving = false;
        }

        if (this.isEventListened(widget -> widget.mouseReleased(mouseX, mouseY, button)))
            return true;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse drags on the overlay.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @param dragX  The new dragged offset x-coordinate from the mouse.
     * @param dragY  The new dragged offset y-coordinate from the mouse.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.isMoving)
        {
            this.x += dragX;
            this.y += dragY;

            this.syncBeforeRender();

            return true;
        }

        if (this.isEventListened(widget -> widget.mouseDragged(mouseX, mouseY, button, dragX, dragY)))
            return true;

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    /**
     * Handler method for when the mouse scrolls in this overlay.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param deltaX The change in scroll in the x-direction. A delta of -1.0D (scroll right) moves rows to the right
     *               while a delta of 1.0D (scroll left) moves rows back to the left.
     * @param deltaY The change in scroll in the y-direction. A delta of -1.0D (scroll down) moves rows up while a delta
     *               of 1.0D (scroll up) moves rows back down.
     * @return Whether this method handled the event.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        boolean isWidgetScrolled = this.getWidgetAtPoint(mouseX, mouseY)
            .stream()
            .anyMatch(widget -> widget.mouseScrolled(mouseX, mouseY, deltaX, deltaY));

        if (isWidgetScrolled)
            return true;

        if (this.isEventListened(widget -> widget.mouseScrolled(mouseX, mouseY, deltaX, deltaY)))
            return true;

        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
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
        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == InputConstants.KEY_D)
        {
            NostalgicTweaks.LOGGER.setDebug();
            return true;
        }

        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == InputConstants.KEY_T)
        {
            Minecraft.getInstance().reloadResourcePacks();
            return true;
        }

        if (Screen.hasShiftDown() && Screen.hasControlDown() && keyCode == InputConstants.KEY_F)
        {
            GuiUtil.toggleShowFps();
            return true;
        }

        boolean isCustomPressed = this.builder.onKeyPressInstructions.stream()
            .anyMatch(instructions -> instructions.test(this, keyCode, scanCode, modifiers));

        if (isCustomPressed)
            return true;

        if (this.getFocused() != null && this.getFocused().keyPressed(keyCode, scanCode, modifiers))
            return true;

        if (this.isEventListened(widget -> widget.keyPressed(keyCode, scanCode, modifiers)))
            return true;

        if (KeyboardUtil.isEsc(keyCode))
        {
            this.close();
            return true;
        }

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
        if (this.getFocused() != null)
            return this.getFocused().charTyped(codePoint, modifiers);

        if (this.isEventListened(widget -> widget.charTyped(codePoint, modifiers)))
            return true;

        return super.charTyped(codePoint, modifiers);
    }

    /**
     * Resize the overlay window if a change in the widget's width/height is detected.
     */
    public void resizeIfNeeded()
    {
        if (this.isMoving)
            return;

        boolean isResized = false;

        if (this.builder.resizeWidthForWidgets || this.builder.resizeForWidgets)
        {
            boolean isWidthChanged = this.widgets.external.stream()
                .map(DynamicWidget::getCache)
                .map(WidgetCache::getWidth)
                .anyMatch(CacheValue::isExpired);

            if (isWidthChanged)
                isResized = true;
        }

        if (this.builder.resizeHeightForWidgets || this.builder.resizeForWidgets)
        {
            boolean isHeightChanged = this.widgets.external.stream()
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
     * Updates the overlay's x/y position based on the builder's x/y suppliers if any are present. This method will
     * update the overlay if it is necessary.
     */
    private void setPositionFromSuppliers()
    {
        if (this.builder.xFunction != null && this.builder.xFunction.applyAsInt(this) != this.x)
            this.x = this.builder.xFunction.applyAsInt(this);

        if (this.builder.yFunction != null && this.builder.yFunction.applyAsInt(this) != this.y)
            this.y = this.builder.yFunction.applyAsInt(this);

        if (this.builder.aboveOrBelow != null)
        {
            if (this.builder.xFunction == null)
                this.x = this.builder.aboveOrBelow.getX();

            int scaledHeight = GuiUtil.getGuiHeight();
            int aboveDiff = 0;
            int belowDiff = 0;

            int yAbove = this.builder.aboveOrBelow.getY() - this.builder.aboveOrBelowMargin - this.getHeight();
            int yBelow = this.builder.aboveOrBelow.getY() + this.builder.aboveOrBelow.getHeight() + this.builder.aboveOrBelowMargin;

            if (yAbove <= 0)
            {
                aboveDiff = Math.abs(yAbove - 1);
                yAbove = 1;
            }

            if (yBelow + this.height >= scaledHeight - 1)
                belowDiff = Math.abs((yBelow + this.height) - (scaledHeight - 1));

            boolean isAboveOrBelow = !this.builder.onlyAbove && !this.builder.onlyBelow;

            if (isAboveOrBelow)
                this.y = this.builder.aboveOrBelow.getY() > scaledHeight / 2 ? yAbove : yBelow;
            else if (this.builder.onlyAbove)
                this.y = yAbove;
            else
                this.y = yBelow;

            if (this.y == yAbove && isAboveOrBelow)
                this.height -= aboveDiff;
            else if (this.y == yBelow && isAboveOrBelow)
                this.height -= belowDiff;
        }
    }

    /**
     * Get a GUI z-offset for rendering items within an overlay.
     *
     * @return A GUI z-offset based on whether the parent screen(s) is/are overlays.
     */
    @Override
    public float getZOffset()
    {
        Screen parent = this.parentScreen;
        float zOffset = 20.0F;
        int index = 1;

        while (parent instanceof Overlay overlay)
        {
            index++;
            zOffset += 15.0F;
            parent = overlay.parentScreen;
        }

        return zOffset * index;
    }

    /**
     * Handler method for rendering this overlay.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        // Update position
        this.setPositionFromSuppliers();

        // Render parents
        if (this.parentScreen != null)
        {
            Minecraft.getInstance().screen = this.parentScreen;
            MouseManager.setPosition(-1, -1);

            this.parentScreen.render(graphics, -1, -1, partialTick);

            MouseManager.setPosition(mouseX, mouseY);
            Minecraft.getInstance().screen = this;
        }

        // Update gui offset
        graphics.pose().pushPose();
        graphics.pose().last().pose().translate(0.0F, 0.0F, this.getZOffset());

        // Render background
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        // Render first pass
        if (this.builder.firstRenderer != null)
            this.builder.firstRenderer.accept(this, graphics, mouseX, mouseY, partialTick);

        // Render border
        this.renderBorder(graphics);

        // Render widgets
        this.renderWidgets(graphics, mouseX, mouseY, partialTick);

        // Render last pass
        if (this.builder.lastRenderer != null)
            this.builder.lastRenderer.accept(this, graphics, mouseX, mouseY, partialTick);

        // Pop offset
        graphics.pose().popPose();
    }

    /**
     * Handler method for rendering this overlay's background.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int winWidth = GuiUtil.getGuiWidth();
        int winHeight = GuiUtil.getGuiHeight();

        this.resizeIfNeeded();

        RenderUtil.beginBatching();

        if (this.builder.hasShadow)
            RenderUtil.fill(graphics, 0.0F, 0.0F, winWidth, winHeight, this.builder.shadowColor);

        int startX = this.getInsideX();
        int startY = this.getInsideY();
        int endX = this.getInsideEndX();
        int endY = this.getInsideEndY();

        if (this.builder.backgroundGradient == null)
            RenderUtil.fill(graphics, startX, startY, endX, endY, this.builder.backgroundColor);
        else
            RenderUtil.gradient(this.builder.backgroundGradient, graphics, startX, startY, endX, endY);

        if (this.borderless() && !this.builder.outlineColor.isEmpty())
        {
            int x = startX - 1;
            int y = startY - 1;
            int w = endX - startX + 2;
            int h = endY - startY + 2;

            RenderUtil.outline(graphics, x, y, w, h, this.builder.outlineColor);
        }

        RenderUtil.endBatching();
    }

    /**
     * Handler method for rendering the overlay's border, background fill, and internal widgets.
     *
     * @param graphics A {@link GuiGraphics} instance.
     */
    private void renderBorder(GuiGraphics graphics)
    {
        if (this.borderless())
            return;

        RenderUtil.blitSprite(ModSprite.OVERLAY, graphics, (int) this.x, (int) this.y, this.width, this.height);
    }

    /**
     * Syncs, without applying dynamic cache, all widgets that influence the positions of other widgets before the
     * overlay renders widgets.
     */
    private void syncBeforeRender()
    {
        DynamicWidget.syncWithoutCache(this.widgets.external);
        DynamicWidget.syncWithoutCache(this.widgets.relatives);
        DynamicWidget.syncWithoutCache(this.widgets.scrollbars);
    }

    /**
     * Handler method for rendering the overlay's subscribed widgets.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
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

        if (isVerticalChanged || isHorizontalChanged)
            this.syncBeforeRender();

        RenderUtil.pushZoneScissor(this.scissor.getRectangle(this));
        DynamicWidget.renderWithoutSync(this.widgets.scissored, graphics, mouseX, mouseY, partialTick);
        RenderUtil.popScissor();

        DynamicWidget.renderWithoutSync(this.widgets.projected, graphics, mouseX, mouseY, partialTick);
        DynamicWidget.render(this.widgets.internal, graphics, mouseX, mouseY, partialTick);

        DynamicWidget.applyCache(this.widgets.external);
        DynamicWidget.applyCache(this.widgets.relatives);

        if (this.areScrollbarsVisible())
        {
            int sX = this.getInsideEndX() - this.scrollbarSize;
            int sY = this.getInsideEndY() - this.scrollbarSize;
            int eX = this.getInsideEndX();
            int eY = this.getInsideEndY();

            RenderUtil.fill(graphics, sX, sY, eX, eY, Color.SONIC_SILVER);
        }
    }

    /**
     * Centers the overlay's x/y position relative to the current size of the game window.
     */
    private void center()
    {
        if (this.builder.xFunction != null || this.builder.yFunction != null)
            return;

        double lastX = this.x;
        double lastY = this.y;

        this.x = MathUtil.center(this.width, GuiUtil.getGuiWidth());
        this.y = MathUtil.center(this.height, GuiUtil.getGuiHeight());

        if (this.x != lastX || this.y != lastY)
            this.updateWidgets();
    }

    /**
     * Resize the overlay using the percentage defined in the builder.
     */
    private void resizeWithPercentage()
    {
        if (!this.builder.resizeUsingPercentage)
            return;

        if (this.builder.resizeWidthPercentage.getAsDouble() > 0.0D)
            this.width = (int) Math.round(this.parentScreen.width * this.builder.resizeWidthPercentage.getAsDouble());

        if (this.builder.resizeHeightPercentage.getAsDouble() > 0.0D)
            this.height = (int) Math.round(this.parentScreen.height * this.builder.resizeHeightPercentage.getAsDouble());

        if (this.builder.resizeWidthMaximum > 0)
            this.width = Math.min(this.width, this.builder.resizeWidthMaximum);

        if (this.builder.resizeHeightMaximum > 0)
            this.height = Math.min(this.height, this.builder.resizeHeightMaximum);

        if (this.width < this.builder.minWidth)
            this.width = this.builder.minWidth;

        if (this.height < this.builder.minHeight)
            this.height = this.builder.minHeight;

        this.x = MathUtil.center(this.width, GuiUtil.getGuiWidth());
        this.y = MathUtil.center(this.height, GuiUtil.getGuiHeight());
    }

    /**
     * Check if the overlay is outside the game's window. If it is, then the overlay's x/y position will be updated so
     * that the overlay is visible.
     */
    private void offScreenCheck()
    {
        double lastX = this.x;
        double lastY = this.y;
        int lastWidth = this.width;
        int lastHeight = this.height;

        if (this.x + this.width <= 0.0D)
            this.x = 0.0D;

        if (this.x > GuiUtil.getGuiWidth() - 5.0D)
            this.x = GuiUtil.getGuiWidth() - 26.0D;

        if (this.y < 0.0D)
            this.y = 0.0D;

        if (this.y > GuiUtil.getGuiHeight())
            this.y = GuiUtil.getGuiHeight() - 16.0D;

        if (this.width > GuiUtil.getGuiWidth() - 10.0D)
            this.width = GuiUtil.getGuiWidth() - 10;

        if (this.height > GuiUtil.getGuiHeight() - 10.0D)
            this.height = GuiUtil.getGuiHeight() - 10;

        if (this.x != lastX || this.y != lastY || this.width != lastWidth || this.height != lastHeight)
            this.center();
    }

    /**
     * @return The calculated width of the inside overlay window based on the widget with the largest ending x-position.
     */
    private int getInsideWidgetWidth()
    {
        int minX = this.getVisibleWidgets(this.widgets.external)
            .mapToInt(DynamicWidget::getX)
            .min()
            .orElse(this.getInsideX());

        int maxX = this.getVisibleWidgets(this.widgets.external)
            .mapToInt(DynamicWidget::getEndX)
            .max()
            .orElse(this.getInsideX() + 20);

        int margin = Math.abs(this.relativeLeft.getX() - minX) * 2;

        return Math.abs(maxX - minX) + margin;
    }

    /**
     * @return The calculated width of the inside overlay window based on the widget with the largest ending y-position.
     */
    private int getInsideWidgetHeight()
    {
        int minY = this.getVisibleWidgets(this.widgets.external)
            .mapToInt(DynamicWidget::getY)
            .min()
            .orElse(this.getInsideY());

        int maxY = this.getVisibleWidgets(this.widgets.external)
            .mapToInt(DynamicWidget::getEndY)
            .max()
            .orElse(this.getInsideY() + 20);

        int margin = Math.abs(this.relativeTop.getY() - minY) * 2;

        return Math.abs(maxY - minY) + margin;
    }

    /**
     * Resize the overlay window so that all widgets are seen in the inner window. If the resize causes the width or
     * height to be greater than the game window's width or height, then the width and height will be updated to fit
     * within the game window.
     */
    @PublicAPI
    public void resizeToFitContent()
    {
        this.setWidth(0);
        this.setHeight(0);

        this.updateWidgets();
        this.resizeForOverflow();
    }

    /**
     * Resize the overlay window to contain overflowing widgets. This is useful if a widget has changed in width and the
     * inner window needs resized to fit the new widget width.
     */
    @PublicAPI
    public void resizeForOverflow()
    {
        int width = this.getInsideWidgetWidth();
        int height = this.getInsideWidgetHeight();

        if (this.width != width)
            this.setWidth(width + this.padding + (this.hasBorder ? 16 : 0) + this.scrollbarSize);

        if (this.height != height)
            this.setHeight(height + this.padding + (this.hasBorder ? 23 : 0) + this.scrollbarSize);

        if (this.width != width || this.height != height)
        {
            this.offScreenCheck();
            this.center();
        }
    }

    /**
     * Resize the overlay width so that it fits overlay content, if and only if, that content's ending x-position is
     * smaller than the current overlay width. If the widget content exceeds the overlay's width, then no resize will
     * occur, and a horizontal scrollbar will become visible.
     */
    @PublicAPI
    public void shrinkWidthToFitContent()
    {
        this.updateWidgets();

        int width = this.getInsideWidgetWidth() + this.padding;

        if (width < this.width)
        {
            this.setWidth(width - this.padding + (this.hasBorder ? 16 : this.padding * 2) + (this.hasBorder ? this.scrollbarSize : 0));
            this.offScreenCheck();
            this.center();
        }
    }

    /**
     * Resize the overlay height so that it fits overlay content, if and only if, that content's ending y-position is
     * smaller than the current overlay height. If the widget content exceeds the overlay's height, then no resizing
     * will occur, and a vertical scrollbar will become visible.
     */
    @PublicAPI
    public void shrinkHeightToFitContent()
    {
        this.updateWidgets();

        int height = this.getInsideWidgetHeight() + this.padding;

        if (height < this.height)
        {
            this.setHeight(height - this.padding + (this.hasBorder ? 23 : this.padding * 2) + (this.hasBorder ? this.scrollbarSize : 0));
            this.offScreenCheck();
            this.center();
        }
    }

    /**
     * Updates all overlay widgets x/y positions as needed.
     */
    @PublicAPI
    public void updateWidgets()
    {
        DynamicWidget.syncWithoutCache(this.widgets.relatives);
        DynamicWidget.syncWithoutCache(this.widgets.external);
    }

    /**
     * Update the size of the overlay based on current builder properties.
     */
    @PublicAPI
    public void updateSize()
    {
        this.verticalScrollbar.setScrollAmount(0.0D);
        this.horizontalScrollbar.setSmoothScrollAmount(0.0D);

        this.resizeWithPercentage();

        if (this.builder.resizeForWidgets)
            this.resizeToFitContent();
        else if (this.builder.resizeWidthForWidgets)
            this.shrinkWidthToFitContent();
        else if (this.builder.resizeHeightForWidgets)
            this.shrinkHeightToFitContent();

        this.updateWidgets();
        this.offScreenCheck();

        if (this.builder.onResize != null)
            this.builder.onResize.accept(this);
    }

    /**
     * Open this overlay. This will set the current Minecraft screen instance to this overlay. If the current Minecraft
     * screen instance is this overlay, then this method will return prematurely.
     *
     * @return The {@link Overlay} instance that called this method.
     */
    @PublicAPI
    public Overlay open()
    {
        if (Minecraft.getInstance().screen == this)
            return this;

        this.parentScreen = Minecraft.getInstance().screen;
        Minecraft.getInstance().setScreen(this);

        this.updateSize();

        return this;
    }

    /**
     * Close this overlay instance.
     */
    @PublicAPI
    public void close()
    {
        this.getBuilder().closingInstructions.forEach(consumer -> consumer.accept(this));

        if (this.builder.onClose != null)
            this.builder.onClose.run();

        Minecraft.getInstance().screen = this.parentScreen;
    }

    /**
     * If the current Minecraft screen is this screen, then this overlay will close. Otherwise, this overlay will open.
     */
    @PublicAPI
    public void openOrClose()
    {
        if (Minecraft.getInstance().screen == this)
            this.close();
        else
            this.open();
    }
}
