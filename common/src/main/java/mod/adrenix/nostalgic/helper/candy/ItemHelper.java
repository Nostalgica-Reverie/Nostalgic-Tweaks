package mod.adrenix.nostalgic.helper.candy;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * This utility can be used by both client and server.
 */
public abstract class ItemHelper
{
    /**
     * Splits an item stack into separate item entities.
     *
     * @param itemStack The {@link ItemStack} to separate.
     * @param consumer  The {@link Consumer} that accepts all {@link ItemStack} to spawn.
     */
    public static void splitStack(ItemStack itemStack, Consumer<ItemStack> consumer)
    {
        int count = itemStack.getCount();

        if (count >= CandyTweak.ITEM_MERGE_LIMIT.get())
            consumer.accept(itemStack);
        else
        {
            ItemStack instance = itemStack.copy();
            instance.setCount(1);

            for (int i = 0; i < count; i++)
                consumer.accept(instance);
        }
    }

    /**
     * Split up an item entity new individual entities.
     *
     * @param level  The {@link Level} instance.
     * @param entity The {@link ItemEntity} to split.
     */
    public static void splitEntity(Level level, ItemEntity entity)
    {
        int count = entity.getItem().getCount();

        if (!CandyTweak.OLD_ITEM_MERGING.get() || CandyTweak.ITEM_MERGE_LIMIT.get() <= count)
            return;

        entity.getItem().setCount(1);
        entity.setDefaultPickUpDelay();

        for (int i = 0; i < Math.max(0, count - 1); i++)
        {
            double x = (double) ((float) entity.getX() + 0.01F) + Mth.nextDouble(level.random, -0.04, 0.04);
            double y = (double) ((float) entity.getY() + 0.01F) + Mth.nextDouble(level.random, -0.04, 0.04) - (double) EntityType.ITEM.getHeight() / 2.0F;
            double z = (double) ((float) entity.getZ() + 0.01F) + Mth.nextDouble(level.random, -0.04, 0.04);

            ItemEntity itemEntity = new ItemEntity(level, x, y, z, entity.getItem());

            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }
    }

    /**
     * Check if the given item stack can merge with its neighbors.
     *
     * @param originStack The origin {@link ItemStack} instance.
     * @param neighbors   The {@link List} of {@link ItemEntity} neighbors.
     * @return Whether the given item stack can merge with the given neighbors.
     */
    public static boolean canMergeWithNeighbors(ItemStack originStack, List<ItemEntity> neighbors)
    {
        int mergeLimit = CandyTweak.ITEM_MERGE_LIMIT.get();
        int numOfNeighbors = neighbors.size();
        int originSize = originStack.getCount();
        boolean isBelowLimit = numOfNeighbors + 1 < mergeLimit && originSize == 1;
        boolean isNeighborStacked = false;

        for (ItemEntity neighbor : neighbors)
        {
            if (neighbor.getItem().getCount() > 1)
            {
                isNeighborStacked = true;
                break;
            }
        }

        return !isBelowLimit || isNeighborStacked;
    }

    /**
     * Check if the given item stack is allowed to render the old durability bar colors.
     *
     * @param itemStack The {@link ItemStack} instance to check.
     * @return Whether the given item stack can use the old durability bar colors.
     */
    public static boolean hasOldDecoration(ItemStack itemStack)
    {
        if (!CandyTweak.OLD_DURABILITY_COLORS.get() || ItemUtil.hasCustomBarColor(itemStack))
            return false;

        return !CandyTweak.IGNORED_DURABILITY_COLORS.get().containsItem(itemStack);
    }
}
