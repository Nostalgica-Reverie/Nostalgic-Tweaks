package mod.adrenix.nostalgic.util.common.asset;

import net.minecraft.resources.Identifier;

public interface GameSprite {
    Identifier BUTTON = GameAsset.sprite("widget/button");
    Identifier BUTTON_DISABLED = GameAsset.sprite("widget/button_disabled");
    Identifier BUTTON_HIGHLIGHTED = GameAsset.sprite("widget/button_highlighted");
    Identifier SLIDER = GameAsset.sprite("widget/slider");
    Identifier SLIDER_HANDLE = GameAsset.sprite("widget/slider_handle");
    Identifier SLIDER_HANDLE_HIGHLIGHTED = GameAsset.sprite("widget/slider_handle_highlighted");
    Identifier FULL_HEART = GameAsset.sprite("hud/heart/full");
    Identifier HALF_HEART = GameAsset.sprite("hud/heart/half");
    Identifier EMPTY_HEART = GameAsset.sprite("hud/heart/container");
}
