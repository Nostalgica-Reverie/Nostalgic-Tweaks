package mod.adrenix.nostalgic.tweak.gui;

import mod.adrenix.nostalgic.tweak.config.SwingTweak;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * This enumeration must be kept server-safe. It is used as a bridge between tweak config definitions and the client
 * slider widget.
 */
public enum SliderType
{
    /* Types */

    GENERIC,
    STAMINA,
    HEARTS,

    // @formatter:off
    SWING
    {
        // @formatter:on
        @Override
        public Component getTranslation(Number value)
        {
            return Lang.Slider.SPEED.get();
        }

        @Override
        public ChatFormatting getColor(Number value)
        {
            if (value.intValue() == SwingTweak.DISABLED)
                return ChatFormatting.RED;
            else if (value.intValue() == SwingTweak.MIN_SPEED)
                return ChatFormatting.YELLOW;
            else if (value.intValue() < SwingTweak.NEW_SPEED)
                return ChatFormatting.GOLD;
            else
                return ChatFormatting.GREEN;
        }
    },

    // @formatter:off
    INTENSITY
    {
        // @formatter:on
        @Override
        public Component getTranslation(Number value)
        {
            return Lang.Slider.INTENSITY.get();
        }

        @Override
        public Component getSuffix(Number value)
        {
            return Component.literal("%");
        }

        @Override
        public ChatFormatting getColor(Number value)
        {
            if (value.intValue() == 0)
                return ChatFormatting.RED;
            else if (value.intValue() <= 50)
                return ChatFormatting.GOLD;
            else if (value.intValue() > 100)
                return ChatFormatting.AQUA;
            else
                return ChatFormatting.GREEN;
        }
    },

    // @formatter:off
    CLOUD
    {
        // @formatter:on
        @Override
        public Component getTranslation(Number value)
        {
            return switch (value.intValue())
            {
                case 108 -> Lang.Enum.ALPHA.get();
                case 128 -> Lang.Enum.BETA.get();
                case 192 -> Lang.Enum.MODERN.get();
                default -> Lang.Slider.CUSTOM.get();
            };
        }

        @Override
        public ChatFormatting getColor(Number value)
        {
            if (value.intValue() == 128)
                return ChatFormatting.YELLOW;
            else if (value.intValue() == 192)
                return ChatFormatting.GOLD;
            else
                return ChatFormatting.LIGHT_PURPLE;
        }
    };

    /* Methods */

    /**
     * Some slider types have a translation that applies to all sliders.
     *
     * @param value The current slider value.
     * @return A custom translation or an empty component.
     */
    public Component getTranslation(Number value)
    {
        return Component.empty();
    }

    /**
     * Some slider types have a custom suffix that applies to all sliders.
     *
     * @param value The current slider value.
     * @return A custom suffix or an empty component.
     */
    public Component getSuffix(Number value)
    {
        return Component.empty();
    }

    /**
     * Some slider types have a custom color applied based on the current slider value.
     *
     * @param value The current slider value.
     * @return A custom chat formatting color.
     */
    public ChatFormatting getColor(Number value)
    {
        return ChatFormatting.RESET;
    }
}
