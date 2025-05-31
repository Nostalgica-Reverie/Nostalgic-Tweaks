package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.EnhancedScreen;
import mod.adrenix.nostalgic.client.gui.screen.home.Panorama;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.FallingBlockRenderer;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockData;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
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
            Panorama.render(graphics, partialTick);

        RenderUtil.fill(graphics, 0, 0, this.width, this.height, Color.BLACK.fromAlpha(0.85D));

        SeparatorWidget separatorTop = this.editorWidgets.getTopOfEditor();
        SeparatorWidget separatorBottom = this.editorWidgets.getBottomOfEditor();

        Rectangle topOfEditor = new Rectangle(0, 0, this.width, separatorTop.getY());
        Rectangle bottomOfEditor = new Rectangle(0, separatorBottom.getY(), this.width, this.height);

        RenderUtil.pushScissor(topOfEditor);

        this.renderDirtBackground(graphics);

        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().translate(0.0F, 1.8F, 0.0F);
        this.blockLogo.render(partialTick);
        RenderSystem.getModelViewStack().popPose();

        RenderUtil.popScissor();
        RenderUtil.pushScissor(bottomOfEditor);

        this.renderDirtBackground(graphics);

        RenderUtil.popScissor();

        super.render(graphics, mouseX, mouseY, partialTick);
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
     * Make a new copy of unique falling block data from the given data.
     *
     * @param from The {@link FallingBlockData} instance to make a copy of.
     * @return A new copy {@link FallingBlockData} that is not linked original.
     */
    public FallingBlockData makeCopyOfData(FallingBlockData from)
    {
        return from.copy();
    }

    /**
     * Make a new copy of unique falling block data from the given block data list.
     *
     * @param from A {@link ArrayList} of {@link FallingBlockData.Block}.
     * @return A new {@link ArrayList} of copied {@link FallingBlockData.Block}.
     */
    public ArrayList<FallingBlockData.Block> makeCopyOfBlocks(ArrayList<FallingBlockData.Block> from)
    {
        ArrayList<FallingBlockData.Block> copy = new ArrayList<>();

        for (FallingBlockData.Block block : from)
            copy.add(block.copy());

        return copy;
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

            this.initial = FallingBlockConfig.BLOCK_DATA.orElse(null);
            this.managed = this.makeCopyOfData(this.initial);

            this.blockLogo = new FallingBlockRenderer(this.managed);
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
        if (FallingBlockConfig.BLOCK_DATA.isEmpty())
        {
            NostalgicTweaks.LOGGER.warn("[Falling Blocks] Could not save config file due to empty block data!");
            return;
        }

        FallingBlockConfig.apply(this.managed);
        FallingBlockConfig.save();

        this.initial = FallingBlockConfig.BLOCK_DATA.orElse(null);
        this.managed = this.makeCopyOfData(this.initial);
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
