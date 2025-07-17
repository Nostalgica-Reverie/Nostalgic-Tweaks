package mod.adrenix.nostalgic.forge.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin extends Gui
{
    /* Fake Constructor */

    private ForgeGuiMixin(Minecraft minecraft, ItemRenderer itemRenderer)
    {
        super(minecraft, itemRenderer);
    }

    /* Injections */

    /**
     * Checks if the health bar is flashing because health was regained. This effect was not present in the early days
     * of Minecraft.
     */
    @Inject(
        method = "renderHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;getHealth()F"
        )
    )
    private void nt_old_hud$onGetHealth(CallbackInfo callback, @Local Player player, @Share("isRegainBlink") LocalBooleanRef isRegainBlink)
    {
        int health = Mth.ceil(player.getHealth());

        if (!CandyTweak.BLINK_HEARTS_ON_INSTANT_EAT.get() && GameplayTweak.INSTANT_EAT.get() && health > this.lastHealth)
            isRegainBlink.set(true);
    }

    /**
     * Prevents highlighting of the hearts bar when health is regained based on tweak context.
     */
    @ModifyArg(
        method = "renderHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/client/gui/overlay/ForgeGui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"
        )
    )
    private boolean nt_old_hud$modifyFlashOnRenderHearts(boolean renderHighlights, @Share("isRegainBlink") LocalBooleanRef isRegainBlink)
    {
        if (!isRegainBlink.get())
            return renderHighlights;

        this.lastHealthTime -= 9001L;
        this.healthBlinkTime = 0L;

        return false;
    }
}
