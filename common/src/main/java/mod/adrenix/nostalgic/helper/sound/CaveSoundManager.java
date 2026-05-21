package mod.adrenix.nostalgic.helper.sound;

import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class CaveSoundManager
{
    /* Fields */

    private int tickCountdown;
    private final RandomSource randomSource;
    private final SoundManager soundManager;
    private final LocalPlayer player;
    private final ClientLevel level;

    /* Constructor */

    /**
     * Create a new {@link CaveSoundManager} instance.
     *
     * @param player       The {@link LocalPlayer} instance to get context with.
     * @param soundManager The {@link SoundManager} instance to use.
     */
    public CaveSoundManager(LocalPlayer player, SoundManager soundManager)
    {
        this.player = player;
        this.level = player.clientLevel;
        this.randomSource = player.clientLevel.getRandom();
        this.soundManager = soundManager;
        this.tickCountdown = this.randomSource.nextInt(12000);
    }

    /* Methods */

    /**
     * Get an old style cave sound.
     *
     * @param randomSource A {@link RandomSource} instance.
     * @param x            The x-coordinate of the sound.
     * @param y            The y-coordinate of the sound.
     * @param z            The z-coordinate of the sound.
     * @return A new old style cave sound {@link SimpleSoundInstance} instance at the given coordinates.
     */
    public static SimpleSoundInstance getSound(RandomSource randomSource, double x, double y, double z)
    {
        return new SimpleSoundInstance(LocateResource.mod("cave"), SoundSource.AMBIENT, 0.7F, 0.8F + randomSource.nextFloat() * 0.2F, randomSource, false, 0, SoundInstance.Attenuation.LINEAR, x, y, z, false);
    }

    /**
     * Tick and play a cave ambient sound near the player if the given block pos matches the required context.
     *
     * @param blockPos The random {@link BlockPos} to check for correct context.
     */
    public void tickAndPlayIfPossible(BlockPos blockPos)
    {
        if (this.level.dimensionType().hasCeiling() || !this.level.dimensionType().hasSkyLight())
            return;

        if (this.tickCountdown > 0)
            this.tickCountdown--;

        if (this.tickCountdown != 0)
            return;

        if (this.isCaveLike(blockPos))
        {
            if (!BlockPos.betweenClosedStream(new BoundingBox(blockPos).inflatedBy(1)).allMatch(this::isCaveLike))
                return;

            AmbientMoodSettings ambientMoodSettings = AmbientMoodSettings.LEGACY_CAVE_SETTINGS;

            double blockX = (double) blockPos.getX() + 0.5D;
            double blockY = (double) blockPos.getY() + 0.5D;
            double blockZ = (double) blockPos.getZ() + 0.5D;
            double dx = blockX - this.player.getX();
            double dy = blockY - this.player.getEyeY();
            double dz = blockZ - this.player.getZ();
            double normal = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double offset = normal + ambientMoodSettings.getSoundPositionOffset();
            double x = this.player.getX() + dx / normal * offset;
            double y = this.player.getEyeY() + dy / normal * offset;
            double z = this.player.getZ() + dz / normal * offset;

            this.soundManager.play(getSound(this.randomSource, x, y, z));
            this.tickCountdown = this.randomSource.nextInt(12000) + 6000;
        }
    }

    /**
     * Check if at the given block position there exists an air block and no light from the sky or blocks.
     *
     * @param blockPos A {@link BlockPos} instance.
     * @return Whether the environment at the given position seems cave like.
     */
    private boolean isCaveLike(BlockPos blockPos)
    {
        if (blockPos.getY() <= this.level.getMinBuildHeight())
            return false;

        if (this.level.getBlockState(blockPos).getBlock() instanceof AirBlock)
        {
            int blockLight = this.level.getBrightness(LightLayer.BLOCK, blockPos);
            int skyLight = this.level.getBrightness(LightLayer.SKY, blockPos);

            return blockLight <= 0 && skyLight <= 0;
        }

        return false;
    }
}
