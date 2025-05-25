package mod.adrenix.nostalgic.client.gui.overlay.types.color;

import mod.adrenix.nostalgic.client.gui.widget.group.Group;
import mod.adrenix.nostalgic.util.common.lang.Lang;

class PickerGroup
{
    final Group hsb;
    final Group rgb;
    final Group alpha;
    final Group sample;
    final Group hex;
    final Group palette;
    final Group recent;
    final Group random;
    final ColorPicker picker;

    PickerGroup(ColorPicker picker)
    {
        this.picker = picker;

        this.hsb = Group.create(picker.overlay)
            .size(ColorPicker.GROUP_WIDTH)
            .title(Lang.literal("HSB"))
            .border(picker.color)
            .build();

        this.rgb = Group.create(picker.overlay)
            .rightOf(this.hsb, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH)
            .title(Lang.literal("RGB"))
            .border(picker.color)
            .build();

        this.alpha = Group.create(picker.overlay)
            .below(this.hsb, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH)
            .title(Lang.Colorize.OPACITY)
            .border(picker.color)
            .build();

        this.sample = Group.create(picker.overlay)
            .rightOf(this.alpha, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH)
            .title(Lang.Colorize.SAMPLE)
            .border(picker.color)
            .build();

        this.hex = Group.create(picker.overlay)
            .rightOf(this.rgb, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH, ColorPicker.GROUP_HEIGHT)
            .title(Lang.literal("Hex"))
            .border(picker.color)
            .build();

        this.palette = Group.create(picker.overlay)
            .posX(this.hex::getX)
            .below(this.hex, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH, ColorPicker.GROUP_HEIGHT)
            .title(Lang.Colorize.PALETTE)
            .border(picker.color)
            .bottomOffset(-1)
            .build();

        this.recent = Group.create(picker.overlay)
            .extendHeightTo(picker.done, ColorPicker.PADDING)
            .rightOf(this.sample, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH, 12)
            .title(Lang.Colorize.RECENT)
            .border(picker.color)
            .bottomOffset(-2)
            .build();

        this.random = Group.create(picker.overlay)
            .extendHeightTo(this.recent, ColorPicker.PADDING)
            .rightOf(this.sample, ColorPicker.PADDING)
            .below(this.palette, ColorPicker.PADDING)
            .size(ColorPicker.GROUP_WIDTH)
            .title(Lang.Colorize.RANDOM)
            .border(picker.color)
            .build();

        this.recent.getBuilder().below(this.random, ColorPicker.PADDING);
    }

    void build()
    {
        this.picker.overlay.addWidgets(this.hsb, this.alpha);
        this.picker.overlay.addWidgets(this.rgb, this.sample);
        this.picker.overlay.addWidgets(this.hex, this.palette, this.random, this.recent);

        this.hsb.addWidgets(this.picker.text.hue, this.picker.slider.hue);
        this.hsb.addWidgets(this.picker.text.saturation, this.picker.slider.saturation);
        this.hsb.addWidgets(this.picker.text.brightness, this.picker.slider.brightness);
        this.rgb.addWidgets(this.picker.text.red, this.picker.slider.red);
        this.rgb.addWidgets(this.picker.text.green, this.picker.slider.green);
        this.rgb.addWidgets(this.picker.text.blue, this.picker.slider.blue);
        this.alpha.addWidgets(this.picker.text.opacity, this.picker.slider.opacity);
        this.sample.addWidgets(this.picker.text.sample);
    }
}
