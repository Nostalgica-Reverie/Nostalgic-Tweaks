package mod.adrenix.nostalgic.mixin.tweak.gameplay.mechanics_block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.level.block.BedBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin
{
    /**
     * Disables the reduction in fall damage when an entity falls on a bed block.
     */
    @ModifyExpressionValue(
        method = "updateEntityAfterFallOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;isSuppressingBounce()Z"
        )
    )
    private boolean nt_mechanics_block$modifySuppressingBounceFlag(boolean isSuppressingBounce)
    {
        return GameplayTweak.DISABLE_BED_BOUNCE.get() || isSuppressingBounce;
    }
}
