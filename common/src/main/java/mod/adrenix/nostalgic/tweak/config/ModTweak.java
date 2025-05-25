package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.group.ModGroup;
import mod.adrenix.nostalgic.tweak.enums.MenuOption;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.tweak.listing.StringSet;
import mod.adrenix.nostalgic.util.common.lang.Lang;

// @formatter:off
public interface ModTweak
{
    /**
     * A global mod state flag that controls whether all tweaks should be off or in their configured state.
     */
    TweakFlag ENABLED = TweakFlag.client(true, Category.ROOT).load().top().reloadChunks().reloadResources().build();

    /**
     * A special mod state flag that controls whether connected LAN players can modify a host's client settings.
     */
    TweakFlag RESTRICTED_LAN = TweakFlag.client(true, Category.ROOT).load().ignore().build();

    /**
     * A special mod state flag that controls whether the server requires players having the mod installed.
     */
    TweakFlag SERVER_SIDE_ONLY = TweakFlag.server(true, Category.ROOT).load().ignore().build();

    /**
     * A server flag that controls whether logging statements are sent to the console window.
     */
    TweakFlag SERVER_LOGGING = TweakFlag.server(true, Category.ROOT).load().ignore().build();

    /**
     * A server flag that controls whether the server is in debugging mode.
     */
    TweakFlag SERVER_DEBUG = TweakFlag.server(false, Category.ROOT).load().ignore().build();

    /**
     * A key binding that opens the mod's configuration menu.
     */
    TweakBinding OPEN_CONFIG_BINDING = TweakBinding.client(79, ModGroup.BINDING, KeybindingId.CONFIG).build();

    /**
     * Enumeration option that determines which screen is opened when the defined hotkey is pressed.
     */
    TweakEnum<MenuOption> DEFAULT_SCREEN = TweakEnum.client(MenuOption.HOME_SCREEN, ModGroup.BINDING).load().top().build();

    /**
     * Controls how many backup files are made in the user's backup folder directory.
     */
    TweakNumber<Integer> NUMBER_OF_BACKUPS = TweakNumber.server(5, ModGroup.CONFIG).slider(Lang.Slider.FILES, 1, 100).load().build();

    /**
     * Controls whether the welcome toast will be displayed.
     */
    TweakFlag SHOW_WELCOME_TOAST = TweakFlag.client(true, ModGroup.TOAST).load().build();

    /**
     * Controls whether the handshake toast will be displayed.
     */
    TweakFlag SHOW_HANDSHAKE_TOAST = TweakFlag.client(false, ModGroup.TOAST).load().build();

    /**
     * Controls whether the tweak changes on LAN toast will be displayed.
     */
    TweakFlag SHOW_LAN_CHANGE_TOAST = TweakFlag.client(true, ModGroup.TOAST).load().build();

    /**
     * Controls whether the serverbound tweak update toast will be displayed.
     */
    TweakFlag SHOW_SERVERBOUND_TOAST = TweakFlag.client(true, ModGroup.TOAST).load().build();

    /**
     * Controls whether the clientbound tweak update toast will be displayed.
     */
    TweakFlag SHOW_CLIENTBOUND_TOAST = TweakFlag.client(false, ModGroup.TOAST).load().build();

    /**
     * Controls whether the scrollbars should perform a smooth animation while scrolling.
     */
    TweakFlag SMOOTH_SCROLL = TweakFlag.client(true, ModGroup.VISUALS).load().build();

    /**
     * Controls the config menu background opacity when the player is in a world.
     */
    TweakNumber<Integer> MENU_BACKGROUND_OPACITY = TweakNumber.client(85, ModGroup.VISUALS)
        .slider(Lang.Colorize.OPACITY, 0, 100, "%")
        .load()
        .build();

    /**
     * Controls if "New" tags are displayed next to tweak names.
     */
    TweakFlag DISPLAY_NEW_TAGS = TweakFlag.client(true, ModGroup.TAGS).load().build();

    /**
     * Controls if tooltips are displayed when the mouse hovers over tweak tags.
     */
    TweakFlag DISPLAY_TAG_TOOLTIPS = TweakFlag.client(true, ModGroup.TAGS).load().build();

    /**
     * Controls if the category tree is rendered in the configuration menu.
     */
    TweakFlag DISPLAY_CATEGORY_TREE = TweakFlag.client(true, ModGroup.TREE).load().build();

    /**
     * Changes the opacity of the menu category tree outlines.
     */
    TweakNumber<Integer> CATEGORY_TREE_OPACITY = TweakNumber.client(80, ModGroup.TREE)
        .slider(Lang.Colorize.OPACITY, 0, 100, "%")
        .load()
        .build();

    /**
     * Controls if a row is highlighted when the mouse hovers it.
     */
    TweakFlag DISPLAY_ROW_HIGHLIGHT = TweakFlag.client(true, ModGroup.ROWS).load().build();

    /**
     * Controls if row highlighting fades in and out.
     */
    TweakFlag DISPLAY_ROW_HIGHLIGHT_FADE = TweakFlag.client(true, ModGroup.ROWS).load().build();

    /**
     * Controls whether row highlight opacity is overridden.
     */
    TweakFlag OVERRIDE_ROW_HIGHLIGHT = TweakFlag.client(false, ModGroup.ROWS).load().build();

    /**
     * Changes the opacity of background row highlighting.
     */
    TweakNumber<Integer> ROW_HIGHLIGHT_OPACITY = TweakNumber.client(20, ModGroup.ROWS)
        .slider(Lang.Colorize.OPACITY, 0, 100, "%")
        .alert(TweakAlert.ROW_HIGHLIGHT_DISABLED)
        .load()
        .build();

    /**
     * Control flag that determines if the config screen remembers its state when closed. When enabled, the config
     * screen will go back to where it was before it was closed.
     */
    TweakFlag PERSISTENT_CONFIG_SCREEN = TweakFlag.client(true, Category.MOD).ignore().load().build();

    /**
     * Control flag that determines if the user has successfully interacted with the tutorial toast. If so, then the
     * toast will not appear the next time a title screen is displayed.
     */
    TweakFlag OPENED_CONFIG_SCREEN = TweakFlag.client(false, Category.MOD).ignore().load().build();

    /**
     * Control flag that determines if the user has successfully interacted with the Sodium information overlay. If so,
     * then the Sodium overlay screen will not open when the config screen is opened.
     */
    TweakFlag OPENED_SODIUM_SCREEN = TweakFlag.client(false, Category.MOD).ignore().load().build();

    /**
     * Control flag that determines if the screen has been displayed to the user. This field is saved when the supporter
     * button is clicked on the home screen.
     */
    TweakFlag OPENED_SUPPORTER_SCREEN = TweakFlag.client(false, Category.MOD).ignore().load().build();

    /**
     * A set of tweak config identifiers that is marked as favorite by the user.
     */
    TweakStringSet FAVORITE_TWEAKS = TweakStringSet.client(new StringSet(), Category.MOD).ignore().load().build();
}
