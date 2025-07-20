package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.tutorial.Tutorial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /**
     * Prevents display of the social interactions toast when joining a multiplayer server for the first time.
     */
    @WrapWithCondition(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/tutorial/Tutorial;addTimedToast(Lnet/minecraft/client/gui/components/toasts/TutorialToast;I)V"
        )
    )
    private boolean nt_old_hud$shouldAddSocialInteractionsToast(Tutorial tutorial, TutorialToast toast, int durationTicks)
    {
        return !CandyTweak.HIDE_TUTORIAL_TOASTS.get();
    }
}
