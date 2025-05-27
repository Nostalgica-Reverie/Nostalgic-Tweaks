package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.candy.light.NostalgicDataLayer;
import mod.adrenix.nostalgic.util.client.GameUtil;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LayerLightSectionStorage.class)
public abstract class LayerLightSectionStorageMixin
{
    /* Shadows */

    @Shadow @Final protected LightChunkGetter chunkSource;

    /* Injections */

    /**
     * Returns a wrapped data layer with custom mod data to help simulate old light rendering. This wrapper modifies the
     * abstract section storage instead of the abstract light engine to support Flywheel.
     */
    @ModifyReturnValue(
        method = "getDataLayerData",
        at = @At("RETURN")
    )
    public DataLayer nt_world_lighting$getStorageLightValue(DataLayer original, long sectionPos)
    {
        if (GameUtil.isOnIntegratedSeverThread() || ClassUtil.isNotInstanceOf(this.chunkSource, ClientChunkCache.class) || original == null)
            return original;

        boolean isSkyEngine = ClassUtil.isInstanceOf(this, SkyLightSectionStorage.class);

        if (this.chunkSource.getLevel() instanceof ClientLevel)
            return new NostalgicDataLayer(original, isSkyEngine ? LightLayer.SKY : LightLayer.BLOCK, sectionPos);

        return original;
    }
}
