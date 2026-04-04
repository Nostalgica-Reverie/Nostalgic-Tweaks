package mod.adrenix.nostalgic.client.gui.overlay.types.info;

import mod.adrenix.nostalgic.client.gui.overlay.Overlay;
import mod.adrenix.nostalgic.client.gui.overlay.OverlayBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonBuilder;
import mod.adrenix.nostalgic.client.gui.widget.button.ButtonWidget;
import mod.adrenix.nostalgic.client.gui.widget.text.TextWidget;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.color.Gradient;
import mod.adrenix.nostalgic.util.common.lang.Lang;
import mod.adrenix.nostalgic.util.common.lang.Translation;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashSet;

public class MessageOverlay {
    /* Static */

    private final LinkedHashSet<ButtonBuilder> buttons;
    private final OverlayBuilder builder;

    /* Fields */
    private final MessageType messageType;
    private final Component message;
    private int padding = 2;
    private int maxSize = 200;
    private double resizePercentage = 0.35D;
    private boolean centerAlign = true;

    private MessageOverlay(MessageType messageType, Component header, Component message) {
        this.buttons = new LinkedHashSet<>();
        this.builder = Overlay.create(header);
        this.message = message;
        this.messageType = messageType;
    }

    /**
     * Create a new message overlay builder instance. The default {@code padding} will be {@code 2}, the default
     * {@code resizePercentage} will be {@code 0.35D}, and the default {@code maxSize} will be {@code 200}.
     *
     * @param messageType The message type for this overlay, each enum value changes the visuals of the overlay.
     * @param header      The title of the message overlay.
     * @param message     The message to display in the body of the overlay.
     * @return A new {@link MessageOverlay} instance.
     */
    public static MessageOverlay create(MessageType messageType, Translation header, Translation message) {
        return new MessageOverlay(messageType, header.get(), message.get());
    }

    /* Constructor */

    /**
     * Create a new message overlay builder instance. The default {@code padding} will be {@code 2}, the default
     * {@code resizePercentage} will be {@code 0.35D}, and the default {@code maxSize} will be {@code 200}.
     *
     * @param messageType The message type for this overlay, each enum value changes the visuals of the overlay.
     * @param header      The title of the message overlay.
     * @param message     The message to display in the body of the overlay.
     * @return A new {@link MessageOverlay} instance.
     */
    public static MessageOverlay create(MessageType messageType, Component header, Component message) {
        return new MessageOverlay(messageType, header, message);
    }

    /* Methods */

    /**
     * If custom overlay factor instructions are needed for this message overlay, then invoke this method to retrieve
     * the overlay factory that will be used in {@link #build()} is called.
     *
     * @return The message overlay's {@link OverlayBuilder} instance.
     */
    @PublicAPI
    public OverlayBuilder getBuilder() {
        return this.builder;
    }

    /**
     * Change the padding of the message overlay.
     *
     * @param padding The padding that surrounds the message from the overlay border.
     */
    @PublicAPI
    public MessageOverlay setPadding(int padding) {
        this.padding = padding;

        return this;
    }

    /**
     * Change the maximum size allowed. The default size is {@code 200}.
     *
     * @param maxSize The maximum square size allowed for this overlay.
     */
    @PublicAPI
    public MessageOverlay setMaxSize(int maxSize) {
        this.maxSize = maxSize;

        return this;
    }

    /**
     * How much of the parent screen (current screen being shown by the game) should this overlay take up.
     *
     * @param percentage A percentage between 0.1D-1.0D.
     */
    @PublicAPI
    public MessageOverlay setResizePercentage(double percentage) {
        this.resizePercentage = percentage;

        return this;
    }

    /**
     * Do not center the text. This will align the text to the left side of the overlay window.
     */
    @PublicAPI
    public MessageOverlay alignLeft() {
        this.centerAlign = false;

        return this;
    }

    /**
     * Add a button that will be above the 'okay' button. The button factory's positioning and width data will be
     * overridden by the final overlay is built.
     *
     * @param factory A {@link ButtonBuilder} instance.
     */
    @PublicAPI
    public MessageOverlay addButton(ButtonBuilder factory) {
        this.buttons.add(factory);

        return this;
    }

    /**
     * Finalize the building process of the message overlay.
     *
     * @return A new {@link Overlay} instance.
     */
    public Overlay build() {
        TextureIcon icon = switch (this.messageType) {
            case SUCCESS -> Icons.SMALL_CHECK;
            case SEARCH -> Icons.SMALL_SEARCH;
            case ERROR -> Icons.SMALL_RED_X;
            case HELP -> Icons.SMALL_INFO;
            case WARNING -> Icons.SMALL_WARNING;
            case RED_WARNING -> Icons.SMALL_RED_WARNING;
        };

        Gradient background = switch (this.messageType) {
            case SUCCESS -> Gradient.vertical(new Color(0x325632, 220), new Color(0x212D21, 220));
            case SEARCH -> Gradient.vertical(new Color(0x337087, 220), new Color(0x132A33, 220));
            case HELP -> Gradient.vertical(new Color(0x2A4772, 220), new Color(0x1C304C, 220));
            case ERROR -> Gradient.vertical(new Color(0x632B2B, 220), new Color(0x281111, 220));
            case WARNING -> Gradient.vertical(new Color(0x77500D, 220), new Color(0x2D1F05, 220));
            case RED_WARNING -> Gradient.vertical(new Color(0x632B2A, 220), new Color(0x281111, 220));
        };

        Overlay overlay = this.builder.icon(icon)
                .gradientBackground(background)
                .resizeUsingPercentage(this.resizePercentage, this.maxSize)
                .resizeHeightForWidgets()
                .padding(this.padding)
                .build();

        TextWidget message = TextWidget.create(this.message).extendWidthToScreenEnd(0).build(overlay::addWidget);

        if (this.centerAlign)
            message.getBuilder().centerAligned();

        ButtonWidget last = null;

        for (ButtonBuilder factory : this.buttons)
            last = factory.below(message, this.padding).extendWidthToScreenEnd(0).build(overlay::addWidget);

        ButtonWidget.create(Lang.Button.OKAY)
                .icon(Icons.GREEN_CHECK)
                .below(last != null ? last : message, this.padding)
                .extendWidthToScreenEnd(0)
                .onPress(overlay::close)
                .build(overlay::addWidget);

        return overlay;
    }
}
