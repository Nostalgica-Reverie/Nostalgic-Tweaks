package mod.adrenix.nostalgic.helper.candy.level.fog;

import com.mojang.blaze3d.shaders.FogShape;
import mod.adrenix.nostalgic.helper.candy.level.ClientLevelHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.FogColor;
import mod.adrenix.nostalgic.tweak.enums.WorldFog;
import mod.adrenix.nostalgic.util.client.CameraUtil;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.timer.LerpTimer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This utility class is used only by the client.
 */
public abstract class OverworldFogRenderer
{
    /* Fields */

    private static final ArrayList<LerpTimer> LERP_TIMERS = new ArrayList<>();
    private static final LerpTimer DARKNESS = makeTimer(1L, TimeUnit.SECONDS);
    private static final LerpTimer TERRAIN_START = makeTimer(500L, TimeUnit.MILLISECONDS);
    private static final LerpTimer TERRAIN_END = makeTimer(500L, TimeUnit.MILLISECONDS);
    private static final LerpTimer SKY_START = makeTimer(500L, TimeUnit.MILLISECONDS);
    private static final LerpTimer SKY_END = makeTimer(500L, TimeUnit.MILLISECONDS);
    private static final LerpTimer FOG_RED = makeTimer(1L, TimeUnit.SECONDS);
    private static final LerpTimer FOG_GREEN = makeTimer(1L, TimeUnit.SECONDS);
    private static final LerpTimer FOG_BLUE = makeTimer(1L, TimeUnit.SECONDS);

    /* Methods */

    /**
     * Create and track a new animation timer.
     *
     * @param duration The duration of the animation.
     * @param timeUnit The {@link TimeUnit} value.
     * @return A new {@link LerpTimer} instance.
     */
    private static LerpTimer makeTimer(long duration, TimeUnit timeUnit)
    {
        LerpTimer timer = LerpTimer.create(duration, timeUnit);
        LERP_TIMERS.add(timer);

        return timer;
    }

    /**
     * Resets overworld fog animation timers.
     */
    public static void reset()
    {
        LERP_TIMERS.forEach(LerpTimer::clear);
    }

    /**
     * Checks if the fog is currently being overridden by the game, such as the camera being in a fluid or the camera
     * entity having the blindness effect.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether the game is overriding the level fog.
     */
    public static boolean isGameOverride(Camera camera)
    {
        return CameraUtil.isInFluid(camera) || CameraUtil.isBlind(camera) || GameUtil.MOB_EFFECT_ACTIVE.get();
    }

    /**
     * Get the old far plane distance for fog rendering.
     *
     * @param worldFog The {@link WorldFog} value.
     * @return The old far plane distance value.
     */
    public static int getFarPlaneDistance(WorldFog worldFog)
    {
        boolean isOldFog = worldFog == WorldFog.CLASSIC || worldFog == WorldFog.ALPHA_R164;
        int renderDistance = GameUtil.getRenderDistance();

        return (renderDistance * 16) * (!isOldFog || renderDistance > 28 ? 1 : 2);
    }

    /**
     * Set the game's terrain fog starting and ending points.
     *
     * @param fogMode  The {@link FogRenderer.FogMode} value.
     * @param worldFog The {@link WorldFog} value.
     * @param fogStart A {@link Consumer} that accepts a float where the fog starts.
     * @param fogEnd   A {@link Consumer} that accepts a float where the fog ends.
     */
    private static void setTerrain(FogRenderer.FogMode fogMode, WorldFog worldFog, Consumer<Float> fogStart, Consumer<Float> fogEnd)
    {
        if (fogMode != FogRenderer.FogMode.FOG_TERRAIN)
            return;

        boolean isOldFog = worldFog == WorldFog.CLASSIC || worldFog == WorldFog.ALPHA_R164;
        int farPlaneDistance = getFarPlaneDistance(worldFog);
        float density = !isOldFog ? 0.8F : switch (GameUtil.getRenderDistance())
        {
            case 9, 10, 11 -> 0.7F;
            case 6, 7, 8 -> 0.65F;
            case 4, 5 -> 0.55F;
            case 2, 3 -> 0.48F;
            default -> 0.8F;
        };

        fogStart.accept(0.0F);
        fogEnd.accept(farPlaneDistance * density);
    }

