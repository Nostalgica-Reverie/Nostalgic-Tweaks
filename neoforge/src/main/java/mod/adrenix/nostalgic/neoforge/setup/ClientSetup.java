package mod.adrenix.nostalgic.neoforge.setup;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.neoforge.event.AppleSkinHandler;
import mod.adrenix.nostalgic.neoforge.event.EmbeddiumHandler;
import mod.adrenix.nostalgic.neoforge.gui.NostalgicGuiOverlay;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
public abstract class ClientSetup
{
    /**
     * Instructions for client initialization.
     *
     * @param event A {@link FMLClientSetupEvent} event instance.
     */
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event)
    {
        // Register config screen
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ClientSetup::getScreenFactory);

        // Define mod screen
        GuiUtil.modListScreen = ModListScreen::new;

        // Listen for AppleSkin
        if (ModTracker.APPLE_SKIN.isInstalled())
        {
            NeoForge.EVENT_BUS.register(AppleSkinHandler.class);
            NostalgicTweaks.LOGGER.info("Registered AppleSkin Listener");
        }

        // Listen for Embeddium
        if (ModTracker.EMBEDDIUM.isInstalled())
        {
            NeoForge.EVENT_BUS.register(EmbeddiumHandler.class);
            EmbeddiumHandler.init();

            NostalgicTweaks.LOGGER.info("Registered Embeddium Listener");
        }
    }

    /**
     * @return A {@link HomeScreen} instance that represents the mod's {@link IConfigScreenFactory}.
     */
    private static IConfigScreenFactory getScreenFactory()
    {
        return ((minecraft, screen) -> new HomeScreen(screen, false));
    }

    /**
     * Register this mod's custom gui overlays.
     *
     * @param event The {@link RegisterGuiLayersEvent} event instance.
     */
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event)
    {
        for (NostalgicGuiOverlay overlay : NostalgicGuiOverlay.values())
            event.registerAbove(overlay.above(), overlay.id(), overlay.renderer());
    }
}
