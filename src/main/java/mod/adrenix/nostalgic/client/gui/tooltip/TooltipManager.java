package mod.adrenix.nostalgic.client.gui.tooltip;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.common.CollectionUtil;

import java.util.Collection;
import java.util.Optional;

public interface TooltipManager {
    /**
     * @return A {@link Collection} of all {@link DynamicWidget} held by the tooltip manager.
     */
    Collection<DynamicWidget<?, ?>> getTooltipWidgets();

    /**
     * Define a specific collection of mouse-over widgets to use for getting tooltip data.
     *
     * @return A {@link Collection} of all {@link DynamicWidget} held by the tooltip manager.
     */
    default Collection<DynamicWidget<?, ?>> getMouseTooltipWidgets() {
        return this.getTooltipWidgets();
    }

    /**
     * Define a specific collection of focused widgets to use for getting tooltip data.
     *
     * @return A {@link Collection} of all {@link DynamicWidget} held by the tooltip manager.
     */
    default Collection<DynamicWidget<?, ?>> getFocusTooltipWidgets() {
        return this.getTooltipWidgets();
    }

    /**
     * Set a tooltip by finding a widget using the current mouse coordinate.
     */
    default void setTooltipUsingMouse() {
        if (this instanceof DynamicWidget<?, ?> widget && widget.isInvisible())
            return;

        Optional<DynamicWidget<?, ?>> withMouse = this.getMouseTooltipWidgets()
                .stream()
                .filter(DynamicWidget::hasTooltipBuilder)
                .filter(DynamicWidget::isVisible)
                .filter(widget -> widget.isMouseOver(widget.getMouseX(), widget.getMouseY()))
                .findFirst();

        if (withMouse.isPresent())
            withMouse.get().setTooltip();
        else {
            CollectionUtil.fromClass(this.getMouseTooltipWidgets(), TooltipManager.class)
                    .forEach(TooltipManager::setTooltipUsingMouse);
        }
    }

    /**
     * Set a tooltip by finding a widget that is focused.
     */
    default void setTooltipUsingFocused() {
        if (this instanceof DynamicWidget<?, ?> widget && widget.isInvisible())
            return;

        Optional<DynamicWidget<?, ?>> withFocus = this.getFocusTooltipWidgets()
                .stream()
                .filter(DynamicWidget::isVisible)
                .filter(DynamicWidget::isFocusedAndActive)
                .filter(DynamicWidget::hasTooltipBuilder)
                .findFirst();

        if (withFocus.isPresent())
            withFocus.get().setTooltip();
        else {
            CollectionUtil.fromClass(this.getFocusTooltipWidgets(), TooltipManager.class)
                    .forEach(TooltipManager::setTooltipUsingFocused);
        }
    }

    /**
     * Reset all tooltip timers, so they will properly trigger again in the future.
     */
    default void resetTooltipTimers() {
        for (DynamicWidget<?, ?> widget : this.getTooltipWidgets()) {
            widget.resetTooltipTimer();

            if (widget instanceof TooltipManager manager)
                manager.resetTooltipTimers();
        }
    }
}
