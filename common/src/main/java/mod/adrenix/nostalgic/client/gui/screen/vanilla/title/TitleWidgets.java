package mod.adrenix.nostalgic.client.gui.screen.vanilla.title;

import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.TitleLayout;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.LanguageSelectScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.server.packs.repository.PackRepository;

class TitleWidgets implements WidgetManager
{
    /* Constants */

    static final int WIDGET_MARGIN = 4;
    static final int LARGE_MARGIN = 28;
    static final int LARGE_BUTTON = 200;
    static final int SMALL_BUTTON = 98;

    /* Fields */

    private final NostalgicTitleScreen titleScreen;
    private final Minecraft minecraft;

    /* Constructor */

    TitleWidgets(NostalgicTitleScreen titleScreen)
    {
        this.titleScreen = titleScreen;
        this.minecraft = Minecraft.getInstance();
    }

    /* Methods */

    /**
     * {@inheritDoc}
     */
    @Override
    public void init()
    {
        switch (this.titleScreen.getLayout())
        {
            case ALPHA -> this.setAlphaLayout();
            case BETA -> this.setBetaLayout();
            default -> this.setReleaseLayout();
        }
    }

    /**
     * @return Whether the layout includes the "Mods" button.
     */
    private boolean hasModsButton()
    {
        return CandyTweak.INCLUDE_MODS_ON_TITLE.get() && GuiUtil.modListScreen != null;
    }

    /**
     * @return The x-coordinate for all left-side buttons.
     */
    int getX()
    {
        return this.titleScreen.width / 2 - 100;
    }

    /**
     * @return The y-coordinate for all top buttons.
     */
    int getY()
    {
        return this.titleScreen.height / 4 + 48 - (this.hasModsButton() ? 12 : 0);
    }

