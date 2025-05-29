package mod.adrenix.nostalgic.forge.setup;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.client.gui.screen.home.HomeScreen;
import mod.adrenix.nostalgic.forge.event.AppleSkinHandler;
import mod.adrenix.nostalgic.forge.gui.NostalgicGuiOverlay;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
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
        ModLoadingContext.get()
            .registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, ClientSetup::getScreenFactory);

        // Define mod screen
        GuiUtil.modListScreen = ModListScreen::new;

        // Listen for AppleSkin
        if (ModTracker.APPLE_SKIN.isInstalled())
        {
            MinecraftForge.EVENT_BUS.register(AppleSkinHandler.class);
            NostalgicTweaks.LOGGER.info("Registered NeoForge AppleSkin Listener");
        }
    }

    /**
     * @return A {@link HomeScreen} instance that represents the mod's {@link ConfigScreenHandler.ConfigScreenFactory}.
     */
    private static ConfigScreenHandler.ConfigScreenFactory getScreenFactory()
    {
        return new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> new HomeScreen(screen, false)));
    }

    /**
     * Register this mod's custom gui overlays. Use {@link NostalgicGuiOverlay#id()} to get this mod's overlay id as a
     * resource location.
     *
     * @param event The {@link RegisterGuiOverlaysEvent} event instance.
     */
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event)
    {
        for (NostalgicGuiOverlay overlay : NostalgicGuiOverlay.values())
            event.registerAbove(overlay.above(), overlay.overlayName(), overlay.renderer());
    }
}
