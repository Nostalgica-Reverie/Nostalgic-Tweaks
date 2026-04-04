package mod.adrenix.nostalgic.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public interface ParentHolder {
    /**
     * @return A {@code nullable} parent {@link Screen}.
     */
    @Nullable Screen getParentScreen();
}
