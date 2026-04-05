package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.multiplayer.LevelLoadTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLoadingScreen.class)
public interface LevelLoadingScreenAccess {
    @Accessor("loadTracker")
    LevelLoadTracker getLoadTracker();

    @Accessor("reason")
    LevelLoadingScreen.Reason getReason();
}
