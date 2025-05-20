package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.MouseManager;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.tooltip.Tooltip;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderPass;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.RecursionAvoidance;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class DynamicWidget<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
    implements Renderable, GuiEventListener, LayoutElement
{
    /* Static */

    /**
     * Sync the given collection of widgets using their dynamic functions and then update the collection's cache.
     *
     * @param widgets A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    public static void sync(Collection<? extends DynamicWidget<?, ?>> widgets)
    {
        syncWithoutCache(widgets);
        applyCache(widgets);
    }

    /**
     * Apply {@link WidgetCache#update()} to the given collection of widgets.
     *
     * @param widgets A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    public static void applyCache(Collection<? extends DynamicWidget<?, ?>> widgets)
    {
        widgets.stream().map(DynamicWidget::getCache).forEach(WidgetCache::update);
    }

    /**
     * Sync the given collection widgets using their dynamic functions. The given collection of widgets will not have
     * their cache updated.
     *
     * @param widgets A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    public static void syncWithoutCache(Collection<? extends DynamicWidget<?, ?>> widgets)
    {
        widgets.forEach(widget -> {
            widget.builder.relativeSync();
            widget.builder.preSync();
            widget.builder.sync();
            widget.builder.postSync();
        });
    }

    /**
     * Sync and render the given collection of widgets.
     *
     * @param widgets     A {@link Collection} of {@link DynamicWidget}.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static void render(Collection<? extends DynamicWidget<?, ?>> widgets, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        sync(widgets);
        renderWithoutSync(widgets, graphics, mouseX, mouseY, partialTick);
    }

    /**
     * Render without syncing the given collection of widgets.
     *
     * @param widgets     A {@link Collection} of {@link DynamicWidget}.
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @PublicAPI
    public static void renderWithoutSync(Collection<? extends DynamicWidget<?, ?>> widgets, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderPass.apply(widgets, DynamicWidget::getRenderPass, widget -> widget.render(graphics, mouseX, mouseY, partialTick));
    }

    /* Fields */

    protected int x = 0;
    protected int y = 0;
    protected int width = 0;
    protected int height = 0;
    protected int tabOrderGroup;
    protected boolean active = true;
    protected boolean visible = true;
    protected boolean focused = false;
    protected final Builder builder;
    protected final RenderPass renderPass;
    @Nullable protected Screen screen;
    public final WidgetCache cache;
    private final RecursionAvoidance hoverOrFocusSync;

    /* Constructor */

    protected DynamicWidget(Builder builder)
    {
        this.builder = builder;
        this.renderPass = builder.renderPass;
        this.tabOrderGroup = builder.tabOrderGroup;
        this.hoverOrFocusSync = RecursionAvoidance.create();
        this.cache = WidgetCache.from(this);
    }

    /* Methods */

    /**
     * @return The {@link Widget} type of this {@link DynamicWidget}.
     */
    @SuppressWarnings("unchecked")
    public Widget self()
    {
        return (Widget) this;
    }

    /**
     * @return The {@link Builder} instance that created this {@link Widget}.
     */
    @PublicAPI
    public Builder getBuilder()
    {
        return this.builder;
    }

    /**
     * @return The {@link WidgetCache} for this dynamic widget.
     */
    @PublicAPI
    public WidgetCache getCache()
    {
        return this.cache;
    }

    /**
     * Define the screen that holds this widget.
     *
     * @param screen A {@link Screen} instance.
     */
    @PublicAPI
    public void setScreen(@Nullable Screen screen)
    {
        this.screen = screen;
    }

    /**
     * Find the screen holding this widget instance.
     *
     * @return A {@code nullable} {@link Screen} instance.
     */
    @Nullable
    public Screen getScreen()
    {
        if (this.screen != null)
            return this.screen;

        return GuiUtil.getScreen().orElse(null);
    }

    /**
     * If the parent screen is an {@link Overlay}, then the overlay's inside window width is used.
     *
     * @return A width from the screen the widget is subscribed.
     */
    @PublicAPI
    public int getScreenWidth()
    {
        Screen screen = this.getScreen();

        if (screen == null)
            return 0;

        if (screen instanceof Overlay overlay)
            return overlay.getScreenEndX();

        return screen.width;
    }

    /**
     * If the parent screen is an {@link Overlay}, then the overlay's inside window height is used.
     *
     * @return A height from the screen the widget is subscribed.
     */
    @PublicAPI
    public int getScreenHeight()
    {
        Screen screen = this.getScreen();

        if (screen == null)
            return 0;

        if (screen instanceof Overlay overlay)
            return overlay.getScreenEndY();

        return screen.height;
    }

    /**
     * Set the information body for the widget's tooltip.
     */
    private void setInfoTooltip()
    {
        if (this.builder.infoTooltip != null)
            Tooltip.setInfo(this.builder.infoTooltip.get());

        if (this.builder.multilineInfoTooltip != null)
            Tooltip.setInfo(this.builder.multilineInfoTooltip.get());

        if (this.isInactive())
        {
            if (this.builder.disabledInfoTooltip != null)
                Tooltip.setInfo(this.builder.disabledInfoTooltip.get());

            if (this.builder.disabledMultilineInfoTooltip != null)
                Tooltip.setInfo(this.builder.disabledMultilineInfoTooltip.get());
        }
    }

    /**
     * Set the tooltip for an active widget.
     */
    private void setActiveTooltip()
    {
        boolean isTimeInvalid = this.builder.tooltipTimer == null || !this.builder.tooltipTimer.getFlag();
        boolean isInactive = this.builder.disabledTooltipTimer != null && this.isInactive();
        boolean isHidden = this.builder.hideTimer != null && this.builder.hideTimer.getFlag();

        if (isTimeInvalid || isInactive || isHidden)
            return;

        if (this.builder.tooltip != null)
        {
            Component tooltip = this.builder.tooltip.get();

            if (GuiUtil.isComponentPresent(tooltip))
            {
                Tooltip.setTooltip(tooltip);
                Tooltip.setRelativeToIfFocused(this);

                this.setInfoTooltip();
            }
        }
        else if (this.builder.multilineTooltip != null)
        {
            List<Component> components = this.builder.multilineTooltip.get();

            if (GuiUtil.isComponentPresent(components))
            {
                Tooltip.setListTooltip(components);
                Tooltip.setRelativeToIfFocused(this);

                this.setInfoTooltip();
            }
        }
    }

    /**
     * Set the tooltip for an inactive widget.
     */
    private void setInactiveTooltip()
    {
        boolean isTimeInvalid = this.builder.disabledTooltipTimer == null || !this.builder.disabledTooltipTimer.getFlag();
        boolean isHidden = this.builder.hideTimer != null && this.builder.hideTimer.getFlag();

        if (this.isActive() || isTimeInvalid || isHidden)
            return;

        if (this.builder.disabledTooltip != null)
        {
            Component tooltip = this.builder.disabledTooltip.get();

            if (GuiUtil.isComponentPresent(tooltip))
            {
                Tooltip.setTooltip(tooltip);
                Tooltip.setRelativeToIfFocused(this);

                this.setInfoTooltip();
            }
        }
        else if (this.builder.disabledMultilineTooltip != null)
        {
            List<Component> components = this.builder.disabledMultilineTooltip.get();

            if (GuiUtil.isComponentPresent(components))
            {
                Tooltip.setListTooltip(components);
                Tooltip.setRelativeToIfFocused(this);

                this.setInfoTooltip();
            }
        }
    }

    /**
     * Resets the active tooltip timer and hide timer if they are available.
     */
    private void resetActiveTooltipTimer()
    {
        if (this.builder.tooltipTimer != null)
        {
            this.builder.tooltipTimer.reset();

            if (this.builder.hideTimer != null)
                this.builder.hideTimer.reset();
        }
    }

    /**
     * Resets the inactive tooltip timer and hide timer if they are available.
     */
    private void resetInactiveTooltipTimer()
    {
        if (this.builder.disabledTooltipTimer != null)
        {
            this.builder.disabledTooltipTimer.reset();

            if (this.builder.hideTimer != null)
                this.builder.hideTimer.reset();
        }
    }

    /**
     * Reset the widget's tooltip timers.
     */
    public void resetTooltipTimer()
    {
        boolean isComboTimer = this.builder.tooltipTimer != null && this.builder.disabledTooltipTimer != null;
        boolean isMouseNotOver = !this.isMouseOver(this.getMouseX(), this.getMouseY());
        boolean isNotHoveredOrFocused = !this.isHoveredOrFocused();

        if (isComboTimer)
        {
            if (this.isActive() && isNotHoveredOrFocused)
                this.resetActiveTooltipTimer();

            if (this.isInactive() && isMouseNotOver)
                this.resetInactiveTooltipTimer();
        }
        else
        {
            if (isNotHoveredOrFocused || (this.isInactive() && isMouseNotOver))
                this.resetActiveTooltipTimer();

            if (isMouseNotOver)
                this.resetInactiveTooltipTimer();
        }
    }

    /**
     * Check if a widget has a tooltip to show.
     *
     * @return Whether this widget has a tooltip.
     */
    public boolean hasTooltipBuilder()
    {
        return this.builder.tooltipTimer != null || this.builder.disabledTooltipTimer != null;
    }

    /**
     * Define a tooltip that will be stored in {@link Tooltip}.
     */
    public void setTooltip()
    {
        this.setActiveTooltip();
        this.setInactiveTooltip();
    }

    /**
     * Check if the given mouse coordinate is a valid point for this widget.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return Whether the mouse coordinate is valid for this widget.
     */
    protected boolean isValidPoint(double mouseX, double mouseY)
    {
        return this.isActive() && this.isVisible() && this.isMouseOver(mouseX, mouseY);
    }

    /**
     * Check if the given mouse coordinate is an invalid point for this widget.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return Whether the mouse coordinate is invalid for this widget.
     */
    protected boolean isInvalidPoint(double mouseX, double mouseY)
    {
        return !this.isValidPoint(mouseX, mouseY);
    }

    /**
     * Check if the mouse click was valid for this widget.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the mouse click context is valid for this widget.
     */
    protected boolean isValidClick(double mouseX, double mouseY, int button)
    {
        return button == 0 && this.isValidPoint(mouseX, mouseY);
    }

    /**
     * Check if the mouse click was invalid for this widget.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the mouse click context is invalid for this widget.
     */
    protected boolean isInvalidClick(double mouseX, double mouseY, int button)
    {
        return !this.isValidClick(mouseX, mouseY, button);
    }

    /**
     * Handler method for when the mouse is moved within the widget.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY the y-coordinate of the mouse.
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        GuiEventListener.super.mouseMoved(mouseX, mouseY);
    }

    /**
     * Handler method for when the mouse is clicked.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether this method handled the mouse being clicked.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return GuiEventListener.super.mouseClicked(mouseX, mouseY, button);
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
        return GuiEventListener.super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * Instructions for when the mouse is dragging within this widget.
     *
     * @param mouseX The current x-coordinate of the mouse.
     * @param mouseY The current y-coordinate of the mouse.
     * @param dragX  The new dragged offset x-coordinate from the mouse.
     * @param dragY  The new dragged offset y-coordinate from the mouse.
     */
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
    {
    }

    /**
     * Handler method for when the mouse drags on the widget.
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
        if (this.isValidClick(mouseX, mouseY, button))
        {
            this.onDrag(mouseX, mouseY, dragX, dragY);

            return true;
        }

        return false;
    }

    /**
     * Handler method for when the mouse scrolls.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @param deltaX Vertical scroll amount.
     * @param deltaY Horizontal scroll amount.
     * @return Whether this method handled the mouse scroll.
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        return GuiEventListener.super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    /**
     * Check if the given mouse coordinate is over the widget.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     * @return Whether the mouse is over the widget.
     */
    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return MathUtil.isWithinBox(mouseX, mouseY, this.x, this.y, this.width, this.height);
    }

    /**
     * Handler method for when a key is pressed.
     *
     * @param keyCode   The key code that was pressed.
     * @param scanCode  A key scan code.
     * @param modifiers Key code modifiers.
     * @return Whether this method handled the key that was pressed.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return GuiEventListener.super.keyPressed(keyCode, scanCode, modifiers);
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
        return GuiEventListener.super.keyReleased(keyCode, scanCode, modifiers);
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
        return GuiEventListener.super.charTyped(codePoint, modifiers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocused(boolean focused)
    {
        if (focused && !this.canFocus())
            return;

        this.focused = focused;

        if (this.builder.whenFocused != null && focused)
            this.builder.whenFocused.accept(this.builder.widget.get());
    }

    /**
     * Set this widget as focused.
     */
    @PublicAPI
    public void setFocused()
    {
        this.setFocused(true);
    }

    /**
     * Set this widget as unfocused.
     */
    @PublicAPI
    public void setUnfocused()
    {
        this.setFocused(false);
    }

    /**
     * If this widget is eligible for focusing after a click, then it will be focused.
     */
    @PublicAPI
    public void setClickFocus()
    {
        this.setFocused(this.getBuilder().focusOnClick);
    }

    /**
     * @return Whether this widget can be focused due to a mouse click event.
     */
    @PublicAPI
    public boolean canFocusOnClick()
    {
        return this.getBuilder().focusOnClick;
    }

    /**
     * @return Whether this widget can be focused.
     */
    @PublicAPI
    public boolean canFocus()
    {
        return this.getBuilder().canFocus.getAsBoolean();
    }

    /**
     * @return Whether this widget is focused.
     */
    @Override
    public boolean isFocused()
    {
        return this.focused;
    }

    /**
     * @return Whether this widget is unfocused.
     */
    @PublicAPI
    public boolean isUnfocused()
    {
        return !this.isFocused();
    }

    /**
     * @return The x-coordinate of the mouse set from the widget's screen.
     */
    @PublicAPI
    public int getMouseX()
    {
        return MouseManager.getX();
    }

    /**
     * @return The y-coordinate of the mouse set from the widget's screen.
     */
    @PublicAPI
    public int getMouseY()
    {
        return MouseManager.getY();
    }

    /**
     * @return Whether widget is both {@code active} and {@code focused}.
     */
    @PublicAPI
    public boolean isFocusedAndActive()
    {
        return this.isActive() && this.isFocused();
    }

    /**
     * @return Whether this widget is {@code hovered} or {@code focused}.
     */
    @PublicAPI
    public boolean isHoveredOrFocused()
    {
        if (this.isInvisible())
            return false;

        int mouseX = this.getMouseX();
        int mouseY = this.getMouseY();

        boolean isHoveredOrFocused = this.isFocused() || this.isMouseOver(mouseX, mouseY);

        if (this.hoverOrFocusSync.isProcessing())
            return isHoveredOrFocused;

        boolean areAnySyncedHoveredOrFocused = Boolean.TRUE.equals(this.hoverOrFocusSync.process(() -> this.builder.hoverSync.stream()
            .anyMatch(DynamicWidget::isHoveredOrFocused)));

        return isHoveredOrFocused || areAnySyncedHoveredOrFocused;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if (this.isInactive() || this.isInvisible() || !this.canFocus())
            return null;

        if (this.isUnfocused())
            return ComponentPath.leaf(this);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath()
    {
        return GuiEventListener.super.getCurrentFocusPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScreenRectangle getRectangle()
    {
        return LayoutElement.super.getRectangle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPosition(int x, int y)
    {
        LayoutElement.super.setPosition(x, y);
    }

    /**
     * This is not used by {@link DynamicWidget} since this {@code override} requires an {@link AbstractWidget}.
     */
    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTabOrderGroup()
    {
        return this.tabOrderGroup;
    }

    /**
     * This is <b>not</b> the same as {@link #getRectangle()} inasmuch that this method returns a position rectangle
     * that is used by other mod utilities.
     *
     * @return A {@link Rectangle} instance with the widget's current position.
     */
    @PublicAPI
    public Rectangle getPositionRectangle()
    {
        return new Rectangle(this.x, this.y, this.getEndX(), this.getEndY());
    }

    /**
     * Define the tab order group for this widget. The game categorizes groups of widgets by their tab order group
     * number. Groups of widgets with lower group numbers are selected first. The order within the group is defined by
     * when the widget was subscribed to the screen. Any widget that does not define their tab order group number is
     * automatically assigned to group {@code 0}.
     *
     * @param tabOrderGroup A tab group order number.
     */
    @PublicAPI
    public void setTabOrderGroup(int tabOrderGroup)
    {
        this.tabOrderGroup = tabOrderGroup;
    }

    /**
     * Shortcut for setting both the x-coordinate and y-coordinate for this widget.
     *
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    @PublicAPI
    public void pos(int x, int y)
    {
        this.setX(x);
        this.setY(y);
    }

    /**
     * Set the x-coordinate for this widget.
     *
     * @param x The x-coordinate.
     */
    @Override
    public void setX(int x)
    {
        if (this.builder.forceRelativeX && this.builder.isProcessingLayout)
            return;

        this.x = x;
    }

    /**
     * Set the y-coordinate for this widget.
     *
     * @param y The y-coordinate.
     */
    @Override
    public void setY(int y)
    {
        if (this.builder.forceRelativeY && this.builder.isProcessingLayout)
            return;

        this.y = y;
    }

    /**
     * @return The widget's x-coordinate.
     */
    @Override
    public int getX()
    {
        return this.x;
    }

    /**
     * @return The widget's y-coordinate.
     */
    @Override
    public int getY()
    {
        return this.y;
    }

    /**
     * Get the ending position on the x-axis for this widget. This adds the widget's {@code x-coordinate} with its
     * current {@code width}.
     *
     * @return The ending x-coordinate for the widget.
     */
    @PublicAPI
    public int getEndX()
    {
        return this.x + this.width;
    }

    /**
     * Get the ending position on the y-axis for this widget. This adds the widget's {@code y-coordinate} with its
     * current {@code height}.
     *
     * @return The ending y-coordinate for the widget.
     */
    @PublicAPI
    public int getEndY()
    {
        return this.y + this.height;
    }

    /**
     * This position data is only useful for layouts that implement {@link RelativeLayout}. If no relative layout was
     * assigned to this widget, then the widget's x-coordinate is returned.
     *
     * @return The widget's relative x-coordinate to a relative layout.
     */
    @PublicAPI
    public int getRelativeX()
    {
        if (this.builder.relativeLayout != null)
            return this.x - this.builder.getRelativeLayoutX();

        return this.x;
    }

    /**
     * This position data is only useful for layouts that implement {@link RelativeLayout}. If no relative layout was
     * assigned to this widget, then the widget's y-coordinate is returned.
     *
     * @return The widget's relative y-coordinate to a relative layout.
     */
    @PublicAPI
    public int getRelativeY()
    {
        if (this.builder.relativeLayout != null)
            return this.y - this.builder.getRelativeLayoutY();

        return this.y;
    }

    /**
     * Set the widget's width.
     *
     * @param width The new width.
     */
    @PublicAPI
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * Set the widget's height.
     *
     * @param height The new height.
     */
    @PublicAPI
    public void setHeight(int height)
    {
        this.height = height;
    }

    /**
     * @return The widget's width.
     */
    @Override
    public int getWidth()
    {
        return this.width;
    }

    /**
     * @return The widget's height.
     */
    @Override
    public int getHeight()
    {
        return this.height;
    }

    /**
     * @return The widget's width scaling amount.
     */
    @PublicAPI
    public float getScaleWidth()
    {
        return this.builder.getScaleWidth(this.self());
    }

    /**
     * @return The widget's height scaling amount.
     */
    @PublicAPI
    public float getScaleHeight()
    {
        return this.builder.getScaleHeight(this.self());
    }

    /**
     * @return The widget's average scale size for square scaling.
     */
    @PublicAPI
    public float getSquareScale()
    {
        return (this.getScaleWidth() + this.getScaleHeight()) / 2.0F;
    }

    /**
     * Set the {@code active} state for this widget.
     *
     * @param state The new flag state.
     */
    @PublicAPI
    public void setActive(boolean state)
    {
        this.active = state;
    }

    /**
     * Set this widget's {@code active} flag to {@code true}.
     */
    @PublicAPI
    public void setActive()
    {
        this.setActive(true);
    }

    /**
     * Set this widget's {@code active} flag to {@code false}.
     */
    @PublicAPI
    public void setInactive()
    {
        this.setActive(false);
    }

    /**
     * If the predicate was never set using the builder, then the current {@code active} flag is returned.
     *
     * @return Whether the widget should be active or inactive.
     */
    @PublicAPI
    public boolean getActiveTest()
    {
        if (this.builder.active == null)
            return this.active;

        return this.builder.active.test(this.self());
    }

    /**
     * @return Whether this widget's {@code active} flag is {@code true}.
     */
    @PublicAPI
    public boolean isActive()
    {
        return this.active;
    }

    /**
     * @return Whether this widget's {@code active} flag is {@code false}.
     */
    @PublicAPI
    public boolean isInactive()
    {
        return !this.isActive();
    }

    /**
     * Set the {@code visible} state for this widget.
     *
     * @param state The new flag state.
     */
    @PublicAPI
    public void setVisible(boolean state)
    {
        this.visible = state;
    }

    /**
     * Set this widget's {@code visible} flag to {@code true}.
     */
    @PublicAPI
    public void setVisible()
    {
        this.setVisible(true);
    }

    /**
     * Set this widget's {@code visible} flag to {@code false}.
     */
    @PublicAPI
    public void setInvisible()
    {
        this.setVisible(false);
    }

    /**
     * If the predicate was never set using the builder, then the current {@code visible} flag is returned.
     *
     * @return Whether the widget should be visible or invisible.
     */
    @PublicAPI
    public boolean getVisibleTest()
    {
        if (this.builder.visible == null)
            return this.visible;

        return this.builder.visible.test(this.self());
    }

    /**
     * @return Whether this widget's {@code visible} flag is {@code true}.
     */
    @PublicAPI
    public boolean isVisible()
    {
        return this.visible;
    }

    /**
     * @return Whether this widget's {@code visible} flag is {@code false}.
     */
    @PublicAPI
    public boolean isInvisible()
    {
        return !this.isVisible();
    }

    /**
     * @return Whether this widget is anchored within a {@link RelativeLayout}.
     */
    @PublicAPI
    public boolean isAnchored()
    {
        if (this.builder.relativeLayout instanceof DynamicWidget<?, ?> relativeWidget)
        {
            if (relativeWidget.isAnchored())
                return true;
        }

        return this.builder.relativeAnchor;
    }

    /**
     * @return Whether this widget follows its {@link RelativeLayout}.
     */
    @PublicAPI
    public boolean isNotAnchored()
    {
        return !this.isAnchored();
    }

    /**
     * Instructions to run every game tick.
     */
    @PublicAPI
    public void tick()
    {
    }

    /**
     * @return The {@link RenderPass} assigned to this widget during the building phase.
     */
    @PublicAPI
    public RenderPass getRenderPass()
    {
        return this.renderPass;
    }

    /**
     * Render instructions for the widget.
     *
     * @param graphics    The {@link GuiGraphics} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
    }

    /**
     * Check if this widget has the given data.
     *
     * @param data A data {@link Object} instance.
     * @return Whether this widget contains the given data.
     */
    @PublicAPI
    public boolean has(Object data)
    {
        return this.builder.attachedData.contains(data);
    }

    /**
     * Check if this widget has the given data, and if so, is yielded back in an {@link Optional}.
     *
     * @param data A data {@link Object} instance.
     * @param <T>  The class type of the data.
     * @return An {@link Optional} containing the passed data argument if it was attached to this widget.
     */
    @PublicAPI
    @SuppressWarnings("unchecked") // Object#equals is used, and therefore, types must be equivalent
    public <T> Optional<T> maybeHas(T data)
    {
        return (Optional<T>) this.builder.attachedData.stream().filter(obj -> obj.equals(data)).findFirst();
    }

    /**
     * Find data attachments that match the given class type.
     *
     * @param data A data {@link Class} type.
     * @param <T>  The class type of the data to search for.
     * @return A {@link Stream} of objects that are instances of the given data class type.
     */
    @PublicAPI
    public <T> Stream<T> find(Class<T> data)
    {
        return CollectionUtil.fromCast(this.builder.attachedData, data);
    }

    /**
     * @return Whether the mod is in debug mode.
     */
    protected boolean isDebugging()
    {
        return NostalgicTweaks.isDebugging();
    }

    /**
     * @return Whether the mod is <b>not</b> in debug mode.
     */
    protected boolean isNotDebugging()
    {
        return !this.isDebugging();
    }

    /**
     * Render debugging squares that indicate where this widget is positioned on the screen.
     *
     * @param graphics The {@link GuiGraphics} object used for rendering.
     */
    protected void renderDebug(GuiGraphics graphics)
    {
        if (this.isNotDebugging() || this.isInvisible())
            return;

        RenderUtil.deferredRenderer(() -> {
            int startX = this.x;
            int startY = this.y;
            int endX = this.getEndX();
            int endY = this.getEndY();

            graphics.pose().pushPose();
            graphics.pose().translate(0.0D, 0.0D, 1.0D);

            RenderUtil.beginBatching();
            RenderUtil.fill(graphics, startX, startY, endX, startY + 1, Color.RED.fromAlpha(0.4F));
            RenderUtil.fill(graphics, endX - 1, startY + 1, endX, endY - 1, Color.PINK.fromAlpha(0.4F));
            RenderUtil.fill(graphics, startX, startY + 1, startX + 1, endY - 1, Color.CYAN.fromAlpha(0.4F));
            RenderUtil.fill(graphics, startX, endY - 1, endX, endY, Color.GREEN.fromAlpha(0.4F));
            RenderUtil.endBatching();

            graphics.pose().popPose();
        });
    }
}
