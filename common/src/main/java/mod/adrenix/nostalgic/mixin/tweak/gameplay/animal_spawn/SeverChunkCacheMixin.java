package mod.adrenix.nostalgic.mixin.tweak.gameplay.animal_spawn;

import com.llamalad7.mixinextras.sugar.Local;
import mod.adrenix.nostalgic.helper.gameplay.AnimalSpawnHelper;
import mod.adrenix.nostalgic.tweak.config.GameplayTweak;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkCache.class)
public abstract class SeverChunkCacheMixin
{
    /* Shadows */

    @Shadow @Final ServerLevel level;
    @Shadow private boolean spawnFriendlies;

    /* Injections */

    /**
     * Does necessary logic before looping over all loaded chunks and performing old animal spawn instructions.
     */
    @Inject(
        method = "tickChunks",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Ljava/util/Collections;shuffle(Ljava/util/List;)V"
        )
    )
    private void nt_animal_spawn$onBeforeTickAllChunks(CallbackInfo callback)
    {
        if (GameplayTweak.OLD_ANIMAL_SPAWNING.get())
            AnimalSpawnHelper.tickLevel(this.level, this.spawnFriendlies);
    }

    /**
     * Performs old animal spawning logic based on tweak context.
     */
    @Inject(
        method = "tickChunks",
        at = @At(
            shift = At.Shift.AFTER,
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/NaturalSpawner;spawnForChunk(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnState;ZZZ)V"
        )
    )
    private void nt_animal_spawn$onSpawnForChunk(CallbackInfo callback, @Local LevelChunk chunk)
    {
        if (GameplayTweak.OLD_ANIMAL_SPAWNING.get())
            AnimalSpawnHelper.tickChunk(chunk, this.level, this.spawnFriendlies);
    }
}