    /**
     * Set the game's horizon fog starting and ending points.
     *
     * @param fogMode  The {@link FogRenderer.FogMode} value.
     * @param worldFog The {@link WorldFog} value.
     * @param fogStart A {@link Consumer} that accepts a float where the fog starts.
     * @param fogEnd   A {@link Consumer} that accepts a float where the fog ends.
     */
    private static void setHorizon(FogRenderer.FogMode fogMode, WorldFog worldFog, Consumer<Float> fogStart, Consumer<Float> fogEnd)
    {
        if (fogMode != FogRenderer.FogMode.FOG_SKY)
            return;

        int farPlaneDistance = getFarPlaneDistance(worldFog);

        if (worldFog == WorldFog.CLASSIC)
        {
            float density = switch (GameUtil.getRenderDistance())
            {
                case 2, 3, 4, 5 -> 0.2F;
                case 6, 7, 8, 9 -> 0.4F;
                default -> 0.8F;
            };

            fogStart.accept(0.0F);
            fogEnd.accept(farPlaneDistance * density);

            return;
        }

        if (worldFog == WorldFog.ALPHA_R164)
        {
            float density = switch (GameUtil.getRenderDistance())
            {
                case 6, 7, 8, 9 -> 0.65F;
                case 4, 5 -> 0.5F;
                case 2, 3 -> 0.2F;
                default -> 0.8F;
            };

            fogStart.accept(0.0F);
            fogEnd.accept(farPlaneDistance * density);

            return;
        }

        fogStart.accept(0.0F);
        fogEnd.accept(farPlaneDistance * 0.8F);
    }

    /**
     * Set the level fog state using current tweak context.
     *
     * @param fogMode  The {@link FogRenderer.FogMode} value.
     * @param fogShape A {@link Consumer} that accepts a {@link FogShape} value.
     * @param fogStart A {@link Consumer} that accepts a float where the fog starts.
     * @param fogEnd   A {@link Consumer} that accepts a float where the fog ends.
     * @return Whether the mod has modified the fog state.
     */
    private static boolean setState(FogRenderer.FogMode fogMode, Consumer<FogShape> fogShape, Consumer<Float> fogStart, Consumer<Float> fogEnd)
    {
        WorldFog worldFog = CandyTweak.OLD_WORLD_FOG.get();
        int farPlaneDistance = getFarPlaneDistance(worldFog);
        boolean isModified = false;

        if (worldFog != WorldFog.MODERN)
        {
            if (worldFog != WorldFog.R17_R118)
            {
                setTerrain(fogMode, worldFog, fogStart, fogEnd);
                setHorizon(fogMode, worldFog, fogStart, fogEnd);
            }
            else
            {
                if (fogMode == FogRenderer.FogMode.FOG_TERRAIN)
                {
                    fogStart.accept(farPlaneDistance * 0.75F);
                    fogEnd.accept((float) farPlaneDistance);
                }
                else
                {
                    fogStart.accept(0.0F);
                    fogEnd.accept((float) farPlaneDistance);
                }
            }

            if (GameUtil.isInNether() && CandyTweak.OLD_NETHER_FOG.get())
                fogStart.accept(0.0F);

            fogShape.accept(FogShape.SPHERE);

            isModified = true;
        }

        if (GameUtil.isInOverworld() && CandyTweak.USE_CUSTOM_OVERWORLD_FOG_DENSITY.get())
        {
            float customFogStart = 1.0F - (CandyTweak.CUSTOM_OVERWORLD_FOG_START.get() / 100.0F);
            float customFogEnd = 1.0F - (CandyTweak.CUSTOM_OVERWORLD_FOG_END.get() / 100.0F);

            fogStart.accept(farPlaneDistance * customFogStart);
            fogEnd.accept(farPlaneDistance * customFogEnd);

            isModified = true;
        }

        if (GameUtil.isInNether() && CandyTweak.USE_CUSTOM_NETHER_FOG_DENSITY.get())
        {
            float customFogStart = 1.0F - (CandyTweak.CUSTOM_NETHER_FOG_START.get() / 100.0F);
            float customFogEnd = 1.0F - (CandyTweak.CUSTOM_NETHER_FOG_END.get() / 100.0F);

            fogStart.accept(farPlaneDistance * customFogStart);
            fogEnd.accept(farPlaneDistance * customFogEnd);

            isModified = true;
        }

        return isModified;
    }

