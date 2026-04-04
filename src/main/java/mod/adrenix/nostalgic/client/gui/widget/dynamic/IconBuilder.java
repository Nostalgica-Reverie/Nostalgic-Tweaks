package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.function.FloatSupplier;

import java.util.function.Supplier;

/**
 * Any builder that extends this interface will be creating a widget that uses icons. There are three states available
 * that different icons can appear in; visible, hovered, and disabled. It is up to the widget using this interface on
 * how and when these icons are displayed.
 *
 * <p>
 * <b color=red>Note:</b> Widget builders should use the {@link IconManager} utility class to manage a widget's icons.
 * The manager will create and cache icon widgets based on a widget builder's icon properties and render the right icon
 * based on a widget's current state.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that is implementing {@link DynamicWidget}.
 * @see IconManager
 */
public interface IconBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
        extends SelfBuilder<Builder, Widget> {
    /**
     * Define the icon that will be used by this widget.
     *
     * @param icon A {@link TextureIcon} instance.
     * @see #icon(TextureIcon, int)
     * @see #icon(TextureIcon, int, int)
     * @see #icon(Supplier)
     * @see #icon(Supplier, int)
     * @see #icon(Supplier, int, int)
     */
    @PublicAPI
    default Builder icon(TextureIcon icon) {
        return this.icon(() -> icon);
    }

    /**
     * Define the icon that will be used by this widget and the size of the icon when rendering.
     *
     * @param icon A {@link TextureIcon} instance.
     * @param size The square size to use when rendering the icon.
     * @see #icon(TextureIcon)
     * @see #icon(TextureIcon, int, int)
     * @see #icon(Supplier)
     * @see #icon(Supplier, int)
     * @see #icon(Supplier, int, int)
     */
    @PublicAPI
    default Builder icon(TextureIcon icon, int size) {
        return this.icon(() -> icon, size);
    }

    /**
     * Define the icon that will be used by this widget and the width/height of the icon when rendering.
     *
     * @param icon   A {@link TextureIcon} instance.
     * @param width  The width to use when rendering the icon.
     * @param height The height to use when rendering the icon.
     * @see #icon(TextureIcon)
     * @see #icon(TextureIcon, int)
     * @see #icon(Supplier)
     * @see #icon(Supplier, int)
     * @see #icon(Supplier, int, int)
     */
    @PublicAPI
    default Builder icon(TextureIcon icon, int width, int height) {
        return this.icon(() -> icon, width, height);
    }

    /**
     * Define an icon supplier for this widget.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @see #icon(TextureIcon)
     * @see #icon(TextureIcon, int)
     * @see #icon(TextureIcon, int, int)
     * @see #icon(Supplier, int)
     * @see #icon(Supplier, int, int)
     */
    @PublicAPI
    default Builder icon(Supplier<TextureIcon> supplier) {
        this.self().iconSupplier = supplier;
        this.self().iconWidth = supplier.get().getWidth();
        this.self().iconHeight = supplier.get().getHeight();

        return this.self();
    }

    /**
     * Define an icon supplier for this widget and the size that will be used during rendering.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @param size     The square size to use when rendering the icon.
     * @see #icon(TextureIcon)
     * @see #icon(TextureIcon, int)
     * @see #icon(TextureIcon, int, int)
     * @see #icon(Supplier)
     * @see #icon(Supplier, int, int)
     */
    @PublicAPI
    default Builder icon(Supplier<TextureIcon> supplier, int size) {
        this.self().iconSupplier = supplier;
        this.self().iconWidth = size;
        this.self().iconHeight = size;

        return this.self();
    }

    /**
     * Define an icon supplier for this widget and the width/height that will be used during rendering.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @param width    The width to use when rendering the icon.
     * @param height   The height to use when rendering the icon.
     * @see #icon(TextureIcon)
     * @see #icon(TextureIcon, int)
     * @see #icon(TextureIcon, int, int)
     * @see #icon(Supplier)
     * @see #icon(Supplier, int)
     */
    @PublicAPI
    default Builder icon(Supplier<TextureIcon> supplier, int width, int height) {
        this.self().iconSupplier = supplier;
        this.self().iconWidth = width;
        this.self().iconHeight = height;

        return this.self();
    }

    /**
     * Define a mouse hover/focused icon that will be used by this widget.
     *
     * @param icon A {@link TextureIcon} instance.
     * @see #hoverIcon(TextureIcon, int)
     * @see #hoverIcon(TextureIcon, int, int)
     * @see #hoverIcon(Supplier)
     * @see #hoverIcon(Supplier, int)
     * @see #hoverIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder hoverIcon(TextureIcon icon) {
        return this.hoverIcon(() -> icon);
    }

    /**
     * Define a mouse hover/focused icon that will be used by this widget and the size of the icon when rendering.
     *
     * @param icon A {@link TextureIcon} instance.
     * @param size The square size to use when rendering the icon.
     * @see #hoverIcon(TextureIcon)
     * @see #hoverIcon(TextureIcon, int, int)
     * @see #hoverIcon(Supplier)
     * @see #hoverIcon(Supplier, int)
     * @see #hoverIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder hoverIcon(TextureIcon icon, int size) {
        return this.hoverIcon(() -> icon, size);
    }

    /**
     * Define a mouse hover/focused icon that will be used by this widget and the width/height of the icon when
     * rendering.
     *
     * @param icon   A {@link TextureIcon} instance.
     * @param width  The width to use when rendering the icon.
     * @param height The height to use when rendering the icon.
     * @see #hoverIcon(TextureIcon)
     * @see #hoverIcon(TextureIcon, int)
     * @see #hoverIcon(Supplier)
     * @see #hoverIcon(Supplier, int)
     * @see #hoverIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder hoverIcon(TextureIcon icon, int width, int height) {
        return this.hoverIcon(() -> icon, width, height);
    }

    /**
     * Define a mouse hover/focused icon supplier for this widget.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @see #hoverIcon(TextureIcon)
     * @see #hoverIcon(TextureIcon, int)
     * @see #hoverIcon(TextureIcon, int, int)
     * @see #hoverIcon(Supplier, int)
     * @see #hoverIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder hoverIcon(Supplier<TextureIcon> supplier) {
        this.self().hoverIconSupplier = supplier;
        this.self().hoverIconWidth = supplier.get().getWidth();
        this.self().hoverIconHeight = supplier.get().getHeight();

        return this.self();
    }

    /**
     * Define a mouse hover/focused icon supplier for this widget and the size that will be used during rendering.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @param size     The square size to use when rendering the icon.
     * @see #hoverIcon(TextureIcon)
     * @see #hoverIcon(TextureIcon, int)
     * @see #hoverIcon(TextureIcon, int, int)
     * @see #hoverIcon(Supplier)
     * @see #hoverIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder hoverIcon(Supplier<TextureIcon> supplier, int size) {
        this.self().hoverIconSupplier = supplier;
        this.self().hoverIconWidth = size;
        this.self().hoverIconHeight = size;

        return this.self();
    }

    /**
     * Define a mouse hover/focused icon supplier for this widget and the width/height that will be used during
     * rendering.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @param width    The width to use when rendering the icon.
     * @param height   The height to use when rendering the icon.
     * @see #hoverIcon(TextureIcon)
     * @see #hoverIcon(TextureIcon, int)
     * @see #hoverIcon(TextureIcon, int, int)
     * @see #hoverIcon(Supplier)
     * @see #hoverIcon(Supplier, int)
     */
    @PublicAPI
    default Builder hoverIcon(Supplier<TextureIcon> supplier, int width, int height) {
        this.self().hoverIconSupplier = supplier;
        this.self().hoverIconWidth = width;
        this.self().hoverIconHeight = height;

        return this.self();
    }

    /**
     * Define the widget disabled icon that will be used by this widget.
     *
     * @param icon A {@link TextureIcon} instance.
     * @see #disabledIcon(TextureIcon, int)
     * @see #disabledIcon(TextureIcon, int, int)
     * @see #disabledIcon(Supplier)
     * @see #disabledIcon(Supplier, int)
     * @see #disabledIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder disabledIcon(TextureIcon icon) {
        return this.disabledIcon(() -> icon);
    }

    /**
     * Define the widget disabled icon that will be used by this widget and the size of the icon when rendering.
     *
     * @param icon A {@link TextureIcon} instance.
     * @param size The square size to use when rendering the icon.
     * @see #disabledIcon(TextureIcon)
     * @see #disabledIcon(TextureIcon, int, int)
     * @see #disabledIcon(Supplier)
     * @see #disabledIcon(Supplier, int)
     * @see #disabledIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder disabledIcon(TextureIcon icon, int size) {
        return this.disabledIcon(() -> icon, size);
    }

    /**
     * Define the widget disabled icon that will be used by this widget and the width/height of the icon when
     * rendering.
     *
     * @param icon   A {@link TextureIcon} instance.
     * @param width  The width to use when rendering the icon.
     * @param height The height to use when rendering the icon.
     * @see #disabledIcon(TextureIcon)
     * @see #disabledIcon(TextureIcon, int)
     * @see #disabledIcon(Supplier)
     * @see #disabledIcon(Supplier, int)
     * @see #disabledIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder disabledIcon(TextureIcon icon, int width, int height) {
        return this.disabledIcon(() -> icon, width, height);
    }

    /**
     * Define a disabled widget icon supplier for this widget.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @see #disabledIcon(TextureIcon)
     * @see #disabledIcon(TextureIcon, int)
     * @see #disabledIcon(TextureIcon, int, int)
     * @see #disabledIcon(Supplier, int)
     * @see #disabledIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder disabledIcon(Supplier<TextureIcon> supplier) {
        this.self().disabledIconSupplier = supplier;
        this.self().disabledIconWidth = supplier.get().getWidth();
        this.self().disabledIconHeight = supplier.get().getHeight();

        return this.self();
    }

    /**
     * Define a disabled widget icon supplier for this widget and the size that will be used during rendering.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @param size     The square size to use when rendering the icon.
     * @see #disabledIcon(TextureIcon)
     * @see #disabledIcon(TextureIcon, int)
     * @see #disabledIcon(TextureIcon, int, int)
     * @see #disabledIcon(Supplier)
     * @see #disabledIcon(Supplier, int, int)
     */
    @PublicAPI
    default Builder disabledIcon(Supplier<TextureIcon> supplier, int size) {
        this.self().disabledIconSupplier = supplier;
        this.self().disabledIconWidth = size;
        this.self().disabledIconHeight = size;

        return this.self();
    }

    /**
     * Define a disabled widget icon supplier for this widget and the width/height that will be used during rendering.
     *
     * @param supplier A {@link TextureIcon} supplier.
     * @param width    The width to use when rendering the icon.
     * @param height   The height to use when rendering the icon.
     * @see #disabledIcon(TextureIcon)
     * @see #disabledIcon(TextureIcon, int)
     * @see #disabledIcon(TextureIcon, int, int)
     * @see #disabledIcon(Supplier)
     * @see #disabledIcon(Supplier, int)
     */
    @PublicAPI
    default Builder disabledIcon(Supplier<TextureIcon> supplier, int width, int height) {
        this.self().disabledIconSupplier = supplier;
        this.self().disabledIconWidth = width;
        this.self().disabledIconHeight = height;

        return this.self();
    }

    /**
     * Brighten the visible icon using the given supplier amount when the mouse hovers over the widget or when the
     * widget is focused. This is an alternative to assigning a specific icon for hovering.
     *
     * @param supplier A supplier that provides the amount to brighten by.
     * @see #brightenOnHover(float)
     * @see #brightenOnHover()
     */
    @PublicAPI
    default Builder brightenOnHover(FloatSupplier supplier) {
        this.self().brightenOnHover = true;
        this.self().brightenAmount = supplier;

        return this.self();
    }

    /**
     * Brighten the visible icon using the given amount when the mouse hovers over the widget or when the widget is
     * focused. This is an alternative to assigning a specific icon for hovering.
     *
     * @param amount An amount to brighten by. The default brightness is 1.0F, to brighten the icon slightly it is
     *               recommended to provide a value of 1.1F.
     * @see #brightenOnHover(FloatSupplier)
     * @see #brightenOnHover()
     */
    @PublicAPI
    default Builder brightenOnHover(float amount) {
        final float supply = Math.max(0.0F, amount);

        return this.brightenOnHover(() -> supply);
    }

    /**
     * Brighten the visible icon when the mouse hovers over the widget or when the widget is focused. The brightness
     * applied is defined by the icon currently being displayed.
     *
     * @see #brightenOnHover(FloatSupplier)
     * @see #brightenOnHover(float)
     */
    @PublicAPI
    default Builder brightenOnHover() {
        this.self().brightenOnHover = true;
        return this.self();
    }

    /**
     * Some widgets may automatically enable brightening an icon when the mouse hovers over the button. If this is not
     * desired, then use this builder method to disable this automation.
     */
    @PublicAPI
    default Builder disableBrightenOnHover() {
        this.self().brightenOnHover = false;
        return this.self();
    }

    /**
     * Darken the visible icon using the given supplier amount when the widget becomes disabled. This effect will only
     * be applied if there is no disabled icon widget assigned.
     *
     * @param supplier A supplier that provides the amount to darken by.
     * @see #darkenOnDisable(float)
     * @see #disableDarkenOnDisable()
     */
    @PublicAPI
    default Builder darkenOnDisable(FloatSupplier supplier) {
        this.self().darkenOnDisable = true;
        this.self().darkenAmount = supplier;

        return this.self();
    }

    /**
     * Darken the visible icon using the given amount when the widget becomes disabled. This effect will only be applied
     * if there is no disable icon widget assigned. This feature is {@code enabled} by default with a darken amount of
     * {@code 0.4F}.
     *
     * @param amount An amount to darken by. The default brightness is 1.0F, to darken the icon it is recommended to
     *               provide a value of 0.4F.
     * @see #darkenOnDisable(FloatSupplier)
     * @see #disableDarkenOnDisable()
     */
    @PublicAPI
    default Builder darkenOnDisable(float amount) {
        final float supply = Math.max(0.0F, amount);

        return this.darkenOnDisable(() -> supply);
    }

    /**
     * Disable darkening the visible icon when the widget becomes disabled. Darkening the icon when a widget is disabled
     * is enabled by default since it is a viable alternative to assigning a specific icon when the widget becomes
     * disabled. If there is already a disabled icon assigned to the widget, then there is no need to call this method.
     *
     * @see #darkenOnDisable(FloatSupplier)
     * @see #darkenOnDisable(float)
     */
    @PublicAPI
    default Builder disableDarkenOnDisable() {
        this.self().darkenOnDisable = false;

        return this.self();
    }
}
