package mod.adrenix.nostalgic.mixin.access;

import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldSelectionList.WorldListEntry.class)
public interface WorldListEntryAccess {
    @Accessor("summary")
    LevelSummary nt$getLevelSummary();

    @Accessor("icon")
    FaviconTexture nt$getIcon();
}
