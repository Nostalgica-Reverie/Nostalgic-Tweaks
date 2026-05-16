package mod.adrenix.nostalgic;

import dev.architectury.platform.Platform;
import dev.architectury.utils.EnvExecutor;
import mod.adrenix.nostalgic.config.cache.ConfigCache;
import mod.adrenix.nostalgic.init.ModInitializer;
import mod.adrenix.nostalgic.network.ModConnection;
import mod.adrenix.nostalgic.services.NostalgicServices;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.log.LogColor;
import mod.adrenix.nostalgic.util.common.log.ModLogger;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class NostalgicTweaks
{
    /* - Identifiers */

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

    /* - Context */

    /**
     * Getter method for checking if the mod is running on the logical client.
     *
     * @return Whether the mod is running on the client.
     */
    public static boolean isClient()
    {
        return NostalgicServices.PLATFORM.isClient();
    }

    /**
     * Getter method for checking if the mod is running on the logical server.
     *
     * @return Whether the mod is running on the server.
     */
    public static boolean isServer()
    {
        return !NostalgicServices.PLATFORM.isClient();
    }

    /**
     * Getter method for checking if the mod is running on Fabric.
     *
     * @return Whether the mod is loaded by a Fabric loader.
     */
    public static boolean isFabric()
    {
        return NostalgicServices.PLATFORM.getPlatformName().equals("Fabric");
    }

    /**
     * Getter method for checking if the mod is running on NeoForge.
     *
     * @return Whether the mod is loaded by a NeoForge loader.
     */
    public static boolean isForge()
    {
        return NostalgicServices.PLATFORM.getPlatformName().equals("NeoForge");
    }

    /**
     * Gets a string representation of the current mod loader the user is using.
     *
     * @return The mod loader name.
     */
    public static String getLoader()
    {
        return NostalgicServices.PLATFORM.getPlatformName();
    }

    /* - Version */

    private static final NullableHolder<String> FULL_VERSION = NullableHolder.empty();
    private static final NullableHolder<String> BETA_VERSION = NullableHolder.empty();
    private static final NullableHolder<String> TINY_VERSION = NullableHolder.empty();
    private static final NullableHolder<String> SHORT_VERSION = NullableHolder.empty();

    /**
     * Retrieves the current Minecraft version.
     *
     * @return The current Minecraft version.
     */
    public static String getMinecraftVersion()
    {
        return SharedConstants.getCurrentVersion().getName();
    }

    /**
     * This will give the version number as represented by the mod's jar file.
     *
     * @return A version number stored in the jar file with no formatting.
     */
    public static String getRawVersion()
    {
        return Platform.getMod(MOD_ID).getVersion();
    }

    /**
     * This will follow the same filename format used on the mod's approved distribution sites.
     *
     * @return A version number with a prefixed title case mod loader name and Minecraft version.
     */
    public static String getFullVersion()
    {
        return FULL_VERSION.computeIfAbsent(() -> String.format("%s-%s-%s", getLoader(), getMinecraftVersion(), getRawVersion()));
    }

    /**
     * This will give the beta version number. If the build is not in beta, then an empty string is returned.
     *
     * @return A beta version number formatted as 'Beta-#' or 'Beta-#.#'.
     */
    public static String getBetaVersion()
    {
        return BETA_VERSION.computeIfAbsent(() -> TextUtil.extract(getRawVersion(), "beta\\.\\d+"));
    }

    /**
     * This will give a version number where all additional information attached to a version string is removed.
     *
     * @return A tiny version number formatted as '#.#.#'.
     */
    public static String getTinyVersion()
    {
        return TINY_VERSION.computeIfAbsent(() -> TextUtil.extract(getRawVersion(), "(\\d\\.\\d\\.\\d)"));
    }

    /**
     * This will follow the same filename format as above but without the mod loader name.
     *
     * @return A version number with a prefixed Minecraft version.
     */
    public static String getShortVersion()
    {
        return SHORT_VERSION.computeIfAbsent(() -> String.format("%s-%s", getMinecraftVersion(), getRawVersion()));
    }

    /* - Networking */

    /**
     * This is the mod's network current protocol version. If there are any changes made to the mod's network code that
     * will cause communication issues with older versions of the mod, then this value needs bumped up. Typically,
     * changes made to network packets and/or changes in tweak data serialization will require a bump.
     */
    public static final String PROTOCOL = "3.1";

    /**
     * Functional lambda shortcut method for supplying the network's protocol version.
     *
     * @return The mod's network protocol version.
     */
    public static String getProtocol()
    {
        return PROTOCOL;
    }

    /**
     * This field indicates if the client is connected to a level with Nostalgic Tweaks installed. This field will
     * always be true in singleplayer. Otherwise, it will only be true in multiplayer if the mod is connected to a
     * server with the mod installed. This field will be <code>false</code> when not playing on a level.
     */
    private static boolean isNetworkSupported = false;

    /**
     * This field stores the current connection state with a server running Nostalgic Tweaks. Once a handshake is
     * performed between the server and the client, data from the Nostalgic Tweaks mod running on the server is sent to
     * the client.
     */
    private static ModConnection connection = null;

    /**
     * Change the server connection data stored on the client. The server has no need to invoke this method. If the user
     * disconnects from a server running Nostalgic Tweaks, this value should be reset back to {@code null}.
     *
     * @param data A nostalgic connection instance.
     */
    public static void setConnection(@Nullable ModConnection data)
    {
        connection = data;
    }

    /**
     * Get the current connection data between a server running Nostalgic Tweaks and the client. If the user is playing
     * in singleplayer, there will be data stored here since the session could possibly move to LAN. It is up to the
     * invoker to check for a singleplayer-only world. If the user is not playing on a server running Nostalgic Tweaks,
     * then no data will be stored.
     *
     * @return An optional nostalgic connection instance.
     */
    public static Optional<ModConnection> getConnection()
    {
        return Optional.ofNullable(connection);
    }

    /**
     * This field tracks the game's logical server if the mod is running in server-side mode. Otherwise, the client
     * should never use this field.
     */
    private static MinecraftServer server;

    /**
     * Set the logical server instance for the mod.
     *
     * @param minecraftServer A minecraft server instance.
     */
    public static void setServer(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
    }

    /**
     * A getter method for retrieving the logical server instance. This should only be used by the server and not used
     * by the client.
     *
     * @return A minecraft server instance.
     */
    @Nullable
    public static MinecraftServer getServer()
    {
        return server;
    }

    /**
     * Gets the integrated server for singleplayer environments or the dedicated server for multiplayer environments.
     *
     * @return A {@code nullable} {@link MinecraftServer} instance.
     */
    @PublicAPI
    @Nullable
    public static MinecraftServer getIntegratedOrDedicatedServer()
    {
        return EnvExecutor.getEnvSpecific(() -> GameUtil::getIntegratedServer, () -> NostalgicTweaks::getServer);
    }

    /**
     * This checks if the network is verified. This will always be <code>true</code> when the mod is running on the
     * logical server since it is impossible to be unverified. This may not always be true on the client since the user
     * may connect to a multiplayer server without the mod installed.
     *
     * @return Whether the network connection is verified by a multiplayer server or internal server.
     */
    public static boolean isNetworkVerified()
    {
        return isServer() || isNetworkSupported;
    }

    /**
     * This sets the state of network connection verification. This should only be changed during level disconnection or
     * during level connection. Never change this while connected and playing on a level.
     *
     * @param verified A network verification state.
     */
    public static void setNetworkVerification(boolean verified)
    {
        LOGGER.debug("Setting network verification to: %s", verified);
        isNetworkSupported = verified;
    }

    /* - Initialization */

    private static boolean modInitialized = false;

    /**
     * @return Check whether a mixin is getting mod data too early.
     */
    public static boolean isMixinEarly()
    {
        return !modInitialized;
    }

    /**
     * Initializes the mod for both the client and server.
     */
    public static void initialize()
    {
        ModInitializer.register();

        if (isServer())
            ConfigCache.initServer();
        else
        {
            ConfigCache.initClient();

            if (ModTracker.OPTIFINE.isInstalled())
                LOGGER.warn("Optifine is installed - some tweaks may not work as intended");
        }

        modInitialized = true;

        String loader = LogColor.apply(LogColor.LIGHT_PURPLE, getLoader());
        String environment = isClient() ? "client" : "server";

        LOGGER.info("Loading mod in [%s] %s environment", loader, environment);
    }

    /* - Debugging */

    /**
     * Check if the logger is in debug mode.
     *
     * @return Whether the internal mod logger is in debugging mode.
     */
    public static boolean isDebugging()
    {
        return LOGGER.isDebugMode();
    }

    /**
     * @return Whether the game is running in a development environment.
     */
    public static boolean isDeveloping()
    {
        return NostalgicServices.PLATFORM.isDevEnvironment();
    }

    /**
     * Flag that indicates if the event API is being tested.
     */
    private static final boolean isEventTesting = false;

    /**
     * @return Whether the mod should register event tests.
     */
    @PublicAPI
    public static boolean isEventTesting()
    {
        return NostalgicServices.PLATFORM.isDevEnvironment() && isEventTesting;
    }
}
