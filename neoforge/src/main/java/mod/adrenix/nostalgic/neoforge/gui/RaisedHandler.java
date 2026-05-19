package mod.adrenix.nostalgic.neoforge.gui;

import dev.yurisuika.raised.api.RaisedApi;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;

/**
 * Simple compatibility class to access the Raised mod API.
 */
public abstract class RaisedHandler
{
    /**
     * Track if an unsupported version is present. This will occur if the Raised mod is installed, but the calls we are
     * making are no longer valid since the API changed or doesn't exist.
     */
    public static final FlagHolder UNSUPPORTED_VERSION = FlagHolder.off();

    /**
     * @return The current x-offset as defined by the hotbar layer.
     */
    public static int getHotbarX()
    {
        if (UNSUPPORTED_VERSION.get())
            return 0;

        try
        {
            return RaisedApi.getX("minecraft:hotbar");
        }
        catch (Throwable t)
        {
            NostalgicTweaks.LOGGER.warn("[Raised] API call doesn't exist! Old version probably installed (currently targeting 5.x)");
            UNSUPPORTED_VERSION.enable();

            return 0;
        }
    }

    /**
     * @return The current y-offset as defined by the hotbar layer.
     */
    public static int getHotbarY()
    {
        if (UNSUPPORTED_VERSION.get())
            return 0;

        try
        {
            return RaisedApi.getY("minecraft:hotbar");
        }
        catch (Throwable t)
        {
            NostalgicTweaks.LOGGER.warn("[Raised] API call doesn't exist! Old version probably installed (currently targeting 5.x)");
            UNSUPPORTED_VERSION.enable();

            return 0;
        }
    }
}
