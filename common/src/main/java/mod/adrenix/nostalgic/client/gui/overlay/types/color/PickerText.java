package mod.adrenix.nostalgic.client.gui.overlay.types.color;

import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

class PickerText
{
    /* Fields */

    private final ColorPicker picker;
    private final Color color;

    final TextWidget hue;
    final TextWidget saturation;
    final TextWidget brightness;
    final TextWidget red;
    final TextWidget green;
    final TextWidget blue;
    final TextWidget opacity;
    final TextWidget sample;

    /* Constructor */

    PickerText(ColorPicker picker)
    {
        this.picker = picker;
        this.color = picker.color;

        this.hue = TextWidget.create(this::getHue).build();
        this.saturation = TextWidget.create(this::getSaturation).build();
        this.brightness = TextWidget.create(this::getBrightness).build();
        this.red = TextWidget.create(this::getRed).build();
        this.green = TextWidget.create(this::getGreen).build();
        this.blue = TextWidget.create(this::getBlue).build();
        this.opacity = TextWidget.create(this::getOpacity).build();
        this.sample = TextWidget.create(Component.literal("The quick brown fox jumps over the lazy dog.")).build();
    }

    /* Methods */

    /**
     * Finalize the building process of text widgets.
     */
    void build()
    {
        this.hue.getBuilder()
            .width(this.picker.group.hsb::getInsideWidth)
            .color(() -> new Color(Color.HSBtoRGB(this.color.getHue(), 1.0F, 1.0F)).get())
            .centerAligned();

        this.saturation.getBuilder()
            .below(this.picker.slider.hue, ColorPicker.PADDING)
            .width(this.picker.group.hsb::getInsideWidth)
            .centerAligned();

        this.brightness.getBuilder()
            .below(this.picker.slider.saturation, ColorPicker.PADDING)
            .width(this.picker.group.hsb::getInsideWidth)
            .centerAligned();

        this.red.getBuilder()
            .width(this.picker.group.rgb::getInsideWidth)
            .color(Color.fromFormatting(ChatFormatting.RED))
            .centerAligned();

        this.green.getBuilder()
            .below(this.picker.slider.red, ColorPicker.PADDING)
            .width(this.picker.group.rgb::getInsideWidth)
            .color(Color.fromFormatting(ChatFormatting.GREEN))
            .centerAligned();

        this.blue.getBuilder()
            .below(this.picker.slider.green, ColorPicker.PADDING)
            .width(this.picker.group.rgb::getInsideWidth)
            .color(Color.fromFormatting(ChatFormatting.BLUE))
            .centerAligned();

        this.opacity.getBuilder()
            .color(this.picker.transparent ? Color.WHITE::get : Color.GRAY::get)
            .width(this.picker.group.alpha::getInsideWidth)
            .centerAligned();

        this.sample.getBuilder()
            .color(this.color)
            .width(this.picker.group.sample::getInsideWidth)
            .height(this.picker.group.alpha::getInsideHeight)
            .background(new Color(() -> this.color.darken(0.5D).get()))
            .centerAligned();
    }

    /* Suppliers */

    private Component getHue()
    {
        return Component.literal(String.format("Hue: %s", Math.round(this.color.getHue() * 360.0F)));
    }

    private Component getSaturation()
    {
        return Component.literal(String.format("Saturation: %s%%", Math.round(this.color.getSaturation() * 100.0F)));
    }

    private Component getBrightness()
    {
        return Component.literal(String.format("Brightness: %s%%", Math.round(this.color.getBrightness() * 100.0F)));
    }

    private Component getRed()
    {
        return Component.literal(String.format("Red: %s", this.color.getRed()));
    }

    private Component getGreen()
    {
        return Component.literal(String.format("Green: %s", this.color.getGreen()));
    }

    private Component getBlue()
    {
        return Component.literal(String.format("Blue: %s", this.color.getBlue()));
    }

    private Component getOpacity()
    {
        String alpha = Lang.Colorize.OPACITY.getString() + ": %s%%";

        return Component.literal(String.format(alpha, Math.round(this.color.getFloatAlpha() * 100.0F)));
    }
}
