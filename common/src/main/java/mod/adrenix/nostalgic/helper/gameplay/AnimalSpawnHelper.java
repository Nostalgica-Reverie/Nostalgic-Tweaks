package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashSet;
import java.util.Optional;

public abstract class AnimalSpawnHelper
{
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
     * Performs old animal spawning logic each tick in each server level chunk.
     *
     * @param level           The {@link ServerLevel} instance.
     * @param chunk           The {@link LevelChunk} instance.
     * @param spawnFriendlies Whether the server level is spawning friendly creatures.
     */
    public static void tickChunk(ServerLevel level, LevelChunk chunk, boolean spawnFriendlies)
    {
        if (!spawnFriendlies)
            return;

        HashSet<ChunkPos> chunksToPoll = new HashSet<>();

        for (int i = 0; i < level.players().size(); i++)
        {
            Player player = level.players().get(i);
            int dx = Mth.floor(player.getX() / 16.0D);
            int dz = Mth.floor(player.getZ() / 16.0D);

            for (int x = -8; x <= 8; ++x)
            {
                for (int z = -8; z <= 8; ++z)
                    chunksToPoll.add(new ChunkPos(x + dx, z + dz));
            }
        }

        int numberOfAnimals = 0;

        for (Entity entity : level.getAllEntities())
        {
            if (entity instanceof Animal)
                numberOfAnimals++;
        }

        if (numberOfAnimals > GameplayTweak.ANIMAL_SPAWN_CAP.get() * chunksToPoll.size() / 256)
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

                if (SpawnPlacements.isSpawnPositionOk(spawnerData.get().type, level, blockPos))
                {
                    float dx = (float) x + 0.5F;
                    float dy = (float) y;
                    float dz = (float) z + 0.5F;

                    if (level.getNearestPlayer(dx, dy, dz, 24.0, true) == null)
                    {
                        BlockPos spawnPos = level.getLevelData().getSpawnPos();

                        float ox = dx - (float) spawnPos.getX();
                        float oy = dy - (float) spawnPos.getY();
                        float oz = dz - (float) spawnPos.getZ();
                        float distance = ox * ox + oy * oy + oz * oz;

                        if (distance >= 576.0F)
                        {
                            Mob mob = null;

                            try
                            {
                                Entity entity = spawnerData.get().type.create(level);

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

                            if (isValidSpawnPositionForMob(level, blockPos, mob))
                            {
                                spawnGroupData = mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.NATURAL, spawnGroupData);
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
        int y = Mth.randomBetweenInclusive(level.random, level.getMinBuildHeight(), height);

        return new BlockPos(x, y, z);
    }

    /**
     * Checks if the given position and mob is valid to be spawned using old animal spawn logic.
     *
     * @param level    The {@link ServerLevel} instance.
     * @param blockPos The {@link BlockPos} to spawn the animal at.
     * @param mob      The {@link Mob} instance for the animal.
     * @return Whether the given position is valid to spawn the given mob.
     */
    public static boolean isValidSpawnPositionForMob(ServerLevel level, BlockPos blockPos, Mob mob)
    {
        if (!isInList(mob.getType()))
            return false;

        if (!mob.getType().canSummon())
            return false;

        if (!SpawnPlacements.checkSpawnRules(mob.getType(), level, MobSpawnType.NATURAL, blockPos, level.random))
            return false;

        if (level.getMaxLocalRawBrightness(blockPos) <= 8)
            return false;

        return level.noCollision(mob.getType()
            .getSpawnAABB((double) blockPos.getX() + 0.5D, blockPos.getY(), (double) blockPos.getZ() + 0.5D));
    }
}
