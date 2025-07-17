package mod.adrenix.nostalgic.tweak.container.group;

import mod.adrenix.nostalgic.tweak.container.Category;
import mod.adrenix.nostalgic.tweak.container.Container;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

// @formatter:off
public interface CandyGroup
{
    // Block

    Container BLOCK = Container.group(Category.EYE_CANDY, "block").color(0x98C767).icon(Blocks.GRASS_BLOCK).build();
    Container BLOCK_HITBOX = Container.group(BLOCK, "block_hitbox").color(0xB8845B).icon(Icons.CUSTOM_HITBOX).build();
    Container BLOCK_HITBOX_OUTLINE = Container.group(BLOCK_HITBOX, "block_hitbox_outline").color(0xAAAAAA).icon(Icons.HITBOX_OUTLINE).build();
    Container BLOCK_HITBOX_OUTLINE_AESTHETIC = Container.group(BLOCK_HITBOX_OUTLINE, "block_hitbox_outline_aesthetic").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container BLOCK_HITBOX_OVERLAY = Container.group(BLOCK_HITBOX, "block_hitbox_overlay").color(0xFFFFFF).icon(Icons.HITBOX_OVERLAY).build();
    Container BLOCK_HITBOX_OVERLAY_COLOR = Container.group(BLOCK_HITBOX_OVERLAY, "block_hitbox_overlay_color").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container BLOCK_HITBOX_OVERLAY_ANIMATION = Container.group(BLOCK_HITBOX_OVERLAY, "block_hitbox_overlay_animation").color(0x00AEDD).icon(Icons.PLAYER_CONTROLS).build();
    Container BLOCK_CHEST = Container.group(BLOCK, "block_chest").color(0xFFB444).icon(Blocks.CHEST).build();
    Container BLOCK_TORCH = Container.group(BLOCK, "block_torch").color(0xFFD800).icon(Blocks.TORCH).build();
    Container BLOCK_BED = Container.group(BLOCK, "block_bed").color(0xFB4A4C).icon(Items.RED_BED).build();

    // Interface

