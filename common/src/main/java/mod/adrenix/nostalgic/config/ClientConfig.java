package mod.adrenix.nostalgic.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.config.factory.Config;
import mod.adrenix.nostalgic.config.factory.ConfigMeta;
import mod.adrenix.nostalgic.config.factory.LoaderException;
import mod.adrenix.nostalgic.tweak.TweakValidator;
import mod.adrenix.nostalgic.tweak.config.*;
import mod.adrenix.nostalgic.tweak.enums.*;
import mod.adrenix.nostalgic.tweak.listing.ItemMap;
import mod.adrenix.nostalgic.tweak.listing.ItemSet;
import mod.adrenix.nostalgic.tweak.listing.StringSet;

/**
 * <b color=red>IMPORTANT</b>
 * <p>
 * Any update to a field name in this class that is associated with a server tweak must update its counterpart field
 * name in the server config structure class as well.
 *
 * @see mod.adrenix.nostalgic.config.ServerConfig
 */

// This class only serves as a structure definition for Gson
@SuppressWarnings("unused")
@Config(filename = NostalgicTweaks.MOD_ID)
public class ClientConfig implements ConfigMeta
{
    /* Config Metadata */

    @Override
    public void validate() throws LoaderException
    {
        new TweakValidator(true).scan(ClientConfig.class);
    }

    /* Root */

    public boolean modEnabled = ModTweak.ENABLED.register("modEnabled");
    public boolean restrictedLan = ModTweak.RESTRICTED_LAN.register("restrictedLan");
    public boolean serverSideOnly = ModTweak.SERVER_SIDE_ONLY.register("serverSideOnly");
    public boolean serverLogging = ModTweak.SERVER_LOGGING.register("serverLogging");
    public boolean serverDebugMode = ModTweak.SERVER_DEBUG.register("serverDebugMode");

    /* Client Config */

    public static class Mod
    {
        // Internal

        public StringSet favoriteTweaks = ModTweak.FAVORITE_TWEAKS.register("favoriteTweaks");
        public boolean openedConfigScreen = ModTweak.OPENED_CONFIG_SCREEN.register("openedConfigScreen");
        public boolean openedSodiumScreen = ModTweak.OPENED_SODIUM_SCREEN.register("openedSodiumScreen");
        public boolean openedSupporterScreen = ModTweak.OPENED_SUPPORTER_SCREEN.register("openedSupporterScreen");
        public boolean persistentConfigScreen = ModTweak.PERSISTENT_CONFIG_SCREEN.register("persistentConfigScreen");

        // Menu Hotkeys

        public MenuOption defaultScreen = ModTweak.DEFAULT_SCREEN.register("defaultScreen");
        public int openConfigBinding = ModTweak.OPEN_CONFIG_BINDING.register("openConfigBinding");

        // Config Management

        public int numberOfBackups = ModTweak.NUMBER_OF_BACKUPS.register("numberOfBackups");

        // Toast Control

        public boolean showWelcomeToast = ModTweak.SHOW_WELCOME_TOAST.register("showWelcomeToast");
        public boolean showHandshakeToast = ModTweak.SHOW_HANDSHAKE_TOAST.register("showHandshakeToast");
        public boolean showLanChangeToast = ModTweak.SHOW_LAN_CHANGE_TOAST.register("showLanChangeToast");
        public boolean showServerboundToast = ModTweak.SHOW_SERVERBOUND_TOAST.register("showServerboundToast");
        public boolean showClientboundToast = ModTweak.SHOW_CLIENTBOUND_TOAST.register("showClientboundToast");

        // Menu Visuals

        public boolean smoothScroll = ModTweak.SMOOTH_SCROLL.register("smoothScroll");
        public int menuBackgroundOpacity = ModTweak.MENU_BACKGROUND_OPACITY.register("menuBackgroundOpacity");

        // Menu Tags

        public boolean displayNewTags = ModTweak.DISPLAY_NEW_TAGS.register("displayNewTags");
        public boolean displayTagTooltips = ModTweak.DISPLAY_TAG_TOOLTIPS.register("displayTagTooltips");

        // Menu Tree

        public boolean displayCategoryTree = ModTweak.DISPLAY_CATEGORY_TREE.register("displayCategoryTree");
        public int categoryTreeOpacity = ModTweak.CATEGORY_TREE_OPACITY.register("categoryTreeOpacity");

        // Menu Rows

        public boolean displayRowHighlight = ModTweak.DISPLAY_ROW_HIGHLIGHT.register("displayRowHighlight");
        public boolean displayRowHighlightFade = ModTweak.DISPLAY_ROW_HIGHLIGHT_FADE.register("displayRowHighlightFade");
        public boolean overrideRowHighlight = ModTweak.OVERRIDE_ROW_HIGHLIGHT.register("overrideRowHighlight");
        public int rowHighlightOpacity = ModTweak.ROW_HIGHLIGHT_OPACITY.register("rowHighlightOpacity");
    }

    public Mod mod = new Mod();

    public static class Sound
    {
        // Disabled

        public StringSet disabledPositionedSounds = SoundTweak.DISABLED_POSITIONED_SOUNDS.register("disabledPositionedSounds");
        public StringSet disabledGlobalSounds = SoundTweak.DISABLED_GLOBAL_SOUNDS.register("disabledGlobalSounds");

        // Music

        public boolean playMusicWhenPaused = SoundTweak.PLAY_MUSIC_WHEN_PAUSED.register("playMusicWhenPaused");
        public MusicType musicForMenu = SoundTweak.MUSIC_FOR_MENU.register("musicForMenu");
        public MusicType musicForCreative = SoundTweak.MUSIC_FOR_CREATIVE.register("musicForCreative");
        public boolean replaceOverworldBiomeMusic = SoundTweak.REPLACE_OVERWORLD_BIOME_MUSIC.register("replaceOverworldBiomeMusic");
        public boolean replaceNetherBiomeMusic = SoundTweak.REPLACE_NETHER_BIOME_MUSIC.register("replaceNetherBiomeMusic");
        public boolean replaceGameplayMusic = SoundTweak.REPLACE_GAMEPLAY_MUSIC.register("replaceGameplayMusic");

        // Music Controls

        public int stopSongBinding = SoundTweak.STOP_SONG_BINDING.register("stopSongBinding");
        public int nextSongBinding = SoundTweak.NEXT_SONG_BINDING.register("nextSongBinding");

        // Ambient

        public boolean oldCaveSounds = SoundTweak.OLD_CAVE_SOUNDS.register("oldCaveSounds");
        public boolean oldCaveAmbience = SoundTweak.OLD_CAVE_AMBIENCE.register("oldCaveAmbience");
        public boolean disableNetherAmbience = SoundTweak.DISABLE_NETHER_AMBIENCE.register("disableNetherAmbience");
        public boolean disableWaterAmbience = SoundTweak.DISABLE_WATER_AMBIENCE.register("disableWaterAmbience");

        // Bed Block

        public boolean oldBed = SoundTweak.OLD_BED.register("oldBed");
        public boolean disableBedPlace = SoundTweak.DISABLE_BED_PLACE.register("disableBedPlace");

        // Chest Block

        public boolean oldChest = SoundTweak.OLD_CHEST.register("oldChest");
        public boolean disableChest = SoundTweak.DISABLE_CHEST.register("disableChest");

        // Lava Block

        public boolean disableLavaAmbience = SoundTweak.DISABLE_LAVA_AMBIENCE.register("disableLavaAmbience");
        public boolean disableLavaPop = SoundTweak.DISABLE_LAVA_POP.register("disableLavaPop");

        // Furnace Block

        public boolean disableFurnace = SoundTweak.DISABLE_FURNACE.register("disableFurnace");
        public boolean disableBlastFurnace = SoundTweak.DISABLE_BLAST_FURNACE.register("disableBlastFurnace");

        // Blocks

        public boolean disableGrowth = SoundTweak.DISABLE_GROWTH.register("disableGrowth");
        public boolean disableDoorPlace = SoundTweak.DISABLE_DOOR_PLACE.register("disableDoorPlace");
        public boolean disableBubbleColumn = SoundTweak.DISABLE_BUBBLE_COLUMN.register("disableBubbleColumn");

        // Damage

        public boolean oldAttack = SoundTweak.OLD_ATTACK.register("oldAttack");
        public boolean oldHurt = SoundTweak.OLD_HURT.register("oldHurt");
        public boolean oldFall = SoundTweak.OLD_FALL.register("oldFall");

        // Experience

