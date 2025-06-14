package mod.adrenix.nostalgic.util.common.lang;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.function.Supplier;

public interface Lang
{
    /* Static */

    @PublicAPI Translation TITLE = new Translation("gui.nostalgic_tweaks.title");
    @PublicAPI Translation EMPTY = new Translation("gui.nostalgic_tweaks.empty");

    /* Functional */

    /**
     * Get a lang key from a string. If a translation exists for the given string, then the key will retrieve that
     * string; otherwise, the literal form of the string will be returned. This is useful in situations where a lang key
     * is required, but the string may not have a translation, such as a version number.
     *
     * @param literal A literal string.
     * @return A new {@link Translation} instance.
     */
    @PublicAPI
    static Translation literal(String literal)
    {
        return new Translation(literal);
    }

    /**
     * Get a lang key from a {@link Component}. This is useful in situations where a lang key is required, but only a
     * component instance is available for translation.
     *
     * @param component A {@link Component} instance.
     * @return A new {@link Translation} instance.
     */
    @PublicAPI
    static Translation component(Component component)
    {
        return new Translation(DecodeLang.findAndReplace(component).getString());
    }

    /* Keybindings */

    /**
     * Lang keys that provide translations for keybindings.
     */
    interface Binding
    {
        Translation OPEN_CONFIG = new Translation("key.nostalgic_tweaks.open_config");
        Translation TOGGLE_FOG = new Translation("key.nostalgic_tweaks.toggle_fog");
        Translation STOP_SONG = new Translation("key.nostalgic_tweaks.stop_song");
        Translation NEXT_SONG = new Translation("key.nostalgic_tweaks.next_song");
        Translation UNBOUND = new Translation("key.nostalgic_tweaks.unbound");
        Translation TOGGLE_KEY = new Translation("key.nostalgic_tweaks.toggle_key");
    }

    /* Vanilla */

    /**
     * Lang keys that reference the default vanilla language file.
     */
    interface Vanilla
    {
        Translation INVENTORY = new Translation("container.inventory");
        Translation GUI_ADVANCEMENTS = new Translation("gui.advancements");
        Translation GUI_CANCEL = new Translation("gui.cancel");
        Translation GUI_STATS = new Translation("gui.stats");
        Translation GUI_DONE = new Translation("gui.done");
        Translation MENU_SINGLEPLAYER = new Translation("menu.singleplayer");
        Translation MENU_MULTIPLAYER = new Translation("menu.multiplayer");
        Translation MENU_DISCONNECT = new Translation("menu.disconnect");
        Translation MENU_OPTIONS = new Translation("menu.options");
        Translation MENU_RETURN_TO_GAME = new Translation("menu.returnToGame");
        Translation MENU_RETURN_TO_TITLE = new Translation("menu.returnToMenu");
        Translation MENU_ONLINE = new Translation("menu.online");
        Translation MENU_MODDED = new Translation("menu.modded");
        Translation MENU_QUIT = new Translation("menu.quit");
        Translation MENU_GAME = new Translation("menu.game");
        Translation MENU_LAN = new Translation("menu.shareToLan");
        Translation SAVE_LEVEL = new Translation("menu.savingLevel");
        Translation WORLD_CREATE = new Translation("selectWorld.create");
        Translation WORLD_DATA_READ = new Translation("selectWorld.data_read");
        Translation WORLD_LOADING_LIST = new Translation("selectWorld.loading_list");
        Translation WORLD_SELECT_TITLE = new Translation("selectWorld.title");
        Translation WORLD_PLAY_SELECTED = new Translation("selectWorld.select");
        Translation WORLD_RESOURCE_LOAD = new Translation("selectWorld.resource_load");
        Translation RESOURCE_PACK_TITLE = new Translation("resourcePack.title");
    }

    /* Screens */

