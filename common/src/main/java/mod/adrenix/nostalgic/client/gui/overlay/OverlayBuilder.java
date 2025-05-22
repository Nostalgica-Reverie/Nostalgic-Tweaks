package mod.adrenix.nostalgic.client.gui.overlay;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.function.*;

public class OverlayBuilder
{
    /* Fields */

    @Nullable final Screen parent;
    final Supplier<Component> title;
    final OverlayWidgets widgets;
    final LinkedHashSet<Consumer<Overlay>> closingInstructions;
    final LinkedHashSet<Consumer<Overlay>> onTickInstructions;
    final LinkedHashSet<OverlayKeyboard> onKeyPressInstructions;

    boolean canDrag = true;
    boolean hasBorder = true;
    boolean hasShadow = true;
    boolean resizeForWidgets = false;
    boolean resizeUsingPercentage = false;
    boolean resizeWidthForWidgets = false;
    boolean resizeHeightForWidgets = false;
    DoubleSupplier resizeWidthPercentage = () -> 0.0D;
    DoubleSupplier resizeHeightPercentage = () -> 0.0D;
    double x = -1.0D;
    double y = -1.0D;
    int width = 100;
    int height = 100;
    int minWidth = 50;
    int minHeight = 50;
    int padding = 0;
    int scissorPadding = 0;
    int aboveOrBelowMargin = 0;
    int resizeWidthMaximum = 0;
    int resizeHeightMaximum = 0;
    @Nullable OverlayRenderer firstRenderer = null;
    @Nullable OverlayRenderer lastRenderer = null;
    @Nullable Gradient backgroundGradient = null;
    Color outlineColor = Color.TRANSPARENT;
    Color backgroundColor = new Color(0, 0, 0, 198);
    Color shadowColor = new Color(0, 0, 0, 165);
    boolean onlyAbove = false;
    boolean onlyBelow = false;
    @Nullable LayoutElement aboveOrBelow = null;
    @Nullable ToIntFunction<Overlay> xFunction = null;
    @Nullable ToIntFunction<Overlay> yFunction = null;
    @Nullable Consumer<Overlay> onResize = null;
    @Nullable Component infoMessage = null;
    @Nullable Runnable onClose = null;
    TextureIcon icon = Icons.GENERIC_OVERLAY;

    /* Constructor */

    OverlayBuilder(Supplier<Component> title)
    {
        this.widgets = new OverlayWidgets();
        this.closingInstructions = new LinkedHashSet<>();
        this.onTickInstructions = new LinkedHashSet<>();
        this.onKeyPressInstructions = new LinkedHashSet<>();
        this.parent = GuiUtil.getScreen().orElse(null);
        this.title = title;
    }

    /* Methods */

    /**
     * Define a widget to be added to the overlay when it is built.
     *
     * @param widget A {@link DynamicWidget} instance.
     */
    @PublicAPI
    public OverlayBuilder addWidget(DynamicWidget<?, ?> widget)
    {
        this.widgets.addScissoredExternal(widget);

        return this;
    }

    /**
     * Define a widget that will listen to overlay events but will not be updated nor rendered by the overlay.
     *
     * @param widget A {@link DynamicWidget} to pass events to.
     */
    @PublicAPI
    public OverlayBuilder addListener(DynamicWidget<?, ?> widget)
    {
        this.widgets.listeners.add(widget);

        return this;
    }

    /**
     * Set the position of this overlay.
     *
     * @param x The overlay x-coordinate.
     * @param y The overlay y-coordinate.
     * @see #pos(ToIntFunction, ToIntFunction)
     * @see #pos(IntSupplier, IntSupplier)
     */
    @PublicAPI
    public OverlayBuilder pos(int x, int y)
    {
        this.x = x;
        this.y = y;

        return this;
    }

