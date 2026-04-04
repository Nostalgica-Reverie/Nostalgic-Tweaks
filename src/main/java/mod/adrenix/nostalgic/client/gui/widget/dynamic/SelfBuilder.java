package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import java.util.List;
import java.util.function.Consumer;

/**
 * Every builder interface that needs access to {@link #self()} must extend this interface. The generic types allow for
 * further customization by widget builders implementing other builder interfaces.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that extends {@link DynamicWidget}.
 */
public interface SelfBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>> {
    /**
     * Simply {@code @Override} this method and put {@code return this;} in the method body.
     *
     * @return A reference to the child builder, so its return type can be accessed in other builder interfaces.
     */
    Builder self();

    /**
     * If a builder inherits this interface, then it must have a build method that returns a widget that extends
     * {@link DynamicWidget}.
     *
     * @return A new {@link Widget} instance.
     */
    default Widget build() {
        Widget widget = this.self().construct();

        this.self().init(widget);

        return widget;
    }

    /**
     * Finish the building process and pass on the built widget to the given consumer.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Widget}.
     * @return The {@link Widget} that was built.
     */
    default Widget build(Consumer<Widget> consumer) {
        Widget widget = this.build();
        consumer.accept(widget);

        return widget;
    }

    /**
     * Finish the build process and pass on the built widget to the given varargs list of consumers.
     *
     * @param consumers A varargs list of {@link Consumer} that accepts the built {@link Widget}.
     * @return The {@link Widget} that was built.
     */
    default Widget build(List<Consumer<Widget>> consumers) {
        Widget widget = this.build();

        for (Consumer<Widget> consumer : consumers)
            consumer.accept(widget);

        return widget;
    }
}
