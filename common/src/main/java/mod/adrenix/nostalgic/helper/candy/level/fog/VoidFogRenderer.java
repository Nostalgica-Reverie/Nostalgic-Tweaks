package mod.adrenix.nostalgic.helper.candy.level.fog;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.helper.candy.level.ClientLevelHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.CameraUtil;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.timer.LerpTimer;
import mod.adrenix.nostalgic.util.common.world.BlockUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This utility class is used only by the client.
 */
public abstract class VoidFogRenderer
{
    /* Fields */

    private static final ArrayList<LerpTimer> LERP_TIMERS = new ArrayList<>();
    private static final LerpTimer CELESTIAL_TRANSPARENCY = makeTimer();
    private static final LerpTimer CLOUDS_TRANSPARENCY = makeTimer();
    private static final LerpTimer STARS_TRANSPARENCY = makeTimer();
    private static final LerpTimer FOG_START = makeTimer();
    private static final LerpTimer FOG_END = makeTimer();
    private static final LerpTimer FOG_RED = makeTimer();
    private static final LerpTimer FOG_GREEN = makeTimer();
    private static final LerpTimer FOG_BLUE = makeTimer();
    private static final LerpTimer VOID_RED = makeTimer();
    private static final LerpTimer VOID_GREEN = makeTimer();
    private static final LerpTimer VOID_BLUE = makeTimer();
    private static final LerpTimer SKY_RED = makeTimer();
    private static final LerpTimer SKY_GREEN = makeTimer();
    private static final LerpTimer SKY_BLUE = makeTimer();

    /* Methods */

    /**
     * Create and track a new animation timer.
     *
     * @return A new {@link LerpTimer} instance.
     */
    private static LerpTimer makeTimer()
    {
        LerpTimer timer = LerpTimer.create(2L, TimeUnit.SECONDS);
        LERP_TIMERS.add(timer);

        return timer;
    }

    /**
     * Reset the initialization state of the void fog.
     */
    public static void reset()
    {
        LERP_TIMERS.forEach(LerpTimer::clear);
    }

    /**
     * Set the target of a timer. The target will not be set if the player is above the cave/void fog altitude.
     *
     * @param timer  The {@link LerpTimer} to set the target of.
     * @param target The target the timer needs to reach.
     */
    private static void setTarget(LerpTimer timer, float target)
    {
        if (timer.isFinished() && isGameOverride(CameraUtil.get()))
            timer.stopAndSetTarget(target);
        else
            timer.setTarget(target);
    }

    /**
     * Set the stars alpha transparency.
     *
     * @param alpha A normalized alpha value.
     */
    public static void setStarsTransparency(float alpha)
    {
        setTarget(STARS_TRANSPARENCY, Mth.clamp(alpha, 0.0F, 1.0F));
    }

    /**
     * Set the void red color of the skybox if cave/void fog is not rendering.
     *
     * @param red A normalized red value.
     */
    public static void setSkyRed(float red)
    {
        if (isGameOverride(CameraUtil.get()))
            setTarget(SKY_RED, Mth.clamp(red, 0.0F, 1.0F));
    }

    /**
     * Set the void green color of the skybox if cave/void fog is not rendering.
     *
     * @param green A normalized green value.
     */
    public static void setSkyGreen(float green)
    {
        if (isGameOverride(CameraUtil.get()))
            setTarget(SKY_GREEN, Mth.clamp(green, 0.0F, 1.0F));
    }

    /**
     * Set the void blue color of the skybox if cave/void fog is not rendering.
     *
     * @param blue A normalized blue value.
     */
    public static void setSkyBlue(float blue)
    {
        if (isGameOverride(CameraUtil.get()))
            setTarget(SKY_BLUE, Mth.clamp(blue, 0.0F, 1.0F));
    }

    /**
     * @return The normalized red value of the sky.
     */
    public static float getSkyRed()
    {
        return SKY_RED.lerpFloat();
    }

    /**
     * @return The normalized green value of the sky.
     */
    public static float getSkyGreen()
    {
        return SKY_GREEN.lerpFloat();
    }

    /**
     * @return The normalized blue value of the sky.
     */
    public static float getSkyBlue()
    {
        return SKY_BLUE.lerpFloat();
    }

    /**
     * Set the void skybox color if cave/void fog is not rendering.
     *
     * @param red   The normalized red color value.
     * @param green The normalized green color value.
     * @param blue  The normalized blue color value.
     */
    public static void setVoidRGB(float red, float green, float blue)
    {
        if (!isGameOverride(CameraUtil.get()))
            return;

        setTarget(VOID_RED, Mth.clamp(red, 0.0F, 1.0F));
        setTarget(VOID_GREEN, Mth.clamp(green, 0.0F, 1.0F));
        setTarget(VOID_BLUE, Mth.clamp(blue, 0.0F, 1.0F));
    }

