package mod.adrenix.nostalgic.helper.gameplay;

import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

/**
 * This utility class is used by both the client and server.
 */
public abstract class NightmareHelper
{
    /**
     * Check if the player is about to have a nightmare.
     *
     * @param player The sleeping {@link ServerPlayer} instance.
     */
    public static void tick(ServerPlayer player)
    {
        boolean isPeaceful = player.level().getDifficulty() == Difficulty.PEACEFUL;

        if (isPeaceful || player.isCreative() || player.getSleepingPos().isEmpty() || !player.isSleepingLongEnough())
            return;

        // noinspection deprecation
        player.getSleepingPos().filter(player.level()::hasChunkAt).ifPresent(bedPos -> {
            BlockState blockState = player.level().getBlockState(bedPos);

            if (blockState.getBlock() instanceof BedBlock)
                checkForNightmare(player, bedPos, blockState);
        });
    }

    /**
     * Perform old beta "nightmare" mechanic by checking if a monster can path find to the player. If so, wake the
     * player up and spawn the monster next to the bed.
     *
     * @param player     The {@link ServerPlayer} instance.
     * @param bedPos     The {@link BlockPos} of the bed.
     * @param blockState The {@link BlockState} of the bed.
     */
    private static void checkForNightmare(ServerPlayer player, BlockPos bedPos, BlockState blockState)
    {
        RandomSource randomSource = player.getRandom();
        ServerLevel serverLevel = player.serverLevel();

        for (BlockPos blockPos : randomSpawnPointsAroundBed(randomSource, player.getOnPos()))
        {
            EntityType<? extends Monster> entityType = switch (MathUtil.randomInt(0, 2))
            {
                case 1 -> EntityType.SKELETON;
                case 2 -> EntityType.SPIDER;
                default -> EntityType.ZOMBIE;
            };

            if (Monster.checkMonsterSpawnRules(entityType, serverLevel, MobSpawnType.NATURAL, blockPos, randomSource))
            {
                Mob monster = entityType.create(serverLevel);

                if (monster != null)
                {
                    monster.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    monster.setYRot(randomSource.nextFloat() * 360.0F);
                    monster.setXRot(0.0F);
                    monster.setOnGround(true);

                    if (isValidPathToBed(monster, bedPos))
                    {
                        setupMonsterAndWakePlayer(monster, player, bedPos, blockState);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Get 20 random {@link BlockPos} points around the bed block's given position in a 32x16x32 area.
     *
     * @param randomSource A {@link RandomSource} instance.
     * @param center       The {@link BlockPos} of the player's bed.
     * @return An {@link Iterable} of {@link BlockPos}.
     */
    private static Iterable<BlockPos> randomSpawnPointsAroundBed(RandomSource randomSource, BlockPos center)
    {
        int minX = center.getX() - 16;
        int minY = center.getY() - 8;
        int minZ = center.getZ() - 16;
        int maxX = center.getX() + 16;
        int maxY = center.getY() + 8;
        int maxZ = center.getZ() + 16;

        return BlockPos.randomBetweenClosed(randomSource, 20, minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Find where the game wants to put the entity after it leaves the bed.
     *
     * @param entity    The {@link LivingEntity} to simulate exiting a bed.
     * @param bedPos    The {@link BlockPos} of the bed.
     * @param direction The {@link Direction} of the bed block state.
     * @return The {@link Vec3} entity stand up position calculated by the game.
     */
    private static Vec3 findSpawnPointAroundBed(LivingEntity entity, BlockPos bedPos, Direction direction)
    {
        return BedBlock.findStandUpPosition(entity.getType(), entity.level(), bedPos, direction, entity.getYRot())
            .orElseGet(() -> {
                BlockPos abovePos = bedPos.above();
                return new Vec3((double) abovePos.getX() + 0.5D, (double) abovePos.getY() + 0.1D, (double) abovePos.getZ() + 0.5D);
            });
    }

    /**
     * Check if a valid path can be made to the player. The accuracy of the path needs to be within a block, so the path
     * end node needs to be exact and not "close enough." If the distance to the bed is less than 2 blocks, then a mob
     * was successful in making a direct path to it.
     *
     * @param monster The {@link Mob} trying to make a path to a bed block.
     * @param bedPos  The {@link BlockPos} of the bed block.
     * @return Whether a mob is able to make a direct path to the player's bed block.
     */
    private static boolean isValidPathToBed(Mob monster, BlockPos bedPos)
    {
        Path path = monster.getNavigation().createPath(bedPos, 0);

        if (path == null || path.getEndNode() == null)
            return false;

        return path.getEndNode().distanceTo(bedPos) < 2.0F;
    }

    /**
     * Finalize the monster spawn context and then wake up the player. This will attempt to spawn the monster opposite
     * to that of where the player wakes up. There is a rare chance that a spider jockey can spawn if the monster that
     * is waking up the player is a spider.
     *
     * @param monster    The {@link Mob} monster waking up the player.
     * @param player     The {@link ServerPlayer} having a nightmare.
     * @param bedPos     The {@link BlockPos} of the player's bed block.
     * @param blockState The {@link BlockState} of the player's bed block.
     */
    private static void setupMonsterAndWakePlayer(Mob monster, ServerPlayer player, BlockPos bedPos, BlockState blockState)
    {
        Direction direction = blockState.getValue(BedBlock.FACING);
        RandomSource randomSource = player.getRandom();
        ServerLevel serverLevel = player.serverLevel();

        Vec3 monsterPos = findSpawnPointAroundBed(monster, bedPos, direction);
        Vec3 playerPos = findSpawnPointAroundBed(player, bedPos, direction);

        for (int i = 0; i < 100; i++)
        {
            if (monsterPos.distanceTo(playerPos) <= 0.5D)
            {
                monster.setYRot(randomSource.nextFloat() * 360.0F);
                monsterPos = findSpawnPointAroundBed(monster, bedPos, direction);
            }
            else
                break;
        }

        if (monsterPos.distanceTo(playerPos) <= 0.5D)
        {
            BlockPos abovePos = bedPos.above();
            monsterPos = new Vec3((double) abovePos.getX() + 0.5D, (double) abovePos.getY() + 0.1D, (double) abovePos.getZ() + 0.5D);
        }

        monster.setPos(monsterPos.x, monsterPos.y, monsterPos.z);

        if (monster instanceof Skeleton)
            monster.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

        serverLevel.addFreshEntity(monster);

        if (monster instanceof Spider spider && randomSource.nextInt(100) == 0)
        {
            Skeleton skeleton = EntityType.SKELETON.create(serverLevel);

            if (skeleton != null)
            {
                skeleton.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                skeleton.setPos(monsterPos.x, monsterPos.y, monsterPos.z);
                skeleton.setYRot(monster.getYRot());
                serverLevel.addFreshEntity(skeleton);

                skeleton.startRiding(spider);
            }
        }

        player.stopSleepInBed(true, true);
        player.displayClientMessage(Lang.Block.BED_NIGHTMARE.get(), false);

        monster.playAmbientSound();
    }
}
