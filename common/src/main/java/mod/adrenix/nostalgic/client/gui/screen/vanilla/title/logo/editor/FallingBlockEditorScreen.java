package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.FallingBlockRenderer;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockData;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;

public class FallingBlockEditorScreen extends EnhancedScreen<FallingBlockEditorScreen, EditorWidgets>
{
    /* Fields */

    private FallingBlockData initial;
    private FallingBlockData managed;
    private EditorWidgets editorWidgets;
    private FallingBlockRenderer blockLogo;
    private boolean hasErrorOccurred = false;
    private boolean areBlocksChanged = false;
    private boolean isInitialAnimationFinished = false;
    private final EditorHistory history = new EditorHistory(this);

    /* Constructor */

    /**
     * Create a new {@link FallingBlockEditorScreen} instance.
     */
    public FallingBlockEditorScreen(Screen parentScreen)
    {
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
    public void tick()
    {
        if (this.hasErrorOccurred)
        {
            this.hasErrorOccurred = false;

            EditorOverlay.couldNotReadConfig(this::reloadFromDisk, true);
        }

        if (this.blockLogo.isFinished() && !this.isInitialAnimationFinished)
            this.isInitialAnimationFinished = true;

        this.areBlocksChanged = FallingBlockConfig.isDataChanged(this.initial, this.managed);

        super.tick();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FallingBlockEditorScreen self()
    {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EditorWidgets getWidgetManager()
    {
        return this.editorWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidgetManager(EditorWidgets editorWidgets)
    {
        this.editorWidgets = editorWidgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.editorWidgets.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.minecraft.level == null)
            Panorama.render(graphics);

        RenderUtil.fill(graphics, 0, 0, this.width, this.height, Color.BLACK.fromAlpha(0.85D));

        SeparatorWidget separatorTop = this.editorWidgets.getTopOfEditor();
        SeparatorWidget separatorBottom = this.editorWidgets.getBottomOfEditor();

        Rectangle topOfEditor = new Rectangle(0, 0, this.width, separatorTop.getY());
        Rectangle bottomOfEditor = new Rectangle(0, separatorBottom.getY(), this.width, this.height);

        RenderUtil.pushScissor(topOfEditor);
        GuiUtil.renderDirtBackground(graphics);

        this.blockLogo.render(1.8F);

        RenderUtil.popScissor();
        RenderUtil.pushScissor(bottomOfEditor);
        GuiUtil.renderDirtBackground(graphics);
        RenderUtil.popScissor();

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * @return The {@link EditorHistory} instance for this screen.
     */
    public EditorHistory getHistory()
    {
        return this.history;
    }

    /**
     * Change the data this screen is managing. The given data will be copied, so there is no need to do that
     * beforehand.
     *
     * @param data The {@link FallingBlockData} to copy from and to set as this screen's managed data.
     */
    public void setManagedData(FallingBlockData data)
    {
        this.managed = data.copy();
    }

    /**
     * @return The falling block data that this screen is managing.
     */
    public FallingBlockData getManagedData()
    {
        return this.managed;
    }

    /**
     * @return The falling data blocks that this screen is managing.
     */
    public ArrayList<FallingBlockData.Block> getManagedBlocks()
    {
        return this.managed.blocks;
    }

    /**
     * Replay the falling block animation.
     *
     * @param immediate Whether the animation should complete immediately.
     */
    public void replayAnimation(boolean immediate)
    {
        if (!this.isInitialAnimationFinished)
            return;

        this.blockLogo = new FallingBlockRenderer(this.managed, immediate);
    }

    /**
     * Replay the falling block animation, which is not immediate.
     */
    public void replayAnimation()
    {
        this.replayAnimation(false);
    }

    /**
     * Reload all falling block data from the config file saved on disk.
     */
    public void reloadFromDisk()
    {
        this.hasErrorOccurred = !FallingBlockConfig.read();

        if (FallingBlockConfig.isNotAvailable())
        {
            NostalgicTweaks.LOGGER.error("[Falling Blocks] The config isn't ready, this shouldn't happen!");
            this.blockLogo = new FallingBlockRenderer(new FallingBlockData());
        }
        else
        {
            if (FallingBlockConfig.hasNoBlocks())
            {
                FallingBlockConfig.setBlockDataToDefault();
                FallingBlockConfig.save();
            }

            this.initial = FallingBlockConfig.getData();
            this.managed = this.initial.copy();
            this.blockLogo = new FallingBlockRenderer(this.managed);

            this.history.setFirstPointOnTimeline(this.initial);
        }
    }

    /**
     * @return Whether changes made to the falling block data is savable.
     */
    public boolean hasChanges()
    {
        return this.areBlocksChanged;
    }

    /**
     * Save the current block data to disk.
     */
    public void save()
    {
        if (FallingBlockConfig.INSTANCE.isEmpty())
        {
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
    public void onClose()
    {
        if (!this.hasChanges())
        {
            super.onClose();
            return;
        }

        EditorOverlay.areYouSure(this);
    }
}
