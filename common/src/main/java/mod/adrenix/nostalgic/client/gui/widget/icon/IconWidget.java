package mod.adrenix.nostalgic.client.gui.widget.icon;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.IconManager;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.TextureIcon;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import org.joml.Matrix4f;

import java.util.function.Supplier;

public class IconWidget extends DynamicWidget<IconFactory, IconWidget>
{
    /* Builders */

    /**
     * Create a new icon widget using a factory builder.
     *
     * @param icon The icon that will be used by this widget.
     * @return A widget builder instance.
     */
    public static IconFactory create(TextureIcon icon)
    {
        return new IconFactory(() -> icon);
    }

    /**
     * Create a new icon widget using a factory builder.
     *
     * @param supplier The icon supplier that will be used by this widget.
     * @return A widget builder instance.
     */
    public static IconFactory create(Supplier<TextureIcon> supplier)
    {
        return new IconFactory(supplier);
    }

    /* Fields */

    private Supplier<TextureIcon> icon;
    private final Supplier<TextureIcon> pressIcon;
    private final Runnable onPress;

    private boolean holding;
    private double zOffset;

    /* Constructor */

    /**
     * Create a new icon widget instance.
     *
     * @param builder A {@link IconFactory} instance.
     */
    protected IconWidget(IconFactory builder)
    {
        super(builder);

        this.icon = builder.icon;
        this.pressIcon = builder.pressIcon;
        this.onPress = builder.onPress;
        this.zOffset = builder.zOffset;

        if (this.isEmpty())
        {
            this.setWidth(builder.emptyWidth);
            this.setHeight(builder.emptyHeight);
        }
    }

    /* Methods */

    /**
     * Change the icon supplier for this widget.
     *
     * @param supplier A {@link TextureIcon} supplier.
     */
    @PublicAPI
    public void setIcon(Supplier<TextureIcon> supplier)
    {
        this.icon = supplier;
    }

    /**
     * @return The {@link TextureIcon} this widget is using.
     */
    @PublicAPI
    public TextureIcon getIcon()
    {
        if (this.icon == null)
            return TextureIcon.EMPTY;

        return this.icon.get();
    }

