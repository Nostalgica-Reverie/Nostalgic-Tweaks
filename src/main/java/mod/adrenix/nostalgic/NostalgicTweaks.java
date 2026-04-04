package mod.adrenix.nostalgic;

import mod.adrenix.nostalgic.services.NostalgicServices;
import mod.adrenix.nostalgic.util.common.log.ModLogger;

public class NostalgicTweaks {
    /**
     * This is the mod's unique identifier. This should never change. If a change is required, then it is important that
     * mod developers using our API are properly informed of the change.
     */
    public static final String MOD_ID = "nostalgic_tweaks";

    /**
     * This is the mod's display name. This can change, but should not be required since it closely resembles the mod's
     * unique identifier.
     */
    public static final String MOD_NAME = "Nostalgic Tweaks";

    /**
     * This is a unique logger instance. It will change the output visible in the debugging console and in a player's
     * runtime console.
     */
    public static final ModLogger LOGGER = new ModLogger(MOD_NAME);

    /**
     * Check if the logger is in debug mode.
     *
     * @return Whether the internal mod logger is in debugging mode.
     */
    public static boolean isDebugging() {
        return LOGGER.isDebugMode();
    }

    /**
     * @return Whether the game is running in a development environment.
     */
    public static boolean isDeveloping() {
        return NostalgicServices.PLATFORM.isDevEnvironment();
    }
}
