package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashSet;
import java.util.Optional;

public abstract class AnimalSpawnHelper
{
    /* Static */

    /**
     * Keeps a record of the chunks to poll every tick. When the server ticks all the loaded chunks, the
     * {@link #tickChunk(LevelChunk, ServerLevel, boolean)} is ran. This method needs to know how many chunks to poll
     * and how many old animals are spawned within the level.
     */
    private static final HashSet<ChunkPos> CHUNKS_TO_POLL = new HashSet<>();

    /**
     * Tracks the number of animals currently spawned in the level. This is necessary to ensure the animal spawn cap is
     * not exceeded.
     */
    private static final IntegerHolder NUMBER_OF_ANIMALS = IntegerHolder.create(0);

    /* Methods */

    /**
     * Check if the given entity type is a valid entry within the old animal spawn list tweak.
     *
     * @param entityType The {@link EntityType} to check.
     * @return Whether the given entity type is available for old animal spawning.
     */
    public static boolean isInList(EntityType<?> entityType)
    {
        return GameplayTweak.OLD_ANIMAL_SPAWN_LIST.get().contains(EntityType.getKey(entityType).toString());
    }

    /**
     * Check if the given ageable mob should be persistent. This will yield true when old animal spawning, keep baby
     * animals while old spawning, and the entity type is within the old spawn whitelist.
     *
     * @param ageableMob The {@link AgeableMob} to check.
     * @return Whether the ageable mob should be persistent.
     */
    public static boolean isPersistent(AgeableMob ageableMob)
    {
        return GameplayTweak.OLD_ANIMAL_SPAWNING.get() && GameplayTweak.KEEP_BABY_ANIMAL_WHILE_OLD_SPAWN.get() && isInList(ageableMob.getType());
    }

    /**
     * Generates a cache of information needed for old animal spawning logic before the server ticks over all loaded
     * chunks.
     *
     * @param level           The {@link ServerLevel} instance.
     * @param spawnFriendlies Whether the server level is spawning friendly creatures.
     */
    public static void tickLevel(ServerLevel level, boolean spawnFriendlies)
    {
        if (!spawnFriendlies)
            return;

        CHUNKS_TO_POLL.clear();
        NUMBER_OF_ANIMALS.set(0);

        for (int i = 0; i < level.players().size(); i++)
        {
            Player player = level.players().get(i);
            int dx = Mth.floor(player.getX() / 16.0D);
            int dz = Mth.floor(player.getZ() / 16.0D);

            for (int x = -8; x <= 8; ++x)
            {
                for (int z = -8; z <= 8; ++z)
                    CHUNKS_TO_POLL.add(new ChunkPos(x + dx, z + dz));
            }
        }

        for (Entity entity : level.getAllEntities())
        {
            if (entity instanceof Animal animal && isInList(animal.getType()) && !animal.isPersistenceRequired())
                NUMBER_OF_ANIMALS.increment();
        }
    }

