package mod.adrenix.nostalgic.client.gui.screen.vanilla.world.select;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.list.RowList;
import mod.adrenix.nostalgic.mixin.access.WorldListEntryAccess;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.io.PathUtil;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.NoticeWithLinkScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.validation.ContentValidationException;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

//import mod.adrenix.nostalgic.tweak.config.CandyTweak;

class SelectWorldWidgets implements WidgetManager {
    /* Constants */

    private static final int ALPHA_BUTTON = 200;
    private static final int BETA_LARGE_BUTTON = 150;
    private static final int BETA_SMALL_BUTTON = 70;

    /* Fields */

    private final FlagHolder deleteMode = FlagHolder.off();
    private final NullableHolder<WorldRow> selected = NullableHolder.empty();
    private final NostalgicSelectWorldScreen selectWorldScreen;
    private final Minecraft minecraft;
    private final Generic layout;

    /* Constructor */

    SelectWorldWidgets(NostalgicSelectWorldScreen selectWorldScreen) {
        this.selectWorldScreen = selectWorldScreen;
        this.layout = selectWorldScreen.getLayout();
        this.minecraft = Minecraft.getInstance();
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        this.deleteMode.disable();
        this.selected.clear();

        if (this.layout == Generic.ALPHA)
            this.setAlphaLayout();
        else
            this.setBetaLayout();
    }

    /**
     * @return Whether the alpha-like screen is in delete mode.
     */
    public boolean isDeleteMode() {
        return this.deleteMode.get();
    }

    /**
     * Handles custom key press inputs for selection widgets.
     *
     * @param event The key event.
     * @return Whether this handled the key press.
     */
    public boolean keyPressed(final KeyEvent event) {
        if (this.layout == Generic.ALPHA && event.hasControlDown()) {
            if (event.key() == InputConstants.KEY_LEFT) {
                this.selectWorldScreen.moveAlphaListIndexLeft();
                return true;
            } else if (event.key() == InputConstants.KEY_RIGHT) {
                this.selectWorldScreen.moveAlphaListIndexRight();
                return true;
            }
        }

        if (this.layout == Generic.BETA && KeyboardUtil.isEnterLike(event.key())) {
            if (this.selected.isPresent())
                this.selected.ifPresent(row -> row.getEntry().joinWorld());

            return true;
        }

        return false;
    }

    /**
     * Get storage access to a level.
     *
     * @param levelSummary The {@link LevelSummary} instance.
     * @return A {@code nullable} {@link LevelStorageSource.LevelStorageAccess} instance.
     */
    @Nullable
    private LevelStorageSource.LevelStorageAccess getLevelStorageAccess(LevelSummary levelSummary) {
        String levelId = levelSummary.getLevelId();
        LevelStorageSource levelSource = this.minecraft.getLevelSource();

        try (LevelStorageSource.LevelStorageAccess levelStorageAccess = levelSource.validateAndCreateAccess(levelId)) {
            return levelStorageAccess;
        } catch (IOException exception) {
            SystemToast.onWorldAccessFailure(this.minecraft, levelId);
            NostalgicTweaks.LOGGER.error("Failed to access level (%s)\n%s", levelId, exception);
        } catch (ContentValidationException exception) {
            NostalgicTweaks.LOGGER.error("%s", exception.getMessage());
            this.minecraft.setScreen(NoticeWithLinkScreen.createWorldSymlinkWarningScreen(() -> this.minecraft.setScreen(this.selectWorldScreen)));
        }

        return null;
    }

    /**
     * Get the size of a level in megabytes.
     *
     * @param levelSummary The {@link LevelSummary} instance.
     * @return The size of the level, or 0 if no level access could be retrieved.
     */
    private Supplier<String> getLevelSize(LevelSummary levelSummary) {
        if (false/*CandyTweak.IGNORE_WORLD_SIZE.get()*/) //TODO
            return () -> "0 MB";

        LevelStorageSource.LevelStorageAccess levelStorageAccess = this.getLevelStorageAccess(levelSummary);

        if (levelStorageAccess == null)
            return () -> "0 MB";

        final CompletableFuture<Long> futureSize = PathUtil.getFutureSizeOfDirectory(levelStorageAccess.getLevelPath(LevelResource.ROOT));

        return () -> {
            if (futureSize.isDone()) {
                long sizeInBytes = futureSize.isCompletedExceptionally() ? -1L : futureSize.getNow(-1L);

                if (sizeInBytes == -1)
                    return "0 B";
                else
                    return String.format("%s", PathUtil.getFormattedFileSize(sizeInBytes));
            } else
                return String.format("%s", LoadingDotsText.get(Util.getMillis()));
        };
    }

