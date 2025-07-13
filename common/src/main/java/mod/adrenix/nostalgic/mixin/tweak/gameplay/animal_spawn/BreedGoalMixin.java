package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import mod.adrenix.nostalgic.helper.gameplay.AnimalSpawnHelper;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.animal.Animal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreedGoal.class)
public abstract class BreedGoalMixin
{
    /* Shadows */

    @Shadow @Final protected Animal animal;
    @Shadow @Nullable protected Animal partner;

    /* Injections */

    /**
     * Prevents a baby's parents from despawning depending on tweak context.
     */
    @Inject(
        method = "breed",
        at = @At("HEAD")
    )
    private void nt_animal_spawn$onBreed(CallbackInfo callback)
    {
        if (!AnimalSpawnHelper.isPersistent(this.animal))
            return;

        this.animal.setPersistenceRequired();

        if (this.partner != null)
            this.partner.setPersistenceRequired();
    }
}
