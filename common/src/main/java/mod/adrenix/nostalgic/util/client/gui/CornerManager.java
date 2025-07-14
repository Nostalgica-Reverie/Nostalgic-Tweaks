package mod.adrenix.nostalgic.util.client.gui;

import mod.adrenix.nostalgic.tweak.enums.Corner;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.data.NullableResult;
import mod.adrenix.nostalgic.util.common.data.NumberHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Collection;
import java.util.HashSet;

/**
 * This utility manages and keeps track of where the overlay text should be rendered on the HUD. Use this utility when
 * drawing the overlay text to the screen.
 */
public class CornerManager
{
    private final float height = (float) GuiUtil.getGuiHeight();
    private final NumberHolder<Double> topLeft = NumberHolder.create(2.0D);
    private final NumberHolder<Double> topRight = NumberHolder.create(2.0D);
    private final NumberHolder<Double> topCenter = NumberHolder.create(2.0D);
    private final NumberHolder<Double> bottomLeft = NumberHolder.create(this.height - 10.0D);
    private final NumberHolder<Double> bottomRight = NumberHolder.create(this.height - 10.0D);

    /**
     * Get where the next line for the given corner should be drawn and then add 10 units to the corner offset.
     *
     * @param corner The {@link Corner} context.
     * @return Where the next line for the given corner should be drawn.
     */
    private double getAndAdd(Corner corner)
    {
        return switch (corner)
        {
            case TOP_LEFT -> this.topLeft.getAndAdd(10.0D);
            case TOP_RIGHT -> this.topRight.getAndAdd(10.0D);
            case TOP_CENTER -> this.topCenter.getAndAdd(10.0D);
            case BOTTOM_LEFT -> this.bottomLeft.getAndAdd(-10.0D);
            case BOTTOM_RIGHT -> this.bottomRight.getAndAdd(-10.0D);
        };
    }

    /**
     * Calculates where text rendering from the right side of the screen should start.
     *
     * @param text The text to get a right side offset for.
     * @return The starting point of where the given text should render.
     */
    private int getRightOffset(String text)
    {
        return GuiUtil.getGuiWidth() - GuiUtil.font().width(text) - 2;
    }

    /**
     * Draws the given text to the screen using the given text and corner.
     *
     * @param graphics The {@link GuiGraphics} instance.
     * @param text     The text to draw.
     * @param corner   The {@link Corner} to draw to.
     */
    @PublicAPI
    public void drawText(GuiGraphics graphics, String text, Corner corner)
    {
        this.drawText(graphics, text, corner, 0, 0, true);
    }

    /**
     * Draws the given text to the screen using the given text, corner, and x/y offsets.
     *
     * @param graphics   The {@link GuiGraphics} instance.
     * @param text       The text to draw.
     * @param corner     The {@link Corner} to draw to.
     * @param xOffset    The x-coordinate offset.
     * @param yOffset    The y-coordinate offset.
     * @param dropShadow Whether the text should have a shadow.
     */
    @PublicAPI
    public void drawText(GuiGraphics graphics, String text, Corner corner, int xOffset, int yOffset, boolean dropShadow)
    {
        float x = (corner.isLeft() ? 2 : this.getRightOffset(text)) + xOffset;
        float y = (int) this.getAndAdd(corner) + yOffset;

        if (corner == Corner.TOP_RIGHT)
        {
            LocalPlayer player = Minecraft.getInstance().player;
            Collection<MobEffectInstance> effects = NullableResult.getOrElse(player, new HashSet<>(), LocalPlayer::getActiveEffects);
            boolean areEffectsHidden = Minecraft.getInstance().screen instanceof EffectRenderingInventoryScreen<?> screen && screen.canSeeEffects();

            if (!effects.isEmpty() && !areEffectsHidden)
            {
                if (effects.stream().anyMatch(MobEffectInstance::showIcon))
                    y += 24;

                if (effects.stream().map(MobEffectInstance::getEffect).anyMatch(mobEffect -> !mobEffect.isBeneficial()))
                    y += 26;
            }
        }

        if (corner == Corner.TOP_CENTER)
            x = DrawText.centerX(0, GuiUtil.getGuiWidth(), text);

        x = Mth.clamp(x, 0, GuiUtil.getGuiWidth() - GuiUtil.font().width(text));
        y = Mth.clamp(y, 0, GuiUtil.getGuiHeight() - GuiUtil.textHeight());

        DrawText.begin(graphics, text).pos(x, y).setShadow(dropShadow).draw();
    }
}
