package mod.adrenix.nostalgic.util.common.world;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * This utility is used by both client and server.
 */
public abstract class BlockUtil
{
    /**
     * Get a block based on the provided resource location key.
     *
     * @param resourceKey The block's resource location key.
     * @return A block from the registry if it exists.
     */
    @PublicAPI
    public static Block getBlock(String resourceKey)
    {
        return ItemUtil.getBlock(resourceKey);
    }

    /**
     * Get a random block position within the given {@code bounds}.
     *
     * @param randomSource The {@link RandomSource} instance.
     * @param bound        How far out from the source a random position can be.
     * @return A random {@link BlockPos} instance.
     */
    @PublicAPI
    public static BlockPos getRandom(RandomSource randomSource, int bound)
    {
        return new BlockPos(randomSource.nextInt(bound), randomSource.nextInt(bound), randomSource.nextInt(bound));
    }

    /**
     * Check if the given {@link BlockState} matches any of the given blocks.
     *
     * @param blockState The {@link BlockState} to check.
     * @param blocks     A varargs list of {@link Block} to compare to.
     * @return Whether the {@link BlockState} matched any of the given blocks.
     */
    @PublicAPI
    public static boolean match(BlockState blockState, Block... blocks)
    {
        for (Block block : blocks)
        {
            if (blockState.is(block))
                return true;
        }

        return false;
    }

    /**
     * Check if the given block state matches a chest-like block.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether the block is chest-like.
     */
    @PublicAPI
    public static boolean isChestLike(BlockState blockState)
    {
        return isChestLike(blockState.getBlock());
    }

    /**
     * Check if the given block is chest-like.
     *
     * @param block The {@link Block} to check.
     * @return Whether the block is chest-like.
     */
    @PublicAPI
    public static boolean isChestLike(Block block)
    {
        return block instanceof AbstractChestBlock<?>;
    }

    /**
     * Check if the given block state matches a water-like block. There are no block or block pos overload methods since
     * it is possible to get an incorrect value. For example, if the default block state is given, then it cannot be
     * checked if the block state is waterlogged.
     *
     * @param blockState The {@link BlockState} to check.
     * @return Whether the block is water-like.
     */
    @PublicAPI
    public static boolean isWaterLike(BlockState blockState)
    {
        return blockState.getFluidState().is(FluidTags.WATER);
    }

    /**
     * Check if the given block position is near the bedrock layer.
     *
     * @param blockPos The {@link BlockPos} to check.
     * @param level    The {@link Level} to get height context from.
     * @return Whether the given position is less than five blocks above the minimum build height.
     */
    @PublicAPI
    public static boolean isNearBedrock(BlockPos blockPos, Level level)
    {
        return blockPos.getY() < level.getMinBuildHeight() + 5;
    }
}
