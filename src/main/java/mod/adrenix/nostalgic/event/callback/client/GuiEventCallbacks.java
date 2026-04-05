package mod.adrenix.nostalgic.event.callback.client;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.MouseManager;
import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.pause.NostalgicPauseScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.progress.NostalgicLoadingScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.progress.NostalgicProgressScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.NostalgicTitleScreen;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.world.select.NostalgicSelectWorldScreen;
import mod.adrenix.nostalgic.client.gui.tooltip.Tooltip;
import mod.adrenix.nostalgic.mixin.access.LevelLoadingScreenAccess;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.event.EventResult;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;

public abstract class GuiEventCallbacks {
    /**
     * Reroutes screens to a nostalgic alternative if applicable.
     *
     * @param screen The {@link Screen} that is about to be set.
     * @return A {@link EventResult} instance.
     */
    public static EventResult<Screen> rerouteScreen(Screen screen) {
        if (screen == null)
            return EventResult.pass();

        Screen parentScreen = Minecraft.getInstance().screen;

        if (screen.getClass() == TitleScreen.class) {
            if (true/*CandyTweak.OVERRIDE_TITLE_SCREEN.get()*/) //TODO
                return EventResult.stop(new NostalgicTitleScreen());
        }

        if (screen.getClass() == NostalgicTitleScreen.class) {
            if (false/*!CandyTweak.OVERRIDE_TITLE_SCREEN.get()*/) //TODO
                return EventResult.stop(new TitleScreen());
        }

        if (screen.getClass() == SelectWorldScreen.class) {
            if (/*CandyTweak.OLD_WORLD_SELECT_SCREEN.get() != Generic.MODERN*/true && parentScreen != null) //TODO
                return EventResult.stop(new NostalgicSelectWorldScreen(parentScreen));
        }

        //TODO: from testing it seems this can get in an infinite loop. I added an edge case check on if the screen
        //  is the NT pause screen. Not sure if it is caused by either my event listener logic (on Fabric at least)
        //  or due to my current hacky commented out code, as I didn't want to implement a tweaks system yet.
        if (screen instanceof PauseScreen && !(screen instanceof NostalgicPauseScreen)) {
            boolean isHoldingF3 = KeyboardUtil.isDown(InputConstants.KEY_F3);

            if (/*CandyTweak.OLD_PAUSE_MENU.get() != PauseLayout.MODERN*/true && !isHoldingF3) //TODO
                return EventResult.stop(new NostalgicPauseScreen());
        }

        if (true/*CandyTweak.OLD_PROGRESS_SCREEN.get()*/) {
            if (screen instanceof ProgressScreen && ClassUtil.isNotInstanceOf(screen, NostalgicProgressScreen.class))
                return EventResult.stop(new NostalgicProgressScreen((ProgressScreen) screen));

            //TODO: investigate if I'm correctly handling edge-case.
            if (screen instanceof LevelLoadingScreen levelLoadingScreen && !(screen instanceof NostalgicLoadingScreen)) {
                LevelLoadTracker progressListener = ((LevelLoadingScreenAccess)levelLoadingScreen).getLoadTracker();
                LevelLoadingScreen.Reason reason = ((LevelLoadingScreenAccess)levelLoadingScreen).getReason();
                Component header = Lang.Level.LOADING.get();
                Component stage = Lang.Level.BUILDING.get();

                if (progressListener != null)
                    return EventResult.stop(new NostalgicLoadingScreen(progressListener, reason, header, stage));
            }

            if (screen instanceof GenericMessageScreen) {
                NostalgicProgressScreen progressScreen = getProgressScreen(screen.getTitle().getString());

                if (progressScreen.hasStage())
                    return EventResult.stop(progressScreen);
            }
        }

        return EventResult.pass();
    }

    /**
     * Get a progress screen for a generic dirt message screen, if applicable.
     *
     * @param title The current {@link Screen} title.
     * @return A {@link NostalgicProgressScreen} instance.
     */
    public static NostalgicProgressScreen getProgressScreen(String title) {
        NostalgicProgressScreen progressScreen = new NostalgicProgressScreen(new ProgressScreen(false));

        if (title.equals(Lang.Vanilla.SAVE_LEVEL.getString()))
            progressScreen.setStage(Lang.Level.SAVING.get());

        if (title.equals(Lang.Vanilla.WORLD_RESOURCE_LOAD.getString())) {
            progressScreen.setHeader(Lang.Level.LOADING.get());
            progressScreen.setStage(Lang.Vanilla.WORLD_RESOURCE_LOAD.get());
        }

        if (title.equals(Lang.Vanilla.WORLD_DATA_READ.getString())) {
            progressScreen.setHeader(Lang.Level.LOADING.get());
            progressScreen.setStage(Lang.Vanilla.WORLD_DATA_READ.get());
        }

        return progressScreen;
    }

    /**
     * Sets the mouse position in {@link MouseManager}.
     *
     * @param screen       The current {@link Screen}.
     * @param graphics     The {@link GuiGraphicsExtractor} instance.
     * @param mouseX       The x-coordinate of the mouse.
     * @param mouseY       The y-coordinate of the mouse.
     * @param tickProgress The delta time for the current frame.
     */
    public static void setMousePosition(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickProgress) {
        MouseManager.setPosition(mouseX, mouseY);
    }

    /**
     * Renders any extra graphics provided by the mod onto the screen.
     *
     * @param screen       The current {@link Screen}.
     * @param graphics     The {@link GuiGraphicsExtractor} instance.
     * @param mouseX       The x-coordinate of the mouse.
     * @param mouseY       The y-coordinate of the mouse.
     * @param tickProgress The delta time for the current frame.
     */
    public static void renderModGraphics(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float tickProgress) {
        Tooltip.render(screen, graphics);

        if (screen instanceof DynamicScreen<?> || screen instanceof Overlay)
            GuiUtil.renderDebug(graphics);
    }
}
