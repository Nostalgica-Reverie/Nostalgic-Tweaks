package mod.adrenix.nostalgic.client.gui.overlay.types.color;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.util.common.array.ArrayUtil;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.IntegerHolder;
import mod.adrenix.nostalgic.util.common.lang.Lang;

import java.util.function.Consumer;

public class ColorPicker
{
    /* Static */

    public static final int GROUP_WIDTH = 120;
    public static final int GROUP_HEIGHT = 24;
    public static final int PADDING = 3;

    /* Fields */

    private final Consumer<ColorPicker> onClose;

    final Overlay overlay;
    final Color color;
    final Color resetColor;
    final ButtonWidget done;
    final PickerGroup group;
    final PickerText text;
    final PickerSlider slider;
    final PickerHex hex;
    final PickerPalette palette;
    final boolean transparent;

    /* Constructor */

    private ColorPicker(Color color, Consumer<ColorPicker> onClose, boolean transparent)
    {
        this.onClose = (picker) -> {
            PickerPalette.RECENT.removeIf(Color::isEmpty);

            Color copy = new Color(this.getColor());

            if (PickerPalette.RECENT.stream().noneMatch(recent -> recent.equals(copy)))
                ArrayUtil.popPush(PickerPalette.RECENT, copy, PickerPalette.SIZE);

            onClose.accept(picker);
        };

        this.color = color;
        this.transparent = transparent;
        this.resetColor = new Color(color.get(), color.getFloatAlpha());

        this.overlay = Overlay.create(Lang.Colorize.TITLE)
            .resizeForWidgets()
            .infoMessage(Lang.Colorize.HINT)
            .icon(Icons.SMALL_COLOR_WHEEL)
            .padding(PADDING)
            .onClose(() -> this.onClose.accept(this))
            .build();

        this.done = ButtonWidget.create(Lang.Vanilla.GUI_DONE).build();
        this.group = new PickerGroup(this);
        this.text = new PickerText(this);
        this.slider = new PickerSlider(this);
        this.hex = new PickerHex(this);
        this.palette = new PickerPalette(this);

        this.done.getBuilder()
            .icon(Icons.GREEN_CHECK)
            .extendWidthToEnd(this.group.recent, 0)
            .below(this.group.recent, PADDING)
            .rightOf(this.group.sample, PADDING)
            .onPress(this.overlay::close);

        this.text.build();
        this.slider.build();
        this.group.build();
        this.palette.build();

        this.overlay.addWidget(this.done);
        this.setTabOrder();
    }

    /* Tab Order */

    /**
     * Set the proper keyboard tab order for the overlay's widgets.
     */
    private void setTabOrder()
    {
        IntegerHolder order = IntegerHolder.create(0);

        this.slider.hue.setTabOrderGroup(order.getAndIncrement());
        this.slider.saturation.setTabOrderGroup(order.getAndIncrement());
        this.slider.brightness.setTabOrderGroup(order.getAndIncrement());
        this.slider.opacity.setTabOrderGroup(order.getAndIncrement());
        this.slider.red.setTabOrderGroup(order.getAndIncrement());
        this.slider.green.setTabOrderGroup(order.getAndIncrement());
        this.slider.blue.setTabOrderGroup(order.getAndIncrement());
        this.hex.input.setTabOrderGroup(order.getAndIncrement());
        this.hex.reset.setTabOrderGroup(order.getAndIncrement());
        this.group.palette.getWidgetStream().forEach(widget -> widget.setTabOrderGroup(order.getAndIncrement()));
        this.group.random.getWidgetStream().forEach(widget -> widget.setTabOrderGroup(order.getAndIncrement()));
        this.group.recent.getWidgetStream().forEach(widget -> widget.setTabOrderGroup(order.getAndIncrement()));
        this.done.setTabOrderGroup(order.getAndIncrement());
    }

    /* Methods */

    /**
     * @return The {@link Color} instance being managed by this color picker.
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * Open a new color picker overlay window.
     *
     * @return The overlay managing this color instance.
     */
    private Overlay open()
    {
        return this.overlay.open();
    }

    /* Builder */

    /**
     * Start the process of building a new {@link ColorPicker} overlay.
     *
     * @param color   The {@link Color} instance this overlay will manage.
     * @param onClose A {@link Consumer} that accepts this {@link ColorPicker} instance when the overlay is closed.
     * @return A new {@link ColorPicker.Builder} instance.
     */
    public static Builder create(Color color, Consumer<ColorPicker> onClose)
    {
        return new Builder(color, onClose);
    }

    /**
     * Start the process of building a new {@link ColorPicker} overlay.
     *
     * @param color The {@link Color} instance this overlay will manage.
     * @return A new {@link ColorPicker.Builder} instance.
     */
    public static Builder create(Color color)
    {
        return new Builder(color, picker -> { });
    }

    public static class Builder
    {
        private final Color color;
        private final Consumer<ColorPicker> onClose;
        private boolean transparent = true;

        private Builder(Color color, Consumer<ColorPicker> onClose)
        {
            this.color = color;
            this.onClose = onClose;
        }

        /**
         * Enforce that this color remains opaque. This will hide alpha slider bars within the color picker overlay.
         * Transparency is enabled by default, invoking this method will disable it.
         */
        public Builder opaque()
        {
            this.transparent = false;

            return this;
        }

        /**
         * Inform the builder that this color is transparent. Transparency is enabled by default. This method only
         * exists for consistency.
         */
        public Builder transparent()
        {
            this.transparent = true;

            return this;
        }

        /**
         * Manually set the transparency flag using the given boolean opaque state. Removes the need of an if statement
         * when building a color picker overlay where the transparency state is unknown. If {@code false} is given, then
         * the transparency flag is set to {@code true}.
         *
         * @param state A boolean for whether this color is opaque.
         */
        public Builder opaque(boolean state)
        {
            if (state)
                return this.opaque();

            return this.transparent();
        }

        /**
         * Finalize the builder process and receive a {@link ColorPicker} instance.
         */
        public ColorPicker build()
        {
            return new ColorPicker(this.color, this.onClose, this.transparent);
        }

        /**
         * Finalize the building process and open the color picker overlay.
         */
        public Overlay open()
        {
            return new ColorPicker(this.color, this.onClose, this.transparent).open();
        }
    }
}
