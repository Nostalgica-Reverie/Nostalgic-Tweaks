package mod.adrenix.nostalgic.forge.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.helper.candy.hud.HudHelper;
import mod.adrenix.nostalgic.helper.gameplay.stamina.StaminaRenderer;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.ModTracker;
import mod.adrenix.nostalgic.util.common.LocateResource;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import org.jetbrains.annotations.NotNull;

public enum NostalgicGuiOverlay
{
    AIR("air", VanillaGuiOverlay.PLAYER_HEALTH.id(), (forgeGui, graphics, partialTick, screenWidth, screenHeight) -> {
        if (!CandyTweak.HIDE_HUNGER_BAR.get() || forgeGui.getMinecraft().options.hideGui || !forgeGui.shouldDrawSurvivalElements())
            return;

        int supply = NullableResult.getOrElse(forgeGui.getMinecraft().player, 0, Player::getAirSupply);
        int maximum = NullableResult.getOrElse(forgeGui.getMinecraft().player, 0, Player::getMaxAirSupply);
        int offsetLeft = screenWidth / 2 - 100;

        if (supply >= maximum)
            return;

        if (ModTracker.RAISED.isInstalled())
        {
            forgeGui.leftHeight -= RaisedHandler.getHotbarY();
            offsetLeft += RaisedHandler.getHotbarX();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        HudHelper.renderAir(graphics, forgeGui.leftHeight, offsetLeft);

        if (ModTracker.RAISED.isInstalled())
            forgeGui.leftHeight += RaisedHandler.getHotbarY();

        forgeGui.leftHeight += 10;

        RenderSystem.disableBlend();
    }),
    ARMOR("armor", VanillaGuiOverlay.EXPERIENCE_BAR.id(), ((forgeGui, graphics, partialTick, screenWidth, screenHeight) -> {
        if (!CandyTweak.HIDE_HUNGER_BAR.get() || forgeGui.getMinecraft().options.hideGui || !forgeGui.shouldDrawSurvivalElements())
            return;

        int offsetRight = screenWidth / 2 + 90;

        if (ModTracker.RAISED.isInstalled())
        {
            forgeGui.rightHeight -= RaisedHandler.getHotbarY();
            offsetRight += RaisedHandler.getHotbarX();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        HudHelper.renderArmor(graphics, forgeGui.rightHeight, offsetRight);

        if (ModTracker.RAISED.isInstalled())
            forgeGui.rightHeight += RaisedHandler.getHotbarY();

        forgeGui.rightHeight += 10;

        RenderSystem.disableBlend();
    })),
    STAMINA_ARMOR("stamina_armor", NostalgicGuiOverlay.ARMOR.id(), ((forgeGui, graphics, partialTick, screenWidth, screenHeight) -> {
        if (!StaminaRenderer.isVisible() || !CandyTweak.HIDE_HUNGER_BAR.get())
            return;

        int offsetLeft = 0;

        if (ModTracker.RAISED.isInstalled())
        {
            forgeGui.rightHeight -= RaisedHandler.getHotbarY();
            offsetLeft += RaisedHandler.getHotbarX();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        if (HudHelper.isArmorEmpty())
            forgeGui.rightHeight -= 10;

        StaminaRenderer.render(graphics, forgeGui.rightHeight, offsetLeft);

        if (ModTracker.RAISED.isInstalled())
            forgeGui.rightHeight += RaisedHandler.getHotbarY();

        forgeGui.rightHeight += 10;

        RenderSystem.disableBlend();
    })),
    STAMINA_FOOD("stamina_food", VanillaGuiOverlay.FOOD_LEVEL.id(), ((forgeGui, graphics, partialTick, screenWidth, screenHeight) -> {
        if (!StaminaRenderer.isVisible() || CandyTweak.HIDE_HUNGER_BAR.get())
            return;

        int offsetLeft = 0;

        if (ModTracker.RAISED.isInstalled())
        {
            forgeGui.rightHeight -= RaisedHandler.getHotbarY();
            offsetLeft += RaisedHandler.getHotbarX();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        StaminaRenderer.render(graphics, forgeGui.rightHeight, offsetLeft);

        if (ModTracker.RAISED.isInstalled())
            forgeGui.rightHeight += RaisedHandler.getHotbarY();

        forgeGui.rightHeight += 10;

        RenderSystem.disableBlend();
    }));

    /* Fields */

    private final String name;
    private final ResourceLocation above;
    private final IGuiOverlay renderer;

    /* Constructor */

    NostalgicGuiOverlay(String name, ResourceLocation above, IGuiOverlay renderer)
    {
        this.name = name;
        this.above = above;
        this.renderer = renderer;
    }

    /* Methods */

    /**
     * Use this if you need the location of this overlay, which is the key stored in the gui overlay manager.
     *
     * @return This overlay's unique {@link ResourceLocation}.
     */
    public @NotNull ResourceLocation id()
    {
        return LocateResource.mod(this.name);
    }

    /**
     * @return The overlay's unique string identifier.
     */
    public @NotNull String overlayName()
    {
        return this.name;
    }

    /**
     * This must be an overlay we have already registered or a vanilla overlay. Do not use other mods' overlays.
     *
     * @return The overlay this overlay will be above.
     */
    public @NotNull ResourceLocation above()
    {
        return this.above;
    }

    /**
     * @return The custom renderer for this overlay.
     */
    public @NotNull IGuiOverlay renderer()
    {
        return this.renderer;
    }
}
