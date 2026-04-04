package mod.adrenix.nostalgic.client.gui.overlay.types.color;

import mod.adrenix.nostalgic.client.gui.widget.slider.color.ColorSlider;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.ColorElement;

class PickerSlider {
    /* Fields */

    final ColorSlider hue;
    final ColorSlider saturation;
    final ColorSlider brightness;
    final ColorSlider opacity;
    final ColorSlider red;
    final ColorSlider green;
    final ColorSlider blue;
    private final ColorPicker picker;
    private final Color color;

    /* Constructor */

    PickerSlider(ColorPicker picker) {
        this.picker = picker;
        this.color = picker.color;

        this.hue = this.createSlider(ColorElement.HUE);
        this.saturation = this.createSlider(ColorElement.SATURATION);
        this.brightness = this.createSlider(ColorElement.BRIGHTNESS);
        this.red = this.createSlider(ColorElement.RED);
        this.green = this.createSlider(ColorElement.GREEN);
        this.blue = this.createSlider(ColorElement.BLUE);
        this.opacity = this.createSlider(ColorElement.ALPHA);
    }

    /* Methods */

    /**
     * Finalize the building process of slider widgets.
     */
    void build() {
        this.hue.getBuilder()
                .below(this.picker.text.hue, ColorPicker.PADDING)
                .width(this.picker.group.hsb::getInsideWidth);

        this.saturation.getBuilder()
                .below(this.picker.text.saturation, ColorPicker.PADDING)
                .width(this.picker.group.hsb::getInsideWidth);

        this.brightness.getBuilder()
                .below(this.picker.text.brightness, ColorPicker.PADDING)
                .width(this.picker.group.hsb::getInsideWidth);

        this.red.getBuilder()
                .below(this.picker.text.red, ColorPicker.PADDING)
                .width(this.picker.group.rgb::getInsideWidth);

        this.green.getBuilder()
                .below(this.picker.text.green, ColorPicker.PADDING)
                .width(this.picker.group.rgb::getInsideWidth);

        this.blue.getBuilder()
                .below(this.picker.text.blue, ColorPicker.PADDING)
                .width(this.picker.group.rgb::getInsideWidth);

        this.opacity.getBuilder()
                .below(this.picker.text.opacity, ColorPicker.PADDING)
                .width(this.picker.group.alpha::getInsideWidth);

        this.opacity.setActive(this.picker.transparent);
    }

    /**
     * Create a new slider.
     *
     * @param element The {@link ColorElement} enumeration.
     * @return A new {@link ColorSlider} instance.
     */
    private ColorSlider createSlider(ColorElement element) {
        return ColorSlider.create(this.color, element).scrollWithoutFocus().build();
    }
}
