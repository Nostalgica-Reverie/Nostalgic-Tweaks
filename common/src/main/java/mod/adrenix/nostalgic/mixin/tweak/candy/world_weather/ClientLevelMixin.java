package mod.adrenix.nostalgic.mixin.tweak.candy.world_weather;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Prevents rain influence on sky darkening based on tweak context.
     */
    @ModifyExpressionValue(
        method = "getSkyDarken",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
        )
    )
    private float nt_world_weather$modifyRainLevelOnSkyDarken(float rainLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return rainLevel;
    }

    /**
     * Prevents thunder influence on sky darkening based on tweak context.
     */
    @ModifyExpressionValue(
        method = "getSkyDarken",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getThunderLevel(F)F"
        )
    )
    private float nt_world_weather$modifyThunderLevelOnSkyDarken(float thunderLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return thunderLevel;
    }

    /**
     * Prevents rain influence on sky color based on tweak context.
     */
    @ModifyExpressionValue(
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
        )
    )
    private float nt_world_weather$modifyRainLevelOnSkyColor(float rainLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return rainLevel;
    }

    /**
     * Prevents thunder influence on sky color based on tweak context.
     */
    @ModifyExpressionValue(
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getThunderLevel(F)F"
        )
    )
    private float nt_world_weather$modifyThunderLevelOnSkyColor(float thunderLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return thunderLevel;
    }

    /**
     * Prevents rain influence on cloud color based on tweak context.
     */
    @ModifyExpressionValue(
        method = "getCloudColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
        )
    )
    private float nt_world_weather$modifyRainLevelOnCloudColor(float rainLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return rainLevel;
    }

    /**
     * Prevents thunder influence on cloud color based on tweak context.
     */
    @ModifyExpressionValue(
        method = "getCloudColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getThunderLevel(F)F"
        )
    )
    private float nt_world_weather$modifyThunderLevelOnCloudColor(float thunderLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return thunderLevel;
    }
}