    /**
     * Get instructions to perform when a world button is pressed in the alpha-like screen.
     *
     * @param world A {@code nullable} {@link WorldSelectionList.WorldListEntry} world instance.
     */
    private void getAlphaWorldOnPress(@Nullable WorldSelectionList.WorldListEntry world) {
        if (this.deleteMode.get()) {
            if (world != null)
                world.deleteWorld();

            this.deleteMode.disable();
            this.selectWorldScreen.moveAlphaListHome();

            return;
        }

        if (world != null)
            world.joinWorld();
        else
            CreateWorldScreen.openFresh(this.minecraft, () -> this.minecraft.setScreen(this.selectWorldScreen)); //TODO, reloadWorldList()
    }

    /**
     * Instructions to perform when the cancel button is pressed in the alpha-like screen.
     */
    private void runAlphaOnCancel() {
        if (this.deleteMode.get())
            this.deleteMode.disable();
        else
            this.selectWorldScreen.onClose();
    }

    /**
     * Check if an alpha world is disabled.
     *
     * @param world        A {@link NullableHolder} that holds a {@link WorldSelectionList.WorldListEntry}.
     * @param levelSummary A {@link NullableHolder} that holds a {@link LevelSummary}.
     * @return Whether an alpha world button is disabled.
     */
    private boolean isAlphaWorldDisabled(NullableHolder<WorldSelectionList.WorldListEntry> world, NullableHolder<LevelSummary> levelSummary) {
        if (this.deleteMode.get())
            return world.isEmpty();
        else
            return levelSummary.isPresent() && levelSummary.getOrThrow().isDisabled();
    }

    /**
     * Get the disabled alpha world tooltip.
     *
     * @param levelSummary A {@link NullableHolder} that holds a {@link LevelSummary}.
     * @return A {@link Component} message for why the alpha world button is disabled.
     */
    private Component getAlphaWorldDisabledTooltip(NullableHolder<LevelSummary> levelSummary) {
        if (this.deleteMode.get())
            return Lang.Worlds.ALPHA_CANNOT_DELETE.get();
        else {
            if (levelSummary.isEmpty())
                return Component.empty();

            return levelSummary.getOrThrow().getInfo();
        }
    }

    /**
     * Sets the widget layout for an alpha-like world selection screen.
     */
    private void setAlphaLayout() {
        final int x = (this.selectWorldScreen.width / 2) - 100;
        final int y = 40;
        final int numberOfWorlds = this.selectWorldScreen.getAllWorlds().size();
        final ArrayList<WorldSelectionList.WorldListEntry> worlds = this.selectWorldScreen.getPagedWorldsForAlpha();
        final NullableHolder<ButtonWidget> lastButton = NullableHolder.empty();

        for (int i = 0; i < 5; i++) {
            Supplier<Component> title = Lang.Worlds.ALPHA_EMPTY::get;
            NullableHolder<WorldSelectionList.WorldListEntry> world = NullableHolder.empty();
            NullableHolder<LevelSummary> levelSummary = NullableHolder.empty();

            if (i < worlds.size())
                world.set(worlds.get(i));

            if (world.isPresent()) {
                final String levelName = world.getOrThrow().getLevelName();

                levelSummary.set(((WorldListEntryAccess) (Object) world.getOrThrow()).nt$getLevelSummary());
                Supplier<String> levelSize = this.getLevelSize(levelSummary.getOrThrow());

                title = () -> Component.literal(String.format("%s (%s)", levelName, levelSize.get()));
            }

            ButtonWidget button = ButtonWidget.create(title)
                    .pos(x, y)
                    .below(lastButton.get(), 4)
                    .width(ALPHA_BUTTON)
                    .disabledTooltip(() -> this.getAlphaWorldDisabledTooltip(levelSummary), 45)
                    .disableIf(() -> this.isAlphaWorldDisabled(world, levelSummary))
                    .onPress(() -> this.getAlphaWorldOnPress(world.get()))
                    .build(this.selectWorldScreen::addWidget);

            lastButton.set(button);

            if (i == 2 && numberOfWorlds > 4) {
                ButtonWidget.create(Lang.literal("<"))
                        .leftOf(button, 4)
                        .tooltip(Lang.Tooltip.KEYBOARD_SHORTCUT, 800L, TimeUnit.MILLISECONDS)
                        .infoTooltip(Lang.Worlds.ALPHA_MOVE_LEFT, 45)
                        .enableIf(this.selectWorldScreen::canMoveAlphaLeft)
                        .onPress(this.selectWorldScreen::moveAlphaListIndexLeft)
                        .build(this.selectWorldScreen::addWidget);

                ButtonWidget.create(Lang.literal(">"))
                        .rightOf(button, 4)
                        .tooltip(Lang.Tooltip.KEYBOARD_SHORTCUT, 800L, TimeUnit.MILLISECONDS)
                        .infoTooltip(Lang.Worlds.ALPHA_MOVE_RIGHT, 45)
                        .enableIf(this.selectWorldScreen::canMoveAlphaRight)
                        .onPress(this.selectWorldScreen::moveAlphaListIndexRight)
                        .build(this.selectWorldScreen::addWidget);
            }
        }

        ButtonWidget deleteWorld = ButtonWidget.create(Lang.Worlds.ALPHA_DELETE_BUTTON)
                .posX(x)
                .below(lastButton.getOrThrow(), 16)
                .width(ALPHA_BUTTON)
                .invisibleIf(this.deleteMode::get)
                .onPress(this.deleteMode::enable)
                .build(this.selectWorldScreen::addWidget);

        ButtonWidget.create(Lang.Vanilla.GUI_CANCEL)
                .posX(x)
                .below(deleteWorld, 16)
                .width(ALPHA_BUTTON)
                .onPress(this::runAlphaOnCancel)
                .build(this.selectWorldScreen::addWidget);
    }

