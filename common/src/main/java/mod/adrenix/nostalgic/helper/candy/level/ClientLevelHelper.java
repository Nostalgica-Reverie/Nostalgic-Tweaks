package mod.adrenix.nostalgic.helper.candy.level;

import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.common.data.NumberHolder;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;

/**
 * This utility class is used only by the client.
 */
public abstract class ClientLevelHelper
{
    /* Fields */

    /**
     * Initialized as non-number so that the biome the player spawns in initializes the temperature value.
     */
    private static final NumberHolder<Float> BIOME_TEMPERATURE = NumberHolder.create(Float.NaN);

    /* Methods */

    /**
     * Get the biome temperature the player is in.
     *
     * @return A smoothly transitioned biome temperature value.
     */
    public static float getBiomeTemperature()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();

        if (player == null)
            return 0.0F;

        float temperature = player.level().getBiome(camera.getBlockPosition()).value().getBaseTemperature();

        if (Float.isNaN(BIOME_TEMPERATURE.get()))
            BIOME_TEMPERATURE.set(temperature);
        else
            BIOME_TEMPERATURE.set(MathUtil.moveTowards(BIOME_TEMPERATURE.get(), temperature, 0.00001F));

        return BIOME_TEMPERATURE.get();
    }

    /**
     * Get an RGB float array that indicates how much a color channel should be influenced by the time of day or weather
     * patterns. None of the values returned are clamped. The values returned are influenced based on a custom color.
     *
     * @return An RGB array.
     */
    public static float[] getStandardEnvironmentInfluence()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return new float[] { 0.0F, 0.0F, 0.0F };

        float partialTick = minecraft.getFrameTime();
        float timeOfDay = level.getTimeOfDay(partialTick);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float rain = 1.0F - (CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : level.getRainLevel(partialTick)) + 0.4F;
        float thunder = 1.0F - (CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : level.getThunderLevel(partialTick)) + 0.2F;
        float weather = Mth.clamp(Math.min(rain, thunder), 0.0F, 1.0F);

        float r = boundedTime * weather;
        float g = boundedTime * weather;
        float b = boundedTime * weather;

        return new float[] { r, g, b };
    }

    /**
     * Get an entity's current skylight value.
     *
     * @param entity The {@link Entity} to get context from.
     * @return The current skylight value above the given entity.
     */
    public static int getSkyLight(Entity entity)
    {
        return entity.level().getBrightness(LightLayer.SKY, entity.blockPosition().above());
    }

    /**
     * Get the greatest brightness value from block light or skylight.
     *
     * @param entity The {@link Entity} to get context from.
     * @return The greatest brightness value around the given entity.
     */
    public static int getMaxLight(Entity entity)
    {
        return Mth.clamp(entity.level().getMaxLocalRawBrightness(entity.blockPosition().above()), 0, 15);
    }
}
