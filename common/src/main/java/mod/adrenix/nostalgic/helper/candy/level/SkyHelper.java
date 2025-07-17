package mod.adrenix.nostalgic.helper.candy.level;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.enums.SkyColor;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import mod.adrenix.nostalgic.util.common.data.Holder;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.function.BiFunction;

/**
 * This utility class is used only by the client.
 */
public abstract class SkyHelper
{
    /* Fields */

    /**
     * Flag that indicates if the client level's star buffer runnable has been cached in the mod's after-save utility.
     */
    public static final FlagHolder STARS_RUNNABLE_SAVED = FlagHolder.off();

    /**
     * Flag that indicates if the client level's void buffer runnable has been cached in the mod's after-save utility.
     */
    public static final FlagHolder BLUE_RUNNABLE_SAVED = FlagHolder.off();

    /**
     * Holds a copy of the sky's current model view matrix.
     */
    public static final Holder<Matrix4f> MODEL_VIEW_MATRIX = Holder.create(new Matrix4f());

    /**
     * Holds a copy of the sky's current projection matrix.
     */
    public static final Holder<Matrix4f> PROJECTION_MATRIX = Holder.create(new Matrix4f());

    /**
     * Holds the sky's blue void buffer. This will be setup and torn down by the level renderer.
     */
    public static final NullableHolder<VertexBuffer> BLUE_VOID_BUFFER = NullableHolder.empty();

    /* Methods */

    /**
     * Create a new blue void buffer.
     *
     * @param skyDiscBuilder A {@link BiFunction} that accepts a {@link BufferBuilder} and height and possibly returns
     *                       {@link MeshData}.
     */
    public static void createBlueVoid(BiFunction<Tesselator, Float, MeshData> skyDiscBuilder)
    {
        Tesselator tesselator = Tesselator.getInstance();

        float height = switch (CandyTweak.OLD_BLUE_VOID.get())
        {
            case ALPHA -> -32.0F;
            case BETA, MODERN -> -48.0F;
        };

        final MeshData mesh = skyDiscBuilder.apply(tesselator, height);

        if (mesh != null)
        {
            BLUE_VOID_BUFFER.ifPresent(VertexBuffer::close);
            BLUE_VOID_BUFFER.set(new VertexBuffer(VertexBuffer.Usage.STATIC));

            BLUE_VOID_BUFFER.ifPresent(vertexBuffer -> {
                vertexBuffer.bind();
                vertexBuffer.upload(mesh);

                VertexBuffer.unbind();
            });
        }
    }

    /**
     * Changes the color of the blue void based on environmental factors such as the time of the day and current level
     * weather patterns.
     *
     * @return An RGB array.
     */
    private static float[] getBlueEnvironmentInfluence()
    {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level == null)
            return new float[] { 0.0F, 0.0F, 0.0F };

        float partialTicks = PartialTick.get();
        float timeOfDay = level.getTimeOfDay(partialTicks);
        float boundedTime = Mth.clamp(Mth.cos(timeOfDay * ((float) Math.PI * 2)) * 2.0F + 0.5F, 0.0F, 1.0F);

        float voidRed = (float) (0.30F * ((double) (boundedTime * 0.05F + 0.95F)));
        float voidGreen = (float) (0.24F * ((double) (boundedTime * 0.05F + 0.95F)));
        float voidBlue = (float) (0.85F * ((double) (boundedTime * 0.85F + 0.15F)));

        float rainLevel = CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : level.getRainLevel(partialTicks);
        float thunderLevel = CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : level.getThunderLevel(partialTicks);

        if (rainLevel > 0.0F)
        {
            float redGreenShift = 1.0F - rainLevel * 0.5F;
            float blueShift = 1.0F - rainLevel * 0.4F;

            voidRed *= redGreenShift;
            voidGreen *= redGreenShift;
            voidBlue *= blueShift;
        }

        if (thunderLevel > 0.0F)
        {
            float shift = 1.0F - thunderLevel * 0.5F;

            voidRed *= shift;
            voidGreen *= shift;
            voidBlue *= shift;
        }

        return new float[] { voidRed, voidGreen, voidBlue };
    }

    /**
     * Creates the correct blue void color based on the current level environment.
     */
    public static void setBlueColor()
    {
        boolean isCustom = CandyTweak.CUSTOM_VOID_SKY.get();

        final float[] ENV_RGB = isCustom ? ClientLevelHelper.getStandardEnvironmentInfluence() : getBlueEnvironmentInfluence();

        float r = ENV_RGB[0];
        float g = ENV_RGB[1];
        float b = ENV_RGB[2];

        final float OLD_RED = 0.13F;
        final float OLD_GREEN = 0.17F;
        final float OLD_BLUE = 0.7F;
        final float[] CUSTOM_RGB = HexUtil.parseFloatRGBA(CandyTweak.CUSTOM_VOID_SKY_COLOR.get());

        r = Mth.clamp(r, 0.08F, 1.0F) * (isCustom ? CUSTOM_RGB[0] : OLD_RED);
        g = Mth.clamp(g, 0.08F, 1.0F) * (isCustom ? CUSTOM_RGB[1] : OLD_GREEN);
        b = Mth.clamp(b, 0.08F, 1.0F) * (isCustom ? CUSTOM_RGB[2] : OLD_BLUE);

        VoidFogRenderer.setVoidRGB(r, g, b);

        if (VoidFogRenderer.isRendering())
        {
            final float[] VOID_RGB = VoidFogRenderer.getVoidRGB();
            r = VOID_RGB[0];
            g = VOID_RGB[1];
            b = VOID_RGB[2];
        }

        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(r, g, b, 1.0F);
    }

    /**
     * Get a dynamic sky color from the current biome.
     *
     * @return An ARGB integer for the sky.
     */
    private static int getColorFromBiome()
    {
        float temp = ClientLevelHelper.getBiomeTemperature() / 2.0F;

        return Color.getHSBColor(0.653F - temp * 0.05F, 0.415F + temp * 0.1F, 1.0F).get();
    }

    /**
     * Get an old sky color using current biome and tweak context.
     *
     * @param color The current modern sky color.
     * @return An old sky color to apply to the sky.
     */
    public static int getOldColor(int color)
    {
        if (GameUtil.isInOverworld())
        {
            if (GameUtil.getRenderDistance() <= 4)
                return HexUtil.parseInt(RenderSystem.getShaderFogColor());

            SkyColor skyColor = CandyTweak.UNIVERSAL_SKY_COLOR.get();

            if (CandyTweak.CUSTOM_OVERWORLD_SKY.get())
                return HexUtil.parseInt(CandyTweak.CUSTOM_OVERWORLD_SKY_COLOR.get());
            else if (CandyTweak.OLD_DYNAMIC_SKY_COLOR.get())
                return getColorFromBiome();
            else
            {
                return switch (skyColor)
                {
                    case DISABLED -> color;
                    case CLASSIC -> 0x9CCDFF;
                    case INF_DEV -> 0xC6DEFF;
                    case ALPHA -> 0x8BBDFF;
                    case BETA -> 0x97A3FF;
                };
            }
        }
        else if (GameUtil.isInNether())
        {
            if (CandyTweak.OLD_NETHER_SKY.get())
                return 0x210505;
        }

        return color;
    }
}