    /**
     * @return Whether this icon widget was made using the {@link TextureIcon#EMPTY} instance.
     */
    @PublicAPI
    public boolean isEmpty()
    {
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
    public void setSize(int width, int height)
    {
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
    public void setSize(int size)
    {
        this.setSize(size, size);
    }

    /**
     * Change the z-offset when rendering the icon.
     *
     * @param offset A z-offset.
     */
    @PublicAPI
    public void setZOffset(double offset)
    {
        this.zOffset = offset;
    }

    /**
     * @return The ratio between this widget's width and height.
     */
    private float getAverageWidgetSize()
    {
        return (this.width + this.height) / 2.0F;
    }

    /**
     * Get the average icon size.
     *
     * @param icon The {@link TextureIcon} to get size data from.
     * @return The ratio between this widget's {@link TextureIcon} width and height.
     */
    private float getAverageIconSize(TextureIcon icon)
    {
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
    private float getTextureScale(TextureIcon icon)
    {
        if (icon.getTextureLocation().isPresent())
            return this.getAverageWidgetSize() / icon.getTextureLocation().get().getAverageSize();

        return this.getAverageWidgetSize() / this.getAverageIconSize(icon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        boolean isHandled = KeyboardUtil.isEnterLike(keyCode) || KeyboardUtil.match(keyCode, InputConstants.KEY_SPACE);

        if (this.isFocused() && isHandled && this.onPress != null)
        {
            GuiUtil.playClick();
            this.onPress.run();

            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isValidClick(mouseX, mouseY, button) && this.onPress != null)
        {
            this.holding = true;
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (this.onPress == null || !this.holding)
            return false;

        if (this.isValidClick(mouseX, mouseY, button))
        {
            GuiUtil.playClick();
            this.onPress.run();
        }

        this.holding = false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isEmpty() || this.isInvisible())
            return;

        TextureIcon hoverIcon = IconManager.getHovered(this.getBuilder()).get();
        TextureIcon disabledIcon = IconManager.getDisabled(this.getBuilder()).get();

        boolean isHoverEmpty = hoverIcon == TextureIcon.EMPTY;
        boolean isDisabledEmpty = disabledIcon == TextureIcon.EMPTY;

        if (RenderSystem.getShaderColor()[0] == 1.0F)
        {
            float brightness = 1.0F;

            if (isHoverEmpty)
                brightness = IconManager.getLightenAmount(this, brightness);

            if (isDisabledEmpty)
                brightness = IconManager.getDarkenAmount(this, brightness);

            RenderSystem.setShaderColor(brightness, brightness, brightness, 1.0F);
        }

        if (this.holding && this.pressIcon != null)
            this.renderIcon(this.pressIcon.get(), graphics);
        else if (this.isHoveredOrFocused() && this.isActive() && !isHoverEmpty)
            this.renderIcon(hoverIcon, graphics);
        else if (this.isInactive() && !isDisabledEmpty)
            this.renderIcon(disabledIcon, graphics);
        else
            this.renderIcon(this.getIcon(), graphics);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.renderDebug(graphics);
    }

    /**
     * Render an icon.
     *
     * @param icon     The icon instance to render.
     * @param graphics A {@link GuiGraphics} instance.
     */
    private void renderIcon(TextureIcon icon, GuiGraphics graphics)
    {
        float scale = this.getTextureScale(icon);
        int x = this.x;
        int y = this.y;

        if (icon.getTextureLocation().isPresent())
        {
            graphics.pose().pushPose();
            graphics.pose().translate(0.0D, 0.0D, this.zOffset);
            RenderUtil.blitTexture(icon.getTextureLocation().get(), graphics, scale, x, y);
            graphics.pose().popPose();

            this.renderDebug(graphics);

            return;
        }

        if (icon.getSpriteLocation().isPresent())
        {
            int width = icon.getWidth();
            int height = icon.getHeight();

            graphics.pose().pushPose();
            graphics.pose().translate(0.0D, 0.0D, this.zOffset);
            RenderUtil.blitSprite(icon.getSpriteLocation().get(), graphics, scale, x, y, width, height);
            graphics.pose().popPose();

            this.renderDebug(graphics);

            return;
        }

        float[] color = RenderSystem.getShaderColor();
        float brightness = (color[0] + color[1] + color[2]) / 3.0F;
        Item item = icon.getItem().orElse(icon.getBlock().orElse(Blocks.AIR).asItem());

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, this.zOffset);
        graphics.pose().scale(scale, scale, scale);
        RenderUtil.renderItem(graphics, item.getDefaultInstance(), 0, 0, brightness);
        graphics.pose().popPose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void renderDebug(GuiGraphics graphics)
    {
        if (this.isNotDebugging() || this.isInvisible())
            return;

        final Matrix4f position = new Matrix4f(graphics.pose().last().pose());

        RenderUtil.deferredRenderer(() -> {
            int startX = this.x;
            int startY = this.y;
            int endX = this.getEndX();
            int endY = this.getEndY();

            graphics.pose().pushPose();
            graphics.pose().setIdentity();
            graphics.pose().mulPose(position);
            graphics.pose().translate(0.0D, 0.0D, 1.0D);

            RenderUtil.beginBatching();
            RenderUtil.fill(graphics, startX, startY, startX + 1, startY + 1, 0xFFFF0000);
            RenderUtil.fill(graphics, endX - 1, startY, endX, startY + 1, 0xFF00FF00);
            RenderUtil.fill(graphics, startX, endY - 1, startX + 1, endY, 0xFF00FFFF);
            RenderUtil.fill(graphics, endX - 1, endY - 1, endX, endY, 0xFFFF00FF);
            RenderUtil.endBatching();

            graphics.pose().popPose();
        });
    }
}
