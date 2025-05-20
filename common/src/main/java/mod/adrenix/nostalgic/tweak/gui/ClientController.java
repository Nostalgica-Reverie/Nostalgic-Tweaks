package mod.adrenix.nostalgic.tweak.gui;

import mod.adrenix.nostalgic.client.gui.screen.vanilla.title.logo.editor.FallingBlockEditorScreen;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.minecraft.client.Minecraft;

/**
 * Client-side only utility class for {@link ControllerId} enumerations.
 */
public abstract class ClientController
{
    public static void openFallingLogoEditor()
    {
        GuiUtil.getScreen().ifPresent(screen -> {
            if (GuiUtil.getScreenAs(FallingBlockEditorScreen.class).isEmpty())
                Minecraft.getInstance().setScreen(new FallingBlockEditorScreen(screen));
        });
    }
}
