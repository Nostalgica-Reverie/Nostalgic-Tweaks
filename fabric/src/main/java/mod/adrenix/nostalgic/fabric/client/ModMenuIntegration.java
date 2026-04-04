package mod.adrenix.nostalgic.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;

/**
 * This class implements the Mod Menu API for Fabric. A functional interface is cached so that mod menu screen can be
 * accessed from the nostalgic title screen and the nostalgic pause screen.
 */
public class ModMenuIntegration implements ModMenuApi {
    static {
        GuiUtil.modListScreen = ModMenuApi::createModsScreen;
    }

    /**
     * Override the mod config screen factory so that it points to the mod's built-in configuration graphical user
     * interface.
     *
     * @return A config screen factory that builds a new settings screen instance.
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> new HomeScreen(parentScreen, false);
    }
}
