package mod.adrenix.nostalgic.util.common.asset;

import mod.adrenix.nostalgic.util.common.sprite.GuiSprite;
import mod.adrenix.nostalgic.util.common.sprite.SpriteAtlas;
import net.minecraft.resources.ResourceLocation;

public interface ModSprite
{
    GuiSprite OVERLAY = GuiSprite.nineSlice(SpriteAtlas.fromSprite("overlay/border", 256, 256), 8, 15, 8, 8);
    GuiSprite RECIPE_BUTTON_SMALL = GuiSprite.stretch(SpriteAtlas.fromSprite("recipe_book/button_small", 9, 10));
    GuiSprite RECIPE_BUTTON_LARGE = GuiSprite.stretch(SpriteAtlas.fromSprite("recipe_book/button_large", 18, 18));
    GuiSprite RECIPE_BUTTON_SMALL_HIGHLIGHTED = GuiSprite.stretch(SpriteAtlas.fromSprite("recipe_book/button_small_highlighted", 9, 10));
    GuiSprite RECIPE_BUTTON_LARGE_HIGHLIGHTED = GuiSprite.stretch(SpriteAtlas.fromSprite("recipe_book/button_large_highlighted", 18, 18));
    GuiSprite ADVENTURE_CRAFT_OFFHAND_LEFT_SLOT = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/ac_offhand_left_slot", 29, 24));
    GuiSprite ADVENTURE_CRAFT_OFFHAND_RIGHT_SLOT = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/ac_offhand_right_slot", 29, 24));
    GuiSprite STAMINA_LEVEL = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_level", 9, 9));
    GuiSprite STAMINA_LEVEL_HALF = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_level_half", 9, 9));
    GuiSprite STAMINA_RECHARGE = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_recharge", 9, 9));
    GuiSprite STAMINA_RECHARGE_HALF = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_recharge_half", 9, 9));
    GuiSprite STAMINA_COOLING = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_cooling", 9, 9));
    GuiSprite STAMINA_COOLING_HALF = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_cooling_half", 9, 9));
    GuiSprite STAMINA_POSITIVE = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_positive", 9, 9));
    GuiSprite STAMINA_POSITIVE_HALF = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_positive_half", 9, 9));
    GuiSprite STAMINA_NEGATIVE = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_negative", 9, 9));
    GuiSprite STAMINA_NEGATIVE_HALF = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_negative_half", 9, 9));
    GuiSprite STAMINA_HIGHLIGHT = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_highlight", 9, 9));
    GuiSprite STAMINA_EMPTY = GuiSprite.stretch(SpriteAtlas.fromSprite("hud/stamina_empty", 9, 9));

    static ResourceLocation icon(String path)
    {
        return ModAsset.sprite("icon/" + path);
    }
}