    /**
     * Set the position of this overlay using functions. This will make the overlay not draggable by the user since its
     * position will be set using the given functions.
     *
     * @param xFunction A {@link ToIntFunction} that accepts the {@link Overlay} instance and provides an x-coordinate.
     * @param yFunction A {@link ToIntFunction} that accepts the {@link Overlay} instance and provides a y-coordinate.
     * @see #pos(IntSupplier, IntSupplier)
     * @see #pos(int, int)
     */
    @PublicAPI
    public OverlayBuilder pos(ToIntFunction<Overlay> xFunction, ToIntFunction<Overlay> yFunction)
    {
        this.xFunction = xFunction;
        this.yFunction = yFunction;
        this.canDrag = false;

        return this;
    }

    /**
     * Set the position of this overlay using suppliers. This will make the overlay not draggable by the user since its
     * position will be set using the given suppliers.
     *
     * @param xSupplier An {@link IntSupplier} instance that provides the overlay's x-coordinate.
     * @param ySupplier An {@link IntSupplier} instance that provides the overlay's y-coordinate.
     * @see #pos(ToIntFunction, ToIntFunction)
     * @see #pos(int, int)
     */
    @PublicAPI
    public OverlayBuilder pos(IntSupplier xSupplier, IntSupplier ySupplier)
    {
        return this.pos((overlay) -> xSupplier.getAsInt(), (overlay) -> ySupplier.getAsInt());
    }

    /**
     * Set the x-coordinate of this overlay.
     *
     * @param x The overlay x-coordinate.
     * @see #setX(ToIntFunction)
     * @see #setX(IntSupplier)
     */
    @PublicAPI
    public OverlayBuilder setX(int x)
    {
        this.x = x;
        return this;
    }

    /**
     * Set the x-coordinate of this overlay using a function. This will make the overlay not draggable by the user since
     * its x-coordinate will be set using the given function.
     *
     * @param xFunction A {@link ToIntFunction} that accepts the {@link Overlay} and provides an x-coordinate.
     * @see #setX(IntSupplier)
     * @see #setX(int)
     */
    @PublicAPI
    public OverlayBuilder setX(ToIntFunction<Overlay> xFunction)
    {
        this.xFunction = xFunction;
        this.canDrag = false;

        return this;
    }

    /**
     * Set the x-coordinate of this overlay using a supplier. This will make the overlay not draggable by the user since
     * its x-coordinate will be set using the given supplier.
     *
     * @param xSupplier An {@link IntSupplier} instance that provides the overlay's x-coordinate.
     * @see #setX(ToIntFunction)
     * @see #setX(int)
     */
    @PublicAPI
    public OverlayBuilder setX(IntSupplier xSupplier)
    {
        return this.setX((overlay) -> xSupplier.getAsInt());
    }

    /**
     * Set the y-coordinate of this overlay.
     *
     * @param y The overlay y-coordinate.
     * @see #setY(ToIntFunction)
     * @see #setY(IntSupplier)
     */
    @PublicAPI
    public OverlayBuilder setY(int y)
    {
        this.y = y;
        return this;
    }

    /**
     * Set the y-coordinate of this overlay using a function. This will make the overlay not draggable by the user since
     * its y-coordinate will be set using the given function.
     *
     * @param yFunction A {@link ToIntFunction} that accepts the {@link Overlay} and provides a y-coordinate.
     * @see #setY(IntSupplier)
     * @see #setY(int)
     */
    @PublicAPI
    public OverlayBuilder setY(ToIntFunction<Overlay> yFunction)
    {
        this.yFunction = yFunction;
        this.canDrag = false;

        return this;
    }

    /**
     * Set the y-coordinate of this overlay using a supplier. This will make the overlay not draggable by the user since
     * its y-coordinate will be set using the given supplier.
     *
     * @param ySupplier A {@link IntSupplier} that provides an integer.
     * @see #setY(ToIntFunction)
     * @see #setY(int)
     */
    @PublicAPI
    public OverlayBuilder setY(IntSupplier ySupplier)
    {
        return this.setY((overlay) -> ySupplier.getAsInt());
    }

    /**
     * Set the width/height size of this overlay.
     *
     * @param width  The overlay width.
     * @param height The overlay height.
     */
    @PublicAPI
    public OverlayBuilder size(int width, int height)
    {
        this.width = width;
        this.height = height;

        return this;
    }