        public boolean oldXp = SoundTweak.OLD_XP.register("oldXp");
        public boolean disableXpPickup = SoundTweak.DISABLE_XP_PICKUP.register("disableXpPickup");
        public boolean disableXpLevel = SoundTweak.DISABLE_XP_LEVEL.register("disableXpLevel");

        // Mobs

        public boolean disableGenericSwim = SoundTweak.DISABLE_GENERIC_SWIM.register("disableGenericSwim");
        public boolean disableFishSwim = SoundTweak.DISABLE_FISH_SWIM.register("disableFishSwim");
        public boolean disableFishHurt = SoundTweak.DISABLE_FISH_HURT.register("disableFishHurt");
        public boolean disableFishDeath = SoundTweak.DISABLE_FISH_DEATH.register("disableFishDeath");
        public boolean disableSquid = SoundTweak.DISABLE_SQUID.register("disableSquid");
        public boolean disableGlowSquidOther = SoundTweak.DISABLE_GLOW_SQUID_OTHER.register("disableGlowSquidOther");
        public boolean disableGlowSquidAmbience = SoundTweak.DISABLE_GLOW_SQUID_AMBIENCE.register("disableGlowSquidAmbience");
        public boolean oldStep = SoundTweak.OLD_STEP.register("oldStep");
        public boolean ignoreModdedStep = SoundTweak.IGNORE_MODDED_STEP.register("ignoreModdedStep");
    }

    public Sound sound = new Sound();

    public static class EyeCandy
    {
        // Block

        public boolean sodiumWaterAo = CandyTweak.SODIUM_WATER_AO.register("sodiumWaterAo");
        public boolean oldGrassSideTexture = CandyTweak.OLD_GRASS_SIDE_TEXTURE.register("oldGrassSideTexture");
        public boolean oldFastGrassTexture = CandyTweak.OLD_FAST_GRASS_TEXTURE.register("oldFastGrassTexture");
        public boolean removeMipmapTexture = CandyTweak.REMOVE_MIPMAP_TEXTURE.register("removeMipmapTexture");
        public ItemSet ambientOcclusionBlocks = CandyTweak.AMBIENT_OCCLUSION_BLOCKS.register("ambientOcclusionBlocks");
        public ItemSet disableBlockOffsets = CandyTweak.DISABLE_BLOCK_OFFSETS.register("disableBlockOffsets");
        public boolean disableAllOffset = CandyTweak.DISABLE_ALL_OFFSET.register("disableAllOffset");
        public MissingTexture oldMissingTexture = CandyTweak.OLD_MISSING_TEXTURE.register("oldMissingTexture");

        // Hitbox Outline

        public boolean applyFullBlockCollisions = CandyTweak.APPLY_FULL_BLOCK_COLLISIONS.register("applyFullBlockCollisions");
        public ItemSet fullBlockCollisions = CandyTweak.FULL_BLOCK_COLLISIONS.register("fullBlockCollisions");
        public ItemSet fullBlockOutlines = CandyTweak.FULL_BLOCK_OUTLINES.register("fullBlockOutlines");
        public String blockOutlineColor = CandyTweak.BLOCK_OUTLINE_COLOR.register("blockOutlineColor");
        public float blockOutlineThickness = CandyTweak.BLOCK_OUTLINE_THICKNESS.register("blockOutlineThickness");
        public boolean disableHitboxOverride = CandyTweak.DISABLE_HITBOX_OVERRIDE.register("disableHitboxOverride");

        // Hitbox Overlay

        public boolean oldBlockOverlay = CandyTweak.OLD_BLOCK_OVERLAY.register("oldBlockOverlay");
        public RenderOrder overlayRenderOrder = CandyTweak.BLOCK_OVERLAY_RENDER_ORDER.register("overlayRenderOrder");
        public ColorType blockOverlayColorType = CandyTweak.BLOCK_OVERLAY_COLOR_TYPE.register("blockOverlayColorType");
        public String blockOverlayColor = CandyTweak.BLOCK_OVERLAY_COLOR.register("blockOverlayColor");
        public String blockOverlayGradientTop = CandyTweak.BLOCK_OVERLAY_GRADIENT_TOP.register("blockOverlayGradientTop");
        public String blockOverlayGradientBottom = CandyTweak.BLOCK_OVERLAY_GRADIENT_BOTTOM.register("blockOverlayGradientBottom");
        public boolean pulsateBlockOverlay = CandyTweak.PULSATE_BLOCK_OVERLAY.register("pulsateBlockOverlay");
        public AnimationType pulsateOverlayAnimation = CandyTweak.PULSATE_OVERLAY_ANIMATION.register("pulsateOverlayAnimation");
        public float oldBlockOverlaySpeed = CandyTweak.BLOCK_OVERLAY_SPEED.register("oldBlockOverlaySpeed");
        public float minimumBlockPulsationTransparency = CandyTweak.MINIMUM_PULSATION_TRANSPARENCY.register("minimumBlockPulsationTransparency");
        public float maximumBlockPulsationTransparency = CandyTweak.MAXIMUM_PULSATION_TRANSPARENCY.register("maximumBlockPulsationTransparency");

        // Chests

        public boolean oldChest = CandyTweak.OLD_CHEST.register("oldChest");
        public boolean oldEnderChest = CandyTweak.OLD_ENDER_CHEST.register("oldEnderChest");
        public boolean oldTrappedChest = CandyTweak.OLD_TRAPPED_CHEST.register("oldTrappedChest");
        public boolean applyChestVoxel = CandyTweak.APPLY_CHEST_VOXEL.register("applyChestVoxel");
        public ItemSet oldModChests = CandyTweak.OLD_MOD_CHESTS.register("oldModChests");
        public ItemSet translucentChests = CandyTweak.TRANSLUCENT_CHESTS.register("translucentChests");

        // Torch

        public boolean oldTorchBrightness = CandyTweak.OLD_TORCH_BRIGHTNESS.register("oldTorchBrightness");
        public boolean oldTorchBottom = CandyTweak.OLD_TORCH_BOTTOM.register("oldTorchBottom");
        public boolean oldTorchModel = CandyTweak.OLD_TORCH_MODEL.register("oldTorchModel");
        public boolean oldRedstoneTorchModel = CandyTweak.OLD_REDSTONE_TORCH_MODEL.register("oldRedstoneTorchModel");
        public boolean oldSoulTorchModel = CandyTweak.OLD_SOUL_TORCH_MODEL.register("oldSoulTorchModel");

        // Bed

        public boolean hidePlayerInBed = CandyTweak.HIDE_PLAYER_IN_BED.register("hidePlayerInBed");

        // Heads-up Toasts

        public boolean hideRecipeToasts = CandyTweak.HIDE_RECIPE_TOASTS.register("hideRecipeToasts");
        public boolean hideTutorialToasts = CandyTweak.HIDE_TUTORIAL_TOASTS.register("hideTutorialToasts");
        public boolean hideAdvancementToasts = CandyTweak.HIDE_ADVANCEMENT_TOASTS.register("hideAdvancementToasts");
        public boolean hideAdvancementChats = CandyTweak.HIDE_ADVANCEMENT_CHATS.register("hideAdvancementChats");

        // Heads-up Display

        public boolean hideExperienceBar = CandyTweak.HIDE_EXPERIENCE_BAR.register("hideExperienceBar");
        public boolean hideHungerBar = CandyTweak.HIDE_HUNGER_BAR.register("hideHungerBar");
        public boolean hideStaminaBar = CandyTweak.HIDE_STAMINA_BAR.register("hideStaminaBar");
        public boolean hideStaminaMoving = CandyTweak.HIDE_STAMINA_BAR_MOVING.register("hideStaminaMoving");
        public boolean hideStaminaCooldown = CandyTweak.HIDE_STAMINA_BAR_COOLDOWN.register("hideStaminaCooldown");
        public boolean hideStaminaBarInactive = CandyTweak.HIDE_STAMINA_BAR_INACTIVE.register("hideStaminaBarInactive");

        // Offhand Slot

        public boolean adventureCraftOffhand = CandyTweak.ADVENTURE_CRAFT_OFFHAND.register("adventureCraftOffhand");
        public int leftOffhandOffset = CandyTweak.LEFT_OFFHAND_OFFSET.register("leftOffhandOffset");
        public int rightOffhandOffset = CandyTweak.RIGHT_OFFHAND_OFFSET.register("rightOffhandOffset");

        // Alternative Experience Text

