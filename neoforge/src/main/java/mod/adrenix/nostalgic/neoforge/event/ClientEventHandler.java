package mod.adrenix.nostalgic.neoforge.event;

import mod.adrenix.nostalgic.NostalgicTweaks;
import mod.adrenix.nostalgic.helper.candy.level.fog.OverworldFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.VoidFogRenderer;
import mod.adrenix.nostalgic.helper.candy.level.fog.WaterFogRenderer;
import mod.adrenix.nostalgic.helper.gameplay.InteractionHelper;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.tweak.config.ModTweak;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(
    modid = NostalgicTweaks.MOD_ID,
    bus = EventBusSubscriber.Bus.GAME,
    value = Dist.CLIENT
)
public abstract class ClientEventHandler
{
    /**
     * Prevents various gui overlays from rendering depending on tweak context.
     *
     * @param event The {@link RenderGuiLayerEvent.Pre} event instance.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void setupHighestGuiOverlayPre(RenderGuiLayerEvent.Pre event)
    {
        ResourceLocation overlay = event.getName();
        LocalPlayer player = Minecraft.getInstance().player;
        Gui gui = Minecraft.getInstance().gui;

        boolean isExperienceOff = CandyTweak.HIDE_EXPERIENCE_BAR.get();
        boolean isFoodOff = CandyTweak.HIDE_HUNGER_BAR.get();
        boolean isMounted = NullableResult.getOrElse(player, false, local -> local.jumpableVehicle() != null);

        if (overlay == VanillaGuiLayers.HOTBAR)
        {
            if (isExperienceOff)
            {
                gui.leftHeight -= 7;
                gui.rightHeight -= 7;

                if (isMounted)
                {
                    gui.leftHeight += 7;
                    gui.rightHeight += 7;
                }
            }
        }

        if (overlay == VanillaGuiLayers.EXPERIENCE_BAR && isExperienceOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.EXPERIENCE_LEVEL && isExperienceOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.FOOD_LEVEL && isFoodOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.ARMOR_LEVEL && isFoodOff)
            event.setCanceled(true);

        if (overlay == VanillaGuiLayers.AIR_LEVEL && isFoodOff)
            event.setCanceled(true);
    }

    /**
     * Changes various aspects of the world's fog depending on tweak context.
     *
     * @param event The {@link ViewportEvent.RenderFog} event instance.
     */
    @SubscribeEvent
    public static void renderFog(ViewportEvent.RenderFog event)
    {
        if (!ModTweak.ENABLED.get())
            return;

        if (OverworldFogRenderer.setupFog(event.getCamera(), event.getMode(), event::getNearPlaneDistance, event::getFarPlaneDistance, event::setFogShape, event::setNearPlaneDistance, event::setFarPlaneDistance))
            event.setCanceled(true);

        if (WaterFogRenderer.setupFog(event.getCamera(), event::setFogShape, event::setNearPlaneDistance, event::setFarPlaneDistance))
            event.setCanceled(true);

        if (VoidFogRenderer.setupFog(event.getCamera(), event.getMode(), event::getNearPlaneDistance, event::getFarPlaneDistance, event::setNearPlaneDistance, event::setFarPlaneDistance))
            event.setCanceled(true);
    }

    /**
     * Changes the fog color depending on tweak context.
     *
     * @param event The {@link ViewportEvent.ComputeFogColor} event instance.
     */
    @SubscribeEvent
    public static void computeFogColor(ViewportEvent.ComputeFogColor event)
    {
        if (!ModTweak.ENABLED.get())
            return;

        if (WaterFogRenderer.setupColor(event.getCamera(), event::setRed, event::setGreen, event::setBlue))
            return;

        OverworldFogRenderer.setupColor(event.getCamera(), event::getRed, event::getGreen, event::getBlue, event::setRed, event::setGreen, event::setBlue);
        VoidFogRenderer.setupColor(event.getCamera(), event::getRed, event::getGreen, event::getBlue, event::setRed, event::setGreen, event::setBlue);
    }

    /**
     * Prevents the use of specific items based on tweak context. On servers with the mod installed, the interaction
     * listener will prevent connected players from using items where needed.
     *
     * @param event The {@link InputEvent.InteractionKeyMappingTriggered} event instance.
     */
    @SubscribeEvent
    public static void onUseItem(InputEvent.InteractionKeyMappingTriggered event)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        HitResult hitResult = Minecraft.getInstance().hitResult;

        if (player == null || hitResult == null || !event.isUseItem())
            return;

        if (hitResult instanceof BlockHitResult blockHitResult)
        {
            Item itemInHand = player.getItemInHand(event.getHand()).getItem();
            BlockState blockState = player.level().getBlockState(blockHitResult.getBlockPos());

            if (InteractionHelper.shouldNotUseItem(itemInHand, blockState))
            {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }
}
