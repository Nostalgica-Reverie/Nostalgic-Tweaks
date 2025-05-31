package mod.adrenix.nostalgic.client.gui.widget.slider.color;

import mod.adrenix.nostalgic.client.gui.widget.slider.AbstractSlider;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.ColorElement;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphics;

public class ColorSlider extends AbstractSlider<ColorSliderBuilder, ColorSlider>
{
    /* Builders */

    /**
     * Create a new {@link ColorSlider} instance.
     *
     * @param color   A {@link Color} instance.
     * @param element A {@link ColorElement} enumeration.
     * @return A new {@link ColorSliderBuilder} instance.
     */
    public static ColorSliderBuilder create(Color color, ColorElement element)
    {
        return new ColorSliderBuilder(color, element);
    }

    /* Fields */

    protected final Color color;
    protected final ColorElement element;

    /* Constructor */

    protected ColorSlider(ColorSliderBuilder builder)
    {
        super(builder);

        this.color = builder.color;
        this.element = builder.element;

        this.builder.handleWidth(3);

        builder.backgroundRenderer(this::renderBackground);
        builder.handleRenderer(this::renderHandle);
    }

    /* Methods */

    /**
     * @return The {@link ColorElement} enumeration of this color slider.
     */
    @PublicAPI
    public ColorElement getElement()
    {
        return this.element;
    }

    /**
     * Custom slider handle renderer.
     *
     * @param slider      The {@link ColorSlider} instance.
     * @param graphics    The {@link GuiGraphics} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderHandle(ColorSlider slider, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int outline = this.isFocused() ? Color.LIGHT_BLUE.get() : 0xFF555555;
        int handle = this.isHoveredOrFocused() ? 0xFFCCCCCC : 0xFFAAAAAA;

        RenderUtil.beginBatching();
        RenderUtil.outline(graphics, this.x, this.y, this.width, this.height, this.isActive() ? outline : 0xFF333333);
        RenderUtil.outline(graphics, this.getHandleX(), this.y, this.getHandleWidth(), this.height, this.isActive() ? handle : 0xFF666666);
        RenderUtil.endBatching();
    }

    /**
     * Custom background renderer.
     *
     * @param slider      The {@link ColorSlider} instance.
     * @param graphics    The {@link GuiGraphics} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderBackground(ColorSlider slider, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderUtil.beginBatching();
        graphics.pose().pushPose();
        graphics.pose().translate(this.x + 1.0D, this.y + 1.0D, 0.0D);

        int innerW = this.width - 2;
        int innerH = this.height - 2;
        int outerW = this.width - 1;

        Color sbColor = switch (this.element)
        {
            case SATURATION -> new Color(this.color.getHueAsRGB());
            case BRIGHTNESS -> new Color(Color.HSBtoRGB(this.color.getHue(), this.color.getSaturation(), 1.0F));
            default -> Color.WHITE;
        };

        switch (this.element)
        {
            case HUE ->
            {
                int diff = (int) (innerW / 6.0F);
                int last = (diff * 6) + Math.abs(innerW - (diff * 6));

                RenderUtil.fromLeftGradient(graphics, 0, 0, diff, innerH, Color.RED, Color.YELLOW);
                RenderUtil.fromLeftGradient(graphics, diff, 0, diff * 2, innerH, Color.YELLOW, Color.GREEN);
                RenderUtil.fromLeftGradient(graphics, diff * 2, 0, diff * 3, innerH, Color.GREEN, Color.CYAN);
                RenderUtil.fromLeftGradient(graphics, diff * 3, 0, diff * 4, innerH, Color.CYAN, Color.BLUE);
                RenderUtil.fromLeftGradient(graphics, diff * 4, 0, diff * 5, innerH, Color.BLUE, Color.PINK);
                RenderUtil.fromLeftGradient(graphics, diff * 5, 0, last, innerH, Color.PINK, Color.RED);
            }
            case SATURATION -> RenderUtil.fromLeftGradient(graphics, 0, 0, innerW, innerH, Color.WHITE, sbColor);
            case BRIGHTNESS -> RenderUtil.fromLeftGradient(graphics, 0, 0, innerW, innerH, Color.BLACK, sbColor);
            case RED ->
            {
                Color from = new Color(0, this.color.getGreen(), this.color.getBlue());
                Color to = new Color(255, this.color.getGreen(), this.color.getBlue());

                RenderUtil.fromLeftGradient(graphics, 0, 0, innerW, innerH, from, to);
            }
            case GREEN ->
            {
                Color from = new Color(this.color.getRed(), 0, this.color.getBlue());
                Color to = new Color(this.color.getRed(), 255, this.color.getBlue());

                RenderUtil.fromLeftGradient(graphics, 0, 0, innerW, innerH, from, to);
            }
            case BLUE ->
            {
                Color from = new Color(this.color.getRed(), this.color.getGreen(), 0);
                Color to = new Color(this.color.getRed(), this.color.getGreen(), 255);

                RenderUtil.fromLeftGradient(graphics, 0, 0, innerW, innerH, from, to);
            }
            case ALPHA ->
            {
                int size = 3;

                for (int row = 1; row <= 6; row++)
                {
                    Color primary = MathUtil.isOdd(row) ? Color.GRAY : Color.WHITE;
                    Color secondary = MathUtil.isOdd(row) ? Color.WHITE : Color.GRAY;

                    for (int i = 0; i < (int) (outerW / (float) size) * size; i += size)
                        RenderUtil.fill(graphics, i, (row - 1) * size, i + size, row * size, MathUtil.isEven(i) ? primary : secondary);
                }

                RenderUtil.fromLeftGradient(graphics, 0, 0, innerW, innerH, this.color.fromAlpha(0.0D), this.color.fromAlpha(1.0D));
            }
        }

        if (this.isInactive())
            RenderUtil.fill(graphics, 0, 0, this.width, this.height, 0xA5000000);

        graphics.pose().popPose();

        RenderUtil.endBatching();
    }
}
