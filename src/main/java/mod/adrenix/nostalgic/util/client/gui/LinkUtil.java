package mod.adrenix.nostalgic.util.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Util;

public abstract class LinkUtil {
    /**
     * Provides logic that handles button presses with URL jumps.
     *
     * @param url The URL the button is referencing.
     * @return A handler method for when a button is pressed.
     */
    public static Runnable onPress(String url) {
        return () -> Minecraft.getInstance().setScreen(confirm(Minecraft.getInstance().screen, url));
    }

    /**
     * Generates a new confirm link screen instance.
     *
     * @param parent The parent screen to return to.
     * @param url    The URL to jump to and display.
     * @return A new confirm link screen instance.
     */
    private static ConfirmLinkScreen confirm(Screen parent, String url) {
        return new ConfirmLinkScreen(accepted -> jump(parent, url, accepted), url, true);
    }

    /**
     * If the link jump is accepted, the URL is opened in the user's default browser. Otherwise, the game returns to the
     * parent screen.
     *
     * @param parent   The parent screen to return to.
     * @param address  The URL address to jump to and display.
     * @param accepted Whether the URL should be opened.
     */
    private static void jump(Screen parent, String address, boolean accepted) {
        if (accepted)
            Util.getPlatform().openUri(address);

        Minecraft.getInstance().setScreen(parent);
    }
}