    /**
     * Changes the fog color brightness depending on max light brightness and render distance.
     *
     * @param FOG_COLOR An RGB array.
     */
    private static void calculateLightInfluence(final float[] FOG_COLOR)
    {
        Camera camera = CameraUtil.get();

        int renderDistance = GameUtil.getOldRenderDistance();
        int maxLight = ClientLevelHelper.getMaxLight(camera.getEntity());

        if (!VoidFogRenderer.isGameOverride(camera))
            maxLight = ClientLevelHelper.getSkyLight(camera.getEntity());

        float light = (float) Math.pow(0.8D, 15.0D - maxLight);
        float offset = (float) (3 - renderDistance) / 3.0F;
        float darkenTarget = light * (1.0F - offset) + offset;

        if (!CandyTweak.OLD_DARK_FOG.get() || CandyTweak.OLD_CLASSIC_ENGINE.get())
            darkenTarget = 1.0F;

        DARKNESS.setTarget(darkenTarget);

        float darkenAmount = DARKNESS.lerpFloat();

        FOG_COLOR[0] *= darkenAmount;
        FOG_COLOR[1] *= darkenAmount;
        FOG_COLOR[2] *= darkenAmount;
    }

    /**
     * Get an RGB float array influenced by environmental world factors such as the time of day and current weather
     * patterns.
     *
     * @param CUSTOM_FOG An RGB array from a custom fog color.
     * @return An RGB array that is influenced by the world environment.
     */
    private static float[] getCustomInfluence(final float[] CUSTOM_FOG)
    {
        final float[] ENV_RGB = ClientLevelHelper.getStandardEnvironmentInfluence();

        calculateLightInfluence(ENV_RGB);

        float r = ENV_RGB[0];
        float g = ENV_RGB[1];
        float b = ENV_RGB[2];

        r = Mth.clamp(r, 0.04F, 1.0F) * CUSTOM_FOG[0];
        g = Mth.clamp(g, 0.04F, 1.0F) * CUSTOM_FOG[1];
        b = Mth.clamp(b, 0.04F, 1.0F) * CUSTOM_FOG[2];

        return new float[] { r, g, b };
    }

    /**
     * Get an RGB array that indicates how much a color channel should be influenced by the sky color and weather
     * patterns. The values returned are influenced based on old vanilla colors.
     *
     * @return An RGB array.
     */
    private static float[] getOldInfluence(final float[] FOG_COLOR)
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return new float[] { 0.0F, 0.0F, 0.0F };

        if (CandyTweak.OLD_WORLD_FOG.get() == WorldFog.CLASSIC)
        {
            int renderDistance = GameUtil.getRenderDistance();

            if (renderDistance <= 3)
            {
                FOG_COLOR[0] = 1.0F;
                FOG_COLOR[1] = 1.0F;
                FOG_COLOR[2] = 1.0F;
            }
            else if (renderDistance == 4)
            {
                FOG_COLOR[0] = 239.0F / 255.0F;
                FOG_COLOR[1] = 247.0F / 255.0F;
                FOG_COLOR[2] = 1.0F;
            }
            else if (renderDistance <= 8)
            {
                FOG_COLOR[0] = 230.0F / 255.0F;
                FOG_COLOR[1] = 243.0F / 255.0F;
                FOG_COLOR[2] = 1.0F;
            }
        }

