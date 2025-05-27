package mod.adrenix.nostalgic.mixin.tweak.candy.world_lighting;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mod.adrenix.nostalgic.helper.candy.light.LightingHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.data.Pair;
import net.minecraft.client.Camera;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ViewArea;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.SectionPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin
{
    /* Shadows */

    @Shadow @Nullable private ClientLevel level;
    @Shadow @Nullable private ViewArea viewArea;

    /* Injections */

    /**
     * Relighting queues are slowly emptied at the start of each render pass of the level. This is done here rather than
     * on each tick to prevent visual issues when relighting.
     */
    @Inject(
        method = "renderLevel",
        at = @At("HEAD")
    )
    private void nt_world_lighting$onRenderLevel(CallbackInfo callback)
    {
        if (ModTweak.ENABLED.get())
            LightingHelper.checkRelightQueues();
    }

    /**
     * Applies world relighting to all chunks when needed.
     */
    @Inject(
        method = "allChanged",
        at = @At("HEAD")
    )
    private void nt_world_lighting$onAllChanged(CallbackInfo callback)
    {
        if (!LightingHelper.RELIGHT_ALL_CHUNKS.get() || ModTracker.SODIUM.isInstalled() || this.level == null || this.viewArea == null)
            return;

        for (ChunkRenderDispatcher.RenderChunk renderChunk : this.viewArea.chunks)
        {
            if (renderChunk == null || renderChunk.getCompiledChunk().hasNoRenderableLayers())
                continue;

            long packedPos = SectionPos.of(renderChunk.getOrigin()).chunk().toLong();

            LightingHelper.PACKED_RELIGHT_QUEUE.add(new Pair<>(packedPos, (byte) 1));
        }

        LightingHelper.RELIGHT_ALL_CHUNKS.disable();
    }

    /**
     * Enqueues chunk relighting if requested by the old lighting engine.
     */
    @Inject(
        method = "compileChunks",
        at = @At("HEAD")
    )
    private void nt_world_lighting$onCompileSections(Camera camera, CallbackInfo callback)
    {
        boolean isRelightNeeded = CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingHelper.isRelightCheckEnqueued();

        if (!isRelightNeeded || this.viewArea == null || this.level == null || ModTracker.SODIUM.isInstalled())
            return;

        LightingHelper.CHUNK_RELIGHT_QUEUE.clear();

        for (ChunkRenderDispatcher.RenderChunk renderChunk : this.viewArea.chunks)
        {
            if (renderChunk == null || renderChunk.getCompiledChunk().hasNoRenderableLayers())
                continue;

            SectionPos sectionPos = SectionPos.of(renderChunk.getOrigin());

            if (this.level.getLightEngine().lightOnInSection(sectionPos))
                LightingHelper.CHUNK_RELIGHT_QUEUE.add(sectionPos.asLong());
        }
    }

    /**
     * Changes chunk update priority to "none" so that lag spikes are prevented during world relighting.
     */
    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(
        method = "compileChunks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"
        )
    )
    private <T> T nt_world_lighting$onGetCompilePriority(T chunkPriority)
    {
        if (ModTracker.SODIUM.isInstalled())
            return chunkPriority;

        return CandyTweak.ROUND_ROBIN_RELIGHT.get() && LightingHelper.isRelightCheckEnqueued() ? (T) PrioritizeChunkUpdates.NONE : chunkPriority;
    }

    /**
     * Marks the world relighting as finished.
     */
    @Inject(
        method = "compileChunks",
        at = @At("RETURN")
    )
    private void nt_world_lighting$onFinishCompileSections(Camera camera, CallbackInfo callback)
    {
        if (LightingHelper.isRelightCheckEnqueued() && !ModTracker.SODIUM.isInstalled())
            LightingHelper.setRelightingAsFinished();
    }
}
