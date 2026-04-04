package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The title layout enumeration is used by the title screen layout tweak. That tweak changes the button layout of the
 * game's main title screen.
 */
public enum TitleLayout implements EnumTweak {
    ALPHA(Generic.ALPHA.getTitle()),
    BETA(Generic.BETA.getTitle()),
    RELEASE_TEXTURE_PACK(Lang.literal("§61.0§r - §61.4.7")),
    RELEASE_NO_TEXTURE_PACK(Lang.literal("§61.5.2§r - §61.7.9")),
    MODERN(Generic.MODERN.getTitle());

    private final Translation title;

    TitleLayout(Translation title) {
        this.title = title;
    }

    @Override
    public Translation getTitle() {
        return this.title;
    }
}
