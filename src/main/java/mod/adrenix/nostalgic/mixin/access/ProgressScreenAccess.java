package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.ProgressScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProgressScreen.class)
public interface ProgressScreenAccess {
    @Accessor("clearScreenAfterStop")
    boolean nt$clearScreenAfterStop();

    @Accessor("stop")
    boolean nt$stop();
}
