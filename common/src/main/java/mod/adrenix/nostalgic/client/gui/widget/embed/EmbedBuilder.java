package mod.adrenix.nostalgic.client.gui.widget.embed;

import mod.adrenix.nostalgic.client.gui.PaddingManager;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.VisibleBuilder;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import org.jetbrains.annotations.Nullable;

public class EmbedBuilder extends DynamicBuilder<EmbedBuilder, Embed>
    implements LayoutBuilder<EmbedBuilder, Embed>, VisibleBuilder<EmbedBuilder, Embed>, PaddingManager<EmbedBuilder>
{
    /* Fields */

    protected int paddingTop = 0;
    protected int paddingBottom = 0;
    protected int paddingLeft = 0;
    protected int paddingRight = 0;
    protected int scissorPadding = 0;
    protected int scrollbarSize = 4;
    protected int borderThickness = 1;
    protected boolean resizeForWidgets = false;
    protected boolean resizeWidthForWidgets = false;
    protected boolean resizeHeightForWidgets = false;

    protected Color borderColor = Color.TRANSPARENT;
    protected Color backgroundColor = Color.TRANSPARENT;

    @Nullable protected Gradient backgroundGradient = null;
    @Nullable protected EmbedRenderer preRenderer = null;
    @Nullable protected EmbedRenderer postRenderer = null;
    @Nullable protected EmbedRenderer borderRenderer = null;

    @Nullable protected EmbedEvent.MouseClicked mouseClicked = null;
    @Nullable protected EmbedEvent.MouseDragged mouseDragged = null;
    @Nullable protected EmbedEvent.MouseReleased mouseReleased = null;
    @Nullable protected EmbedEvent.MouseScrolled mouseScrolled = null;
    @Nullable protected EmbedEvent.KeyPressed keyPressed = null;
    @Nullable protected EmbedEvent.KeyReleased keyReleased = null;
    @Nullable protected EmbedEvent.CharTyped charTyped = null;

    @Nullable protected Runnable onTick = null;

    /* Constructor */

    protected EmbedBuilder()
    {
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public EmbedBuilder self()
    {
        return this;
    }

    /**
     * Set the width and height of this embed so that all widgets are seen in the inner window.
     */
    @PublicAPI
    public EmbedBuilder resizeForWidgets()
    {
        this.resizeForWidgets = true;

        return this;
    }

    /**
     * Resize the embed width so that it fits embed content.
     */
    @PublicAPI
    public EmbedBuilder resizeWidthForWidgets()
    {
        this.resizeWidthForWidgets = true;

        return this;
    }

    /**
     * Resize the embed height so that it fits embed content.
     */
    @PublicAPI
    public EmbedBuilder resizeHeightForWidgets()
    {
        this.resizeHeightForWidgets = true;

        return this;
    }

    /**
     * Change the size of the scrollbar widgets.
     *
     * @param size The max size for the scrollbars.
     */
    @PublicAPI
    public EmbedBuilder scrollbarSize(int size)
    {
        this.scrollbarSize = size;

        return this;
    }

    /**
     * Set the inside embed background color. Set the color as transparent to hide. The default color is transparent.
     *
     * @param color A {@link Color} instance to use as a background color.
     */
    @PublicAPI
    public EmbedBuilder backgroundColor(Color color)
    {
        this.backgroundColor = color;

        return this;
    }

    /**
     * Define a gradient background color to render.
     *
     * @param gradient A {@link Gradient} instance.
     */
    @PublicAPI
    public EmbedBuilder gradientBackground(Gradient gradient)
    {
        this.backgroundGradient = gradient;

        return this;
    }

    /**
     * Set the border color for this embed. Set the color as transparent to hide. The default color is transparent.
     *
     * @param color A {@link Color} instance to use as the border color.
     */
    @PublicAPI
    public EmbedBuilder borderColor(Color color)
    {
        this.borderColor = color;

        return this;
    }

    /**
     * Set the border thickness for this embed. This will add onto any previously or later defined padding.
     *
     * @param thickness The thickness of the embed border.
     */
    @PublicAPI
    public EmbedBuilder borderThickness(int thickness)
    {
        this.borderThickness = thickness;

        return this;
    }

    /**
     * Provide a custom renderer for the border. Use {@link #borderThickness(int)} to define the size of the embed
     * border.
     *
     * @param renderer A {@link EmbedRenderer}.
     */
    @PublicAPI
    public EmbedBuilder borderRenderer(EmbedRenderer renderer)
    {
        this.borderRenderer = renderer;

        return this;
    }

    /**
     * Perform custom rendering before the embed's widgets are rendered. All renderings by default will be done within
     * the embed scissor zone.
     *
     * @param renderer A {@link EmbedRenderer}.
     */
    @PublicAPI
    public EmbedBuilder preRenderer(EmbedRenderer renderer)
    {
        this.preRenderer = renderer;

        return this;
    }

    /**
     * Perform custom rendering after the embed's widgets are rendered. All renderings by default will be done within
     * the embed scissor zone.
     *
     * @param renderer A {@link EmbedRenderer}.
     */
    @PublicAPI
    public EmbedBuilder postRenderer(EmbedRenderer renderer)
    {
        this.postRenderer = renderer;

        return this;
    }

    /**
     * Provide instructions for when the mouse is clicked in the embed. If the handler does not yield a truthful value,
     * then default embed handling will be applied.
     *
     * @param handler A {@link EmbedEvent.MouseClicked} handler.
     */
    @PublicAPI
    public EmbedBuilder mouseClicked(EmbedEvent.MouseClicked handler)
    {
        this.mouseClicked = handler;

        return this;
    }

    /**
     * Provide instructions for when the mouse is dragged in the embed. If the handler does not yield a truthful value,
     * then default embed handling will be applied.
     *
     * @param handler A {@link EmbedEvent.MouseDragged} handler.
     */
    @PublicAPI
    public EmbedBuilder mouseDragged(EmbedEvent.MouseDragged handler)
    {
        this.mouseDragged = handler;

        return this;
    }

    /**
     * Provide instructions for when the mouse is released in the embed. If the handler does not yield a truthful value,
     * then default embed handling will be applied.
     *
     * @param handler A {@link EmbedEvent.MouseReleased} handler.
     */
    @PublicAPI
    public EmbedBuilder mouseReleased(EmbedEvent.MouseReleased handler)
    {
        this.mouseReleased = handler;

        return this;
    }

    /**
     * Provide instructions for when the mouse is scrolled in the embed. If the handler does not yield a truthful value,
     * then default embed handling will be applied.
     *
     * @param handler A {@link EmbedEvent.MouseScrolled} handler.
     */
    @PublicAPI
    public EmbedBuilder mouseScrolled(EmbedEvent.MouseScrolled handler)
    {
        this.mouseScrolled = handler;

        return this;
    }

    /**
     * Provide instructions for when a key is pressed. If the handler does not yield a truthful value, then default
     * embed handling will be applied.
     *
     * @param handler A {@link EmbedEvent.KeyPressed} handler.
     */
    @PublicAPI
    public EmbedBuilder keyPressed(EmbedEvent.KeyPressed handler)
    {
        this.keyPressed = handler;

        return this;
    }

    /**
     * Provide instructions for when a key is released. If the handler does not yield a truthful value, then default
     * embed handling will be applied.
     *
     * @param handler A {@link EmbedEvent.KeyReleased} handler.
     */
    @PublicAPI
    public EmbedBuilder keyReleased(EmbedEvent.KeyReleased handler)
    {
        this.keyReleased = handler;

        return this;
    }

    /**
     * Provide instructions for when a char is typed. If the handler does not yield a truthful value, then default embed
     * handling will be applied.
     *
     * @param handler A {@link EmbedEvent.CharTyped} handler.
     */
    @PublicAPI
    public EmbedBuilder charTyped(EmbedEvent.CharTyped handler)
    {
        this.charTyped = handler;

        return this;
    }

    /**
     * Provide instructions this embed widget should perform each game tick.
     *
     * @param runnable A {@link Runnable} to run each tick.
     */
    @PublicAPI
    public EmbedBuilder onTick(Runnable runnable)
    {
        this.onTick = runnable;

        return this;
    }

    /**
     * Set the padding that will be added to widget scissoring. The padding for this embed will be set equal to given
     * padding if it is not large enough.
     *
     * @param padding The padding amount.
     */
    @PublicAPI
    public EmbedBuilder scissorPadding(int padding)
    {
        this.scissorPadding = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the top of this embed.
     *
     * @param padding The padding amount.
     */
    @Override
    public EmbedBuilder paddingTop(int padding)
    {
        this.paddingTop = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the bottom of this embed.
     *
     * @param padding The padding amount.
     */
    @Override
    public EmbedBuilder paddingBottom(int padding)
    {
        this.paddingBottom = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the left side of this embed.
     *
     * @param padding The padding amount.
     */
    @Override
    public EmbedBuilder paddingLeft(int padding)
    {
        this.paddingLeft = padding;

        return this;
    }

    /**
     * Set the padding between the widget and the right side of this embed.
     *
     * @param padding The padding amount.
     */
    @Override
    public EmbedBuilder paddingRight(int padding)
    {
        this.paddingRight = padding;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Embed construct()
    {
        return new Embed(this);
    }
}