        public boolean showExpLevelText = CandyTweak.SHOW_EXP_LEVEL_TEXT.register("showExpLevelText");
        public boolean showExpLevelInCreative = CandyTweak.SHOW_EXP_LEVEL_IN_CREATIVE.register("showExpLevelInCreative");
        public Corner altExpLevelCorner = CandyTweak.ALT_EXP_LEVEL_CORNER.register("altExpLevelCorner");
        public int altExpLevelOffsetX = CandyTweak.ALT_EXP_LEVEL_OFFSET_X.register("altExpLevelOffsetX");
        public int altExpLevelOffsetY = CandyTweak.ALT_EXP_LEVEL_OFFSET_Y.register("altExpLevelOffsetY");
        public boolean altExpLevelShadow = CandyTweak.ALT_EXP_LEVEL_SHADOW.register("altExpLevelShadow");
        public String altExpLevelText = CandyTweak.ALT_EXP_LEVEL_TEXT.register("altExpLevelText");

        // Alternative Progress Text

        public boolean showExpProgressText = CandyTweak.SHOW_EXP_PROGRESS_TEXT.register("showExpProgressText");
        public boolean showExpProgressInCreative = CandyTweak.SHOW_EXP_PROGRESS_IN_CREATIVE.register("showExpProgressInCreative");
        public boolean useDynamicProgressColor = CandyTweak.USE_DYNAMIC_PROGRESS_COLOR.register("useDynamicProgressColor");
        public Corner altExpProgressCorner = CandyTweak.ALT_EXP_PROGRESS_CORNER.register("altExpProgressCorner");
        public int altExpProgressOffsetX = CandyTweak.ALT_EXP_PROGRESS_OFFSET_X.register("altExpProgressOffsetX");
        public int altExpProgressOffsetY = CandyTweak.ALT_EXP_PROGRESS_OFFSET_Y.register("altExpProgressOffsetY");
        public boolean altExpProgressShadow = CandyTweak.ALT_EXP_PROGRESS_SHADOW.register("altExpProgressShadow");
        public String altExpProgressText = CandyTweak.ALT_EXP_PROGRESS_TEXT.register("altExpProgressText");

        // Alternative Food Text

        public boolean showHungerFoodText = CandyTweak.SHOW_HUNGER_FOOD_TEXT.register("showHungerFoodText");
        public boolean useDynamicFoodColor = CandyTweak.USE_DYNAMIC_FOOD_COLOR.register("useDynamicFoodColor");
        public Corner altHungerFoodCorner = CandyTweak.ALT_HUNGER_FOOD_CORNER.register("altHungerFoodCorner");
        public int altHungerFoodOffsetX = CandyTweak.ALT_HUNGER_FOOD_OFFSET_X.register("altHungerFoodOffsetX");
        public int altHungerFoodOffsetY = CandyTweak.ALT_HUNGER_FOOD_OFFSET_Y.register("altHungerFoodOffsetY");
        public boolean altHungerFoodShadow = CandyTweak.ALT_HUNGER_FOOD_SHADOW.register("altHungerFoodShadow");
        public String altHungerFoodText = CandyTweak.ALT_HUNGER_FOOD_TEXT.register("altHungerFoodText");

        // Alternative Saturation Text

        public boolean showHungerSaturationText = CandyTweak.SHOW_HUNGER_SATURATION_TEXT.register("showHungerSaturationText");
        public boolean useDynamicSaturationColor = CandyTweak.USE_DYNAMIC_SATURATION_COLOR.register("useDynamicSaturationColor");
        public Corner altHungerSaturationCorner = CandyTweak.ALT_HUNGER_SATURATION_CORNER.register("altHungerSaturationCorner");
        public int altHungerSaturationOffsetX = CandyTweak.ALT_HUNGER_SATURATION_OFFSET_X.register("altHungerSaturationOffsetX");
        public int altHungerSaturationOffsetY = CandyTweak.ALT_HUNGER_SATURATION_OFFSET_Y.register("altHungerSaturationOffsetY");
        public boolean altHungerSaturationShadow = CandyTweak.ALT_HUNGER_SATURATION_SHADOW.register("altHungerSaturationShadow");
        public String altHungerSaturationText = CandyTweak.ALT_HUNGER_SATURATION_TEXT.register("altHungerSaturationText");

        // Alternative Stamina Text

        public boolean showStaminaText = CandyTweak.SHOW_STAMINA_TEXT.register("showStaminaText");
        public boolean useDynamicStaminaColor = CandyTweak.USE_DYNAMIC_STAMINA_COLOR.register("useDynamicStaminaColor");
        public boolean altStaminaShowOnActive = CandyTweak.ALT_STAMINA_SHOW_ON_ACTIVE.register("altStaminaShowOnActive");
        public Corner altStaminaCorner = CandyTweak.ALT_STAMINA_CORNER.register("altStaminaCorner");
        public int altStaminaOffsetX = CandyTweak.ALT_STAMINA_OFFSET_X.register("altStaminaOffsetX");
        public int altStaminaOffsetY = CandyTweak.ALT_STAMINA_OFFSET_Y.register("altStaminaOffsetY");
        public boolean altStaminaShadow = CandyTweak.ALT_STAMINA_SHADOW.register("altStaminaShadow");
        public String altStaminaText = CandyTweak.ALT_STAMINA_TEXT.register("altStaminaText");

        // Game Version Overlay

        public boolean oldVersionOverlay = CandyTweak.OLD_VERSION_OVERLAY.register("oldVersionOverlay");
        public boolean oldOverlayShadow = CandyTweak.OLD_OVERLAY_SHADOW.register("oldOverlayShadow");
        public Corner oldOverlayCorner = CandyTweak.OLD_OVERLAY_CORNER.register("oldOverlayCorner");
        public int oldOverlayOffsetX = CandyTweak.OLD_OVERLAY_OFFSET_X.register("oldOverlayOffsetX");
        public int oldOverlayOffsetY = CandyTweak.OLD_OVERLAY_OFFSET_Y.register("oldOverlayOffsetY");
        public String oldOverlayText = CandyTweak.OLD_OVERLAY_TEXT.register("oldOverlayText");

        // Window Title

        public boolean enableWindowTitle = CandyTweak.ENABLE_WINDOW_TITLE.register("enableWindowTitle");
        public boolean matchVersionOverlay = CandyTweak.MATCH_VERSION_OVERLAY.register("matchVersionOverlay");
        public String windowTitleText = CandyTweak.WINDOW_TITLE_TEXT.register("windowTitleText");

        // Debug Screen

        public Generic oldDebug = CandyTweak.OLD_DEBUG.register("oldDebug");
        public boolean debugEntityId = CandyTweak.DEBUG_ENTITY_ID.register("debugEntityId");

        // Debug Chart

        public DebugChart fpsChart = CandyTweak.FPS_CHART.register("fpsChart");
        public boolean showDebugPieChart = CandyTweak.SHOW_DEBUG_PIE_CHART.register("showDebugPieChart");
        public boolean oldPieChartBackground = CandyTweak.OLD_PIE_CHART_BACKGROUND.register("oldPieChartBackground");

        // Debug Color

        public boolean showDebugLeftTextShadow = CandyTweak.SHOW_DEBUG_LEFT_TEXT_SHADOW.register("showDebugLeftTextShadow");
        public boolean showDebugRightTextShadow = CandyTweak.SHOW_DEBUG_RIGHT_TEXT_SHADOW.register("showDebugRightTextShadow");
        public boolean showDebugLeftBackground = CandyTweak.SHOW_DEBUG_LEFT_BACKGROUND.register("showDebugLeftBackground");
        public boolean showDebugRightBackground = CandyTweak.SHOW_DEBUG_RIGHT_BACKGROUND.register("showDebugRightBackground");
        public String debugLeftBackgroundColor = CandyTweak.DEBUG_LEFT_BACKGROUND_COLOR.register("debugLeftBackgroundColor");
        public String debugRightBackgroundColor = CandyTweak.DEBUG_RIGHT_BACKGROUND_COLOR.register("debugRightBackgroundColor");
        public boolean showDebugLeftTextColor = CandyTweak.SHOW_DEBUG_LEFT_TEXT_COLOR.register("showDebugLeftTextColor");
        public boolean showDebugRightTextColor = CandyTweak.SHOW_DEBUG_RIGHT_TEXT_COLOR.register("showDebugRightTextColor");
        public String debugLeftTextColor = CandyTweak.DEBUG_LEFT_TEXT_COLOR.register("debugLeftTextColor");
        public String debugRightTextColor = CandyTweak.DEBUG_RIGHT_TEXT_COLOR.register("debugRightTextColor");

        // Debug Extra

