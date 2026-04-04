package mod.adrenix.nostalgic.client.gui.widget.input;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.concurrent.TimeUnit;

public class InputModule<Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>> {
    /* Fields */

    private final Input widget;
    private final Overlay overlay;
    private final ButtonWidget copy;
    private final ButtonWidget paste;
    private final ButtonWidget trash;

    private InputModule(Input widget, ModuleType type) {
        this.widget = widget;

        this.overlay = Overlay.create()
                .size(66, 24)
                .setX(() -> widget.getEndX() - 66)
                .above(widget, -3)
                .backgroundColor(widget.getBackgroundColor())
                .addListener(widget)
                .postRenderer(this::renderOutline)
                .unmovable()
                .shadowless()
                .borderless()
                .build();

        this.copy = ButtonWidget.create()
                .pos(2, 2)
                .icon(Icons.COPY)
                .tooltip(Lang.Input.COPY, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Input.COPY_INFO, 35)
                .onPress(() -> Minecraft.getInstance().keyboardHandler.setClipboard(widget.input))
                .build(this.overlay::addWidget);

        this.paste = ButtonWidget.create()
                .icon(Icons.CLIPBOARD)
                .tooltip(Lang.Input.PASTE, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Input.PASTE_INFO, 35)
                .rightOf(this.copy, 1)
                .build(this.overlay::addWidget);

        switch (type) {
            case GENERIC -> this.paste.getBuilder()
                    .onPress(() -> widget.insertText(Minecraft.getInstance().keyboardHandler.getClipboard()));

            case COLOR -> this.paste.getBuilder()
                    .onPress(() -> widget.setInput(Minecraft.getInstance().keyboardHandler.getClipboard()));
        }

        this.trash = ButtonWidget.create()
                .icon(Icons.TRASH_CAN)
                .tooltip(Lang.Input.CLEAR, 35, 500L, TimeUnit.MILLISECONDS)
                .infoTooltip(Lang.Input.CLEAR_INFO, 35)
                .rightOf(this.paste, 1)
                .onPress(() -> widget.setInput(""))
                .build(this.overlay::addWidget);

        switch (type) {
            case GENERIC -> this.trash.getBuilder().onPress(() -> widget.setInput(""));
            case COLOR -> this.trash.getBuilder().onPress(() -> widget.setInput("#"));
        }
    }

    /**
     * Create a new generic control module.
     *
     * @param widget The {@link Input} widget.
     * @return A new {@link InputModule} instance.
     */
    static <Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>> InputModule<Builder, Input> generic(Input widget) {
        return new InputModule<>(widget, ModuleType.GENERIC);
    }

    /**
     * Create a new color control module.
     *
     * @param widget The {@link Input} widget.
     * @return A new {@link InputModule} instance.
     */
    static <Builder extends AbstractInputMaker<Builder, Input>, Input extends AbstractInput<Builder, Input>> InputModule<Builder, Input> color(Input widget) {
        return new InputModule<>(widget, ModuleType.COLOR);
    }

    /* Constructor */

    @PublicAPI
    public Overlay getOverlay() {
        return this.overlay;
    }

    /* Methods */

    @PublicAPI
    public ButtonWidget getCopy() {
        return this.copy;
    }

    @PublicAPI
    public ButtonWidget getPaste() {
        return this.paste;
    }

    @PublicAPI
    public ButtonWidget getTrash() {
        return this.trash;
    }

    private void renderOutline(Overlay overlay, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Color color = this.widget.getBorderColor();
        int x0 = overlay.getX();
        int y0 = overlay.getY();
        int x1 = overlay.getEndX() - 1;
        int y1 = overlay.getEndY();

        graphics.verticalLine(x0, y0, y1 - 2, color.get());
        graphics.verticalLine(x1, y0, y1, color.get());
        graphics.horizontalLine(x0, y0, x1, color.get());
    }

    private enum ModuleType {
        GENERIC,
        COLOR
    }
}
