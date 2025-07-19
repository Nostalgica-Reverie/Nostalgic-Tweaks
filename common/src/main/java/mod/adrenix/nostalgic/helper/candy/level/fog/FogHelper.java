package mod.adrenix.nostalgic.helper.candy.level.fog;

import mod.adrenix.nostalgic.util.common.data.FlagHolder;

/**
 * This class is only used by the client.
 */
public abstract class FogHelper
{
    /**
     * Sodium's custom cloud renderer breaks our fog. This will track if Sodium is doing this and prevent our changes
     * from being applied when Sodium begins rendering fog from its cloud renderer.
     */
    public static final FlagHolder SODIUM_CLOUDS = FlagHolder.off();
}
