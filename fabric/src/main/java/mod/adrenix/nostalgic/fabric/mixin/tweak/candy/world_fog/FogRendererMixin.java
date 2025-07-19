package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.world_fog;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.helper.candy.level.fog.FogHelper;
import mod.adrenix.nostalgic.helper.candy.level.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.WaterFogRenderer;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.common.MixinPriority;
import mod.adrenix.nostalgic.util.common.data.NumberHolder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = FogRenderer.class,
    priority = MixinPriority.APPLY_FIRST
)
public abstract class FogRendererMixin
{
    /* Shadows */

    @Shadow private static float fogRed;
    @Shadow private static float fogGreen;
    @Shadow private static float fogBlue;

    /* Injections */

    /**
     * Changes the world fog based on current tweak context.
     */
    @Inject(
        method = "setupFog",
        at = @At("RETURN")
    )
    private static void nt_fabric_world_fog$onFinishSetup(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo callback)
    {
        if (!ModTweak.ENABLED.get() || FogHelper.SODIUM_CLOUDS.ifEnabledThenDisable())
            return;

        OverworldFogRenderer.setupFog(camera, fogMode, RenderSystem::getShaderFogStart, RenderSystem::getShaderFogEnd, RenderSystem::setShaderFogShape, RenderSystem::setShaderFogStart, RenderSystem::setShaderFogEnd);
        WaterFogRenderer.setupFog(camera, RenderSystem::setShaderFogShape, RenderSystem::setShaderFogStart, RenderSystem::setShaderFogEnd);
        VoidFogRenderer.setupFog(camera, fogMode, RenderSystem::getShaderFogStart, RenderSystem::getShaderFogEnd, RenderSystem::setShaderFogStart, RenderSystem::setShaderFogEnd);
    }

    /**
     * Changes the fog color based on current tweak context.
     */
    @Inject(
        method = "setupColor",
        at = @At("RETURN")
    )
    private static void nt_fabric_world_fog$onFinishColorSetup(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo callback)
    {
        if (!ModTweak.ENABLED.get())
            return;

        final NumberHolder<Float> FOG_RED = NumberHolder.create(fogRed);
        final NumberHolder<Float> FOG_GREEN = NumberHolder.create(fogGreen);
        final NumberHolder<Float> FOG_BLUE = NumberHolder.create(fogBlue);

        if (!WaterFogRenderer.setupColor(camera, FOG_RED::set, FOG_GREEN::set, FOG_BLUE::set))
        {
            OverworldFogRenderer.setupColor(camera, FOG_RED::get, FOG_GREEN::get, FOG_BLUE::get, FOG_RED::set, FOG_GREEN::set, FOG_BLUE::set);
            VoidFogRenderer.setupColor(camera, FOG_RED::get, FOG_GREEN::get, FOG_BLUE::get, FOG_RED::set, FOG_GREEN::set, FOG_BLUE::set);
        }

        fogRed = FOG_RED.get();
        fogGreen = FOG_GREEN.get();
        fogBlue = FOG_BLUE.get();

        RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
    }
}
