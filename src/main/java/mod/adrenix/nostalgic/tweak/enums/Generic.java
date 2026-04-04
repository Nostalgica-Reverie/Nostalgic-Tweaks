package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The generic enumeration is used by tweaks that change their behavior based on a wide Minecraft version range.
 */
public enum Generic implements EnumTweak {
    ALPHA(Lang.Enum.ALPHA),
    BETA(Lang.Enum.BETA),
    MODERN(Lang.Enum.MODERN);

    private final Translation title;

    Generic(Translation title) {
        this.title = title;
    }

    @Override
    public Translation getTitle() {
        return this.title;
    }
}
