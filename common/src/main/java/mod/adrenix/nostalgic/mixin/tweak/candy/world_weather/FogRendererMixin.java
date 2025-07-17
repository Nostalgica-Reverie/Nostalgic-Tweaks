package mod.adrenix.nostalgic.mixin.tweak.candy.world_weather;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin
{
    /**
     * Prevents rain influence on fog color based on tweak context.
     */
    @ModifyExpressionValue(
        method = "setupColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
        )
    )
    private static float nt_world_weather$modifyRainLevelOnFogSetupColor(float rainLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return rainLevel;
    }

    /**
     * Prevents thunder influence on fog color based on tweak context.
     */
    @ModifyExpressionValue(
        method = "setupColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getThunderLevel(F)F"
        )
    )
    private static float nt_world_weather$modifyThunderLevelOnFogSetupColor(float thunderLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return thunderLevel;
    }
}
