package mod.adrenix.nostalgic.util.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public abstract class DrawText
{
    /* Builders */

    /**
     * Build a text drawer that will leverage the render utility's batching if it is available.
     *
     * @param graphics    The {@link GuiGraphics} instance.
     * @param translation The {@link Translation} that will be converted into a {@link Component}.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphics graphics, Translation translation)
    {
        return new Builder(graphics, translation.get());
    }

    /**
     * Build a text drawer that will leverage the render utility's batching if it is available.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param string   The string to draw.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphics graphics, @Nullable String string)
    {
        return new Builder(graphics, string);
    }

    /**
     * Build a text drawer that will leverage the render utility's batching if it is available.
     *
     * @param graphics  The {@link GuiGraphics} instance.
     * @param component The {@link Component} instance to draw.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphics graphics, @Nullable Component component)
    {
        return new Builder(graphics, component);
    }

    /**
     * Build a text drawer that will leverage the render utility's batching if it is available.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param sequence The {@link FormattedCharSequence} instance to draw.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphics graphics, @Nullable FormattedCharSequence sequence)
    {
        return new Builder(graphics, sequence);
    }

    /* Helpers */

    /**
     * Center a text component within the given bounds.
     *
     * @param startX The starting x-coordinate.
     * @param width  The width of the box that the text is being centered within.
     * @param text   The {@link Component} to get text width from.
     * @return A centered x-coordinate.
     */
    @PublicAPI
    public static float centerX(int startX, int width, Component text)
    {
        return MathUtil.center(startX, width, GuiUtil.font().width(text));
    }

    /**
     * Center a string of text within the given bounds.
     *
     * @param startX The starting x-coordinate.
     * @param width  The width of the box that the text is being centered within.
     * @param text   The {@link String} to get the font text width of.
     * @return A centered x-coordinate.
     */
    @PublicAPI
    public static float centerX(int startX, int width, String text)
    {
        return MathUtil.center(startX, width, GuiUtil.font().width(text));
    }

    /**
     * Get a centered y-coordinate for a line of text based on the given bounds.
     *
     * @param startY The starting y-coordinate.
     * @param height The height of the box that the line of text is being centered within.
     * @return A centered y-coordinate.
     */
    @PublicAPI
    public static float centerY(int startY, int height)
    {
        return MathUtil.center(startY, height, GuiUtil.textHeight());
    }

    /* Builder */

    public static class Builder
    {
        private final GuiGraphics graphics;
        private float x = 0.0F;
        private float y = 0.0F;
        private int color = 0xFFFFFF;
        private boolean useIntegerX = false;
        private boolean useIntegerY = false;
        private boolean centerText = false;
        private boolean dropShadow = true;
        @Nullable private String string = null;
        @Nullable private Component component = null;
        @Nullable private FormattedCharSequence sequence = null;

        private Builder(GuiGraphics graphics, @Nullable String string)
        {
            this.graphics = graphics;
            this.string = string;
        }

        private Builder(GuiGraphics graphics, @Nullable Component component)
        {
            this.graphics = graphics;
            this.component = component;
        }

        private Builder(GuiGraphics graphics, @Nullable FormattedCharSequence sequence)
        {
            this.graphics = graphics;
            this.sequence = sequence;
        }

        /**
         * Specify the float x-coordinate to draw this text at.
         *
         * @param x The x-coordinate.
         */
        @PublicAPI
        public Builder posX(float x)
        {
            this.x = x;

            return this;
        }

        /**
         * Specify the integer x-coordinate to draw this text at.
         *
         * @param x The x-coordinate.
         */
        @PublicAPI
        public Builder posX(int x)
        {
            this.useIntegerX = true;
            this.x = x;

            return this;
        }

        /**
         * Specify the float y-coordinate to draw this text at.
         *
         * @param y The y-coordinate.
         */
        @PublicAPI
        public Builder posY(float y)
        {
            this.y = y;

            return this;
        }

        /**
         * Specify the integer y-coordinate to draw this text at.
         *
         * @param y The y-coordinate.
         */
        @PublicAPI
        public Builder posY(int y)
        {
            this.useIntegerY = true;
            this.y = y;

            return this;
        }

        /**
         * Specify the floating coordinates to draw this text at.
         *
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         */
        @PublicAPI
        public Builder pos(float x, float y)
        {
            this.posX(x);
            this.posY(y);

            return this;
        }

        /**
         * Specify the integer coordinates to draw this text at.
         *
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         */
        @PublicAPI
        public Builder pos(int x, int y)
        {
            this.posX(x);
            this.posY(y);

            return this;
        }

        /**
         * Specify the color to draw with.
         *
         * @param color A {@link Color} instance.
         */
        @PublicAPI
        public Builder color(Color color)
        {
            this.color = color.get();

            return this;
        }

        /**
         * Specify the color to draw with.
         *
         * @param argb An RGB integer.
         */
        @PublicAPI
        public Builder color(int argb)
        {
            this.color = argb;

            return this;
        }

        /**
         * Set the {@code dropShadow} flag.
         *
         * @param dropShadow The new flag state.
         */
        @PublicAPI
        public Builder setShadow(boolean dropShadow)
        {
            this.dropShadow = dropShadow;

            return this;
        }

        /**
         * Set the {@code dropShadow} flag to {@code false}.
         */
        @PublicAPI
        public Builder flat()
        {
            return this.setShadow(false);
        }

        /**
         * This will center the drawn text by subtracting the text's font width from the current x-coordinate.
         */
        @PublicAPI
        public Builder center()
        {
            this.centerText = true;

            return this;
        }

        /**
         * Draws the text to the screen using the given builder properties if the render utility's batch rendering is
         * disabled. Otherwise, the text is batched using the given builder properties.
         *
         * @return The given x-coordinate plus the font width of the text.
         */
        @PublicAPI
        public int draw()
        {
            if (this.centerText)
            {
                if (this.string != null)
                    this.x -= GuiUtil.font().width(this.string) / 2.0F;
                else if (this.component != null)
                    this.x -= GuiUtil.font().width(this.component) / 2.0F;
                else if (this.sequence != null)
                    this.x -= GuiUtil.font().width(this.sequence) / 2.0F;
            }

            if (this.useIntegerX)
                this.x = (int) this.x;

            if (this.useIntegerY)
                this.y = (int) this.y;

            this.graphics.pose().pushPose();
            this.graphics.pose().translate(this.x, this.y, 0.03F);

            Font.DisplayMode mode = Font.DisplayMode.NORMAL;
            Matrix4f matrix = this.graphics.pose().last().pose();
            MultiBufferSource.BufferSource buffer = RenderUtil.fontBuffer();
            boolean isBidirectional = GuiUtil.font().isBidirectional();
            int textWidth = 0;

            if (this.string != null)
            {
                textWidth = GuiUtil.font()
                    .drawInBatch(this.string, 0, 0, this.color, this.dropShadow, matrix, buffer, mode, 0, 0xF000F0, isBidirectional);
            }
            else if (this.component != null)
            {
                textWidth = GuiUtil.font()
                    .drawInBatch(this.component, 0, 0, this.color, this.dropShadow, matrix, buffer, mode, 0, 0xF000F0);
            }
            else if (this.sequence != null)
            {
                textWidth = GuiUtil.font()
                    .drawInBatch(this.sequence, 0, 0, this.color, this.dropShadow, matrix, buffer, mode, 0, 0xF000F0);
            }

            this.graphics.pose().popPose();

            if (!RenderUtil.isBatching())
            {
                RenderSystem.enableDepthTest();
                buffer.endBatch();
                RenderSystem.disableDepthTest();
            }

            return Math.round(this.x) + textWidth;
        }
    }
}
