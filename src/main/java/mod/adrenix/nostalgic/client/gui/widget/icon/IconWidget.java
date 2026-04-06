package mod.adrenix.nostalgic.client.gui.widget.icon;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.IconManager;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import mod.adrenix.nostalgic.util.common.asset.TextureLocation;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix3x2f;

import java.util.function.Supplier;

public class IconWidget extends DynamicWidget<IconFactory, IconWidget> {
    /* Builders */

    private final Supplier<TextureIcon> pressIcon;
    private final Runnable onPress;

    /* Fields */
    private Supplier<TextureIcon> icon;
    private boolean holding;
    private double zOffset;

    /**
     * Create a new icon widget instance.
     *
     * @param builder A {@link IconFactory} instance.
     */
    protected IconWidget(IconFactory builder) {
        super(builder);

        this.icon = builder.icon;
        this.pressIcon = builder.pressIcon;
        this.onPress = builder.onPress;
        this.zOffset = builder.zOffset;

        if (this.isEmpty()) {
            this.setWidth(builder.emptyWidth);
            this.setHeight(builder.emptyHeight);
        }
    }

    /**
     * Create a new icon widget using a factory builder.
     *
     * @param icon The icon that will be used by this widget.
     * @return A widget builder instance.
     */
    public static IconFactory create(TextureIcon icon) {
        return new IconFactory(() -> icon);
    }

    /* Constructor */

    /**
     * Create a new icon widget using a factory builder.
     *
     * @param supplier The icon supplier that will be used by this widget.
     * @return A widget builder instance.
     */
    public static IconFactory create(Supplier<TextureIcon> supplier) {
        return new IconFactory(supplier);
    }

    /* Methods */

    /**
     * @return The {@link TextureIcon} this widget is using.
     */
    @PublicAPI
    public TextureIcon getIcon() {
        if (this.icon == null)
            return TextureIcon.EMPTY;

        return this.icon.get();
    }

    /**
     * Change the icon supplier for this widget.
     *
     * @param supplier A {@link TextureIcon} supplier.
     */
    @PublicAPI
    public void setIcon(Supplier<TextureIcon> supplier) {
        this.icon = supplier;
    }

    /**
     * @return Whether this icon widget was made using the {@link TextureIcon#EMPTY} instance.
     */
    @PublicAPI
    public boolean isEmpty() {
        return this.getIcon().isEmpty();
    }

    /**
     * Set the width and height of this icon widget. The icon renderer will render the icon so that it fits in the given
     * dimensions.
     *
     * @param width  The new width.
     * @param height The new height.
     */
    @PublicAPI
    public void setSize(int width, int height) {
        this.setWidth(width);
        this.setHeight(height);
    }

    /**
     * Set both the width and height of this icon widget using the given size. The icon renderer will render the icon so
     * that it fits in the given dimensions.
     *
     * @param size The new width and height of this widget.
     */
    @PublicAPI
    public void setSize(int size) {
        this.setSize(size, size);
    }

    /**
     * Change the z-offset when rendering the icon.
     *
     * @param offset A z-offset.
     */
    @PublicAPI
    public void setZOffset(double offset) {
        this.zOffset = offset;
    }

    /**
     * @return The ratio between this widget's width and height.
     */
    private float getAverageWidgetSize() {
        return (this.width + this.height) / 2.0F;
    }

    /**
     * Get the average icon size.
     *
     * @param icon The {@link TextureIcon} to get size data from.
     * @return The ratio between this widget's {@link TextureIcon} width and height.
     */
    private float getAverageIconSize(TextureIcon icon) {
        return (icon.getWidth() + icon.getHeight()) / 2.0F;
    }

    /**
     * If this icon uses a texture location, then the sheet's size will be used to calculate the scale. If this icon
     * uses an item or block reference, then the width/height will be 16. Otherwise, the width/height that is associated
     * with the icon's (u, v) coordinates is used to calculate the scale.
     *
     * @param icon The {@link TextureIcon} to get the scale of.
     * @return The scale the icon should be rendered at to match this widget's width and height.
     */
    private float getTextureScale(TextureIcon icon) {
        if (icon.getTextureLocation().isPresent())
            return this.getAverageWidgetSize() / icon.getTextureLocation().get().getAverageSize();

        return this.getAverageWidgetSize() / this.getAverageIconSize(icon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        boolean isHandled = KeyboardUtil.isEnterLike(event.key()) || KeyboardUtil.match(event.key(), InputConstants.KEY_SPACE);

        if (this.isFocused() && isHandled && this.onPress != null) {
            GuiUtil.playClick();
            this.onPress.run();

            return true;
        }

        return super.keyPressed(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isValidClick(event) && this.onPress != null) {
            this.holding = true;
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.onPress == null || !this.holding)
            return false;

        if (this.isValidClick(event)) {
            GuiUtil.playClick();
            this.onPress.run();
        }

        this.holding = false;

        return true;
    }

    /**
     * Render instructions for the widget.
     *
     * @param graphics    The {@link GuiGraphicsExtractor} object used for rendering.
     * @param mouseX      The x-coordinate of the mouse cursor.
     * @param mouseY      The y-coordinate of the mouse cursor.
     * @param partialTick The normalized progress between two ticks [0.0F, 1.0F].
     * @param brightness  The brightness of the icon.
     */
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, float brightness) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (this.isEmpty() || this.isInvisible())
            return;

