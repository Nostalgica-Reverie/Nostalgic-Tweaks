package mod.adrenix.nostalgic.neoforge.gui;

import dev.yurisuika.raised.api.RaisedApi;

/**
 * Simple compatibility class to access the Raised mod API.
 */
public abstract class RaisedHandler
{
    /**
     * @return The current x-offset as defined by the hotbar layer.
     */
    public static int getHotbarX()
    {
        return RaisedApi.getX("minecraft:hotbar");
    }

    /**
     * @return The current y-offset as defined by the hotbar layer.
     */
    public static int getHotbarY()
    {
        return RaisedApi.getY("minecraft:hotbar");
    }
}
