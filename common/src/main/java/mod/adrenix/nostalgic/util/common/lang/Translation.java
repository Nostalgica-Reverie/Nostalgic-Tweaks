package mod.adrenix.nostalgic.util.common.lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * The translation record provides a utility layer for lang keys stored in {@link Lang}.
 *
 * @param langKey The lang file key.
 */
public record Translation(String langKey)
{
    /**
     * Get the translation component for this lang key.
     *
     * @param args Any additional arguments that need passed to the translatable language file definition.
     * @return A {@link MutableComponent} translation.
     */
    public MutableComponent get(Object... args)
    {
        return DecodeLang.findAndReplace(Component.translatable(this.langKey, args));
    }

    /**
     * Get the translation component for this lang key with the given formatting applied.
     *
     * @param formatting A varargs of {@link ChatFormatting}.
     * @return A {@link MutableComponent} translation.
     */
    public MutableComponent withStyle(ChatFormatting... formatting)
    {
        return this.get().withStyle(formatting);
    }

    /**
     * Get the translation component for this lang key that is void of any formatting.
     *
     * @param args Any additional arguments that need passed to the translatable language file definition.
     * @return A {@link MutableComponent} translation.
     */
    public MutableComponent plainCopy(Object... args)
    {
        return Component.literal(this.get(args).getString().replaceAll("§[a-f0-9k-or]", ""));
    }

    /**
     * Get the translation string for this lang key.
     *
     * @param args Any additional arguments that need passed to the translatable language file definition.
     * @return A string representation of the translated component.
     */
    public String getString(Object... args)
    {
        return this.get(args).getString();
    }

    /**
     * Keys defined in the above utility must have a lang file key associated with them. If a key instance is created
     * outside of this utility, then it may be possible for it to have a blank lang file key. This method provides a
     * quick way of checking that.
     *
     * @return Whether the lang file key for this key instance is blank.
     */
    public boolean isBlank()
    {
        return this.langKey.isBlank();
    }
}
