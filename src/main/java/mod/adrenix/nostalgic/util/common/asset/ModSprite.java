package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.Identifier;

public interface ModSprite {
    Identifier OVERLAY = ModAsset.sprite("overlay/border");
    Identifier RECIPE_BUTTON_SMALL = ModAsset.sprite("recipe_book/button_small");
    Identifier RECIPE_BUTTON_LARGE = ModAsset.sprite("recipe_book/button_large");
    Identifier RECIPE_BUTTON_SMALL_HIGHLIGHTED = ModAsset.sprite("recipe_book/button_small_highlighted");
    Identifier RECIPE_BUTTON_LARGE_HIGHLIGHTED = ModAsset.sprite("recipe_book/button_large_highlighted");
    Identifier ADVENTURE_CRAFT_OFFHAND_LEFT_SLOT = ModAsset.sprite("hud/ac_offhand_left_slot");
    Identifier ADVENTURE_CRAFT_OFFHAND_RIGHT_SLOT = ModAsset.sprite("hud/ac_offhand_right_slot");
    Identifier STAMINA_LEVEL = ModAsset.sprite("hud/stamina_level");
    Identifier STAMINA_LEVEL_HALF = ModAsset.sprite("hud/stamina_level_half");
    Identifier STAMINA_RECHARGE = ModAsset.sprite("hud/stamina_recharge");
    Identifier STAMINA_RECHARGE_HALF = ModAsset.sprite("hud/stamina_recharge_half");
    Identifier STAMINA_COOLING = ModAsset.sprite("hud/stamina_cooling");
    Identifier STAMINA_COOLING_HALF = ModAsset.sprite("hud/stamina_cooling_half");
    Identifier STAMINA_POSITIVE = ModAsset.sprite("hud/stamina_positive");
    Identifier STAMINA_POSITIVE_HALF = ModAsset.sprite("hud/stamina_positive_half");
    Identifier STAMINA_NEGATIVE = ModAsset.sprite("hud/stamina_negative");
    Identifier STAMINA_NEGATIVE_HALF = ModAsset.sprite("hud/stamina_negative_half");
    Identifier STAMINA_HIGHLIGHT = ModAsset.sprite("hud/stamina_highlight");
    Identifier STAMINA_EMPTY = ModAsset.sprite("hud/stamina_empty");

    static Identifier icon(String path) {
        return ModAsset.sprite("icon/" + path);
    }
}
