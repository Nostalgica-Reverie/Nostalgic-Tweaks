package mod.adrenix.nostalgic.tweak.container;

import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;

/**
 * All categories defined in this utility class need to have container ids that match what will appear in the config
 * JSON file. This is required so reflection can be performed when tweak values need changed.
 */
// @formatter:off
public interface Category
{
    Container ROOT = Container.category("").icon(TextureLocation.NT_LOGO_64).color(0xFFFF00).internal().build();
    Container MOD = Container.category("mod").inherit(ROOT).icon(TextureLocation.NT_LOGO_64).color(0x87BAFE).internal().build();
    Container SOUND = Container.category("sound").icon(TextureLocation.SOUND_16).color(0xA894DD).build();
    Container EYE_CANDY = Container.category("eyeCandy").icon(TextureLocation.CANDY_16).color(0xE5707F).build();
    Container GAMEPLAY = Container.category("gameplay").icon(TextureLocation.GAMEPLAY_16).color(0xFFCC4D).build();
    Container ANIMATION = Container.category("animation").icon(TextureLocation.ANIMATION_16).color(0xFA743E).build();
    Container SWING = Container.category("swing").icon(Icons.BREAK_WOOD).color(0x71C041).description().build();
}
