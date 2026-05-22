package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

/**
 * This utility class is used by both the client and server.
 */
public abstract class SpongeBlockHelper
{
    /**
     * Check if water is about to flow into the dry zone of a sponge block.
     *
     * @param level   The {@link BlockGetter} level instance.
     * @param fromPos The {@link BlockPos} of the water block extending itself.
     * @param toPos   The {@link BlockPos} of where the water block is trying to extend to.
     * @return Whether water is about to flow into the 5x5x5 dry zone area of a sponge block.
     */
    public static boolean isWaterFlowingTowardsSponge(BlockGetter level, BlockPos fromPos, BlockPos toPos)
    {
        AABB searchArea = new AABB(fromPos).inflate(3.0D);

        boolean isSpongeNearby = BlockPos.betweenClosedStream(searchArea)
            .anyMatch(blockPos -> level.getBlockState(blockPos).is(Blocks.SPONGE));

        if (!isSpongeNearby)
            return false;

        return BlockPos.betweenClosedStream(searchArea)
            .filter(blockPos -> level.getBlockState(blockPos).is(Blocks.SPONGE))
            .anyMatch(spongePos -> {
                int sourceDistToSponge = BlockUtil.getChebyshevDist(fromPos, spongePos);
                int flowDistToSponge = BlockUtil.getChebyshevDist(toPos, spongePos);

                return flowDistToSponge < sourceDistToSponge;
            });
    }

    /**
     * Quickly get an iterable of a 5x5x5 dry zone area of block positions.
     *
     * @param blockPos The {@link BlockPos} center.
     * @return An {@link Iterable} of {@link BlockPos} for a 5x5x5 area around the given center.
     */
    private static Iterable<BlockPos> dryZone(BlockPos blockPos)
    {
        BlockPos firstPos = blockPos.offset(-2, -2, -2);
        BlockPos secondPos = blockPos.offset(2, 2, 2);

        return BlockPos.betweenClosed(firstPos, secondPos);
    }

    /**
     * Try to absorb all water around a sponge block.
     *
     * @param level     The {@link Level} instance.
     * @param spongePos The {@link BlockPos} of the sponge block.
     */
    public static void tryAbsorbWater(Level level, BlockPos spongePos)
    {
        for (BlockPos blockPos : dryZone(spongePos))
        {
            BlockState blockState = level.getBlockState(blockPos);

            if (blockState.getFluidState().is(FluidTags.WATER))
            {
                if (blockState.hasProperty(BlockStateProperties.WATERLOGGED))
                    level.setBlock(blockPos, blockState.setValue(BlockStateProperties.WATERLOGGED, false), Block.UPDATE_ALL);
                else if (blockState.getBlock() instanceof LiquidBlock)
                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                else if (blockState.getBlock() instanceof LiquidBlockContainer)
                {
                    BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
                    Block.dropResources(blockState, level, blockPos, blockEntity);
                    level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    /**
     * Try to update the physics of all water blocks surrounding the sponge's 5x5x5 dry zone.
     *
     * @param level       The {@link Level} instance.
     * @param spongePos   The {@link BlockPos} of the sponge block.
     * @param spongeState The {@link BlockState} of the sponge block.
     */
    public static void tryRestoreWater(Level level, BlockPos spongePos, BlockState spongeState)
    {
        for (BlockPos blockPos : dryZone(spongePos))
        {
            int dx = Math.abs(blockPos.getX() - spongePos.getX());
            int dy = Math.abs(blockPos.getY() - spongePos.getY());
            int dz = Math.abs(blockPos.getZ() - spongePos.getZ());

            if (dx == 2 || dy == 2 || dz == 2)
                level.updateNeighborsAt(blockPos, spongeState.getBlock());
        }
    }
}
