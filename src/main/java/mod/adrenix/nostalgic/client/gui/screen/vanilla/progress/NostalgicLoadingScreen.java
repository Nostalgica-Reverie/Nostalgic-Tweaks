package mod.adrenix.nostalgic.client.gui.screen.vanilla.progress;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.progress.LevelLoadProgressTracker;

public class NostalgicLoadingScreen extends Screen {
    /* Fields */

    private final Component header;
    private final Component stage;
    private final LevelLoadProgressTracker progressListener;

    /* Constructor */

    /**
     * Create a new old loading screen instance.
     *
     * @param progressListener The {@link LevelLoadProgressTracker} instance.
     * @param header           The {@link Component} header.
     * @param stage            The {@link Component} stage.
     */
    public NostalgicLoadingScreen(LevelLoadProgressTracker progressListener, Component header, Component stage) {
        super(Component.empty());

        this.progressListener = progressListener;
        this.header = header;
        this.stage = stage;
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldNarrateNavigation() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null)
            return;

        this.extractBackground(graphics, mouseX, mouseY, partialTick);

        ProgressBarExtractor.drawHeaderText(graphics, this.header, this.width);
        ProgressBarExtractor.drawStageText(graphics, this.stage, this.width);
        ProgressBarExtractor.extractProgressWithChunks(graphics, this.progressListener);
    }
}
