package mod.adrenix.nostalgic.helper.candy.light;

import mod.adrenix.nostalgic.helper.candy.block.ChestHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.data.NullableAction;
import mod.adrenix.nostalgic.util.common.data.Pair;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * This utility class is used only by the client.
 */
public abstract class LightingHelper
{
    /* Fields */

    /**
     * This tracks the last known time skylight value. When the time skylight does not equal the value stored in this
     * holder, then world relighting will need to be applied.
     */
    private static final IntegerHolder TIME_SKYLIGHT = IntegerHolder.create(-1);

    /**
     * This tracks the last known skylight value from weather. When the weather skylight does not equal the value stored
     * in this holder, then world relighting will need to be applied.
     */
    private static final IntegerHolder WEATHER_SKYLIGHT = IntegerHolder.create(-1);

    /**
     * This tracks if the world needs relighting applied. World relighting should only be applied as needed since the
     * lighting process takes time to complete.
     */
    private static final FlagHolder ENQUEUE_RELIGHT = FlagHolder.off();

    /**
     * This caches the light block values used by the block behavior class. Using a cache greatly speeds up processing
     * time when a chunk is being relighted.
     */
    public static final ConcurrentHashMap<Block, Integer> LIGHT_BLOCK_CACHE = new ConcurrentHashMap<>();

    /**
     * This tracks the chunks that need relighting. Chunks that load into view only need a chunk relight pass once. The
     * change in light data is stored in the client level lighting engine. The packed chunk pos is stored on the left
     * and a boolean-like byte (1 is on/all other values are off) that indicates if the chunk needs full relighting. A
     * boolean-like byte is used to save memory.
     */
    public static final ConcurrentLinkedDeque<Pair<Long, Byte>> PACKED_RELIGHT_QUEUE = new ConcurrentLinkedDeque<>();

    /**
     * This is a queue of packed longs where the packed chunk pos is on the left, and the packed block pos is on the
     * right to check after a chunk is relighted. This queue is slowly emptied at the start of level rendering. Only a
     * certain number of queued objects are executed per render pass to prevent excessive lag.
     */
    public static final ConcurrentLinkedDeque<Pair<Long, Long>> PACKED_CHUNK_BLOCK_QUEUE = new ConcurrentLinkedDeque<>();

    /**
     * This is a queue of packed section coordinates for relighting. The purpose of this queue is to improve performance
     * and mod compatibility.
     */
    public static final ConcurrentLinkedDeque<Long> CHUNK_RELIGHT_QUEUE = new ConcurrentLinkedDeque<>();

    /**
     * This is a queue of non-built packed section coordinates that will later be scheduled for rebuilding in Sodium's
     * render section manager. A queue is used to ensure all sections receive relighting during terrain rendering. Once
     * Sodium's render section manager has finished terrain rendering, this queue is emptied.
     */
    public static final HashSet<Long> SODIUM_REBUILD_QUEUE = new HashSet<>();

    /**
     * This tracks whether the level renderer needs to relight all chunks loaded by the client player.
     */
    public static final FlagHolder RELIGHT_ALL_CHUNKS = FlagHolder.off();

    /* Methods */

    /**
     * Chunks need their light recalculated if any of these tweaks have their disk values changed.
     */
    public static void init()
    {
        CandyTweak.OLD_WATER_LIGHTING.whenChanged(LightingHelper::invalidateAndRelight);
        CandyTweak.CHEST_LIGHT_BLOCK.whenChanged(LightingHelper::invalidateAndRelight);
    }

    /**
     * Invalidates the light block cache and instructs the lighting engine to recalculate the lighting in chunks loaded
     * by the client.
     */
    private static void invalidateAndRelight()
    {
        LIGHT_BLOCK_CACHE.clear();
        RELIGHT_ALL_CHUNKS.enable();
    }

    /**
     * Resets relighting cache back to its default state. This should be done when the player exits the world since the
     * last known relighting cache will be invalid if the next world joined is different.
     */
    public static void resetLightingCache()
    {
        TIME_SKYLIGHT.set(-1);
        WEATHER_SKYLIGHT.set(-1);
        ENQUEUE_RELIGHT.disable();
        CHUNK_RELIGHT_QUEUE.clear();
        SODIUM_REBUILD_QUEUE.clear();
        PACKED_RELIGHT_QUEUE.clear();
        PACKED_CHUNK_BLOCK_QUEUE.clear();
    }

