package mod.adrenix.nostalgic.util.common;

import mod.adrenix.nostalgic.NostalgicTweaks;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class LocateResource
{
    /**
     * Get a resource location using the mod as the namespace.
     *
     * @param path The path of the mod's resource.
     * @return A {@link ResourceLocation} using the mod id as the namespace and the given {@code path}.
     */
    public static ResourceLocation mod(@NotNull String path)
    {
        return ResourceLocation.fromNamespaceAndPath(NostalgicTweaks.MOD_ID, path);
    }

    /**
     * Get a resource location using Minecraft as the namespace.
     *
     * @param location The location of the game's resource.
     * @return A {@link ResourceLocation} using {@code minecraft} as the namespace and the given {@code location}.
     */
    public static ResourceLocation game(@NotNull String location)
    {
        return ResourceLocation.withDefaultNamespace(location);
    }

    /**
     * Get a resource location by parsing the given location string.
     *
     * @param location The location of the resource. This location must have a namespace and ':' separator.
     * @return A {@link ResourceLocation} using the given location.
     */
    public static ResourceLocation from(@NotNull String location)
    {
        return ResourceLocation.parse(location);
    }
}
