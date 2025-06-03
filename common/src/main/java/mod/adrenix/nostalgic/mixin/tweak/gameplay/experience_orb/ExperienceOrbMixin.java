package mod.adrenix.nostalgic.mixin.tweak.gameplay.experience_orb;

import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity
{
    /* Shadows */

    @Shadow private Player followingPlayer;

    @Shadow
    protected abstract void scanForEntities();

    @Shadow
    public abstract void playerTouch(Player player);

    /* Fake Constructor */

    private ExperienceOrbMixin(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Checks for the closest player and immediately goes to that player.
     */
    @Inject(
        method = "<init>(Lnet/minecraft/world/level/Level;DDDI)V",
        at = @At("RETURN")
    )
    private void nt_experience_orb$onTickBeforeMovement(CallbackInfo callback)
    {
        if (this.level().isClientSide || !GameplayTweak.IMMEDIATE_EXPERIENCE_PICKUP.get())
            return;

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.scanForEntities();

        if (this.followingPlayer != null && !(this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying()))
        {
            this.playerTouch(this.followingPlayer);
            this.followingPlayer.takeXpDelay = 0;
        }
    }
}
