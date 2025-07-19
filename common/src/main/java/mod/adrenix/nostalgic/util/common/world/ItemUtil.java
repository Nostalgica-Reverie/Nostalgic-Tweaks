package mod.adrenix.nostalgic.util.common.world;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * This utility is used by both client and server.
 */
public abstract class ItemUtil
{
    /**
     * Get an optional item instance based on the provided resource location key.
     *
     * @param resourceKey An item's resource location key.
     * @return An optional item instance from the registry.
     */
    @PublicAPI
    public static Optional<Item> getOptionalItem(String resourceKey)
    {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.tryParse(resourceKey));
    }

    /**
     * Get an item instance based on the provided resource location key.
     *
     * @param resourceKey An item's resource location key.
     * @return An item instance from the registry if that item exists.
     */
    @PublicAPI
    public static Item getItem(String resourceKey)
    {
        return BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(resourceKey));
    }

    /**
     * Get an item stack instance based on the provided resource location key.
     *
     * @param resourceKey An item's resource location key.
     * @return An item stack instance from the registry if that item exists.
     */
    @PublicAPI
    public static ItemStack getItemStack(String resourceKey)
    {
        return getItem(resourceKey).getDefaultInstance();
    }

    /**
     * Get a block based on the provided resource location key.
     *
     * @param resourceKey The block's resource location key.
     * @return A block from the registry if it exists.
     */
    @PublicAPI
    public static Block getBlock(String resourceKey)
    {
        return BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(resourceKey));
    }

    /**
     * Get a block based on the provided item.
     *
     * @param item An item instance to get block data from.
     * @return A block from the registry if it exists.
     */
    @PublicAPI
    public static Block getBlockFromItem(Item item)
    {
        return getBlock(getResourceKey(item));
    }

    /**
     * Generates a unique key that will be associated with an item instance. This key is the toString method associated
     * with the item registry.
     *
     * @param item The item instance to get registry key information from.
     * @return A resource location key that can be stored in a configuration file.
     */
    @PublicAPI
    public static String getResourceKey(Item item)
    {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    /**
     * Generates a unique key that will be associated with an item stack instance. This key is the toString method
     * associated with the item registry.
     *
     * @param itemStack The item stack instance to get registry key information from.
     * @return A resource location key that can be stored in a configuration file.
     */
    @PublicAPI
    public static String getResourceKey(ItemStack itemStack)
    {
        return getResourceKey(itemStack.getItem());
    }

    /**
     * Generates a unique key that will be associated with a block item instance. This key is the toString method
     * associated with the item registry.
     *
     * @param block The block instance to get registry key information from.
     * @return A resource location key that can be stored in a configuration file.
     */
    @PublicAPI
    public static String getResourceKey(Block block)
    {
        return BuiltInRegistries.ITEM.getKey(block.asItem()).toString();
    }

    /**
     * Generate a set of resource item keys from the given var args list of item instances.
     *
     * @param items The var args item list to get keys from.
     * @return A hash set of item resource keys.
     */
    @PublicAPI
    public static LinkedHashSet<String> getKeysFromItems(Item... items)
    {
        LinkedHashSet<String> keys = new LinkedHashSet<>();

        for (Item item : items)
            keys.add(getResourceKey(item));

        return keys;
    }

    /**
     * Checks if a resource location key exists within the registry.
     *
     * @param resourceKey An item's resource location key.
     * @return Whether the given resource location key exists.
     */
    @PublicAPI
    public static boolean isValidKey(String resourceKey)
    {
        return getResourceKey(getItem(resourceKey)).equals(resourceKey);
    }

    /**
     * Get a localized item name.
     *
     * @param resourceKey An item's resource location key.
     * @return A localized item name (if it exists) that is associated with the given key.
     */
    @PublicAPI
    public static String getLocalizedItem(String resourceKey)
    {
        try
        {
            return getItem(resourceKey).getDefaultInstance().getHoverName().getString();
        }
        catch (Throwable throwable)
        {
            return resourceKey;
        }
    }

    /**
     * Get a localized item name.
     *
     * @param itemStack An {@link ItemStack} to get a resource location key from.
     * @return A localized item name that is associated with the given item stack.
     */
    @PublicAPI
    public static String getLocalizedItem(ItemStack itemStack)
    {
        return getLocalizedItem(getResourceKey(itemStack.getItem()));
    }

    /**
     * Check if an item is using a custom durability bar color.
     *
     * @param itemStack The {@link ItemStack} instance to check.
     * @return Whether the given item is using a custom durability bar color.
     */
    @PublicAPI
    public static boolean hasCustomBarColor(ItemStack itemStack)
    {
        float maxDamage = (float) itemStack.getMaxDamage();
        float damageValue = (float) itemStack.getDamageValue();
        int color = Mth.hsvToRgb((Math.max(0.0F, (maxDamage - damageValue) / maxDamage)) / 3.0F, 1.0F, 1.0F);

        return itemStack.getBarColor() != color;
    }

    /**
     * Check if the given item stack is damageable.
     *
     * @param itemStack The {@link ItemStack} to check.
     * @return Whether the given item stack is damageable.
     */
    @PublicAPI
    public static boolean isDamageable(ItemStack itemStack)
    {
        return itemStack.isDamageableItem() || itemStack.isBarVisible();
    }

    /**
     * Check if the given item is damageable.
     *
     * @param item The {@link Item} to check.
     * @return Whether the given item is damageable.
     */
    @PublicAPI
    public static boolean isDamageable(Item item)
    {
        return isDamageable(item.getDefaultInstance());
    }
}
