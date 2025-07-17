package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.gameplay.FoodHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.world.ItemUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity
{
    /* Fake Constructor */

    private PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Restores the player's health after eating food.
     */
    @Inject(
        method = "eat",
        at = @At("HEAD")
    )
    private void nt_food_health$onPlayerEat(Level level, ItemStack itemStack, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> callback)
    {
        if (GameplayTweak.DISABLE_HUNGER.get() && ItemUtil.isEdible(itemStack))
        {
            FoodProperties food = itemStack.get(DataComponents.FOOD);
            int healAmount = NullableResult.getOrElse(food, 0, FoodProperties::nutrition);

            if (GameplayTweak.CUSTOM_FOOD_HEALTH.get().containsItem(itemStack))
                healAmount = GameplayTweak.CUSTOM_FOOD_HEALTH.get().valueFrom(itemStack);

            this.heal(healAmount);
        }
    }

    /**
     * Allows the player to eat if their current health is below their max health.
     */
    @ModifyReturnValue(
        method = "canEat",
        at = @At("RETURN")
    )
    private boolean nt_food_health$canPlayerEat(boolean playerCanEat, boolean canAlwaysEat)
    {
        if (GameplayTweak.DISABLE_HUNGER.get() && !canAlwaysEat)
            return Mth.ceil(this.getHealth()) < Mth.ceil(this.getMaxHealth());

        return playerCanEat;
    }

    /**
     * Prevents the "burp" sound that plays after a player finishes eating an item.
     */
    @WrapWithCondition(
        method = "eat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    private boolean nt_food_health$shouldPlayBurpSound(Level level, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        return !FoodHelper.isInstantaneousEdible(this.getUseItem());
    }
}
