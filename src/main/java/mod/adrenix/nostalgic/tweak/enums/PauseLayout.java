package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Translation;
import mod.adrenix.nostalgic.util.common.lang.Lang;

/**
 * The pause layout enumeration is used by the pause layout tweak. That tweak changes the button layout of the game's
 * pause screen.
 */
public enum PauseLayout implements EnumTweak
{
    ALPHA_BETA(Lang.literal("§aAlpha§r - §eb1.4_01")),
    ACHIEVE_LOWER(Lang.literal("§eb1.5§r - §61.0")),
    ACHIEVE_UPPER(Lang.literal("§61.1§r - §61.2.5")),
    LAN(Lang.literal("§61.3§r - §61.11")),
    ADVANCEMENT(Lang.literal("§61.12§r - §61.13.2")),
    MODERN(Generic.MODERN.getTitle());

    private final Translation title;

    PauseLayout(Translation title)
    {
        this.title = title;
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