        TextureIcon hoverIcon = IconManager.getHovered(this.getBuilder()).get();
        TextureIcon disabledIcon = IconManager.getDisabled(this.getBuilder()).get();

        boolean isHoverEmpty = hoverIcon == TextureIcon.EMPTY;
        boolean isDisabledEmpty = disabledIcon == TextureIcon.EMPTY;

        if (brightness == 1.0F) {
            if (isHoverEmpty)
                brightness = IconManager.getLightenAmount(this, brightness);

            if (isDisabledEmpty)
                brightness = IconManager.getDarkenAmount(this, brightness);
        }

        if (this.holding && this.pressIcon != null)
            this.renderIcon(this.pressIcon.get(), graphics, brightness);
        else if (this.isHoveredOrFocused() && this.isActive() && !isHoverEmpty)
            this.renderIcon(hoverIcon, graphics, brightness);
        else if (this.isInactive() && !isDisabledEmpty)
            this.renderIcon(disabledIcon, graphics, brightness);
        else
            this.renderIcon(this.getIcon(), graphics, brightness);

        this.renderDebug(graphics);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        this.extractRenderState(graphics, mouseX, mouseY, partialTick, 1.0F);
    }

    /**
     * Render an icon.
     *
     * @param icon       The icon instance to render.
     * @param graphics   A {@link GuiGraphicsExtractor} instance.
     * @param brightness The brightness to use for the icon.
     */
    private void renderIcon(TextureIcon icon, GuiGraphicsExtractor graphics, float brightness) {
        float scale = this.getTextureScale(icon);
        int x = this.x;
        int y = this.y;

        float normalBrightness = Mth.clamp(brightness, 0.0F, 1.0F);
        int color = ARGB.colorFromFloat(1.0F, normalBrightness, normalBrightness, normalBrightness);

        if (icon.getTextureLocation().isPresent()) {
            TextureLocation location = icon.getTextureLocation().get();
            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y);
            graphics.pose().scale(scale, scale);
            graphics.blit(RenderPipelines.GUI_TEXTURED, location.location(), 0, 0, 0.0F, 0.0F, location.width(), location.height(), location.width(), location.height(), color);

            //TODO: maybe deduplicate this, and clean it up, also unsure about values >2
            if (brightness > 1.0F) {
                int base = (int)brightness;
                float fraction = brightness - base;

                color = ARGB.colorFromFloat(1.0F, fraction, fraction, fraction);
                graphics.blit(RenderPipelines.GUI_NAUSEA_OVERLAY, location.location(), 0, 0, 0.0F, 0.0F, location.width(), location.height(), location.width(), location.height(), color);
            }

            graphics.pose().popMatrix();

            this.renderDebug(graphics);

            return;
        }

        if (icon.getSpriteLocation().isPresent()) {
            int width = icon.getWidth();
            int height = icon.getHeight();

            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y);
            graphics.pose().scale(scale, scale);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon.getSpriteLocation().get(), 0, 0, width, height, color);

            //TODO: maybe deduplicate this, and clean it up, also unsure about values >2
            if (brightness > 1.0F) {
                int base = (int)brightness;
                float fraction = brightness - base;

                color = ARGB.colorFromFloat(1.0F, fraction, fraction, fraction);
                graphics.blitSprite(RenderPipelines.GUI_NAUSEA_OVERLAY, icon.getSpriteLocation().get(), 0, 0, width, height, color);
            }

            graphics.pose().popMatrix();

            this.renderDebug(graphics);

            return;
        }

        Item item = icon.getItem().orElse(icon.getBlock().orElse(Blocks.AIR).asItem());

        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.fakeItem(item.getDefaultInstance(), 0, 0); //TODO, BRIGHTNESS
        graphics.pose().popMatrix();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderDebug(GuiGraphicsExtractor graphics) {
        if (this.isNotDebugging() || this.isInvisible())
            return;

        final Matrix3x2f position = new Matrix3x2f(graphics.pose());

        int startX = this.x;
        int startY = this.y;
        int endX = this.getEndX();
        int endY = this.getEndY();

        graphics.pose().pushMatrix();
        graphics.pose().identity();
        graphics.pose().mul(position);

        graphics.fill(startX, startY, startX + 1, startY + 1, 0xFFFF0000);
        graphics.fill(endX - 1, startY, endX, startY + 1, 0xFF00FF00);
        graphics.fill(startX, endY - 1, startX + 1, endY, 0xFF00FFFF);
        graphics.fill(endX - 1, endY - 1, endX, endY, 0xFFFF00FF);

        graphics.pose().popMatrix();
    }
}
