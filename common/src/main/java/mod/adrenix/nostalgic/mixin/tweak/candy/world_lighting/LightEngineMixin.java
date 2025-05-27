package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.light.NostalgicDataLayer;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin
{
    /* Shadows */

    @Shadow @Final protected LightChunkGetter chunkSource;

    /* Injections */

    /**
     * Modifies returned light values from the client light engine to help simulate old light rendering.
     */
    @ModifyReturnValue(
        method = "getLightValue",
        at = @At("RETURN")
    )
    private int nt_world_lighting$getLightValue(int lightValue, BlockPos blockPos)
    {
        if (GameUtil.isOnIntegratedSeverThread() || ClassUtil.isNotInstanceOf(this.chunkSource, ClientChunkCache.class))
            return lightValue;

        LightLayer layer = ClassUtil.isInstanceOf(this, SkyLightEngine.class) ? LightLayer.SKY : LightLayer.BLOCK;

        if (this.chunkSource.getLevel() instanceof ClientLevel)
            return NostalgicDataLayer.getLightValue(layer, blockPos, lightValue);

        return lightValue;
    }
}
