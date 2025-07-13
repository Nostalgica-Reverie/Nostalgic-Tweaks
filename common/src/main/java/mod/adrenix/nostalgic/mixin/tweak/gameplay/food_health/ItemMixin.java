package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.gameplay.FoodHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin
{
    /**
     * Prevents the player from starting the use of an item that is edible.
     */
    @WrapWithCondition(
        method = "use",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Player;startUsingItem(Lnet/minecraft/world/InteractionHand;)V"
        )
    )
    private boolean nt_food_health$shouldStartUsingItem(Player player, InteractionHand hand)
    {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (FoodHelper.isInstantaneousEdible(itemInHand))
        {
            itemInHand.finishUsingItem(player.level(), player);

            return false;
        }

        return true;
    }
}