    /**
     * Lang keys that are used by the mod's homepage settings screen.
     */
    interface Home
    {
        Translation TITLE = from("title");
        Translation KOFI = from("kofi");
        Translation DISCORD = from("discord");
        Translation GOLDEN_DAYS = from("golden_days");
        Translation SUPPORTERS = from("supporters");
        Translation CONNECTING = from("connecting");
        Translation DISCONNECTED = from("disconnected");
        Translation WRONG_VERSION = from("wrong_version");
        Translation INVALID_DATA = from("invalid_data");
        Translation KOFI_MEMBER = from("kofi_member");
        Translation MOD_CREATOR = from("mod_creator");
        Translation INIT_CONFIG = from("init_config");
        Translation SETUP = from("setup");
        Translation SETUP_INFO = from("setup_info");
        Translation DEFAULT = from("default");
        Translation DEFAULT_INFO = from("default_info");
        Translation DISABLE = from("disable");
        Translation DISABLE_INFO = from("disable_info");
        Translation FPS_INFO = from("fps_info");
        Translation FPS_SWITCH = from("fps_switch");
        Translation DEBUG = from("debug");
        Translation DEBUG_INFO = from("debug_info");
        Translation DEBUG_SWITCH = from("debug_switch");
        Translation DEBUG_SHORTCUT = from("debug_shortcut");
        Translation PREV_PANORAMA = from("prev_panorama");
        Translation PREV_PANORAMA_INFO = from("prev_panorama_info");
        Translation CYCLE_PANORAMA = from("cycle_panorama");
        Translation CYCLE_PANORAMA_INFO = from("cycle_panorama_info");
        Translation NEXT_PANORAMA = from("next_panorama");
        Translation NEXT_PANORAMA_INFO = from("next_panorama_info");
        Translation WARNING_OVERLAY = from("warning_overlay");
        Translation WARNING_BANNER = from("warning_banner");
        Translation WARNING_BANNER_CLICK = from("warning_banner_click");
        Translation SODIUM_TITLE = from("sodium_title");
        Translation SODIUM_CLOUDS_TITLE = from("sodium_clouds_title");
        Translation SODIUM_CLOUDS_INFO = from("sodium_clouds_info");
        Translation SODIUM_CLOUDS_OVERRIDE = from("sodium_clouds_override");
        Translation SODIUM_CLOUDS_RESTART = from("sodium_clouds_restart");
        Translation SODIUM_OPEN_PROPERTIES = from("sodium_open_properties");
        Translation SODIUM_COPY_CLOUDS = from("sodium_copy_clouds");
        Translation SODIUM_MISSING_FILE_HEADER = from("sodium_missing_file_header");
        Translation SODIUM_MISSING_FILE_BODY = from("sodium_missing_file_body");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.home." + name);
        }
    }

    /**
     * Lang keys that are used by the tweak packs screen.
     */
    interface Packs
    {
        Translation HEADER = from("header");
        Translation MESSAGE = from("message");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.packs." + name);
        }
    }

    /**
     * Lang keys that are used by affirmation screens.
     */
    interface Affirm
    {
        Translation QUIT_TITLE = from("quit_title");
        Translation QUIT_BODY = from("quit_body");
        Translation QUIT_DISCARD = from("quit_discard");
        Translation QUIT_CANCEL = from("quit_cancel");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.affirm." + name);
        }
    }

    /**
     * Lang keys that are used by the world screens.
     */
    interface Worlds
    {
        Translation ALPHA_EMPTY = from("alpha_empty");
        Translation ALPHA_DELETE_TITLE = from("alpha_delete_title");
        Translation ALPHA_DELETE_BUTTON = from("alpha_delete_button");
        Translation ALPHA_CANNOT_DELETE = from("alpha_cannot_delete");
        Translation ALPHA_PAGE_TITLE = from("alpha_page_title");
        Translation ALPHA_MOVE_RIGHT = from("alpha_move_right");
        Translation ALPHA_MOVE_LEFT = from("alpha_move_left");
        Translation BETA_RENAME = from("beta_rename");
        Translation BETA_UNKNOWN_LAST_PLAYED = from("beta_unknown_last_played");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.worlds." + name);
        }
    }

    /**
     * Lang keys that are used by the death screen.
     */
    interface Death
    {
        Translation SCORE = from("score");
        Translation GAME_OVER = from("game_over");
        Translation TITLE_MENU = from("title_menu");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.death." + name);
        }
    }

    /**
     * Lang keys that are used in the game's level loading screens.
     */
    interface Level
    {
        Translation LOADING = from("loading");
        Translation BUILDING = from("building");
        Translation SIMULATE = from("simulate");
        Translation SAVING = from("saving");
        Translation ENTER_NETHER = from("enterNether");
        Translation ENTER_END = from("enterEnd");
        Translation LEAVING_NETHER = from("leaveNether");
        Translation LEAVING_END = from("leaveEnd");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.level." + name);
        }
    }

    /**
     * Lang keys that are used in the game's title screen.
     */
    interface Title
    {
        Translation MODS = from("mods");
        Translation MODS_TEXTURE = from("mods_texture");
        Translation TEXTURE_PACK = from("texture_pack");
        Translation TUTORIAL = from("tutorial");
        Translation COPYRIGHT_ALPHA = from("copyright_alpha");
        Translation COPYRIGHT_BETA = from("copyright_beta");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.title." + name);
        }
    }

    /**
     * Lang keys that are used in the game's pause screen.
     */
    interface Pause
    {
        Translation GAME_MENU = from("game_menu");
        Translation ACHIEVEMENTS = from("achievements");
        Translation RETURN_LOWER = from("return");
        Translation SAVE_LOWER = from("save");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.screen.pause." + name);
        }
    }

    /**
     * Lang keys that are used by the mod's toast system.
     */
    interface Toast
    {
        Translation WELCOME_TITLE = from("welcome_title");
        Translation WELCOME_MESSAGE = from("welcome_message");
        Translation LAN_CHANGE_TITLE = from("lan_change_title");
        Translation LAN_CHANGE_MESSAGE = from("lan_change_message");
        Translation LAN_REJECTED_TITLE = from("lan_rejected_title");
        Translation LAN_REJECTED_MESSAGE = from("lan_rejected_message");
        Translation CLIENTBOUND_TWEAK_TITLE = from("clientbound_tweak_title");
        Translation CLIENTBOUND_TWEAK_MESSAGE = from("clientbound_tweak_message");
        Translation SERVERBOUND_TWEAK_TITLE = from("serverbound_tweak_title");
        Translation SERVERBOUND_TWEAK_MESSAGE = from("serverbound_tweak_message");
        Translation HANDSHAKE_TITLE = from("handshake_title");
        Translation HANDSHAKE_MESSAGE = from("handshake_message");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.toast." + name);
        }
    }

    /* Widgets */

    /**
     * Lang keys that are used by input widgets.
     */
    interface Input
    {
        Translation SEARCH = from("search");
        Translation INVALID_TAG = from("search.invalid_tag");
        Translation TYPE = from("type");
        Translation TIP = from("tip");
        Translation TIP_CLICK = from("tip.click");
        Translation TIP_SEARCH = from("tip.search");
        Translation COPY = from("copy");
        Translation COPY_INFO = from("copy.info");
        Translation PASTE = from("paste");
        Translation PASTE_INFO = from("paste.info");
        Translation CLEAR = from("clear");
        Translation CLEAR_INFO = from("clear.info");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.input." + name);
        }
    }

    /**
     * Lang keys that are used by slider widgets.
     */
    interface Slider
    {
        Translation CAP = from("cap");
        Translation SCALE = from("scale");
        Translation STACK = from("stack");
        Translation LIMIT = from("limit");
        Translation FILES = from("files");
        Translation SPEED = from("speed");
        Translation CUSTOM = from("custom");
        Translation RADIUS = from("radius");
        Translation OFFSET = from("offset");
        Translation DENSITY = from("density");
        Translation MINIMUM = from("minimum");
        Translation MAXIMUM = from("maximum");
        Translation SECONDS = from("seconds");
        Translation ENCROACH = from("encroach");
        Translation INTENSITY = from("intensity");
        Translation THICKNESS = from("thickness");
        Translation PERCENTAGE = from("percentage");
        Translation BLOCK_LIGHT = from("block_light");
        Translation Y_LEVEL = from("y_level");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.slider." + name);
        }
    }

    /**
     * Lang keys that are used by button widgets.
     */
    interface Button
    {
        Translation SAVE = from("save");
        Translation MANAGE = from("manage");
        Translation DOWNLOAD = from("download");
        Translation RECONNECT = from("reconnect");
        Translation SUGGESTIONS = from("suggestions");
        Translation OPEN_EDITOR = from("open_editor");
        Translation MENU_OPTIONS = from("menu_options");
        Translation IMPORT_AND_EXPORT = from("import_and_export");
        Translation SERVER_OPERATIONS = from("server_operations");
        Translation TOGGLE_ALL_TWEAKS = from("toggle_all_tweaks");
        Translation SEND_TO_SERVER = from("send_to_server");
        Translation REVIEW_CHANGES = from("review_changes");
        Translation CONFIG_PRESETS = from("config_presets");
        Translation CREATE_PRESET = from("create_preset");
        Translation LOGICAL_SIDE = from("logical_side");
        Translation CREATE_BACKUP = from("create_backup");
        Translation CLIENT_BACKUP = from("client_backup");
        Translation SERVER_BACKUP = from("server_backup");
        Translation CLIENT_IMPORT = from("client_import");
        Translation CLIENT_EXPORT = from("client_export");
        Translation CLIENT_RELOAD = from("client_reload");
        Translation SERVER_IMPORT = from("server_import");
        Translation SERVER_EXPORT = from("server_export");
        Translation SERVER_RELOAD = from("server_reload");
        Translation VIEW_BACKUPS = from("view_backups");
        Translation OPEN_FOLDER = from("open_folder");
        Translation DISABLE_ALL = from("disable_all");
        Translation DELETE_ALL = from("delete_all");
        Translation ENABLE_ALL = from("enable_all");
        Translation FILTER_ALL = from("filter_all");
        Translation SHOW_ALL = from("show_all");
        Translation EDIT_LIST = from("edit_list");
        Translation VIEW_LIST = from("view_list");
        Translation FAVORITE = from("favorite");
        Translation NETWORK = from("network");
        Translation INSPECT = from("inspect");
        Translation REFRESH = from("refresh");
        Translation SEE_ALL = from("see_all");
        Translation STATUS = from("status");
        Translation DELETE = from("delete");
        Translation FILTER = from("filter");
        Translation EXPORT = from("export");
        Translation IMPORT = from("import");
        Translation MODERN = from("modern");
        Translation LOCAL = from("local");
        Translation QUICK = from("quick");
        Translation RESET = from("reset");
        Translation APPLY = from("apply");
        Translation COPY = from("copy");
        Translation EDIT = from("edit");
        Translation HELP = from("help");
        Translation OKAY = from("okay");
        Translation UNDO = from("undo");
        Translation ADD = from("add");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.button." + name);
        }
    }

    /**
     * Lang keys that are used by tooltip bubbles.
     */
    interface Tooltip
    {
        Translation HIDE = from("hide");
        Translation SHIFT = from("shift");
        Translation QUICK = from("quick");
        Translation MANAGE = from("manage");
        Translation FILTER = from("filter");
        Translation FILTER_SEARCH = from("filter_search");
        Translation FILTER_SEARCH_INFO = from("filter_search_info");
        Translation MANAGE_LISTING = from("manage_listing");
        Translation HOME_INIT = from("home_init");
        Translation HOME_DEBUG = from("home_debug");
        Translation HOME_SODIUM = from("home_sodium");
        Translation HOME_WARNING = from("home_warning");
        Translation HOME_SUPPORTERS = from("home_supporters");
        Translation SODIUM_PROPERTIES = from("sodium_properties");
        Translation SODIUM_COPY_PROPERTY = from("sodium_copy_property");
        Translation FILTER_DISABLED = from("filter_disabled");
        Translation FAVORITE_DISABLED = from("favorite_disabled");
        Translation FAVORITE = from("favorite");
        Translation ALL = from("all");
        Translation ALL_DISABLED = from("all_disabled");
        Translation NOT_OPERATOR = from("not_operator");
        Translation NOT_CONNECTED_OR_OPERATOR = from("not_connected_or_operator");
        Translation FINISH = from("finish");
        Translation COPY = from("copy");
        Translation UNDO = from("undo");
        Translation SAVE = from("save");
        Translation ADD = from("add");
        Translation SAVE_LAN = from("save_lan");
        Translation SAVE_SSO = from("save_sso");
        Translation SAVE_MAX_BACKUP = from("save_max_backup");
        Translation SAVE_TWEAK_LOCAL = from("save_tweak_local");
        Translation SAVE_TWEAK_NETWORK = from("save_tweak_network");
        Translation SAVE_BACKUP = from("save_backup");
        Translation EDIT_BACKUP = from("edit_backup");
        Translation DELETE_BACKUP = from("delete_backup");
        Translation INSPECT_BACKUP = from("inspect_backup");
        Translation DOWNLOAD_BACKUP = from("download_backup");
        Translation DELETE_PRESET = from("delete_preset");
        Translation INSPECT_PRESET = from("inspect_preset");
        Translation APPLY_PRESET = from("apply_preset");
        Translation APPLY_CLIENT_BACKUP = from("apply_client_backup");
        Translation APPLY_SERVER_BACKUP = from("apply_server_backup");
        Translation OPEN_BACKUP_FOLDER = from("open_backup_folder");
        Translation OPEN_PRESETS_FOLDER = from("open_presets_folder");
        Translation SEND_PRESET_TO_SERVER = from("send_preset_to_server");
        Translation REFRESH_VIEW = from("refresh_view");
        Translation REVIEW_CHANGES = from("review_changes");
        Translation DELETE_ALL_BACKUPS = from("delete_all_backups");
        Translation DELETE_ALL_PRESETS = from("delete_all_presets");
        Translation CREATE_PRESET = from("create_preset");
        Translation OPEN_DISCORD = from("open_discord");
        Translation KEYBOARD_SHORTCUT = from("keyboard_shortcut");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.tooltip." + name);
        }
    }

    /**
     * Lang keys that are used by the falling block logo editor.
     */
    interface Logo
    {
        Translation READ_ERROR = logo("read_error");
        Translation READ_ERROR_INFO = logo("read_error_info");
        Translation RESET_CONFIG = logo("reset_config");
        Translation QUIT_DISCARD = logo("quit_discard");
        Translation UP = logo("up");
        Translation UP_TOOLTIP = logo("up.tooltip");
        Translation DOWN = logo("down");
        Translation DOWN_TOOLTIP = logo("down.tooltip");
        Translation PLAY = logo("play");
        Translation PLAY_TOOLTIP = logo("play.tooltip");
        Translation UNDO = logo("undo");
        Translation UNDO_TOOLTIP = logo("undo.tooltip");
        Translation REDO = logo("redo");
        Translation REDO_TOOLTIP = logo("redo.tooltip");
        Translation HELP = logo("help");
        Translation HELP_TOOLTIP = logo("help.tooltip");
        Translation HELP_UNDERLINE = logo("help.underline");
        Translation SELECT_BLOCK = logo("select_block");
        Translation BLOCK = logo("block");
        Translation BLOCK_TOOLTIP = logo("block.tooltip");
        Translation BLOCK_UNDERLINE = logo("block.underline");
        Translation SHADOW = logo("shadow");
        Translation SOUND_ON = logo("sound.on");
        Translation SOUND_OFF = logo("sound.off");
        Translation SOUND_ON_UNDERLINE = logo("sound.on.underline");
        Translation SOUND_OFF_UNDERLINE = logo("sound.off.underline");
        Translation SOUND_TOOLTIP = logo("sound.tooltip");
        Translation DRAW_TOOL = logo("draw_tool");
        Translation DRAW_TOOL_UNDERLINE = logo("draw_tool.underline");
        Translation DRAW_TOOL_TOOLTIP = logo("draw_tool.tooltip");
        Translation PICK_TOOL = logo("pick_tool");
        Translation PICK_TOOL_UNDERLINE = logo("pick_tool.underline");
        Translation PICK_TOOL_TOOLTIP = logo("pick_tool.tooltip");
        Translation SELECT_TOOL = logo("select_tool");
        Translation SELECT_TOOL_UNDERLINE = logo("select_tool.underline");
        Translation SELECT_TOOL_TOOLTIP = logo("select_tool.tooltip");
        Translation MOVEIT_TOOL = logo("moveit_tool");
        Translation MOVEIT_TOOL_UNDERLINE = logo("moveit_tool.underline");
        Translation MOVEIT_TOOL_TOOLTIP = logo("moveit_tool.tooltip");
        Translation ERASER_TOOL = logo("eraser_tool");
        Translation ERASER_TOOL_UNDERLINE = logo("eraser_tool.underline");
        Translation ERASER_TOOL_TOOLTIP = logo("eraser_tool.tooltip");
        Translation FILTER_SELECTION = logo("filter_selection");
        Translation FILTER_SELECTION_UNDERLINE = logo("filter_selection.underline");
        Translation FILTER_SELECTION_TOOLTIP = logo("filter_selection.tooltip");
        Translation APPLY_SELECTION = logo("apply_selection");
        Translation APPLY_SELECTION_UNDERLINE = logo("apply_selection.underline");
        Translation APPLY_SELECTION_TOOLTIP = logo("apply_selection.tooltip");
        Translation CLEAR_CANVAS = logo("clear_canvas");
        Translation CLEAR_CANVAS_UNDERLINE = logo("clear_canvas.underline");
        Translation CLEAR_CANVAS_TOOLTIP = logo("clear_canvas.tooltip");
        Translation OPEN_FOLDER = logo("open_folder");
        Translation OPEN_FOLDER_TOOLTIP = logo("open_folder.tooltip");
        Translation COPY_CANVAS = logo("copy_canvas");
        Translation COPY_CANVAS_TOOLTIP = logo("copy_canvas.tooltip");
        Translation UPLOAD_CONFIG = logo("upload_config");
        Translation UPLOAD_CONFIG_TOOLTIP = logo("upload_config.tooltip");

        private static Translation logo(String name)
        {
            return new Translation("gui.nostalgic_tweaks.logo_editor." + name);
        }

        private static Translation help(String name)
        {
            return new Translation("gui.nostalgic_tweaks.logo_editor.help_overlay." + name);
        }

        interface Help
        {
            Translation TITLE = help("title");

            /**
             * Make a list of translatable paragraphs for the falling logo blocks editor help overlay.
             *
             * @param jsonIdentifier  The base JSON identifier string that precedes the .p%d appendix.
             * @param numOfParagraphs The number of paragraphs this section will have.
             * @return An {@link ArrayList} of {@link Translation} paragraphs.
             */
            private static ArrayList<Translation> makeInfo(String jsonIdentifier, int numOfParagraphs)
            {
                ArrayList<Translation> paragraphs = new ArrayList<>();

                for (int i = 1; i <= numOfParagraphs; i++)
                    paragraphs.add(help(String.format("%s.p%d", jsonIdentifier, i)));

                return paragraphs;
            }

            Translation TERMINOLOGY = help("terminology");
            Translation TIPS_AND_HINTS = help("tips_and_hints");
            Translation KEY_SHORTCUTS = help("key_shortcuts");
            Translation CANVAS_TOOLS = help("canvas_tools");
            Translation PIXEL_SETTINGS = help("pixel_settings");
            Translation BATCH_EDITING = help("batch_editing");
            Translation FILE_OPTIONS = help("file_options");
            Translation MOVE_TOOLBAR = help("move_toolbar");
            Translation ACTION_HISTORY = help("action_history");

            Supplier<ArrayList<Translation>> TERMINOLOGY_INFO = () -> makeInfo("terminology", 7);
            Supplier<ArrayList<Translation>> TIPS_AND_HINTS_INFO = () -> makeInfo("tips_and_hints", 4);
            Supplier<ArrayList<Translation>> KEY_SHORTCUTS_INFO = () -> makeInfo("key_shortcuts", 26);
            Supplier<ArrayList<Translation>> CANVAS_TOOLS_INFO = () -> makeInfo("canvas_tools", 5);
            Supplier<ArrayList<Translation>> PIXEL_SETTINGS_INFO = () -> makeInfo("pixel_settings", 3);
            Supplier<ArrayList<Translation>> BATCH_EDITING_INFO = () -> makeInfo("batch_editing", 3);
            Supplier<ArrayList<Translation>> FILE_OPTIONS_INFO = () -> makeInfo("file_options", 3);
            Supplier<ArrayList<Translation>> MOVE_TOOLBAR_INFO = () -> makeInfo("move_toolbar", 2);
            Supplier<ArrayList<Translation>> ACTION_HISTORY_INFO = () -> makeInfo("action_history", 3);
        }
    }

    /**
     * Lang keys that lists use within the graphical user interface.
     */
    interface Listing
    {
        Translation ADD = from("add");
        Translation SEARCHING = from("searching");
        Translation DISABLED_TITLE = from("disabled_title");
        Translation DISABLED_MESSAGE = from("disabled_message");
        Translation DISABLED_WARNING = from("disabled_warning");
        Translation WILDCARD_TITLE = from("wildcard_title");
        Translation WILDCARD_MESSAGE = from("wildcard_message");
        Translation WILDCARD_HELP = from("wildcard_help");
        Translation INVALID_ITEM = from("invalid_item");
        Translation INVALID_TYPE = from("invalid_type");
        Translation INVALID_MESSAGE = from("invalid_message");
        Translation ALREADY_ADDED = from("already_added");
        Translation NOTHING_FOUND = from("nothing_found");
        Translation NOTHING_SAVED = from("nothing_saved");
        Translation ITEM_INFO = from("item_info");
        Translation INPUT_COPIED = from("input_copied");
        Translation EMPTY_FAVORITES = from("empty_favorites");
        Translation EMPTY_SUGGESTIONS = from("empty_suggestions");
        Translation OPEN_SUGGESTIONS = from("open_suggestions");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.listing." + name);
        }
    }

    /**
     * Lang keys that are used by rows within row-list widgets.
     */
    interface TweakRow
    {
        Translation ENABLED = from("enabled");
        Translation DISABLED = from("disabled");
        Translation CACHE = from("cache");
        Translation CACHE_CLIENT = from("cache_client");
        Translation NETWORK_DISCONNECTED = from("network_disconnected");
        Translation STAR_OFF = from("star_off");
        Translation STAR = from("star");
        Translation RESET = from("reset");
        Translation RESET_OFF = from("reset_off");
        Translation STATUS = from("status");
        Translation MODERN = from("modern");
        Translation MODERN_OFF = from("modern_off");
        Translation DESCRIPTION = from("description");
        Translation NO_IMPL = from("no_impl");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.row.tweak." + name);
        }
    }

    /**
     * Lang keys that are used by tweak tag widgets.
     */
    interface Tag
    {
        Translation CONFLICT = from("conflict");
        Translation NEW = from("new");
        Translation NEW_TOOLTIP = from("new.info");
        Translation ALERT = from("alert");
        Translation CLIENT = from("client");
        Translation CLIENT_TOOLTIP = from("client.info");
        Translation SERVER = from("server");
        Translation SERVER_TOOLTIP = from("server.info");
        Translation DYNAMIC = from("dynamic");
        Translation DYNAMIC_TOOLTIP = from("dynamic.info");
        Translation RELOAD = from("reload");
        Translation RELOAD_TOOLTIP = from("reload.info");
        Translation WARNING = from("warning");
        Translation SYNC = from("sync");
        Translation SYNC_TOOLTIP = from("sync.info");
        Translation NO_SSO = from("no_sso");
        Translation NO_SSO_HEADER = from("no_sso.header");
        Translation NO_SSO_TOOLTIP = from("no_sso.info");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.tag." + name);
        }
    }

    interface Text
    {
        Translation TOGGLE = from("toggle");
        Translation WAIT = from("wait");
        Translation WARN = from("warn");
        Translation FAIL = from("fail");
        Translation LOADED = from("loaded");
        Translation MOD_CONFLICT = from("mod_conflict");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.text." + name);
        }
    }

    /* Overlays */

    /**
     * Lang keys that are used by default overlay screens.
     */
    interface Overlay
    {
        Translation DRAG_TIP = from("drag");

        @SuppressWarnings("SameParameterValue")
        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay." + name);
        }
    }

    /**
     * Lang keys that are used by error overlay screens.
     */
    interface Error
    {
        Translation IO_TITLE = from("io_title");
        Translation SERVER_TITLE = from("server_title");
        Translation JAVA_TITLE = from("java.title");
        Translation JAVA_MESSAGE = from("java.message");
        Translation APPLY_TITLE = from("apply.title");
        Translation APPLY_MESSAGE = from("apply.message");
        Translation IMPORT_TITLE = from("import.title");
        Translation IMPORT_MESSAGE = from("import.message");
        Translation CREATE_BACKUP_TITLE = from("create_backup.title");
        Translation CREATE_BACKUP_MESSAGE = from("create_backup.message");
        Translation VIEW_CLIENT_BACKUPS = from("view_client_backups");
        Translation DELETE_CONFIG_PRESET = from("delete_config_preset");
        Translation DELETE_CLIENT_BACKUP = from("delete_client_backup");
        Translation DELETE_SERVER_BACKUP = from("delete_server_backup");
        Translation DELETE_ALL_BACKUPS = from("delete_all_backups");
        Translation DELETE_ALL_PRESETS = from("delete_all_presets");
        Translation INSPECT_FILE = from("inspect_file");
        Translation NONEXISTENT_FILE = from("nonexistent_file");
        Translation SERVER_APPLY = from("server_apply");
        Translation SERVER_BACKUPS = from("server_backups");
        Translation DOWNLOAD_WRITER = from("download_writer");
        Translation VIEW_CONFIG_PRESETS = from("view_config_presets");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.error." + name);
        }
    }

    /**
     * Lang keys that are used by informative overlay screens.
     */
    interface Info
    {
        Translation IMPORT_CLIENT_TITLE = from("import_client.title");
        Translation IMPORT_CLIENT_MESSAGE = from("import_client.message");
        Translation IMPORT_SERVER_TITLE = from("import_server.title");
        Translation IMPORT_SERVER_MESSAGE = from("import_server.message");
        Translation EXPORT_CLIENT_TITLE = from("export_client.title");
        Translation EXPORT_CLIENT_MESSAGE = from("export_client.message");
        Translation EXPORT_SERVER_TITLE = from("export_server.title");
        Translation EXPORT_SERVER_MESSAGE = from("export_server.message");
        Translation CREATE_BACKUP_TITLE = from("create_backup.title");
        Translation CREATE_BACKUP_MESSAGE = from("create_backup.message");
        Translation CREATE_PRESET_TITLE = from("create_preset.title");
        Translation CREATE_PRESET_MESSAGE = from("create_preset.message");
        Translation SENT_PRESET_TITLE = from("sent_preset.title");
        Translation SENT_PRESET_MESSAGE = from("sent_preset.message");
        Translation FILTER_BY_CATEGORY = from("filter_by_category");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.info." + name);
        }
    }

    /**
     * Lang keys that are used by the color picker overlay.
     */
    interface Colorize
    {
        Translation TITLE = from("title");
        Translation OPEN = from("open");
        Translation SAMPLE = from("sample");
        Translation OPACITY = from("opacity");
        Translation PALETTE = from("palette");
        Translation HINT = from("hint");
        Translation RANDOM = from("random");
        Translation RECENT = from("recent");
        Translation RANDOMIZE = from("randomize");
        Translation CLEAR = from("clear");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.colorize." + name);
        }
    }

    /**
     * Lang keys that are used by the item picker overlay.
     */
    interface Itemize
    {
        Translation TITLE = from("title");
        Translation OPEN = from("open");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.itemize." + name);
        }
    }

    /**
     * Lang keys that are used by the status overlay screen.
     */
    interface Status
    {
        Translation TITLE = from("title");
        Translation LOADED = from("loaded");
        Translation WAIT = from("wait");
        Translation WARN = from("warn");
        Translation FAIL = from("fail");
        Translation CONFLICT = from("conflict");
        Translation CLIENT_ONLY = from("client_only");
        Translation NO_CONNECTION = from("no_connection");
        Translation DYNAMIC = from("dynamic");
        Translation DYNAMIC_OPERATOR = from("dynamic_operator");
        Translation DYNAMIC_VERIFIED = from("dynamic_verified");
        Translation DYNAMIC_UNVERIFIED = from("dynamic_unverified");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.status." + name);
        }
    }

    /**
     * Lang keys that are used by the config management overlay screen.
     */
    interface Manage
    {
        Translation TITLE = from("title");
        Translation HOT_SWAP = from("hot_swap");
        Translation HOT_SWAP_INFO = from("hot_swap.info");
        Translation TOGGLE_VIEW_SCREEN = from("menu_options.view_screen");
        Translation TOGGLE_VIEW_INFO = from("menu_options.view_info");
        Translation PERSISTENT_SCREEN = from("menu_options.persistent_screen");
        Translation PERSISTENT_INFO = from("menu_options.persistent_info");
        Translation PERSISTENCE = from("menu_options.persistence");
        Translation RESET_CONFIG = from("menu_options.reset_config");
        Translation RESET_CONFIG_INFO = from("menu_options.reset_config_info");
        Translation RESET_CONFIG_BACKUP = from("menu_options.reset_config_backup");
        Translation IMPORT_ADVISORY_TITLE = from("import_advisory.title");
        Translation IMPORT_ADVISORY_MESSAGE = from("import_advisory.message");
        Translation IMPORT_EXPORT_HEADER = from("import_export.header");
        Translation CLIENT_IMPORT = from("import_export.client_import");
        Translation CLIENT_EXPORT = from("import_export.client_export");
        Translation SERVER_IMPORT = from("import_export.server_import");
        Translation SERVER_EXPORT = from("import_export.server_export");
        Translation CREATE_BACKUP_HELP = from("create_backup.help");
        Translation CREATE_BACKUP_VIEW = from("create_backup.view");
        Translation CREATE_BACKUP_CLIENT = from("create_backup.client");
        Translation CREATE_BACKUP_SERVER = from("create_backup.server");
        Translation VIEW_BACKUPS_WAITING = from("view_backups.waiting");
        Translation VIEW_BACKUPS_DISCONNECTED = from("view_backups.disconnected");
        Translation VIEW_BACKUPS_LAST_MODIFIED = from("view_backups.last_modified");
        Translation VIEW_BACKUPS_FILENAME = from("view_backups.filename");
        Translation VIEW_BACKUPS_EMPTY = from("view_backups.empty");
        Translation CONFIG_PRESETS_EMPTY = from("config_presets.empty");
        Translation CONFIG_PRESETS_FILENAME = from("config_presets.filename");
        Translation CONFIG_PRESETS_LAST_MODIFIED = from("config_presets.last_modified");
        Translation OPERATIONS_WIP = from("operations.wip");
        Translation OPERATIONS_WIP_MESSAGE = from("operations.wip.message");
        Translation OPERATIONS_LAN = from("operations.lan");
        Translation OPERATIONS_LAN_MESSAGE = from("operations.lan.message");
        Translation OPERATIONS_SSO = from("operations.sso");
        Translation OPERATIONS_SSO_MESSAGE = from("operations.sso.message");
        Translation OPERATIONS_LOGGING = from("operations.logging");
        Translation OPERATIONS_LOGGING_MESSAGE = from("operations.logging.message");
        Translation OPERATIONS_DEBUG = from("operations.debug");
        Translation OPERATIONS_DEBUG_MESSAGE = from("operations.debug.message");
        Translation TOGGLE_ALL_HEADER = from("toggle_all.header");
        Translation TOGGLE_ALL_ENABLE = from("toggle_all.enable");
        Translation TOGGLE_ALL_DISABLE = from("toggle_all.disable");
        Translation TOGGLE_ALL_LOCAL = from("toggle_all.local");
        Translation TOGGLE_ALL_NETWORK = from("toggle_all.network");
        Translation TOGGLE_ALL_LOCAL_INFO = from("toggle_all.local.info");
        Translation TOGGLE_ALL_NETWORK_INFO = from("toggle_all.network.info");
        Translation TOGGLE_ALL_ENABLE_INFO = from("toggle_all.enable.info");
        Translation TOGGLE_ALL_DISABLE_INFO = from("toggle_all.disable.info");
        Translation TOGGLE_ALL_APPLY_INFO = from("toggle_all.apply.info");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.manage." + name);
        }
    }

    /**
     * Lang keys that are used by the help section in the config management overlay screen.
     */
    interface Help
    {
        Translation HEADER = from("header");
        Translation TWEAK_TAGS_TITLE = from("tweak_tags.title");
        Translation TWEAK_TAGS_MESSAGE = from("tweak_tags.message");
        Translation SEARCH_TAGS_TITLE = from("search_tags.title");
        Translation SEARCH_TAGS_MESSAGE = from("search_tags.message");
        Translation SHORTCUT_TITLE = from("shortcut.title");
        Translation SHORTCUT_MESSAGE = from("shortcut.message");
        Translation SHORTCUT_SEARCH = from("shortcut.search");
        Translation SHORTCUT_SAVE = from("shortcut.save");
        Translation SHORTCUT_EXIT = from("shortcut.exit");
        Translation SHORTCUT_JUMP = from("shortcut.jump");
        Translation SHORTCUT_ALL = from("shortcut.all");
        Translation SHORTCUT_CATEGORY = from("shortcut.category");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.overlay.manage.help." + name);
        }
    }

    /* Tweak Messages */

    /**
     * Lang keys that are used by tweak alert messages.
     */
    interface Alert
    {
        Translation NONE = from("none");
        Translation VOID = from("void");
        Translation SHIELD = from("shield");
        Translation BRIGHTNESS = from("brightness");
        Translation DYNAMIC_FOG = from("dynamic_fog");
        Translation DYNAMIC_SKY = from("dynamic_sky");
        Translation UNIVERSAL_FOG = from("universal_fog");
        Translation UNIVERSAL_SKY = from("universal_sky");
        Translation OVERWORLD_FOG_DENSITY = from("overworld_fog_density");
        Translation OVERWORLD_FOG_COLOR = from("overworld_fog_color");
        Translation NETHER_FOG_DENSITY = from("nether_fog_density");
        Translation NETHER_FOG_COLOR = from("nether_fog_color");
        Translation FOOD_STACKING = from("food_stacking");
        Translation FOOD_HEALTH = from("food_health");
        Translation ARM_SWAY = from("arm_sway");
        Translation STAMINA_SPRINTING = from("stamina_sprinting");
        Translation WINDOW_TITLE_DISABLED = from("window_title_disabled");
        Translation ROW_HIGHLIGHT_DISABLED = from("row_highlight_disabled");
        Translation CUSTOM_GUI_GRADIENT = from("custom_gui_gradient");
        Translation CUSTOM_FALLING_LOGO_DISABLED = from("custom_falling_logo_disabled");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.alert." + name);
        }
    }

    /**
     * Lang keys that are used by tweak issue messages.
     */
    interface Issue
    {
        Translation SODIUM = from("sodium");
        Translation OPTIFINE = from("optifine");
        Translation POLYTONE = from("polytone");
        Translation IRIS = from("iris");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.issue." + name);
        }
    }

    /**
     * Lang keys that are used by the home screen mod warning overlay.
     */
    interface Warning
    {
        Translation POLYTONE = from("polytone");
        Translation OPTIFINE = from("optifine");

        private static Translation from(String name)
        {
            return new Translation("gui.nostalgic_tweaks.warning." + name);
        }
    }

    /* Enumerations */

    /**
     * Lang keys that tweak's use in enumerations.
     */
    interface Enum
    {
        String PARENT_KEY = "gui.nostalgic_tweaks.enum.";

        Translation CLASSIC = from("generic.classic");
        Translation ALPHA = from("generic.alpha");
        Translation BETA = from("generic.beta");
        Translation MODERN = from("generic.modern");
        Translation ALPHA_BETA = from("generic.alpha_beta");
        Translation PLAIN_CLASSIC = from("generic.plain_classic");
        Translation INF_DEV = from("generic.inf_dev");
        Translation DISABLED = from("generic.disabled");
        Translation MUSIC_ALPHA = from("musicType.alpha");
        Translation MUSIC_BETA = from("musicType.beta");
        Translation MUSIC_BLENDED = from("musicType.blended");
        Translation SCREEN_HOME = from("menuOption.home");
        Translation SCREEN_PACKS = from("menuOption.packs");
        Translation SCREEN_CONFIG = from("menuOption.config");
        Translation SOLID_COLOR = from("colorType.solid");
        Translation GRADIENT_COLOR = from("colorType.gradient");
        Translation STAMINA_REGAIN_NORMAL = from("staminaRegain.normal");
        Translation STAMINA_REGAIN_HALF = from("staminaRegain.half");
        Translation STAMINA_REGAIN_NONE = from("staminaRegain.none");
        Translation BACKGROUND_SOLID_BLACK = from("guiBackground.solid_black");
        Translation BACKGROUND_SOLID_BLUE = from("guiBackground.solid_blue");
        Translation BACKGROUND_GRADIENT_BLUE = from("guiBackground.gradient_blue");
        Translation RECIPE_BOOK_DISABLED = from("recipeBook.disabled");
        Translation RECIPE_BOOK_LARGE = from("recipeBook.large");
        Translation RECIPE_BOOK_SMALL = from("recipeBook.small");
        Translation DEBUG_CHART_DISABLED = from("debugChart.disabled");
        Translation DEBUG_CHART_CLASSIC = from("debugChart.classic");
        Translation DEBUG_CHART_MODERN = from("debugChart.modern");
        Translation INVENTORY_SHIELD_INVISIBLE = from("inventoryShield.invisible");
        Translation INVENTORY_SHIELD_MIDDLE_RIGHT = from("inventoryShield.middle_right");
        Translation INVENTORY_SHIELD_BOTTOM_LEFT = from("inventoryShield.bottom_left");
        Translation RENDER_ORDER_FIRST = from("renderOrder.first");
        Translation RENDER_ORDER_LAST = from("renderOrder.last");
        Translation ANIMATION_LINEAR = from("animationType.linear");
        Translation ANIMATION_EASE_IN_SINE = from("animationType.ease_in_sine");
        Translation ANIMATION_EASE_IN_EXPO = from("animationType.ease_in_expo");
        Translation ANIMATION_EASE_IN_CUBIC = from("animationType.ease_in_cubic");
        Translation ANIMATION_EASE_IN_CIRCULAR = from("animationType.ease_in_circular");
        Translation ANIMATION_EASE_OUT_SINE = from("animationType.ease_out_sine");
        Translation ANIMATION_EASE_OUT_EXPO = from("animationType.ease_out_expo");
        Translation ANIMATION_EASE_OUT_CUBIC = from("animationType.ease_out_cubic");
        Translation ANIMATION_EASE_OUT_CIRCULAR = from("animationType.ease_out_circular");
        Translation ANIMATION_EASE_IN_OUT_SINE = from("animationType.ease_in_out_sine");
        Translation ANIMATION_EASE_IN_OUT_EXPO = from("animationType.ease_in_out_expo");
        Translation ANIMATION_EASE_IN_OUT_CUBIC = from("animationType.ease_in_out_cubic");
        Translation ANIMATION_EASE_IN_OUT_CIRCULAR = from("animationType.ease_in_out_circular");
        Translation CORNER_TOP_LEFT = from("corner.top_left");
        Translation CORNER_TOP_RIGHT = from("corner.top_right");
        Translation CORNER_BOTTOM_LEFT = from("corner.bottom_left");
        Translation CORNER_BOTTOM_RIGHT = from("corner.bottom_right");
        Translation FOG_ALPHA_R164 = from("worldFog.alpha_r164");
        Translation FOG_R17_R118 = from("worldFog.r17_r118");

        private static Translation from(String name)
        {
            return new Translation(PARENT_KEY + name);
        }
    }

    /* Language Keys */

    /**
     * Functional helper that provides a component supplier that changes based on the given boolean supplier.
     *
     * @param supplier A {@link Supplier} that yields a {@code boolean}.
     * @param ifTrue   A {@link Translation} to get a {@link Component} from when the given supplier is {@code true}.
     * @param ifFalse  A {@link Translation} to get a {@link Component} from when the given supplier is {@code false}.
     * @return A {@link Supplier} that yields a {@link Component} based on the given arguments.
     */
    static Supplier<Component> supply(Supplier<Boolean> supplier, Translation ifTrue, Translation ifFalse)
    {
        return () -> supplier.get() ? ifTrue.get() : ifFalse.get();
    }
}
