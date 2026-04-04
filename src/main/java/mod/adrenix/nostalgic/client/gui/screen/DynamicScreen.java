package mod.adrenix.nostalgic.client.gui.screen;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.tooltip.TooltipManager;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.WidgetHolder;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Implement this interface if a {@link Screen} uses {@link DynamicWidget}. Any other interfaces that are needed by
 * {@link DynamicWidget} are extended here.
 *
 * @param <T> The class type of the extending {@link Screen}.
 */
public interface DynamicScreen<T extends Screen> extends WidgetHolder, ParentHolder, TooltipManager {
    /**
     * A pointer to the extending screen instance is required so that dynamic widgets can update properly. Below is a
     * simple example of what an override self-method would look like.
     *
     * <pre>
     * &#64;Override
     * public T self()
     * {
     *     return this;
     * }
     * </pre>
     * Where {@code T} is replaced with the class implementing this {@link DynamicScreen}.
     *
     * @return A pointer to {@code this}.
     */
    T self();

    /**
     * @return A {@link UniqueArrayList} of all {@link DynamicWidget} subscribed to this {@link DynamicScreen}.
     */
    UniqueArrayList<DynamicWidget<?, ?>> getWidgets();

    /**
     * {@inheritDoc}
     */
    default UniqueArrayList<DynamicWidget<?, ?>> getTooltipWidgets() {
        return this.getWidgets();
    }

    /**
     * Add a widget to this screen and set its parent screen to this instance.
     */
    default void addWidget(DynamicWidget<?, ?> widget) {
        this.getWidgets().add(widget);
        widget.setScreen(this.self());
    }

    /**
     * Helper handler method for when a key is pressed.
     * <p>
     *
     * @param event The {@link KeyEvent} for this keypress.
     * @return Whether this method handled the key that was pressed.
     */
    default boolean isKeyPressed(KeyEvent event) {
        GuiEventListener focused = this.self().getFocused();

        if (focused != null && focused.keyPressed(event))
            return true;

        for (DynamicWidget<?, ?> widget : this.getWidgets()) {
            if (widget.keyPressed(event))
                return true;
        }

        if (KeyboardUtil.isEsc(event.key()) && this.self().shouldCloseOnEsc()) {
            this.self().onClose();
            return true;
        }

        if (event.hasShiftDown() && event.hasControlDown() && event.key() == InputConstants.KEY_D) {
            NostalgicTweaks.LOGGER.setDebug();
            return true;
        }

        if (event.hasShiftDown() && event.hasControlDown() && event.key() == InputConstants.KEY_T) {
            Minecraft.getInstance().reloadResourcePacks();
            return true;
        }

        if (event.hasShiftDown() && event.hasControlDown() && event.key() == InputConstants.KEY_F) {
            GuiUtil.toggleShowFps();
            return true;
        }

        return false;
    }

    /**
     * Helper handler method for a keyboard key is released.
     * <p>
     *
     * @param event The {@link KeyEvent} for this keypress.
     * @return Whether this method handled the event.
     */
    default boolean isKeyReleased(KeyEvent event) {
        return this.getWidgets().stream().anyMatch(widget -> widget.keyReleased(event));
    }

    /**
     * Helper handler method for when the mouse is clicked.
     * <p>
     *
     * @param event       The {@link MouseButtonEvent} of the mouse click.
     * @param doubleClick Whether the mouse is being double-clicked.
     * @return Whether this method handled the mouse being clicked.
     */
    default boolean isMouseClicked(MouseButtonEvent event, boolean doubleClick) {
        NostalgicTweaks.LOGGER.debug(String.format("mouseX: %s | mouseY: %s", event.x(), event.y()));

        boolean isWidgetClicked = false;

        for (DynamicWidget<?, ?> widget : this.getWidgets()) {
            if (widget.mouseClicked(event, doubleClick)) {
                this.getWidgets().stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);
                widget.setClickFocus();

                this.self().setFocused(widget);

                if (event.button() == 0)
                    this.self().setDragging(true);

                isWidgetClicked = true;
                break;
            }
        }

        if (isWidgetClicked)
            return true;

        this.getWidgets().stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);

        return false;
    }

    /**
     * Helper handler method for when the mouse is released.
     * <p>
     *
     * @param event The {@link MouseButtonEvent} of the mouse click.
     * @return Whether this method handled the event.
     */
    default boolean isMouseReleased(MouseButtonEvent event) {
        return this.getWidgets().stream().anyMatch(widget -> widget.mouseReleased(event));
    }

    /**
     * Helper handler method for when the mouse drags on the screen.
     * <p>
     *
     * @param event The {@link MouseButtonEvent} of the mouse click.
     * @param dragX The new dragged offset x-position from the mouse.
     * @param dragY The new dragged offset y-position from the mouse.
     * @return Whether this method handled the event.
     */
    default boolean isMouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return this.getWidgets().stream().anyMatch(widget -> widget.mouseDragged(event, dragX, dragY));
    }

    /**
     * Helper handler method for when the mouse scrolls on the screen.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param deltaX A positive or negative value that indicates horizontal scroll direction.
     * @param deltaY A positive or negative value that indicates vertical scroll direction.
     * @return Whether this method handled the event.
     */
    default boolean isMouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return this.getWidgets().stream().anyMatch(widget -> widget.mouseScrolled(mouseX, mouseY, deltaX, deltaY));
    }

    /**
     * Focus the first eligible widget.
     */
    default void focusFirst() {
        this.getWidgets().stream().filter(DynamicWidget::canFocus).findFirst().ifPresent(this.self()::setFocused);
    }

    /**
     * Focusing logic for dynamic widgets.
     *
     * @param focused A {@link GuiEventListener} instance.
     */
    default void setDynamicFocus(@Nullable GuiEventListener focused) {
        if (focused instanceof DynamicWidget<?, ?> dynamic) {
            if (!dynamic.canFocus())
                return;
        }

        this.getWidgets().stream().filter(DynamicWidget::isFocused).forEach(DynamicWidget::setUnfocused);
    }
}
