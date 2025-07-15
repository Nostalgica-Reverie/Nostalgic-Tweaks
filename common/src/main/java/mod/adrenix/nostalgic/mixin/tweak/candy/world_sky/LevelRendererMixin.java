package mod.adrenix.nostalgic.mixin.tweak.candy.world_sky;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import mod.adrenix.nostalgic.client.AfterConfigSave;
import mod.adrenix.nostalgic.helper.candy.level.SkyHelper;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.Generic;
import mod.adrenix.nostalgic.tweak.enums.WorldFog;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Shadows */

    @Shadow @Nullable private ClientLevel level;
    @Shadow @Final private Minecraft minecraft;

    @Shadow
    protected abstract void createStars();

    @Shadow
    private static BufferBuilder.RenderedBuffer buildSkyDisc(BufferBuilder builder, float y)
    {
        return null;
    }

    /* Injections */

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void nt_world_sky$onCreateLevelRenderer(CallbackInfo callback)
    {
        SkyHelper.createBlueVoid(LevelRendererMixin::buildSkyDisc);

        if (SkyHelper.STARS_RUNNABLE_SAVED.ifDisabledThenEnable())
            AfterConfigSave.addInstruction(this::createStars);

        if (SkyHelper.BLUE_RUNNABLE_SAVED.ifDisabledThenEnable())
            AfterConfigSave.addInstruction(() -> SkyHelper.createBlueVoid(LevelRendererMixin::buildSkyDisc));
    }

    /**
     * Caches the model view matrix and the projection matrix so the stars and sky can be overlaid with the blue void
     * correctly.
     */
    @Inject(
        method = "renderSky",
        at = @At("HEAD")
    )
    private void nt_world_sky$onRenderSky(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        if (!ModTweak.ENABLED.get())
            return;

        SkyHelper.MODEL_VIEW_MATRIX.set(new Matrix4f(poseStack.last().pose()));
        SkyHelper.PROJECTION_MATRIX.set(new Matrix4f(projectionMatrix));
    }

    /**
     * Draws the old blue void color onto the sky. This occurs after the sky buffer has drawn and before the dark buffer
     * is drawn.
     */
    @Inject(
        method = "renderSky",
        at = @At(
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
        )
    )
    private void nt_world_sky$onDrawSkyBuffer(CallbackInfo callback)
    {
        if (CandyTweak.OLD_BLUE_VOID.get() == Generic.MODERN)
            return;

        SkyHelper.setBlueColor();
        ShaderInstance shader = RenderSystem.getShader();

        if (shader != null)
        {
            SkyHelper.BLUE_VOID_BUFFER.ifPresent(buffer -> {
                buffer.bind();
                buffer.drawWithShader(SkyHelper.MODEL_VIEW_MATRIX.get(), SkyHelper.PROJECTION_MATRIX.get(), shader);
                VertexBuffer.unbind();
            });
        }
    }

    /**
     * Disables rendering of the dark void if the blue void is enabled and its respective override is enabled.
     */
    @WrapWithCondition(
        method = "renderSky",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/player/LocalPlayer;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"
            )
        ),
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"
        )
    )
    private boolean nt_world_sky$onRenderVoid(VertexBuffer buffer, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader)
    {
        if (!ModTweak.ENABLED.get())
            return true;

        Generic voidState = CandyTweak.OLD_BLUE_VOID.get();
        boolean isBlueOverride = CandyTweak.OLD_BLUE_VOID_OVERRIDE.get();
        boolean isBlueRendered = Generic.ALPHA == voidState || Generic.BETA == voidState;

        if (!isBlueRendered || !isBlueOverride)
        {
            final float[] DARK_RGB = RenderSystem.getShaderColor();
            final float[] VOID_RGB = VoidFogRenderer.getVoidRGB();

            VoidFogRenderer.setVoidRGB(DARK_RGB[0], DARK_RGB[1], DARK_RGB[2]);

            if (VoidFogRenderer.isRendering())
                RenderSystem.setShaderColor(VOID_RGB[0], VOID_RGB[1], VOID_RGB[2], DARK_RGB[3]);

            return true;
        }

        return false;
    }

    /**
     * Changes the star brightness and transparency values.
     */
    @Inject(
        method = "renderSky",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/FogRenderer;setupNoFog()V"
        )
    )
    private void nt_world_sky$onSetupStarColor(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        Generic starsState = CandyTweak.OLD_STARS.get();

        if (!ModTweak.ENABLED.get() || this.level == null)
            return;

        float rain = 1.0F - (CandyTweak.PREVENT_WEATHER_INFLUENCE.get() ? 0.0F : this.level.getRainLevel(partialTick));
        float transparency = this.level.getStarBrightness(partialTick) * rain;
        float color = Generic.BETA == starsState ? transparency : transparency / 0.5F;

        VoidFogRenderer.setStarsTransparency(transparency);

        if (VoidFogRenderer.isRendering())
            transparency = VoidFogRenderer.getStarsTransparency();

        if (Generic.MODERN != starsState)
            RenderSystem.setShaderColor(color, color, color, transparency);

        if (CandyTweak.OLD_WORLD_FOG.get() == WorldFog.ALPHA_R164 && GameUtil.getRenderDistance() <= 4)
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Lets the dark void follow the camera's current y-level.
     */
    @ModifyArg(
        index = 1,
        method = "renderSky",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
        )
    )
    private float nt_world_sky$setTranslationForDarkSkyBuffer(float y)
    {
        if (!CandyTweak.OLD_DARK_VOID_HEIGHT.get())
            return y;

        return (float) (y - Math.max(this.minecraft.gameRenderer.getMainCamera().getPosition().y - 65.0F, 0.0F));
    }

    /**
     * Disables the change in sky color when the sun is rising or setting.
     */
    @ModifyExpressionValue(
        method = "renderSky",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/DimensionSpecialEffects;getSunriseColor(FF)[F"
        )
    )
    private float[] nt_world_sky$setSunriseColor(float[] color)
    {
        return CandyTweak.RENDER_SUNRISE_SUNSET_COLOR.get() ? color : null;
    }

    /**
     * Changes the rotation of the sunrise/sunset sky disc color by 90 degrees on the Z-axis.
     */
    @ModifyArg(
        method = "renderSky",
        at = @At(
            ordinal = 2,
            value = "INVOKE",
            target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;"
        )
    )
    private float nt_world_sky$setSkyDiscColorRotation(float rotation)
    {
        return CandyTweak.OLD_SUNRISE_AT_NORTH.get() ? 0.0F : rotation;
    }

    /**
     * Changes the rotation of the sun and moon by 90 degrees on the y-axis.
     */
    @ModifyArg(
        method = "renderSky",
        at = @At(
            ordinal = 3,
            value = "INVOKE",
            target = "Lcom/mojang/math/Axis;rotationDegrees(F)Lorg/joml/Quaternionf;"
        )
    )
    private float nt_world_sky$setCelestialRotation(float rotation)
    {
        return CandyTweak.OLD_SUNRISE_AT_NORTH.get() ? 0.0F : rotation;
    }

    /**
     * Changes the width of the stars to simulate old stars.
     */
    @ModifyExpressionValue(
        method = "drawStars",
        at = @At(
            value = "CONSTANT",
            args = "floatValue=0.15F"
        )
    )
    private float nt_world_sky$setStarWidth(float width)
    {
        return switch (CandyTweak.OLD_STARS.get())
        {
            case ALPHA, BETA -> 0.25F;
            default -> width;
        };
    }

    /**
     * Changes the height of the stars to simulate old stars.
     */
    @ModifyExpressionValue(
        method = "drawStars",
        at = @At(
            value = "CONSTANT",
            args = "floatValue=0.1F"
        )
    )
    private float nt_world_sky$setStarHeight(float height)
    {
        return switch (CandyTweak.OLD_STARS.get())
        {
            case ALPHA, BETA -> 0.25F;
            default -> height;
        };
    }
}
