package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.util.Collection;
import java.util.stream.Stream;

public interface WidgetHolder {
    /**
     * @return A {@link Collection} of all {@link DynamicWidget} subscribed to this holder.
     */
    Collection<DynamicWidget<?, ?>> getWidgets();

    /**
     * @return A {@link Stream} of all {@link DynamicWidget} subscribed to this holder.
     */
    @PublicAPI
    default Stream<DynamicWidget<?, ?>> getWidgetStream() {
        return this.getWidgets().stream();
    }

    /**
     * @return A {@link Stream} of {@code visible} {@link DynamicWidget}.
     */
    @PublicAPI
    default Stream<DynamicWidget<?, ?>> getVisibleWidgets() {
        return this.getWidgets().stream().filter(DynamicWidget::isVisible);
    }

    /**
     * Add a widget to this holder.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    default void addWidget(DynamicWidget<?, ?> widget) {
        this.getWidgets().add(widget);
    }

    /**
     * Add a widget(s) to this holder.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    default void addWidgets(DynamicWidget<?, ?>... widgets) {
        for (DynamicWidget<?, ?> widget : widgets)
            this.addWidget(widget);
    }

    /**
     * Add a collection of widgets to this holder.
     *
     * @param widgets A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    default void addWidgets(Collection<DynamicWidget<?, ?>> widgets) {
        widgets.forEach(this::addWidget);
    }

    /**
     * Remove a widget from this holder.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    default void removeWidget(DynamicWidget<?, ?> widget) {
        this.getWidgets().remove(widget);
    }

    /**
     * Remove a widget(s) from this holder.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    default void removeWidgets(DynamicWidget<?, ?>... widgets) {
        for (DynamicWidget<?, ?> widget : widgets)
            this.removeWidget(widget);
    }

    /**
     * Remove a collection of widgets from this holder.
     *
     * @param widgets A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    default void removeWidgets(Collection<DynamicWidget<?, ?>> widgets) {
        widgets.forEach(this::removeWidget);
    }
}