        public boolean showDebugGpuUsage = CandyTweak.SHOW_DEBUG_GPU_USAGE.register("showDebugGpuUsage");
        public boolean showDebugLightData = CandyTweak.SHOW_DEBUG_LIGHT_DATA.register("showDebugLightData");
        public boolean showDebugFacingData = CandyTweak.SHOW_DEBUG_FACING_DATA.register("showDebugFacingData");
        public boolean showDebugTargetData = CandyTweak.SHOW_DEBUG_TARGET_DATA.register("showDebugTargetData");
        public boolean showDebugBiomeData = CandyTweak.SHOW_DEBUG_BIOME_DATA.register("showDebugBiomeData");

        // Inventory Screen

        public boolean oldInventory = CandyTweak.OLD_INVENTORY.register("oldInventory");
        public RecipeBook inventoryBook = CandyTweak.INVENTORY_BOOK.register("inventoryBook");
        public InventoryShield inventoryShield = CandyTweak.INVENTORY_SHIELD.register("inventoryShield");
        public boolean disableEmptyArmorTexture = CandyTweak.DISABLE_EMPTY_ARMOR_TEXTURE.register("disableEmptyArmorTexture");
        public boolean disableEmptyShieldTexture = CandyTweak.DISABLE_EMPTY_SHIELD_TEXTURE.register("disableEmptyShieldTexture");
        public boolean invertedPlayerLighting = CandyTweak.INVERTED_PLAYER_LIGHTING.register("invertedPlayerLighting");
        public Hotbar oldCreativeHotbar = CandyTweak.OLD_CREATIVE_HOTBAR.register("oldCreativeHotbar");

        // Generic Screen

        public boolean oldButtonTextColor = CandyTweak.OLD_BUTTON_TEXT_COLOR.register("oldButtonTextColor");
        public GuiBackground oldGuiBackground = CandyTweak.OLD_GUI_BACKGROUND.register("oldGuiBackground");
        public boolean customGuiBackground = CandyTweak.CUSTOM_GUI_BACKGROUND.register("customGuiBackground");
        public String customGuiTopGradient = CandyTweak.CUSTOM_GUI_TOP_GRADIENT.register("customGuiTopGradient");
        public String customGuiBottomGradient = CandyTweak.CUSTOM_GUI_BOTTOM_GRADIENT.register("customGuiBottomGradient");

        // Loading Overlay

        public Overlay oldLoadingOverlay = CandyTweak.OLD_LOADING_OVERLAY.register("oldLoadingOverlay");
        public boolean removeLoadingBar = CandyTweak.REMOVE_LOADING_BAR.register("removeLoadingBar");
        public boolean customLoadingOverlayBackground = CandyTweak.CUSTOM_LOADING_OVERLAY_BACKGROUND.register("customLoadingOverlayBackground");
        public String loadingOverlayBackgroundColor = CandyTweak.LOADING_OVERLAY_BACKGROUND_COLOR.register("loadingOverlayBackgroundColor");
        public boolean customLoadingProgressBar = CandyTweak.CUSTOM_LOADING_PROGRESS_BAR.register("customLoadingProgressBar");
        public String progressBarOutlineColor = CandyTweak.PROGRESS_BAR_OUTLINE_COLOR.register("progressBarOutlineColor");
        public String progressBarInsideColor = CandyTweak.PROGRESS_BAR_INSIDE_COLOR.register("progressBarInsideColor");

        // Progress Screen

        public boolean oldProgressScreen = CandyTweak.OLD_PROGRESS_SCREEN.register("oldProgressScreen");

        // Pause Screen

        public PauseLayout oldPauseMenu = CandyTweak.OLD_PAUSE_MENU.register("oldPauseMenu");
        public boolean includeModsOnPause = CandyTweak.INCLUDE_MODS_ON_PAUSE.register("includeModsOnPause");
        public boolean removeExtraPauseButtons = CandyTweak.REMOVE_EXTRA_PAUSE_BUTTONS.register("removeExtraPauseButtons");

        // Anvil Screen

        public boolean oldAnvilScreen = CandyTweak.OLD_ANVIL_SCREEN.register("oldAnvilScreen");

        // Crafting Screen

        public boolean oldCraftingScreen = CandyTweak.OLD_CRAFTING_SCREEN.register("oldCraftingScreen");
        public RecipeBook craftingBook = CandyTweak.CRAFTING_BOOK.register("craftingBook");

        // Furnace Screen

        public boolean oldFurnaceScreen = CandyTweak.OLD_FURNACE_SCREEN.register("oldFurnaceScreen");
        public RecipeBook furnaceBook = CandyTweak.FURNACE_BOOK.register("furnaceBook");

        // Chat Screen

        public boolean oldChatInput = CandyTweak.OLD_CHAT_INPUT.register("oldChatInput");
        public boolean oldChatBox = CandyTweak.OLD_CHAT_BOX.register("oldChatBox");
        public boolean disableSignatureBoxes = CandyTweak.DISABLE_SIGNATURE_BOXES.register("disableSignatureBoxes");
        public int chatOffset = CandyTweak.CHAT_OFFSET.register("chatOffset");

        // Death Screen

        public boolean oldDeathScreen = CandyTweak.OLD_DEATH_SCREEN.register("oldDeathScreen");
        public boolean oldDeathScore = CandyTweak.OLD_DEATH_SCORE.register("oldDeathScore");
        public boolean hideCauseOfDeath = CandyTweak.HIDE_CAUSE_OF_DEATH.register("hideCauseOfDeath");

        // World Select Screen

        public Generic oldWorldSelectScreen = CandyTweak.OLD_WORLD_SELECT_SCREEN.register("oldWorldSelectScreen");
        public boolean levelSelectWhenEmpty = CandyTweak.LEVEL_SELECT_WHEN_EMPTY.register("levelSelectWhenEmpty");
        public boolean addWorldThumbnail = CandyTweak.ADD_WORLD_THUMBNAIL.register("addWorldThumbnail");
        public boolean addWorldMetadata = CandyTweak.ADD_WORLD_METADATA.register("addWorldMetadata");
        public boolean ignoreWorldSize = CandyTweak.IGNORE_WORLD_SIZE.register("ignoreWorldSize");

        // World Create Screen

        public boolean oldStyleCreateWorldTabs = CandyTweak.OLD_STYLE_CREATE_WORLD_TABS.register("oldStyleCreateWorldTabs");
        public boolean removeCreateWorldFooter = CandyTweak.REMOVE_CREATE_WORLD_FOOTER.register("removeCreateWorldFooter");

        // Title Screen

        public boolean overrideTitleScreen = CandyTweak.OVERRIDE_TITLE_SCREEN.register("overrideTitleScreen");
        public boolean oldTitleBackground = CandyTweak.OLD_TITLE_BACKGROUND.register("oldTitleBackground");
        public boolean uncapTitleFPS = CandyTweak.UNCAP_TITLE_FPS.register("uncapTitleFPS");

        // Title Screen Logo

        public boolean oldAlphaLogo = CandyTweak.OLD_ALPHA_LOGO.register("oldAlphaLogo");
        public boolean useCustomFallingLogo = CandyTweak.USE_CUSTOM_FALLING_LOGO.register("useCustomFallingLogo");
        public Object fallingLogoEditor = CandyTweak.FALLING_LOGO_EDITOR.register("fallingLogoEditor");
        public int splashOffsetX = CandyTweak.SPLASH_OFFSET_X.register("splashOffsetX");
        public int splashOffsetY = CandyTweak.SPLASH_OFFSET_Y.register("splashOffsetY");

        // Title Screen Buttons

        public TitleLayout titleButtonLayout = CandyTweak.TITLE_BUTTON_LAYOUT.register("titleButtonLayout");
        public boolean includeModsOnTitle = CandyTweak.INCLUDE_MODS_ON_TITLE.register("includeModsOnTitle");
        public boolean removeTitleRealmsButton = CandyTweak.REMOVE_TITLE_REALMS_BUTTON.register("removeTitleRealmsButton");
        public boolean removeTitleAccessibilityButton = CandyTweak.REMOVE_TITLE_ACCESSIBILITY_BUTTON.register("removeTitleAccessibilityButton");
        public boolean removeTitleLanguageButton = CandyTweak.REMOVE_TITLE_LANGUAGE_BUTTON.register("removeTitleLanguageButton");
        public boolean removeExtraTitleButtons = CandyTweak.REMOVE_EXTRA_TITLE_BUTTONS.register("removeExtraTitleButtons");
        public boolean addQuitButton = CandyTweak.ADD_QUIT_BUTTON.register("addQuitButton");

