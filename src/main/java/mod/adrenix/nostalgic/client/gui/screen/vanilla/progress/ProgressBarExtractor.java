package mod.adrenix.nostalgic.client.gui.screen.vanilla.progress;

import com.mojang.blaze3d.platform.Window;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.LevelLoadProgressTracker;

public abstract class ProgressBarExtractor {
    /**
     * Draws the header text to the top of the progress screen.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     * @param header   The {@link Component} header to draw.
     * @param width    The width of the current {@link Screen}.
     */
    public static void drawHeaderText(GuiGraphicsExtractor graphics, Component header, int width) {
        graphics.centeredText(GuiUtil.font(), header, width / 2, GuiUtil.getGuiHeight() / 2 - 4 - 16, 0xFFFFFFFF);
    }

    /**
     * Draws the stage text below the title of the progress screen.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     * @param stage    The {@link Component} stage to draw.
     * @param width    The width of the current {@link Screen}.
     */
    public static void drawStageText(GuiGraphicsExtractor graphics, Component stage, int width) {
        graphics.centeredText(GuiUtil.font(), stage, width / 2, GuiUtil.getGuiHeight() / 2 - 4 + 8, 0xFFFFFFFF);
    }

    /**
     * Extracts a progress bar with a chunk progress listener.
     * <p>
     *
     * @param graphics         A {@link GuiGraphicsExtractor} instance.
     * @param progressListener A {@link LevelLoadTracker} instance.
     */
    public static void extractProgressWithChunks(GuiGraphicsExtractor graphics, LevelLoadTracker progressListener) {
        ProgressBarExtractor.extract(graphics, (int) (progressListener.serverProgress() * 100.0F));
    }

    /**
     * Extracts a progress bar with an integer.
     * <p>
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     * @param progress An integer between 0-100.
     */
    public static void extractProgressWithInt(GuiGraphicsExtractor graphics, int progress) {
        ProgressBarExtractor.extract(graphics, progress);
    }

    /**
     * Extracts a progress bar.
     * <p>
     *
     * @param graphics A {@link GuiGraphicsExtractor} instance.
     * @param progress An integer between 0-100.
     */
    private static void extract(GuiGraphicsExtractor graphics, int progress) {
        Window window = GuiUtil.getWindow();

        int xOffset = 100;
        int yOffset = 2;
        int xStart = window.getGuiScaledWidth() / 2 - xOffset / 2;
        int yStart = window.getGuiScaledHeight() / 2 + 16;

        if (progress >= xOffset)
            progress = xOffset;

        Color background = new Color(128, 128, 128);
        Color foreground = new Color(128, 255, 128);

        graphics.fill(xStart, yStart, xStart + xOffset, yStart + yOffset, background.get());
        graphics.fill(xStart, yStart, xStart + progress, yStart + yOffset, foreground.get());
    }
}
