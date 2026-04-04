package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.overlay.types.color.ColorPicker;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ColorInputBuilder extends AbstractInputMaker<ColorInputBuilder, ColorInput> {
    /* Fields */

    protected final Color color;
    protected boolean opaqueColor = false;
    protected boolean useColorPicker = true;
    protected boolean displayColorBox = true;
    protected Consumer<ColorPicker> onPickerClose = (picker) -> {
    };

    /* Constructor */

    protected ColorInputBuilder(Color color) {
        super();

        this.color = color;
        this.startWith = HexUtil.parseString(color.getIntComponents());
    }

    /* Methods */

    @Override
    public ColorInputBuilder self() {
        return this;
    }

    /**
     * Restrict this color input to only accepting opaque colors.
     */
    @PublicAPI
    public ColorInputBuilder opaque() {
        this.opaqueColor = true;
        this.widget.ifPresent(input -> input.setInput(HexInput.update(input.getInput(), this.opaqueColor)));

        return this;
    }

    /**
     * Hide the color pipette icon and prevent the user from opening a color picker overlay.
     */
    @PublicAPI
    public ColorInputBuilder hidePipette() {
        this.useColorPicker = false;

        return this;
    }

    /**
     * Hide the sample color box that displays on the far right side of the input widget.
     */
    @PublicAPI
    public ColorInputBuilder hideColor() {
        this.displayColorBox = false;

        return this;
    }

    /**
     * Add a color pipette icon to the input widget, that when clicked, will open a new color picker overlay.
     *
     * @param onClose A {@link Consumer} that accepts the {@link ColorPicker} when it is closed.
     */
    @PublicAPI
    public ColorInputBuilder colorPicker(Consumer<ColorPicker> onClose) {
        this.onPickerClose = onClose;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColorInputBuilder onInput(BiConsumer<ColorInput, String> responder) {
        return super.onInput((widget, typed) -> {
            widget.setInput(HexInput.update(typed, this.opaqueColor));
            responder.accept(widget, typed);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ColorInput construct() {
        if (this.responder == null)
            this.onInput(string -> {
            });

        return new ColorInput(this);
    }
}
