package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The corner enumeration is used by tweaks that display information to one of the four corners of the game window. This
 * can also be used in other scenarios where text corner tracking would be helpful.
 */
public enum Corner implements EnumTweak
{
    TOP_LEFT(Lang.Enum.CORNER_TOP_LEFT),
    TOP_RIGHT(Lang.Enum.CORNER_TOP_RIGHT),
    TOP_CENTER(Lang.Enum.CORNER_TOP_CENTER),
    BOTTOM_LEFT(Lang.Enum.CORNER_BOTTOM_LEFT),
    BOTTOM_RIGHT(Lang.Enum.CORNER_BOTTOM_RIGHT);

    private final Translation title;

    Corner(Translation title)
    {
        this.title = title;
    }

    /**
     * @return Whether this corner is on the left side of the screen.
     */
    @PublicAPI
    public boolean isLeft()
    {
        return this.equals(TOP_LEFT) || this.equals(BOTTOM_LEFT);
    }

    /**
     * @return Whether this corner is at the top of the screen.
     */
    @PublicAPI
    public boolean isTop()
    {
        return this.equals(TOP_LEFT) || this.equals(TOP_RIGHT);
    }

    /**
     * @return Whether this corner is on the right side of the screen.
     */
    @PublicAPI
    public boolean isRight()
    {
        return this.equals(TOP_RIGHT) || this.equals(BOTTOM_RIGHT);
    }

    /**
     * @return Whether this corner is at the bottom of the screen.
     */
    @PublicAPI
    public boolean isBottom()
    {
        return this.equals(BOTTOM_RIGHT) || this.equals(BOTTOM_LEFT);
    }

    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
