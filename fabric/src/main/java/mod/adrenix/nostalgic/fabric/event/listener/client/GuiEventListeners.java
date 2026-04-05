package mod.adrenix.nostalgic.fabric.event.listener.client;

import mod.adrenix.nostalgic.event.EventResult;
import mod.adrenix.nostalgic.event.callback.client.GuiEventCallbacks;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.Screen;

public class GuiEventListeners {
    /**
     * Registers the event listeners
     */
    public static void register() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            EventResult<Screen> reroute = GuiEventCallbacks.rerouteScreen(screen);
            if (reroute.cancelled()) {
                client.setScreen(reroute.value());
                return;
            }

            registerEventsForScreen(screen);
        });
    }

    private static void registerEventsForScreen(Screen screen) {
        ScreenEvents.beforeExtract(screen).register(GuiEventCallbacks::setMousePosition);
        ScreenEvents.afterExtract(screen).register(GuiEventCallbacks::renderModGraphics);
    }
}
