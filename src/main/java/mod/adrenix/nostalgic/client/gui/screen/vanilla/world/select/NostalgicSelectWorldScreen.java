package mod.adrenix.nostalgic.client.gui.screen.vanilla.world.select;

import mod.adrenix.nostalgic.client.gui.screen.DynamicScreen;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.mixin.access.SelectWorldScreenAccess;
import mod.adrenix.nostalgic.mixin.access.WorldSelectionListAccess;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.common.array.UniqueArrayList;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.text.TextUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.LevelSummary;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import mod.adrenix.nostalgic.tweak.config.CandyTweak;

public class NostalgicSelectWorldScreen extends SelectWorldScreen implements DynamicScreen<NostalgicSelectWorldScreen> {
    /* Fields */

    private final UniqueArrayList<DynamicWidget<?, ?>> widgets;
    private final SelectWorldWidgets selectWorldWidgets;
    private final Screen parentScreen;
    private final Generic layout;
    private final String title;
    private WorldSelectionList selectionList;
    private boolean isListLoaded;
    private int alphaListIndex;

    /* Constructor */

    /**
     * Create a new {@link NostalgicSelectWorldScreen} instance.
     *
     * @param parentScreen The parent {@link Screen} to return to when this screen is closed.
     */
    public NostalgicSelectWorldScreen(Screen parentScreen) {
        super(parentScreen);

        this.layout = Generic.BETA;//CandyTweak.OLD_WORLD_SELECT_SCREEN.get(); //TODO
        this.widgets = new UniqueArrayList<>();
        this.selectWorldWidgets = new SelectWorldWidgets(this);
        this.parentScreen = parentScreen;
        this.title = switch (this.layout) {
            case ALPHA -> TextUtil.uppercaseFirstLetter(Lang.Vanilla.WORLD_SELECT_TITLE.getString().toLowerCase());
            case BETA, MODERN -> Lang.Vanilla.WORLD_SELECT_TITLE.getString();
        };
    }

    /* Methods */

    /**
     * @return The {@link Generic} layout being used by the nostalgic world selection screen.
     */
    public Generic getLayout() {
        return this.layout;
    }

    /**
     * @return The {@link WorldSelectionList} being used by the nostalgic world selection screen.
     */
    public WorldSelectionList getSelectionList() {
        return this.selectionList;
    }

    /**
     * @return The {@link WorldSelectionListAccess} made available to {@link WorldSelectionList}.
     */
    public WorldSelectionListAccess getAccessToSelectionList() {
        return (WorldSelectionListAccess) this.getSelectionList();
    }

    /**
     * @return An {@link ArrayList} of all {@link WorldSelectionList.WorldListEntry} instances.
     */
    public ArrayList<WorldSelectionList.WorldListEntry> getAllWorlds() {
        ArrayList<WorldSelectionList.WorldListEntry> entries = new ArrayList<>();

        for (Object entry : this.selectionList.children()) {
            if (entry instanceof WorldSelectionList.WorldListEntry worldEntry)
                entries.add(worldEntry);
        }

        return entries;
    }

    /**
     * @return An {@link ArrayList} of 5 {@link WorldSelectionList.WorldListEntry} instances to use for alpha buttons.
     */
    public ArrayList<WorldSelectionList.WorldListEntry> getPagedWorldsForAlpha() {
        return this.getAllWorlds()
                .stream()
                .skip(this.alphaListIndex * 5L)
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * @return Whether the alpha-like world list can move left.
     */
    public boolean canMoveAlphaLeft() {
        return this.alphaListIndex > 0;
    }

    /**
     * @return Whether the alpha-like world list can move right.
     */
    public boolean canMoveAlphaRight() {
        int listed = this.getPagedWorldsForAlpha().size();

        if (listed == 0)
            return false;

        return this.alphaListIndex < this.getMaxAlphaListIndex() - 1 || listed % 5 == 0;
    }

    /**
     * @return The maximum list index allowed for the alpha world select pagination.
     */
    public int getMaxAlphaListIndex() {
        return (int) Math.ceil(this.getAllWorlds().size() / 5.0D);
    }

    /**
     * Move the alpha screen list index to the left.
     */
    public void moveAlphaListIndexLeft() {
        if (this.canMoveAlphaLeft()) {
            this.alphaListIndex = Math.max(this.alphaListIndex - 1, 0);
            this.refreshWidgets();
        }
    }

    /**
     * Move the alpha screen list index to the right.
     */
    public void moveAlphaListIndexRight() {
        if (this.canMoveAlphaRight()) {
            this.alphaListIndex = Math.min(this.alphaListIndex + 1, this.getMaxAlphaListIndex());
            this.refreshWidgets();
        }
    }

    /**
     * Move the alpha screen list index back to the home position.
     */
    public void moveAlphaListHome() {
        this.alphaListIndex = 0;
    }

    /**
     * Refresh the world selection screen widgets.
     */
    public void refreshWidgets() {
        this.clearWidgets();
        this.selectWorldWidgets.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        super.init();

        this.clearWidgets();

        if (this.minecraft == null)
            return;

        this.alphaListIndex = 0;
        this.isListLoaded = false;
        this.selectionList = ((SelectWorldScreenAccess) this).nt$getList();
        this.selectWorldWidgets.init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NostalgicSelectWorldScreen self() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueArrayList<DynamicWidget<?, ?>> getWidgets() {
        return this.widgets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable Screen getParentScreen() {
        return this.parentScreen;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClose() {
        if (this.minecraft != null)
            this.minecraft.setScreen(this.parentScreen);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (this.selectWorldWidgets.keyPressed(event))
            return true;

        return super.keyPressed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        WorldSelectionListAccess worldListAccess = (WorldSelectionListAccess) this.selectionList;
        List<LevelSummary> worldList = worldListAccess.nt$pollLevelsIgnoreErrors();

        if (worldList != worldListAccess.nt$getCurrentlyDisplayedLevels())
            worldListAccess.nt$handleNewLevels(worldList);

        if (this.selectionList.children().contains(this.getAccessToSelectionList().nt$getLoadingHeader())) {
            int y = this.height / 2 - 20;

            DrawText.begin(graphics, Lang.Vanilla.WORLD_LOADING_LIST.get()).pos(this.width / 2, y).center().draw();
            DrawText.begin(graphics, LoadingDotsText.get(Util.getMillis())).pos(this.width / 2, y + 11).center().draw();
        } else {
            if (!this.isListLoaded) {
                this.isListLoaded = true;
                this.refreshWidgets();
            }

            int maxAlphaListIndex = this.getMaxAlphaListIndex();
            int y = 20;

            String title = this.selectWorldWidgets.isDeleteMode() ? Lang.Worlds.ALPHA_DELETE_TITLE.getString() : this.title;
            String page = Lang.Worlds.ALPHA_PAGE_TITLE.getString(this.alphaListIndex + 1, this.getMaxAlphaListIndex());

            if (this.getLayout() == Generic.ALPHA && maxAlphaListIndex > 1)
                title = title + " (" + page + ")";

            DynamicWidget.render(this.widgets, graphics, mouseX, mouseY, partialTick);
            DrawText.begin(graphics, title).pos(this.width / 2, y).center().draw();
        }
    }
}
