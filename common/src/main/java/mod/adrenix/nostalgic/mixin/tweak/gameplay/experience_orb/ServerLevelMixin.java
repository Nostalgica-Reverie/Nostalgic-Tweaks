package mod.adrenix.nostalgic.mixin.tweak.gameplay.experience_orb;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin
{
    /**
     * Prevents the server level from spawning experience orbs.
     */
    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(
        method = "addEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addNewEntity(Lnet/minecraft/world/level/entity/EntityAccess;)Z"
        )
    )
    private <T extends EntityAccess> boolean nt_experience_orb$shouldAddExperienceEntity(PersistentEntitySectionManager<T> manager, T entity, Operation<Boolean> operation)
    {
        if (GameplayTweak.DISABLE_ORB_SPAWN.get() && entity instanceof ExperienceOrb)
            return false;

        return operation.call(manager, entity);
    }

    /**
     * Prevents logger console spam of experience orb being removed before it is added to the level.
     */
    @WrapWithCondition(
        method = "addEntity",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"
        )
    )
    private boolean nt_experience_orb$shouldWarnConsoleOnMissingEntity(Logger logger, String message, Object args, Entity entity)
    {
        return !GameplayTweak.IMMEDIATE_EXPERIENCE_PICKUP.get() || !(entity instanceof ExperienceOrb);
    }
}
