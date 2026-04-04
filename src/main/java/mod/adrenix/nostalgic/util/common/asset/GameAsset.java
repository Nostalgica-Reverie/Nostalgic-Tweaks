package mod.adrenix.nostalgic.util.common.asset;

import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.resources.Identifier;

public abstract class GameAsset {
    /**
     * Get a new resource location from the game's texture directory.
     *
     * @param path A path in the game's {@code textures} directory.
     * @return A new {@link Identifier} location instance where the full-path appears as
     * {@code minecraft:textures/$path} where {@code $path} is the given {@code path} argument.
     */
    @PublicAPI
    public static Identifier texture(String path) {
        return LocateResource.game("textures/" + path);
    }

    /**
     * Get a new resource location from the game's gui sprites directory. These textures will be stored on the vanilla
     * gui texture atlas. Therefore, sprites are stored in {@code assets/minecraft/textures/gui/sprites}. Do
     * <b color=red>not</b> include {@code .png} in the path. The path should be relative to the sprite directory. For
     * example, {@code "recipe_book/button"}.
     *
     * @param path A path in the game's {@code sprites} directory.
     * @return A new {@link Identifier} location instance.
     */
    @PublicAPI
    public static Identifier sprite(String path) {
        return LocateResource.game(path);
    }
}