    /**
     * Set the square size of this overlay.
     *
     * @param size The size of both the width/height for this overlay.
     */
    @PublicAPI
    public OverlayBuilder size(int size)
    {
        this.width = size;
        this.height = size;

        return this;
    }

    /**
     * Set the minimum width and minimum height that this overlay must be. If the width and/or height is smaller than
     * the given arguments, then the overlay will be resized to meet the minimum requirements. This will only be
     * enforced if the overlay is resized using percentages.
     *
     * @param minWidth  The minimum width.
     * @param minHeight The minimum height.
     */
    @PublicAPI
    public OverlayBuilder minSize(int minWidth, int minHeight)
    {
        this.minWidth = minWidth;
        this.minHeight = minHeight;

        return this;
    }

    /**
     * Set the minimum size that this overlay must be. If the size of the overlay is smaller than the given arguments,
     * then the overlay will be resized. This will only be enforced if the overlay is resized using percentages.
     *
     * @param minSize The minimum size the overlay must at least meet.
     */
    @PublicAPI
    public OverlayBuilder minSize(int minSize)
    {
        this.minWidth = minSize;
        this.minHeight = minSize;

        return this;
    }

    /**
     * Set the width of this overlay.
     *
     * @param width The overlay width.
     */
    @PublicAPI
    public OverlayBuilder setWidth(int width)
    {
        this.width = width;
        return this;
    }