        calculateLightInfluence(FOG_COLOR);

        float partialTick = PartialTick.get();
        float timeOfDay = level.getTimeOfDay(partialTick);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float fogRed = (float) (FOG_COLOR[0] * ((double) (boundedTime * 0.96F + 0.04F)));
        float fogGreen = (float) (FOG_COLOR[1] * ((double) (boundedTime * 0.96F + 0.04F)));
        float fogBlue = (float) (FOG_COLOR[2] * ((double) (boundedTime * 0.947F + 0.063F)));

        float rainLevel = CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : level.getRainLevel(partialTick);
        float thunderLevel = CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : level.getThunderLevel(partialTick);

        if (rainLevel > 0.0F)
        {
            float redGreenShift = 1.0F - rainLevel * 0.5F;
            float blueShift = 1.0F - rainLevel * 0.4F;

            fogRed *= redGreenShift;
            fogGreen *= redGreenShift;
            fogBlue *= blueShift;
        }

        if (thunderLevel > 0.0F)
        {
            float shift = 1.0F - thunderLevel * 0.5F;

            fogRed *= shift;
            fogGreen *= shift;
            fogBlue *= shift;
        }


        return new float[] { fogRed, fogGreen, fogBlue };
    }

    /**
     * Get a fog color from the current biome and render distance.
     *
     * @return An RGBA array that will be used to change the fog colors.
     */
    private static float[] getColorFromBiome()
    {
        float saturation = 0.26F;
        float temp = ClientLevelHelper.getBiomeTemperature() / 2.0F;
        int renderDistance = GameUtil.getRenderDistance();

        if (renderDistance <= 8)
        {
            saturation = switch (renderDistance)
            {
                case 6, 7, 8 -> 0.235F;
                case 4, 5 -> 0.22F;
                case 2, 3 -> 0.18F;
                default -> 0.26F;
            };
        }

        return Color.getHSBColor(0.648F - temp * 0.05F, saturation + temp * 0.1F, 1.0F).getComponents();
    }

    /**
     * Setup overworld or Nether fog.
     *
     * @param camera      The {@link Camera} instance.
     * @param red         A {@link Supplier} that provides the current red fog color.
     * @param green       A {@link Supplier} that provides the current green fog color.
     * @param blue        A {@link Supplier} that provides the current blue fog color.
     * @param redSetter   A {@link Consumer} that accepts the new red void fog color.
     * @param greenSetter A {@link Consumer} that accepts the new green void fog color.
     * @param blueSetter  A {@link Consumer} that accepts the new blue void fog color.
     */
    public static void setupColor(Camera camera, Supplier<Float> red, Supplier<Float> green, Supplier<Float> blue, Consumer<Float> redSetter, Consumer<Float> greenSetter, Consumer<Float> blueSetter)
    {
        if (CameraUtil.isInFluid(camera))
            return;

        float currentRed = red.get();
        float currentGreen = green.get();
        float currentBlue = blue.get();
        boolean isFogOverride = isGameOverride(camera);

        Consumer<float[]> setFogRGB = (rgb) -> {
            redSetter.accept(rgb[0]);
            greenSetter.accept(rgb[1]);
            blueSetter.accept(rgb[2]);
        };

        if (GameUtil.isInOverworld())
        {
            FogColor fogColor = CandyTweak.UNIVERSAL_FOG_COLOR.get();

            if (CandyTweak.USE_CUSTOM_OVERWORLD_FOG_COLOR.get())
                setFogRGB.accept(getCustomInfluence(HexUtil.parseFloatRGBA(CandyTweak.CUSTOM_OVERWORLD_FOG_COLOR.get())));
            else if (CandyTweak.OLD_DYNAMIC_FOG_COLOR.get())
                setFogRGB.accept(getOldInfluence(getColorFromBiome()));
            else if (fogColor != FogColor.DISABLED)
            {
                switch (fogColor)
                {
                    case ALPHA_BETA -> setFogRGB.accept(getOldInfluence(HexUtil.parseFloatRGBA("0xC0D8FF")));
                    case CLASSIC -> setFogRGB.accept(getOldInfluence(HexUtil.parseFloatRGBA("0xE1F0FF")));
                    case INF_DEV -> setFogRGB.accept(getOldInfluence(HexUtil.parseFloatRGBA("0xB0D0FF")));
                }
            }
        }

        if (GameUtil.isInNether())
        {
            if (CandyTweak.USE_CUSTOM_NETHER_FOG_COLOR.get())
                setFogRGB.accept(HexUtil.parseFloatRGBA(CandyTweak.CUSTOM_NETHER_FOG_COLOR.get()));
            else if (CandyTweak.OLD_NETHER_FOG.get())
                setFogRGB.accept(HexUtil.parseFloatRGBA("0x210505"));
        }

        if (isFogOverride)
        {
            FOG_RED.setTarget(Math.min(currentRed, red.get()));
            FOG_GREEN.setTarget(Math.min(currentGreen, green.get()));
            FOG_BLUE.setTarget(Math.min(currentBlue, blue.get()));
        }
        else
        {
            FOG_RED.ifEndThenSetTarget(red.get());
            FOG_GREEN.ifEndThenSetTarget(green.get());
            FOG_BLUE.ifEndThenSetTarget(blue.get());
        }

        final float[] RGB = new float[] { FOG_RED.lerpFloat(), FOG_GREEN.lerpFloat(), FOG_BLUE.lerpFloat() };

        setFogRGB.accept(RGB);
    }

    /**
     * Setup world fog using current tweak context.
     *
     * @param camera         The {@link Camera} instance.
     * @param fogMode        The {@link FogRenderer.FogMode} value.
     * @param fogStart       A {@link Supplier} that provides where the fog currently starts.
     * @param fogEnd         A {@link Supplier} that provides where the fog currently ends.
     * @param fogShapeSetter A {@link Consumer} that accepts a {@link FogShape} value.
     * @param fogStartSetter A {@link Consumer} that accepts a float where the fog starts.
     * @param fogEndSetter   A {@link Consumer} that accepts a float where the fog ends.
     * @return Whether the mod changed the world fog.
     */
    public static boolean setupFog(Camera camera, FogRenderer.FogMode fogMode, Supplier<Float> fogStart, Supplier<Float> fogEnd, Consumer<FogShape> fogShapeSetter, Consumer<Float> fogStartSetter, Consumer<Float> fogEndSetter)
    {
        if (CameraUtil.isInFluid(camera))
            return false;

        float currentFogStart = fogStart.get();
        float currentFogEnd = fogEnd.get();
        boolean isFogOverride = isGameOverride(camera);
        boolean isModified = false;

        if (GameUtil.isInOverworld() || GameUtil.isInNether())
            isModified = setState(fogMode, fogShapeSetter, fogStartSetter, fogEndSetter);

        float modFogStart = isFogOverride ? currentFogStart : fogStart.get();
        float modFogEnd = isFogOverride ? currentFogEnd : fogEnd.get();

        if (fogMode == FogRenderer.FogMode.FOG_TERRAIN)
        {
            TERRAIN_START.setTarget(modFogStart);
            TERRAIN_END.setTarget(modFogEnd);

            fogStartSetter.accept(TERRAIN_START.lerpFloat());
            fogEndSetter.accept(TERRAIN_END.lerpFloat());
        }
        else
        {
            SKY_START.setTarget(modFogStart);
            SKY_END.setTarget(modFogEnd);

            fogStartSetter.accept(SKY_START.lerpFloat());
            fogEndSetter.accept(SKY_END.lerpFloat());
        }

        return isModified;
    }
}
