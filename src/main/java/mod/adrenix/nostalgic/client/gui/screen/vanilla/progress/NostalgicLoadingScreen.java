package mod.adrenix.nostalgic.client.gui.screen.vanilla.progress;

import mod.adrenix.nostalgic.mixin.access.LevelLoadingScreenAccess;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import net.minecraft.network.chat.Component;

public class NostalgicLoadingScreen extends LevelLoadingScreen {
    /* Fields */

    private final Component header;
    private final Component stage;

    /* Constructor */

    /**
     * Create a new old loading screen instance.
     *
     * @param loadTracker The {@link LevelLoadTracker} instance.
     * @param reason      The {@link LevelLoadingScreen.Reason} of the loading screen.
     * @param header      The {@link Component} header.
     * @param stage       The {@link Component} stage.
     */
    public NostalgicLoadingScreen(LevelLoadTracker loadTracker, LevelLoadingScreen.Reason reason,
                                  Component header, Component stage) {
        super(loadTracker, reason);

        this.header = header;
        this.stage = stage;
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        ProgressBarExtractor.drawHeaderText(graphics, this.header, this.width);
        ProgressBarExtractor.drawStageText(graphics, this.stage, this.width);
        ProgressBarExtractor.extractProgressWithChunks(graphics, ((LevelLoadingScreenAccess)this).getLoadTracker());
    }
}
