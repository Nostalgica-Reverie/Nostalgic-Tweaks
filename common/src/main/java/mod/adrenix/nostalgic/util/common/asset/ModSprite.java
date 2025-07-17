package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.ResourceLocation;

public interface ModSprite
{
    ResourceLocation OVERLAY = ModAsset.sprite("overlay/border");
    ResourceLocation RECIPE_BUTTON_SMALL = ModAsset.sprite("recipe_book/button_small");
    ResourceLocation RECIPE_BUTTON_LARGE = ModAsset.sprite("recipe_book/button_large");
    ResourceLocation RECIPE_BUTTON_SMALL_HIGHLIGHTED = ModAsset.sprite("recipe_book/button_small_highlighted");
    ResourceLocation RECIPE_BUTTON_LARGE_HIGHLIGHTED = ModAsset.sprite("recipe_book/button_large_highlighted");
    ResourceLocation ADVENTURE_CRAFT_OFFHAND_LEFT_SLOT = ModAsset.sprite("hud/ac_offhand_left_slot");
    ResourceLocation ADVENTURE_CRAFT_OFFHAND_RIGHT_SLOT = ModAsset.sprite("hud/ac_offhand_right_slot");
    ResourceLocation STAMINA_LEVEL = ModAsset.sprite("hud/stamina_level");
    ResourceLocation STAMINA_LEVEL_HALF = ModAsset.sprite("hud/stamina_level_half");
    ResourceLocation STAMINA_RECHARGE = ModAsset.sprite("hud/stamina_recharge");
    ResourceLocation STAMINA_RECHARGE_HALF = ModAsset.sprite("hud/stamina_recharge_half");
    ResourceLocation STAMINA_COOLING = ModAsset.sprite("hud/stamina_cooling");
    ResourceLocation STAMINA_COOLING_HALF = ModAsset.sprite("hud/stamina_cooling_half");
    ResourceLocation STAMINA_POSITIVE = ModAsset.sprite("hud/stamina_positive");
    ResourceLocation STAMINA_POSITIVE_HALF = ModAsset.sprite("hud/stamina_positive_half");
    ResourceLocation STAMINA_NEGATIVE = ModAsset.sprite("hud/stamina_negative");
    ResourceLocation STAMINA_NEGATIVE_HALF = ModAsset.sprite("hud/stamina_negative_half");
    ResourceLocation STAMINA_HIGHLIGHT = ModAsset.sprite("hud/stamina_highlight");
    ResourceLocation STAMINA_EMPTY = ModAsset.sprite("hud/stamina_empty");

    static ResourceLocation icon(String path)
    {
        return ModAsset.sprite("icon/" + path);
    }
}