    /**
     * @return Whether the world needs relighting applied.
     */
    public static boolean isRelightCheckEnqueued()
    {
        return ENQUEUE_RELIGHT.get();
    }

    /**
     * Mark the world relighting as finished.
     */
    public static void setRelightingAsFinished()
    {
        ENQUEUE_RELIGHT.disable();
    }

    /**
     * Checks if world relighting is necessary based on the time of day and weather.
     */
    public static void onTick()
    {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null)
            return;

        int skylightFromTime = getSkyLightFromTime(level);
        int skylightFromWeather = getSkyLightFromWeather(level);

        if (TIME_SKYLIGHT.get() == -1 || TIME_SKYLIGHT.get() != skylightFromTime)
        {
            TIME_SKYLIGHT.set(skylightFromTime);
            ENQUEUE_RELIGHT.enable();
        }

        if (WEATHER_SKYLIGHT.get() == -1 || WEATHER_SKYLIGHT.get() != skylightFromWeather)
        {
            WEATHER_SKYLIGHT.set(skylightFromWeather);

            if (TIME_SKYLIGHT.get() > 4)
                ENQUEUE_RELIGHT.enable();
        }
    }

    /**
     * Checks the relighting queues and performs relighting on chunks that need it.
     */
    public static void checkRelightQueues()
    {
        ClientLevel level = Minecraft.getInstance().level;
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;

        if (level == null)
            return;

        if (!PACKED_RELIGHT_QUEUE.isEmpty())
        {
            Pair<Long, Byte> packedRelight = PACKED_RELIGHT_QUEUE.pop();
            ChunkPos chunkPos = new ChunkPos(packedRelight.left());
            LevelChunk chunk = level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);

            if (chunk != null)
                relightChunk(chunk, packedRelight.right());
            else if (renderer.isSectionCompiled(chunkPos.getWorldPosition()))
                PACKED_RELIGHT_QUEUE.add(packedRelight);
        }

        while (!CHUNK_RELIGHT_QUEUE.isEmpty())
            level.getChunkSource().onLightUpdate(LightLayer.BLOCK, SectionPos.of(CHUNK_RELIGHT_QUEUE.pop()));

        for (int i = 0; i < 4096; i++)
        {
            if (PACKED_CHUNK_BLOCK_QUEUE.isEmpty())
                break;

            Pair<Long, Long> packedQueue = PACKED_CHUNK_BLOCK_QUEUE.pop();
            ChunkPos chunkPos = new ChunkPos(packedQueue.left());
            LevelChunk chunk = level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, false);

            if (chunk != null)
            {
                BlockPos blockPos = BlockPos.of(packedQueue.right());
                int x = blockPos.getX() & 15;
                int y = blockPos.getY() & 15;
                int z = blockPos.getZ() & 15;

                NullableAction.attempt(chunk.getSkyLightSources(), skyLightSources -> skyLightSources.update(chunk, x, y, z));

                chunk.getLevel().getLightEngine().checkBlock(blockPos);
            }
            else if (renderer.isSectionCompiled(chunkPos.getWorldPosition()))
                PACKED_CHUNK_BLOCK_QUEUE.add(packedQueue);
        }
    }

    /**
     * Gets the greatest light value surrounding a block without considering emissive rendering.
     *
     * @param level    A {@link BlockAndTintGetter} instance.
     * @param blockPos The {@link BlockPos} to inspect.
     * @return The largest light value around the block.
     */
    public static int getNonEmissiveLightColor(BlockAndTintGetter level, BlockPos blockPos)
    {
        int skyLight = level.getBrightness(LightLayer.SKY, blockPos);
        int blockLight = level.getBrightness(LightLayer.BLOCK, blockPos);

        return skyLight << 20 | blockLight << 4;
    }

    /**
     * Gets the greatest light value surrounding a water block.
     *
     * @param level    A {@link BlockAndTintGetter} instance.
     * @param blockPos The {@link BlockPos} of the water block.
     * @return The largest light value around the water block.
     */
    public static int getWaterLight(BlockAndTintGetter level, BlockPos blockPos)
    {
        int center = getNonEmissiveLightColor(level, blockPos);
        int above = getNonEmissiveLightColor(level, blockPos.above());
        int below = getNonEmissiveLightColor(level, blockPos.below());
        int north = getNonEmissiveLightColor(level, blockPos.north());
        int south = getNonEmissiveLightColor(level, blockPos.south());
        int west = getNonEmissiveLightColor(level, blockPos.west());
        int east = getNonEmissiveLightColor(level, blockPos.east());

        return MathUtil.getLargest(center, above, below, north, south, west, east);
    }

    /**
     * Get a skylight value based on the time of day.
     *
     * @param level A {@link Level} instance.
     * @return A skylight value based on the time of day.
     */
    public static int getSkyLightFromTime(Level level)
    {
        if (level.dimensionType().hasCeiling() || !level.dimensionType().hasSkyLight())
            return 0;

        float time = level.getTimeOfDay(1.0F) * ((float) Math.PI * 2.0F);
        int skyLight = 15;

        if (MathUtil.isInRange(time, 1.8235918F, 4.459594F))
            skyLight = 4;
        else if (MathUtil.isInRange(time, 4.459884F, 4.5061855F) || MathUtil.isInRange(time, 1.7769997F, 1.8233016F))
            skyLight = 5;
        else if (MathUtil.isInRange(time, 4.5064745F, 4.55252F) || MathUtil.isInRange(time, 1.7306658F, 1.7767112F))
            skyLight = 6;
        else if (MathUtil.isInRange(time, 4.552807F, 4.5983024F) || MathUtil.isInRange(time, 1.684883F, 1.7303787F))
            skyLight = 7;
        else if (MathUtil.isInRange(time, 4.598588F, 4.6440983F) || MathUtil.isInRange(time, 1.6390872F, 1.6845976F))
            skyLight = 8;
        else if (MathUtil.isInRange(time, 4.6443815F, 4.689612F) || MathUtil.isInRange(time, 1.5938551F, 1.6388037F))
            skyLight = 9;
        else if (MathUtil.isInRange(time, 4.6898937F, 4.735117F) || MathUtil.isInRange(time, 1.548349F, 1.5935733F))
            skyLight = 10;
        else if (MathUtil.isInRange(time, 4.7353964F, 4.7805977F) || MathUtil.isInRange(time, 1.5028657F, 1.548069F))
            skyLight = 11;
        else if (MathUtil.isInRange(time, 4.780876F, 4.826043F) || MathUtil.isInRange(time, 1.4571424F, 1.5025874F))
            skyLight = 12;
        else if (MathUtil.isInRange(time, 4.826319F, 4.8719864F) || MathUtil.isInRange(time, 1.4111987F, 1.4568661F))
            skyLight = 13;
        else if (MathUtil.isInRange(time, 4.8722606F, 4.9184027F) || MathUtil.isInRange(time, 1.3650552F, 1.4109247F))
            skyLight = 14;

        return skyLight;
    }

    /**
     * Get a skylight value based on the current level weather.
     *
     * @param level A {@link Level} instance.
     * @return A skylight value based on the current level weather.
     */
    public static int getSkyLightFromWeather(Level level)
    {
        float partialTick = PartialTick.get();
        float rain = level.getRainLevel(partialTick);
        float thunder = level.getThunderLevel(partialTick);

        int rainDiff = 0;
        int thunderDiff = 0;

        if (rain >= 0.3F)
            rainDiff = 1;

        if (rain >= 0.6F)
            rainDiff = 2;

        if (rain >= 0.9F)
            rainDiff = 3;

        if (thunder >= 0.8F)
            thunderDiff = 5;

        return Math.max(rainDiff, thunderDiff);
    }

    /**
     * Get the correct light value to use when combining skylight and block light to simulate old lighting.
     *
     * @param skyLight   The skylight from a data layer.
     * @param blockLight The block light from a data layer.
     * @return A combined skylight and block light value to use.
     */
    public static int getCombinedLight(int skyLight, int blockLight)
    {
        Level level = Minecraft.getInstance().level;

        if (level == null || skyLight <= 0)
            return 0;

        int maxLightLevel = level.getMaxLightLevel();
        int lightFromTime = TIME_SKYLIGHT.get();
        int weatherDiff = WEATHER_SKYLIGHT.get();
        int minSkyLight = Math.max(0, (level.dimensionType().hasFixedTime() ? lightFromTime : maxLightLevel) - 11);
        int minLight = skyLight >= maxLightLevel ? minSkyLight : 0;
        int maxLight = Math.max(blockLight, CandyTweak.MAX_BLOCK_LIGHT.get());
        int oldSkyLight = lightFromTime - weatherDiff;
        int offsetSkyLight = maxLightLevel - skyLight;

        if (skyLight != maxLightLevel && oldSkyLight <= minSkyLight)
            oldSkyLight += weatherDiff;

        return Mth.clamp(Math.max(oldSkyLight - offsetSkyLight, blockLight), minLight, maxLight);
    }

    /**
     * Get a skylight value based on the classic light rendering logic.
     *
     * @param level    The {@link ClientLevel} instance.
     * @param skyLight The original skylight value.
     * @param blockPos The {@link BlockPos} to look above.
     * @return A classic skylight value.
     */
    public static int getClassicLight(int skyLight, ClientLevel level, BlockPos blockPos)
    {
        BlockPos abovePos = blockPos.above();

        if (!level.dimensionType().hasSkyLight())
            return 0;

        if (skyLight >= level.getMaxLightLevel() || skyLight <= 0)
            return skyLight;

        while (abovePos.getY() < level.getMaxBuildHeight())
        {
            BlockState blockState = level.getBlockState(abovePos);
            boolean isWater = blockState.is(Blocks.WATER);
            boolean isSolid = level.getBlockState(abovePos).getLightBlock(level, abovePos) >= level.getMaxLightLevel();

            if (isWater || isSolid)
                return 0;

            abovePos = abovePos.above();
        }

        return level.getMaxLightLevel();
    }

    /**
     * Find block states within a given chunk that match the given predicate and perform an operation and the found
     * block states and their block position.
     *
     * @param chunk     The {@link LevelChunk} to search.
     * @param predicate The {@link Predicate} that accepts a {@link BlockState}.
     * @param output    A {@link BiConsumer} that accepts a {@link BlockPos} and {@link BlockState} that matched the
     *                  given predicate.
     */
    public static void findBlocks(LevelChunk chunk, Predicate<BlockState> predicate, BiConsumer<BlockPos, BlockState> output)
    {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = chunk.getMinSection(); i < chunk.getMaxSection(); ++i)
        {
            LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(i));
            BlockPos blockPos = SectionPos.of(chunk.getPos(), i).origin();

            for (int y = 0; y < 16; ++y)
            {
                for (int z = 0; z < 16; ++z)
                {
                    for (int x = 0; x < 16; ++x)
                    {
                        BlockState blockState = section.getBlockState(x, y, z);

                        if (predicate.test(blockState))
                            output.accept(mutablePos.setWithOffset(blockPos, x, y, z), blockState);
                    }
                }
            }
        }
    }

    /**
     * Applies relighting to the given chunk based on tweak context.
     *
     * @param chunk      The {@link LevelChunk} to relight.
     * @param allChanged A boolean-like byte (where 1 is true and all other values are false) that indicates whether the
     *                   chunk needs full relighting.
     */
    public static void relightChunk(@Nullable final LevelChunk chunk, final byte allChanged)
    {
        if (chunk == null)
            return;

        final boolean isChestLightBlocked = CandyTweak.CHEST_LIGHT_BLOCK.get();
        final boolean isWaterDarker = CandyTweak.OLD_WATER_LIGHTING.get();

        if (!isChestLightBlocked && !isWaterDarker && allChanged != 1)
            return;

        CompletableFuture.runAsync(() -> findBlocks(chunk, blockState -> {
            if (allChanged == 1)
                return BlockUtil.isWaterLike(blockState) || BlockUtil.isChestLike(blockState);

            boolean relightChest = isChestLightBlocked && ChestHelper.isOld(blockState);
            boolean relightWater = isWaterDarker && BlockUtil.isWaterLike(blockState);

            return relightChest || relightWater;
        }, (blockPos, blockState) -> {
            final long packedChunk = chunk.getPos().toLong();
            final long packedBlock = blockPos.asLong();

            PACKED_CHUNK_BLOCK_QUEUE.add(new Pair<>(packedChunk, packedBlock));
        }), Util.backgroundExecutor());
    }
}
