package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This builder will update the {@code active} flag in {@link DynamicBuilder} which will then be reflected onto the
 * built {@link Widget}.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that is implementing {@link DynamicWidget}.
 */
public interface ActiveBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
        extends SelfBuilder<Builder, Widget> {
    /**
     * The widget built by this builder will have its {@code active} flag set to {@code true} when the given function
     * yields a state of {@code true}. If this builder has {@code disableIf} defined, then this will be overrun by the
     * value returned by method.
     *
     * @param predicate A {@link Predicate} that tests if the widget is enabled.
     * @see #enableIf(BooleanSupplier)
     */
    @PublicAPI
    default Builder enableIf(Predicate<Widget> predicate) {
        this.self().active = predicate;

        return this.self();
    }

    /**
     * The widget built by this builder will have its {@code active} flag set to {@code true} when the given supplier
     * yields a state of {@code true}. If this builder has {@code disableIf} defined, then this will be overrun by the
     * value returned by method.
     *
     * @param supplier A {@link BooleanSupplier}.
     * @see #enableIf(Predicate)
     */
    @PublicAPI
    default Builder enableIf(BooleanSupplier supplier) {
        return this.enableIf(widget -> supplier.getAsBoolean());
    }

    /**
     * The widget built by this builder will have its {@code active} flag set to {@code false} when the given function
     * yields a state of {@code true}. If this builder has {@code enableIf} defined, then this will override the
     * {@code active} flag.
     *
     * @param predicate A {@link Predicate} that tests if the widget is disabled.
     * @see #disableIf(BooleanSupplier)
     */
    @PublicAPI
    default Builder disableIf(Predicate<Widget> predicate) {
        this.self().active = (widget) -> !predicate.test(widget);

        return this.self();
    }

    /**
     * The widget built by this builder will have its {@code active} flag set to {@code false} when the given supplier
     * yields a state of {@code true}. If this builder has {@code enableIf} defined, then this will override the
     * {@code active} flag.
     *
     * @param supplier A {@link BooleanSupplier}.
     * @see #disableIf(Predicate)
     */
    @PublicAPI
    default Builder disableIf(BooleanSupplier supplier) {
        return this.disableIf(widget -> supplier.getAsBoolean());
    }

    /**
     * Define instructions to run when the built widget changes its {@code active} flag.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Widget}.
     */
    @PublicAPI
    default Builder onActiveChange(Consumer<Widget> consumer) {
        this.self().onActiveChange = consumer;

        return this.self();
    }

    /**
     * Define instructions to run when the built widget changes its {@code active} flag.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    default Builder onActiveChange(Runnable runnable) {
        return this.onActiveChange(widget -> runnable.run());
    }
}
