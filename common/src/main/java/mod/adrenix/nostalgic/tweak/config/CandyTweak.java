package mod.adrenix.nostalgic.tweak.config;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.TweakAlert;
import mod.adrenix.nostalgic.tweak.TweakCondition;
import mod.adrenix.nostalgic.tweak.TweakIssue;
import mod.adrenix.nostalgic.tweak.container.group.CandyGroup;
import mod.adrenix.nostalgic.tweak.enums.*;
import mod.adrenix.nostalgic.tweak.factory.*;
import mod.adrenix.nostalgic.tweak.gui.ControllerId;
import mod.adrenix.nostalgic.tweak.gui.KeybindingId;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.listing.*;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.CollectionUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.world.item.Items;

// @formatter:off
public interface CandyTweak
{
    // Block

    TweakEnum<MissingTexture> OLD_MISSING_TEXTURE = TweakEnum.client(MissingTexture.MODERN, CandyGroup.BLOCK).reloadResources().build();
    TweakFlag OLD_GRASS_SIDE_TEXTURE = TweakFlag.client(false, CandyGroup.BLOCK).reloadResources().build();
    TweakFlag OLD_FAST_GRASS_TEXTURE = TweakFlag.client(true, CandyGroup.BLOCK).reloadResources().build();
    TweakFlag REMOVE_MIPMAP_TEXTURE = TweakFlag.client(true, CandyGroup.BLOCK).reloadResources().build();
    TweakFlag SODIUM_WATER_AO = TweakFlag.client(false, CandyGroup.BLOCK).reloadChunks().ignoreIf(CollectionUtil.areAnyTrue(ModTracker.SODIUM::isNotInstalled, ModTracker.EMBEDDIUM::isInstalled)).build();
    TweakItemSet AMBIENT_OCCLUSION_BLOCKS = TweakItemSet.client(DefaultListing.ambientOcclusion(), CandyGroup.BLOCK).reloadChunks().build();
    TweakItemSet DISABLE_BLOCK_OFFSETS = TweakItemSet.client(DefaultListing.disabledOffsets(), CandyGroup.BLOCK).icon(TextureIcon.fromItem(Items.POPPY)).reloadChunks().build();
    TweakFlag DISABLE_ALL_OFFSET = TweakFlag.client(false, CandyGroup.BLOCK).reloadChunks().build();

    // Custom Hitbox

    TweakFlag APPLY_FULL_BLOCK_COLLISIONS = TweakFlag.server(true, CandyGroup.BLOCK_HITBOX).build();
    TweakItemSet FULL_BLOCK_COLLISIONS = TweakItemSet.client(new ItemSet(ItemRule.ONLY_BLOCKS), CandyGroup.BLOCK_HITBOX).icon(Icons.HITBOX_OUTLINE).build();
    TweakItemSet FULL_BLOCK_OUTLINES = TweakItemSet.client(DefaultListing.blockOutlines(), CandyGroup.BLOCK_HITBOX_OUTLINE).icon(Icons.HITBOX_OUTLINE).build();
    TweakFlag DISABLE_HITBOX_OVERRIDE = TweakFlag.client(false, CandyGroup.BLOCK_HITBOX_OUTLINE_AESTHETIC).whenDisabled(true).build();
    TweakColor BLOCK_OUTLINE_COLOR = TweakColor.client("#00000066", CandyGroup.BLOCK_HITBOX_OUTLINE_AESTHETIC).whenDisabled("#00000066").build();
    TweakNumber<Float> BLOCK_OUTLINE_THICKNESS = TweakNumber.client(2.5F, CandyGroup.BLOCK_HITBOX_OUTLINE_AESTHETIC).modIssues(TweakIssue.IRIS).slider(Lang.Slider.THICKNESS, 0.0F, 10.0F).interval(0.25F).roundTo(2).build();
    TweakFlag OLD_BLOCK_OVERLAY = TweakFlag.client(false, CandyGroup.BLOCK_HITBOX_OVERLAY).whenDisabled(false).build();
    TweakEnum<RenderOrder> BLOCK_OVERLAY_RENDER_ORDER = TweakEnum.client(RenderOrder.FIRST, CandyGroup.BLOCK_HITBOX_OVERLAY).build();
    TweakEnum<ColorType> BLOCK_OVERLAY_COLOR_TYPE = TweakEnum.client(ColorType.SOLID, CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).build();
    TweakColor BLOCK_OVERLAY_COLOR = TweakColor.client("#FFFFFF5A", CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).build();
    TweakColor BLOCK_OVERLAY_GRADIENT_TOP = TweakColor.client("#0000005A", CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).build();
    TweakColor BLOCK_OVERLAY_GRADIENT_BOTTOM = TweakColor.client("#FFFFFF5A", CandyGroup.BLOCK_HITBOX_OVERLAY_COLOR).build();
    TweakFlag PULSATE_BLOCK_OVERLAY = TweakFlag.client(true, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).whenDisabled(true).build();
    TweakEnum<AnimationType> PULSATE_OVERLAY_ANIMATION = TweakEnum.client(AnimationType.LINEAR, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).build();
    TweakNumber<Float> BLOCK_OVERLAY_SPEED = TweakNumber.client(0.2F, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).slider(Lang.Slider.SECONDS, 0.0F, 10.0F).interval(0.25F).build();
    TweakNumber<Float> MINIMUM_PULSATION_TRANSPARENCY = TweakNumber.client(0.1F, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).slider(Lang.Slider.MINIMUM, 0.0F, 1.0F).interval(0.25F).build();
    TweakNumber<Float> MAXIMUM_PULSATION_TRANSPARENCY = TweakNumber.client(0.4F, CandyGroup.BLOCK_HITBOX_OVERLAY_ANIMATION).slider(Lang.Slider.MAXIMUM, 0.0F, 1.0F).interval(0.25F).build();

