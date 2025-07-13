package mod.adrenix.nostalgic.mixin.tweak.gameplay.food_health;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
    /* Shadows */

    @Shadow
    public abstract ItemStack getUseItem();

    /* Injections */

    /**
     * Prevents the generic food consumption sound when instantaneous eating is enabled.
     */
    @WrapWithCondition(
        method = "eat",
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
     * Prevents the hunger effect from being applied to entities if it is disabled.
     */
    @ModifyReturnValue(
        method = "canBeAffected",
        at = @At("RETURN")
    )
    private boolean nt_food_health$shouldAddFoodEffect(boolean canBeAffected, MobEffectInstance effectInstance)
    {
        if (GameplayTweak.PREVENT_HUNGER_EFFECT.get() && effectInstance.getEffect() == MobEffects.HUNGER)
            return false;

        return canBeAffected;
    }
}
