package mod.adrenix.nostalgic.client.gui.widget.icon;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.*;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;

import java.util.function.Supplier;

public class IconFactory extends DynamicBuilder<IconFactory, IconWidget>
        implements LayoutBuilder<IconFactory, IconWidget>, IconBuilder<IconFactory, IconWidget>,
        TooltipBuilder<IconFactory, IconWidget>, ActiveBuilder<IconFactory, IconWidget>,
        VisibleBuilder<IconFactory, IconWidget> {
    final Supplier<TextureIcon> icon;

    int emptyWidth = 0;
    int emptyHeight = 0;
    double zOffset = 0.0D;
    Supplier<TextureIcon> pressIcon = null;
    Runnable onPress = null;

    protected IconFactory(Supplier<TextureIcon> icon) {
        this.icon = icon;
        this.canFocus = () -> this.onPress != null;

        this.width(icon.get().getWidth());
        this.height(icon.get().getHeight());
    }

    @Override
    public IconFactory self() {
        return this;
    }

    /**
     * Set the width and height of an empty icon. The default size for an empty icon is {@code zero}.
     *
     * @param width  The width of the empty icon.
     * @param height The height of the empty icon.
     */
    @PublicAPI
    public IconFactory emptySize(int width, int height) {
        this.emptyWidth = width;
        this.emptyHeight = height;

        return this;
    }

    /**
     * Set the size of an empty icon. The default size for an empty icon is {@code zero}.
     *
     * @param size A square size for the empty icon.
     */
    @PublicAPI
    public IconFactory emptySize(int size) {
        this.emptyWidth = size;
        this.emptyHeight = size;

        return this;
    }

    /**
     * Set the z-offset of the icon for when it is drawn to the screen.
     *
     * @param offset An offset amount.
     */
    @PublicAPI
    public IconFactory zOffset(double offset) {
        this.zOffset = offset;

        return this;
    }

    /**
     * Instructions to perform when this icon widget is successfully pressed.
     *
     * @param runnable A runnable function.
     */
    @PublicAPI
    public IconFactory onPress(Runnable runnable) {
        this.onPress = runnable;
        return this;
    }

    /**
     * An icon to render when the mouse is depressing this icon widget.
     *
     * @param icon An icon instance.
     * @see IconFactory#pressIcon(Supplier)
     */
    @PublicAPI
    public IconFactory pressIcon(TextureIcon icon) {
        this.pressIcon = () -> icon;
        return this;
    }

    /**
     * An icon to render when the mouse is depressing this icon widget.
     *
     * @param supplier An icon supplier.
     * @see IconFactory#pressIcon(TextureIcon)
     */
    @PublicAPI
    public IconFactory pressIcon(Supplier<TextureIcon> supplier) {
        this.pressIcon = supplier;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IconWidget construct() {
        return new IconWidget(this);
    }
}