    /**
     * Open the select world screen.
     */
    private void gotoSelectWorld()
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new SelectWorldScreen(this.titleScreen));
    }

    /**
     * Open the multiplayer screen.
     */
    private void gotoMultiplayer()
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new JoinMultiplayerScreen(this.titleScreen));
    }

    /**
     * Open the game options screen.
     */
    private void gotoOptions()
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new OptionsScreen(this.titleScreen, this.minecraft.options));
    }

    /**
     * Open the mod loader's mod list screen.
     */
    private void gotoMods()
    {
        if (this.minecraft != null && GuiUtil.modListScreen != null)
            this.minecraft.setScreen(GuiUtil.modListScreen.apply(this.titleScreen));
    }

    /**
     * Updates the resource pack selection list.
     *
     * @param packRepository The {@link PackRepository} instance.
     */
    private void updatePackList(PackRepository packRepository)
    {
        if (this.minecraft == null)
            return;

        this.minecraft.options.updateResourcePacks(packRepository);
        this.minecraft.setScreen(this.titleScreen);
    }

    /**
     * Open the resource pack selections screen.
     */
    private void gotoResourcePacks()
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new PackSelectionScreen(this.minecraft.getResourcePackRepository(), this::updatePackList, this.minecraft.getResourcePackDirectory(), Lang.Vanilla.RESOURCE_PACK_TITLE.get()));
    }

    /**
     * Open the language selection screen.
     */
    private void gotoLanguage()
    {
        if (this.minecraft != null)
            this.minecraft.setScreen(new LanguageSelectScreen(this.titleScreen, this.minecraft.options, this.minecraft.getLanguageManager()));
    }

    /**
     * Define widgets for an alpha-like screen layout.
     */
    private void setAlphaLayout()
    {
        ButtonWidget singleplayer = ButtonWidget.create(Lang.Vanilla.MENU_SINGLEPLAYER)
            .pos(this.getX(), this.getY())
            .width(LARGE_BUTTON)
            .onPress(this::gotoSelectWorld)
            .build(this.titleScreen::addWidget);

        ButtonWidget multiplayer = ButtonWidget.create(Lang.Vanilla.MENU_MULTIPLAYER)
            .posX(this.getX())
            .below(singleplayer, WIDGET_MARGIN)
            .width(LARGE_BUTTON)
            .onPress(this::gotoMultiplayer)
            .build(this.titleScreen::addWidget);

        ButtonWidget mods = null;

        if (this.hasModsButton())
        {
            mods = ButtonWidget.create(Lang.Title.MODS)
                .posX(this.getX())
                .below(multiplayer, WIDGET_MARGIN)
                .width(LARGE_BUTTON)
                .onPress(this::gotoMods)
                .build(this.titleScreen::addWidget);
        }

        ButtonWidget tutorial = ButtonWidget.create(Lang.Title.TUTORIAL.withStyle(ChatFormatting.GRAY))
            .disableIf(BooleanSupplier.ALWAYS)
            .posX(this.getX())
            .below(mods != null ? mods : multiplayer, WIDGET_MARGIN)
            .width(LARGE_BUTTON)
            .build(this.titleScreen::addWidget);

        boolean canStop = CandyTweak.ADD_QUIT_BUTTON.get();

        ButtonWidget options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
            .posX(this.getX())
            .below(tutorial, LARGE_MARGIN - 12)
            .width(canStop ? SMALL_BUTTON : LARGE_BUTTON)
            .onPress(this::gotoOptions)
            .build(this.titleScreen::addWidget);

        if (canStop)
        {
            ButtonWidget.create(Lang.Vanilla.MENU_QUIT)
                .rightOf(options, WIDGET_MARGIN)
                .width(SMALL_BUTTON)
                .onPress(this.minecraft::stop)
                .build(this.titleScreen::addWidget);
        }
    }

    /**
     * Define widgets for a beta-like screen layout.
     */
    private void setBetaLayout()
    {
        ButtonWidget singleplayer = ButtonWidget.create(Lang.Vanilla.MENU_SINGLEPLAYER)
            .pos(this.getX(), this.getY())
            .width(LARGE_BUTTON)
            .onPress(this::gotoSelectWorld)
            .build(this.titleScreen::addWidget);

        ButtonWidget multiplayer = ButtonWidget.create(Lang.Vanilla.MENU_MULTIPLAYER)
            .posX(this.getX())
            .below(singleplayer, WIDGET_MARGIN)
            .width(LARGE_BUTTON)
            .onPress(this::gotoMultiplayer)
            .build(this.titleScreen::addWidget);

        ButtonWidget mods = null;

        if (this.hasModsButton())
        {
            mods = ButtonWidget.create(Lang.Title.MODS)
                .posX(this.getX())
                .below(multiplayer, WIDGET_MARGIN)
                .width(LARGE_BUTTON)
                .onPress(this::gotoMods)
                .build(this.titleScreen::addWidget);
        }

        ButtonWidget packs = ButtonWidget.create(this.hasModsButton() ? Lang.Title.TEXTURE_PACK : Lang.Title.MODS_TEXTURE)
            .posX(this.getX())
            .below(mods != null ? mods : multiplayer, WIDGET_MARGIN)
            .width(LARGE_BUTTON)
            .onPress(this::gotoResourcePacks)
            .build(this.titleScreen::addWidget);

        boolean canStop = CandyTweak.ADD_QUIT_BUTTON.get();

        ButtonWidget options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
            .posX(this.getX())
            .below(packs, canStop ? 16 : WIDGET_MARGIN)
            .width(canStop ? SMALL_BUTTON : LARGE_BUTTON)
            .onPress(this::gotoOptions)
            .build(this.titleScreen::addWidget);

        if (canStop)
        {
            ButtonWidget.create(Lang.Vanilla.MENU_QUIT)
                .rightOf(options, WIDGET_MARGIN)
                .width(SMALL_BUTTON)
                .onPress(this.minecraft::stop)
                .build(this.titleScreen::addWidget);
        }
    }

    /**
     * Define widgets for a release-like screen layout.
     */
    private void setReleaseLayout()
    {
        if (this.minecraft == null)
            return;

        ButtonWidget singleplayer = ButtonWidget.create(Lang.Vanilla.MENU_SINGLEPLAYER)
            .pos(this.getX(), this.getY())
            .width(LARGE_BUTTON)
            .onPress(this::gotoSelectWorld)
            .build(this.titleScreen::addWidget);

        ButtonWidget multiplayer = ButtonWidget.create(Lang.Vanilla.MENU_MULTIPLAYER)
            .posX(this.getX())
            .below(singleplayer, WIDGET_MARGIN)
            .width(LARGE_BUTTON)
            .onPress(this::gotoMultiplayer)
            .build(this.titleScreen::addWidget);

        ButtonWidget mods = null;
        ButtonWidget packs = null;

        if (this.hasModsButton())
        {
            mods = ButtonWidget.create(Lang.Title.MODS)
                .posX(this.getX())
                .below(multiplayer, WIDGET_MARGIN)
                .width(LARGE_BUTTON)
                .onPress(this::gotoMods)
                .build(this.titleScreen::addWidget);
        }

        if (this.titleScreen.getLayout() == TitleLayout.RELEASE_TEXTURE_PACK)
        {
            packs = ButtonWidget.create(Lang.Title.TEXTURE_PACK)
                .posX(this.getX())
                .below(mods != null ? mods : multiplayer, WIDGET_MARGIN)
                .width(LARGE_BUTTON)
                .onPress(this::gotoResourcePacks)
                .build(this.titleScreen::addWidget);
        }

        ButtonWidget options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
            .below(packs != null ? packs : mods != null ? mods : multiplayer, packs == null && mods == null ? 40 : 16)
            .width(SMALL_BUTTON)
            .posX(this.getX())
            .onPress(this::gotoOptions)
            .build(this.titleScreen::addWidget);

        ButtonWidget.create(Lang.Vanilla.MENU_QUIT)
            .rightOf(options, WIDGET_MARGIN)
            .width(SMALL_BUTTON)
            .onPress(this.minecraft::stop)
            .build(this.titleScreen::addWidget);

        if (!CandyTweak.REMOVE_TITLE_LANGUAGE_BUTTON.get())
        {
            ButtonWidget.create()
                .leftOf(options, WIDGET_MARGIN)
                .icon(TextureIcon.fromSprite(LocateResource.game("icon/language"), 15))
                .iconCenterOffset(-1)
                .onPress(this::gotoLanguage)
                .build(this.titleScreen::addWidget);
        }
    }
}