    /**
     * Sets the widget layout for a beta-like world selection screen.
     */
    private void setBetaLayout() {
        final int x = (this.selectWorldScreen.width / 2) - 154;
        final int listHeight = this.selectWorldScreen.height - 96;
        final ArrayList<WorldSelectionList.WorldListEntry> worlds = this.selectWorldScreen.getAllWorlds();

        RowList rowList = RowList.create()
                .posY(32)
                .height(listHeight)
                .heightOverflowMargin(2)
                .defaultRowHeight(36)
                .defaultRowWidth(220)
                .verticalMargin(0)
                .topMargin(2)
                .centerRows()
                .extendWidthToScreenEnd(0)
                .leftAlignedScrollbar(14)
                .scrollbarBackground(Color.BLACK)
                .build(this.selectWorldScreen::addWidget);

        if (false/*CandyTweak.OLD_DIRT_SCREEN_BACKGROUND.get()*/) //TODO
            rowList.getBuilder().renderTopAndBottomDirt().renderBackgroundDirt().renderBackgroundOpacity();
        else
            rowList.getBuilder().useMenuBackground();

        for (WorldSelectionList.WorldListEntry world : worlds) {
            LevelSummary summary = ((WorldListEntryAccess) (Object) world).nt$getLevelSummary();

            if (summary == null)
                continue;

            rowList.addBottomRow(new WorldRowMaker(rowList, world, summary, this.selected, this::getLevelSize).build());
        }

        ButtonWidget play = ButtonWidget.create(Lang.Vanilla.WORLD_PLAY_SELECTED)
                .posX(x)
                .below(rowList, 12)
                .width(BETA_LARGE_BUTTON)
                .enableIf(() -> this.selected.test(WorldRow::isPrimaryActionActive))
                .onPress(() -> this.selected.ifPresent(row -> row.getEntry().joinWorld()))
                .build(this.selectWorldScreen::addWidget);

        ButtonWidget rename = ButtonWidget.create(Lang.Worlds.BETA_RENAME)
                .posX(x)
                .below(play, 4)
                .width(BETA_SMALL_BUTTON)
                .enableIf(() -> this.selected.test(WorldRow::canEdit))
                .onPress(() -> this.selected.ifPresent(row -> row.getEntry().editWorld()))
                .build(this.selectWorldScreen::addWidget);

        ButtonWidget delete = ButtonWidget.create(Lang.Button.DELETE)
                .rightOf(rename, 10)
                .width(BETA_SMALL_BUTTON)
                .enableIf(() -> this.selected.test(WorldRow::canDelete))
                .onPress(() -> this.selected.ifPresent(row -> row.getEntry().deleteWorld()))
                .build(this.selectWorldScreen::addWidget);

        ButtonWidget.create(Lang.Vanilla.WORLD_CREATE)
                .rightOf(play, 8)
                .width(BETA_LARGE_BUTTON)
                .onPress(() -> CreateWorldScreen.openFresh(this.minecraft, () -> this.minecraft.setScreen(this.selectWorldScreen))) //TODO, refreshWorldList()
                .build(this.selectWorldScreen::addWidget);

        ButtonWidget.create(Lang.Vanilla.GUI_CANCEL)
                .rightOf(delete, 8)
                .width(BETA_LARGE_BUTTON)
                .onPress(this.selectWorldScreen::onClose)
                .build(this.selectWorldScreen::addWidget);
    }
}