        // Title Screen Text

        public String titleVersionText = CandyTweak.TITLE_VERSION_TEXT.register("titleVersionText");
        public boolean titleBottomLeftText = CandyTweak.TITLE_BOTTOM_LEFT_TEXT.register("titleBottomLeftText");
        public boolean titleTopRightDebugText = CandyTweak.TITLE_TOP_RIGHT_DEBUG_TEXT.register("titleTopRightDebugText");
        public boolean removeTitleModLoaderText = CandyTweak.REMOVE_TITLE_MOD_LOADER_TEXT.register("removeTitleModLoaderText");

        // Tooltips

        public boolean oldTooltipBoxes = CandyTweak.OLD_TOOLTIP_BOXES.register("oldTooltipBoxes");
        public boolean oldNoItemTooltips = CandyTweak.OLD_NO_ITEM_TOOLTIPS.register("oldNoItemTooltips");

        // Tooltip Parts

        public boolean showEnchantmentTip = CandyTweak.SHOW_ENCHANTMENT_TIP.register("showEnchantmentTip");
        public boolean showModifierTip = CandyTweak.SHOW_MODIFIER_TIP.register("showModifierTip");
        public boolean showDyeTip = CandyTweak.SHOW_DYE_TIP.register("showDyeTip");

        // Tooltip Color

        public ColorType tooltipColorType = CandyTweak.TOOLTIP_COLOR_TYPE.register("tooltipColorType");
        public String tooltipBackgroundColor = CandyTweak.TOOLTIP_BACKGROUND_COLOR.register("tooltipBackgroundColor");
        public String tooltipGradientTop = CandyTweak.TOOLTIP_GRADIENT_TOP.register("tooltipGradientTop");
        public String tooltipGradientBottom = CandyTweak.TOOLTIP_GRADIENT_BOTTOM.register("tooltipGradientBottom");

        // Items

        public boolean fixItemModelGap = CandyTweak.FIX_ITEM_MODEL_GAP.register("fixItemModelGap");
        public boolean oldDamageArmorTint = CandyTweak.OLD_DAMAGE_ARMOR_TINT.register("oldDamageArmorTint");
        public boolean oldItemHolding = CandyTweak.OLD_ITEM_HOLDING.register("oldItemHolding");
        public ItemSet ignoredHoldingItems = CandyTweak.IGNORED_HOLDING_ITEMS.register("ignoredHoldingItems");

        // Item Merging

        public int itemMergeLimit = CandyTweak.ITEM_MERGE_LIMIT.register("itemMergeLimit");
        public boolean oldItemMerging = CandyTweak.OLD_ITEM_MERGING.register("oldItemMerging");

        // 2D Items

        public boolean old2dItems = CandyTweak.OLD_2D_ITEMS.register("old2dItems");
        public boolean old2dColors = CandyTweak.OLD_2D_COLORS.register("old2dColors");
        public boolean old2dRendering = CandyTweak.OLD_2D_RENDERING.register("old2dRendering");
        public ItemSet old2dExceptions = CandyTweak.OLD_2D_EXCEPTIONS.register("old2dExceptions");
        public boolean disableEnchantedGroundItems = CandyTweak.DISABLE_ENCHANTED_GROUND_ITEMS.register("disableEnchantedGroundItems");
        public boolean disableEnchantedStaticItems = CandyTweak.DISABLE_ENCHANTED_STATIC_ITEMS.register("disableEnchantedStaticItems");

        // Item Display

        public boolean oldDurabilityColors = CandyTweak.OLD_DURABILITY_COLORS.register("oldDurabilityColors");
        public boolean oldNoSelectedItemName = CandyTweak.OLD_NO_SELECTED_ITEM_NAME.register("oldNoSelectedItemName");
        public boolean oldPlainSelectedItemName = CandyTweak.OLD_PLAIN_SELECTED_ITEM_NAME.register("oldPlainSelectedItemName");

        // Name Tag

        public boolean oldNameTags = CandyTweak.OLD_NAME_TAGS.register("oldNameTags");
        public boolean supporterTags = CandyTweak.SUPPORTER_TAGS.register("supporterTags");

        // Block Lighting

        public boolean disableLightFlicker = CandyTweak.DISABLE_LIGHT_FLICKER.register("disableLightFlicker");
        public boolean invertedBlockLighting = CandyTweak.INVERTED_BLOCK_LIGHTING.register("invertedBlockLighting");
        public boolean oldLeavesLighting = CandyTweak.OLD_LEAVES_LIGHTING.register("oldLeavesLighting");
        public boolean oldWaterLighting = CandyTweak.OLD_WATER_LIGHTING.register("oldWaterLighting");
        public boolean chestLightBlock = CandyTweak.CHEST_LIGHT_BLOCK.register("chestLightBlock");

        // Light Engine

        public boolean roundRobinRelight = CandyTweak.ROUND_ROBIN_RELIGHT.register("roundRobinRelight");
        public boolean oldNetherLighting = CandyTweak.OLD_NETHER_LIGHTING.register("oldNetherLighting");
        public boolean oldSmoothLighting = CandyTweak.OLD_SMOOTH_LIGHTING.register("oldSmoothLighting");
        public boolean disableSmoothLighting = CandyTweak.DISABLE_SMOOTH_LIGHTING.register("disableSmoothLighting");
        public boolean oldClassicEngine = CandyTweak.OLD_CLASSIC_ENGINE.register("oldClassicEngine");

        // Lightmap Texture

        public boolean smoothLightTransition = CandyTweak.SMOOTH_LIGHT_TRANSITION.register("smoothLightTransition");
        public boolean dynamicLightBrightness = CandyTweak.DYNAMIC_LIGHT_BRIGHTNESS.register("dynamicLightBrightness");
        public boolean disableLightBrightness = CandyTweak.DISABLE_LIGHT_BRIGHTNESS.register("disableLightBrightness");
        public boolean oldLightColor = CandyTweak.OLD_LIGHT_COLOR.register("oldLightColor");

        // Shader Support

        public int maxBlockLight = CandyTweak.MAX_BLOCK_LIGHT.register("maxBlockLight");

        // Disabled Particles

        public StringSet disabledParticles = CandyTweak.DISABLED_PARTICLES.register("disabledParticles");

        // Experience Particles

        public boolean oldOpaqueExperience = CandyTweak.OLD_OPAQUE_EXPERIENCE.register("oldOpaqueExperience");

        // Biome Particles

        public boolean disableNetherParticles = CandyTweak.DISABLE_NETHER_PARTICLES.register("disableNetherParticles");
        public boolean disableUnderwaterParticles = CandyTweak.DISABLE_UNDERWATER_PARTICLES.register("disableUnderwaterParticles");

        // Block Particles

        public boolean disableLavaParticles = CandyTweak.DISABLE_LAVA_PARTICLES.register("disableLavaParticles");
        public boolean disableLavaDripParticles = CandyTweak.DISABLE_LAVA_DRIP_PARTICLES.register("disableLavaDripParticles");
        public boolean disableWaterDripParticles = CandyTweak.DISABLE_WATER_DRIP_PARTICLES.register("disableWaterDripParticles");
        public boolean disableLeverParticles = CandyTweak.DISABLE_LEVER_PARTICLES.register("disableLeverParticles");
        public boolean disableModelDestructionParticles = CandyTweak.DISABLE_MODEL_DESTRUCTION_PARTICLES.register("disableModelDestructionParticles");
        public boolean disableEnderChestParticles = CandyTweak.DISABLE_ENDER_CHEST_PARTICLES.register("disableEnderChestParticles");
        public boolean disableGrowthParticles = CandyTweak.DISABLE_GROWTH_PARTICLES.register("disableGrowthParticles");

        // Player Particles

        public boolean disableFallingParticles = CandyTweak.DISABLE_FALLING_PARTICLES.register("disableFallingParticles");
        public boolean disableSprintingParticles = CandyTweak.DISABLE_SPRINTING_PARTICLES.register("disableSprintingParticles");
        public boolean hideFirstPersonMagicParticles = CandyTweak.HIDE_FIRST_PERSON_MAGIC_PARTICLES.register("hideFirstPersonMagicParticles");

        // Boat Particles

        public boolean oldBoatMovementParticles = CandyTweak.OLD_BOAT_MOVEMENT_PARTICLES.register("oldBoatMovementParticles");

        // Attack Particles

