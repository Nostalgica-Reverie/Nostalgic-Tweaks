package mod.adrenix.nostalgic.client.gui.widget.icon;

import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.Icons;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;

import java.util.function.Supplier;

public abstract class IconTemplate {
    /**
     * Create a button icon from the given textures.
     *
     * @param icon    The {@link TextureIcon} of the button.
     * @param hover   The {@link TextureIcon} when the button is hovered by the mouse or when the button is focused.
     * @param pressed The {@link TextureIcon} when the button is held down by the mouse.
     * @return A {@link IconFactory} instance.
     */
    @PublicAPI
    public static IconFactory button(TextureIcon icon, TextureIcon hover, TextureIcon pressed) {
        return IconWidget.create(icon).hoverIcon(hover).disabledIcon(pressed).pressIcon(pressed);
    }

    /**
     * Create a button icon from the given textures.
     *
     * @param icon    A {@link Supplier} that provides the {@link TextureIcon} of the button.
     * @param hover   A {@link Supplier} that provides the {@link TextureIcon} when the button is hovered by the mouse
     *                or when the button is focused.
     * @param pressed A {@link Supplier} that provides the {@link TextureIcon} when the button is held down by the
     *                mouse.
     * @return A {@link IconFactory} instance.
     */
    @PublicAPI
    public static IconFactory button(Supplier<TextureIcon> icon, Supplier<TextureIcon> hover, Supplier<TextureIcon> pressed) {
        return IconWidget.create(icon).hoverIcon(hover).disabledIcon(pressed).pressIcon(pressed);
    }

    /**
     * This icon widget template is used for displaying a small "x" close icon that can act as a button.
     *
     * @return An {@link IconFactory} instance so that additional properties can be defined before building.
     */
    @PublicAPI
    public static IconFactory close() {
        return IconWidget.create(Icons.GENERIC_CLOSE)
                .hoverIcon(Icons.GENERIC_CLOSE_HOVER)
                .disabledIcon(Icons.GENERIC_CLOSE_OFF)
                .pressIcon(Icons.GENERIC_CLOSE_OFF);
    }

    /**
     * This icon widget template is used for displaying a small "!" info icon that can act as a button.
     *
     * @return An {@link IconFactory} instance so that additional properties can be defined before building.
     */
    @PublicAPI
    public static IconFactory info() {
        return IconWidget.create(Icons.GENERIC_INFO)
                .hoverIcon(Icons.GENERIC_INFO_HOVER)
                .disabledIcon(Icons.GENERIC_INFO_OFF)
                .pressIcon(Icons.GENERIC_INFO_OFF);
    }

    /**
     * This icon widget template is used for displaying a small circular "i" icon that can act as a button.
     *
     * @return An {@link IconFactory} instance so that additional properties can be defined before building.
     */
    @PublicAPI
    public static IconFactory help() {
        return IconWidget.create(Icons.SMALL_INFO)
                .hoverIcon(Icons.SMALL_INFO_HOVER)
                .disabledIcon(Icons.SMALL_INFO_OFF)
                .pressIcon(Icons.SMALL_INFO_OFF);
    }

    /**
     * This icon widget template is used for context menu buttons "..."
     *
     * @return An {@link IconFactory} instance so that additional properties can be defined before building.
     */
    @PublicAPI
    public static IconFactory menu() {
        return IconWidget.create(Icons.THREE_DOTS)
                .hoverIcon(Icons.THREE_DOTS_HOVER)
                .disabledIcon(Icons.THREE_DOTS_OFF)
                .pressIcon(Icons.THREE_DOTS_OFF);
    }

    /**
     * Create an icon widget that matches the current font's line height.
     *
     * @param icon A {@link TextureIcon} instance.
     * @return An {@link IconFactory} instance so that addition properties can be defined.
     */
    @PublicAPI
    public static IconFactory text(TextureIcon icon) {
        return IconWidget.create(icon).size(GuiUtil.textHeight() - 1);
    }

    /**
     * Create an icon widget that matches the current font's line height.
     *
     * @param supplier A {@link Supplier} that provides a {@link TextureIcon}.
     * @return An {@link IconFactory} instance so that addition properties can be defined.
     */
    @PublicAPI
    public static IconFactory text(Supplier<TextureIcon> supplier) {
        return IconWidget.create(supplier).size(GuiUtil.textHeight() - 1);
    }
}
