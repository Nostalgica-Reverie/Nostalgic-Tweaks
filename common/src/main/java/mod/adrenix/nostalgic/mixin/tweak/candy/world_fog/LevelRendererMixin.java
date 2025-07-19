package mod.adrenix.nostalgic.mixin.tweak.candy.world_fog;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.helper.candy.level.fog.FogHelper;
import mod.adrenix.nostalgic.helper.candy.level.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.WaterFogRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.tweak.enums.WorldFog;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /**
     * Resets the level fog renderer
     */
    @Inject(
        method = "allChanged",
        at = @At("HEAD")
    )
    private void nt_world_fog$onAllChanged(CallbackInfo callback)
    {
        OverworldFogRenderer.reset();
        WaterFogRenderer.reset();
        VoidFogRenderer.reset();
    }

    /**
     * Sets the transparency of the sunrise/sunset colors when cave/void fog is rendering.
     */
    @Inject(
        method = "renderSky",
        at = @At(
            ordinal = 1,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
        )
    )
    private void nt_world_fog$onSetSunriseColor(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo callback)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.setCelestialTransparency();
    }

    /**
     * Sets the transparency of the sun/moon when void fog is rendered. The sun/moon is rendered transparent if the
     * render distance is less than four, and the old world fog tweak is set to alpha - r1.6.4.
     */
    @Inject(
        method = "renderSky",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
            )
        ),
        at = @At(
            ordinal = 0,
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
        )
    )
    private void nt_world_fog$setSunAndMoonTransparency(CallbackInfo callback)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.setCelestialTransparency();

        boolean isAlphaFog = CandyTweak.OLD_WORLD_FOG.get() == WorldFog.ALPHA_R164;
        boolean isShort = GameUtil.getRenderDistance() <= 5;

        if (CandyTweak.OLD_CLASSIC_ENGINE.get() || (isAlphaFog && isShort))
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 0.0F);
    }

    /**
     * Sets the transparency of the clouds when void fog is rendered.
     */
    @Inject(
        method = "renderClouds",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/vertex/VertexBuffer;drawWithShader(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/ShaderInstance;)V"
        )
    )
    private void nt_world_fog$setCloudTransparency(CallbackInfo callback)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.setCloudTransparency();
    }

    /**
     * Restores the shader color after clouds have rendered.
     */
    @Inject(
        method = "renderClouds",
        at = @At("RETURN")
    )
    private void nt_world_fog$resetCloudTransparency(CallbackInfo callback)
    {
        if (!ModTweak.ENABLED.get())
            return;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Checks if Sodium is getting ready to mess with the fog.
     */
    @Inject(
        method = "renderClouds",
        at = @At("HEAD")
    )
    private void nt_world_fog$sodiumCheckOnRenderClouds(CallbackInfo callback)
    {
        FogHelper.SODIUM_CLOUDS.enableIf(ModTracker.SODIUM::isInstalled);
    }
}
