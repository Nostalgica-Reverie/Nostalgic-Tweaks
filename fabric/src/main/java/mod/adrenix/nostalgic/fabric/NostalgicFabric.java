package mod.adrenix.nostalgic.fabric;

import mod.adrenix.nostalgic.fabric.event.listener.client.GuiEventListeners;
import mod.adrenix.nostalgic.fabric.event.listener.client.TickEventListeners;
import net.fabricmc.api.ModInitializer;

public class NostalgicFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        GuiEventListeners.register();
        TickEventListeners.register();
    }
}
