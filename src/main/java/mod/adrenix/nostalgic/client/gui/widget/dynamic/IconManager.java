package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IconManager<Widget extends DynamicWidget<?, Widget>> {
    /* Fields */

    private final DynamicBuilder<?, Widget> builder;
    private final Widget widget;
    private boolean isCacheMode = false;
    private IconWidget cache;
    private IconWidget iconWidget;
    private IconWidget hoverIconWidget;
    private IconWidget disabledIconWidget;
    private TextureIcon lastIcon;

    /* Constructor */

    /**
     * This utility class is responsible for creating and caching icon widgets based on the given aligner's icon builder
     * properties. A {@code protected} field should be created for a new {@link IconManager} instance in the aligned
     * widget, so that the widget can access the icon widgets at runtime.
     *
     * @param widget A {@link Widget} instance.
     */
    public IconManager(Widget widget) {
        this.widget = widget;
        this.builder = widget.getBuilder();
    }

    /* Expose Builder */

    /**
     * Get the hovered icon supplier.
     *
     * @param builder A {@link DynamicBuilder} instance.
     * @return A {@link Supplier} that provides a {@link TextureIcon}.
     */
    public static Supplier<TextureIcon> getHovered(DynamicBuilder<?, ?> builder) {
        return builder.hoverIconSupplier;
    }

    /**
     * Get the disabled icon supplier.
     *
     * @param builder A {@link DynamicBuilder} instance.
     * @return A {@link Supplier} that provides a {@link TextureIcon}.
     */
    public static Supplier<TextureIcon> getDisabled(DynamicBuilder<?, ?> builder) {
        return builder.disabledIconSupplier;
    }

    /* Methods */

    /**
     * Get a lightened amount based on whether an icon needs to be brighter.
     *
     * @param widget     A {@link DynamicWidget} instance.
     * @param brightness A predefined brightness amount to return if there is no lightened amount.
     * @return A brightness amount.
     */
    public static float getLightenAmount(DynamicWidget<?, ?> widget, float brightness) {
        if (widget.isHoveredOrFocused() && widget.getBuilder().brightenOnHover)
            return widget.getBuilder().brightenAmount.getAsFloat();

        return brightness;
    }

    /**
     * Get a darkened amount based on whether to darken an icon.
     *
     * @param widget     An {@link DynamicWidget} instance.
     * @param brightness A predefined brightness amount to return if there is no darkened amount.
     * @return A brightness amount.
     */
    public static float getDarkenAmount(DynamicWidget<?, ?> widget, float brightness) {
        if (!widget.isActive() && widget.getBuilder().darkenOnDisable)
            return widget.getBuilder().darkenAmount.getAsFloat();

        return brightness;
    }

    /**
     * Get the icon widget that should be used based on the current state of the widget.
     *
     * @return An {@link IconWidget} instance.
     */
    public IconWidget get() {
        if (this.isCacheMode)
            return this.cache;

        IconWidget toReturn = this.getIconWidget();

        if (this.lastIcon == null || this.lastIcon != toReturn.getIcon())
            this.lastIcon = toReturn.getIcon();

        return toReturn;
    }

    /**
     * Push a new cache value for this manager. This will prevent invoking the icon supplier until the cache is popped
     * using {@link #popCache()}.
     */
    public void pushCache() {
        this.cache = this.get();
        this.isCacheMode = true;
    }

    /**
     * Pop a previous cache value for this manager. This will allow invoking of the icon supplier until the cache is
     * pushed using {@link #pushCache()}.
     */
    public void popCache() {
        this.isCacheMode = false;
        this.cache = null;
    }

    /**
     * @return The {@link IconWidget} that will be shown based on the current game context.
     */
    private IconWidget getIconWidget() {
        IconWidget toReturn;
        IconWidget iconWidget = this.getDefault();
        IconWidget hoverIconWidget = this.getHover();
        IconWidget disabledIconWidget = this.getDisabled();

        if (hoverIconWidget.isEmpty())
            toReturn = iconWidget;
        else
            toReturn = this.widget.isHoveredOrFocused() && this.widget.isActive() ? hoverIconWidget : iconWidget;

        if (this.widget.isInactive() && !disabledIconWidget.isEmpty())
            toReturn = disabledIconWidget;

        return toReturn;
    }

    /**
     * Functional shortcut for {@link IconWidget#pos(int, int)}.
     *
     * @param x The new x-coordinate.
     * @param y The new y-coordinate.
     */
    public void pos(int x, int y) {
        this.get().pos(x, y);
    }

    /**
     * Functional shortcut for {@link IconWidget#getWidth()}.
     *
     * @return The current width of the visible icon.
     */
    public int getWidth() {
        return this.get().getWidth();
    }

    /**
     * Functional shortcut for {@link IconWidget#getHeight()}.
     *
     * @return The current height of the visible icon.
     */
    public int getHeight() {
        return this.get().getHeight();
    }

    /**
     * Functional shortcut for {@link IconWidget#isEmpty()}.
     *
     * @return Whether the current icon being displayed is empty.
     */
    public boolean isEmpty() {
        return this.get().isEmpty();
    }

    /**
     * Functional shortcut for {@link #isEmpty()}.
     *
     * @return Whether the current icon being displayed is present.
     */
    public boolean isPresent() {
        return !this.isEmpty();
    }

    /**
     * Apply all three icon states with the provided {@link Consumer}. This bypasses the need to manually apply changes
     * to all three icon states individually.
     *
     * @param consumer A {@link Consumer} that accepts an {@link IconWidget}.
     */
    public void apply(Consumer<IconWidget> consumer) {
        consumer.accept(this.getDisabled());
        consumer.accept(this.getDefault());
        consumer.accept(this.getHover());
    }

    /**
     * @return The default icon widget.
     */
    public IconWidget getDefault() {
        this.iconWidget = this.iconWidget != null ? this.iconWidget : IconWidget.create(this.builder.iconSupplier)
                                                                      .size(this.builder.iconWidth, this.builder.iconHeight)
                                                                      .build();

        if (this.builder.iconSupplier.get() != this.iconWidget.getIcon()) {
            this.iconWidget.setIcon(this.builder.iconSupplier);
            this.iconWidget.setSize(this.builder.iconWidth, this.builder.iconHeight);
        }

        return this.iconWidget;
    }

    /**
     * This widget should appear when a widget is hovered by the mouse, or is focused by selection.
     *
     * @return The hover icon widget.
     */
    public IconWidget getHover() {
        this.hoverIconWidget = this.hoverIconWidget != null ? this.hoverIconWidget : IconWidget.create(this.builder.hoverIconSupplier)
                                                                                     .size(this.builder.hoverIconWidth, this.builder.hoverIconHeight)
                                                                                     .build();

        if (this.builder.hoverIconSupplier.get() != this.hoverIconWidget.getIcon()) {
            this.hoverIconWidget.setIcon(this.builder.hoverIconSupplier);
            this.hoverIconWidget.setSize(this.builder.hoverIconWidth, this.builder.hoverIconHeight);
        }

        return this.hoverIconWidget;
    }

    /**
     * This widget should appear when a widget {@code active} field is {@code false}.
     *
     * @return The disabled icon widget.
     */
    public IconWidget getDisabled() {
        this.disabledIconWidget = this.disabledIconWidget != null ? this.disabledIconWidget : IconWidget.create(this.builder.disabledIconSupplier)
                                                                                              .size(this.builder.disabledIconWidth, this.builder.disabledIconHeight)
                                                                                              .build();

        if (this.builder.disabledIconSupplier.get() != this.disabledIconWidget.getIcon()) {
            this.disabledIconWidget.setIcon(this.builder.disabledIconSupplier);
            this.disabledIconWidget.setSize(this.builder.disabledIconWidth, this.builder.disabledIconHeight);
        }

        return this.disabledIconWidget;
    }

    /**
     * Handler method for rendering the current icon.
     *
     * @param graphics    A {@link GuiGraphicsExtractor} instance.
     * @param mouseX      The x-position of the mouse.
     * @param mouseY      The y-position of the mouse.
     * @param partialTick The normalized progress made between two ticks [0.0F, 1.0F].
     */
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        float brightness = 1.0F;

        if (this.getHover().isEmpty())
            brightness = getLightenAmount(this.widget, brightness);

        if (this.getDisabled().isEmpty())
            brightness = getDarkenAmount(this.widget, brightness);

        this.get().extractRenderState(graphics, mouseX, mouseY, partialTick, brightness);
    }
}
