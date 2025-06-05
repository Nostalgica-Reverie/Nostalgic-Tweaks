package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.adrenix.nostalgic.helper.gameplay.AnimalSpawnHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Chicken.class)
public abstract class ChickenMixin extends Animal
{
    /* Fake Constructor */

    private ChickenMixin(EntityType<? extends Animal> entityType, Level level)
    {
        super(entityType, level);
    }

    /* Injections */

    /**
     * Changes the behavior of chicken entity removal. This is needed for chickens that have jockeys attached.
     */
    @ModifyReturnValue(
        method = "removeWhenFarAway",
        at = @At("RETURN")
    )
    private boolean nt_animal_spawn$modifyRemoveWhenFarAway(boolean removeWhenFarAway, double distanceToClosestPlayer)
    {
        if (GameplayTweak.OLD_ANIMAL_SPAWNING.get() && AnimalSpawnHelper.isInList(EntityType.CHICKEN))
            return super.removeWhenFarAway(distanceToClosestPlayer);

        return removeWhenFarAway;
    }
}
