package mod.adrenix.nostalgic.util.common.asset;

import com.google.common.hash.Hashing;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.resources.ResourceLocation;

public abstract class ModAsset
{
    /**
     * Get a new resource location from the mod's assets directory.
     *
     * @param path A path in the mod's assets directory.
     * @return A new {@link ResourceLocation} instance where the full-path appears as {@code modId:$path} where
     * {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation get(String path)
    {
        return LocateResource.mod(path);
    }

    /**
     * Get a new resource location from the mod's textures directory.
     *
     * @param path A path in the mod's {@code textures} directory.
     * @return A new {@link ResourceLocation} instance where the full-path appears as {@code modId:textures/$path} where
     * {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation texture(String path)
    {
        return get("textures/" + path);
    }

    /**
     * Get a new resource location from the mod's icon directory.
     *
     * @param path A path in the mod's {@code icon} directory.
     * @return A new {@link ResourceLocation} instance where the full-path appears as {@code modId:textures/icon/$path}
     * where {@code $path} is the given {@code path} argument.
     */
    public static ResourceLocation icon(String path)
    {
        return get("textures/icon/" + path);
    }

    /**
     * Get a new resource location from the mod's gui sprites directory. These textures will be stored on the vanilla
     * gui texture atlas. Therefore, sprites are stored in {@code assets/minecraft/textures/gui/sprites/mod_id}. Do
     * <b color=red>not</b> include {@code .png} in the path. The game does not use the file extension.
     *
     * @param path A path in the mod's {@code sprites} directory.
     * @return A new {@link ResourceLocation} instance where the returned-path references a location on the gui atlas
     * texture sheet, and appears as {@code minecraft:nostalgic_tweaks/$path} where {@code $path} is the given
     * {@code path} argument.
     */
    public static ResourceLocation sprite(String path)
    {
        return LocateResource.game(NostalgicTweaks.MOD_ID + "/" + path);
    }

    /**
     * Get a new resource location that references a supporter face icon.
     *
     * @param username The username to lookup.
     * @return A new {@link ResourceLocation}.
     */
    @SuppressWarnings({ "deprecation" })
    public static ResourceLocation supporter(String username)
    {
        return get("supporter_face/" + Hashing.sha1().hashUnencodedChars(username));
    }
}
