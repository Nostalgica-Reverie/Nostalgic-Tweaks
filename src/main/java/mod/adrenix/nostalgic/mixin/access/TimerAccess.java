package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeltaTracker.Timer.class)
public interface TimerAccess {
    @Accessor("deltaTickResidual")
    float nt$getPartialTick();
}
