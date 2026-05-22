package mod.adrenix.nostalgic.mixin.tweak.gameplay.sponge_block;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.gameplay.SpongeBlockHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin
{
    /**
     * Prevents water flowing into an area controlled by a classic sponge block.
     */
    @ModifyReturnValue(
        method = "canSpreadTo",
        at = @At("RETURN")
    )
    private boolean nt_sponge_block$modifyCanSpreadTo(boolean canSpreadTo, BlockGetter level, BlockPos fromPos, BlockState fromBlockState, Direction direction, BlockPos toPos, BlockState toBlockState, FluidState toFluidState, Fluid fluid)
    {
        if (!GameplayTweak.OLD_SPONGE_ABSORPTION.get() || !fluid.defaultFluidState().is(FluidTags.WATER))
            return canSpreadTo;

        return canSpreadTo && !SpongeBlockHelper.isWaterFlowingTowardsSponge(level, fromPos, toPos);
    }
}
