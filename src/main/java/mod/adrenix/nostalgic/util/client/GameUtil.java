package mod.adrenix.nostalgic.util.client;

//import mod.adrenix.nostalgic.tweak.config.CandyTweak;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class GameUtil {
    /* Fields */

    /**
     * Checks if there is a mob present that is changing the fog state.
     */
    public static final FlagHolder MOB_EFFECT_ACTIVE = FlagHolder.off();

    /* Methods */

    /**
     * @return The current version of the game.
     */
    @PublicAPI
    public static String getVersion() {
        return SharedConstants.getCurrentVersion().name();
    }

    /**
     * @return Whether this was called on the integrated server thread.
     */
    @PublicAPI
    public static boolean isOnIntegratedSeverThread() {
        IntegratedServer integratedServer = Minecraft.getInstance().getSingleplayerServer();

        if (integratedServer == null)
            return false;

        return Thread.currentThread() == integratedServer.getRunningThread();
    }

    /**
     * Check if the current game mode is creative.
     *
     * @return Whether Minecraft is in creative mode.
     */
    @PublicAPI
    public static boolean isCreativeMode() {
        return Minecraft.getInstance().gameMode != null &&
                Minecraft.getInstance().gameMode.getPlayerMode().isCreative();
    }

    /**
     * Check if the current game mode is not creative.
     *
     * @return Whether Minecraft is not in creative mode.
     */
    @PublicAPI
    public static boolean isNotCreativeMode() {
        return !isCreativeMode();
    }

    /**
     * Check if the current game mode is spectator.
     *
     * @return Whether Minecraft is in spectator mode.
     */
    @PublicAPI
    public static boolean isSpectatorMode() {
        return Minecraft.getInstance().gameMode != null &&
                Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR;
    }

    /**
     * Check if the current game mode is not spectator.
     *
     * @return Whether Minecraft is not in spectator mode.
     */
    @PublicAPI
    public static boolean isNotSpectatorMode() {
        return !isSpectatorMode();
    }

    /**
     * Check if the current game mode is survival.
     *
     * @return Whether Minecraft is in survival mode.
     */
    @PublicAPI
    public static boolean isSurvivalMode() {
        return isNotCreativeMode() && isNotSpectatorMode();
    }

    /**
     * Check if the current game mode is not survival.
     *
     * @return Whether Minecraft is not in survival mode.
     */
    @PublicAPI
    public static boolean isNotSurvivalMode() {
        return !isSurvivalMode();
    }

    /**
     * Gets the singleplayer server if it exists.
     *
     * @return A {@code nullable} {@link MinecraftServer} instance.
     */
    @Nullable
    @PublicAPI
    public static MinecraftServer getIntegratedServer() {
        return Minecraft.getInstance().getSingleplayerServer();
    }

    /**
     * Get the client's integrated server level instance.
     *
     * @return The current {@link ServerLevel} instance for the overworld, if it exists.
     */
    @Nullable
    @PublicAPI
    public static ServerLevel getOverworldLevel() {
        if (Minecraft.getInstance().getSingleplayerServer() == null)
            return null;

        return Minecraft.getInstance().getSingleplayerServer().getLevel(Level.OVERWORLD);
    }

    /**
     * Check if the game is currently in the overworld.
     *
     * @return Whether local player is in the overworld dimension.
     */
    @PublicAPI
    public static boolean isInOverworld() {
        ClientLevel level = Minecraft.getInstance().level;

        return level != null && level.dimension().equals(Level.OVERWORLD);
    }

    /**
     * Check if the game is currently in the Nether.
     *
     * @return Whether local player is in the Nether dimension.
     */
    @PublicAPI
    public static boolean isInNether() {
        ClientLevel level = Minecraft.getInstance().level;

        return level != null && level.dimension().equals(Level.NETHER);
    }

    /**
     * Check if the game is currently in the End.
     *
     * @return Whether local player is in the End dimension.
     */
    @PublicAPI
    public static boolean isInEnd() {
        ClientLevel level = Minecraft.getInstance().level;

        return level != null && level.dimension().equals(Level.END);
    }

    /**
     * Shortcut for checking if a model is flat based on the given item stack.
     *
     * @param itemStack      The {@link ItemStack} to get model data from.
     * @param displayContext The {@link ItemDisplayContext} to check for.
     * @return Whether the item stack is rendered flat.
     */
    @PublicAPI
    public static boolean isModelFlat(ItemStack itemStack, ItemDisplayContext displayContext) {
        ItemStackRenderState renderState = new ItemStackRenderState();
        Minecraft.getInstance()
                .getItemModelResolver()
                .updateForTopItem(renderState, itemStack, displayContext, null, null, 0);

        //TODO
        boolean isException = false;//CandyTweak.OLD_2D_EXCEPTIONS.get().containsItem(itemStack);

        return !renderState.usesBlockLight() && !isException;
    }

    /**
     * Shortcut for checking if a model is flat based on the given item.
     *
     * @param item           The {@link Item} to get model data from.
     * @param displayContext The {@link ItemDisplayContext} to check for.
     * @return Whether the item is rendered flat.
     */
    @PublicAPI
    public static boolean isModelFlat(Item item, ItemDisplayContext displayContext) {
        return isModelFlat(item.getDefaultInstance(), displayContext);
    }

    /**
     * @return The effective render distance defined by the game's user settings.
     */
    @PublicAPI
    public static int getRenderDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance();
    }

    /**
     * Get an integer between zero and three (which represents the old render distance tiny, short, normal, and far)
     * using the current game's render distance.
     *
     * @return An integer between zero and three.
     */
    @PublicAPI
    public static int getOldRenderDistance() {
        int renderDistance = getRenderDistance();

        if (renderDistance >= 16)
            return 0;
        else if (renderDistance >= 8)
            return 1;
        else if (renderDistance >= 4)
            return 2;
        else
            return 3;
    }
}
