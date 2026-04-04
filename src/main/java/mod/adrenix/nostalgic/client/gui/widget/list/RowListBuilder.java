package mod.adrenix.nostalgic.client.gui.widget.list;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.ActiveBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.VisibleBuilder;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.renderer.RenderPass;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.function.FloatSupplier;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class RowListBuilder extends DynamicBuilder<RowListBuilder, RowList>
        implements LayoutBuilder<RowListBuilder, RowList>, ActiveBuilder<RowListBuilder, RowList>,
        VisibleBuilder<RowListBuilder, RowList> {
    /* Fields */

    int topMargin = 0;
    int heightOverflowMargin = 0;
    int verticalMargin = 4;
    int horizontalMargin = 4;
    int separatorHeight = 1;
    int separatorPadding = 2;
    int defaultRowHeight = 20;
    int defaultRowWidth = 0;
    int scrollbarWidth = 6;
    int scrollbarLeftMargin = 0;
    Color scrollbarBackground = Color.DARK_CHARCOAL;
    Color separatorColor = Color.WHITE;
    boolean leftAlignedScrollbar = false;
    boolean centerRows = false;
    boolean renderBatched = true;
    boolean useSeparators = false;
    boolean useMenuBackground = false;
    boolean useScissorRendering = true;
    boolean showSelectionBorder = false;
    boolean renderBackgroundDirt = false;
    boolean renderTopAndBottomDirt = false;
    boolean renderTopAndBottomShadow = false;
    boolean renderBackgroundOpacity = false;
    double rowHighlightAlpha = 0.0D;
    Color rowHighlightColor = Color.WHITE;
    Animation rowHighlightAnimation = null;
    BooleanSupplier useHighlightsWhen = BooleanSupplier.ALWAYS;
    BooleanSupplier useOverrideHighlights = BooleanSupplier.NEVER;
    IntSupplier overrideHighlightOpacity = () -> 100;
    FloatSupplier backgroundOpacity = null;
    Supplier<Rectangle> scissorRectangle = null;
    Supplier<Component> emptyMessage = null;
    RowListRenderer postRenderer = RowListRenderer.EMPTY;
    RowListRenderer backgroundRenderer = RowListRenderer.EMPTY;

    /* Constructor */

    protected RowListBuilder() {
        this.renderPass = RenderPass.FIRST;
    }

    /* Methods */

    @Override
    public RowListBuilder self() {
        return this;
    }

    /**
     * Display a message with the given {@link Supplier} when the row list is empty.
     *
     * @param supplier A {@link Supplier} that provides a {@link Component} message.
     * @see #emptyMessage(Component)
     * @see #emptyMessage(Translation)
     */
    @PublicAPI
    public RowListBuilder emptyMessage(Supplier<Component> supplier) {
        this.emptyMessage = supplier;

        return this;
    }

    /**
     * Display a message with the given {@link Component} when the row list is empty.
     *
     * @param message A {@link Component} empty message.
     * @see #emptyMessage(Supplier)
     * @see #emptyMessage(Translation)
     */
    @PublicAPI
    public RowListBuilder emptyMessage(Component message) {
        return this.emptyMessage(() -> message);
    }

    /**
     * Display a message with the given {@link Translation} when the row list is empty.
     *
     * @param langKey A {@link Translation} instance.
     * @see #emptyMessage(Supplier)
     * @see #emptyMessage(Component)
     */
    @PublicAPI
    public RowListBuilder emptyMessage(Translation langKey) {
        return this.emptyMessage(langKey::get);
    }

    /**
     * Render separators between row widgets.
     *
     * @param color   The {@link Color} of the row separators.
     * @param height  The height of the separators.
     * @param padding The padding between a separator and a row widget.
     * @see #useSeparators(Color)
     * @see #useSeparators(int, int)
     * @see #useSeparators()
     */
    @PublicAPI
    public RowListBuilder useSeparators(Color color, int height, int padding) {
        this.useMenuBackground = false;
        this.useSeparators = true;
        this.separatorColor = color;
        this.separatorHeight = height;
        this.separatorPadding = padding;

        return this;
    }

    /**
     * Use the game's row list background and separator textures.
     */
    @PublicAPI
    public RowListBuilder useMenuBackground() {
        this.useMenuBackground = true;
        this.useSeparators = false;
        this.renderBackgroundDirt = false;
        this.renderBackgroundOpacity = false;

        return this;
    }

    /**
     * Render separators between row widgets.
     *
     * @param color The {@link Color} of the row separators.
     * @see #useSeparators(Color, int, int)
     * @see #useSeparators(int, int)
     * @see #useSeparators()
     */
    @PublicAPI
    public RowListBuilder useSeparators(Color color) {
        return this.useSeparators(color, 1, 2);
    }

    /**
     * Render separators between row widgets.
     *
     * @param height  The height of the separators.
     * @param padding The padding between a separator and a row widget.
     * @see #useSeparators(Color, int, int)
     * @see #useSeparators(Color)
     * @see #useSeparators()
     */
    @PublicAPI
    public RowListBuilder useSeparators(int height, int padding) {
        return this.useSeparators(Color.WHITE, height, padding);
    }

    /**
     * Render separators between row widgets.
     *
     * @see #useSeparators(Color, int, int)
     * @see #useSeparators(Color)
     * @see #useSeparators(int, int)
     */
    @PublicAPI
    public RowListBuilder useSeparators() {
        return this.useSeparators(Color.WHITE, 1, 2);
    }

    /**
     * Center rows horizontally.
     */
    @PublicAPI
    public RowListBuilder centerRows() {
        this.centerRows = true;

        return this;
    }

    /**
     * Align the scrollbar to the left. This will move the scrollbar to the right of the widest row with the given
     * margin.
     *
     * @param margin The margin between the widest row and the scrollbar.
     */
    @PublicAPI
    public RowListBuilder leftAlignedScrollbar(int margin) {
        this.leftAlignedScrollbar = true;
        this.scrollbarLeftMargin = margin;

        return this;
    }

    /**
     * Change the scrollbar's background color.
     *
     * @param color A background {@link Color}.
     */
    @PublicAPI
    public RowListBuilder scrollbarBackground(Color color) {
        this.scrollbarBackground = color;

        return this;
    }

    /**
     * Change the default scrollbar width, which is 6, to a different width.
     *
     * @param width The scrollbar width.
     */
    @PublicAPI
    public RowListBuilder scrollbarWidth(int width) {
        this.scrollbarWidth = width;

        return this;
    }

    /**
     * Change the default row width, which is 0, to a different width. Rows will automatically adjust their width to the
     * current list context, but if a static width is desired, then use this builder property.
     *
     * @param defaultWidth The default row width.
     */
    @PublicAPI
    public RowListBuilder defaultRowWidth(int defaultWidth) {
        this.defaultRowWidth = defaultWidth;

        return this;
    }

    /**
     * Change the default row height, which is 20, to a different height. Rows will have their height resized if their
     * widgets exceed the default row height. Otherwise, the default row height is used.
     *
     * @param defaultHeight The default row height.
     */
    @PublicAPI
    public RowListBuilder defaultRowHeight(int defaultHeight) {
        this.defaultRowHeight = defaultHeight;

        return this;
    }

    /**
     * Change the extra margin added to the height of a row when there is a widget with an ending y-position greater
     * than that of the row. The default margin added is zero. Any rows that define this in their builder will override
     * this row list setting.
     *
     * @param margin The height overflow margin.
     */
    @PublicAPI
    public RowListBuilder heightOverflowMargin(int margin) {
        this.heightOverflowMargin = margin;

        return this;
    }

    /**
     * Render a blue outline around a row when it has a focused widget.
     */
    @PublicAPI
    public RowListBuilder showSelectionBorder() {
        this.showSelectionBorder = true;

        return this;
    }

    /**
     * Apply a global background color, with the given fading animation, and opacity for rows when the mouse hovers over
     * them.
     *
     * @param alpha     A normalized maximum alpha transparency of the row [0.0D, 1.0D].
     * @param animation An {@link Animation} instance.
     * @param color     A {@link Color} instance.
     */
    @PublicAPI
    public RowListBuilder highlight(double alpha, Animation animation, Color color) {
        this.rowHighlightAlpha = Mth.clamp(alpha, 0.0D, 1.0D);
        this.rowHighlightAnimation = animation;
        this.rowHighlightColor = color;

        return this;
    }

    /**
     * Apply a global white background color, with the given fading animation, and opacity for rows when the mouse
     * hovers over them.
     *
     * @param alpha     A normalized maximum alpha transparency of the row [0.0D, 1.0D].
     * @param animation An {@link Animation} instance.
     */
    @PublicAPI
    public RowListBuilder highlight(double alpha, Animation animation) {
        return this.highlight(alpha, animation, Color.WHITE);
    }

    /**
     * Override row highlighting when certain conditions are met and apply the given override amount.
     *
     * @param when   A {@link BooleanSupplier} that indicates when to use override highlights.
     * @param amount A {@link BooleanSupplier} that yields a normalized {@code integer}. The value is automatically
     *               clamped.
     */
    @PublicAPI
    public RowListBuilder overrideHighlights(BooleanSupplier when, IntSupplier amount) {
        this.useOverrideHighlights = when;
        this.overrideHighlightOpacity = () -> Mth.clamp(amount.getAsInt(), 0, 100);

        return this;
    }

    /**
     * Apply row highlighting only when the given supplier yields {@code true}.
     *
     * @param when A {@link BooleanSupplier} that indicates when to use highlights.
     */
    @PublicAPI
    public RowListBuilder useHighlightsWhen(BooleanSupplier when) {
        this.useHighlightsWhen = when;

        return this;
    }

    /**
     * Disable batch rendering for elements rendered in rows.
     */
    @PublicAPI
    public RowListBuilder disableBatchRendering() {
        this.renderBatched = true;

        return this;
    }

    /**
     * Change the margin added between the first row and the top of the row list. The default margin is {@code 0}.
     *
     * @param margin A margin amount.
     */
    @PublicAPI
    public RowListBuilder topMargin(int margin) {
        this.topMargin = margin;

        return this;
    }

    /**
     * Change the margin added between the top and bottom of rows. The default margin is {@code 4}.
     *
     * @param margin A margin amount.
     */
    @PublicAPI
    public RowListBuilder verticalMargin(int margin) {
        this.verticalMargin = margin;

        return this;
    }

    /**
     * Change the margin added between the left and right side of the row list. The default margin is {@code 4}.
     *
     * @param margin A margin amount.
     */
    @PublicAPI
    public RowListBuilder horizontalMargin(int margin) {
        this.horizontalMargin = margin;

        return this;
    }

    /**
     * Render background dirt when there is no Minecraft level present in the background. Useful for lists that take up
     * the entire game window screen.
     */
    @PublicAPI
    public RowListBuilder renderBackgroundDirt() {
        this.renderBackgroundDirt = true;
        this.useMenuBackground = false;

        return this;
    }

    /**
     * Render top and bottom dirt borders that extend to the top and bottom of the game screen. This should only be used
     * by lists that take up the entire game window screen.
     */
    @PublicAPI
    public RowListBuilder renderTopAndBottomDirt() {
        this.renderTopAndBottomDirt = true;

        return this;
    }

    /**
     * Render shadow edges at the top and bottom of the row list. If {@link #renderTopAndBottomDirt()} is used, then
     * this is already enabled.
     */
    @PublicAPI
    public RowListBuilder renderTopAndBottomShadow() {
        this.renderTopAndBottomShadow = true;

        return this;
    }

    /**
     * Disable the use of OpenGL scissor rendering. The scissored rendering prevents rows from being rendered outside
     * the row list's bounds. This method should only be invoked if top and bottom dirt rendering is enabled.
     */
    @PublicAPI
    public RowListBuilder disableScissorRendering() {
        this.useScissorRendering = true;

        return this;
    }

    /**
     * Use OpenGL scissor rendering to prevent rows from being outside the given rectangle's bounds. The rectangle
     * provides an alternative way to setting specific bounds of where the scissoring occurs on the screen. This should
     * be used if top and bottom dirt rendering is disabled and nothing is being rendered in its place.
     *
     * @param supplier A {@link Supplier} that provides a {@link Rectangle} instance that defines scissoring bounds.
     */
    @PublicAPI
    public RowListBuilder useScissorRectangle(Supplier<Rectangle> supplier) {
        this.useScissorRendering = true;
        this.scissorRectangle = supplier;

        return this;
    }

    /**
     * Set a custom renderer for this list. By default, the background renderer is empty. If
     * {@link #renderTopAndBottomDirt()} is invoked, then the vanilla row list background renderer is used.
     *
     * @param renderer A {@link RowListRenderer} consumer.
     */
    @PublicAPI
    public RowListBuilder backgroundRenderer(RowListRenderer renderer) {
        this.backgroundRenderer = renderer;

        return this;
    }

    /**
     * Set the background opacity for this list. The yield from the supplier will be clamped. Note, this will only be
     * applied when using {@link #renderTopAndBottomDirt()}.
     *
     * @param opacity An {@link IntSupplier} that provides opacity in the range 0-100.
     */
    @PublicAPI
    public RowListBuilder backgroundOpacity(IntSupplier opacity) {
        this.backgroundOpacity = () -> Mth.clamp(opacity.getAsInt() / 100.0F, 0.0F, 1.0F);
        this.renderBackgroundOpacity = true;
        this.useMenuBackground = false;

        return this;
    }

    /**
     * Set the background opacity for this list. The given argument will be clamped. Note, this will only be applied
     * when using {@link #renderTopAndBottomDirt()}.
     *
     * @param opacity The background opacity that ranges 0-100.
     */
    @PublicAPI
    public RowListBuilder backgroundOpacity(int opacity) {
        return this.backgroundOpacity(() -> opacity);
    }

    /**
     * Render the default background opacity.
     */
    @PublicAPI
    public RowListBuilder renderBackgroundOpacity() {
        this.renderBackgroundOpacity = true;

        return this;
    }

    /**
     * Set post-rendering instructions for this list.
     *
     * @param renderer A {@link RowListRenderer} function consumer.
     */
    @PublicAPI
    public RowListBuilder postRenderer(RowListRenderer renderer) {
        this.postRenderer = renderer;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RowList construct() {
        return new RowList(this);
    }
}
