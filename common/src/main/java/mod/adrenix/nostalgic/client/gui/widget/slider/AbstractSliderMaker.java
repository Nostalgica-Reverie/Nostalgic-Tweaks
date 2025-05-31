package mod.adrenix.nostalgic.client.gui.widget.slider;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractSliderMaker<Builder extends AbstractSliderMaker<Builder, Slider>, Slider extends AbstractSlider<Builder, Slider>>
    extends DynamicBuilder<Builder, Slider>
    implements LayoutBuilder<Builder, Slider>, ActiveBuilder<Builder, Slider>, VisibleBuilder<Builder, Slider>,
               TooltipBuilder<Builder, Slider>
{
    /* Fields */

    protected Number minValue;
    protected Number maxValue;
    protected Consumer<Number> valueConsumer;
    protected Supplier<Number> valueSupplier;
    @Nullable protected Consumer<Slider> onValueChange = null;
    @Nullable protected SliderRenderer<Builder, Slider> backgroundRenderer = null;
    @Nullable protected SliderRenderer<Builder, Slider> effectsRenderer = null;
    @Nullable protected SliderRenderer<Builder, Slider> handleRenderer = null;
    @Nullable protected Supplier<Number> interval = null;
    protected int roundTo = 2;
    protected int handleWidth = 8;
    protected boolean useRounding = false;
    protected boolean scrollRequiresFocus = true;
    protected boolean clickSoundOnRelease = true;
    protected Supplier<Component> title = Component::empty;
    protected Supplier<Component> suffix = Component::empty;
    protected Supplier<Component> separator = () -> Component.literal(": ");
    protected Function<Number, String> formatter = Object::toString;

    /* Constructor */

    protected AbstractSliderMaker(Number minValue, Number maxValue, Consumer<Number> valueConsumer, Supplier<Number> valueSupplier)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueConsumer = valueConsumer;
        this.valueSupplier = valueSupplier;
    }

    protected AbstractSliderMaker(Number minValue, Number maxValue)
    {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.valueConsumer = (number) -> { };
        this.valueSupplier = () -> 0;
    }

    /* Methods */

    /**
     * Set the title so that it matches what is returned by the given supplier.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     * @see #title(Component)
     * @see #title(Translation)
     */
    @PublicAPI
    public Builder title(Supplier<Component> supplier)
    {
        this.title = supplier;

        return this.self();
    }

    /**
     * Set the title of this slider using the given component.
     *
     * @param title A {@link Component}.
     * @see #title(Supplier)
     * @see #title(Translation)
     */
    @PublicAPI
    public Builder title(Component title)
    {
        return this.title(() -> title);
    }

    /**
     * Set the title so that it matches what is translated by the given lang key.
     *
     * @param langKey A {@link Translation} instance that does not accept arguments.
     * @see #title(Supplier)
     * @see #title(Component)
     */
    @PublicAPI
    public Builder title(Translation langKey)
    {
        return this.title(langKey::get);
    }

    /**
     * Set the suffix so that it matches what is returned by the given supplier.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     * @see #suffix(Component)
     * @see #suffix(Translation)
     * @see #suffix(String)
     */
    @PublicAPI
    public Builder suffix(Supplier<Component> supplier)
    {
        this.suffix = supplier;

        return this.self();
    }

    /**
     * Set the suffix of this slider using the given component.
     *
     * @param suffix A {@link Component}.
     * @see #suffix(Supplier)
     * @see #suffix(Translation)
     * @see #suffix(String)
     */
    @PublicAPI
    public Builder suffix(Component suffix)
    {
        return this.suffix(() -> suffix);
    }

    /**
     * Set the suffix so that it matches what is translated by the given lang key.
     *
     * @param langKey A {@link Translation} instance that does not accept arguments.
     * @see #suffix(Supplier)
     * @see #suffix(Component)
     * @see #suffix(String)
     */
    @PublicAPI
    public Builder suffix(Translation langKey)
    {
        return this.suffix(langKey::get);
    }

    /**
     * Set the suffix using an explicit string value.
     *
     * @param suffix The suffix to use.
     * @see #suffix(Supplier)
     * @see #suffix(Component)
     * @see #suffix(Translation)
     */
    @PublicAPI
    public Builder suffix(String suffix)
    {
        return this.suffix(Component.literal(suffix));
    }

    /**
     * Set the separator so that it matches what is returned by the given supplier.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component}.
     * @see #separator(Component)
     * @see #separator(Translation)
     */
    @PublicAPI
    public Builder separator(Supplier<Component> supplier)
    {
        this.separator = supplier;

        return this.self();
    }

    /**
     * Set the separator of this slider using the given component.
     *
     * @param separator A {@link Component}.
     * @see #separator(Supplier)
     * @see #separator(Translation)
     */
    @PublicAPI
    public Builder separator(Component separator)
    {
        return this.separator(() -> separator);
    }

    /**
     * Set the separator so that it matches what is translated by the given lang key.
     *
     * @param langKey A {@link Translation} instance that does not accept arguments.
     * @see #separator(Supplier)
     * @see #separator(Component)
     */
    @PublicAPI
    public Builder separator(Translation langKey)
    {
        return this.separator(langKey::get);
    }

    /**
     * Provide a function that formats the current number selected by the slider.
     *
     * @param function A {@link Function} that accepts a number type and returns a formatted string.
     */
    @PublicAPI
    public Builder formatter(Function<Number, String> function)
    {
        this.formatter = function;

        return this.self();
    }

    /**
     * Set the skipping interval for this slider using a supplier. The interval change is used when the slider is
     * scrolled or changed by key input.
     *
     * @param supplier A {@link Supplier} that provides an interval number to skip by.
     */
    @PublicAPI
    public Builder interval(@Nullable Supplier<Number> supplier)
    {
        this.interval = supplier;

        return this.self();
    }

    /**
     * Set the skipping interval number for this slider. The interval change is used when the slider is scrolled or
     * changed by key input.
     *
     * @param number A number interval to skip by.
     */
    @PublicAPI
    public Builder interval(Number number)
    {
        return interval(() -> number);
    }

    /**
     * Set a round to place value for this slider. The default is {@code 2}, which is the hundredth place.
     *
     * @param place The place to round to. For example, {@code 1} represents the tenths' place.
     */
    @PublicAPI
    public Builder roundTo(int place)
    {
        this.useRounding = true;
        this.roundTo = place;

        return this.self();
    }

    /**
     * Set a custom handle width for this slider.
     *
     * @param width A custom slider handle width. The default handle width is {@code 8}.
     */
    @PublicAPI
    public Builder handleWidth(int width)
    {
        this.handleWidth = width;

        return this.self();
    }

    /**
     * Disable the clicking sound that plays when the mouse releases a slider.
     */
    @PublicAPI
    public Builder noClickSound()
    {
        this.clickSoundOnRelease = false;

        return this.self();
    }

    /**
     * If the widget is not focused, and the mouse is over the slider, then scrolling will change the slider's internal
     * value.
     */
    @PublicAPI
    public Builder scrollWithoutFocus()
    {
        this.scrollRequiresFocus = false;

        return this.self();
    }

    /**
     * Perform the given instructions when the slider's value changes.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Slider}.
     */
    @PublicAPI
    public Builder onChange(Consumer<Slider> consumer)
    {
        this.onValueChange = consumer;

        return this.self();
    }

    /**
     * Perform the given instructions when the slider's value changes.
     *
     * @param runnable A {@link Runnable} instance.
     */
    @PublicAPI
    public Builder onChange(Runnable runnable)
    {
        return this.onChange(slider -> runnable.run());
    }

    /**
     * Provide a custom slider handle renderer that renders the movable slider button. This renders above any background
     * rendering.
     *
     * @param renderer A {@link SliderRenderer} consumer.
     */
    @PublicAPI
    public Builder handleRenderer(@Nullable SliderRenderer<Builder, Slider> renderer)
    {
        this.handleRenderer = renderer;

        return this.self();
    }

    /**
     * Provide custom effects that are rendered above the background and below the slider handle.
     *
     * @param renderer A {@link SliderRenderer} consumer.
     */
    @PublicAPI
    public Builder effectsRenderer(@Nullable SliderRenderer<Builder, Slider> renderer)
    {
        this.effectsRenderer = renderer;

        return this.self();
    }

    /**
     * Provide a custom background renderer that will render behind the slider and in front of the button background.
     *
     * @param renderer A {@link SliderRenderer} consumer.
     */
    @PublicAPI
    public Builder backgroundRenderer(@Nullable SliderRenderer<Builder, Slider> renderer)
    {
        this.backgroundRenderer = renderer;

        return this.self();
    }
}