    /**
     * @return A copy of the void skybox RGB color.
     */
    public static float[] getVoidRGB()
    {
        return new float[] { VOID_RED.lerpFloat(), VOID_GREEN.lerpFloat(), VOID_BLUE.lerpFloat() };
    }

    /**
     * @return The current normalized stars transparency alpha.
     */
    public static float getStarsTransparency()
    {
        return STARS_TRANSPARENCY.lerpFloat();
    }

    /**
     * @return Whether cave/void fog is ready to be rendered.
     */
    public static boolean isRendering()
    {
        return CandyTweak.RENDER_VOID_FOG.get();
    }

    /**
     * Check if void fog should be considered disabled.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether void fog is disabled.
     */
    private static boolean isDisabled(Camera camera)
    {
        boolean isNotSurvival = GameUtil.isNotSurvivalMode();
        boolean isHeightOutOfBounds = CandyTweak.VOID_FOG_START.get() < camera.getEntity().getY() + 0.5D;
        boolean isCreativeOverride = !CandyTweak.CREATIVE_VOID_FOG.get() && isNotSurvival;
        boolean isVoidFogDisabled = !CandyTweak.RENDER_VOID_FOG.get();

        return isVoidFogDisabled || isHeightOutOfBounds || isCreativeOverride || isAboveHorizon();
    }

    /**
     * Whether the void/cave fog renderer is finished (or there is a game override) and is transitioning values back to
     * vanilla.
     *
     * @param camera The {@link Camera} instance.
     * @return Whether the void/cave fog renderer is finished.
     */
    public static boolean isGameOverride(Camera camera)
    {
        return isDisabled(camera) || CameraUtil.canSeeSky(camera) || CameraUtil.isInFluid(camera);
    }

    /**
     * Set the transparency for the sun/moon.
     */
    public static void setCelestialTransparency()
    {
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null)
            return;

        float[] rgb = RenderSystem.getShaderColor();
        float alpha = Math.min(1.0F - level.getRainLevel(PartialTick.get()), CELESTIAL_TRANSPARENCY.lerpFloat());