        public boolean oldSweepParticles = CandyTweak.OLD_SWEEP_PARTICLES.register("oldSweepParticles");
        public boolean oldNoDamageParticles = CandyTweak.OLD_NO_DAMAGE_PARTICLES.register("oldNoDamageParticles");
        public boolean oldNoCritParticles = CandyTweak.OLD_NO_CRIT_PARTICLES.register("oldNoCritParticles");
        public boolean oldNoMagicHitParticles = CandyTweak.OLD_NO_MAGIC_HIT_PARTICLES.register("oldNoMagicHitParticles");

        // Explosion Particles

        public boolean oldExplosionParticles = CandyTweak.OLD_EXPLOSION_PARTICLES.register("oldExplosionParticles");
        public boolean oldMixedExplosionParticles = CandyTweak.OLD_MIXED_EXPLOSION_PARTICLES.register("oldMixedExplosionParticles");
        public boolean unoptimizedExplosionParticles = CandyTweak.UNOPTIMIZED_EXPLOSION_PARTICLES.register("unoptimizedExplosionParticles");

        // World

        public boolean oldSquareBorder = CandyTweak.OLD_SQUARE_BORDER.register("oldSquareBorder");

        // World Fog

        public WorldFog oldWorldFog = CandyTweak.OLD_WORLD_FOG.register("oldWorldFog");
        public int fogBinding = CandyTweak.FOG_BINDING.register("fogBinding");
        public boolean oldNetherFog = CandyTweak.OLD_NETHER_FOG.register("oldNetherFog");
        public boolean oldSunriseSunsetFog = CandyTweak.OLD_SUNRISE_SUNSET_FOG.register("oldSunriseSunsetFog");
        public boolean oldDarkFog = CandyTweak.OLD_DARK_FOG.register("oldDarkFog");
        public boolean oldDynamicFogColor = CandyTweak.OLD_DYNAMIC_FOG_COLOR.register("oldDynamicFogColor");
        public FogColor universalFogColor = CandyTweak.UNIVERSAL_FOG_COLOR.register("universalFogColor");

        // Custom Fog

        public boolean useCustomOverworldFogDensity = CandyTweak.USE_CUSTOM_OVERWORLD_FOG_DENSITY.register("useCustomOverworldFogDensity");
        public boolean useCustomOverworldFogColor = CandyTweak.USE_CUSTOM_OVERWORLD_FOG_COLOR.register("useCustomOverworldFogColor");
        public boolean useCustomNetherFogDensity = CandyTweak.USE_CUSTOM_NETHER_FOG_DENSITY.register("useCustomNetherFogDensity");
        public boolean useCustomNetherFogColor = CandyTweak.USE_CUSTOM_NETHER_FOG_COLOR.register("useCustomNetherFogColor");

        // Custom Fog Color

        public String customOverworldFogColor = CandyTweak.CUSTOM_OVERWORLD_FOG_COLOR.register("customOverworldFogColor");
        public String customNetherFogColor = CandyTweak.CUSTOM_NETHER_FOG_COLOR.register("customNetherFogColor");

        // Custom Fog Density

        public int customOverworldFogStart = CandyTweak.CUSTOM_OVERWORLD_FOG_START.register("customOverworldFogStart");
        public int customOverworldFogEnd = CandyTweak.CUSTOM_OVERWORLD_FOG_END.register("customOverworldFogEnd");
        public int customNetherFogStart = CandyTweak.CUSTOM_NETHER_FOG_START.register("customNetherFogStart");
        public int customNetherFogEnd = CandyTweak.CUSTOM_NETHER_FOG_END.register("customNetherFogEnd");

        // Water Fog

        public boolean oldWaterFogDensity = CandyTweak.OLD_WATER_FOG_DENSITY.register("oldWaterFogDensity");
        public boolean oldWaterFogColor = CandyTweak.OLD_WATER_FOG_COLOR.register("oldWaterFogColor");
        public boolean smoothWaterDensity = CandyTweak.SMOOTH_WATER_DENSITY.register("smoothWaterDensity");
        public boolean smoothWaterColor = CandyTweak.SMOOTH_WATER_COLOR.register("smoothWaterColor");

        // World Sky

        public boolean renderSunriseSunsetColor = CandyTweak.RENDER_SUNRISE_SUNSET_COLOR.register("renderSunriseSunsetColor");
        public boolean oldSunriseAtNorth = CandyTweak.OLD_SUNRISE_AT_NORTH.register("oldSunriseAtNorth");
        public Generic oldStars = CandyTweak.OLD_STARS.register("oldStars");
        public boolean oldDynamicSkyColor = CandyTweak.OLD_DYNAMIC_SKY_COLOR.register("oldDynamicSkyColor");
        public SkyColor universalSkyColor = CandyTweak.UNIVERSAL_SKY_COLOR.register("universalSkyColor");
        public boolean oldNetherSky = CandyTweak.OLD_NETHER_SKY.register("oldNetherSky");
        public int oldCloudHeight = CandyTweak.OLD_CLOUD_HEIGHT.register("oldCloudHeight");

        // Custom Sky

        public boolean customOverworldSky = CandyTweak.CUSTOM_OVERWORLD_SKY.register("customOverworldSky");
        public String customOverworldSkyColor = CandyTweak.CUSTOM_OVERWORLD_SKY_COLOR.register("customOverworldSkyColor");

        // Void Sky

        public Generic oldBlueVoid = CandyTweak.OLD_BLUE_VOID.register("oldBlueVoid");
        public boolean oldBlueVoidOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.register("oldBlueVoidOverride");
        public boolean oldDarkVoidHeight = CandyTweak.OLD_DARK_VOID_HEIGHT.register("oldDarkVoidHeight");
        public boolean customVoidSky = CandyTweak.CUSTOM_VOID_SKY.register("customVoidSky");
        public String customVoidSkyColor = CandyTweak.CUSTOM_VOID_SKY_COLOR.register("customVoidSkyColor");

        // Void Fog

        public boolean renderVoidFog = CandyTweak.RENDER_VOID_FOG.register("renderVoidFog");
        public boolean creativeVoidFog = CandyTweak.CREATIVE_VOID_FOG.register("creativeVoidFog");
        public boolean creativeVoidParticles = CandyTweak.CREATIVE_VOID_PARTICLES.register("creativeVoidParticles");
        public boolean lightRemovesVoidFog = CandyTweak.LIGHT_REMOVES_VOID_FOG.register("lightRemovesVoidFog");
        public int voidFogEncroach = CandyTweak.VOID_FOG_ENCROACH.register("voidFogEncroach");
        public int voidFogStart = CandyTweak.VOID_FOG_START.register("voidFogStart");
        public int voidParticleStart = CandyTweak.VOID_PARTICLE_START.register("voidParticleStart");
        public int voidParticleRadius = CandyTweak.VOID_PARTICLE_RADIUS.register("voidParticleRadius");
        public int voidParticleDensity = CandyTweak.VOID_PARTICLE_DENSITY.register("voidParticleDensity");
    }

    public EyeCandy eyeCandy = new EyeCandy();

    public static class Gameplay
    {
        // Bugs

        public boolean oldLadderGap = GameplayTweak.OLD_LADDER_GAP.register("oldLadderGap");
        public boolean oldSquidMilking = GameplayTweak.OLD_SQUID_MILKING.register("oldSquidMilking");

        // Mob AI

        public boolean disableAnimalPanic = GameplayTweak.DISABLE_ANIMAL_PANIC.register("disableAnimalPanic");
        public boolean disableMonsterAvoidSun = GameplayTweak.DISABLE_MONSTER_AVOID_SUN.register("disableMonsterAvoidSun");
        public boolean disableSkeletonStrafing = GameplayTweak.DISABLE_SKELETON_STRAFING.register("disableSkeletonStrafing");
        public boolean oldCreeperStrafeOnSwell = GameplayTweak.OLD_CREEPER_STRAFE_ON_SWELL.register("oldCreeperStrafeOnSwell");

        // Monsters

        public boolean disableMonsterItemPickup = GameplayTweak.DISABLE_MONSTER_ITEM_PICKUP.register("disableMonsterItemPickup");

        // Monster Spawning

