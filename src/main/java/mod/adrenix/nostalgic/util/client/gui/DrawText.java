package mod.adrenix.nostalgic.util.client.gui;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

public abstract class DrawText {
    /* Builders */

    /**
     * Build a text drawer.
     *
     * @param graphics    The {@link GuiGraphicsExtractor} instance.
     * @param translation The {@link Translation} that will be converted into a {@link Component}.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphicsExtractor graphics, Translation translation) {
        return new Builder(graphics, translation.get());
    }

    /**
     * Build a text drawer.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     * @param string   The string to draw.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphicsExtractor graphics, @Nullable String string) {
        return new Builder(graphics, string);
    }

    /**
     * Build a text drawer.
     *
     * @param graphics  The {@link GuiGraphicsExtractor} instance.
     * @param component The {@link Component} instance to draw.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphicsExtractor graphics, @Nullable Component component) {
        return new Builder(graphics, component);
    }

    /**
     * Build a text drawer.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     * @param sequence The {@link FormattedCharSequence} instance to draw.
     * @return A new {@link Builder} instance.
     */
    public static Builder begin(GuiGraphicsExtractor graphics, @Nullable FormattedCharSequence sequence) {
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
    public static float centerX(int startX, int width, Component text) {
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
    public static float centerX(int startX, int width, String text) {
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
    public static float centerY(int startY, int height) {
        return MathUtil.center(startY, height, GuiUtil.textHeight());
    }

    /* Builder */

    public static class Builder {
        private final GuiGraphicsExtractor graphics;
        private int x = 0;
        private int y = 0;
        private int color = 0xFFFFFFFF;
        private boolean centerText = false;
        private boolean dropShadow = true;
        @Nullable
        private String string = null;
        @Nullable
        private Component component = null;
        @Nullable
        private FormattedCharSequence sequence = null;

        private Builder(GuiGraphicsExtractor graphics, @Nullable String string) {
            this.graphics = graphics;
            this.string = string;
        }

        private Builder(GuiGraphicsExtractor graphics, @Nullable Component component) {
            this.graphics = graphics;
            this.component = component;
        }

        private Builder(GuiGraphicsExtractor graphics, @Nullable FormattedCharSequence sequence) {
            this.graphics = graphics;
            this.sequence = sequence;
        }

        /**
         * Specify the x-coordinate to draw this text at.
         *
         * @param x The x-coordinate.
         */
        @PublicAPI
        public Builder posX(int x) {
            this.x = x;

            return this;
        }

        /**
         * Specify the y-coordinate to draw this text at.
         *
         * @param y The y-coordinate.
         */
        @PublicAPI
        public Builder posY(int y) {
            this.y = y;

            return this;
        }

        /**
         * Specify the coordinates to draw this text at.
         *
         * @param x The x-coordinate.
         * @param y The y-coordinate.
         */
        @PublicAPI
        public Builder pos(int x, int y) {
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
        public Builder color(Color color) {
            this.color = color.get();

            return this;
        }

        /**
         * Specify the color to draw with.
         *
         * @param argb An RGB integer.
         */
        @PublicAPI
        public Builder color(int argb) {
            this.color = argb;

            return this;
        }

        /**
         * Set the {@code dropShadow} flag.
         *
         * @param dropShadow The new flag state.
         */
        @PublicAPI
        public Builder setShadow(boolean dropShadow) {
            this.dropShadow = dropShadow;

            return this;
        }

        /**
         * Set the {@code dropShadow} flag to {@code false}.
         */
        @PublicAPI
        public Builder flat() {
            return this.setShadow(false);
        }

        /**
         * This will center the drawn text by subtracting the text's font width from the current x-coordinate.
         */
        @PublicAPI
        public Builder center() {
            this.centerText = true;

            return this;
        }

        /**
         * Draws the text to the screen using the given builder properties.
         */
        @PublicAPI
        public int draw() {
            int textWidth = 0;
            if (this.string != null)
                textWidth = GuiUtil.font().width(this.string);
            else if (this.component != null)
                textWidth = GuiUtil.font().width(this.component);
            else if (this.sequence != null)
                textWidth = GuiUtil.font().width(this.sequence);

            if (this.centerText) {
                this.x -= textWidth / 2;
            }

            if (this.string != null) {
                this.graphics.text(GuiUtil.font(), this.string, this.x, this.y, this.color);
            } else if (this.component != null) {
                this.graphics.text(GuiUtil.font(), this.component, this.x, this.y, this.color, this.dropShadow);
            } else if (this.sequence != null) {
                this.graphics.text(GuiUtil.font(), this.sequence, 0, 0, this.color, this.dropShadow);
            }

            return this.x + textWidth;
        }
    }
}
