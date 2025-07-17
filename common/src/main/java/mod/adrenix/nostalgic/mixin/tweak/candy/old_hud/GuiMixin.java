package mod.adrenix.nostalgic.mixin.tweak.candy.old_hud;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.asset.ModSprite;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    /* Shadows */

    @Shadow private int lastHealth;
    @Shadow private long lastHealthTime;
    @Shadow private long healthBlinkTime;

    /* Injections */

    /**
     * Checks if the health bar is flashing because health was regained. This effect was not present in the early days
     * of Minecraft.
     */
    @Inject(
        method = "renderPlayerHealth",
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
        method = "renderPlayerHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"
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

    /**
     * Changes the offhand left slot texture to the Adventure Craft style.
     */
    @ModifyArg(
        method = "renderItemHotbar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
        ),
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private ResourceLocation nt_old_hud$modifyLeftOffhandSprite(ResourceLocation sprite)
    {
        return CandyTweak.ADVENTURE_CRAFT_OFFHAND.get() ? ModSprite.ADVENTURE_CRAFT_OFFHAND_LEFT_SLOT : sprite;
    }

    /**
     * Moves the left offhand horizontal position to the left by one if using the Adventure Craft style.
     */
    @ModifyArg(
        index = 1,
        method = "renderItemHotbar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
        ),
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private int nt_old_hud$modifyLeftOffhandHorizontal(int x)
    {
        if (CandyTweak.ADVENTURE_CRAFT_OFFHAND.get())
            return x - 1 + CandyTweak.LEFT_OFFHAND_OFFSET.get();

        return x + CandyTweak.LEFT_OFFHAND_OFFSET.get();
    }

    /**
     * Changes the offhand right slot texture to the Adventure Craft style.
     */
    @ModifyArg(
        method = "renderItemHotbar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
        ),
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private ResourceLocation nt_old_hud$modifyRightOffhandSprite(ResourceLocation sprite)
    {
        return CandyTweak.ADVENTURE_CRAFT_OFFHAND.get() ? ModSprite.ADVENTURE_CRAFT_OFFHAND_RIGHT_SLOT : sprite;
    }

    /**
     * Moves the right offhand horizontal position to the right by one if using the Adventure Craft style.
     */
    @ModifyArg(
        index = 1,
        method = "renderItemHotbar",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
            )
        ),
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    private int nt_old_hud$modifyRightOffhandHorizontal(int x)
    {
        if (CandyTweak.ADVENTURE_CRAFT_OFFHAND.get())
            return x + 1 + CandyTweak.RIGHT_OFFHAND_OFFSET.get();

        return x + CandyTweak.RIGHT_OFFHAND_OFFSET.get();
    }

    /**
     * Change the left offhand item offset.
     */
    @ModifyArg(
        index = 1,
        method = "renderItemHotbar",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private int nt_old_hud$modifyLeftOffhandItemOffset(int x)
    {
        return x + CandyTweak.LEFT_OFFHAND_OFFSET.get();
    }

    /**
     * Change the right offhand item offset.
     */
    @ModifyArg(
        index = 1,
        method = "renderItemHotbar",
        at = @At(
            ordinal = 2,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/client/DeltaTracker;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private int nt_old_hud$modifyRightOffhandItemOffset(int x)
    {
        return x + CandyTweak.RIGHT_OFFHAND_OFFSET.get();
    }
}
