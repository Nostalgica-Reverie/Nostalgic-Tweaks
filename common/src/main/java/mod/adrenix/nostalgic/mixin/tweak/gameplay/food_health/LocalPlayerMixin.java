package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import mod.adrenix.nostalgic.helper.gameplay.FoodHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer
{
    /* Fake Constructor */

    private LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile)
    {
        super(clientLevel, gameProfile);
    }

    /* Injections */

    /**
     * Prevents movement "stutter" and allows the player to sprint while the player is consuming an instantaneous edible
     * and moving at the same time.
     */
    @ModifyExpressionValue(
        method = { "aiStep", "canStartSprinting" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"
        )
    )
    private boolean nt_food_health$shouldModifyImpulseOnInstantEat(boolean isUsingItem)
    {
        if (FoodHelper.isInstantaneousEdible(this.useItem))
            return false;

        return isUsingItem;
    }
}
