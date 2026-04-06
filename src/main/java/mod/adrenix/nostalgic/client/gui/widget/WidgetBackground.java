package mod.adrenix.nostalgic.client.gui.widget;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.GameSprite;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Create a new {@link WidgetBackground} instance using the given widget background sprites.
 *
 * @param background  A {@link Identifier} for the widget's background.
 * @param disabled    A {@link Identifier} for the widget's disabled background.
 * @param highlighted A {@link Identifier} for the widget's highlighted background.
 */
public record WidgetBackground(Identifier background, Identifier highlighted, Identifier disabled) {
    /* Static */

    public static final WidgetBackground BUTTON = new WidgetBackground(GameSprite.BUTTON, GameSprite.BUTTON_HIGHLIGHTED, GameSprite.BUTTON_DISABLED);
    public static final WidgetBackground SLIDER = new WidgetBackground(GameSprite.SLIDER, GameSprite.SLIDER, GameSprite.SLIDER);

    /* Methods */

    /**
     * Get the proper widget sprite.
     *
     * @param isActive           Whether the widget is active.
     * @param isHoveredOrFocused Whether the widget is hovered or focused.
     * @return A sprite {@link Identifier} instance.
     */
    public Identifier get(boolean isActive, boolean isHoveredOrFocused) {
        if (isHoveredOrFocused && isActive)
            return this.highlighted;

        if (!isActive)
            return this.disabled;

        return this.background;
    }

    /**
     * Get the proper widget sprite.
     *
     * @param widget A {@link DynamicWidget} instance.
     * @return A sprite {@link Identifier} instance.
     */
    public Identifier get(DynamicWidget<?, ?> widget) {
        return this.get(widget.isActive(), widget.isHoveredOrFocused());
    }

    /**
     * Get the proper widget sprite.
     *
     * @param widget A {@link AbstractWidget} instance.
     * @return A sprite {@link Identifier} instance.
     */
    public Identifier get(AbstractWidget widget) {
        return this.get(widget.isActive(), widget.isHoveredOrFocused());
    }

    /**
     * Render a widget sprite background based on the given context.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     * @param sprite   The sprite {@link Identifier} instance.
     * @param x        The starting x-coordinate.
     * @param y        The starting y-coordinate.
     * @param width    The width of the render.
     * @param height   The height of the render.
     */
    @PublicAPI
    public void extractRenderState(GuiGraphicsExtractor graphics, Identifier sprite, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
    }

    /**
     * Render a widget sprite background based on the given widget's state context.
     *
     * @param widget   A {@link DynamicWidget} instance.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    @PublicAPI
    public void render(DynamicWidget<?, ?> widget, GuiGraphicsExtractor graphics) {
        this.extractRenderState(graphics, this.get(widget), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }

    /**
     * Render a widget sprite background based on the given widget's state context.
     *
     * @param widget   A {@link AbstractWidget} instance.
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     */
    @PublicAPI
    public void render(AbstractWidget widget, GuiGraphicsExtractor graphics) {
        this.extractRenderState(graphics, this.get(widget), widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
    }
}
