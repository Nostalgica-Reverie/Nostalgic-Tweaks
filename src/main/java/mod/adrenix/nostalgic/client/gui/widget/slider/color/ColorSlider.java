package mod.adrenix.nostalgic.client.gui.widget.slider.color;

import mod.adrenix.nostalgic.client.gui.widget.slider.AbstractSlider;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.ColorElement;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ColorSlider extends AbstractSlider<ColorSliderBuilder, ColorSlider> {
    /* Builders */

    protected final Color color;

    /* Fields */
    protected final ColorElement element;

    protected ColorSlider(ColorSliderBuilder builder) {
        super(builder);

        this.color = builder.color;
        this.element = builder.element;

        this.builder.handleWidth(3);

        builder.backgroundRenderer(this::renderBackground);
        builder.handleRenderer(this::renderHandle);
    }

    /* Constructor */

    /**
     * Create a new {@link ColorSlider} instance.
     *
     * @param color   A {@link Color} instance.
     * @param element A {@link ColorElement} enumeration.
     * @return A new {@link ColorSliderBuilder} instance.
     */
    public static ColorSliderBuilder create(Color color, ColorElement element) {
        return new ColorSliderBuilder(color, element);
    }

    /* Methods */

    /**
     * @return The {@link ColorElement} enumeration of this color slider.
     */
    @PublicAPI
    public ColorElement getElement() {
        return this.element;
    }

    /**
     * Custom slider handle renderer.
     *
     * @param slider      The {@link ColorSlider} instance.
     * @param graphics    The {@link GuiGraphicsExtractor} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderHandle(ColorSlider slider, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int outline = this.isFocused() ? Color.LIGHT_BLUE.get() : 0xFF555555;
        int handle = this.isHoveredOrFocused() ? 0xFFCCCCCC : 0xFFAAAAAA;

        graphics.outline(this.x, this.y, this.width, this.height, this.isActive() ? outline : 0xFF333333);
        graphics.outline(this.getHandleX(), this.y, this.getHandleWidth(), this.height, this.isActive() ? handle : 0xFF666666);
    }

    /**
     * Custom background renderer.
     *
     * @param slider      The {@link ColorSlider} instance.
     * @param graphics    The {@link GuiGraphicsExtractor} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderBackground(ColorSlider slider, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(this.x + 1.0F, this.y + 1.0F);

        int innerW = this.width - 2;
        int innerH = this.height - 2;
        int outerW = this.width - 1;

        Color sbColor = switch (this.element) {
            case SATURATION -> new Color(this.color.getHueAsRGB());
            case BRIGHTNESS -> new Color(Color.HSBtoRGB(this.color.getHue(), this.color.getSaturation(), 1.0F));
            default -> Color.WHITE;
        };

        switch (this.element) {
            //TODO: Horizontal gradient support.
            //  To add these we'd need to probably mixing into GuiGraphicsExtractor so we
            //  could either access the GUI state, or do interface injection to add those and RenderUtil#outline
            //  extensions.
            case HUE -> {
                int diff = (int) (innerW / 6.0F);
                int last = (diff * 6) + Math.abs(innerW - (diff * 6));

                graphics.fillGradient(0, 0, diff, innerH, Color.RED.get(), Color.YELLOW.get());
                graphics.fillGradient(diff, 0, diff * 2, innerH, Color.YELLOW.get(), Color.GREEN.get());
                graphics.fillGradient(diff * 2, 0, diff * 3, innerH, Color.GREEN.get(), Color.CYAN.get());
                graphics.fillGradient(diff * 3, 0, diff * 4, innerH, Color.CYAN.get(), Color.BLUE.get());
                graphics.fillGradient(diff * 4, 0, diff * 5, innerH, Color.BLUE.get(), Color.PINK.get());
                graphics.fillGradient(diff * 5, 0, last, innerH, Color.PINK.get(), Color.RED.get());
            }
            case SATURATION -> graphics.fillGradient(0, 0, innerW, innerH, Color.WHITE.get(), sbColor.get());
            case BRIGHTNESS -> graphics.fillGradient(0, 0, innerW, innerH, Color.BLACK.get(), sbColor.get());
            case RED -> {
                Color from = new Color(0, this.color.getGreen(), this.color.getBlue());
                Color to = new Color(255, this.color.getGreen(), this.color.getBlue());

                graphics.fillGradient(0, 0, innerW, innerH, from.get(), to.get());
            }
            case GREEN -> {
                Color from = new Color(this.color.getRed(), 0, this.color.getBlue());
                Color to = new Color(this.color.getRed(), 255, this.color.getBlue());

                graphics.fillGradient(0, 0, innerW, innerH, from.get(), to.get());
            }
            case BLUE -> {
                Color from = new Color(this.color.getRed(), this.color.getGreen(), 0);
                Color to = new Color(this.color.getRed(), this.color.getGreen(), 255);

                graphics.fillGradient(0, 0, innerW, innerH, from.get(), to.get());
            }
            case ALPHA -> {
                int size = 3;

                for (int row = 1; row <= 6; row++) {
                    Color primary = MathUtil.isOdd(row) ? Color.GRAY : Color.WHITE;
                    Color secondary = MathUtil.isOdd(row) ? Color.WHITE : Color.GRAY;

                    for (int i = 0; i < (int) (outerW / (float) size) * size; i += size)
                        graphics.fill(i, (row - 1) * size, i + size, row * size, MathUtil.isEven(i) ? primary.get() : secondary.get());
                }

                graphics.fillGradient(0, 0, innerW, innerH, this.color.fromAlpha(0.0D).get(), this.color.fromAlpha(1.0D).get());
            }
        }

        if (this.isInactive())
            graphics.fill(0, 0, this.width, this.height, 0xA5000000);

        graphics.pose().popMatrix();
    }
}
