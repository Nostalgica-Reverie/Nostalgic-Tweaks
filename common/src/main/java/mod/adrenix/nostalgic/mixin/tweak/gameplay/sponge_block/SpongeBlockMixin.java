package mod.adrenix.nostalgic.mixin.tweak.gameplay.sponge_block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.helper.gameplay.SpongeBlockHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin
{
    /**
     * Immediately absorb surrounding water and waterlogged blocks after a sponge block is placed.
     */
    @WrapOperation(
        method = "tryAbsorbWater",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/SpongeBlock;removeWaterBreadthFirstSearch(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"
        )
    )
    private boolean nt_sponge_block$skipVanillaWaterRemoval(SpongeBlock sponge, Level level, BlockPos blockPos, Operation<Boolean> original)
    {
        if (GameplayTweak.OLD_SPONGE_ABSORPTION.get())
        {
            SpongeBlockHelper.tryAbsorbWater(level, blockPos);
            return false;
        }

        return original.call(sponge, level, blockPos);
    }
}
