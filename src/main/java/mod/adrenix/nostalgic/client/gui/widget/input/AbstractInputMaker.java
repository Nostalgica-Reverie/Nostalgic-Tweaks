package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.client.gui.widget.input.suggestion.InputSuggester;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.*;

public abstract class AbstractInputMaker<Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>>
        extends DynamicBuilder<Builder, Input>
        implements LayoutBuilder<Builder, Input>, ActiveBuilder<Builder, Input>, VisibleBuilder<Builder, Input>,
        TooltipBuilder<Builder, Input>, IconBuilder<Builder, Input> {
    /* Fields */

    @Nullable
    protected BiConsumer<Input, String> responder;
    @Nullable
    protected Function<Input, String> sync;
    @Nullable
    protected Function<Input, ? extends InputSuggester<Input>> suggesterProvider;
    protected UniqueArrayList<Consumer<String>> listeners;
    protected BiFunction<String, Integer, FormattedCharSequence> formatter;
    protected Predicate<String> filter;

    protected String whenEmpty = "";
    protected String startWith = "";
    protected String cursor = "_";

    protected int maxLength = 120;
    protected int iconPadding = 2;
    protected long responseDelay = 100L;
    protected boolean delayedResponse = false;
    protected boolean searchShortcut = false;
    protected boolean editable = true;

    @Nullable
    protected Color hoverBackgroundColor = null;
    @Nullable
    protected Color hoverBorderColor = null;
    protected Color backgroundColor = Color.BLACK;
    protected Color backgroundFocusColor = Color.BLACK;
    protected Color borderColor = Color.LIGHT_GRAY;
    protected Color borderFocusColor = Color.WHITE;
    protected Color cursorColor = Color.WHITE;
    protected Color cursorVerticalColor = Color.LIGHT_GRAY;
    protected Color textColor = Color.WHITE;
    protected Color textUnfocusedColor = Color.WHITE;
    protected Color textEmptyColor = Color.LIGHT_GRAY;

    /* Constructor */

    protected AbstractInputMaker() {
        this.brightenOnHover = true;
        this.listeners = new UniqueArrayList<>();
        this.formatter = (string, maxLength) -> FormattedCharSequence.forward(string, Style.EMPTY);
        this.filter = Objects::nonNull;
    }

    /* Methods */

    /**
     * Define a suggester provider for this input widget. The suggester will suggest options based on the current input.
     * The suggestion appears as ghost text in front of the last input character.
     *
     * @param provider A {@link Function} that accepts the {@link Input} widget and returns a new {@link InputSuggester}
     *                 instance.
     */
    @PublicAPI
    public Builder suggester(@Nullable Function<Input, ? extends InputSuggester<Input>> provider) {
        this.suggesterProvider = provider;

        return this.self();
    }

    /**
     * Add an input listener. This listener will receive updates when the input is updated. When you are done listening,
     * make sure you remove the listener using {@link #removeListener(Consumer)}.
     *
     * @param listener A {@link Consumer} that accepts the new input.
     */
    @PublicAPI
    public Builder addListener(Consumer<String> listener) {
        this.listeners.add(listener);

        return this.self();
    }

    /**
     * Remove an input listener.
     *
     * @param listener The {@link Consumer} listener to remove.
     */
    @PublicAPI
    public Builder removeListener(Consumer<String> listener) {
        this.listeners.remove(listener);

        return this.self();
    }

    /**
     * When this property is defined, the widget's text value will update with what is provided by the supplier at the
     * start of each render pass. This should be used in conjunction with {@link #whenFocused(Consumer)}. The input
     * function should manage a string field that this supplier returns. When the value managed by the onInput function
     * changes from the outside, this function will provide that change and will be reflected by this input widget at
     * the start of the next render pass.
     *
     * @param function A function that accepts the input widget instance and returns a string that the input widget will
     *                 sync up with.
     * @see Builder#onSync(Supplier)
     */
    @PublicAPI
    public Builder onSync(Function<Input, String> function) {
        this.sync = function;

        return this.self();
    }

    /**
     * An overload method for {@link Builder#onSync(Function)}. This functional shortcut only requires a supplier that
     * returns a {@code String}.
     *
     * @param supplier A supplier that returns a {@code String} that this input widget will stay in sync with.
     * @see Builder#onSync(Function)
     */
    @PublicAPI
    public Builder onSync(Supplier<String> supplier) {
        return this.onSync(widget -> supplier.get());
    }

    /**
     * Set the response instructions to perform when a user changes input string data.
     *
     * @param responder A {@link BiConsumer} that accepts the {@link Input} widget and the typed input.
     */
    @PublicAPI
    public Builder onInput(BiConsumer<Input, String> responder) {
        this.responder = responder;

        return this.self();
    }

    /**
     * Set the response instructions to perform when a user changes input string data.
     *
     * @param responder A {@link Consumer} that accepts input.
     */
    @PublicAPI
    public Builder onInput(Consumer<String> responder) {
        return this.onInput((widget, typed) -> responder.accept(typed));
    }

    /**
     * Delay the response to user input by the given number of milliseconds. This is useful for logically expensive
     * responses to input. The input won't be processed until the user is done typing in a query.
     *
     * @param delay The amount of time in milliseconds to wait until responding to the input.
     */
    @PublicAPI
    public Builder delayedResponse(long delay) {
        this.responseDelay = delay;

        return this.delayedResponse();
    }

    /**
     * Delay the response to user input. This is useful for logically expensive responses to input. The input won't be
     * processed until the user is done typing in a query.
     */
    @PublicAPI
    public Builder delayedResponse() {
        this.delayedResponse = true;

        return this.self();
    }

    /**
     * Start this input widget as non-editable. The widget can be later enabled with
     * {@link GenericInput#setEditable(boolean)}.
     */
    @PublicAPI
    public Builder startAsNonEditable() {
        this.editable = false;

        return this.self();
    }

    /**
     * Filters out input based on the result of the given predicate.
     *
     * @param filter A {@link Predicate} that accepts the new input string before it is assigned to the widget.
     */
    @PublicAPI
    public Builder setFilter(Predicate<String> filter) {
        this.filter = filter;

        return this.self();
    }

    /**
     * Set a text formatter to apply to input.
     *
     * @param formatter A {@link BiFunction} that accepts a substring of the input widget and the inclusive index used
     *                  to create that substring from the whole input string. For example, consider the current input to
     *                  be "Harbison" and the cursor is positioned at "Har|bison". The widget will pass two strings to
     *                  the formatter. "Har" (with index 0) and "bison" (with index 3). This is the equivalent of
     *                  invoking {@code "Harbison".substring(3)}. The return must be a {@link FormattedCharSequence} for
     *                  both substrings.
     */
    @PublicAPI
    public Builder formatter(BiFunction<String, Integer, FormattedCharSequence> formatter) {
        this.formatter = formatter;

        return this.self();
    }

    /**
     * Change the appended character that flashes in the input widget.
     *
     * @param cursor The character to use for the cursor.
     */
    @PublicAPI
    public Builder cursorChar(char cursor) {
        this.cursor = Character.toString(cursor);

        return this.self();
    }

    /**
     * Change the appended character that flashes in the input widget. Only the first character of the given string will
     * be used.
     *
     * @param cursor An input character.
     */
    @PublicAPI
    public Builder cursorChar(String cursor) {
        if (cursor.isEmpty())
            return this.self();

        this.cursor = cursor.substring(0, 1);

        return this.self();
    }

    /**
     * Set the maximum length allowed for the input string.
     *
     * @param maxLength A maximum allocated string length.
     */
    @PublicAPI
    public Builder maxLength(int maxLength) {
        this.maxLength = maxLength;

        return this.self();
    }

    /**
     * The input box will be focused when the {@code ctrl} key is held and the {@code F} key is pressed.
     */
    @PublicAPI
    public Builder searchShortcut() {
        this.searchShortcut = true;

        return this.self();
    }

    /**
     * Set the initial value for the input widget.
     *
     * @param value A starting string value.
     */
    @PublicAPI
    public Builder startWith(String value) {
        this.startWith = value;

        return this.self();
    }

    /**
     * Set text that appears when there is nothing typed into the input widget.
     *
     * @param whenEmpty The string to show when the input widget has an empty value.
     */
    @PublicAPI
    public Builder whenEmpty(String whenEmpty) {
        this.whenEmpty = whenEmpty;

        return this.self();
    }

    /**
     * Set text that appears when there is nothing typed into the input widget.
     *
     * @param whenEmpty The string to show when the input widget has an empty value.
     */
    @PublicAPI
    public Builder whenEmpty(Component whenEmpty) {
        return this.whenEmpty(whenEmpty.getString());
    }

    /**
     * Set text that appears when there is nothing typed into the input widget.
     *
     * @param langKey A {@link Translation} instance.
     */
    @PublicAPI
    public Builder whenEmpty(Translation langKey) {
        return this.whenEmpty(langKey.get());
    }

    /**
     * Set the text color when the empty string text is displayed.
     *
     * @param color A {@link Color}.
     */
    @PublicAPI
    public Builder emptyColor(Color color) {
        this.textEmptyColor = color;

        return this.self();
    }

    /**
     * Set the text color when the empty string text is displayed.
     *
     * @param color A {@link IntSupplier} that provides an ARGB color.
     */
    @PublicAPI
    public Builder emptyColor(IntSupplier color) {
        return this.emptyColor(new Color(color));
    }

    /**
     * Set the text color when the empty string text is displayed.
     *
     * @param color An ARGB color integer.
     */
    @PublicAPI
    public Builder emptyColor(int color) {
        return this.emptyColor(new Color(color));
    }

    /**
     * Change the padding between the icon and the input border/text. The default padding is {@code 2}.
     *
     * @param padding The padding to use.
     */
    @PublicAPI
    public Builder iconPadding(int padding) {
        this.iconPadding = padding;

        return this.self();
    }

    /**
     * Set a focused and unfocused background color for this input widget.
     *
     * @param unfocused The normal unfocused {@link Color}.
     * @param focused   The focused {@link Color}.
     */
    @PublicAPI
    public Builder background(Color unfocused, Color focused) {
        this.backgroundColor = unfocused;
        this.backgroundFocusColor = focused;

        return this.self();
    }

    /**
     * Set a focused and unfocused background color for this input widget.
     *
     * @param unfocused The normal unfocused {@link IntSupplier} that provides an ARGB integer.
     * @param focused   The focused {@link IntSupplier} that provides an ARGB integer.
     */
    @PublicAPI
    public Builder background(IntSupplier unfocused, IntSupplier focused) {
        return this.background(new Color(unfocused), new Color(focused));
    }

    /**
     * Set a focused and unfocused background color for this input widget.
     *
     * @param unfocused The normal unfocused ARGB color integer.
     * @param focused   The focused ARGB color integer.
     */
    @PublicAPI
    public Builder background(int unfocused, int focused) {
        return this.background(new Color(unfocused), new Color(focused));
    }

    /**
     * Set a generic color for the background. The {@link Color#brighter()} method will be used for when the background
     * is focused.
     *
     * @param color A {@link Color} to used for the background.
     */
    @PublicAPI
    public Builder background(Color color) {
        return this.background(color, color.brighter());
    }

    /**
     * Set a generic color for the background. The {@link Color#brighter()} method will be used for when the background
     * is focused.
     *
     * @param color A {@link IntSupplier} that provides an ARGB color integer.
     */
    @PublicAPI
    public Builder background(IntSupplier color) {
        return this.background(new Color(color), new Color(color).brighter());
    }

    /**
     * Set a generic color for the background. The {@link Color#brighter()} method will be used for when the background
     * is focused.
     *
     * @param color An ARGB integer color.
     */
    @PublicAPI
    public Builder background(int color) {
        return this.background(new Color(color), new Color(color).brighter());
    }

    /**
     * Set a focused and unfocused border color for this input widget.
     *
     * @param unfocused The normal unfocused {@link Color}.
     * @param focused   The focused {@link Color}.
     */
    @PublicAPI
    public Builder border(Color unfocused, Color focused) {
        this.borderColor = unfocused;
        this.borderFocusColor = focused;

        return this.self();
    }

    /**
     * Set a focused and unfocused border color for this input widget.
     *
     * @param unfocused The normal unfocused {@link IntSupplier} that provides an ARGB integer.
     * @param focused   The focused {@link IntSupplier} that provides an ARGB integer.
     */
    @PublicAPI
    public Builder border(IntSupplier unfocused, IntSupplier focused) {
        return this.border(new Color(unfocused), new Color(focused));
    }

    /**
     * Set a focused and unfocused border color for this input widget.
     *
     * @param unfocused The normal unfocused ARGB color integer.
     * @param focused   The focused ARGB color integer.
     */
    @PublicAPI
    public Builder border(int unfocused, int focused) {
        return this.border(new Color(unfocused), new Color(focused));
    }

    /**
     * Set a generic color for the border. The {@link Color#brighter()} method will be used for when the border is
     * focused.
     *
     * @param color A {@link Color} to used for the border.
     */
    @PublicAPI
    public Builder border(Color color) {
        return this.border(color, color.brighter());
    }

    /**
     * Set a generic color for the border. The {@link Color#brighter()} method will be used for when the border is
     * focused.
     *
     * @param color A {@link IntSupplier} that provides an ARGB color integer.
     */
    @PublicAPI
    public Builder border(IntSupplier color) {
        return this.border(new Color(color), new Color(color).brighter());
    }

    /**
     * Set a generic color for the border. The {@link Color#brighter()} method will be used for when the border is
     * focused.
     *
     * @param color An ARGB integer color.
     */
    @PublicAPI
    public Builder border(int color) {
        return this.border(new Color(color), new Color(color).brighter());
    }

    /**
     * Set a hover border and background color for this input widget.
     *
     * @param border     The border {@link Color} when hovered.
     * @param background The background {@link Color} when hovered.
     */
    @PublicAPI
    public Builder hover(Color border, Color background) {
        this.hoverBorderColor = border;
        this.hoverBackgroundColor = background;

        return this.self();
    }

    /**
     * Set a hover border and background color for this input widget.
     *
     * @param border     The border {@link IntSupplier} that provides an ARGB integer color when hovered.
     * @param background The background {@link IntSupplier} that provides an ARGB integer color when hovered.
     */
    @PublicAPI
    public Builder hover(IntSupplier border, IntSupplier background) {
        return this.hover(new Color(border), new Color(background));
    }

    /**
     * Set a hover border and background color for this input widget.
     *
     * @param border     The ARGB color for the border when hovered.
     * @param background The ARGB color for the background when hovered.
     */
    @PublicAPI
    public Builder hover(int border, int background) {
        return this.hover(new Color(border), new Color(background));
    }

    /**
     * Change the focused and unfocused text color for this input widget.
     *
     * @param focused   A {@link Color} for when the widget is focused.
     * @param unfocused A {@link Color} for when the widget is unfocused.
     */
    @PublicAPI
    public Builder textColor(Color focused, Color unfocused) {
        this.textColor = focused;
        this.textUnfocusedColor = unfocused;

        return this.self();
    }

    /**
     * Change the focused and unfocused text color for this input widget.
     *
     * @param focused   A {@link IntSupplier} that provides an ARGB color integer for when the widget is focused.
     * @param unfocused A {@link IntSupplier} that provides an ARGB color integer for when the widget is unfocused.
     */
    @PublicAPI
    public Builder textColor(IntSupplier focused, IntSupplier unfocused) {
        return this.textColor(new Color(focused), new Color(unfocused));
    }

    /**
     * Change the focused and unfocused text color for this input widget.
     *
     * @param focused   An ARGB color integer for when the widget is focused.
     * @param unfocused An ARGB color integer for when the widget is unfocused.
     */
    @PublicAPI
    public Builder textColor(int focused, int unfocused) {
        return this.textColor(new Color(focused), new Color(unfocused));
    }

    /**
     * Change text color for this input widget.
     *
     * @param color A {@link Color} for the input text.
     */
    @PublicAPI
    public Builder textColor(Color color) {
        this.textColor = color;

        return this.self();
    }

    /**
     * Change text color for this input widget.
     *
     * @param color A {@link IntSupplier} that provides an ARGB color.
     */
    @PublicAPI
    public Builder textColor(IntSupplier color) {
        return this.textColor(new Color(color));
    }

    /**
     * Change text color for this input widget.
     *
     * @param color An ARGB integer color for text.
     */
    @PublicAPI
    public Builder textColor(int color) {
        return this.textColor(new Color(color));
    }

    /**
     * Change the cursor color for this input widget.
     *
     * @param color A {@link Color} for the cursor.
     */
    @PublicAPI
    public Builder cursorColor(Color color) {
        this.cursorColor = color;

        return this.self();
    }

    /**
     * Change the cursor color for this input widget.
     *
     * @param color A {@link IntSupplier} that provides an ARGB color.
     */
    @PublicAPI
    public Builder cursorColor(IntSupplier color) {
        return this.cursorColor(new Color(color));
    }

    /**
     * Change the cursor color for this input widget.
     *
     * @param color An ARGB integer color for the cursor.
     */
    @PublicAPI
    public Builder cursorColor(int color) {
        return this.cursorColor(new Color(color));
    }

    /**
     * Change the vertical cursor color for this input widget.
     *
     * @param color A {@link Color} for the vertical cursor.
     */
    @PublicAPI
    public Builder cursorVerticalColor(Color color) {
        this.cursorVerticalColor = color;

        return this.self();
    }

    /**
     * Change the vertical cursor color for this input widget.
     *
     * @param color A {@link IntSupplier} that provides an ARGB color.
     */
    @PublicAPI
    public Builder cursorVerticalColor(IntSupplier color) {
        return this.cursorVerticalColor(new Color(color));
    }

    /**
     * Change the vertical cursor color for this input widget.
     *
     * @param color An ARGB integer color for the vertical cursor.
     */
    @PublicAPI
    public Builder cursorVerticalColor(int color) {
        return this.cursorVerticalColor(new Color(color));
    }
}
