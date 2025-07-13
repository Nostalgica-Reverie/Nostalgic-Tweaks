package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import mod.adrenix.nostalgic.helper.gameplay.FoodHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /* Shadows */

    @Shadow protected ItemStack useItem;

    @Shadow
    public abstract ItemStack getUseItem();

    /* Injections */

    /**
     * Prevents the generic food consumption sound when instantaneous eating is enabled.
     */
    @WrapWithCondition(
        method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"
        )
    )
    private boolean nt_food_health$shouldPlayConsumedFoodSound(Level level, Player player, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch)
    {
        return !FoodHelper.isInstantaneousEdible(this.getUseItem());
    }

    /**
     * Prevents effects being applied to the player based on tweak context.
     */
    @WrapWithCondition(
        method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;addEatEffect(Lnet/minecraft/world/food/FoodProperties;)V"
        )
    )
    private boolean nt_food_health$shouldAddFoodEffects(LivingEntity instance, FoodProperties foodProperties, Level level, ItemStack food)
    {
        return !FoodHelper.isInstantaneousEdible(food) || !GameplayTweak.PREVENT_INSTANT_EAT_EFFECTS.get();
    }

    /**
     * Prevents the hunger effect from being applied to entities if it is disabled.
     */
    @ModifyReturnValue(
        method = "canBeAffected",
        at = @At("RETURN")
    )
    private boolean nt_food_health$shouldAddHungerEffect(boolean canBeAffected, MobEffectInstance effectInstance)
    {
        if (GameplayTweak.PREVENT_HUNGER_EFFECT.get() && effectInstance.getEffect() == MobEffects.HUNGER)
            return false;

        return canBeAffected;
    }

    /**
     * Sets the use duration to one if the item is an instantaneous edible item so that on the next tick the item is
     * immediately consumed.
     */
    @ModifyExpressionValue(
        method = "startUsingItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/ItemStack;getUseDuration(Lnet/minecraft/world/entity/LivingEntity;)I"
        )
    )
    private int nt_food_health$modifyUseDurationOnStartUsingItem(int useDuration)
    {
        if (FoodHelper.isInstantaneousEdible(this.useItem))
            return 1;

        return useDuration;
    }

    /**
     * Prevents the item use effects from triggering on an item update when the use item is an instantaneous edible
     * item.
     */
    @WrapWithCondition(
        method = "updateUsingItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;triggerItemUseEffects(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private boolean nt_food_health$shouldTriggerItemUseEffectsOnUpdate(LivingEntity entity, ItemStack itemStack, int amount)
    {
        return !FoodHelper.isInstantaneousEdible(this.useItem);
    }

    /**
     * Prevents the item use effects from triggering on item use completion when the use item is an instantaneous edible
     * item.
     */
    @WrapWithCondition(
        method = "completeUsingItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;triggerItemUseEffects(Lnet/minecraft/world/item/ItemStack;I)V"
        )
    )
    private boolean nt_food_health$shouldTriggerItemUseEffectsOnComplete(LivingEntity entity, ItemStack ItemStack, int amount)
    {
        return !FoodHelper.isInstantaneousEdible(this.useItem);
    }
}
