package mod.adrenix.nostalgic.mixin.tweak.candy.world_weather;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /**
     * Forces rendering of rain or snow based on tweak context.
     */
    @ModifyExpressionValue(
        method = "renderSnowAndRain",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
        )
    )
    private float nt_world_weather$modifyGetRainLevelOnRender(float rainLevel)
    {
        if (CandyTweak.ALWAYS_RENDER_WEATHER.get())
            return 1.0F;

        return rainLevel;
    }

    /**
     * Forcefully changes biome precipitation based on tweak context.
     */
    @ModifyExpressionValue(
        method = "renderSnowAndRain",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
        )
    )
    private Biome.Precipitation nt_world_weather$modifyPrecipitationForWeatherOnRender(Biome.Precipitation precipitation)
    {
        return switch (CandyTweak.WEATHER_TYPE.get())
        {
            case BIOME -> precipitation;
            case RAIN -> Biome.Precipitation.RAIN;
            case SNOW -> Biome.Precipitation.SNOW;
        };
    }

    /**
     * Forcefully changes biome precipitation based on tweak context.
     */
    @ModifyExpressionValue(
        method = "tickRain",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
        )
    )
    private Biome.Precipitation nt_world_weather$modifyPrecipitationOnTickRain(Biome.Precipitation precipitation)
    {
        return switch (CandyTweak.WEATHER_TYPE.get())
        {
            case BIOME -> precipitation;
            case RAIN -> Biome.Precipitation.RAIN;
            case SNOW -> Biome.Precipitation.SNOW;
        };
    }

    /**
     * Prevents weather influencing the sky based on tweak context.
     */
    @ModifyExpressionValue(
        method = "renderSky",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F"
        )
    )
    private float nt_world_weather$modifyGetRainLevelForSky(float rainLevel)
    {
        if (CandyTweak.PREVENT_WEATHER_INFLUENCE.get())
            return 0.0F;

        return rainLevel;
    }
}