        public boolean disableBabyZombieSpawn = GameplayTweak.DISABLE_BABY_ZOMBIE_SPAWN.register("disableBabyZombieSpawn");
        public boolean disableBabyPiglinSpawn = GameplayTweak.DISABLE_BABY_PIGLIN_SPAWN.register("disableBabyPiglinSpawn");
        public boolean disableMonsterItemSpawn = GameplayTweak.DISABLE_MONSTER_ITEM_SPAWN.register("disableMonsterItemSpawn");
        public boolean disableMonsterArmorSpawn = GameplayTweak.DISABLE_MONSTER_ARMOR_SPAWN.register("disableMonsterArmorSpawn");
        public boolean disableMonsterEnchantSpawn = GameplayTweak.DISABLE_MONSTER_ENCHANT_SPAWN.register("disableMonsterEnchantSpawn");
        public boolean piglinOnlyGoldSwordSpawn = GameplayTweak.PIGLIN_ONLY_GOLD_SWORD_SPAWN.register("piglinOnlyGoldSwordSpawn");
        public int monsterSpawnCap = GameplayTweak.MONSTER_SPAWN_CAP.register("monsterSpawnCap");

        // Animal Spawning

        public int animalSpawnCap = GameplayTweak.ANIMAL_SPAWN_CAP.register("animalSpawnCap");
        public boolean oldAnimalSpawning = GameplayTweak.OLD_ANIMAL_SPAWNING.register("oldAnimalSpawning");
        public boolean disableBabyAnimalSpawning = GameplayTweak.DISABLE_BABY_ANIMAL_SPAWNING.register("disableBabyAnimalSpawning");
        public boolean disableAnimalBreeding = GameplayTweak.DISABLE_ANIMAL_BREEDING.register("disableAnimalBreeding");
        public boolean disableAnimalTempting = GameplayTweak.DISABLE_ANIMAL_TEMPTING.register("disableAnimalTempting");

        // Sheep

        public boolean disableSheepEatGrass = GameplayTweak.DISABLE_SHEEP_EAT_GRASS.register("disableSheepEatGrass");
        public boolean randomSheepWoolRegen = GameplayTweak.RANDOM_SHEEP_WOOL_REGEN.register("randomSheepWoolRegen");
        public boolean oldSheepPunching = GameplayTweak.OLD_SHEEP_PUNCHING.register("oldSheepPunching");
        public boolean oneWoolPunch = GameplayTweak.ONE_WOOL_PUNCH.register("oneWoolPunch");

        // Classic Mob Drops

        public boolean oldZombiePigmenDrops = GameplayTweak.OLD_ZOMBIE_PIGMEN_DROPS.register("oldZombiePigmenDrops");
        public boolean oldSkeletonDrops = GameplayTweak.OLD_SKELETON_DROPS.register("oldSkeletonDrops");
        public boolean oldChickenDrops = GameplayTweak.OLD_CHICKEN_DROPS.register("oldChickenDrops");
        public boolean oldZombieDrops = GameplayTweak.OLD_ZOMBIE_DROPS.register("oldZombieDrops");
        public boolean oldSpiderDrops = GameplayTweak.OLD_SPIDER_DROPS.register("oldSpiderDrops");
        public boolean oldSheepDrops = GameplayTweak.OLD_SHEEP_DROPS.register("oldSheepDrops");
        public boolean oldCowDrops = GameplayTweak.OLD_COW_DROPS.register("oldCowDrops");
        public boolean oldPigDrops = GameplayTweak.OLD_PIG_DROPS.register("oldPigDrops");

        // Modern Mob Drops

        public boolean oldStyleZombieVillagerDrops = GameplayTweak.OLD_STYLE_ZOMBIE_VILLAGER_DROPS.register("oldStyleZombieVillagerDrops");
        public boolean oldStyleCaveSpiderDrops = GameplayTweak.OLD_STYLE_CAVE_SPIDER_DROPS.register("oldStyleCaveSpiderDrops");
        public boolean oldStyleMooshroomDrops = GameplayTweak.OLD_STYLE_MOOSHROOM_DROPS.register("oldStyleMooshroomDrops");
        public boolean oldStyleDrownedDrops = GameplayTweak.OLD_STYLE_DROWNED_DROPS.register("oldStyleDrownedDrops");
        public boolean oldStyleRabbitDrops = GameplayTweak.OLD_STYLE_RABBIT_DROPS.register("oldStyleRabbitDrops");
        public boolean oldStyleStrayDrops = GameplayTweak.OLD_STYLE_STRAY_DROPS.register("oldStyleStrayDrops");
        public boolean oldStyleHuskDrops = GameplayTweak.OLD_STYLE_HUSK_DROPS.register("oldStyleHuskDrops");

        // Combat Blocking

        public boolean blockWithSwordOnShield = GameplayTweak.BLOCK_WITH_SWORD_ON_SHIELD.register("blockWithSwordOnShield");
        public boolean oldSwordBlocking = GameplayTweak.OLD_SWORD_BLOCKING.register("oldSwordBlocking");
        public boolean attackWhileSwordBlocking = GameplayTweak.ATTACK_WHILE_SWORD_BLOCKING.register("attackWhileSwordBlocking");
        public int swordBlockDamageReduction = GameplayTweak.SWORD_BLOCK_DAMAGE_REDUCTION.register("swordBlockDamageReduction");

        // Combat

        public boolean oldDamageValues = GameplayTweak.OLD_DAMAGE_VALUES.register("oldDamageValues");
        public boolean disableCooldown = GameplayTweak.DISABLE_COOLDOWN.register("disableCooldown");
        public boolean disableMissTimer = GameplayTweak.DISABLE_MISS_TIMER.register("disableMissTimer");
        public boolean disableCriticalHit = GameplayTweak.DISABLE_CRITICAL_HIT.register("disableCriticalHit");
        public boolean disableSweep = GameplayTweak.DISABLE_SWEEP.register("disableSweep");

        // Combat Bow

        public int arrowSpeed = GameplayTweak.ARROW_SPEED.register("arrowSpeed");
        public boolean instantBow = GameplayTweak.INSTANT_BOW.register("instantBow");
        public boolean invincibleBow = GameplayTweak.INVINCIBLE_BOW.register("invincibleBow");

        // Experience Orb

        public boolean disableOrbSpawn = GameplayTweak.DISABLE_ORB_SPAWN.register("disableOrbSpawn");
        public boolean disableOrbRendering = GameplayTweak.DISABLE_ORB_RENDERING.register("disableOrbRendering");

        // Experience Blocks

        public boolean disableAnvil = GameplayTweak.DISABLE_ANVIL.register("disableAnvil");
        public boolean disableEnchantTable = GameplayTweak.DISABLE_ENCHANT_TABLE.register("disableEnchantTable");

        // Stamina Mechanics

        public boolean staminaSprint = GameplayTweak.STAMINA_SPRINT.register("staminaSprint");
        public boolean staminaInfinitePeaceful = GameplayTweak.STAMINA_INFINITE_PEACEFUL.register("staminaInfinitePeaceful");
        public StaminaRegain staminaRegainWhenMoving = GameplayTweak.STAMINA_REGAIN_WHEN_MOVING.register("staminaRegainWhenMoving");
        public int staminaDuration = GameplayTweak.STAMINA_DURATION.register("staminaDuration");
        public int staminaRecharge = GameplayTweak.STAMINA_RECHARGE.register("staminaRecharge");
        public int staminaCooldown = GameplayTweak.STAMINA_COOLDOWN.register("staminaCooldown");

        // Stamina Effects

        public boolean staminaSaturationEffect = GameplayTweak.STAMINA_SATURATION_EFFECT.register("staminaSaturationEffect");
        public boolean staminaHungerEffect = GameplayTweak.STAMINA_HUNGER_EFFECT.register("staminaHungerEffect");

        // Player Mechanics

        public boolean oldNightmares = GameplayTweak.OLD_NIGHTMARES.register("oldNightmares");
        public boolean disableSprint = GameplayTweak.DISABLE_SPRINT.register("disableSprint");
        public boolean leftClickDoor = GameplayTweak.LEFT_CLICK_DOOR.register("leftClickDoor");
        public boolean leftClickLever = GameplayTweak.LEFT_CLICK_LEVER.register("leftClickLever");
        public boolean leftClickButton = GameplayTweak.LEFT_CLICK_BUTTON.register("leftClickButton");

        // Fishing Mechanics

        public boolean oldFishingLoot = GameplayTweak.OLD_FISHING_LOOT.register("oldFishingLoot");
        public boolean oldFishingLuring = GameplayTweak.OLD_FISHING_LURING.register("oldFishingLuring");
        public boolean oldFishingCasting = GameplayTweak.OLD_FISHING_CASTING.register("oldFishingCasting");

        // Farming Mechanics

        public boolean instantBonemeal = GameplayTweak.INSTANT_BONEMEAL.register("instantBonemeal");
        public boolean tilledGrassSeeds = GameplayTweak.TILLED_GRASS_SEEDS.register("tilledGrassSeeds");

        // Fire Mechanics