        RenderSystem.setShaderColor(rgb[0], rgb[1], rgb[2], alpha);
    }

    /**
     * Set the transparency for the clouds.
     */
    public static void setCloudTransparency()
    {
        float[] rgb = RenderSystem.getShaderColor();
        float alpha = CLOUDS_TRANSPARENCY.lerpFloat();

        RenderSystem.setShaderColor(rgb[0], rgb[1], rgb[2], alpha);
    }

    /**
     * @return Whether the player is currently above the level's horizon height.
     */
    private static boolean isAboveHorizon()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;

        if (player == null || level == null)
            return true;

        return !(player.getEyePosition(PartialTick.get()).y - level.getLevelData().getHorizonHeight(level) < 0.0D);
    }

    /**
     * Get the entity's level minimum build height.
     *
     * @param entity The {@link Entity} to get a level from.
     * @return The current level's minimum build height.
     */
    private static int getMinBuildHeight(Entity entity)
    {
        return entity.level().getMinBuildHeight();
    }

    /**
     * Get an entity's y-level position that is greater than zero. This is the distance from the bottom of the world to
     * the given entity.
     *
     * @param entity The {@link Entity} to get a y-level from.
     * @return The entity's absolute y-level position.
     */
    private static double getAbsoluteY(Entity entity)
    {
        return entity.getY() - getMinBuildHeight(entity);
    }

    /**
     * Get where cave/void fog should start based on fog thickness and current cave/void fog distance.
     *
     * @param camera   The {@link Camera} instance.
     * @param distance The calculated distance based on local brightness and y-level
     * @return Where cave/void fog should start.
     */
    private static float getFogStart(Camera camera, float distance)
    {
        return CameraUtil.isFoggy(camera) ? distance * 0.05F : distance * Math.max(0.0F, 0.55F * (1.0F - (distance - 5.0F) / 127.0F));
    }

    /**
     * Get where cave/void fog should end based on fog thickness and current cave/void fog distance.
     *
     * @param camera   The {@link Camera} instance.
     * @param distance The calculated distance based on local brightness and y-level.
     * @return Where cave/void fog should end.
     */
    private static float getFogEnd(Camera camera, float distance)
    {
        return CameraUtil.isFoggy(camera) ? Math.min(distance, 192.0F) / 2.0F : distance;
    }

    /**
     * Get the greatest light value from block light or skylight. The encroachment tweak will add onto the calculated
     * brightness.
     *
     * @param entity The {@link Entity} to get context from.
     * @return The greatest light value around the given entity.
     */
    private static int getMaxLight(Entity entity)
    {
        int light;

        if (CandyTweak.LIGHT_REMOVES_VOID_FOG.get())
            light = entity.level().getMaxLocalRawBrightness(entity.blockPosition());
        else
            light = ClientLevelHelper.getSkyLight(entity);

        int encroachment = (int) ((1 - (CandyTweak.VOID_FOG_ENCROACH.get() / 100.0F)) * 15);

        return Mth.clamp(light + encroachment, 0, 15);
    }

    /**
     * Get the value to use for saturation and brightness for the fog color, sky color, and void color.
     *
     * @param camera The {@link Camera} instance.
     * @return The darkness value of cave/void fog.
     */
    private static float getDarknessValue(Camera camera)
    {
        int fogStart = CandyTweak.VOID_FOG_START.get();
        int skyLight = ClientLevelHelper.getSkyLight(camera.getEntity());
        int maxHeight = fogStart - getMinBuildHeight(camera.getEntity());
        float heightShift = (float) getAbsoluteY(camera.getEntity()) / maxHeight;

        return Math.max((float) Math.pow(Math.max(heightShift, skyLight / 15.0F), 4.0D), 0.05F);
    }

    /**
     * Gets the fog distance based on current render distance and current tweak context.
     *
     * @param entity The {@link Entity} to get context from.
     * @return A cave/void fog distance value based on tweak context, render distance, and local brightness.
     */
    private static float getDistance(Entity entity)
    {
        float renderDistance = Minecraft.getInstance().gameRenderer.getRenderDistance();
        double fogStart = CandyTweak.VOID_FOG_START.get();
        double fogDistance = getMaxLight(entity) / 16.0D + getAbsoluteY(entity) / (fogStart == 0 ? 1 : fogStart);

        return fogDistance >= 1 ? renderDistance : (float) Mth.clamp(100.0D * Math.pow(Math.max(fogDistance, 0.0D), 2.0D), 5.0D, renderDistance);
    }

    /**
     * The change in cave/void fog distance based on y-level.
     *
     * @param entity The {@link Entity} to get context from.
     * @return A normalized delta value based on where cave/void fog should start.
     */
    private static float getDistanceDelta(Entity entity)
    {
        return Math.max(1.0F, Math.min(1.0F, (1.0F - ((float) getAbsoluteY(entity) - CandyTweak.VOID_FOG_START.get() - 15.0F) / 15.0F)));
    }

    /**
     * Add void fog particles to the client level if the proper conditions are met.
     *
     * @param randomSource The {@link RandomSource} instance.
     */
    public static void addParticles(RandomSource randomSource)
    {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        ClientLevel level = Minecraft.getInstance().level;
        boolean isFogDisabled = !CandyTweak.RENDER_VOID_FOG.get() || isAboveHorizon();
        boolean isCreativeDisabled = !CandyTweak.CREATIVE_VOID_PARTICLES.get() && GameUtil.isCreativeMode();
        boolean isDisabled = isFogDisabled || isCreativeDisabled;

        if (isDisabled || entity == null || level == null)
            return;

        BlockPos playerPos = entity.blockPosition();
        int radius = CandyTweak.VOID_PARTICLE_RADIUS.get();
        int particleStart = CandyTweak.VOID_PARTICLE_START.get();
        float density = (float) CandyTweak.VOID_PARTICLE_DENSITY.get() / 100.0F;

        if (GameUtil.isInOverworld() && Math.random() <= density && entity.getY() <= particleStart)
        {
            BlockPos randX = BlockUtil.getRandom(randomSource, radius);
            BlockPos randY = BlockUtil.getRandom(randomSource, radius);
            BlockPos randPos = randX.subtract(randY).offset(playerPos);
            BlockState blockState = level.getBlockState(randPos);
            FluidState fluidState = level.getFluidState(randPos);

            if (blockState.isAir() && fluidState.isEmpty() && randPos.getY() <= particleStart)
            {
                if (randomSource.nextInt(8) <= particleStart - level.getMinBuildHeight())
                {
                    double px = randPos.getX() + randomSource.nextFloat();
                    double py = randPos.getY() + randomSource.nextFloat();
                    double pz = randPos.getZ() + randomSource.nextFloat();

                    boolean nearBedrock = BlockUtil.isNearBedrock(randPos, level);
                    double ySpeed = nearBedrock ? randomSource.nextFloat() : 0.0D;

                    ParticleOptions particle = nearBedrock ? ParticleTypes.ASH : ParticleTypes.MYCELIUM;

                    level.addParticle(particle, px, py, pz, 0.0D, ySpeed, 0.0D);
                }
            }
        }
    }

    /**
     * Apply darkness linear interpolation to the given red, green, and blue linear interpolation timers.
     *
     * @param darkness The darkness value.
     * @param color    The non-cave/void fog color.
     * @param red      The red {@link LerpTimer}.
     * @param green    The green {@link LerpTimer}.
     * @param blue     The blue {@link LerpTimer}.
     */
    private static void lerpColor(float darkness, Color color, LerpTimer red, LerpTimer green, LerpTimer blue)
    {
        float saturation = Math.max(darkness * color.getSaturation(), 0.02F);
        float brightness = Math.max(darkness * color.getBrightness(), 0.02F);
        float[] rgb = Color.getHSBColor(color.getHue(), saturation, brightness).getComponents();

        setTarget(red, rgb[0]);
        setTarget(green, rgb[1]);
        setTarget(blue, rgb[2]);
    }

    /**
     * Set the void fog color.
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
        if (!CandyTweak.RENDER_VOID_FOG.get() || !GameUtil.isInOverworld() || CameraUtil.isInFluid(camera))
            return;

        if (isGameOverride(camera))
        {
            setTarget(FOG_RED, red.get());
            setTarget(FOG_GREEN, green.get());
            setTarget(FOG_BLUE, blue.get());
        }
        else
        {
            final float DARKNESS = getDarknessValue(camera);
            final Color OVERWORLD_COLOR = new Color(red.get(), green.get(), blue.get());

            lerpColor(DARKNESS, OVERWORLD_COLOR, FOG_RED, FOG_GREEN, FOG_BLUE);
            lerpColor(DARKNESS, OVERWORLD_COLOR, SKY_RED, SKY_GREEN, SKY_BLUE);
            lerpColor(DARKNESS, OVERWORLD_COLOR, VOID_RED, VOID_GREEN, VOID_BLUE);
        }

        redSetter.accept(FOG_RED.lerpFloat());
        greenSetter.accept(FOG_GREEN.lerpFloat());
        blueSetter.accept(FOG_BLUE.lerpFloat());
    }

    /**
     * Setup cave/void fog.
     *
     * @param camera         The {@link Camera} instance.
     * @param fogMode        The {@link FogRenderer.FogMode} value.
     * @param fogStart       A {@link Supplier} that provides where the fog currently starts.
     * @param fogEnd         A {@link Supplier} that provides where the fog currently ends.
     * @param fogStartSetter A {@link Consumer} that accepts a float where the fog starts.
     * @param fogEndSetter   A {@link Consumer} that accepts a float where the fog ends.
     * @return Whether the mod is setting up cave/void fog.
     */
    public static boolean setupFog(Camera camera, FogRenderer.FogMode fogMode, Supplier<Float> fogStart, Supplier<Float> fogEnd, Consumer<Float> fogStartSetter, Consumer<Float> fogEndSetter)
    {
        if (fogMode != FogRenderer.FogMode.FOG_TERRAIN || !CandyTweak.RENDER_VOID_FOG.get() || !GameUtil.isInOverworld() || CameraUtil.isInFluid(camera))
            return false;

        Entity entity = camera.getEntity();
        boolean isDisabled = isDisabled(camera) || OverworldFogRenderer.isGameOverride(camera);
        float darkness = getDarknessValue(camera);
        float distance = getDistance(entity);
        float encroach = getDistanceDelta(entity);

        if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.NIGHT_VISION))
            distance *= 4 * GameRenderer.getNightVisionScale(living, PartialTick.get());

        float celestialTarget = !isDisabled && darkness < 0.5F && ClientLevelHelper.getSkyLight(entity) == 0 ? 0.0F : 1.0F;
        float cloudsTarget = !isDisabled && darkness < 0.5F && ClientLevelHelper.getSkyLight(entity) == 0 ? 0.0F : 1.0F;
        float starsTarget = !isDisabled && darkness < 0.5F && ClientLevelHelper.getSkyLight(entity) == 0 ? 0.0F : STARS_TRANSPARENCY.endFloat();
        float startTarget = Mth.lerp(Math.abs(darkness - 1.0F), fogStart.get(), getFogStart(camera, distance) * encroach);
        float endTarget = Mth.lerp(Math.abs(darkness - 1.0F), fogEnd.get(), getFogEnd(camera, distance) * encroach);

        setTarget(CELESTIAL_TRANSPARENCY, celestialTarget);
        setTarget(CLOUDS_TRANSPARENCY, cloudsTarget);
        setTarget(STARS_TRANSPARENCY, starsTarget);
        setTarget(FOG_START, Math.min(fogStart.get(), isDisabled ? fogStart.get() : startTarget));
        setTarget(FOG_END, Math.min(fogEnd.get(), isDisabled ? fogEnd.get() : endTarget));

        fogStartSetter.accept(FOG_START.lerpFloat());
        fogEndSetter.accept(FOG_END.lerpFloat());

        return !isDisabled || !FOG_START.isFinished() || !FOG_END.isFinished();
    }
}
