package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.home.NTPanorama;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockData;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;

import java.util.ArrayList;

//import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.FallingBlockRenderer;

public class FallingBlockEditorScreen extends EnhancedScreen<FallingBlockEditorScreen, EditorWidgets> {
    /* Fields */

    private final EditorHistory history = new EditorHistory(this);
    private final boolean isInitialAnimationFinished = false;
    private FallingBlockData initial;
    private FallingBlockData managed;
    private EditorWidgets editorWidgets;
    //    private FallingBlockRenderer blockLogo; //TODO
    private boolean hasErrorOccurred = false;
    private boolean areBlocksChanged = false;

    /* Constructor */

    /**
     * Create a new {@link FallingBlockEditorScreen} instance.
     */
    public FallingBlockEditorScreen(Screen parentScreen) {
        super(EditorWidgets::new, parentScreen, Lang.EMPTY.get());

        this.reloadFromDisk();

        CanvasTools.reset();
        ToolDrawer.reset();
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void tick() {
        if (this.hasErrorOccurred) {
            this.hasErrorOccurred = false;

            EditorOverlay.couldNotReadConfig(this::reloadFromDisk, true);
        }

        //TODO
//        if (this.blockLogo.isFinished() && !this.isInitialAnimationFinished)
//            this.isInitialAnimationFinished = true;

        this.areBlocksChanged = FallingBlockConfig.isDataChanged(this.initial, this.managed);

        super.tick();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FallingBlockEditorScreen self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditorWidgets getWidgetManager() {
        return this.editorWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidgetManager(EditorWidgets editorWidgets) {
        this.editorWidgets = editorWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        return this.editorWidgets.keyPressed(event.key()) || super.keyPressed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft.level == null)
            NTPanorama.extractRenderState(graphics);

        graphics.fill(0, 0, this.width, this.height, Color.BLACK.fromAlpha(0.85D).get());

        SeparatorWidget separatorTop = this.editorWidgets.getTopOfEditor();
        SeparatorWidget separatorBottom = this.editorWidgets.getBottomOfEditor();

        Rectangle topOfEditor = new Rectangle(0, 0, this.width, separatorTop.getY());
        Rectangle bottomOfEditor = new Rectangle(0, separatorBottom.getY(), this.width, this.height);

        graphics.enableScissor(topOfEditor.startX(), topOfEditor.startY(), topOfEditor.endX(), topOfEditor.endY());
        GuiUtil.extractDirtBackground(graphics);

        //TODO
//        this.blockLogo.render(1.8F);

        graphics.disableScissor();
        graphics.enableScissor(bottomOfEditor.startX(), bottomOfEditor.startY(), bottomOfEditor.endX(), bottomOfEditor.endY());
        GuiUtil.extractDirtBackground(graphics);
        graphics.disableScissor();

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * @return The {@link EditorHistory} instance for this screen.
     */
    public EditorHistory getHistory() {
        return this.history;
    }

    /**
     * @return The falling block data that this screen is managing.
     */
    public FallingBlockData getManagedData() {
        return this.managed;
    }

    /**
     * Change the data this screen is managing. The given data will be copied, so there is no need to do that
     * beforehand.
     *
     * @param data The {@link FallingBlockData} to copy from and to set as this screen's managed data.
     */
    public void setManagedData(FallingBlockData data) {
        this.managed = data.copy();
    }

    /**
     * @return The falling data blocks that this screen is managing.
     */
    public ArrayList<FallingBlockData.Block> getManagedBlocks() {
        return this.managed.blocks;
    }

    /**
     * Replay the falling block animation.
     *
     * @param immediate Whether the animation should complete immediately.
     */
    public void replayAnimation(boolean immediate) {
        if (!this.isInitialAnimationFinished) {
        }

        //TODO
//        this.blockLogo = new FallingBlockRenderer(this.managed, immediate);
    }

    /**
     * Replay the falling block animation, which is not immediate.
     */
    public void replayAnimation() {
        this.replayAnimation(false);
    }

    /**
     * Reload all falling block data from the config file saved on disk.
     */
    public void reloadFromDisk() {
        this.hasErrorOccurred = !FallingBlockConfig.read();

        //TODO
        /*if (FallingBlockConfig.isNotAvailable()) {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] The config isn't ready, this shouldn't happen!");
            this.blockLogo = new FallingBlockRenderer(new FallingBlockData());
        } else {
            if (FallingBlockConfig.hasNoBlocks()) {
                FallingBlockConfig.setBlockDataToDefault();
                FallingBlockConfig.save();
            }

            this.initial = FallingBlockConfig.getData();
            this.managed = this.initial.copy();
            this.blockLogo = new FallingBlockRenderer(this.managed);

            this.history.setFirstPointOnTimeline(this.initial);
        }*/
    }

    /**
     * @return Whether changes made to the falling block data is savable.
     */
    public boolean hasChanges() {
        return this.areBlocksChanged;
    }

    /**
     * Save the current block data to disk.
     */
    public void save() {
        if (FallingBlockConfig.INSTANCE.isEmpty()) {
            NostalgicTweaks.LOGGER.warn("[Falling Blocks] Could not save config file due to empty block data!");
            return;
        }

        FallingBlockConfig.apply(this.managed);
        FallingBlockConfig.save();

        this.initial = FallingBlockConfig.INSTANCE.orElse(null);
        this.managed = this.initial.copy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose() {
        if (!this.hasChanges()) {
            super.onClose();
            return;
        }

        EditorOverlay.areYouSure(this);
    }
}
