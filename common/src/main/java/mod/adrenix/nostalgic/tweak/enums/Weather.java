package mod.adrenix.nostalgic.tweak.enums;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;

/**
 * The weather enumeration is used by the client-side weather overrides tweak.
 */
public enum Weather implements EnumTweak
{
    BIOME(Lang.Enum.WEATHER_BIOME),
    RAIN(Lang.Enum.WEATHER_RAIN),
    SNOW(Lang.Enum.WEATHER_SNOW);

    private final Translation title;

    Weather(Translation title)
    {
        this.title = title;
    }


    @Override
    public Translation getTitle()
    {
        return this.title;
    }
}
