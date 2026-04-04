package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.widget.blank.BlankWidget;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonTemplate;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.HexUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;

public class ColorInput extends AbstractInput<ColorInputBuilder, ColorInput> {
    /* Builder */

    protected final BlankWidget colorSample;

    /* Fields */
    protected boolean insertingText;

    protected ColorInput(ColorInputBuilder builder) {
        super(builder);

        this.setInput(HexInput.update(builder.startWith, builder.opaqueColor));

        builder.formatter(this::formatHex);

        this.module = InputModule.color(this.self());
        this.cursorPos = 1;
        this.highlightPos = 1;
        this.minCursorPos = 1;

        this.colorSample = BlankWidget.create()
                .size(GuiUtil.textHeight())
                .renderer(this::renderSample)
                .leftOf(this.controls, 1)
                .build(this.internal::add);

        if (builder.displayColorBox)
            this.printer.getBuilder().extendWidthTo(this.colorSample, 4);

        this.internal.moveToEnd(this.printer);
    }

    /* Constructor */

    /**
     * Begin the process of building a new {@link ColorInput} widget.
     *
     * @param color The {@link Color} this widget is managing.
     * @return A new {@link ColorInputBuilder} instance.
     */
    public static ColorInputBuilder create(Color color) {
        return new ColorInputBuilder(color);
    }

    /* Color Picker */

    /**
     * @return A {@link ButtonBuilder} that will open a color picker overlay that manages this color input's color
     * instance.
     */
    public ButtonBuilder getPickerButton() {
        return ButtonTemplate.colorPicker(this::getColor, this.builder.onPickerClose, this.builder.opaqueColor);
    }

    /* Methods */

    /**
     * @return A {@link Color} instance based on the current input.
     */
    public Color getColor() {
        return new Color(HexUtil.parseRGBA(this.input));
    }

    /**
     * Colors a properly formatted hex input string.
     *
     * @param format     The string to format (a substring of the full hex).
     * @param beginIndex The beginning index of the substring.
     * @return A {@link FormattedCharSequence}.
     */
    protected FormattedCharSequence formatHex(String format, int beginIndex) {
        ArrayList<FormattedCharSequence> sequences = new ArrayList<>();

        for (int i = beginIndex; i < format.length() + beginIndex; i++) {
            Style style = switch (i) {
                case 1, 2 -> Style.EMPTY.withColor(ChatFormatting.RED);
                case 3, 4 -> Style.EMPTY.withColor(ChatFormatting.GREEN);
                case 5, 6 -> Style.EMPTY.withColor(ChatFormatting.BLUE);
                case 7, 8 -> Style.EMPTY.withColor(ChatFormatting.WHITE);
                default -> Style.EMPTY;
            };

            int begin = Math.min(i, this.input.length());
            int end = Math.min(i + 1, this.input.length());

            sequences.add(FormattedCharSequence.forward(this.input.substring(begin, end), style));
        }

        return FormattedCharSequence.fromList(sequences);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertText(String text) {
        if (this.insertingText)
            return;

        this.insertingText = true;

        for (int i = 0; i < text.length(); i++)
            this.deleteChars(1);

        super.insertText(text);

        this.insertingText = false;
    }

    /**
     * Renders the sample color.
     *
     * @param widget      The {@link BlankWidget} with position data for the sample.
     * @param graphics    The {@link GuiGraphicsExtractor} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     */
    protected void renderSample(BlankWidget widget, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (!this.builder.displayColorBox)
            return;

        graphics.fill(widget.getX(), widget.getY(), widget.getEndX(), widget.getEndY(), this.getColor().get());
    }
}