    Container INTERFACE = Container.group(Category.EYE_CANDY, "interface").color(0xFFFFFF).icon(Icons.BUTTON).build();
    Container INTERFACE_HUD = Container.group(INTERFACE, "interface_hud").color(0x0094FF).icon(Icons.HUD).build();
    Container INTERFACE_HUD_OFFHAND = Container.group(INTERFACE_HUD, "interface_hud_offhand").color(0xFFBC9B).icon(Icons.ARM_SWAY).build();
    Container INTERFACE_HUD_VERSION = Container.group(INTERFACE_HUD, "interface_hud_version").color(0xB7603E).icon(Items.WRITABLE_BOOK).build();
    Container INTERFACE_HUD_TOAST = Container.group(INTERFACE_HUD, "interface_hud_toast").icon(Items.BREAD).color(0xBB8926).build();
    Container INTERFACE_HUD_EXP_BAR = Container.group(INTERFACE_HUD, "interface_hud_exp_bar").color(0x86C457).icon(Icons.XP_BAR).build();
    Container INTERFACE_HUD_EXP_BAR_ALT_PROGRESS = Container.group(INTERFACE_HUD_EXP_BAR, "interface_hud_exp_bar_alt_progress").color(0x8FB7A3).icon(Icons.XP_HALF_BAR).build();
    Container INTERFACE_HUD_EXP_BAR_ALT_LEVEL = Container.group(INTERFACE_HUD_EXP_BAR, "interface_hud_exp_bar_alt_level").color(0x7EFC20).icon(Icons.XP_LEVEL).build();
    Container INTERFACE_HUD_HUNGER_BAR = Container.group(INTERFACE_HUD, "interface_hud_hunger_bar").color(0xD42A2A).icon(Icons.HUNGER).build();
    Container INTERFACE_HUD_HUNGER_BAR_ALT_SATURATION = Container.group(INTERFACE_HUD_HUNGER_BAR, "interface_hud_hunger_bar_alt_saturation").color(0xE56B7E).icon(Icons.HUNGER_PARTIAL).build();
    Container INTERFACE_HUD_HUNGER_BAR_ALT_FOOD = Container.group(INTERFACE_HUD_HUNGER_BAR, "interface_hud_hunger_bar_alt_food").color(0xB79859).icon(Items.OAK_SIGN).build();
    Container INTERFACE_HUD_STAMINA_BAR = Container.group(INTERFACE_HUD, "interface_hud_stamina_bar").color(0xFCD200).icon(Icons.HUD_STAMINA).build();
    Container INTERFACE_HUD_STAMINA_BAR_ALT = Container.group(INTERFACE_HUD_STAMINA_BAR, "interface_hud_stamina_bar_alt").color(0x8B8B8B).icon(Icons.HUD_STAMINA_RECHARGE).build();
    Container INTERFACE_GUI = Container.group(INTERFACE, "interface_gui").color(0x72C9fC).icon(Icons.CLIENT).build();
    Container INTERFACE_GUI_COLOR = Container.group(INTERFACE_GUI, "interface_gui_color").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container INTERFACE_CHAT = Container.group(INTERFACE, "interface_chat").color(0xE0E0E0).icon(Icons.CHAT).build();
    Container INTERFACE_ANVIL = Container.group(INTERFACE, "interface_anvil").color(0x969696).icon(Blocks.ANVIL).build();
    Container INTERFACE_DEATH = Container.group(INTERFACE, "interface_death").color(0xC7C7C7).icon(Blocks.SKELETON_SKULL).build();
    Container INTERFACE_INVENTORY = Container.group(INTERFACE, "interface_inventory").color(0xB6896C).icon(Blocks.PLAYER_HEAD).build();
    Container INTERFACE_CRAFTING = Container.group(INTERFACE, "interface_crafting").color(0xF38A47).icon(Blocks.CRAFTING_TABLE).build();
    Container INTERFACE_FURNACE = Container.group(INTERFACE, "interface_furnace").color(0x8F8F90).icon(Blocks.FURNACE).build();
    Container INTERFACE_DEBUG = Container.group(INTERFACE, "interface_debug").color(0xFB4A4C).icon(Icons.BUG).build();
    Container INTERFACE_DEBUG_COLOR = Container.group(INTERFACE_DEBUG, "interface_debug_color").color(0xFFD800).icon(Icons.BUG_COLOR).build();
    Container INTERFACE_DEBUG_EXTRA = Container.group(INTERFACE_DEBUG, "interface_debug_extra").color(0x8EFF5E).icon(Icons.BUG_EXTRA).build();
    Container INTERFACE_DEBUG_CHART = Container.group(INTERFACE_DEBUG, "interface_debug_chart").color(0x886AE2).icon(Icons.DEBUG_CHART).build();
    Container INTERFACE_LOADING = Container.group(INTERFACE, "interface_loading").color(0xE22837).icon(Icons.MOJANG).build();
    Container INTERFACE_LOADING_COLOR = Container.group(INTERFACE_LOADING, "interface_loading_color").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container INTERFACE_WORLD = Container.group(INTERFACE, "interface_world").color(0x46A2EE).icon(Icons.FLAT_EARTH).build();
    Container INTERFACE_WORLD_SELECT = Container.group(INTERFACE_WORLD, "interface_world_select").color(0xF5E9B7).icon(Icons.BOOK_OPEN).build();
    Container INTERFACE_WORLD_CREATE = Container.group(INTERFACE_WORLD, "interface_world_create").color(0xBCDA70).icon(Icons.ADD).build();
    Container INTERFACE_PROGRESS = Container.group(INTERFACE, "interface_progress").color(0x80FF80).icon(Icons.PROGRESS_SCREEN).build();
    Container INTERFACE_PAUSE = Container.group(INTERFACE, "interface_pause").color(0xFFFFA0).icon(Icons.PAUSE).build();
    Container INTERFACE_TITLE = Container.group(INTERFACE, "interface_title").color(0xFFFF00).icon(Icons.TEXT).build();
    Container INTERFACE_TITLE_BUTTON = Container.group(INTERFACE_TITLE, "interface_title_button").color(0xE0E0E0).icon(Icons.BUTTON).build();
    Container INTERFACE_TITLE_LOGO = Container.group(INTERFACE_TITLE, "interface_title_logo").color(0xA9A7A6).icon(Blocks.STONE).build();
    Container INTERFACE_TITLE_TEXT = Container.group(INTERFACE_TITLE, "interface_title_text").color(0xB79859).icon(Items.OAK_SIGN).build();
    Container INTERFACE_TOOLTIP = Container.group(INTERFACE, "interface_tooltip").color(0x98D0E7).icon(Icons.TOOLTIP).build();
    Container INTERFACE_TOOLTIP_PARTS = Container.group(INTERFACE_TOOLTIP, "interface_tooltip_parts").color(0xFFDB5C).icon(Icons.SCREWDRIVER).build();
    Container INTERFACE_TOOLTIP_COLOR = Container.group(INTERFACE_TOOLTIP, "interface_tooltip_color").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container INTERFACE_WINDOW = Container.group(INTERFACE, "interface_window").color(0xFFFFFF).icon(Blocks.GLASS_PANE).build();

    // Item

    Container ITEM = Container.group(Category.EYE_CANDY, "item").color(0x8CF4E2).icon(Items.DIAMOND).build();
    Container ITEM_FLAT = Container.group(ITEM, "item_flat").color(0xFFFFFF).icon(Items.PAPER).build();
    Container ITEM_DISPLAY = Container.group(ITEM, "item_display").color(0xBC9862).icon(Items.ITEM_FRAME).build();
    Container ITEM_MERGING = Container.group(ITEM, "item_merging").color(0x969696).icon(Blocks.HOPPER).build();

