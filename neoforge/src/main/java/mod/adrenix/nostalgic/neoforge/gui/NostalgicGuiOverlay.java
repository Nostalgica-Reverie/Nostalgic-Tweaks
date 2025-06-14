package mod.adrenix.nostalgic.neoforge.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.helper.candy.hud.HudHelper;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public enum NostalgicGuiOverlay
{
    AIR("air", VanillaGuiLayers.PLAYER_HEALTH, ((graphics, deltaTracker) -> {
        Minecraft minecraft = Minecraft.getInstance();

        if (!CandyTweak.HIDE_HUNGER_BAR.get() || minecraft.options.hideGui || (minecraft.gameMode != null && !minecraft.gameMode.canHurtPlayer()))
            return;

        int supply = NullableResult.getOrElse(minecraft.player, 0, Player::getAirSupply);
        int maximum = NullableResult.getOrElse(minecraft.player, 0, Player::getMaxAirSupply);

        if (supply >= maximum)
            return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        HudHelper.renderAir(graphics, minecraft.gui.leftHeight, GuiUtil.getGuiWidth() / 2 - 100);

        minecraft.gui.leftHeight += 10;

        RenderSystem.disableBlend();
    })),
    ARMOR("armor", VanillaGuiLayers.EXPERIENCE_BAR, ((graphics, deltaTracker) -> {
        Minecraft minecraft = Minecraft.getInstance();

        if (!CandyTweak.HIDE_HUNGER_BAR.get() || minecraft.options.hideGui || (minecraft.gameMode != null && !minecraft.gameMode.canHurtPlayer()))
            return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        HudHelper.renderArmor(graphics, minecraft.gui.rightHeight, GuiUtil.getGuiWidth() / 2 + 90);

        minecraft.gui.rightHeight += 10;

        RenderSystem.disableBlend();
    })),
    STAMINA("stamina", VanillaGuiLayers.EXPERIENCE_BAR, ((graphics, deltaTracker) -> {
        Minecraft minecraft = Minecraft.getInstance();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        StaminaRenderer.render(graphics, minecraft.gui.rightHeight);

        if (StaminaRenderer.isVisible())
            minecraft.gui.rightHeight += 10;

        RenderSystem.disableBlend();
    }));

    /* Fields */

    private final ResourceLocation id;
    private final ResourceLocation above;
    private final LayeredDraw.Layer renderer;

    /* Constructor */

    NostalgicGuiOverlay(String id, ResourceLocation above, LayeredDraw.Layer renderer)
    {
        this.id = LocateResource.mod(id);
        this.above = above;
        this.renderer = renderer;
    }

    /* Methods */

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
