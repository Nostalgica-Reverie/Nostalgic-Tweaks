package mod.adrenix.nostalgic.mixin.tweak.candy.item_display;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.helper.candy.ItemHelper;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin
{
    /**
     * Prevents drawing of the item bar decoration background.
     */
    @WrapOperation(
        method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
        at = @At(
            ordinal = 0,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V"
        )
    )
    private void nt_item_display$wrapDecorationBackground(GuiGraphics graphics, RenderType renderType, int minX, int minY, int maxX, int maxY, int color, Operation<Void> operation, Font font, ItemStack itemStack)
    {
        if (ItemHelper.hasOldDecoration(itemStack))
            return;

        operation.call(graphics, renderType, minX, minY, maxX, maxY, color);
    }

    /**
     * Prevents the original draw call of the foreground item bar decoration and then replaces it with the old style
     * item decoration rendering.
     */
    @WrapOperation(
        method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;fill(Lnet/minecraft/client/renderer/RenderType;IIIII)V"
        )
    )
    private void nt_item_display$wrapDecorationForeground(GuiGraphics graphics, RenderType renderType, int minX, int minY, int maxX, int maxY, int color, Operation<Void> operation, Font font, ItemStack itemStack, int x, int y)
    {
        if (!ItemHelper.hasOldDecoration(itemStack))
        {
            operation.call(graphics, renderType, minX, minY, maxX, maxY, color);
            return;
        }

        double health = (double) itemStack.getDamageValue() / (double) itemStack.getMaxDamage();
        double healthRemaining = ((double) itemStack.getDamageValue() * 255.0D) / (double) itemStack.getMaxDamage();

        int width = Math.round(13.0F - (float) health * 13.0F);
        int damage = (int) Math.round(255.0D - healthRemaining);

        int damageForegroundColor = 0xFF000000 | (255 - damage << 16 | damage << 8);
        int damageBackgroundColor = 0xFF000000 | ((255 - damage) / 4 << 16 | 0x3F00);

        int startX = x + 2;
        int startY = y + 13;

        graphics.fill(RenderType.guiOverlay(), startX, startY, startX + 13, startY + 2, 0xFF000000);
        graphics.fill(RenderType.guiOverlay(), startX, startY, startX + 12, startY + 1, damageBackgroundColor);
        graphics.fill(RenderType.guiOverlay(), startX, startY, startX + width, startY + 1, damageForegroundColor);
    }
}
