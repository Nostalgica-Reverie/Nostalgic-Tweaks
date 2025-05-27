package mod.adrenix.nostalgic.neoforge.gui;

import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaRenderer;
import mod.adrenix.nostalgic.util.common.LocateResource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public enum NostalgicGuiOverlay
{
    STAMINA("stamina", VanillaGuiLayers.EXPERIENCE_BAR, ((graphics, deltaTracker) -> {
        Gui gui = Minecraft.getInstance().gui;

        StaminaRenderer.render(graphics, gui.rightHeight);

        if (StaminaRenderer.isVisible())
            gui.rightHeight += 10;
    }));

    private final ResourceLocation id;
    private final ResourceLocation above;
    private final LayeredDraw.Layer renderer;

    NostalgicGuiOverlay(String id, ResourceLocation above, LayeredDraw.Layer renderer)
    {
        this.id = LocateResource.mod(id);
        this.above = above;
        this.renderer = renderer;
    }

    /**
     * Use this if you need the location of this overlay, which is the key stored in the gui overlay manager.
     *
     * @return This overlay's unique {@link ResourceLocation}.
     */
    public ResourceLocation id()
    {
        return this.id;
    }

    /**
     * This must be an overlay we have already registered or a vanilla overlay. Do not use other mods' overlays.
     *
     * @return The overlay this overlay will be above.
     */
    public ResourceLocation above()
    {
        return this.above;
    }

    /**
     * @return The custom renderer for this overlay.
     */
    public LayeredDraw.Layer renderer()
    {
        return this.renderer;
    }
}
