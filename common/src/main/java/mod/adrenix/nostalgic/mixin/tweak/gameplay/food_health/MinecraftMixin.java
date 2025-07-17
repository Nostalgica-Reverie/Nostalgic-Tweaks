package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.gameplay.FoodHelper;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    /* Shadows */

    @Shadow @Nullable public LocalPlayer player;

    /* Injections */

    /**
     * Prevents the reequipped animation after the player eats something instantaneously.
     */
    @WrapWithCondition(
        method = "startUseItem",
        slice = @Slice(
            from = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/item/ItemStack;isItemEnabled(Lnet/minecraft/world/flag/FeatureFlagSet;)Z"
            )
        ),
        at = @At(
            ordinal = 1,
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;itemUsed(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_food_health$shouldSetItemAsUsed(ItemInHandRenderer itemInHandRenderer, InteractionHand hand)
    {
        if (this.player == null)
            return true;

        ItemStack itemInHand = this.player.getItemInHand(hand);

        return !ItemUtil.isEdible(itemInHand) || !FoodHelper.isInstantaneousEdible(itemInHand);
    }
}
