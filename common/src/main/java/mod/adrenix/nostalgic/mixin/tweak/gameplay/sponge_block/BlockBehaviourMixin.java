package mod.adrenix.nostalgic.mixin.tweak.gameplay.sponge_block;

import mod.adrenix.nostalgic.helper.gameplay.SpongeBlockHelper;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin
{
    /**
     * Try to update water blocks around the sponge regardless of tweak state. This is because if the old sponge
     * absorption tweak was on, and then later turned off, the water won't update after a new sponge removal. This isn't
     * a huge issue since the water blocks can manually be updated, but for the convenience of the user, we will try to
     * update all water block physics for them while the mod is installed when a dry sponge block is broken.
     */
    @Inject(
        method = "onRemove",
        at = @At("HEAD")
    )
    private void nt_sponge_block$onRemove(BlockState prevState, Level level, BlockPos blockPos, BlockState newState, boolean movedByPiston, CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get() && prevState.is(Blocks.SPONGE))
            SpongeBlockHelper.tryRestoreWater(level, blockPos, prevState);
    }
}
