package mod.adrenix.nostalgic.mixin.tweak.candy.block_hitbox;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.GameUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClipContext.Block.class)
public abstract class ClipContextBlockMixin
{
    /**
     * Setting block voxel shape to a full block allows a player's client hit results so that placing, breaking, or
     * picking blocks properly targets the full block outline. Because this could give a player an unfair advantage on a
     * server without the mod, the client must get permission from a server with N.T installed to use this tweak.
     */
    @ModifyReturnValue(
        method = "get",
        at = @At("RETURN")
    )
    private VoxelShape nt_block_hitbox$modifyShapeGetter(VoxelShape voxelShape, BlockState blockState)
    {
        ClipContext.Block context = (ClipContext.Block) (Object) this;

        if (NostalgicTweaks.isMixinEarly() || GameUtil.isOnIntegratedSeverThread() || !context.equals(ClipContext.Block.OUTLINE))
            return voxelShape;

        boolean allowCollision = CandyTweak.APPLY_FULL_BLOCK_COLLISIONS.get();
        boolean allowTarget = CandyTweak.APPLY_FULL_HITBOX_TARGETING.get();
        boolean hasTarget = CandyTweak.FULL_BLOCK_OUTLINES.get().containsBlock(blockState);
        boolean hasCollision = CandyTweak.FULL_BLOCK_COLLISIONS.get().containsBlock(blockState);

        if ((allowTarget && hasTarget) || (allowCollision && hasCollision))
            return Shapes.block();

        return voxelShape;
    }
}