    /**
     * Change the minimum width that this overlay must be. If the size of the overlay is smaller than the given
     * arguments, then the overlay will be resized. This will only be enforced if the overlay is resized using
     * percentages.
     *
     * @param minWidth The minimum width the overlay must at least meet.
     */
    @PublicAPI
    public OverlayBuilder minWidth(int minWidth)
    {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * Set the height of this overlay.
     *
     * @param height The overlay height.
     */
    @PublicAPI
    public OverlayBuilder setHeight(int height)
    {
        this.height = height;
        return this;
    }

    /**
     * Change the minimum height that this overlay must be. If the size of the overlay is smaller than the given
     * arguments, then the overlay will be resized. This will only be enforced if the overlay is resized using
     * percentages.
     *
     * @param minHeight The minimum height the overlay must at least meet.
     */
    @PublicAPI
    public OverlayBuilder minHeight(int minHeight)
    {
        this.minHeight = minHeight;
        return this;
    }

    /**
     * Perform instructions after the overlay window is resized.
     *
     * @param consumer A {@link Consumer} that accepts an {@link Overlay}.
     */
    @PublicAPI
    public OverlayBuilder onResize(Consumer<Overlay> consumer)
    {
        this.onResize = consumer;
        return this;
    }

    /**
     * Perform instructions after the overlay window is resized.
     *
     * @param runnable A {@link Runnable}.
     */
    @PublicAPI
    public OverlayBuilder onResize(Runnable runnable)
    {
        this.onResize = overlay -> runnable.run();
        return this;
    }

    /**
     * Set the size of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage A {@link DoubleSupplier} that provides a normalized percentage size relative to the current
     *                   screen.
     * @param maximum    The maximum size to clamp to when resizing.
     */
    @PublicAPI
    public OverlayBuilder resizeUsingPercentage(DoubleSupplier percentage, int maximum)
    {
        this.resizeUsingPercentage = true;

        this.resizeWidthPercentage = () -> Mth.clamp(percentage.getAsDouble(), 0.1D, 1.0D);
        this.resizeHeightPercentage = () -> Mth.clamp(percentage.getAsDouble(), 0.1D, 1.0D);

        this.resizeWidthMaximum = maximum;
        this.resizeHeightMaximum = maximum;

        return this;
    }

    /**
     * Set the size of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage The normalized percentage size relative to the current screen.
     * @param maximum    The maximum size to clamp to when resizing.
     */
    @PublicAPI
    public OverlayBuilder resizeUsingPercentage(double percentage, int maximum)
    {
        return this.resizeUsingPercentage(() -> percentage, maximum);
    }

    /**
     * Set the size of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage A {@link DoubleSupplier} that provides a normalized percentage size relative to the current
     *                   screen.
     */
    @PublicAPI
    public OverlayBuilder resizeUsingPercentage(DoubleSupplier percentage)
    {
        return this.resizeUsingPercentage(percentage, 0);
    }

    /**
     * Set the size of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage The normalized percentage size relative to the current screen.
     */
    @PublicAPI
    public OverlayBuilder resizeUsingPercentage(double percentage)
    {
        return this.resizeUsingPercentage(percentage, 0);
    }

    /**
     * Set the width of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage A {@link DoubleSupplier} that provides a normalized percentage size relative to the current
     *                   screen.
     * @param maximum    The maximum size to clamp to when resizing.
     */
    @PublicAPI
    public OverlayBuilder resizeWidthUsingPercentage(DoubleSupplier percentage, int maximum)
    {
        this.resizeUsingPercentage = true;
        this.resizeWidthPercentage = () -> Mth.clamp(percentage.getAsDouble(), 0.0D, 1.0D);
        this.resizeWidthMaximum = maximum;

        return this;
    }

    /**
     * Set the width of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage The normalized percentage size relative to the current screen.
     * @param maximum    The maximum size to clamp to when resizing.
     */
    @PublicAPI
    public OverlayBuilder resizeWidthUsingPercentage(double percentage, int maximum)
    {
        return this.resizeWidthUsingPercentage(() -> percentage, maximum);
    }

    /**
     * Set the width of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage The normalized percentage size relative to the current screen.
     */
    @PublicAPI
    public OverlayBuilder resizeWidthUsingPercentage(double percentage)
    {
        return this.resizeWidthUsingPercentage(percentage, 0);
    }

    /**
     * Set the height of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage A {@link DoubleSupplier} that provides a normalized percentage size relative to the current
     *                   screen.
     * @param maximum    The maximum size to clamp to when resizing.
     */
    @PublicAPI
    public OverlayBuilder resizeHeightUsingPercentage(DoubleSupplier percentage, int maximum)
    {
        this.resizeUsingPercentage = true;
        this.resizeHeightPercentage = () -> Mth.clamp(percentage.getAsDouble(), 0.0D, 1.0D);
        this.resizeHeightMaximum = maximum;

        return this;
    }

    /**
     * Set the height of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage The normalized percentage size relative to the current screen.
     * @param maximum    The maximum size to clamp to when resizing.
     */
    @PublicAPI
    public OverlayBuilder resizeHeightUsingPercentage(double percentage, int maximum)
    {
        return this.resizeHeightUsingPercentage(() -> percentage, maximum);
    }

    /**
     * Set the height of the overlay by using a percentage of the current screen size. For example, a percentage of 0.8D
     * will give a size that is 20% smaller than the parent screen.
     *
     * @param percentage The normalized percentage size relative to the current screen.
     */
    @PublicAPI
    public OverlayBuilder resizeHeightUsingPercentage(double percentage)
    {
        return this.resizeHeightUsingPercentage(percentage, 0);
    }

    /**
     * Set the padding that will be added between the inner overlay window and widgets. This padding will also be
     * applied to the gap between widgets and scrollbars.
     *
     * @param padding A padding to apply around the inner overlay window.
     */
    @PublicAPI
    public OverlayBuilder padding(int padding)
    {
        this.padding = padding;
        return this;
    }

    /**
     * Set the padding that will be added to widget scissoring.
     *
     * @param padding The padding amount.
     */
    @PublicAPI
    public OverlayBuilder scissorPadding(int padding)
    {
        this.scissorPadding = padding;
        return this;
    }

    /**
     * Set the width and height of this overlay so that all widgets are seen in the inner overlay window. The resize
     * will ensure the overlay does not exceed the game window's width and height.
     */
    @PublicAPI
    public OverlayBuilder resizeForWidgets()
    {
        this.resizeForWidgets = true;
        return this;
    }

    /**
     * Resize the overlay width so that it fits overlay content, if and only if, that content's ending x-position is
     * smaller than the current overlay width. If the widget content exceeds the overlay's width, then no resizing will
     * occur, and a horizontal scrollbar will become visible.
     */
    @PublicAPI
    public OverlayBuilder resizeWidthForWidgets()
    {
        this.resizeWidthForWidgets = true;
        return this;
    }

    /**
     * Resize the overlay height so that it fits overlay content, if and only if, that content's ending y-position is
     * smaller than the current overlay height. If the widget content exceeds the overlay's height, then no resizing
     * will occur, and a vertical scrollbar will become visible.
     */
    @PublicAPI
    public OverlayBuilder resizeHeightForWidgets()
    {
        this.resizeHeightForWidgets = true;
        return this;
    }

    /**
     * Set the {@code canDrag} flag for this overlay to {@code false}. This will make it so the user cannot drag the
     * overlay.
     */
    @PublicAPI
    public OverlayBuilder unmovable()
    {
        this.canDrag = false;
        return this;
    }

    /**
     * Set the {@code hasBorder} flag for this overlay to {@code false}. This will make it so that the overlay does not
     * have a textured border. The internal widgets for this overlay, such as the close button and the extra info
     * button, will not exist when an overlay is borderless. The overlay will be closed when the user clicks outside the
     * overlay's border.
     */
    @PublicAPI
    public OverlayBuilder borderless()
    {
        this.hasBorder = false;
        return this;
    }

    /**
     * Set the {@code hasShadow} flag for this overlay to {@code false}. This will make it so that there is no
     * background shadow that covers the parent screen.
     */
    @PublicAPI
    public OverlayBuilder shadowless()
    {
        this.hasShadow = false;
        return this;
    }

    /**
     * Keep this overlay above the given element using the given margin amount.
     *
     * @param element The {@link LayoutElement} to stay above or below.
     * @param margin  The margin between the overlay and the element.
     */
    @PublicAPI
    public OverlayBuilder above(LayoutElement element, int margin)
    {
        this.onlyAbove = true;

        return this.aboveOrBelow(element, margin);
    }

    /**
     * Keep this overlay above or below the given element using the given margin amount. If there is not enough screen
     * space above the given element, then the overlay will be repositioned to be below the given widget and vice
     * versa.
     *
     * @param element The {@link LayoutElement} to stay above or below.
     * @param margin  The margin between the overlay and the element.
     */
    @PublicAPI
    public OverlayBuilder aboveOrBelow(LayoutElement element, int margin)
    {
        this.aboveOrBelow = element;
        this.aboveOrBelowMargin = margin;

        return this;
    }

    /**
     * Keep this overlay below the given element using the given margin amount.
     *
     * @param element The {@link LayoutElement} to stay above or below.
     * @param margin  The margin between the overlay and the element.
     */
    @PublicAPI
    public OverlayBuilder below(LayoutElement element, int margin)
    {
        this.onlyBelow = true;

        return this.aboveOrBelow(element, margin);
    }

    /**
     * Define a gradient background color to render instead of the default semi-transparent black color.
     *
     * @param gradient A {@link Gradient} instance.
     */
    @PublicAPI
    public OverlayBuilder gradientBackground(Gradient gradient)
    {
        this.backgroundGradient = gradient;
        return this;
    }

    /**
     * Define pre-rendering (first render pass) instructions for the overlay.
     *
     * @param renderer A {@link Runnable}.
     */
    @PublicAPI
    public OverlayBuilder preRenderer(OverlayRenderer renderer)
    {
        this.firstRenderer = renderer;
        return this;
    }

    /**
     * Define post-rendering (last render pass) instructions for the overlay.
     *
     * @param renderer A {@link Runnable}.
     */
    @PublicAPI
    public OverlayBuilder postRenderer(OverlayRenderer renderer)
    {
        this.lastRenderer = renderer;
        return this;
    }

    /**
     * Set the outline color for a borderless ({@link #borderless()}) overlay. The default color is transparent and is
     * not rendered.
     *
     * @param color A {@link Color} instance to use as the outline color.
     */
    @PublicAPI
    public OverlayBuilder outlineColor(Color color)
    {
        this.outlineColor = color;
        return this;
    }

    /**
     * Set the inside window background color of this overlay.
     *
     * @param color A {@link Color} instance to use as a background color.
     */
    @PublicAPI
    public OverlayBuilder backgroundColor(Color color)
    {
        this.backgroundColor = color;
        return this;
    }

    /**
     * Set the shadow color that overlays the parent screen.
     *
     * @param color A {@link Color} instance to use as a shadow color.
     */
    @PublicAPI
    public OverlayBuilder shadowColor(Color color)
    {
        this.shadowColor = color;
        return this;
    }

    /**
     * Set an icon for this overlay.
     *
     * @param icon An icon instance.
     */
    @PublicAPI
    public OverlayBuilder icon(TextureIcon icon)
    {
        this.icon = icon;
        return this;
    }

    /**
     * Set a block icon for this overlay.
     *
     * @param block A block instance.
     */
    @PublicAPI
    public OverlayBuilder icon(Block block)
    {
        this.icon = TextureIcon.fromBlock(block);
        return this;
    }

    /**
     * Set an item icon for this overlay.
     *
     * @param item An item instance.
     */
    @PublicAPI
    public OverlayBuilder icon(Item item)
    {
        this.icon = TextureIcon.fromItem(item);
        return this;
    }

    /**
     * Give instructions to perform when the overlay is closed.
     *
     * @param runnable A runnable function.
     */
    @PublicAPI
    public OverlayBuilder onClose(Runnable runnable)
    {
        this.onClose = runnable;
        return this;
    }

    /**
     * Set an informative message for this overlay.
     *
     * @param tooltip A component.
     */
    @PublicAPI
    public OverlayBuilder infoMessage(Component tooltip)
    {
        this.infoMessage = tooltip;
        return this;
    }

    /**
     * Set an informative message for this overlay.
     *
     * @param langKey A {@link Translation} instance to get a translation component from.
     */
    @PublicAPI
    public OverlayBuilder infoMessage(Translation langKey)
    {
        return this.infoMessage(langKey.get());
    }

    /**
     * Add instructions to test when keyboard input is received by the built overlay.
     *
     * @param keyPressed A {@link OverlayKeyboard} instance.
     */
    @PublicAPI
    public OverlayBuilder runOnKeyPressed(OverlayKeyboard keyPressed)
    {
        this.onKeyPressInstructions.add(keyPressed);
        return this;
    }

    /**
     * Add instructions to perform each tick.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Overlay}.
     */
    @PublicAPI
    public OverlayBuilder runOnTick(Consumer<Overlay> consumer)
    {
        this.onTickInstructions.add(consumer);
        return this;
    }

    /**
     * Add instructions to perform each tick.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    public OverlayBuilder runOnTick(Runnable runnable)
    {
        return this.runOnTick(overlay -> runnable.run());
    }

    /**
     * Add instructions to run before the overlay's {@link #onClose(Runnable)} instructions are run.
     *
     * @param consumer A {@link Consumer} that accepts the built {@link Overlay}.
     */
    @PublicAPI
    public OverlayBuilder runOnClose(Consumer<Overlay> consumer)
    {
        this.closingInstructions.add(consumer);
        return this;
    }

    /**
     * Add instructions to run before the overlay's {@link #onClose(Runnable)} instructions are run.
     *
     * @param runnable A {@link Runnable} to run.
     */
    @PublicAPI
    public OverlayBuilder runOnClose(Runnable runnable)
    {
        return this.runOnClose((overlay) -> runnable.run());
    }

    /**
     * Finish the builder process and create a new overlay instance.
     */
    public Overlay build()
    {
        Overlay overlay = new Overlay(this);

        if (this.xFunction != null)
            this.x = this.xFunction.applyAsInt(overlay);

        if (this.yFunction != null)
            this.y = this.yFunction.applyAsInt(overlay);

        if (this.x < 0)
            overlay.setX(MathUtil.center(this.width, GuiUtil.getScreenWidth()));

        if (this.y < 0)
            overlay.setY(MathUtil.center(this.height, GuiUtil.getScreenHeight()));

        return overlay;
    }
}
