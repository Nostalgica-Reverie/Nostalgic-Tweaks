package mod.adrenix.nostalgic.client.gui.widget.text;

import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class TextBuilder extends DynamicBuilder<TextBuilder, TextWidget>
        implements LayoutBuilder<TextBuilder, TextWidget>, VisibleBuilder<TextBuilder, TextWidget>,
        TooltipBuilder<TextBuilder, TextWidget>, ScaleBuilder<TextBuilder, TextWidget> {
    /* Fields */

    Supplier<Component> text;
    boolean useTextWidth = false;
    boolean useSeparator = false;
    boolean disableUnderline = false;
    boolean isCenterVertical = false;
    boolean isCenterAligned = false;
    boolean useClickSound = true;
    boolean useEllipsis = false;
    int lineHeight = 0;
    int iconMargin = 0;
    LinkedHashSet<DynamicWidget<?, ?>> intersections = new LinkedHashSet<>();
    @Nullable IntSupplier maxEndX = null;
    @Nullable Color hoverColor = null;
    @Nullable Color backgroundColor = null;
    IntSupplier separatorHeight = () -> 2;
    Color separatorColor = Color.WHITE;
    Color clickableColor = Color.WHITE;
    Color fontColor = Color.WHITE;
    BooleanSupplier backgroundIf = BooleanSupplier.ALWAYS;
    BooleanSupplier highlightIf = BooleanSupplier.ALWAYS;
    BooleanSupplier italic = BooleanSupplier.NEVER;
    @Nullable Runnable onPress = null;
    @Nullable Animation highlighter = null;
    @Nullable BlankWidget pressArea = null;

    /* Constructor */

    protected TextBuilder(Supplier<Component> supplier) {
        this.text = supplier;
        this.canFocus = () -> this.onPress != null;
    }

    protected TextBuilder(Component text) {
        this(() -> text);
    }

    /* Methods */

    @Override
    public TextBuilder self() {
        return this;
    }

    /**
     * Keep the width of this widget the same as the width of the current text.
     */
    @PublicAPI
    public TextBuilder useTextWidth() {
        this.useTextWidth = true;

        return this;
    }

    /**
     * Keep the width of this widget the same as the width of the current text and wrap the text if the widget's ending
     * x-position exceeds the x-position returned by the given supplier.
     *
     * @param maxEndX An {@link IntSupplier} that provides an ending x-position that the text widget cannot exceed.
     */
    @PublicAPI
    public TextBuilder useTextWidth(IntSupplier maxEndX) {
        this.maxEndX = maxEndX;

        return this.useTextWidth();
    }

    /**
     * Keep the width of this widget the same as the width of the current text and wrap the text if the text's ending
     * x-position exceeds the given x-position.
     *
     * @param maxEndX The maximum ending x-position allowed for this widget.
     */
    @PublicAPI
    public TextBuilder useTextWidth(int maxEndX) {
        return this.useTextWidth(() -> maxEndX);
    }

    /**
     * Change the line height used by the {@link MultiLineText}. The default is the height defined in the game's font.
     *
     * @param height A custom line height.
     */
    @PublicAPI
    public TextBuilder lineHeight(int height) {
        this.lineHeight = height;

        return this;
    }

    /**
     * Make the text centered only within its bounding height box.
     */
    @PublicAPI
    public TextBuilder centerVertical() {
        this.isCenterVertical = true;
        this.isCenterAligned = true;

        return this;
    }

    /**
     * Make the text centered within its bounding width/height box.
     */
    @PublicAPI
    public TextBuilder centerAligned() {
        this.isCenterAligned = true;

        return this;
    }

    /**
     * Render a separator bar. If the text is centered, then two bars will be visible with one being on the left side
     * and the other being on the right side of the text. Otherwise, the bar will be on the right side of the text.
     *
     * @param color  The {@link Color} of the separator.
     * @param height A {@link IntSupplier} instance that provides the separator height.
     */
    @PublicAPI
    public TextBuilder separator(Color color, IntSupplier height) {
        this.useSeparator = true;
        this.separatorColor = color;
        this.separatorHeight = height;

        return this;
    }

    /**
     * Render a separator bar. If the text is centered, then two bars will be visible with one being on the left side
     * and the other being on the right side of the text. Otherwise, the bar will be on the right side of the text.
     *
     * @param color  The {@link Color} of the separator.
     * @param height The height of the separator bar.
     */
    @PublicAPI
    public TextBuilder separator(Color color, int height) {
        return this.separator(color, () -> height);
    }

    /**
     * Render a separator bar. If the text is centered, then two bars will be visible with one being on the left side
     * and the other being on the right side of the text. Otherwise, the bar will be on the right side of the text.
     *
     * @param color  An ARGB integer color.
     * @param height The height of the separator bar.
     * @see #separator(Color, IntSupplier)
     */
    @PublicAPI
    public TextBuilder separator(int color, int height) {
        return this.separator(new Color(color), height);
    }

    /**
     * Render a separator bar. If the text is centered, then two bars will be visible with one being on the left side
     * and the other being on the right side of the text. Otherwise, the bar will be on the right side of the text. The
     * separator will have a default height of two.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public TextBuilder separator(Color color) {
        return this.separator(color, () -> 2);
    }

    /**
     * Render a separator bar. If the text is centered, then two bars will be visible with one being on the left side
     * and the other being on the right side of the text. Otherwise, the bar will be on the right side of the text. The
     * separator will use the current font color.
     *
     * @param height The height of the separator bar.
     */
    @PublicAPI
    public TextBuilder separator(int height) {
        return this.separator(this.fontColor, height);
    }

    /**
     * Render a separator bar. If the text is centered, then two bars will be visible with one being on the left side
     * and the other being on the right side of the text. Otherwise, the bar will be on the right side of the text. The
     * separator will have a default height of two and will use the current font color.
     */
    @PublicAPI
    public TextBuilder separator() {
        return this.separator(this.fontColor, () -> 2);
    }

    /**
     * Shorten the text using ellipsis (...) when it exceeds the widget's width size.
     */
    @PublicAPI
    public TextBuilder shorten() {
        this.useEllipsis = true;

        return this;
    }

    /**
     * This is an alias method that is identical to {@link #shorten()}.
     */
    @PublicAPI
    public TextBuilder ellipsis() {
        return this.shorten();
    }

    /**
     * Set the color of this text widget. The color will stay in sync with the given {@link Color} instance.
     *
     * @param color A {@link Color} instance.
     */
    @PublicAPI
    public TextBuilder color(Color color) {
        this.fontColor = color;

        return this;
    }

    /**
     * Set the color of this text widget. The color will stay in sync with the given {@link IntSupplier}.
     *
     * @param color A {@link IntSupplier} that provides the text's ARGB color.
     */
    @PublicAPI
    public TextBuilder color(IntSupplier color) {
        return this.color(new Color(color));
    }

    /**
     * Set the color of this text widget.
     *
     * @param color A custom ARGB integer.
     */
    @PublicAPI
    public TextBuilder color(int color) {
        return this.color(new Color(color));
    }

    /**
     * Set the color of this text widget using chat formatting. <b color=red>Note:</b> Any formatter enumeration that
     * does not have a color will cause the text widget to be white.
     *
     * @param formatting A vanilla chat color formatting enumeration.
     */
    @PublicAPI
    public TextBuilder color(ChatFormatting formatting) {
        return this.color(Optional.ofNullable(formatting.getColor()).orElse(Color.WHITE.get()));
    }

    /**
     * Set the background color of this text widget.
     *
     * @param color A {@link Color} that provides the background color for this widget.
     */
    @PublicAPI
    public TextBuilder background(Color color) {
        this.backgroundColor = color;

        return this;
    }

    /**
     * Set the background color of this text widget.
     *
     * @param color A background ARGB color integer.
     */
    @PublicAPI
    public TextBuilder background(int color) {
        return this.background(new Color(color));
    }

    /**
     * Set the background color of this text widget using chat formatting. <b color=red>Note:</b> Any formatter
     * enumeration that does not have a color will cause the background color to be white.
     *
     * @param formatting A vanilla chat color formatting enumeration.
     */
    @PublicAPI
    public TextBuilder background(ChatFormatting formatting) {
        return this.color(Optional.ofNullable(formatting.getColor()).orElse(Color.WHITE.get()));
    }

    /**
     * Only show the defined background color when the given boolean supplier yields {@code true}.
     *
     * @param when A {@link BooleanSupplier} instance.
     */
    @PublicAPI
    public TextBuilder backgroundWhen(BooleanSupplier when) {
        this.backgroundIf = when;

        return this;
    }

    /**
     * Set a boolean supplier that indicates whether the text label is rendered in italics.
     *
     * @param italics A {@link BooleanSupplier} instance.
     */
    @PublicAPI
    public TextBuilder italicsWhen(BooleanSupplier italics) {
        this.italic = italics;

        return this;
    }

    /**
     * Inform the text renderer to never underline the text.
     */
    @PublicAPI
    public TextBuilder disableUnderline() {
        this.disableUnderline = true;

        return this;
    }

    /**
     * Provide instructions to perform when the text is clicked. The text will be underlined when it is clickable. The
     * clickable color parameter is the color used when the mouse hovers over the text.
     *
     * @param onPress        Post successful click instructions.
     * @param clickableColor A {@link Color} that is used when the mouse hovers over the clickable text.
     */
    @PublicAPI
    public TextBuilder onPress(Runnable onPress, Color clickableColor) {
        this.onPress = onPress;
        this.clickableColor = clickableColor;

        return this;
    }

    /**
     * Provide instructions to perform when the text is clicked. The text will be underlined when it is clickable. The
     * clickable color parameter is the color used when the mouse hovers over the text.
     *
     * @param onPress        Post successful click instructions.
     * @param clickableColor An RGB color that is used when the mouse hovers over the clickable text.
     */
    @PublicAPI
    public TextBuilder onPress(Runnable onPress, int clickableColor) {
        return this.onPress(onPress, new Color(clickableColor));
    }

    /**
     * Provide instructions to perform when the text is clicked. The text will be underlined when it is clickable. The
     * default color shown is what is currently set in this builder's color field. If you want to the hover color to be
     * the same as the non-hover color, then use {@link TextBuilder#color(int)} before invoking this function.
     *
     * @param onPress Post successful click instructions.
     */
    @PublicAPI
    public TextBuilder onPress(Runnable onPress) {
        return this.onPress(onPress, this.fontColor);
    }

    /**
     * An additional area can be defined as a "pressing area" outside the text widget's bounds. This prevents the
     * requirement that the mouse must be within the text area to cause the text to be highlighted and eligible for
     * clicking.
     *
     * @param pressArea Provide a blank widget that has its x/y and width/height defined as the extra "pressing area"
     *                  around the text widget.
     */
    @PublicAPI
    public TextBuilder pressArea(BlankWidget pressArea) {
        this.pressArea = pressArea;

        return this;
    }

    /**
     * Add an intersecting widget that overlaps this text widget's boundaries. This will let the widget(s) receive any
     * events that the text widget would have otherwise consumed, like a mouse click event or mouse hover event.
     * Multiple intersections can be added at a time.
     *
     * @param widgets A varargs list of {@link DynamicWidget} to add as intersections.
     */
    @PublicAPI
    public TextBuilder intersection(DynamicWidget<?, ?>... widgets) {
        this.intersections.addAll(Arrays.asList(widgets));

        return this;
    }

    /**
     * Prevents the button "click sound" from playing when the press area is successfully clicked within this text
     * widget.
     */
    @PublicAPI
    public TextBuilder noClickSound() {
        this.useClickSound = false;

        return this;
    }

    /**
     * Use an animation that dictates how the underline and highlight color will appear when the text widget is
     * clickable.
     *
     * @param animation An {@link Animation} instance.
     */
    @PublicAPI
    public TextBuilder highlighter(Animation animation) {
        this.highlighter = animation;
        return this;
    }

    /**
     * Animate the highlighting of the text, if and only if, the given supplier yields {@code true}.
     *
     * @param highlightIf A {@code boolean} {@link Supplier}.
     */
    @PublicAPI
    public TextBuilder highlightIf(BooleanSupplier highlightIf) {
        this.highlightIf = highlightIf;

        return this;
    }

    /**
     * Show a specific color when the widget is hovered or focused.
     *
     * @param color The {@link Color} to show when the given widget is hovered or focused.
     */
    @PublicAPI
    public TextBuilder hoverOrFocusColor(Color color) {
        this.hoverColor = color;

        return this;
    }

    /**
     * Set an icon that will be rendered to the left of the text with the given margin.
     *
     * @param supplier A {@link TextureIcon} {@link Supplier}.
     * @param margin   The margin between the icon and the text.
     * @see #icon(Supplier)
     * @see #icon(TextureIcon, int)
     * @see #icon(TextureIcon)
     */
    @PublicAPI
    public TextBuilder icon(Supplier<TextureIcon> supplier, int margin) {
        int size = GuiUtil.textHeight() - 1;

        this.iconWidth = size;
        this.iconHeight = size;

        this.iconSupplier = supplier;
        this.iconMargin = margin;

        return this;
    }

    /**
     * Set an icon that will be rendered to the left of the text with a margin of {@code 3}.
     *
     * @param supplier A {@link TextureIcon} {@link Supplier}.
     * @see #icon(Supplier, int)
     * @see #icon(TextureIcon)
     * @see #icon(TextureIcon, int)
     */
    @PublicAPI
    public TextBuilder icon(Supplier<TextureIcon> supplier) {
        return this.icon(supplier, 3);
    }

    /**
     * Set an icon that will be rendered to the left of the text with the given margin.
     *
     * @param icon   A {@link TextureIcon}.
     * @param margin The margin between the icon and the text.
     * @see #icon(Supplier, int)
     * @see #icon(Supplier)
     * @see #icon(TextureIcon)
     */
    @PublicAPI
    public TextBuilder icon(TextureIcon icon, int margin) {
        return this.icon(() -> icon, margin);
    }

    /**
     * Set an icon that will be rendered to the left of the text with a margin of {@code 3}.
     *
     * @param icon A {@link TextureIcon}.
     * @see #icon(Supplier, int)
     * @see #icon(Supplier)
     * @see #icon(TextureIcon, int)
     */
    @PublicAPI
    public TextBuilder icon(TextureIcon icon) {
        return this.icon(() -> icon, 3);
    }

    /**
     * Brighten the icon by the given amount when this widget is focused or hovered.
     *
     * @param amount The amount to brighten the icon by. An amount less than {@code 1.0F} will darken the icon and an
     *               amount greater than {@code 1.0F} will brighten the icon.
     */
    @PublicAPI
    public TextBuilder brightenIconOnHover(float amount) {
        this.brightenOnHover = true;
        this.brightenAmount = () -> amount;

        return this;
    }

    /**
     * Brighten the icon using the default icon brightness settings when this widget is focused or hovered. Use
     * {@link #brightenIconOnHover(float)} to enable brightness and to define how much to brighten by.
     */
    @PublicAPI
    public TextBuilder brightenIconOnHover() {
        this.brightenOnHover = true;

        return this;
    }

    /**
     * @return Whether the text height is being controlled by a supplier.
     */
    protected boolean isHeightOverridden() {
        return this.height != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TextWidget construct() {
        if (this.lineHeight == 0)
            this.lineHeight = GuiUtil.textHeight();

        return new TextWidget(this);
    }
}
