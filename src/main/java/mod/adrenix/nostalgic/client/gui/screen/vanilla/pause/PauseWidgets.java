//package mod.adrenix.nostalgic.client.gui.screen.vanilla.pause;
//
//import mod.adrenix.nostalgic.client.gui.screen.WidgetManager;
//import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
//import mod.adrenix.nostalgic.mixin.access.PauseScreenAccess;
//import mod.adrenix.nostalgic.tweak.config.CandyTweak;
//import mod.adrenix.nostalgic.tweak.enums.PauseLayout;
//import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
//import mod.adrenix.nostalgic.util.client.network.NetUtil;
//import mod.adrenix.nostalgic.util.common.lang.Lang;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.screens.ShareToLanScreen;
//import net.minecraft.client.gui.screens.achievement.StatsScreen;
//import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
//import net.minecraft.client.gui.screens.options.OptionsScreen;
//import net.minecraft.network.chat.Component;
//
//public class PauseWidgets implements WidgetManager {
//    /* Constants */
//
//    private static final int WIDGET_MARGIN = 4;
//    private static final int LARGE_MARGIN = 28;
//    private static final int LARGE_BUTTON = 200;
//    private static final int SMALL_BUTTON = 98;
//
//    /* Fields */
//
//    private final NostalgicPauseScreen pauseScreen;
//    private final Minecraft minecraft;
//    private final PauseLayout layout;
//
//    /* Constructor */
//
//    PauseWidgets(NostalgicPauseScreen pauseScreen) {
//        this.pauseScreen = pauseScreen;
//        this.layout = pauseScreen.getLayout();
//        this.minecraft = Minecraft.getInstance();
//    }
//
//    /* Methods */
//
//    @Override
//    public void init() {
//        switch (this.layout) {
//            case LAN -> this.setLanLayout();
//            case ALPHA_BETA -> this.setAlphaLayout();
//            case ADVANCEMENT -> this.setAdvancementLayout();
//            case ACHIEVE_LOWER -> this.setLowerAchieveLayout();
//            case ACHIEVE_UPPER -> this.setUpperAchieveLayout();
//        }
//    }
//
//    /**
//     * @return The x-coordinate for all left-side buttons.
//     */
//    private int getX() {
//        return this.pauseScreen.width / 2 - 100;
//    }
//
//    /**
//     * @return The y-coordinate for all top buttons.
//     */
//    private int getY() {
//        return (this.pauseScreen.height / 4) + 8;
//    }
//
//    /**
//     * Checks if the current network is multiplayer and is not a local host session.
//     *
//     * @return Whether the current network environment is connected to a dedicated server.
//     */
//    private boolean isMultiplayer() {
//        return NetUtil.isMultiplayer() && !NetUtil.isLocalHost();
//    }
//
//    /**
//     * Changes the save button message.
//     *
//     * @param isLowercase Whether the button message should be lowercase.
//     * @return A save button message component.
//     */
//    private Component getSaveMessage(boolean isLowercase) {
//        Component lower = Lang.Pause.SAVE_LOWER.get();
//        Component upper = Lang.Vanilla.MENU_RETURN_TO_TITLE.get();
//
//        return this.isMultiplayer() ? Lang.Vanilla.MENU_DISCONNECT.get() : (isLowercase ? lower : upper);
//    }
//
//    /**
//     * Exit the screen and return to the game.
//     */
//    private void returnToGame() {
//        if (this.minecraft == null)
//            return;
//
//        this.minecraft.setScreen(null);
//        this.minecraft.mouseHandler.grabMouse();
//    }
//
//    /**
//     * Save or quit to the title screen.
//     */
//    private void saveOrQuit() {
//        ((PauseScreenAccess) this.pauseScreen).nt$onDisconnect();
//    }
//
//    /**
//     * Open the vanilla options screen.
//     */
//    private void gotoOptions() {
//        if (this.minecraft != null)
//            this.minecraft.setScreen(new OptionsScreen(this.pauseScreen, this.minecraft.options));
//    }
//
//    /**
//     * Open the vanilla advancements screen.
//     */
//    private void gotoAdvancements() {
//        if (this.minecraft != null && this.minecraft.player != null)
//            this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements()));
//    }
//
//    /**
//     * Open the vanilla statistics screen.
//     */
//    private void gotoStats() {
//        if (this.minecraft != null && this.minecraft.player != null)
//            this.minecraft.setScreen(new StatsScreen(this.pauseScreen, this.minecraft.player.getStats()));
//    }
//
//    /**
//     * Open the share to LAN screen.
//     */
//    private void gotoLan() {
//        if (this.minecraft != null)
//            this.minecraft.setScreen(new ShareToLanScreen(this.pauseScreen));
//    }
//
//    /**
//     * Open the mod loader's mod list screen.
//     */
//    private void gotoMods() {
//        if (this.minecraft != null && GuiUtil.modListScreen != null)
//            this.minecraft.setScreen(GuiUtil.modListScreen.apply(this.pauseScreen));
//    }
//
//    /**
//     * Sets the widget layout for an alpha-like screen.
//     */
//    private void setAlphaLayout() {
//        ButtonWidget returnToGame = ButtonWidget.create(Lang.Pause.RETURN_LOWER)
//                .pos(this.getX(), this.getY() + 16)
//                .width(LARGE_BUTTON)
//                .onPress(this::returnToGame)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget saveAndQuit = ButtonWidget.create(this.getSaveMessage(true))
//                .posX(this.getX())
//                .below(returnToGame, WIDGET_MARGIN)
//                .width(LARGE_BUTTON)
//                .onPress(this::saveOrQuit)
//                .build(this.pauseScreen::addWidget);
//
//        if (CandyTweak.INCLUDE_MODS_ON_PAUSE.get() && GuiUtil.modListScreen != null) {
//            ButtonWidget options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                    .posX(this.getX())
//                    .below(saveAndQuit, LARGE_MARGIN)
//                    .width(SMALL_BUTTON)
//                    .onPress(this::gotoOptions)
//                    .build(this.pauseScreen::addWidget);
//
//            ButtonWidget.create(Lang.Title.MODS)
//                    .rightOf(options, WIDGET_MARGIN)
//                    .width(SMALL_BUTTON)
//                    .onPress(this::gotoMods)
//                    .build(this.pauseScreen::addWidget);
//        } else {
//            ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                    .posX(this.getX())
//                    .below(saveAndQuit, LARGE_MARGIN)
//                    .width(LARGE_BUTTON)
//                    .onPress(this::gotoOptions)
//                    .build(this.pauseScreen::addWidget);
//        }
//    }
//
//    /**
//     * Sets the widget layout to achievement-style with a lowercase save button message.
//     */
//    private void setLowerAchieveLayout() {
//        ButtonWidget returnToGame = ButtonWidget.create(Lang.Pause.RETURN_LOWER)
//                .pos(this.getX(), this.getY())
//                .width(LARGE_BUTTON)
//                .onPress(this::returnToGame)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget achievements = ButtonWidget.create(Lang.Pause.ACHIEVEMENTS)
//                .posX(this.getX())
//                .below(returnToGame, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoAdvancements)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget statistics = ButtonWidget.create(Lang.Vanilla.GUI_STATS)
//                .rightOf(achievements, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoStats)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget options;
//
//        if (CandyTweak.INCLUDE_MODS_ON_PAUSE.get() && GuiUtil.modListScreen != null) {
//            options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                    .below(statistics, LARGE_MARGIN)
//                    .width(SMALL_BUTTON)
//                    .posX(this.getX())
//                    .onPress(this::gotoOptions)
//                    .build(this.pauseScreen::addWidget);
//
//            ButtonWidget.create(Lang.Title.MODS)
//                    .rightOf(options, WIDGET_MARGIN)
//                    .width(SMALL_BUTTON)
//                    .onPress(this::gotoMods)
//                    .build(this.pauseScreen::addWidget);
//        } else {
//            options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                    .below(statistics, LARGE_MARGIN)
//                    .width(LARGE_BUTTON)
//                    .posX(this.getX())
//                    .onPress(this::gotoOptions)
//                    .build(this.pauseScreen::addWidget);
//        }
//
//        ButtonWidget.create(this.getSaveMessage(true))
//                .posX(this.getX())
//                .below(options, WIDGET_MARGIN)
//                .width(LARGE_BUTTON)
//                .onPress(this::saveOrQuit)
//                .build(this.pauseScreen::addWidget);
//    }
//
//    /**
//     * Sets the widget layout to achievement-style with an uppercase save button message.
//     */
//    private void setUpperAchieveLayout() {
//        ButtonWidget returnToGame = ButtonWidget.create(Lang.Vanilla.MENU_RETURN_TO_GAME)
//                .pos(this.getX(), this.getY())
//                .width(LARGE_BUTTON)
//                .onPress(this::returnToGame)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget achievements = ButtonWidget.create(Lang.Pause.ACHIEVEMENTS)
//                .posX(this.getX())
//                .below(returnToGame, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoAdvancements)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget statistics = ButtonWidget.create(Lang.Vanilla.GUI_STATS)
//                .rightOf(achievements, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoStats)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget options;
//
//        if (CandyTweak.INCLUDE_MODS_ON_PAUSE.get() && GuiUtil.modListScreen != null) {
//            options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                    .below(statistics, LARGE_MARGIN)
//                    .width(SMALL_BUTTON)
//                    .posX(this.getX())
//                    .onPress(this::gotoOptions)
//                    .build(this.pauseScreen::addWidget);
//
//            ButtonWidget.create(Lang.Title.MODS)
//                    .rightOf(options, WIDGET_MARGIN)
//                    .width(SMALL_BUTTON)
//                    .onPress(this::gotoMods)
//                    .build(this.pauseScreen::addWidget);
//        } else {
//            options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                    .below(statistics, LARGE_MARGIN)
//                    .width(LARGE_BUTTON)
//                    .posX(this.getX())
//                    .onPress(this::gotoOptions)
//                    .build(this.pauseScreen::addWidget);
//        }
//
//        ButtonWidget.create(this.getSaveMessage(false))
//                .posX(this.getX())
//                .below(options, WIDGET_MARGIN)
//                .width(LARGE_BUTTON)
//                .onPress(this::saveOrQuit)
//                .build(this.pauseScreen::addWidget);
//    }
//
//    /**
//     * Sets the widget layout to LAN style.
//     */
//    private void setLanLayout() {
//        ButtonWidget returnToGame = ButtonWidget.create(Lang.Vanilla.MENU_RETURN_TO_GAME)
//                .pos(this.getX(), this.getY())
//                .width(LARGE_BUTTON)
//                .onPress(this::returnToGame)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget achievements = ButtonWidget.create(Lang.Pause.ACHIEVEMENTS)
//                .posX(this.getX())
//                .below(returnToGame, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoAdvancements)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget statistics = ButtonWidget.create(Lang.Vanilla.GUI_STATS)
//                .rightOf(achievements, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoStats)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                .below(statistics, LARGE_MARGIN)
//                .width(SMALL_BUTTON)
//                .posX(this.getX())
//                .onPress(this::gotoOptions)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget lan = ButtonWidget.create(Lang.Vanilla.MENU_LAN)
//                .rightOf(options, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .disableIf(NetUtil::isMultiplayer)
//                .onPress(this::gotoLan)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget mods = null;
//
//        if (CandyTweak.INCLUDE_MODS_ON_PAUSE.get() && GuiUtil.modListScreen != null) {
//            mods = ButtonWidget.create(Lang.Title.MODS)
//                    .below(lan, WIDGET_MARGIN)
//                    .width(LARGE_BUTTON)
//                    .posX(this.getX())
//                    .onPress(this::gotoMods)
//                    .build(this.pauseScreen::addWidget);
//        }
//
//        ButtonWidget.create(this.getSaveMessage(false))
//                .posX(this.getX())
//                .below(mods != null ? mods : lan, WIDGET_MARGIN)
//                .width(LARGE_BUTTON)
//                .onPress(this::saveOrQuit)
//                .build(this.pauseScreen::addWidget);
//    }
//
//    /**
//     * Sets the widget layout to advancement-style that includes LAN.
//     */
//    private void setAdvancementLayout() {
//        ButtonWidget returnToGame = ButtonWidget.create(Lang.Vanilla.MENU_RETURN_TO_GAME)
//                .pos(this.getX(), this.getY())
//                .width(LARGE_BUTTON)
//                .onPress(this::returnToGame)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget advancements = ButtonWidget.create(Lang.Vanilla.GUI_ADVANCEMENTS)
//                .posX(this.getX())
//                .below(returnToGame, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoAdvancements)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget statistics = ButtonWidget.create(Lang.Vanilla.GUI_STATS)
//                .rightOf(advancements, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .onPress(this::gotoStats)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget options = ButtonWidget.create(Lang.Vanilla.MENU_OPTIONS)
//                .below(statistics, LARGE_MARGIN)
//                .width(SMALL_BUTTON)
//                .posX(this.getX())
//                .onPress(this::gotoOptions)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget lan = ButtonWidget.create(Lang.Vanilla.MENU_LAN)
//                .rightOf(options, WIDGET_MARGIN)
//                .width(SMALL_BUTTON)
//                .disableIf(NetUtil::isMultiplayer)
//                .onPress(this::gotoLan)
//                .build(this.pauseScreen::addWidget);
//
//        ButtonWidget mods = null;
//
//        if (CandyTweak.INCLUDE_MODS_ON_PAUSE.get() && GuiUtil.modListScreen != null) {
//            mods = ButtonWidget.create(Lang.Title.MODS)
//                    .below(lan, WIDGET_MARGIN)
//                    .width(LARGE_BUTTON)
//                    .posX(this.getX())
//                    .onPress(this::gotoMods)
//                    .build(this.pauseScreen::addWidget);
//        }
//
//        ButtonWidget.create(this.getSaveMessage(false))
//                .posX(this.getX())
//                .below(mods != null ? mods : lan, WIDGET_MARGIN)
//                .width(LARGE_BUTTON)
//                .onPress(this::saveOrQuit)
//                .build(this.pauseScreen::addWidget);
//    }
//}
