package mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.overlay.types.color.ColorPicker;
import mod.adrenix.nostalgic.client.gui.overlay.types.item.ItemPicker;
import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockConfig;
import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.config.FallingBlockData;
import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.embed.Embed;
import mod.adrenix.nostalgic.client.gui.widget.grid.Grid;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconTemplate;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.client.gui.widget.separator.SeparatorWidget;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import mod.adrenix.nostalgic.util.client.dialog.DialogType;
import mod.adrenix.nostalgic.util.client.dialog.FileDialog;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.math.Rectangle;
import mod.adrenix.nostalgic.util.common.sprite.GuiSprite;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class EditorWidgets implements WidgetManager
{
    /* Fields */

    private static final int ACTION_CAPTURE_MAX = 50;

    private final FallingBlockEditorScreen editorScreen;
    private final Supplier<ArrayList<FallingBlockData.Block>> blocks;
    private final ArrayDeque<ArrayList<FallingBlockData.Block>> history;
    private final ArrayDeque<ArrayList<FallingBlockData.Block>> redo;
    private final ArrayList<Pixel> pixels;
    private final ArrayList<Pixel> moving;

    private final Color shadow;
    private final FlagHolder sound;
    private Item block;

    private Embed canvas;
    private Grid canvasTools;
    private Grid batchEditing;
    private Grid pixelSettings;
    private SeparatorWidget topOfEditor;
    private SeparatorWidget bottomOfEditor;
    private ButtonWidget colorPicker;
    private ButtonWidget blockPicker;
    private ButtonWidget soundToggle;
    private ButtonWidget clearCanvas;
    private ButtonWidget applySelection;
    private ButtonWidget filterSelection;
    private ButtonWidget helpManual;
    private boolean isMouseClickedOnCanvas;

    /* Constructor */

    EditorWidgets(FallingBlockEditorScreen editorScreen)
    {
        this.editorScreen = editorScreen;
        this.blocks = editorScreen::getData;
        this.shadow = new Color(Color.BLACK);
        this.pixels = new ArrayList<>();
        this.moving = new ArrayList<>();
        this.history = new ArrayDeque<>();
        this.redo = new ArrayDeque<>();
        this.sound = FlagHolder.off();
        this.block = Blocks.STONE.asItem();

        ToolDrawer.reset();
    }

    /* Widgets */

    @PublicAPI
    public SeparatorWidget getTopOfEditor()
    {
        return this.topOfEditor;
    }

    @PublicAPI
    public SeparatorWidget getBottomOfEditor()
    {
        return this.bottomOfEditor;
    }

    @PublicAPI
    public Embed getCanvas()
    {
        return this.canvas;
    }

    @PublicAPI
    public ArrayList<Pixel> getPixels()
    {
        return this.pixels;
    }

    /* Methods */

    @Override
    public void init()
    {
        IntegerHolder tabOrder = IntegerHolder.create(1);

        BlankWidget topZone = BlankWidget.create()
            .heightOfScreen(0.3F)
            .extendWidthToScreenEnd(0)
            .build(this.editorScreen::addWidget);

        this.topOfEditor = SeparatorWidget.create(Color.SILVER_CHALICE)
            .height(1)
            .extendWidthToScreenEnd(0)
            .below(topZone, 3)
            .build(this.editorScreen::addWidget);

        BlankWidget.create()
            .attach(Direction.NORTH)
            .height(4)
            .extendWidthToScreenEnd(0)
            .above(this.topOfEditor, 0)
            .renderer(this::borderShadow)
            .build(this.editorScreen::addWidget);

        Embed player = Embed.create()
            .tabOrderGroup(tabOrder.getAndIncrement())
            .padding(1)
            .posX(5)
            .resizeForWidgets()
            .above(this.topOfEditor, -1)
            .borderColor(Color.TRANSPARENT)
            .backgroundColor(Color.OLIVE_BLACK)
            .borderRenderer(this::aboveEmbedController)
            .build(this.editorScreen::addWidget);

        IconTemplate.button(Icons.SMALL_PLAY, Icons.SMALL_PLAY_HOVER, Icons.SMALL_PLAY_OFF)
            .tooltip(Lang.Logo.PLAY, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.PLAY_TOOLTIP, 45)
            .skipFocusOnClick()
            .onPress(this.editorScreen::replayAnimation)
            .build(player::addWidget);

        this.canvas = Embed.create()
            .padding(0)
            .cannotFocus()
            .below(this.topOfEditor, 0)
            .backgroundColor(Color.TRANSPARENT)
            .borderColor(Color.TRANSPARENT)
            .height(this::getCanvasHeight)
            .mouseClicked(this::onCanvasMouseClicked)
            .mouseDragged(this::onCanvasMouseDragged)
            .mouseReleased(this::onCanvasMouseReleased)
            .build(this.editorScreen::addWidget);

        if (Pixel.SIZE * FallingBlockConfig.MAX_WIDTH < GuiUtil.getGuiWidth())
            this.canvas.getBuilder().centerInScreenX().resizeWidthForWidgets();
        else
            this.canvas.getBuilder().extendWidthToScreenEnd(0);

        SeparatorWidget bottomOfPixels = SeparatorWidget.create(Color.SILVER_CHALICE)
            .height(1)
            .extendWidthToScreenEnd(0)
            .below(this.canvas, 0)
            .build(this.editorScreen::addWidget);

        /* Canvas Tools */

        this.canvasTools = Grid.create(this.editorScreen, Grid::size)
            .centerInScreenX()
            .extendWidthToScreenEnd(0)
            .below(bottomOfPixels, 0)
            .columnSpacing(1)
            .build(this.editorScreen::addWidget);

        tabOrder.increment();

        for (int i = 0; i < CanvasTools.values().length; i++)
        {
            CanvasTools mode = CanvasTools.values()[i];

            ButtonWidget.create(mode.underline())
                .skipFocusOnClick()
                .attach(mode)
                .icon(mode.icon())
                .tabOrderGroup(tabOrder.get())
                .tooltip(mode.header(), 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(mode.tooltip(), 45)
                .onPress(this::onSetEditorMode)
                .backgroundRenderer(this::editorToolsBackground)
                .build(this.canvasTools::addCell);
        }

        /* Pixel Settings */

        this.pixelSettings = Grid.create(this.editorScreen, Grid::size)
            .centerInScreenX()
            .columnSpacing(1)
            .extendWidthToScreenEnd(0)
            .below(this.canvasTools, 1)
            .build(this.editorScreen::addWidget);

        ItemPicker itemPicker = ItemPicker.create(itemStack -> this.block = itemStack.getItem(), ItemRule.ONLY_BLOCKS)
            .title(Lang.Logo.SELECT_BLOCK)
            .build();

        this.blockPicker = ButtonWidget.create(Lang.Logo.BLOCK_UNDERLINE)
            .icon(() -> TextureIcon.fromItem(this.block))
            .tooltip(Lang.Logo.BLOCK, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.BLOCK_TOOLTIP, 45)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.getAndIncrement())
            .onPress(itemPicker::open)
            .backgroundRenderer(this::regularToolsBackground)
            .build(this.pixelSettings::addCell);

        this.soundToggle = ButtonWidget.create()
            .infoTooltip(Lang.Logo.SOUND_TOOLTIP, 45)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.get())
            .icon(this::getSoundIcon)
            .title(this::getSoundTitle)
            .tooltip(this::getSoundTooltip, 500L, TimeUnit.MILLISECONDS)
            .onPress(this.sound::toggle)
            .backgroundRenderer(this::regularToolsBackground)
            .build(this.pixelSettings::addCell);

        this.colorPicker = ButtonWidget.create(Lang.Logo.SHADOW)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.get())
            .icon(Icons.COLOR_WHEEL)
            .tooltip(Lang.Colorize.TITLE, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Colorize.OPEN, 45)
            .onPress(() -> ColorPicker.create(this.shadow).open())
            .backgroundRenderer(this::shadowToolBackground)
            .build(this.pixelSettings::addCell);

        /* Batch Editing & Help */

        this.batchEditing = Grid.create(this.editorScreen, Grid::size)
            .centerInScreenX()
            .columnSpacing(1)
            .extendWidthToScreenEnd(0)
            .below(this.pixelSettings, 1)
            .build(this.editorScreen::addWidget);

        this.filterSelection = ButtonWidget.create(Lang.Logo.FILTER_SELECTION_UNDERLINE)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.getAndIncrement())
            .icon(Icons.FILTER)
            .tooltip(Lang.Logo.FILTER_SELECTION, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.FILTER_SELECTION_TOOLTIP, 45)
            .onPress(this::findMatchedPixels)
            .backgroundRenderer(this::regularToolsBackground)
            .build(this.batchEditing::addCell);

        this.applySelection = ButtonWidget.create(Lang.Logo.APPLY_SELECTION_UNDERLINE)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.get())
            .icon(Icons.CLIPBOARD)
            .tooltip(Lang.Logo.APPLY_SELECTION, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.APPLY_SELECTION_TOOLTIP, 45)
            .holdFor(500L, TimeUnit.MILLISECONDS)
            .onPress(this::applySelectedPixels)
            .enableIf(this::isAnyPixelSelected)
            .backgroundRenderer(this::regularToolsBackground)
            .build(this.batchEditing::addCell);

        this.clearCanvas = ButtonWidget.create(Lang.Logo.CLEAR_CANVAS_UNDERLINE)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.get())
            .icon(Icons.TRASH_CAN)
            .tooltip(Lang.Logo.CLEAR_CANVAS, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.CLEAR_CANVAS_TOOLTIP, 45)
            .holdFor(1L, TimeUnit.SECONDS)
            .onPress(this::clear)
            .backgroundRenderer(this::regularToolsBackground)
            .build(this.batchEditing::addCell);

        this.helpManual = ButtonWidget.create(Lang.Logo.HELP_UNDERLINE)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.get())
            .icon(Icons.HELP)
            .hoverIcon(Icons.HELP_HOVER)
            .tooltip(Lang.Logo.HELP, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.HELP_TOOLTIP, 45)
            .onPress(EditorOverlay::howToUse)
            .backgroundRenderer(this::regularToolsBackground)
            .build(this.batchEditing::addCell);

        this.bottomOfEditor = SeparatorWidget.create(Color.SILVER_CHALICE)
            .below(this.batchEditing, 0)
            .extendWidthToScreenEnd(0)
            .height(1)
            .build(this.editorScreen::addWidget);

        BlankWidget.create()
            .attach(Direction.SOUTH)
            .height(4)
            .extendWidthToScreenEnd(0)
            .below(this.bottomOfEditor, 0)
            .renderer(this::borderShadow)
            .build(this.editorScreen::addWidget);

        /* Collapse & Expand */

        Embed drawer = Embed.create()
            .padding(1)
            .posX(5)
            .resizeForWidgets()
            .tabOrderGroup(tabOrder.getAndIncrement())
            .below(this.bottomOfEditor, -1)
            .borderColor(Color.TRANSPARENT)
            .backgroundColor(Color.OLIVE_BLACK)
            .invisibleIf(() -> this.canvas.getHeight() == Pixel.SIZE * FallingBlockConfig.MAX_HEIGHT)
            .borderRenderer(this::bottomEmbedController)
            .build(this.editorScreen::addWidget);

        IconWidget moveUp = IconTemplate.button(Icons.SMALL_UP_ARROW, Icons.SMALL_UP_ARROW_HOVER, Icons.SMALL_UP_ARROW_OFF)
            .tooltip(Lang.Logo.UP, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.UP_TOOLTIP, 45)
            .skipFocusOnClick()
            .onPress(this::moveDrawerUp)
            .disableIf(ToolDrawer::isClosed)
            .build(drawer::addWidget);

        IconTemplate.button(Icons.SMALL_DOWN_ARROW, Icons.SMALL_DOWN_ARROW_HOVER, Icons.SMALL_DOWN_ARROW_OFF)
            .tooltip(Lang.Logo.DOWN, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.DOWN_TOOLTIP, 45)
            .skipFocusOnClick()
            .rightOf(moveUp, 1)
            .onPress(this::moveDrawerDown)
            .disableIf(ToolDrawer::isOpen)
            .build(drawer::addWidget);

        /* Undo & Redo */

        Embed cache = Embed.create()
            .padding(1)
            .fromScreenEndX(5)
            .resizeForWidgets()
            .tabOrderGroup(tabOrder.getAndIncrement())
            .below(this.bottomOfEditor, -1)
            .borderColor(Color.TRANSPARENT)
            .backgroundColor(Color.OLIVE_BLACK)
            .borderRenderer(this::bottomEmbedController)
            .build(this.editorScreen::addWidget);

        IconWidget undo = IconTemplate.button(Icons.SMALL_UNDO, Icons.SMALL_UNDO_HOVER, Icons.SMALL_UNDO_OFF)
            .tooltip(Lang.Logo.UNDO, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.UNDO_TOOLTIP, 45)
            .skipFocusOnClick()
            .onPress(this::undoLastAction)
            .disableIf(this::isUndoEmpty)
            .build(cache::addWidget);

        IconTemplate.button(Icons.SMALL_REDO, Icons.SMALL_REDO_HOVER, Icons.SMALL_REDO_OFF)
            .tooltip(Lang.Logo.REDO, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.REDO_TOOLTIP, 45)
            .skipFocusOnClick()
            .rightOf(undo, 1)
            .onPress(this::redoLastAction)
            .disableIf(this::isRedoEmpty)
            .build(cache::addWidget);

        /* Bottom Zone */

        BlankWidget bottomZone = BlankWidget.create()
            .below(this.bottomOfEditor, 1)
            .extendWidthToScreenEnd(0)
            .extendHeightToScreenEnd(0)
            .build(this.editorScreen::addWidget);

        /* File Tools */

        Grid fileTools = Grid.create(this.editorScreen, Grid::size)
            .centerInScreenX()
            .widthOfScreen(0.75F)
            .columnSpacing(1)
            .centerInWidgetY(bottomZone, 21)
            .build(this.editorScreen::addWidget);

        ButtonTemplate.openFolder(PathUtil.getLogoPath())
            .tooltip(Lang.Logo.OPEN_FOLDER, 500L, TimeUnit.MILLISECONDS)
            .infoTooltip(Lang.Logo.OPEN_FOLDER_TOOLTIP, 45)
            .skipFocusOnClick()
            .tabOrderGroup(tabOrder.getAndIncrement())
            .build(fileTools::addCell);

        ButtonWidget.create(Lang.Logo.COPY_CANVAS)
            .icon(Icons.COPY)
            .tabOrderGroup(tabOrder.get())
            .infoTooltip(Lang.Logo.COPY_CANVAS_TOOLTIP, 45)
            .tooltip(Lang.Logo.COPY_CANVAS, 500L, TimeUnit.MILLISECONDS)
            .skipFocusOnClick()
            .onPress(this::copyCanvasToFile)
            .build(fileTools::addCell);

        ButtonWidget.create(Lang.Logo.UPLOAD_CONFIG)
            .icon(Icons.UP_ARROW)
            .tabOrderGroup(tabOrder.get())
            .infoTooltip(Lang.Logo.UPLOAD_CONFIG_TOOLTIP, 45)
            .tooltip(Lang.Logo.UPLOAD_CONFIG, 500L, TimeUnit.MILLISECONDS)
            .skipFocusOnClick()
            .onPress(this::uploadFileToCanvas)
            .build(fileTools::addCell);

        /* Save & Exit */

        Grid bottomGrid = Grid.create(this.editorScreen, Grid::size)
            .centerInScreenX()
            .widthOfScreen(0.6F)
            .columnSpacing(1)
            .below(fileTools, 1)
            .build(this.editorScreen::addWidget);

        ButtonWidget.create(Lang.Button.SAVE)
            .icon(Icons.SAVE_FLOPPY)
            .tabOrderGroup(tabOrder.getAndIncrement())
            .onPress(this.editorScreen::save)
            .enableIf(this.editorScreen::hasChanges)
            .build(bottomGrid::addCell);

        ButtonWidget.create()
            .tabOrderGroup(tabOrder.get())
            .icon(this::getFinishIcon)
            .title(this::getFinishTitle)
            .onPress(this.editorScreen::onClose)
            .build(bottomGrid::addCell);

        /* Create Pixels */

        this.populatePixelsForCanvas();
    }

    /**
     * Builds all the pixel controllers for the falling block animation editor canvas.
     */
    private void populatePixelsForCanvas()
    {
        ButtonWidget[][] pixels = new ButtonWidget[FallingBlockConfig.MAX_WIDTH][FallingBlockConfig.MAX_HEIGHT];

        this.canvas.setResizerToPaused();

        for (int x = 0; x < FallingBlockConfig.MAX_WIDTH; x++)
        {
            for (int y = 0; y < FallingBlockConfig.MAX_HEIGHT; y++)
            {
                Pixel pixel = new Pixel(x, y);
                pixels[x][y] = pixel.getButton();

                if (x == 0 && y > 0)
                    pixels[x][y].getBuilder().below(pixels[x][y - 1], 0);
                else if (x > 0)
                    pixels[x][y].getBuilder().rightOf(pixels[x - 1][y], 0);

                this.canvas.addWidget(pixels[x][y]);
                this.pixels.add(pixel);
            }
        }

        this.canvas.setResizerToResume();
    }

    /* Widget Helpers */

    /**
     * Get the height the pixel canvas should be based on screen and widget context.
     *
     * @param embed The canvas {@link Embed} instance.
     * @return The height of the pixel canvas.
     */
    private int getCanvasHeight(Embed embed)
    {
        int fullSize = Pixel.SIZE * FallingBlockConfig.MAX_HEIGHT;
        int endYLargest = CollectionUtil.filterOutByClass(this.editorScreen.getWidgetStream(), BlankWidget.class)
            .mapToInt(DynamicWidget::getEndY)
            .max()
            .orElse(0);

        boolean isWidgetOutOfBounds = endYLargest > this.editorScreen.height;
        boolean isFullSizeOutOfBounds = fullSize - embed.getHeight() + endYLargest >= this.editorScreen.height;

        if (isWidgetOutOfBounds || isFullSizeOutOfBounds)
        {
            return 56 + switch (ToolDrawer.get())
            {
                case OPEN -> 0;
                case HALF -> 21;
                case CLOSED -> 42;
            };
        }

        return fullSize;
    }

    /**
     * Instructions to perform when a canvas tool button is pressed.
     *
     * @param button A {@link ButtonWidget} instance.
     */
    private void onSetEditorMode(ButtonWidget button)
    {
        if (button.find(CanvasTools.class).noneMatch(CanvasTools::isMoveOrSelect))
            this.getSelectedPixels().forEach(Pixel::deselect);

        button.find(CanvasTools.class).findFirst().ifPresent(CanvasTools::set);
    }

    /**
     * @return The icon for the finish button.
     */
    private TextureIcon getFinishIcon()
    {
        return this.editorScreen.hasChanges() ? Icons.RED_X : Icons.GREEN_CHECK;
    }

    /**
     * @return The title for the finish button.
     */
    private Component getFinishTitle()
    {
        return this.editorScreen.hasChanges() ? Lang.Vanilla.GUI_CANCEL.get() : Lang.Vanilla.GUI_DONE.get();
    }

    /**
     * @return The icon for the sound button.
     */
    private TextureIcon getSoundIcon()
    {
        return this.sound.get() ? Icons.SOUND : Icons.SOUND_OFF;
    }

    /**
     * @return The title for the sound button.
     */
    private Component getSoundTitle()
    {
        return this.sound.get() ? Lang.Logo.SOUND_ON_UNDERLINE.get() : Lang.Logo.SOUND_OFF_UNDERLINE.get();
    }

    /**
     * @return The tooltip header for the sound button.
     */
    private Component getSoundTooltip()
    {
        return this.sound.get() ? Lang.Logo.SOUND_ON.get() : Lang.Logo.SOUND_OFF.get();
    }

    /**
     * Add the current pixel canvas state to the history queue.
     */
    private void makeHistory()
    {
        if (this.history.isEmpty() || FallingBlockConfig.isDataChanged(this.history.peekLast(), this.blocks.get()))
        {
            this.redo.clear();
            this.history.add(this.editorScreen.makeCopy(this.blocks.get()));

            if (this.history.size() > ACTION_CAPTURE_MAX)
                this.history.pop();
        }
    }

    /**
     * Clears the editor of all block data.
     */
    private void clear()
    {
        this.makeHistory();
        this.blocks.get().clear();
        this.editorScreen.replayAnimation(true);
    }

    /**
     * Save a copy of the current falling blocks config saved on disk.
     */
    private void copyCanvasToFile()
    {
        final FallingBlockData data = new FallingBlockData();
        data.blocks.addAll(this.blocks.get());

        CompletableFuture.runAsync(() -> {
            Path defaultFile = PathUtil.getLogoPath().resolve(FallingBlockConfig.COPY_NAME);
            String writeLocation = FileDialog.getJsonLocation("Copy Pixel Canvas", defaultFile, DialogType.SAVE_FILE);

            if (writeLocation != null)
            {
                FallingBlockConfig.write(data, Path.of(writeLocation).toFile());
                NostalgicTweaks.LOGGER.info("[Falling Blocks] Successfully copied pixel canvas to config file at %s", writeLocation);
            }
        });
    }

    /**
     * Uploads a falling blocks config file saved on disk to the pixel canvas.
     */
    private void uploadFileToCanvas()
    {
        CompletableFuture.supplyAsync(() -> {
            ArrayList<FallingBlockData.Block> blocks = new ArrayList<>();

            Path defaultFile = PathUtil.getLogoPath().resolve(" ");
            String readLocation = FileDialog.getJsonLocation("Upload Falling Block Config", defaultFile, DialogType.OPEN_FILE);

            if (readLocation != null)
                FallingBlockConfig.upload(Path.of(readLocation).toFile(), blocks);

            return blocks;
        }).whenCompleteAsync((blocks, throwable) -> Minecraft.getInstance().execute(() -> {
            if (throwable != null)
            {
                EditorOverlay.couldNotReadConfig();
                NostalgicTweaks.LOGGER.error("[Falling Blocks] An error occurred when trying to read uploaded file\n%s", throwable);
            }
            else if (blocks != null && !blocks.isEmpty())
            {
                this.makeHistory();
                this.blocks.get().clear();
                this.blocks.get().addAll(blocks);
                this.editorScreen.replayAnimation();
            }
        }));
    }

    /**
     * Moves the drawer up a level and readjusts editor widgets.
     */
    private void moveDrawerUp()
    {
        ToolDrawer.up();
        this.collapseOrExpand();
    }

    /**
     * Moves the drawer down a level and readjusts editor widgets.
     */
    private void moveDrawerDown()
    {
        ToolDrawer.down();
        this.collapseOrExpand();
    }

    /**
     * Collapse or expand the canvas button tools drawer.
     */
    private void collapseOrExpand()
    {
        switch (ToolDrawer.get())
        {
            case OPEN ->
            {
                this.pixelSettings.setVisible();
                this.batchEditing.setVisible();
                this.bottomOfEditor.getBuilder().below(this.batchEditing, 0);
            }
            case HALF ->
            {
                this.pixelSettings.setVisible();
                this.batchEditing.setInvisible();
                this.bottomOfEditor.getBuilder().below(this.pixelSettings, 0);
            }
            case CLOSED ->
            {
                this.pixelSettings.setInvisible();
                this.batchEditing.setInvisible();
                this.bottomOfEditor.getBuilder().below(this.canvasTools, 0);
            }
        }
    }

    /**
     * Undo a previous action done to the pixel canvas.
     */
    private void undoLastAction()
    {
        if (this.history.isEmpty())
            return;

        this.redo.add(this.editorScreen.makeCopy(this.blocks.get()));

        if (this.redo.size() > ACTION_CAPTURE_MAX)
            this.redo.pop();

        ArrayList<FallingBlockData.Block> last = this.history.pollLast();

        if (last != null)
        {
            this.blocks.get().clear();
            this.blocks.get().addAll(this.editorScreen.makeCopy(last));
            this.editorScreen.replayAnimation(true);
        }
    }

    /**
     * Redo a previously undone action.
     */
    private void redoLastAction()
    {
        if (this.redo.isEmpty())
            return;

        this.history.add(this.editorScreen.makeCopy(this.blocks.get()));

        if (this.history.size() > ACTION_CAPTURE_MAX)
            this.history.pop();

        ArrayList<FallingBlockData.Block> last = this.redo.pollLast();

        if (last != null)
        {
            this.blocks.get().clear();
            this.blocks.get().addAll(this.editorScreen.makeCopy(last));
            this.editorScreen.replayAnimation(true);
        }
    }

    /**
     * @return Whether there is nothing to undo.
     */
    private boolean isUndoEmpty()
    {
        return this.history.isEmpty();
    }

    /**
     * @return Whether there is nothing to redo.
     */
    private boolean isRedoEmpty()
    {
        return this.redo.isEmpty();
    }

    /* Pixel Canvas Events */

    /**
     * Check if a valid canvas tool is active and if the canvas is being mouse dragged.
     *
     * @param mouseX The current x-position of the mouse.
     * @param mouseY The current y-position of the mouse.
     * @param button The mouse button that was clicked.
     * @return Whether the canvas is being mouse dragged.
     */
    boolean isCanvasDragged(double mouseX, double mouseY, int button)
    {
        return CanvasTools.get().isDraggable() && this.canvas.isWidgetClicked(mouseX, mouseY, button);
    }

    /**
     * Instructions for when the mouse is clicked on the canvas.
     *
     * @return Whether this manager handled the mouse click event.
     */
    boolean onCanvasMouseClicked(double mouseX, double mouseY, int button)
    {
        if (!this.canvas.isValidClick(mouseX, mouseY, button))
        {
            if (CanvasTools.get() == CanvasTools.SELECT)
            {
                boolean isMovingClicked = this.editorScreen.getVisibleWidgets()
                    .anyMatch(widget -> widget.has(CanvasTools.MOVEIT) && widget.isMouseOver(mouseX, mouseY));

                if (!isMovingClicked)
                    this.pixels.forEach(Pixel::deselect);
            }

            return false;
        }

        this.isMouseClickedOnCanvas = true;

        if (CanvasTools.get() == CanvasTools.SELECT)
        {
            if (!Screen.hasControlDown() && !Screen.hasShiftDown())
                this.pixels.forEach(Pixel::deselect);

            if (!Screen.hasShiftDown())
                this.getPixelAtMouse(mouseX, mouseY).ifPresent(Pixel::select);
            else
                this.getPixelAtMouse(mouseX, mouseY).ifPresent(Pixel::deselect);
        }

        if (CanvasTools.get() == CanvasTools.MOVEIT)
        {
            this.makeHistory();
            this.getSelectedPixels().forEach(Pixel::prepareForMovement);
            this.movePixelsByMouse(mouseX, mouseY);
        }

        return false;
    }

    /**
     * Instructions for when the mouse is dragged on the canvas.
     *
     * @return Whether this manager handled the mouse drag event.
     */
    boolean onCanvasMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (this.isCanvasDragged(mouseX, mouseY, button) && this.isMouseClickedOnCanvas)
        {
            if (CanvasTools.get() == CanvasTools.MOVEIT)
            {
                this.movePixelsByMouse(mouseX, mouseY);

                return true;
            }

            final Rectangle fromMouse = Rectangle.fromPoint((int) mouseX, (int) mouseY);

            this.pixels.stream().filter(pixel -> pixel.intersects(fromMouse)).forEach(pixel -> {
                switch (CanvasTools.get())
                {
                    case DRAW -> pixel.draw();
                    case ERASER -> pixel.erase();
                    case SELECT ->
                    {
                        if (Screen.hasShiftDown())
                            pixel.deselect();
                        else
                            pixel.select();
                    }
                }
            });

            return true;
        }

        return false;
    }

    /**
     * Instructions for when the mouse is released in the canvas.
     *
     * @return Whether this manager handled the mouse release event.
     */
    boolean onCanvasMouseReleased(double mouseX, double mouseY, int button)
    {
        if (CollectionUtil.isNotEmpty(this.moving))
        {
            this.getSelectedPixels().forEach(Pixel::deselect);

            this.moving.forEach(Pixel::applyChangeInMovement);
            this.moving.clear();
        }

        if (this.isCanvasDragged(mouseX, mouseY, button))
            this.editorScreen.replayAnimation(true);

        this.isMouseClickedOnCanvas = false;

        return false;
    }

    /**
     * Instructions for when a keyboard key is pressed by the editor screen.
     *
     * @return Whether this manager handled the key press event.
     */
    boolean keyPressed(int keyCode)
    {
        return switch (keyCode)
        {
            case InputConstants.KEY_Q ->
            {
                if (this.canvas.isFocused())
                    this.canvas.setOverrideFocused(false);
                else
                {
                    this.canvas.setOverrideFocused(true);
                    this.canvas.getVisibleWidgets()
                        .filter(DynamicWidget::isFocused)
                        .findFirst()
                        .ifPresentOrElse(this.canvas::setScrollOn, () -> CollectionUtil.first(this.canvas.getVisibleWidgets())
                            .ifPresent(DynamicWidget::setFocused));
                }

                yield true;
            }
            case InputConstants.KEY_Z ->
            {
                if (Screen.hasControlDown())
                {
                    this.undoLastAction();
                    yield true;
                }

                yield false;
            }
            case InputConstants.KEY_Y ->
            {
                if (Screen.hasControlDown())
                {
                    this.redoLastAction();
                    yield true;
                }

                yield false;
            }
            case InputConstants.KEY_D ->
            {
                CanvasTools.set(CanvasTools.DRAW);
                yield true;
            }
            case InputConstants.KEY_K ->
            {
                CanvasTools.set(CanvasTools.PICK);
                yield true;
            }
            case InputConstants.KEY_S ->
            {
                if (Screen.hasControlDown())
                {
                    if (this.editorScreen.hasChanges())
                        this.editorScreen.save();
                }
                else
                    CanvasTools.set(CanvasTools.SELECT);

                yield true;
            }
            case InputConstants.KEY_M ->
            {
                CanvasTools.set(CanvasTools.MOVEIT);
                yield true;
            }
            case InputConstants.KEY_E ->
            {
                CanvasTools.set(CanvasTools.ERASER);
                yield true;
            }
            case InputConstants.KEY_B ->
            {
                this.blockPicker.onPress();
                yield true;
            }
            case InputConstants.KEY_O ->
            {
                if (Screen.hasControlDown())
                {
                    Util.getPlatform().openFile(PathUtil.getLogoPath().toFile());
                    yield true;
                }

                yield false;
            }
            case InputConstants.KEY_U ->
            {
                if (Screen.hasControlDown())
                    this.uploadFileToCanvas();
                else
                    this.soundToggle.onPress();

                yield true;
            }
            case InputConstants.KEY_C ->
            {
                if (Screen.hasControlDown())
                    this.copyCanvasToFile();
                else
                    this.colorPicker.onPress();

                yield true;
            }
            case InputConstants.KEY_F ->
            {
                this.filterSelection.onPress();
                yield true;
            }
            case InputConstants.KEY_A ->
            {
                this.applySelection.onPress();
                yield true;
            }
            case InputConstants.KEY_R ->
            {
                this.clearCanvas.onPress();
                yield true;
            }
            case InputConstants.KEY_H ->
            {
                this.helpManual.onPress();
                yield true;
            }
            case InputConstants.KEY_P ->
            {
                this.editorScreen.replayAnimation();
                yield true;
            }
            case InputConstants.KEY_PAGEDOWN ->
            {
                this.moveDrawerDown();
                yield true;
            }
            case InputConstants.KEY_PAGEUP ->
            {
                this.moveDrawerUp();
                yield true;
            }
            case InputConstants.KEY_DELETE ->
            {
                if (CanvasTools.get().equals(CanvasTools.SELECT) && this.isAnyPixelSelected())
                {
                    this.getSelectedPixels().forEach(Pixel::erase);
                    yield true;
                }

                yield false;
            }
            case InputConstants.KEY_ESCAPE ->
            {
                if (this.isAnyPixelSelected())
                {
                    this.getSelectedPixels().forEach(Pixel::deselect);
                    yield true;
                }

                yield false;
            }
            default -> false;
        };
    }

    /* Widget Renderers */

    /**
     * Renders a border shadow for the pixel canvas.
     */
    private void borderShadow(BlankWidget widget, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        int x0 = widget.getX();
        int y0 = widget.getY();
        int x1 = widget.getEndX();
        int y1 = widget.getEndY();

        if (widget.has(Direction.NORTH))
            RenderUtil.fromTopGradient(graphics, x0, y0, x1, y1, Color.BLACK.fromAlpha(0), Color.BLACK);
        else
            RenderUtil.fromTopGradient(graphics, x0, y0, x1, y1, Color.BLACK, Color.BLACK.fromAlpha(0));
    }

    /**
     * Renders a custom translucent background color for the shadow tool editor button.
     */
    private void shadowToolBackground(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isActive = button.isHoveredOrFocused();

        Color outline = isActive ? Color.CADET_GRAY.fromAlpha(0.2D).brighten(0.2D) : Color.CADET_GRAY.fromAlpha(0.2D);
        Color fill = isActive ? this.shadow.brighten(0.2D) : this.shadow;

        int x = button.getX();
        int y = button.getY();
        int w = button.getWidth();
        int h = button.getHeight();

        RenderUtil.outline(graphics, x, y, w, h, outline);
        RenderUtil.fill(graphics, x + 1, y + 1, x + w - 1, y + h - 1, fill.fromAlpha(Math.min(0.4D, fill.getFloatAlpha())));
    }

    /**
     * Button background rendering for the pixel editing tools.
     */
    private void editorToolsBackground(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Color line = button.has(CanvasTools.get()) ? Color.ORANGE : Color.TRANSPARENT;
        Color fill = button.has(CanvasTools.get()) ? Color.COPPER_RED.fromAlpha(0.4D) : Color.CADET_GRAY.fromAlpha(0.2D);

        if (button.isHoveredOrFocused())
            fill = fill.brighten(0.2D);

        RenderUtil.fill(graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), fill);
        RenderUtil.hLine(graphics, button.getX(), button.getEndY() - 1, button.getEndX(), line);
    }

    /**
     * Button background rendering for the pixel drawing tools.
     */
    private void regularToolsBackground(ButtonWidget button, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        Color fill = Color.CADET_GRAY.fromAlpha(0.2D);

        if (button.isHoveredOrFocused())
            fill = fill.brighten(0.2D);

        RenderUtil.fill(graphics, button.getX(), button.getY(), button.getEndX(), button.getEndY(), fill);
    }

    /**
     * Embed background rendering for embeds above the pixel canvas.
     */
    private void aboveEmbedController(Embed embed, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderUtil.vLine(graphics, embed.getX() - 1, embed.getY() - 1, embed.getEndY(), Color.SILVER_CHALICE);
        RenderUtil.vLine(graphics, embed.getEndX(), embed.getY() - 1, embed.getEndY(), Color.SILVER_CHALICE);
        RenderUtil.hLine(graphics, embed.getX(), embed.getY() - 1, embed.getEndX(), Color.SILVER_CHALICE);
    }

    /**
     * Embed background rendering for embeds below the pixel canvas.
     */
    private void bottomEmbedController(Embed embed, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderUtil.vLine(graphics, embed.getX() - 1, embed.getY(), embed.getEndY() + 1, Color.SILVER_CHALICE);
        RenderUtil.vLine(graphics, embed.getEndX(), embed.getY(), embed.getEndY() + 1, Color.SILVER_CHALICE);
        RenderUtil.hLine(graphics, embed.getX(), embed.getEndY(), embed.getEndX(), Color.SILVER_CHALICE);
    }

    /* Selection Helpers */

    /**
     * Find a pixel at the given mouse coordinate.
     *
     * @param mouseX The mouse x-coordinate.
     * @param mouseY The mouse y-coordinate.
     * @return A {@link Optional} {@link Pixel} at the given mouse coordinate.
     */
    Optional<Pixel> getPixelAtMouse(double mouseX, double mouseY)
    {
        return this.pixels.stream().filter(pixel -> pixel.isMouseAt(mouseX, mouseY)).findFirst();
    }

    /**
     * Find a pixel at a particular coordinate relative to logo placement.
     *
     * @param x The x-coordinate relative to the pixel canvas.
     * @param y The y-coordinate relative to the pixel canvas.
     * @return An {@link Optional} {@link Pixel} at the given coordinate relative to logo placement.
     */
    Optional<Pixel> getPixelAt(int x, int y)
    {
        return this.pixels.stream().filter(pixel -> pixel.isAt(x, y)).findFirst();
    }

    /**
     * Find a selected pixel at a particular coordinate relative to logo placement.
     *
     * @param x The x-coordinate relative to the pixel canvas.
     * @param y The y-coordinate relative to the pixel canvas.
     * @return An {@link Optional} {@link Pixel} at the given coordinate relative to logo placement.
     */
    Optional<Pixel> getSelectedPixelAt(int x, int y)
    {
        return this.getPixelAt(x, y).stream().filter(Pixel::isSelected).findFirst();
    }

    /**
     * @return A {@link Stream} of any pixels that are {@link Pixel#isSelected()}.
     */
    Stream<Pixel> getSelectedPixels()
    {
        return this.pixels.stream().filter(Pixel::isSelected);
    }

    /**
     * @return Whether any pixels are currently selected.
     */
    boolean isAnyPixelSelected()
    {
        return this.pixels.stream().anyMatch(Pixel::isSelected);
    }

    /**
     * Offset the selection using the given mouse coordinates.
     *
     * @param mouseX The x-coordinate of the mouse.
     * @param mouseY The y-coordinate of the mouse.
     */
    void movePixelsByMouse(double mouseX, double mouseY)
    {
        IntegerHolder offsetX = IntegerHolder.create(0);
        IntegerHolder offsetY = IntegerHolder.create(0);

        this.getPixelAtMouse(mouseX, mouseY).ifPresent(pixel -> CollectionUtil.first(this.moving).ifPresent(first -> {
            offsetX.set(pixel.x - first.moveX);
            offsetY.set(pixel.y - first.moveY);
        }));

        for (Pixel pixel : this.moving)
        {
            int dx = pixel.moveX + offsetX.get();
            int dy = pixel.moveY + offsetY.get();

            boolean isXOutOfBounds = dx < 0 || dx >= FallingBlockConfig.MAX_WIDTH;
            boolean isYOutOfBounds = dy < 0 || dy >= FallingBlockConfig.MAX_HEIGHT;

            if (isXOutOfBounds || isYOutOfBounds)
                return;
        }

        this.moving.forEach(pixel -> {
            pixel.moveX += offsetX.get();
            pixel.moveY += offsetY.get();
        });
    }

    /**
     * Find pixels that match the current editor tool settings.
     */
    void findMatchedPixels()
    {
        this.pixels.forEach(pixel -> {
            pixel.deselect();

            pixel.getData().ifPresent(block -> {
                boolean blockId = ItemUtil.getResourceKey(this.block).equals(block.getBlockId());
                boolean color = HexUtil.parseInt(block.getShadowColor()) == this.shadow.get();
                boolean sound = block.hasSound() == this.sound.get();

                if (blockId && color && sound)
                    pixel.select();
            });
        });
    }

    /**
     * Apply the current editor settings to all selected pixels.
     */
    void applySelectedPixels()
    {
        this.makeHistory();
        this.getSelectedPixels().forEach(Pixel::draw);
        this.editorScreen.replayAnimation(true);
    }

    /* Canvas Pixels */

    /**
     * Helper class for pixel controller.
     */
    public class Pixel
    {
        /* Static */

        public static final int SIZE = 10;

        /* Fields */

        private final ButtonWidget button;
        private final int x;
        private final int y;
        private boolean selected;

        private final NullableHolder<FallingBlockData.Block> moveData;
        private boolean moving;
        private int moveX;
        private int moveY;

        /* Constructor */

        public Pixel(int x, int y)
        {
            this.x = x;
            this.y = y;
            this.moveData = NullableHolder.empty();
            this.button = ButtonWidget.create()
                .skipFocusOnClick()
                .noClickSound()
                .size(SIZE)
                .onPress(this::onPress)
                .renderer(this::render)
                .build();
        }

        /* Setters & Getters */

        /**
         * @return The x-coordinate relative to the pixel map.
         */
        public int getX()
        {
            return this.x;
        }

        /**
         * @return The y-coordinate relative to the pixel map.
         */
        public int getY()
        {
            return this.y;
        }

        /**
         * Mark this pixel is selected by the current selection region.
         */
        public void select()
        {
            this.selected = true;
        }

        /**
         * Unmark this pixel as selected.
         */
        public void deselect()
        {
            this.selected = false;
        }

        /**
         * @return Whether this pixel was captured during select mode.
         */
        public boolean isSelected()
        {
            return this.selected;
        }

        /**
         * @return Whether the given coordinate is equivalent to this pixel's coordinate.
         */
        public boolean isAt(int x, int y)
        {
            return this.x == x && this.y == y;
        }

        /**
         * Check if the given mouse coordinate is at this pixel.
         *
         * @param mouseX The mouse x-coordinate.
         * @param mouseY The mouse y-coordinate.
         * @return Whether the mouse is at this pixel.
         */
        public boolean isMouseAt(double mouseX, double mouseY)
        {
            return this.intersects(Rectangle.fromPoint((int) mouseX, (int) mouseY));
        }

        /**
         * Check if this pixel is within the given region.
         *
         * @param rectangle The {@link Rectangle} to check for intersection against.
         * @return Whether this pixel intersects the given area.
         */
        public boolean intersects(Rectangle rectangle)
        {
            return Rectangle.intersect(rectangle, this.button.getPositionRectangle());
        }

        /* Methods */

        /**
         * Prepare a pixel for movement. This will erase this pixel's current falling block data.
         */
        public void prepareForMovement()
        {
            EditorWidgets.this.moving.add(this);

            this.moveData.set(this.getData().orElse(null));

            this.moveX = this.x;
            this.moveY = this.y;
            this.moving = true;

            this.erase();
        }

        /**
         * Apply changes in pixel movement data. This will override any previous pixel data it may be over.
         */
        public void applyChangeInMovement()
        {
            EditorWidgets.this.getPixelAt(this.moveX, this.moveY).ifPresent(pixel -> {
                pixel.selected = true;

                pixel.getData().ifPresentOrElse(block -> this.moveData.ifPresent(data -> {
                    block.setBlockId(data.getBlockId());
                    block.setShadowColor(data.getShadowColor());
                    block.setSound(data.hasSound());
                }), () -> this.moveData.ifPresent(data -> {
                    String blockId = data.getBlockId();
                    String shadow = data.getShadowColor();
                    boolean sound = data.hasSound();

                    EditorWidgets.this.blocks.get()
                        .add(new FallingBlockData.Block(this.moveX, this.moveY, blockId, shadow, sound));
                }));
            });

            this.moveData.clear();
            this.moving = false;
        }

        /**
         * Fill this pixel's data with the current state of the canvas' drawing tools.
         */
        void draw()
        {
            final String blockId = ItemUtil.getResourceKey(EditorWidgets.this.block);
            final String shadow = HexUtil.parseString(EditorWidgets.this.shadow.getIntComponents());
            final boolean sound = EditorWidgets.this.sound.get();

            EditorWidgets.this.blocks.get()
                .stream()
                .filter(block -> block.at(this.x, this.y))
                .findFirst()
                .ifPresentOrElse(block -> {
                    block.setBlockId(blockId);
                    block.setShadowColor(shadow);
                    block.setSound(sound);
                }, () -> EditorWidgets.this.blocks.get()
                    .add(new FallingBlockData.Block(this.x, this.y, blockId, shadow, sound)));
        }

        /**
         * Copies pixel data to the canvas' drawing tools.
         */
        void pick()
        {
            Optional<FallingBlockData.Block> data = this.getData();

            if (data.isEmpty())
                return;

            EditorWidgets.this.block = ItemUtil.getItem(data.get().getBlockId());

            EditorWidgets.this.sound.set(data.get().hasSound());
            EditorWidgets.this.shadow.set(HexUtil.parseInt(data.get().getShadowColor()));
            EditorWidgets.this.shadow.setAlpha(HexUtil.parseFloatRGBA(data.get().getShadowColor())[3]);
        }

        /**
         * Removes this pixel's data from the managed block data.
         */
        void erase()
        {
            EditorWidgets.this.blocks.get().removeIf(block -> block.at(this.x, this.y));
        }

        /**
         * @return The {@link FallingBlockData.Block} data, if it is present.
         */
        Optional<FallingBlockData.Block> getData()
        {
            return EditorWidgets.this.blocks.get().stream().filter(block -> block.at(this.x, this.y)).findFirst();
        }

        /**
         * Instructions for when the pixel is pressed.
         */
        private void onPress()
        {
            CanvasTools tool = CanvasTools.get();

            if (CanvasTools.DRAW == tool || CanvasTools.ERASER == tool)
                EditorWidgets.this.makeHistory();

            switch (tool)
            {
                case DRAW -> this.draw();
                case PICK -> this.pick();
                case ERASER -> this.erase();
            }

            if (CanvasTools.DRAW == tool || CanvasTools.ERASER == tool)
                EditorWidgets.this.editorScreen.replayAnimation(true);
        }

        /**
         * Get a pixel color based on context and canvas coordinate.
         *
         * @param x The canvas x-coordinate.
         * @param y The canvas y-coordinate.
         * @return The pixel's current {@link Color} at the given coordinate on the canvas.
         */
        public Color getColor(int x, int y)
        {
            boolean isOdd = MathUtil.isOdd(x) && MathUtil.isOdd(y);
            boolean isEven = MathUtil.isEven(x) && MathUtil.isEven(y);
            boolean isFirst = isOdd || isEven;

            Color color = isFirst ? new Color(0xDDDDDD) : new Color(0x999999);

            if (this.selected)
                color = isFirst ? Color.FRENCH_SKY_BLUE : Color.IRIS_BLUE;

            if (this.button.isHoveredOrFocused())
                color = Color.LEMON_YELLOW;

            return color.fromAlpha(0.2D);
        }

        /**
         * Renderer helper method for rendering the contents of this pixel using the {@link ButtonWidget}.
         */
        private void render(ButtonWidget base, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            FallingBlockData.Block data = this.moveData.orElse(this.getData().orElse(null));

            ButtonWidget active = !this.moving ? base : EditorWidgets.this.getPixelAt(this.moveX, this.moveY)
                .map(Pixel::getButton)
                .orElse(base);

            RenderUtil.fill(graphics, base.getX(), base.getY(), base.getEndX(), base.getEndY(), this.getColor(this.x, this.y));

            if (this.moving)
                RenderUtil.fill(graphics, active.getX(), active.getY(), active.getEndX(), active.getEndY(), this.getColor(this.moveX, this.moveY));

            if (data != null)
            {
                int[] rgba = HexUtil.parseRGBA(data.getShadowColor());
                Color shadow = new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
                ItemStack itemStack = ItemUtil.getItemStack(data.getBlockId());
                Optional<GuiSprite> notes = Icons.MUSIC_SINGLE.getSprite();

                graphics.pose().pushPose();
                graphics.pose().translate(active.getX() + 1.0F, active.getY() + 0.5F, 0.0F);
                graphics.pose().scale(0.5F, 0.5F, 0.5F);
                RenderUtil.renderItem(graphics, itemStack, 0, 0);
                graphics.pose().popPose();

                RenderUtil.hLine(graphics, active.getX(), active.getEndY() - 1, active.getEndX(), shadow);

                if (data.hasSound() && notes.isPresent())
                {
                    graphics.pose().pushPose();
                    graphics.pose().translate(0.0F, 0.0F, 20.0F);
                    RenderUtil.blitSprite(notes.get(), graphics, 0.5F, active.getX(), active.getY(), Icons.MUSIC_SINGLE.getWidth(), Icons.MUSIC_SINGLE.getHeight());
                    graphics.pose().popPose();
                }
            }

            if (this.selected)
                this.drawSelectionBorder(active, graphics);

            if (active.isFocused())
                RenderUtil.outline(graphics, active.getX(), active.getY(), active.getWidth(), active.getHeight(), Color.MAYA_BLUE);
        }

        /**
         * Renderer helper method for rendering a selection border, if possible.
         */
        private void drawSelectionBorder(ButtonWidget button, GuiGraphics graphics)
        {
            Color color = Color.AZURE_WHITE;

            int startX = button.getX();
            int startY = button.getY();
            int endX = button.getEndX();
            int endY = button.getEndY();

            if (EditorWidgets.this.getSelectedPixelAt(this.x, this.y - 1).isEmpty())
                RenderUtil.hLine(graphics, startX, startY, endX, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x, this.y + 1).isEmpty())
                RenderUtil.hLine(graphics, startX, endY - 1, endX, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x + 1, this.y).isEmpty())
                RenderUtil.vLine(graphics, endX - 1, startY, endY, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x - 1, this.y).isEmpty())
                RenderUtil.vLine(graphics, startX, startY, endY, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x + 1, this.y - 1).isEmpty())
                RenderUtil.fill(graphics, endX - 1, startY, endX, startY + 1, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x - 1, this.y - 1).isEmpty())
                RenderUtil.fill(graphics, startX, startY, startX + 1, startY + 1, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x - 1, this.y + 1).isEmpty())
                RenderUtil.fill(graphics, startX, endY - 1, startX + 1, endY, color);

            if (EditorWidgets.this.getSelectedPixelAt(this.x + 1, this.y + 1).isEmpty())
                RenderUtil.fill(graphics, endX - 1, endY - 1, endX, endY, color);
        }

        /**
         * @return A {@link ButtonWidget} that will be used for pixel management.
         */
        public ButtonWidget getButton()
        {
            return this.button;
        }
    }
}
