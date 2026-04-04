package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.text.TextWrap;
import mod.adrenix.nostalgic.util.common.timer.FlagTimer;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * This builder will assign tooltips to a {@link DynamicWidget} via a {@link DynamicBuilder}.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that is implementing {@link DynamicWidget}.
 */
public interface TooltipBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
        extends SelfBuilder<Builder, Widget> {
    /**
     * Set a tooltip supplier along with a delay until the tooltip appears. To set a delay, provide a number along with
     * a time unit enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 1L, TimeUnit.SECONDS)</code>
     *
     * @param supplier The {@link Supplier} that provides a {@link Component} to display.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit A {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder tooltip(Supplier<Component> supplier, long delay, TimeUnit timeUnit) {
        this.self().tooltip = supplier;
        this.self().tooltipTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }

    /**
     * Set a tooltip along with a delay until the tooltip appears. To set a delay, provide a number along with a time
     * unit enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 1L, TimeUnit.SECONDS)</code>
     *
     * @param tooltip  The tooltip to display.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit A {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder tooltip(Component tooltip, long delay, TimeUnit timeUnit) {
        return this.tooltip(() -> tooltip, delay, timeUnit);
    }

    /**
     * Set a tooltip along with a delay until the tooltip appears. To set a delay, provide a number along with a time
     * unit enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 1L, TimeUnit.SECONDS)</code>
     *
     * @param langKey  The {@link Translation} instance.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit A {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder tooltip(Translation langKey, long delay, TimeUnit timeUnit) {
        return this.tooltip(langKey.get(), delay, timeUnit);
    }

    /**
     * Set a tooltip supplier that displays immediately upon mouse hovering. To apply a delay until the tooltip appears,
     * use {@link #tooltip(Component, long, TimeUnit)}.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     */
    @PublicAPI
    default Builder tooltip(Supplier<Component> supplier) {
        return this.tooltip(supplier, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Set a tooltip that displays immediately upon mouse hovering. To apply a delay until the tooltip appears, use
     * {@link #tooltip(Component, long, TimeUnit)}.
     *
     * @param tooltip The tooltip to display.
     */
    @PublicAPI
    default Builder tooltip(Component tooltip) {
        return this.tooltip(tooltip, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Set a tooltip that displays immediately upon mouse hovering. To apply a delay until the tooltip appears, use
     * {@link #tooltip(Translation, long, TimeUnit)}.
     *
     * @param langKey The {@link Translation} instance.
     */
    @PublicAPI
    default Builder tooltip(Translation langKey) {
        return this.tooltip(langKey.get());
    }

    /**
     * Set a multiline supplier tooltip along with a delay until the tooltip appears. To set a delay, provide a number
     * along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param supplier The {@link Supplier} that provides a {@link Component} to display.
     * @param width    The maximum width of the multiline tooltip.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder tooltip(Supplier<Component> supplier, int width, long delay, TimeUnit timeUnit) {
        this.self().multilineTooltip = () -> TextWrap.tooltip(supplier.get(), width);
        this.self().tooltipTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }

    /**
     * Set a multiline tooltip along with a delay until the tooltip appears. To set a delay, provide a number along with
     * a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param tooltip  The multiline tooltip to display.
     * @param width    The maximum width of the multiline tooltip.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder tooltip(Component tooltip, int width, long delay, TimeUnit timeUnit) {
        return this.tooltip(() -> tooltip, width, delay, timeUnit);
    }

    /**
     * Set a multiline tooltip along with a delay until the tooltip appears. To set a delay, provide a number along with
     * a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param langKey  The {@link Translation} instance.
     * @param width    The maximum width of the multiline tooltip.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder tooltip(Translation langKey, int width, long delay, TimeUnit timeUnit) {
        return this.tooltip(langKey.get(), width, delay, timeUnit);
    }

    /**
     * Set a multiline tooltip supplier that displays immediately upon mouse hovering. To apply a delay until the
     * tooltip appears, use {@link #tooltip(Component, int, long, TimeUnit)}.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     * @param width    The maximum width of the multiline tooltip.
     */
    @PublicAPI
    default Builder tooltip(Supplier<Component> supplier, int width) {
        return this.tooltip(supplier, width, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Set a multiline tooltip that displays immediately upon mouse hovering. To apply a delay until the tooltip
     * appears, use {@link #tooltip(Component, int, long, TimeUnit)}.
     *
     * @param tooltip The multiline tooltip to display.
     * @param width   The maximum width of the multiline tooltip.
     */
    @PublicAPI
    default Builder tooltip(Component tooltip, int width) {
        return this.tooltip(tooltip, width, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Set a multiline tooltip that displays immediately upon mouse hovering. To apply a delay until the tooltip
     * appears, use {@link #tooltip(Translation, int, long, TimeUnit)}.
     *
     * @param langKey The {@link Translation} instance.
     * @param width   The maximum width of the multiline tooltip.
     */
    @PublicAPI
    default Builder tooltip(Translation langKey, int width) {
        return this.tooltip(langKey.get(), width);
    }

    /**
     * Set a multiline supplier tooltip along with a delay until the tooltip appears. To set a delay, provide a number
     * along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param supplier The {@link Supplier} that provides a {@link List} of {@link Component}s to display.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder listTooltip(Supplier<List<Component>> supplier, long delay, TimeUnit timeUnit) {
        this.self().multilineTooltip = supplier;
        this.self().tooltipTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }

    /**
     * Set a multiline tooltip along with a delay until the tooltip appears. To set a delay, provide a number along with
     * a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.tooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param tooltipLines A {@link List} of {@link Component}s to show in a multiline tooltip.
     * @param delay        The delay until the tooltip appears.
     * @param timeUnit     The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder listTooltip(List<Component> tooltipLines, long delay, TimeUnit timeUnit) {
        return this.listTooltip(() -> tooltipLines, delay, timeUnit);
    }

    /**
     * Set a multiline tooltip supplier that displays immediately upon mouse hovering. To apply a delay until the
     * tooltip appears, use {@link #listTooltip(List, long, TimeUnit)}.
     *
     * @param supplier A {@link Supplier} that provides a {@link List} of {@link Component}s to display.
     */
    @PublicAPI
    default Builder listTooltip(Supplier<List<Component>> supplier) {
        return this.listTooltip(supplier, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Set a multiline tooltip that displays immediately upon mouse hovering. To apply a delay until the tooltip
     * appears, use {@link #listTooltip(List, long, TimeUnit)}.
     *
     * @param tooltipLines A {@link List} of {@link Component}s to show in a multiline tooltip.
     */
    @PublicAPI
    default Builder listTooltip(List<Component> tooltipLines) {
        return this.listTooltip(tooltipLines, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * When this widget is disabled, set a tooltip supplier along with a delay until the tooltip appears. To set a
     * delay, provide a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 1L, TimeUnit.SECONDS)</code>
     *
     * @param supplier The {@link Supplier} that provides a {@link Component} to display when disabled.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit A {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledTooltip(Supplier<Component> supplier, long delay, TimeUnit timeUnit) {
        this.self().disabledTooltip = supplier;
        this.self().disabledTooltipTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }

    /**
     * When this widget is disabled, set a tooltip along with a delay until the tooltip appears. To set a delay, provide
     * a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 1L, TimeUnit.SECONDS)</code>
     *
     * @param tooltip  The tooltip to display.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit A {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledTooltip(Component tooltip, long delay, TimeUnit timeUnit) {
        return this.disabledTooltip(() -> tooltip, delay, timeUnit);
    }

    /**
     * When this widget is disabled, set a tooltip along with a delay until the tooltip appears. To set a delay, provide
     * a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 1L, TimeUnit.SECONDS)</code>
     *
     * @param langKey  The {@link Translation} instance.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit A {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledTooltip(Translation langKey, long delay, TimeUnit timeUnit) {
        return this.disabledTooltip(langKey.get(), delay, timeUnit);
    }

    /**
     * When this widget is disabled, set a tooltip supplier that displays immediately upon mouse hovering. To apply a
     * delay until the tooltip appears, use {@link #disabledTooltip(Component, long, TimeUnit)}.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     */
    @PublicAPI
    default Builder disabledTooltip(Supplier<Component> supplier) {
        return this.disabledTooltip(supplier, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * When this widget is disabled, set a tooltip that displays immediately upon mouse hovering. To apply a delay until
     * the tooltip appears, use {@link #disabledTooltip(Component, long, TimeUnit)}.
     *
     * @param tooltip The tooltip to display when this widget is disabled.
     */
    @PublicAPI
    default Builder disabledTooltip(Component tooltip) {
        return this.disabledTooltip(tooltip, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * When this widget is disabled, set a tooltip that displays immediately upon mouse hovering. To apply a delay until
     * the tooltip appears, use {@link #disabledTooltip(Translation, long, TimeUnit)}.
     *
     * @param langKey The {@link Translation} instance.
     */
    @PublicAPI
    default Builder disabledTooltip(Translation langKey) {
        return this.disabledTooltip(langKey.get());
    }

    /**
     * When this widget is disabled, set a multiline tooltip supplier along with a delay until the tooltip appears. To
     * set a delay, provide a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1
     * second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param supplier The {@link Supplier} that provides a {@link Component} to display when disabled.
     * @param width    The maximum width of the multiline tooltip.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledTooltip(Supplier<Component> supplier, int width, long delay, TimeUnit timeUnit) {
        this.self().disabledMultilineTooltip = () -> TextWrap.tooltip(supplier.get(), width);
        this.self().disabledTooltipTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }

    /**
     * When this widget is disabled, set a multiline tooltip along with a delay until the tooltip appears. To set a
     * delay, provide a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param tooltip  The multiline tooltip to display when this widget is disabled.
     * @param width    The maximum width of the multiline tooltip.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledTooltip(Component tooltip, int width, long delay, TimeUnit timeUnit) {
        return this.disabledTooltip(() -> tooltip, width, delay, timeUnit);
    }

    /**
     * When this widget is disabled, set a multiline tooltip along with a delay until the tooltip appears. To set a
     * delay, provide a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param langKey  The {@link Translation} instance.
     * @param width    The maximum width of the multiline tooltip.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledTooltip(Translation langKey, int width, long delay, TimeUnit timeUnit) {
        return this.disabledTooltip(langKey.get(), width, delay, timeUnit);
    }

    /**
     * When this widget is disabled, set a multiline tooltip supplier that displays immediately upon mouse hovering. To
     * apply a delay until the tooltip appears, use {@link #disabledTooltip(Component, int, long, TimeUnit)}.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     * @param width    The maximum width of the multiline tooltip.
     */
    @PublicAPI
    default Builder disabledTooltip(Supplier<Component> supplier, int width) {
        return this.disabledTooltip(supplier, width, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * When this widget is disabled, set a multiline tooltip that displays immediately upon mouse hovering. To apply a
     * delay until the tooltip appears, use {@link #disabledTooltip(Component, int, long, TimeUnit)}.
     *
     * @param tooltip The multiline tooltip to display when this widget is disabled.
     * @param width   The maximum width of the multiline tooltip.
     */
    @PublicAPI
    default Builder disabledTooltip(Component tooltip, int width) {
        return this.disabledTooltip(tooltip, width, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * When this widget is disabled, set a multiline tooltip that displays immediately upon mouse hovering. To apply a
     * delay until the tooltip appears, use {@link #disabledTooltip(Translation, int, long, TimeUnit)}.
     *
     * @param langKey The {@link Translation} instance.
     * @param width   The maximum width of the multiline tooltip.
     */
    @PublicAPI
    default Builder disabledTooltip(Translation langKey, int width) {
        return this.disabledTooltip(langKey.get(), width);
    }

    /**
     * When this widget is disabled, set a multiline tooltip supplier along with a delay until the tooltip appears. To
     * set a delay, provide a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1
     * second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param supplier The {@link Supplier} that provides a {@link List} of {@link Component}s to display when
     *                 disabled.
     * @param delay    The delay until the tooltip appears.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledListTooltip(Supplier<List<Component>> supplier, long delay, TimeUnit timeUnit) {
        this.self().disabledMultilineTooltip = supplier;
        this.self().disabledTooltipTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }

    /**
     * When this widget is disabled, set a multiline tooltip along with a delay until the tooltip appears. To set a
     * delay, provide a number along with a {@link TimeUnit} enumeration. For example, to set a delay of 1 second:
     *
     * <p>
     * <code>Builder.disabledTooltip(tooltip, 50, 1L, TimeUnit.SECONDS)</code>
     * <p>
     * Where 50 is the maximum width of the tooltip and 1L represents one second.
     *
     * @param tooltipLines A {@link List} of {@link Component}s to show in a multiline tooltip.
     * @param delay        The delay until the tooltip appears.
     * @param timeUnit     The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder disabledListTooltip(List<Component> tooltipLines, long delay, TimeUnit timeUnit) {
        return this.disabledListTooltip(() -> tooltipLines, delay, timeUnit);
    }

    /**
     * When this widget is disabled, set a multiline tooltip supplier that displays immediately upon mouse hovering. To
     * apply a delay until the tooltip appears, use {@link #disabledListTooltip(List, long, TimeUnit)}.
     *
     * @param supplier A {@link Supplier} that provides a {@link List} of {@link Component}s to display when disabled.
     */
    @PublicAPI
    default Builder disabledListTooltip(Supplier<List<Component>> supplier) {
        return this.disabledListTooltip(supplier, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * When this widget is disabled, set a multiline tooltip that displays immediately upon mouse hovering. To apply a
     * delay until the tooltip appears, use {@link #disabledListTooltip(List, long, TimeUnit)}.
     *
     * @param tooltipLines A {@link List} of {@link Component}s to show in a multiline tooltip.
     */
    @PublicAPI
    default Builder disabledListTooltip(List<Component> tooltipLines) {
        return this.disabledListTooltip(tooltipLines, 0L, TimeUnit.MILLISECONDS);
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component} to display when the shift key is held.
     */
    @PublicAPI
    default Builder infoTooltip(Supplier<Component> supplier) {
        this.self().infoTooltip = supplier;

        return this.self();
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param tooltip A {@link Component} to display when the shift key is held.
     */
    @PublicAPI
    default Builder infoTooltip(Component tooltip) {
        return this.infoTooltip(() -> tooltip);
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param langKey A {@link Translation} to get information from when the shift key is held.
     */
    @PublicAPI
    default Builder infoTooltip(Translation langKey) {
        return this.infoTooltip(langKey::get);
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component} to display when the shift key is held.
     * @param width    The maximum width of the informative multiline tooltip.
     */
    @PublicAPI
    default Builder infoTooltip(Supplier<Component> supplier, int width) {
        this.self().multilineInfoTooltip = () -> TextWrap.tooltip(supplier.get(), width);

        return this.self();
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param tooltip A {@link Component} to display when the shift key is held.
     * @param width   The maximum width of the informative multiline tooltip.
     */
    @PublicAPI
    default Builder infoTooltip(Component tooltip, int width) {
        this.self().multilineInfoTooltip = () -> TextWrap.tooltip(tooltip, width);

        return this.self();
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param langKey A {@link Translation} to get information from when the shift key is held.
     * @param width   The maximum width of the informative multiline tooltip.
     */
    @PublicAPI
    default Builder infoTooltip(Translation langKey, int width) {
        return this.infoTooltip(langKey::get, width);
    }

    /**
     * Set a disabled information tooltip that will appear when the shift key is held.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component} to display when the shift key is held.
     */
    @PublicAPI
    default Builder disabledInfoTooltip(Supplier<Component> supplier) {
        this.self().disabledInfoTooltip = supplier;

        return this.self();
    }

    /**
     * Set a disabled information tooltip that will appear when the shift key is held.
     *
     * @param tooltip A {@link Component} to display when the shift key is held.
     */
    @PublicAPI
    default Builder disabledInfoTooltip(Component tooltip) {
        return this.disabledInfoTooltip(() -> tooltip);
    }

    /**
     * Set a disabled information tooltip that will appear when the shift key is held.
     *
     * @param langKey A {@link Translation} to get information from when the shift key is held.
     */
    @PublicAPI
    default Builder disabledInfoTooltip(Translation langKey) {
        return this.disabledInfoTooltip(langKey::get);
    }

    /**
     * Set a disabled information tooltip that will appear when the shift key is held.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component} to display when the shift key is held.
     * @param width    The maximum width of the disabled informative multiline tooltip.
     */
    @PublicAPI
    default Builder disabledInfoTooltip(Supplier<Component> supplier, int width) {
        this.self().disabledMultilineInfoTooltip = () -> TextWrap.tooltip(supplier.get(), width);

        return this.self();
    }

    /**
     * Set a disabled information tooltip that will appear when the shift key is held.
     *
     * @param tooltip A {@link Component} to display when the shift key is held.
     * @param width   The maximum width of the disabled informative multiline tooltip.
     */
    @PublicAPI
    default Builder disabledInfoTooltip(Component tooltip, int width) {
        this.self().disabledMultilineInfoTooltip = () -> TextWrap.tooltip(tooltip, width);

        return this.self();
    }

    /**
     * Set a disabled information tooltip that will appear when the shift key is held.
     *
     * @param langKey A {@link Translation} to get information from when the shift key is held.
     * @param width   The maximum width of the disabled informative multiline tooltip.
     */
    @PublicAPI
    default Builder disabledInfoTooltip(Translation langKey, int width) {
        return this.disabledInfoTooltip(langKey::get, width);
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param supplier A {@link Supplier} that provides a {@link List} of {@link Component}s to display when the shift
     *                 key is held.
     */
    @PublicAPI
    default Builder infoListTooltip(Supplier<List<Component>> supplier) {
        this.self().multilineInfoTooltip = supplier;

        return this.self();
    }

    /**
     * Set an information tooltip that will appear when the shift key is held.
     *
     * @param tooltip A {@link List} of {@link Component}s to display when the shift key is held.
     */
    @PublicAPI
    default Builder infoListTooltip(List<Component> tooltip) {
        return this.infoListTooltip(() -> tooltip);
    }

    /**
     * Hide the tooltip after the given delay has elapsed. The tooltip will reappear when the conditions defined in this
     * builder are met.
     *
     * @param delay    The delay until the tooltip disappears after it appeared.
     * @param timeUnit The {@link TimeUnit} enumeration to use to convert the delay into milliseconds.
     */
    @PublicAPI
    default Builder hideTooltipAfter(long delay, TimeUnit timeUnit) {
        this.self().hideTimer = FlagTimer.create(delay, timeUnit).once().build();

        return this.self();
    }
}
