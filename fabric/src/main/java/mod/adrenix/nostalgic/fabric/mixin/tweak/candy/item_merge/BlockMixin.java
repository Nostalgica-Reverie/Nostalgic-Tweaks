package mod.adrenix.nostalgic.fabric.mixin.tweak.candy.item_merge;

import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.helper.candy.ItemHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Block.class)
public abstract class BlockMixin
{
    /**
     * Splits up the item stack spawned from breaking blocks.
     */
    @Inject(
        method = "popResource(Lnet/minecraft/world/level/Level;Ljava/util/function/Supplier;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
        )
    )
    private static void nt_fabric_item_merge$wrapAddFreshEntity(Level level, Supplier<ItemEntity> supplier, ItemStack itemStack, CallbackInfo callback, @Local ItemEntity itemEntity)
    {
        if (CandyTweak.OLD_ITEM_MERGING.get())
            ItemHelper.splitEntity(level, itemEntity, level::addFreshEntity);
    }
}
