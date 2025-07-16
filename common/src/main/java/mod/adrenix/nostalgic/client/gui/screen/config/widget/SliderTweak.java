package mod.adrenix.nostalgic.client.gui.screen.config.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.gui.widget.slider.SliderBuilder;
import mod.adrenix.nostalgic.client.gui.widget.slider.SliderWidget;
import mod.adrenix.nostalgic.tweak.gui.SliderType;
import mod.adrenix.nostalgic.tweak.gui.TweakSlider;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.asset.GameSprite;
import mod.adrenix.nostalgic.util.common.asset.ModSprite;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderTweak
{
    /* Builder */

    /**
     * Create a new {@link SliderWidget} based on the given data.
     *
     * @param slider        A {@link TweakSlider} metadata instance.
     * @param valueConsumer A {@link Consumer} that consumes a number when the slider value changes.
     * @param valueSupplier A {@link Supplier} that provides a number for the slider value.
     * @return A new {@link SliderBuilder} instance.
     */
    public static SliderBuilder create(TweakSlider slider, Consumer<? super Number> valueConsumer, Supplier<? super Number> valueSupplier)
    {
        return new SliderTweak(slider, valueConsumer::accept, () -> (Number) valueSupplier.get()).getBuilder();
    }

    /* Fields */

    private final TweakSlider slider;
    private final Consumer<Number> valueConsumer;
    private final Supplier<Number> valueSupplier;

    /* Constructor */

    private SliderTweak(TweakSlider slider, Consumer<Number> valueConsumer, Supplier<Number> valueSupplier)
    {
        this.slider = slider;
        this.valueConsumer = valueConsumer;
        this.valueSupplier = valueSupplier;
    }

    /* Methods */

    /**
     * @return Create a new {@link SliderBuilder} instance that will manage a number tweak.
     */
    private SliderBuilder getBuilder()
    {
        return SliderWidget.create(this.slider.getMin(), this.slider.getMax(), this.valueConsumer, this.valueSupplier)
            .effectsRenderer(this::effectsRenderer)
            .title(this::getTitle)
            .suffix(this::getSuffix)
            .formatter(this::getFormatter)
            .interval(this.slider.getInterval())
            .roundTo(this.slider.getRoundTo());
    }

    /**
     * A custom formatter that changes the number color based on a slider's type.
     *
     * @param number A generic number.
     * @return A formatted string.
     */
    private String getFormatter(Number number)
    {
        return this.slider.getType().getColor(this.valueSupplier.get()) + this.slider.getFormatter().apply(number);
    }

    /**
     * @return A custom title for the slider based on its type.
     */
    private Component getTitle()
    {
        Component fromType = this.slider.getType().getTranslation(this.valueSupplier.get());

        if (fromType.getString().isEmpty())
            return this.slider.getTranslation();

        return fromType;
    }

    /**
     * @return A custom suffix for the slider based on its type.
     */
    private Component getSuffix()
    {
        Component fromType = this.slider.getType().getSuffix(this.valueSupplier.get());

        if (fromType.getString().isEmpty())
            return Component.literal(this.slider.getSuffix());

        return fromType;
    }

    /**
     * Custom effects rendering for sliders that need it, such as the heart slider.
     *
     * @param slider      The {@link SliderWidget} instance.
     * @param graphics    The {@link GuiGraphics} instance.
     * @param mouseX      The x-coordinate of the mouse.
     * @param mouseY      The y-coordinate of the mouse.
     * @param partialTick The normalized progress made between two ticks [0.0F, 1.0F].
     */
    private void effectsRenderer(SliderWidget slider, GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        boolean isHearts = this.slider.getType() == SliderType.HEARTS;
        boolean isStamina = this.slider.getType() == SliderType.STAMINA;

        if (!isHearts && !isStamina)
            return;

        if (slider.isInactive())
            RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);

        int value = this.valueSupplier.get().intValue();
        int x = slider.getX() + (slider.getWidth() / 2) - 45;
        int y = slider.getY() + 6;
        int dx = x;

        ResourceLocation empty = isHearts ? GameSprite.EMPTY_HEART : ModSprite.STAMINA_EMPTY;
        ResourceLocation full = isHearts ? GameSprite.FULL_HEART : ModSprite.STAMINA_LEVEL;
        ResourceLocation half = isHearts ? GameSprite.HALF_HEART : ModSprite.STAMINA_LEVEL_HALF;

        RenderUtil.beginBatching();

        for (int i = 0; i < 10; i++)
        {
            RenderUtil.blitSprite(empty, graphics, dx, y, 9, 9);
            dx += 9;
        }

        dx = x;

        for (int i = 0; i < value - 1; i++)
        {
            if (MathUtil.isOdd(i))
                continue;

            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 1.0F);
            RenderUtil.blitSprite(full, graphics, dx, y, 9, 9);
            graphics.pose().popPose();

            dx += 9;
        }

        if (MathUtil.isOdd(value))
        {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 1.0F);
            RenderUtil.blitSprite(half, graphics, dx, y, 9, 9);
            graphics.pose().popPose();
        }

        RenderUtil.endBatching();
    }
}