    // Name Tag

    Container NAME_TAG = Container.group(Category.EYE_CANDY, "name_tags").color(0xE6C78C).icon(Items.NAME_TAG).build();

    // Lighting

    Container LIGHTING = Container.group(Category.EYE_CANDY, "lighting").color(0xFEDB5B).icon(Icons.YELLOW_LIGHT).build();
    Container LIGHTING_BLOCK = Container.group(LIGHTING, "lighting_block").color(0xF9D49D).icon(Blocks.GLOWSTONE).build();
    Container LIGHTING_WORLD = Container.group(LIGHTING, "lighting_world").color(0x46A2EE).icon(Icons.EARTH_LIGHT).build();
    Container LIGHTING_WORLD_ENGINE = Container.group(LIGHTING_WORLD, "lighting_world_engine").color(0xDAAE6D).icon(Blocks.PISTON).build();
    Container LIGHTING_WORLD_TEXTURE = Container.group(LIGHTING_WORLD, "lighting_world_texture").color(0xB7B7B7).icon(Items.PAINTING).build();
    Container LIGHTING_WORLD_SHADER = Container.group(LIGHTING_WORLD, "lighting_world_shader").color(0xF4E5FF).icon(Items.NETHER_STAR).build();

    // Particle

    Container PARTICLE = Container.group(Category.EYE_CANDY, "particle").color(0x00C94D).icon(Icons.PARTICLES).build();
    Container PARTICLE_BIOME = Container.group(PARTICLE, "particle_biome").color(0x46A2EE).icon(Icons.CIRCLE_EARTH).build();
    Container PARTICLE_DISABLED = Container.group(PARTICLE, "particle_disabled").color(0xDB433B).icon(Blocks.BARRIER).build();
    Container PARTICLE_EXPERIENCE = Container.group(PARTICLE, "particle_experience").color(0xF5FF8F).icon(Items.EXPERIENCE_BOTTLE).build();
    Container PARTICLE_ATTACK = Container.group(PARTICLE, "particle_attack").color(0xE0E0E0).icon(Items.IRON_SWORD).build();
    Container PARTICLE_PLAYER = Container.group(PARTICLE, "particle_player").color(0xB6896C).icon(Items.PLAYER_HEAD).build();
    Container PARTICLE_BOAT = Container.group(PARTICLE, "particle_boat").color(0x896727).icon(Items.OAK_BOAT).build();
    Container PARTICLE_BLOCK = Container.group(PARTICLE, "particle_block").color(0x98C767).icon(Blocks.GRASS_BLOCK).build();
    Container PARTICLE_EXPLOSION = Container.group(PARTICLE, "particle_explosion").color(0xE63D14).icon(Blocks.TNT).build();

    // World

    Container WORLD = Container.group(Category.EYE_CANDY, "world").color(0x46A2EE).icon(Icons.FLAT_EARTH).build();
    Container WORLD_FOG = Container.group(WORLD, "world_fog").color(0xFFFFFF).icon(Icons.FOG_EARTH).build();
    Container WORLD_FOG_CUSTOM = Container.group(WORLD_FOG, "world_fog_custom").color(0xDFADD5).icon(Icons.PINK_FOG).build();
    Container WORLD_FOG_CUSTOM_DENSITY = Container.group(WORLD_FOG_CUSTOM, "world_fog_custom_density").color(0x969696).icon(Icons.VOID_FOG).build();
    Container WORLD_FOG_CUSTOM_COLOR = Container.group(WORLD_FOG_CUSTOM, "world_fog_custom_color").color(0xEEA930).icon(Icons.FILLED_COLOR_PICKER).build();
    Container WORLD_FOG_WATER = Container.group(WORLD_FOG, "world_fog_water").color(0x8ED1FF).icon(Icons.WATER_FOG).build();
    Container WORLD_SKY = Container.group(WORLD, "world_sky").color(0xFEDB5B).icon(Icons.SKY).build();
    Container WORLD_SKY_CUSTOM = Container.group(WORLD_SKY, "world_sky_custom").color(0xEBC8E3).icon(Icons.PINK_SKY).build();
    Container WORLD_WEATHER = Container.group(WORLD, "world_weather").color(0x91FCFF).icon(Icons.WEATHER).build();
    Container WORLD_VOID = Container.group(WORLD, "world_void").color(0x969696).icon(Blocks.BEDROCK).build();
    Container WORLD_VOID_FOG = Container.group(WORLD_VOID, "world_void_fog").color(0xFFFFFF).icon(Icons.VOID_FOG).build();
    Container WORLD_VOID_SKY = Container.group(WORLD_VOID, "world_void_sky").color(0xF6BB2F).icon(Icons.VOID_SKY).build();
}
