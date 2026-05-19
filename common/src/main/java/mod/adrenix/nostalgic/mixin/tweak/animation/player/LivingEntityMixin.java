package mod.adrenix.nostalgic.mixin.tweak.animation.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.mixin.duck.CameraPitching;
import mod.adrenix.nostalgic.tweak.config.AnimationTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /**
     * Brings back the old backwards walking animation.
     */
    @ModifyExpressionValue(
        method = "tick",
        slice = @Slice(
            to = @At(
                value = "FIELD",
                target = "Lnet/minecraft/world/entity/LivingEntity;attackAnim:F"
            )
        ),
        at = @At(
            value = "CONSTANT",
            args = "floatValue=180.0F"
        )
    )
    private float nt_player_animation$modifyBackwardsRotation(float rotation)
    {
        if (ClassUtil.isNotInstanceOf(this, Player.class) && ClassUtil.isNotInstanceOf(this, Mob.class))
            return rotation;

        return (AnimationTweak.OLD_BACKWARD_WALKING.get() || AnimationTweak.OLD_MOB_HEAD_BODY_TURN.get()) ? 0.0F : rotation;
    }

    /**
     * Sets the previous camera pitch at the correct time during entity tick updating.
     */
    @Inject(
        method = "baseTick",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;isAlive()Z"
        )
    )
    private void nt_player_animation$setPrevCameraPitch(CallbackInfo callback)
    {
        if (this instanceof CameraPitching player)
            player.nt$setPrevCameraPitch(player.nt$getCameraPitch());
    }
}