        public boolean oldFire = GameplayTweak.OLD_FIRE.register("oldFire");
        public boolean infiniteBurn = GameplayTweak.INFINITE_BURN.register("infiniteBurn");

        // Swimming Mechanics

        public boolean instantAir = GameplayTweak.INSTANT_AIR.register("instantAir");
        public boolean disableSwim = GameplayTweak.DISABLE_SWIM.register("disableSwim");

        // Minecart Mechanics

        public boolean cartBoosting = GameplayTweak.CART_BOOSTING.register("cartBoosting");
        public boolean cartRiderTurning = GameplayTweak.CART_RIDER_TURNING.register("cartRiderTurning");
        public boolean cartSafeRiderTurning = GameplayTweak.CART_SAFE_RIDER_TURNING.register("cartSafeRiderTurning");

        // Boat Mechanics

        public boolean disableBoatBusyHands = GameplayTweak.DISABLE_BOAT_BUSY_HANDS.register("disableBoatBusyHands");
        public boolean oldBoatWaterLift = GameplayTweak.OLD_BOAT_WATER_LIFT.register("oldBoatWaterLift");
        public boolean oldBoatDrops = GameplayTweak.OLD_BOAT_DROPS.register("oldBoatDrops");

        // Block Mechanics

        public ItemSet selfBlockDrops = GameplayTweak.SELF_BLOCK_DROPS.register("selfBlockDrops");
        public boolean punchTntIgnition = GameplayTweak.PUNCH_TNT_IGNITION.register("punchTntIgnition");
        public boolean disableBedBounce = GameplayTweak.DISABLE_BED_BOUNCE.register("disableBedBounce");
        public boolean alwaysOpenChest = GameplayTweak.ALWAYS_OPEN_CHEST.register("alwaysOpenChest");

        // Tool Mechanics

        public boolean disableShovelPathing = GameplayTweak.DISABLE_SHOVEL_PATHING.register("disableShovelPathing");
        public boolean disableAxeStripping = GameplayTweak.DISABLE_AXE_STRIPPING.register("disableAxeStripping");

        // Food

        public boolean disableHunger = GameplayTweak.DISABLE_HUNGER.register("disableHunger");
        public boolean instantEat = GameplayTweak.INSTANT_EAT.register("instantEat");
        public boolean oldFoodStacking = GameplayTweak.OLD_FOOD_STACKING.register("oldFoodStacking");
        public boolean preventHungerEffect = GameplayTweak.PREVENT_HUNGER_EFFECT.register("preventHungerEffect");

        public ItemMap<Integer> customFoodHealth = GameplayTweak.CUSTOM_FOOD_HEALTH.register("customFoodHealth");
        public ItemMap<Integer> customFoodStacking = GameplayTweak.CUSTOM_FOOD_STACKING.register("customFoodStacking");
        public ItemMap<Integer> customItemStacking = GameplayTweak.CUSTOM_ITEM_STACKING.register("customItemStacking");
    }

    public Gameplay gameplay = new Gameplay();

    public static class Animation
    {
        // Arm

        public boolean preventArmSway = AnimationTweak.PREVENT_ARM_SWAY.register("preventArmSway");
        public boolean armSwayMirror = AnimationTweak.ARM_SWAY_MIRROR.register("armSwayMirror");
        public int armSwayIntensity = AnimationTweak.ARM_SWAY_INTENSITY.register("armSwayIntensity");
        public boolean oldSwing = AnimationTweak.OLD_SWING.register("oldSwing");
        public boolean oldSwingInterrupt = AnimationTweak.OLD_SWING_INTERRUPT.register("oldSwingInterrupt");
        public boolean oldSwingDropping = AnimationTweak.OLD_SWING_DROPPING.register("oldSwingDropping");
        public boolean oldClassicAttackSwing = AnimationTweak.OLD_CLASSIC_ATTACK_SWING.register("oldClassicAttackSwing");
        public boolean oldClassicUseSwing = AnimationTweak.OLD_CLASSIC_USE_SWING.register("oldClassicUseSwing");

        // Item

        public boolean oldItemCooldown = AnimationTweak.OLD_ITEM_COOLDOWN.register("oldItemCooldown");
        public boolean oldItemReequip = AnimationTweak.OLD_ITEM_REEQUIP.register("oldItemReequip");
        public boolean oldToolExplosion = AnimationTweak.OLD_TOOL_EXPLOSION.register("oldToolExplosion");

        // Mob

        public boolean oldZombieArms = AnimationTweak.OLD_ZOMBIE_ARMS.register("oldZombieArms");
        public boolean oldSkeletonArms = AnimationTweak.OLD_SKELETON_ARMS.register("oldSkeletonArms");
        public boolean oldGhastCharging = AnimationTweak.OLD_GHAST_CHARGING.register("oldGhastCharging");
        public boolean oldMobDeathTopple = AnimationTweak.OLD_MOB_DEATH_TOPPLE.register("oldMobDeathTopple");
        public boolean oldMobHeadBodyTurn = AnimationTweak.OLD_MOB_HEAD_BODY_TURN.register("oldMobHeadBodyTurn");

        // Boat

        public boolean hideBoatRowing = AnimationTweak.HIDE_BOAT_ROWING.register("hideBoatRowing");

        // Player

        public boolean oldClassicWalkBobbing = AnimationTweak.OLD_CLASSIC_WALK_BOBBING.register("oldClassicWalkBobbing");
        public boolean oldClassicWalkArms = AnimationTweak.OLD_CLASSIC_WALK_ARMS.register("oldClassicWalkArms");
        public boolean oldBackwardWalking = AnimationTweak.OLD_BACKWARD_WALKING.register("oldBackwardWalking");
        public boolean oldVerticalBobbing = AnimationTweak.OLD_VERTICAL_BOBBING.register("oldVerticalBobbing");
        public boolean oldCreativeCrouch = AnimationTweak.OLD_CREATIVE_CROUCH.register("oldCreativeCrouch");
        public boolean oldRandomDamage = AnimationTweak.OLD_RANDOM_DAMAGE.register("oldRandomDamage");
        public boolean oldSneaking = AnimationTweak.OLD_SNEAKING.register("oldSneaking");
        public boolean preventDeathTopple = AnimationTweak.PREVENT_DEATH_TOPPLE.register("preventDeathTopple");
    }

    public Animation animation = new Animation();

    public static class Swing
    {
        // Global Speeds

        public boolean overrideSpeeds = SwingTweak.OVERRIDE_SPEEDS.register("overrideSpeeds");
        public int attackGlobalSpeed = SwingTweak.ATTACK_GLOBAL_SPEED.register("attackGlobalSpeed");
        public int useGlobalSpeed = SwingTweak.USE_GLOBAL_SPEED.register("useGlobalSpeed");

        // Item Speeds

        public int attackItemSpeed = SwingTweak.ATTACK_ITEM_SPEED.register("attackItemSpeed");
        public int useItemSpeed = SwingTweak.USE_ITEM_SPEED.register("useItemSpeed");
        public int attackToolSpeed = SwingTweak.ATTACK_TOOL_SPEED.register("attackToolSpeed");
        public int useToolSpeed = SwingTweak.USE_TOOL_SPEED.register("useToolSpeed");
        public int attackBlockSpeed = SwingTweak.ATTACK_BLOCK_SPEED.register("attackBlockSpeed");
        public int useBlockSpeed = SwingTweak.USE_BLOCK_SPEED.register("useBlockSpeed");
        public int attackSwordSpeed = SwingTweak.ATTACK_SWORD_SPEED.register("attackSwordSpeed");
        public int useSwordSpeed = SwingTweak.USE_SWORD_SPEED.register("useSwordSpeed");

        // Potion Speeds

        public int attackHasteSpeed = SwingTweak.ATTACK_HASTE_SPEED.register("attackHasteSpeed");
        public int useHasteSpeed = SwingTweak.USE_HASTE_SPEED.register("useHasteSpeed");
        public int attackFatigueSpeed = SwingTweak.ATTACK_FATIGUE_SPEED.register("attackFatigueSpeed");
        public int useFatigueSpeed = SwingTweak.USE_FATIGUE_SPEED.register("useFatigueSpeed");

        // Custom Speeds

        public ItemMap<Integer> attackSwingSpeeds = SwingTweak.ATTACK_SWING_SPEEDS.register("attackSwingSpeeds");
        public ItemMap<Integer> useSwingSpeeds = SwingTweak.USE_SWING_SPEEDS.register("useSwingSpeeds");
    }

    public Swing swing = new Swing();
}
