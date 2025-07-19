package mod.adrenix.nostalgic.util.common.world;

import mod.adrenix.nostalgic.tweak.listing.ItemListing;
import mod.adrenix.nostalgic.tweak.listing.ItemRule;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.AbstractChestBlock;

public abstract class ItemFilter
{
    /**
     * Check if an item is filtered by the given listing.
     *
     * @param item An {@link Item} to check.
     * @param list A {@link ItemListing} to get {@link ItemRule} values from.
     * @return Whether the given item should be filtered.
     */
    public static boolean isFiltered(Item item, ItemListing<?, ?> list)
    {
        if (isUnique(item))
            return true;

        if (list.rules().contains(ItemRule.NONE))
            return false;
        else if (list.rules().contains(ItemRule.ONLY_TOOLS))
            return !isToolLike(item);
        else if (list.rules().contains(ItemRule.ONLY_ITEMS))
            return !isItemLike(item);
        else if (list.rules().contains(ItemRule.ONLY_BLOCKS))
            return !isBlockLike(item);
        else if (list.rules().contains(ItemRule.ONLY_CHESTS))
            return !isChestLike(item);
        else if (list.rules().contains(ItemRule.ONLY_EDIBLES))
            return !item.isEdible();
        else if (list.rules().contains(ItemRule.ONLY_DAMAGEABLE))
            return !ItemUtil.isDamageable(item);
        else
        {
            boolean isToolFiltered = ItemFilter.isToolLike(item) && list.rules().contains(ItemRule.NO_TOOLS);
            boolean isItemFiltered = ItemFilter.isItemLike(item) && list.rules().contains(ItemRule.NO_ITEMS);
            boolean isBlockFiltered = ItemFilter.isBlockLike(item) && list.rules().contains(ItemRule.NO_BLOCKS);
            boolean isEdibleFiltered = item.isEdible() && list.rules().contains(ItemRule.NO_EDIBLES);
            boolean isDamageableFiltered = ItemUtil.isDamageable(item) && list.rules().contains(ItemRule.INVINCIBLE);

            return isToolFiltered || isItemFiltered || isBlockFiltered || isEdibleFiltered || isDamageableFiltered;
        }
    }

    /**
     * Overload method of {@link #isFiltered(Item, ItemListing)}.
     *
     * @param itemStack An {@link ItemStack} to check.
     * @param list      A {@link ItemListing} to get {@link ItemRule} values from.
     * @return Whether the given item should be filtered.
     */
    public static boolean isFiltered(ItemStack itemStack, ItemListing<?, ?> list)
    {
        return isFiltered(itemStack.getItem(), list);
    }

    /**
     * Check if an item has extra data that cannot be properly saved to a config file.
     *
     * @param item An {@link Item} to check.
     * @return Whether the item is unique.
     */
    public static boolean isUnique(Item item)
    {
        ItemStack itemStack = item.getDefaultInstance();
        ItemStack copyStack = ItemUtil.getItemStack(ItemUtil.getResourceKey(item));

        return itemStack.getTag() != null && !itemStack.getTag().equals(copyStack.getTag());
    }

    /**
     * Check if the given {@link Item} is a tool-like item.
     *
     * @param item An {@link Item} instance.
     * @return Whether the given {@link Item} is tool-like.
     */
    public static boolean isToolLike(Item item)
    {
        return item instanceof DiggerItem || item instanceof SwordItem;
    }

    /**
     * Check if the given {@link Item} is a block-like item.
     *
     * @param item An {@link Item} instance.
     * @return Whether the given {@link Item} is block-like.
     */
    public static boolean isBlockLike(Item item)
    {
        return item instanceof BlockItem;
    }

    /**
     * Check if the given {@link Item} is an item-like item.
     *
     * @param item An {@link Item} instance.
     * @return Whether the given {@link Item} is item-like.
     */
    public static boolean isItemLike(Item item)
    {
        return !isToolLike(item) && !isBlockLike(item);
    }

    /**
     * Check if the given {@link Item} is a chest-like item.
     *
     * @param item An {@link Item} instance.
     * @return Whether the given {@link Item} is chest-like.
     */
    public static boolean isChestLike(Item item)
    {
        if (item instanceof BlockItem blockItem)
            return blockItem.getBlock() instanceof AbstractChestBlock<?>;

        return false;
    }
}