    // Chests

    TweakFlag OLD_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).modIssues(TweakIssue.OPTIFINE).reloadResources().build();
    TweakFlag OLD_ENDER_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).reloadResources().build();
    TweakFlag OLD_TRAPPED_CHEST = TweakFlag.client(true, CandyGroup.BLOCK_CHEST).reloadResources().build();
    TweakFlag APPLY_CHEST_VOXEL = TweakFlag.server(true, CandyGroup.BLOCK_CHEST).build();
    TweakItemSet OLD_MOD_CHESTS = TweakItemSet.client(new ItemSet(ItemRule.ONLY_CHESTS), CandyGroup.BLOCK_CHEST).reloadResources().build();
    TweakItemSet TRANSLUCENT_CHESTS = TweakItemSet.client(new ItemSet(ItemRule.ONLY_CHESTS), CandyGroup.BLOCK_CHEST).reloadChunks().build();

    // Torch

    TweakFlag OLD_TORCH_BRIGHTNESS = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).reloadChunks().build();
    TweakFlag OLD_TORCH_BOTTOM = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).reloadChunks().build();
    TweakFlag OLD_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).reloadChunks().build();
    TweakFlag OLD_REDSTONE_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).reloadChunks().build();
    TweakFlag OLD_SOUL_TORCH_MODEL = TweakFlag.client(true, CandyGroup.BLOCK_TORCH).reloadChunks().build();

    // Bed

    TweakFlag HIDE_PLAYER_IN_BED = TweakFlag.client(true, CandyGroup.BLOCK_BED).build();

    // Heads-up Toasts

    TweakFlag HIDE_RECIPE_TOASTS = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_TOAST).newForUpdate().build();
    TweakFlag HIDE_TUTORIAL_TOASTS = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_TOAST).newForUpdate().build();
    TweakFlag HIDE_ADVANCEMENT_TOASTS = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_TOAST).newForUpdate().whenDisabled(false).build();
    TweakFlag HIDE_ADVANCEMENT_CHATS = TweakFlag.server(false, CandyGroup.INTERFACE_HUD_TOAST).newForUpdate().whenDisabled(false).ignoreNetworkCheck().build();

    // Heads-up Display

    TweakFlag HIDE_EXPERIENCE_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR).orIf(GameplayTweak.DISABLE_ORB_SPAWN::get).build();
    TweakFlag HIDE_HUNGER_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR).orIf(GameplayTweak.DISABLE_HUNGER::get).build();
    TweakFlag HIDE_STAMINA_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_STAMINA_BAR).newForUpdate().orIf(GameplayTweak.DISABLE_SPRINT::get).build();
    TweakFlag HIDE_STAMINA_BAR_MOVING = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_STAMINA_BAR).newForUpdate().build();
    TweakFlag HIDE_STAMINA_BAR_COOLDOWN = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_STAMINA_BAR).newForUpdate().build();
    TweakFlag HIDE_STAMINA_BAR_INACTIVE = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_STAMINA_BAR).newForUpdate().build();

    // Offhand Slot

    TweakFlag ADVENTURE_CRAFT_OFFHAND = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_OFFHAND).build();
    TweakNumber<Integer> LEFT_OFFHAND_OFFSET = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_OFFHAND).slider(Lang.Slider.OFFSET, -14, 7).build();
    TweakNumber<Integer> RIGHT_OFFHAND_OFFSET = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_OFFHAND).slider(Lang.Slider.OFFSET, -7, 14).build();

    // Game Version Overlay

    TweakFlag OLD_VERSION_OVERLAY = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_VERSION).build();
    TweakEnum<Corner> OLD_OVERLAY_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_VERSION).build();
    TweakNumber<Integer> OLD_OVERLAY_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_VERSION).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> OLD_OVERLAY_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_VERSION).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakFlag OLD_OVERLAY_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_VERSION).whenDisabled(true).build();
    TweakText OLD_OVERLAY_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_HUD_VERSION).build();

    // Alternative Experience Text

    TweakFlag SHOW_EXP_LEVEL_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).build();
    TweakFlag SHOW_EXP_LEVEL_ONLY_INVENTORY = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).build();
    TweakFlag SHOW_EXP_LEVEL_IN_CREATIVE = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).build();
    TweakEnum<Corner> ALT_EXP_LEVEL_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).build();
    TweakNumber<Integer> ALT_EXP_LEVEL_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_EXP_LEVEL_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakFlag ALT_EXP_LEVEL_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).whenDisabled(true).build();
    TweakText ALT_EXP_LEVEL_TEXT = TweakText.client("Level: %a%v", CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_LEVEL).build();

    // Alternative Progress Text

    TweakFlag SHOW_EXP_PROGRESS_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).build();
    TweakFlag SHOW_EXP_PROGRESS_ONLY_INVENTORY = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).build();
    TweakFlag SHOW_EXP_PROGRESS_IN_CREATIVE = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).build();
    TweakFlag USE_DYNAMIC_PROGRESS_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).whenDisabled(true).build();
    TweakEnum<Corner> ALT_EXP_PROGRESS_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).build();
    TweakNumber<Integer> ALT_EXP_PROGRESS_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_EXP_PROGRESS_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakFlag ALT_EXP_PROGRESS_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).whenDisabled(true).build();
    TweakText ALT_EXP_PROGRESS_TEXT = TweakText.client("Experience: %v%", CandyGroup.INTERFACE_HUD_EXP_BAR_ALT_PROGRESS).build();

    // Alternative Food Text

    TweakFlag SHOW_HUNGER_FOOD_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).build();
    TweakFlag SHOW_HUNGER_FOOD_ONLY_INVENTORY = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).build();
    TweakFlag USE_DYNAMIC_FOOD_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).whenDisabled(true).build();
    TweakEnum<Corner> ALT_HUNGER_FOOD_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).build();
    TweakNumber<Integer> ALT_HUNGER_FOOD_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_HUNGER_FOOD_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakFlag ALT_HUNGER_FOOD_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).whenDisabled(true).build();
    TweakText ALT_HUNGER_FOOD_TEXT = TweakText.client("Food: %v", CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_FOOD).build();

    // Alternative Saturation Text

    TweakFlag SHOW_HUNGER_SATURATION_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).build();
    TweakFlag SHOW_HUNGER_SATURATION_ONLY_INVENTORY = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).build();
    TweakFlag USE_DYNAMIC_SATURATION_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).whenDisabled(true).build();
    TweakEnum<Corner> ALT_HUNGER_SATURATION_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).build();
    TweakNumber<Integer> ALT_HUNGER_SATURATION_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_HUNGER_SATURATION_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakFlag ALT_HUNGER_SATURATION_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).whenDisabled(true).build();
    TweakText ALT_HUNGER_SATURATION_TEXT = TweakText.client("Saturation: %v%", CandyGroup.INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION).build();

    // Alternative Stamina Text

    TweakFlag SHOW_STAMINA_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().build();
    TweakFlag USE_DYNAMIC_STAMINA_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().whenDisabled(true).build();
    TweakFlag ALT_STAMINA_SHOW_ON_ACTIVE = TweakFlag.client(false, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().build();
    TweakEnum<Corner> ALT_STAMINA_CORNER = TweakEnum.client(Corner.TOP_LEFT, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().build();
    TweakNumber<Integer> ALT_STAMINA_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakNumber<Integer> ALT_STAMINA_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().slider(Lang.Slider.OFFSET, -100, 100).build();
    TweakFlag ALT_STAMINA_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().whenDisabled(true).build();
    TweakText ALT_STAMINA_TEXT = TweakText.client("Stamina: %v%", CandyGroup.INTERFACE_HUD_STAMINA_BAR_ALT).newForUpdate().build();

    // Window Title

    TweakFlag ENABLE_WINDOW_TITLE = TweakFlag.client(false, CandyGroup.INTERFACE_WINDOW).whenDisabled(false).build();
    TweakFlag MATCH_VERSION_OVERLAY = TweakFlag.client(false, CandyGroup.INTERFACE_WINDOW).alert(TweakAlert.WINDOW_TITLE_DISABLED).build();
    TweakText WINDOW_TITLE_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_WINDOW).alert(TweakAlert.WINDOW_TITLE_DISABLED).build();

    // Debug Screen

    TweakEnum<Generic> OLD_DEBUG = TweakEnum.client(Generic.BETA, CandyGroup.INTERFACE_DEBUG).whenDisabled(Generic.MODERN).build();
    TweakFlag DEBUG_ENTITY_ID = TweakFlag.server(true, CandyGroup.INTERFACE_DEBUG).build();

    // Debug Chart

    TweakEnum<DebugChart> FPS_CHART = TweakEnum.client(DebugChart.CLASSIC, CandyGroup.INTERFACE_DEBUG_CHART).whenDisabled(DebugChart.DISABLED).build();
    TweakFlag SHOW_DEBUG_PIE_CHART = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).build();
    TweakFlag OLD_PIE_CHART_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_CHART).build();

    // Debug Color

    TweakFlag SHOW_DEBUG_LEFT_TEXT_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).build();
    TweakFlag SHOW_DEBUG_RIGHT_TEXT_SHADOW = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).build();
    TweakFlag SHOW_DEBUG_LEFT_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_COLOR).whenDisabled(true).build();
    TweakFlag SHOW_DEBUG_RIGHT_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_COLOR).whenDisabled(true).build();
    TweakColor DEBUG_LEFT_BACKGROUND_COLOR = TweakColor.client("#50505090", CandyGroup.INTERFACE_DEBUG_COLOR).whenDisabled("#50505090").build();
    TweakColor DEBUG_RIGHT_BACKGROUND_COLOR = TweakColor.client("#50505090", CandyGroup.INTERFACE_DEBUG_COLOR).whenDisabled("#50505090").build();
    TweakFlag SHOW_DEBUG_LEFT_TEXT_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).build();
    TweakFlag SHOW_DEBUG_RIGHT_TEXT_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_DEBUG_COLOR).build();
    TweakColor DEBUG_LEFT_TEXT_COLOR = TweakColor.client("#FFFFFFFF", CandyGroup.INTERFACE_DEBUG_COLOR).whenDisabled("#FFFFFFFF").build();
    TweakColor DEBUG_RIGHT_TEXT_COLOR = TweakColor.client("#E0E0E0FF", CandyGroup.INTERFACE_DEBUG_COLOR).whenDisabled("#FFFFFFFF").build();

    // Debug Extra

    TweakFlag SHOW_DEBUG_GPU_USAGE = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).build();
    TweakFlag SHOW_DEBUG_LIGHT_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).build();
    TweakFlag SHOW_DEBUG_FACING_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).build();
    TweakFlag SHOW_DEBUG_TARGET_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).build();
    TweakFlag SHOW_DEBUG_BIOME_DATA = TweakFlag.client(false, CandyGroup.INTERFACE_DEBUG_EXTRA).build();

    // Inventory Screen

    TweakFlag OLD_INVENTORY = TweakFlag.client(false, CandyGroup.INTERFACE_INVENTORY).build();
    TweakEnum<RecipeBook> INVENTORY_BOOK = TweakEnum.client(RecipeBook.SMALL, CandyGroup.INTERFACE_INVENTORY).whenDisabled(RecipeBook.MODERN).build();
    TweakEnum<InventoryShield> INVENTORY_SHIELD = TweakEnum.client(InventoryShield.MODERN, CandyGroup.INTERFACE_INVENTORY).whenDisabled(InventoryShield.MODERN).alert(TweakAlert.SHIELD_CONFLICT).build();
    TweakFlag DISABLE_EMPTY_ARMOR_TEXTURE = TweakFlag.client(false, CandyGroup.INTERFACE_INVENTORY).build();
    TweakFlag DISABLE_EMPTY_SHIELD_TEXTURE = TweakFlag.client(false, CandyGroup.INTERFACE_INVENTORY).build();
    TweakFlag INVERTED_PLAYER_LIGHTING = TweakFlag.client(true, CandyGroup.INTERFACE_INVENTORY).build();
    TweakEnum<Hotbar> OLD_CREATIVE_HOTBAR = TweakEnum.server(Hotbar.CLASSIC, CandyGroup.INTERFACE_INVENTORY).ignoreNetworkCheck().whenDisabled(Hotbar.MODERN).build();

    // Generic Screen

    TweakFlag FIX_SAVING_INDICATOR_OFFSET = TweakFlag.client(true, CandyGroup.INTERFACE_GUI).newForUpdate().build();
    TweakFlag OLD_DIRT_SCREEN_BACKGROUND = TweakFlag.client(true, CandyGroup.INTERFACE_GUI).build();
    TweakFlag OLD_BUTTON_TEXT_COLOR = TweakFlag.client(true, CandyGroup.INTERFACE_GUI).build();
    TweakFlag REMOVE_SCREEN_BLUR = TweakFlag.client(true, CandyGroup.INTERFACE_GUI).build();
    TweakFlag APPLY_GUI_COLOR_BACKGROUND = TweakFlag.client(true, CandyGroup.INTERFACE_GUI_COLOR).build();
    TweakEnum<GuiBackground> OLD_GUI_BACKGROUND = TweakEnum.client(GuiBackground.SOLID_BLACK, CandyGroup.INTERFACE_GUI_COLOR).build();
    TweakFlag CUSTOM_GUI_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_GUI_COLOR).build();
    TweakColor CUSTOM_GUI_TOP_GRADIENT = TweakColor.client("#00000000", CandyGroup.INTERFACE_GUI_COLOR).alert(TweakAlert.CUSTOM_GUI_DISABLED).build();
    TweakColor CUSTOM_GUI_BOTTOM_GRADIENT = TweakColor.client("#00000000", CandyGroup.INTERFACE_GUI_COLOR).alert(TweakAlert.CUSTOM_GUI_DISABLED).build();

    // Loading Overlay

    TweakEnum<Overlay> OLD_LOADING_OVERLAY = TweakEnum.client(Overlay.ALPHA, CandyGroup.INTERFACE_LOADING).whenDisabled(Overlay.MODERN).build();
    TweakFlag REMOVE_LOADING_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING).modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag CUSTOM_LOADING_OVERLAY_BACKGROUND = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING_COLOR).build();
    TweakColor LOADING_OVERLAY_BACKGROUND_COLOR = TweakColor.client("#000000", CandyGroup.INTERFACE_LOADING_COLOR).build();
    TweakFlag CUSTOM_LOADING_PROGRESS_BAR = TweakFlag.client(false, CandyGroup.INTERFACE_LOADING_COLOR).build();
    TweakColor PROGRESS_BAR_OUTLINE_COLOR = TweakColor.client("#FFFFFF", CandyGroup.INTERFACE_LOADING_COLOR).build();
    TweakColor PROGRESS_BAR_INSIDE_COLOR = TweakColor.client("#FFFFFF", CandyGroup.INTERFACE_LOADING_COLOR).build();

    // Progress Screen

    TweakFlag OLD_PROGRESS_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_PROGRESS).build();

    // Pause Screen

    TweakEnum<PauseLayout> OLD_PAUSE_MENU = TweakEnum.client(PauseLayout.MODERN, CandyGroup.INTERFACE_PAUSE).build();
    TweakFlag INCLUDE_MODS_ON_PAUSE = TweakFlag.client(true, CandyGroup.INTERFACE_PAUSE).build();
    TweakFlag REMOVE_EXTRA_PAUSE_BUTTONS = TweakFlag.client(false, CandyGroup.INTERFACE_PAUSE).build();

    // Anvil Screen

    TweakFlag OLD_ANVIL_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_ANVIL).build();

    // Crafting Screen

    TweakFlag OLD_CRAFTING_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_CRAFTING).build();
    TweakEnum<RecipeBook> CRAFTING_BOOK = TweakEnum.client(RecipeBook.SMALL, CandyGroup.INTERFACE_CRAFTING).whenDisabled(RecipeBook.MODERN).build();

    // Furnace Screen

    TweakFlag OLD_FURNACE_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_FURNACE).build();
    TweakEnum<RecipeBook> FURNACE_BOOK = TweakEnum.client(RecipeBook.SMALL, CandyGroup.INTERFACE_FURNACE).whenDisabled(RecipeBook.MODERN).build();

    // Chat Screen

    TweakFlag OLD_CHAT_INPUT = TweakFlag.client(true, CandyGroup.INTERFACE_CHAT).build();
    TweakFlag OLD_CHAT_BOX = TweakFlag.client(true, CandyGroup.INTERFACE_CHAT).build();
    TweakFlag DISABLE_SIGNATURE_BOXES = TweakFlag.client(false, CandyGroup.INTERFACE_CHAT).build();
    TweakNumber<Integer> CHAT_OFFSET = TweakNumber.client(0, CandyGroup.INTERFACE_CHAT).slider(Lang.Slider.OFFSET, 0, 32).build();

    // Death Screen

    TweakFlag OLD_DEATH_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_DEATH).build();
    TweakFlag OLD_DEATH_SCORE = TweakFlag.client(true, CandyGroup.INTERFACE_DEATH).build();
    TweakFlag HIDE_CAUSE_OF_DEATH = TweakFlag.client(true, CandyGroup.INTERFACE_DEATH).build();

    // World Select Screen

    TweakEnum<Generic> OLD_WORLD_SELECT_SCREEN = TweakEnum.client(Generic.BETA, CandyGroup.INTERFACE_WORLD_SELECT).whenDisabled(Generic.MODERN).build();
    TweakFlag LEVEL_SELECT_WHEN_EMPTY = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_SELECT).newForUpdate().build();
    TweakFlag ADD_WORLD_THUMBNAIL = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_SELECT).whenDisabled(true).build();
    TweakFlag ADD_WORLD_METADATA = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_SELECT).whenDisabled(true).build();
    TweakFlag IGNORE_WORLD_SIZE = TweakFlag.client(false, CandyGroup.INTERFACE_WORLD_SELECT).build();

    // World Create Screen

    TweakFlag OLD_STYLE_CREATE_WORLD_TABS = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_CREATE).build();
    TweakFlag REMOVE_CREATE_WORLD_FOOTER = TweakFlag.client(true, CandyGroup.INTERFACE_WORLD_CREATE).build();

    // Title Screen

    TweakFlag OVERRIDE_TITLE_SCREEN = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).build();
    TweakFlag OLD_TITLE_BACKGROUND = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).build();
    TweakFlag UNCAP_TITLE_FPS = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE).build();

    // Title Screen Logo

    TweakFlag OLD_ALPHA_LOGO = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_LOGO).build();
    TweakFlag CLICK_ON_LOGO_TOGGLE = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_LOGO).build();
    TweakFlag USE_CUSTOM_FALLING_LOGO = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_LOGO).build();
    TweakCustom FALLING_LOGO_EDITOR = TweakCustom.client(ControllerId.FALLING_LOGO, CandyGroup.INTERFACE_TITLE_LOGO).alert(TweakAlert.CUSTOM_FALLING_LOGO_DISABLED).build();
    TweakNumber<Integer> SPLASH_OFFSET_X = TweakNumber.client(0, CandyGroup.INTERFACE_TITLE_LOGO).slider(Lang.Slider.OFFSET, 0, 100).build();
    TweakNumber<Integer> SPLASH_OFFSET_Y = TweakNumber.client(0, CandyGroup.INTERFACE_TITLE_LOGO).slider(Lang.Slider.OFFSET, 0, 50).build();

    // Title Screen Buttons

    TweakEnum<TitleLayout> TITLE_BUTTON_LAYOUT = TweakEnum.client(TitleLayout.MODERN, CandyGroup.INTERFACE_TITLE_BUTTON).conflictMods(ModTracker.CONTROLIFY).build();
    TweakFlag INCLUDE_MODS_ON_TITLE = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_BUTTON).build();
    TweakFlag REMOVE_TITLE_REALMS_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).build();
    TweakFlag REMOVE_TITLE_ACCESSIBILITY_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).build();
    TweakFlag REMOVE_TITLE_LANGUAGE_BUTTON = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).build();
    TweakFlag REMOVE_EXTRA_TITLE_BUTTONS = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_BUTTON).build();
    TweakFlag ADD_QUIT_BUTTON = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_BUTTON).whenDisabled(true).build();

    // Title Screen Text

    TweakText TITLE_VERSION_TEXT = TweakText.client("Minecraft %v", CandyGroup.INTERFACE_TITLE_TEXT).build();
    TweakFlag TITLE_BOTTOM_LEFT_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_TEXT).build();
    TweakFlag TITLE_TOP_RIGHT_DEBUG_TEXT = TweakFlag.client(false, CandyGroup.INTERFACE_TITLE_TEXT).build();
    TweakFlag REMOVE_TITLE_MOD_LOADER_TEXT = TweakFlag.client(true, CandyGroup.INTERFACE_TITLE_TEXT).build();

    // Tooltips

    TweakFlag OLD_TOOLTIP_BOXES = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP).build();
    TweakFlag OLD_NO_ITEM_TOOLTIPS = TweakFlag.client(false, CandyGroup.INTERFACE_TOOLTIP).build();

    // Tooltip Parts

    TweakFlag SHOW_ENCHANTMENT_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).whenDisabled(true).build();
    TweakFlag SHOW_MODIFIER_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).whenDisabled(true).build();
    TweakFlag SHOW_DYE_TIP = TweakFlag.client(true, CandyGroup.INTERFACE_TOOLTIP_PARTS).whenDisabled(true).build();

    // Tooltip Color

    TweakEnum<ColorType> TOOLTIP_COLOR_TYPE = TweakEnum.client(ColorType.SOLID, CandyGroup.INTERFACE_TOOLTIP_COLOR).build();
    TweakColor TOOLTIP_BACKGROUND_COLOR = TweakColor.client("#000000C0", CandyGroup.INTERFACE_TOOLTIP_COLOR).build();
    TweakColor TOOLTIP_GRADIENT_TOP = TweakColor.client("#FFFFFFC0", CandyGroup.INTERFACE_TOOLTIP_COLOR).build();
    TweakColor TOOLTIP_GRADIENT_BOTTOM = TweakColor.client("#000000C0", CandyGroup.INTERFACE_TOOLTIP_COLOR).build();

    // Items

    TweakFlag FIX_ITEM_MODEL_GAP = TweakFlag.client(true, CandyGroup.ITEM).reloadResources().ignoreIf(NostalgicTweaks::isForge).build();
    TweakFlag OLD_DAMAGE_ARMOR_TINT = TweakFlag.client(true, CandyGroup.ITEM).modIssues(TweakIssue.OPTIFINE).build();
    TweakFlag OLD_ITEM_HOLDING = TweakFlag.client(true, CandyGroup.ITEM).build();
    TweakItemSet IGNORED_HOLDING_ITEMS = TweakItemSet.client(DefaultListing.ignoredHoldingItems(), CandyGroup.ITEM).build();

    // Item Merging

    TweakFlag OLD_ITEM_MERGING = TweakFlag.server(true, CandyGroup.ITEM_MERGING).build();
    TweakNumber<Integer> ITEM_MERGE_LIMIT = TweakNumber.server(16, CandyGroup.ITEM_MERGING).slider(Lang.Slider.LIMIT, 1, 64).build();

    // 2D Items

    TweakFlag OLD_2D_ITEMS = TweakFlag.client(true, CandyGroup.ITEM_FLAT).build();
    TweakFlag OLD_2D_RENDERING = TweakFlag.client(true, CandyGroup.ITEM_FLAT).modIssues(TweakIssue.OPTIFINE).build();
    TweakItemSet OLD_2D_EXCEPTIONS = TweakItemSet.client(DefaultListing.old2dExceptions(), CandyGroup.ITEM_FLAT).newForUpdate().build();
    TweakFlag DISABLE_ENCHANTED_GROUND_ITEMS = TweakFlag.client(false, CandyGroup.ITEM_FLAT).andIf(TweakCondition::areItemsFlat).build();
    TweakFlag DISABLE_ENCHANTED_STATIC_ITEMS = TweakFlag.client(false, CandyGroup.ITEM_FLAT).andIf(TweakCondition::areItemsFlat).build();

    // Item Display

    TweakItemSet IGNORED_DURABILITY_COLORS = TweakItemSet.client(new ItemSet(ItemRule.ONLY_DAMAGEABLE), CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_DURABILITY_COLORS = TweakFlag.client(true, CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_NO_SELECTED_ITEM_NAME = TweakFlag.client(true, CandyGroup.ITEM_DISPLAY).build();
    TweakFlag OLD_PLAIN_SELECTED_ITEM_NAME = TweakFlag.client(false, CandyGroup.ITEM_DISPLAY).build();

    // Name Tag

    TweakFlag OLD_NAME_TAGS = TweakFlag.client(false, CandyGroup.NAME_TAG).build();
    TweakFlag SUPPORTER_TAGS = TweakFlag.client(true, CandyGroup.NAME_TAG).whenDisabled(true).build();

    // Block Lighting

    TweakFlag DISABLE_LIGHT_FLICKER = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).build();
    TweakFlag INVERTED_BLOCK_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).build();
    TweakFlag OLD_LEAVES_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().build();
    TweakFlag OLD_WATER_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().conflictMods(ModTracker.STARLIGHT).build();
    TweakFlag CHEST_LIGHT_BLOCK = TweakFlag.client(true, CandyGroup.LIGHTING_BLOCK).reloadChunks().conflictMods(ModTracker.STARLIGHT).build();

    // Light Engine

    TweakFlag ROUND_ROBIN_RELIGHT = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_ENGINE).reloadChunks().modIssues(TweakIssue.SODIUM, TweakIssue.OPTIFINE, TweakIssue.POLYTONE).conflictMods(ModTracker.DISTANT_HORIZONS, ModTracker.STARLIGHT).build();
    TweakFlag OLD_NETHER_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_ENGINE).reloadChunks().build();
    TweakFlag OLD_SMOOTH_LIGHTING = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_ENGINE).reloadChunks().build();
    TweakFlag DISABLE_SMOOTH_LIGHTING = TweakFlag.client(false, CandyGroup.LIGHTING_WORLD_ENGINE).newForUpdate().reloadChunks().build();
    TweakFlag OLD_CLASSIC_ENGINE = TweakFlag.server(false, CandyGroup.LIGHTING_WORLD_ENGINE).reloadChunks().warningTag().build();

    // Lightmap Texture

    TweakFlag SMOOTH_LIGHT_TRANSITION = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_TEXTURE).whenDisabled(true).build();
    TweakFlag DYNAMIC_LIGHT_BRIGHTNESS = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_TEXTURE).newForUpdate().alert(TweakAlert.BRIGHTNESS_CONFLICT).build();
    TweakFlag DISABLE_LIGHT_BRIGHTNESS = TweakFlag.client(false, CandyGroup.LIGHTING_WORLD_TEXTURE).alert(TweakAlert.BRIGHTNESS_CONFLICT).build();
    TweakFlag OLD_LIGHT_COLOR = TweakFlag.client(true, CandyGroup.LIGHTING_WORLD_TEXTURE).modIssues(TweakIssue.POLYTONE).build();

    // Shader Support

    TweakNumber<Integer> MAX_BLOCK_LIGHT = TweakNumber.client(15, CandyGroup.LIGHTING_WORLD_SHADER).slider(Lang.Slider.BLOCK_LIGHT, 0, 15).reloadChunks().build();

    // Disabled Particles

    TweakStringSet DISABLED_PARTICLES = TweakStringSet.client(new StringSet(ListingSuggestion.PARTICLE), CandyGroup.PARTICLE_DISABLED).build();

    // Experience Particles

    TweakFlag OLD_OPAQUE_EXPERIENCE = TweakFlag.client(true, CandyGroup.PARTICLE_EXPERIENCE).build();

    // Biome Particles

    TweakFlag DISABLE_NETHER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BIOME).build();
    TweakFlag DISABLE_UNDERWATER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BIOME).build();

    // Block Particles

    TweakFlag DISABLE_LAVA_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_BLOCK).build();
    TweakFlag DISABLE_LAVA_DRIP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).build();
    TweakFlag DISABLE_WATER_DRIP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).build();
    TweakFlag DISABLE_LEVER_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).build();
    TweakFlag DISABLE_GROWTH_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).build();
    TweakFlag DISABLE_MODEL_DESTRUCTION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BLOCK).build();
    TweakFlag DISABLE_ENDER_CHEST_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_BLOCK).build();

    // Player Particles

    TweakFlag DISABLE_FALLING_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_PLAYER).build();
    TweakFlag DISABLE_SPRINTING_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_PLAYER).build();
    TweakFlag HIDE_FIRST_PERSON_MAGIC_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_PLAYER).build();

    // Boat Particles

    TweakFlag OLD_BOAT_MOVEMENT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_BOAT).build();

    // Attack Particles

    TweakFlag OLD_SWEEP_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_DAMAGE_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_CRIT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();
    TweakFlag OLD_NO_MAGIC_HIT_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_ATTACK).build();

    // Explosion Particles

    TweakFlag OLD_EXPLOSION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_EXPLOSION).build();
    TweakFlag OLD_MIXED_EXPLOSION_PARTICLES = TweakFlag.client(false, CandyGroup.PARTICLE_EXPLOSION).build();
    TweakFlag UNOPTIMIZED_EXPLOSION_PARTICLES = TweakFlag.client(true, CandyGroup.PARTICLE_EXPLOSION).build();

    // World

    TweakFlag OLD_SQUARE_BORDER = TweakFlag.server(true, CandyGroup.WORLD).reloadChunks().ignoreNetworkCheck().build();

    // World Fog

    TweakEnum<WorldFog> OLD_WORLD_FOG = TweakEnum.client(WorldFog.ALPHA_R164, CandyGroup.WORLD_FOG).whenDisabled(WorldFog.MODERN).build();
    TweakBinding FOG_BINDING = TweakBinding.client(-1, CandyGroup.WORLD_FOG, KeybindingId.FOG).build();
    TweakFlag OLD_SUNRISE_SUNSET_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();
    TweakFlag OLD_DARK_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();
    TweakFlag OLD_DYNAMIC_FOG_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG).alert(TweakAlert.DYNAMIC_FOG).modIssues(TweakIssue.POLYTONE).build();
    TweakEnum<FogColor> UNIVERSAL_FOG_COLOR = TweakEnum.client(FogColor.DISABLED, CandyGroup.WORLD_FOG).alert(TweakAlert.UNIVERSAL_FOG).build();
    TweakFlag OLD_NETHER_FOG = TweakFlag.client(true, CandyGroup.WORLD_FOG).build();

    // Custom World Fog

    TweakFlag USE_CUSTOM_OVERWORLD_FOG_DENSITY = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).build();
    TweakFlag USE_CUSTOM_OVERWORLD_FOG_COLOR = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).build();
    TweakFlag USE_CUSTOM_NETHER_FOG_DENSITY = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).build();
    TweakFlag USE_CUSTOM_NETHER_FOG_COLOR = TweakFlag.client(false, CandyGroup.WORLD_FOG_CUSTOM).build();

    // Custom Fog Color

    TweakColor CUSTOM_OVERWORLD_FOG_COLOR = TweakColor.client("#FFFFFF", CandyGroup.WORLD_FOG_CUSTOM_COLOR).alert(TweakAlert.OVERWORLD_FOG_COLOR).build();
    TweakColor CUSTOM_NETHER_FOG_COLOR = TweakColor.client("#540E0E", CandyGroup.WORLD_FOG_CUSTOM_COLOR).alert(TweakAlert.NETHER_FOG_COLOR).build();

    // Custom Fog Density

    TweakNumber<Integer> CUSTOM_OVERWORLD_FOG_START = TweakNumber.client(100, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).alert(TweakAlert.OVERWORLD_FOG_DENSITY).slider(Lang.Slider.PERCENTAGE, 0, 100, "%").build();
    TweakNumber<Integer> CUSTOM_OVERWORLD_FOG_END = TweakNumber.client(60, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).alert(TweakAlert.OVERWORLD_FOG_DENSITY).slider(Lang.Slider.DENSITY, 0, 100, "%").build();
    TweakNumber<Integer> CUSTOM_NETHER_FOG_START = TweakNumber.client(100, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).alert(TweakAlert.NETHER_FOG_DENSITY).slider(Lang.Slider.PERCENTAGE, 0, 100, "%").build();
    TweakNumber<Integer> CUSTOM_NETHER_FOG_END = TweakNumber.client(60, CandyGroup.WORLD_FOG_CUSTOM_DENSITY).alert(TweakAlert.NETHER_FOG_DENSITY).slider(Lang.Slider.DENSITY, 0, 100, "%").build();

    // Water Fog

    TweakFlag OLD_WATER_FOG_DENSITY = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).build();
    TweakFlag OLD_WATER_FOG_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).build();
    TweakFlag SMOOTH_WATER_DENSITY = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).whenDisabled(true).build();
    TweakFlag SMOOTH_WATER_COLOR = TweakFlag.client(true, CandyGroup.WORLD_FOG_WATER).whenDisabled(true).build();

    // World Sky

    TweakNumber<Integer> OLD_CLOUD_HEIGHT = TweakNumber.client(128, CandyGroup.WORLD_SKY).range(108, 192).type(SliderType.CLOUD).whenDisabled(192).build();
    TweakFlag RENDER_SUNRISE_SUNSET_COLOR = TweakFlag.client(true, CandyGroup.WORLD_SKY).whenDisabled(true).build();
    TweakFlag OLD_SUNRISE_AT_NORTH = TweakFlag.client(true, CandyGroup.WORLD_SKY).build();
    TweakEnum<Generic> OLD_STARS = TweakEnum.client(Generic.ALPHA, CandyGroup.WORLD_SKY).whenDisabled(Generic.MODERN).build();
    TweakEnum<SkyColor> UNIVERSAL_SKY_COLOR = TweakEnum.client(SkyColor.DISABLED, CandyGroup.WORLD_SKY).alert(TweakAlert.UNIVERSAL_SKY).build();
    TweakFlag OLD_DYNAMIC_SKY_COLOR = TweakFlag.client(true, CandyGroup.WORLD_SKY).alert(TweakAlert.DYNAMIC_SKY).modIssues(TweakIssue.POLYTONE).build();
    TweakFlag OLD_NETHER_SKY = TweakFlag.client(true, CandyGroup.WORLD_SKY).build();

    // Custom Sky

    TweakFlag CUSTOM_OVERWORLD_SKY = TweakFlag.client(false, CandyGroup.WORLD_SKY_CUSTOM).build();
    TweakColor CUSTOM_OVERWORLD_SKY_COLOR = TweakColor.client("#FFFFFF", CandyGroup.WORLD_SKY_CUSTOM).build();

    // Void Sky

    TweakEnum<Generic> OLD_BLUE_VOID = TweakEnum.client(Generic.ALPHA, CandyGroup.WORLD_VOID_SKY).whenDisabled(Generic.MODERN).build();
    TweakFlag OLD_BLUE_VOID_OVERRIDE = TweakFlag.client(true, CandyGroup.WORLD_VOID_SKY).build();
    TweakFlag OLD_DARK_VOID_HEIGHT = TweakFlag.client(false, CandyGroup.WORLD_VOID_SKY).alert(TweakAlert.VOID_CONFLICT).build();
    TweakFlag CUSTOM_VOID_SKY = TweakFlag.client(false, CandyGroup.WORLD_VOID_SKY).build();
    TweakColor CUSTOM_VOID_SKY_COLOR = TweakColor.client("#0000FF", CandyGroup.WORLD_VOID_SKY).build();

    // Void Fog

    TweakFlag RENDER_VOID_FOG = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).build();
    TweakFlag CREATIVE_VOID_FOG = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).whenDisabled(true).build();
    TweakFlag CREATIVE_VOID_PARTICLES = TweakFlag.client(true, CandyGroup.WORLD_VOID_FOG).whenDisabled(true).build();
    TweakFlag LIGHT_REMOVES_VOID_FOG = TweakFlag.client(false, CandyGroup.WORLD_VOID_FOG).build();
    TweakNumber<Integer> VOID_FOG_ENCROACH = TweakNumber.client(70, CandyGroup.WORLD_VOID_FOG).slider(Lang.Slider.ENCROACH, 0, 100, "%").build();
    TweakNumber<Integer> VOID_FOG_START = TweakNumber.client(50, CandyGroup.WORLD_VOID_FOG).slider(Lang.Slider.Y_LEVEL, -64, 320).build();
    TweakNumber<Integer> VOID_PARTICLE_START = TweakNumber.client(-47, CandyGroup.WORLD_VOID_FOG).slider(Lang.Slider.Y_LEVEL, -64, 320).build();
    TweakNumber<Integer> VOID_PARTICLE_RADIUS = TweakNumber.client(16, CandyGroup.WORLD_VOID_FOG).slider(Lang.Slider.RADIUS, 0, 32).build();
    TweakNumber<Integer> VOID_PARTICLE_DENSITY = TweakNumber.client(20, CandyGroup.WORLD_VOID_FOG).slider(Lang.Slider.DENSITY, 0, 100, "%").build();
}
