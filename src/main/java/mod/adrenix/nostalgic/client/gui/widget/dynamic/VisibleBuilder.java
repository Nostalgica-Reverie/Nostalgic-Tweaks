package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * This builder will update the {@code visible} flag in {@link DynamicBuilder} which will then be reflected onto the
 * built {@link Widget}.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that is implementing {@link DynamicWidget}.
 */
public interface VisibleBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
        extends SelfBuilder<Builder, Widget> {
    /**
     * The widget built by this builder will only be {@code visible} when the given function yields a state of
     * {@code true}.
     *
     * @param predicate A {@link Predicate} that tests if the widget is visible.
     * @see #visibleIf(BooleanSupplier)
     */
    @PublicAPI
    default Builder visibleIf(Predicate<Widget> predicate) {
        this.self().visible = predicate;

        return this.self();
    }

    /**
     * The widget built by this builder will only be {@code visible} when the given supplier yields a state of
     * {@code true}.
     *
     * @param supplier A {@link BooleanSupplier}.
     * @see #visibleIf(Predicate)
     */
    @PublicAPI
    default Builder visibleIf(BooleanSupplier supplier) {
        return this.visibleIf(widget -> supplier.getAsBoolean());
    }

    /**
     * The widget built by this builder will not be {@code visible} when the given function yields a state of
     * {@code true}.
     *
     * @param predicate A {@link Predicate} that tests if the widget is invisible.
     * @see #invisibleIf(BooleanSupplier)
     */
    @PublicAPI
    default Builder invisibleIf(Predicate<Widget> predicate) {
        this.self().visible = (widget) -> !predicate.test(widget);

        return this.self();
    }

    /**
     * The widget built by this builder will not be {@code visible} when the given supplier yields a state of
     * {@code true}.
     *
     * @param supplier A {@link BooleanSupplier}.
     * @see #invisibleIf(Predicate)
     */
    @PublicAPI
    default Builder invisibleIf(BooleanSupplier supplier) {
        return this.invisibleIf(widget -> supplier.getAsBoolean());
    }

    /**
     * Define instructions to run when the built widget changes its {@code visible} flag.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Widget}.
     */
    @PublicAPI
    default Builder onVisibleChange(Consumer<Widget> consumer) {
        this.self().onVisibleChange = consumer;

        return this.self();
    }

    /**
     * Define instructions to run when the built widget changes its {@code visible} flag.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    default Builder onVisibleChange(Runnable runnable) {
        return this.onVisibleChange(widget -> runnable.run());
    }
}