    /**
     * Performs old animal spawning logic each tick in each server level chunk.
     *
     * @param chunk           The {@link LevelChunk} instance.
     * @param level           The {@link ServerLevel} instance.
     * @param spawnFriendlies Whether the server level is spawning friendly creatures.
     */
    public static void tickChunk(LevelChunk chunk, ServerLevel level, boolean spawnFriendlies)
    {
        if (!spawnFriendlies || NUMBER_OF_ANIMALS.get() > GameplayTweak.ANIMAL_SPAWN_CAP.get() * CHUNKS_TO_POLL.size() / 256)
            return;

        BlockPos blockPos = getRandomPosWithin(level, chunk);

        WeightedRandomList<MobSpawnSettings.SpawnerData> creatures = level.getBiomeManager()
            .getBiome(blockPos)
            .value()
            .getMobSettings()
            .getMobs(MobCategory.CREATURE);

        int spawnCount = 0;

        for (int i = 0; i < 3; i++)
        {
            SpawnGroupData spawnGroupData = null;
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();

            for (int j = 0; j < 4; j++)
            {
                x += level.random.nextInt(6) - level.random.nextInt(6);
                y += level.random.nextInt(1) - level.random.nextInt(1);
                z += level.random.nextInt(6) - level.random.nextInt(6);

                Optional<MobSpawnSettings.SpawnerData> spawnerData = creatures.getRandom(level.random);

                if (spawnerData.isEmpty())
                    break;

                EntityType<?> entityType = spawnerData.get().type;

                if (GameplayTweak.IGNORE_ANIMAL_BIOME_RESTRICTIONS.get() && level.dimension().equals(Level.OVERWORLD))
                {
                    String[] spawnList = GameplayTweak.OLD_ANIMAL_SPAWN_LIST.get().stream().toArray(String[]::new);
                    int randomIndex = level.random.nextInt(spawnList.length);

                    Optional<EntityType<?>> maybeEntity = BuiltInRegistries.ENTITY_TYPE.getOptional(LocateResource.game(spawnList[randomIndex]));

                    if (maybeEntity.isEmpty())
                        break;

                    entityType = maybeEntity.get();
                }

                if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.getPlacementType(entityType), level, blockPos, entityType))
                {
                    float dx = (float) x + 0.5F;
                    float dy = (float) y;
                    float dz = (float) z + 0.5F;

                    if (level.getNearestPlayer(dx, dy, dz, 24.0, false) == null)
                    {
                        float ox = dx - (float) level.getLevelData().getXSpawn();
                        float oy = dy - (float) level.getLevelData().getYSpawn();
                        float oz = dz - (float) level.getLevelData().getZSpawn();
                        float distance = ox * ox + oy * oy + oz * oz;

                        if (distance >= 576.0F)
                        {
                            Mob mob = null;

                            try
                            {
                                Entity entity = entityType.create(level);

                                if (entity instanceof Mob)
                                    mob = (Mob) entity;
                            }
                            catch (Exception exception)
                            {
                                NostalgicTweaks.LOGGER.warn("Failed to create mob\n%s", exception);
                            }

                            if (mob == null)
                                return;

                            mob.moveTo(dx, dy, dz, level.random.nextFloat() * 360.0F, 0.0F);

                            if (isValidSpawnPositionForMob(level, mob))
                            {
                                spawnGroupData = mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.NATURAL, spawnGroupData, null);
                                ++spawnCount;

                                level.addFreshEntityWithPassengers(mob);

                                if (spawnCount >= mob.getMaxSpawnClusterSize())
                                    return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Get a random block position within the given chunk.
     *
     * @param level The {@link ServerLevel} instance.
     * @param chunk The {@link LevelChunk} to get a random position within.
     * @return A random {@link BlockPos} within the given chunk.
     */
    public static BlockPos getRandomPosWithin(ServerLevel level, LevelChunk chunk)
    {
        ChunkPos chunkPos = chunk.getPos();
        int x = chunkPos.getMinBlockX() + level.random.nextInt(16);
        int z = chunkPos.getMinBlockZ() + level.random.nextInt(16);
        int height = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) + 1;
        int y = Mth.randomBetweenInclusive(level.random, level.getMinBuildHeight() + 1, height);

        return new BlockPos(x, y, z);
    }

    /**
     * Checks if the given position and mob is valid to be spawned using old animal spawn logic.
     *
     * @param level The {@link ServerLevel} instance.
     * @param mob   The {@link Mob} instance for the animal.
     * @return Whether the given position is valid to spawn the given mob.
     */
    public static boolean isValidSpawnPositionForMob(ServerLevel level, Mob mob)
    {
        BlockPos blockPos = mob.blockPosition();

        if (level.getMaxLocalRawBrightness(blockPos) <= 8)
            return false;

        EntityType<?> mobType = mob.getType();

        if (!isInList(mobType) || !mobType.canSummon())
            return false;

        if (!SpawnPlacements.checkSpawnRules(mobType, level, MobSpawnType.NATURAL, blockPos, level.random))
            return false;

        return level.noCollision(mobType.getAABB((double) blockPos.getX() + 0.5D, blockPos.getY(), (double) blockPos.getZ() + 0.5D));
    }
}
