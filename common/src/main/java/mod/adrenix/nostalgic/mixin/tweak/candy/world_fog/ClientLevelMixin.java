package mod.adrenix.nostalgic.mixin.tweak.candy.world_fog;

import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin
{
    /**
     * Tracks the red sky color for void fog and applies void fog changes if needed.
     */
    @ModifyArg(
        index = 0,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double nt_world_fog$onSetSkyColorRed(double red)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.setSkyRed((float) red);
        else
            return red;

        return VoidFogRenderer.getSkyRed();
    }

    /**
     * Tracks the green sky color for void fog and applies void fog changes if needed.
     */
    @ModifyArg(
        index = 1,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double nt_world_fog$onSetSkyColorGreen(double green)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.setSkyGreen((float) green);
        else
            return green;

        return VoidFogRenderer.getSkyGreen();
    }

    /**
     * Tracks the blue sky color for void fog and applies void fog changes if needed.
     */
    @ModifyArg(
        index = 2,
        method = "getSkyColor",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;<init>(DDD)V"
        )
    )
    private double nt_world_fog$onSetSkyColorBlue(double blue)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.setSkyBlue((float) blue);
        else
            return blue;

        return VoidFogRenderer.getSkyBlue();
    }

    /**
     * Adds void fog particles to the client level if the correct conditions are met.
     */
    @Inject(
        method = "doAnimateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
        )
    )
    private void nt_world_fog$onAddBiomeParticles(int posX, int posY, int posZ, int range, RandomSource randomSource, Block block, BlockPos.MutableBlockPos blockPos, CallbackInfo callback)
    {
        if (VoidFogRenderer.isRendering())
            VoidFogRenderer.addParticles(randomSource);
    }
}
