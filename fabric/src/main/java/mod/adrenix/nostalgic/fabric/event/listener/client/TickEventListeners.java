package mod.adrenix.nostalgic.fabric.event.listener.client;

import mod.adrenix.nostalgic.event.callback.client.TickEventCallbacks;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public abstract class TickEventListeners {
    public static void register() {
        ClientTickEvents.START_CLIENT_TICK.register(TickEventCallbacks::onPreTick);
    }
}
