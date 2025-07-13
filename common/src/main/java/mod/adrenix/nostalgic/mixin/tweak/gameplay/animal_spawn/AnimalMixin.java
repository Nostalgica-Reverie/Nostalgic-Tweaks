package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.gameplay.AnimalSpawnHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import mod.adrenix.nostalgic.util.common.ClassUtil;
import mod.adrenix.nostalgic.util.common.data.FlagHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin extends Mob
{
    /* Fake Constructor */

    private AnimalMixin(EntityType<? extends Mob> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Prevents mobs from consuming food items to start breeding.
     */
    @ModifyExpressionValue(
        method = "mobInteract",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Animal;isFood(Lnet/minecraft/world/item/ItemStack;)Z"
        )
    )
    private boolean nt_animal_spawn$modifyIsFoodOnMobInteract(boolean isFood)
    {
        return !GameplayTweak.DISABLE_ANIMAL_BREEDING.get() && isFood;
    }

    /**
     * Changes the behavior of animal removal.
     */
    @ModifyReturnValue(
        method = "removeWhenFarAway",
        at = @At("RETURN")
    )
    private boolean nt_animal_spawn$modifyRemoveWhenFarAway(boolean removeWhenFarAway)
    {
        if (!GameplayTweak.OLD_ANIMAL_SPAWNING.get() || !AnimalSpawnHelper.isInList(this.getType()))
            return removeWhenFarAway;

        if (GameplayTweak.KEEP_BABY_ANIMAL_WHILE_OLD_SPAWN.get() && this.isPersistenceRequired())
            return removeWhenFarAway;

        FlagHolder leashed = FlagHolder.off();
        FlagHolder saddled = FlagHolder.off();
        FlagHolder tamed = FlagHolder.off();

        if (this.mayBeLeashed())
            leashed.enable();

        if (this instanceof Saddleable saddleable && saddleable.isSaddled())
            saddled.enable();

        ClassUtil.cast(this, TamableAnimal.class).ifPresent(tamable -> {
            if (tamable.isTame())
                tamed.enable();
        });

        if (!leashed.get() && !saddled.get() && !tamed.get())
            return true;

        return removeWhenFarAway;
    }

    /**
     * Set the baby animal persistence flag to {@code true} so the animal can't be removed when its current chunk
     * unloads.
     */
    @Inject(
        method = "finalizeSpawnChildFromBreeding",
        at = @At("HEAD")
    )
    private void nt_animal_spawn$onFinalizeSpawnChildFromBreeding(ServerLevel level, Animal animal, AgeableMob baby, CallbackInfo callback)
    {
        if (AnimalSpawnHelper.isPersistent(baby))
            baby.setPersistenceRequired();
    }
}
