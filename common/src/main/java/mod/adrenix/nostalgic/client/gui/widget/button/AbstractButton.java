package mod.adrenix.nostalgic.client.gui.widget.button;

import mod.adrenix.nostalgic.client.gui.widget.WidgetBackground;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.IconManager;
import mod.adrenix.nostalgic.client.gui.widget.icon.IconWidget;
import mod.adrenix.nostalgic.tweak.config.CandyTweak;
import mod.adrenix.nostalgic.util.client.KeyboardUtil;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.client.renderer.RenderUtil;
import mod.adrenix.nostalgic.util.client.timer.ClientTimer;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.data.NullableHolder;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
import mod.adrenix.nostalgic.util.common.timer.TickTimer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class AbstractButton<Builder extends AbstractButtonMaker<Builder, Button>, Button extends AbstractButton<Builder, Button>>
    extends DynamicWidget<Builder, Button>
{
    /* Fields */

    protected final SimpleTimer scrollTimer;
    protected final Animation scrollAnimator;
    protected final CacheValue<Component> cacheTitle;
    protected final IconManager<Button> iconManager;
    protected final Consumer<Button> onPress;
    protected boolean scrollingText;
    protected boolean shrunk;
    protected boolean holding;
    protected int textX = 0;
    protected int textY = 0;
    protected int iconX = 0;
    protected int iconY = 0;

    /* Constructor */

    /**
     * Create a new button widget instance.
     *
     * @param builder A {@link Builder} instance.
     * @param onPress A {@link Consumer} that accepts this {@link Button} instance.
     */
    protected AbstractButton(Builder builder, Consumer<Button> onPress)
    {
        super(builder);

        this.scrollTimer = SimpleTimer.create(1500L, TimeUnit.MILLISECONDS).waitFirst().build();
        this.scrollAnimator = Animate.linear();
        this.cacheTitle = CacheValue.create(this::getTitle);
        this.iconManager = new IconManager<>(this.self());
        this.shrunk = builder.shrunk;
        this.onPress = onPress;
    }

    /* Methods */

    /**
     * @return The {@link IconManager} for this button instance.
     */
    @PublicAPI
    public IconManager<Button> getIconManager()
    {
        return this.iconManager;
    }

    /**
     * @return The {@link Component} for this button instance.
     */
    @PublicAPI
    public Component getTitle()
    {
        if (this.shrunk)
            return Component.empty();

        if (this.builder.titleSupplier != null)
            return this.builder.titleSupplier.get();

        return this.builder.title;
    }

    /**
     * @return The text color that should be used for this button.
     */
    protected int getTextColor()
    {
        boolean isOldHover = CandyTweak.OLD_BUTTON_TEXT_COLOR.get();
        int color = this.active ? isOldHover ? 0xE0E0E0 : 0xFFFFFF : 0xA0A0A0;

        if (this.isHoveredOrFocused() && this.active && isOldHover)
            color = 0xFFFFA0;

        if (this.builder.useTextColors && this.active)
            color = this.isHoveredOrFocused() ? this.builder.hoverColor.get() : this.builder.textColor.get();

        return color;
    }

    /**
     * @return Where this button's text is rendering on the game window's x-axis.
     */
    @PublicAPI
    public int getTextX()
    {
        return this.textX;
    }

    /**
     * @return Where this button's text is rendering on the game window's y-axis.
     */
    @PublicAPI
    public int getTextY()
    {
        return this.textY;
    }

    /**
     * If the icon for this button is {@code EMPTY}, then the position will be where the button renders on the x-axis
     * plus an offset of four.
     *
     * @return Where this button's icon is rendering on the game window's x-axis.
     */
    @PublicAPI
    public int getIconX()
    {
        return this.iconX;
    }

    /**
     * If the icon for this button is {@code EMPTY}, then the position will be where the button renders on the y-axis
     * plus an offset of four.
     *
     * @return Where this button's icon is rendering on the game window's y-axis.
     */
    @PublicAPI
    public int getIconY()
    {
        return this.iconY;
    }

    /**
     * Sets text and icon x/y positions. This update is performed during the alignment update that runs at the start of
     * each render cycle.
     */
    protected void setTextAndIconPos()
    {
        if (this.iconManager.isEmpty())
        {
            this.textX = this.x + this.width / 2;
            this.textY = this.y + (this.height - 8) / 2;

            this.iconX = this.x + 4;
            this.iconY = this.y + 4;
        }
        else
        {
            IconWidget icon = this.iconManager.get();
            int iconWidth = icon.getWidth();
            int iconHeight = icon.getHeight();

            if (this.builder.padding < 0 && GuiUtil.font().width(this.getTitle()) > 0)
                this.builder.padding = MathUtil.isOdd(iconWidth) ? 5 : 6;

            if (this.builder.iconTextPadding < 0 && GuiUtil.font().width(this.getTitle()) > 0)
                this.builder.iconTextPadding = this.builder.padding;

            int padding = this.builder.padding;
            int textOffset = this.builder.useTextWidth ? -1 : 0;
            int iconTextPadding = this.builder.iconTextPadding;

            if (this.shrunk)
            {
                padding = -1;
                textOffset = 0;
                iconTextPadding = -1;
            }

            int width = iconWidth + GuiUtil.font().width(this.getTitle()) + padding;
            int centerX = this.x + (this.builder.alignLeft ? this.builder.alignLeftOffset : (int) MathUtil.center(width, this.width));

            this.textX = centerX + iconWidth + iconTextPadding + textOffset;
            this.textY = this.y + (this.height - 8) / 2;

            this.iconX = centerX + this.builder.offsetIcon;
            this.iconY = (int) MathUtil.center(iconHeight, this.height) + this.y - this.builder.iconCenterOffset;
        }
    }

    /**
     * Updates the builder with new width data based on the current builder title.
     */
    protected void setWidthFromText()
    {
        if (this.shrunk)
        {
            this.setWidth(20);
            return;
        }

        if (this.builder.useTextWidth)
        {
            IconWidget icon = this.iconManager.get();

            if (this.builder.padding < 0)
                this.builder.padding = MathUtil.isOdd(icon.getWidth()) ? 5 : 6;

            if (this.builder.iconTextPadding < 0)
                this.builder.iconTextPadding = this.builder.padding;

            int padding = this.builder.iconTextPadding + (this.builder.padding * 2);

            this.setWidth(padding + icon.getWidth() + GuiUtil.font().width(this.getTitle()));
        }
    }

    /**
     * Shrink the button so that only it's icon is displayed.
     */
    @PublicAPI
    public void shrink()
    {
        this.shrunk = true;

        this.setWidthFromText();
        this.getBuilder().sync();
    }

    /**
     * Grow the button so that button's text is displayed again after being shrunk.
     */
    @PublicAPI
    public void grow()
    {
        this.shrunk = false;

        this.setWidthFromText();
        this.getBuilder().sync();
    }

    /**
     * @return Whether the button text is currently being scrolled due to width overflow.
     */
    @PublicAPI
    public boolean isScrollingText()
    {
        return this.scrollingText;
    }

    /**
     * Manually perform an on-press action.
     */
    @PublicAPI
    public void onPress()
    {
        this.onPress.accept(this.self());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.isInactive() || this.isInvisible())
            return false;

        if (this.isValidClick(mouseX, mouseY, button))
        {
            if (this.getBuilder().holdTimer != null)
            {
                ClientTimer.getInstance().run(this.getBuilder().holdTimer, () -> {
                    if (this.getBuilder().useClickSound)
                        GuiUtil.playClick();

                    this.onPress.accept(this.self());
                });

                return true;
            }

            if (this.getBuilder().useClickSound)
                GuiUtil.playClick();

            this.onPress.accept(this.self());

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
        if (this.isInactive() || this.isInvisible())
        {
            if (this.getBuilder().holdTimer != null)
                ClientTimer.getInstance().cancel(this.getBuilder().holdTimer);

            return false;
        }

        if (this.isValidClick(mouseX, mouseY, button) && this.getBuilder().holdTimer != null)
        {
            ClientTimer.getInstance().cancel(this.getBuilder().holdTimer);
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (this.isInactive() || this.isInvisible() || this.isUnfocused())
            return false;

        if (this.holding)
            return true;

        if (KeyboardUtil.isEnterLike(keyCode))
        {
            if (this.getBuilder().holdTimer != null)
            {
                this.holding = true;

                ClientTimer.getInstance().run(this.getBuilder().holdTimer, () -> {
                    if (this.getBuilder().useClickSound)
                        GuiUtil.playClick();

                    this.onPress.accept(this.self());
                });

                return true;
            }

            if (this.getBuilder().useClickSound)
                GuiUtil.playClick();

            this.onPress.accept(this.self());

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers)
    {
        if (this.isInactive() || this.isInvisible() || this.isUnfocused())
        {
            this.holding = false;

            if (this.getBuilder().holdTimer != null)
                ClientTimer.getInstance().cancel(this.getBuilder().holdTimer);

            return false;
        }

        if (KeyboardUtil.isEnterLike(keyCode) && this.getBuilder().holdTimer != null)
        {
            ClientTimer.getInstance().cancel(this.getBuilder().holdTimer);

            this.holding = false;
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * Renders the button's text and icon. This method is equipped with scissoring logic so that the text always stays
     * within the button.
     *
     * @param graphics    A {@link GuiGraphics} instance.
     * @param mouseX      The x-position of the mouse.
     * @param mouseY      The y-position of the mouse.
     * @param partialTick The normalized progress made between two ticks [0.0F, 1.0F].
     */
    protected void renderTextAndIcon(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.iconManager.pushCache();

        boolean isDefaultBackground = this.builder.backgroundRenderer == null;

        int margin = isDefaultBackground ? 3 : 0;
        int endX = this.getEndX() - margin;
        int startX = this.iconManager.isEmpty() ? this.getX() + margin : this.textX;
        int textWidth = GuiUtil.font().width(this.getTitle()) + margin;

        this.scrollingText = startX + GuiUtil.font().width(this.getTitle()) > endX;

        if (this.builder.renderer != null)
            this.scrollingText = false;

        if (this.scrollingText)
        {
            startX = isDefaultBackground ? this.x + 2 + margin : this.iconX;
            endX -= isDefaultBackground ? 3 : 0;

            if (this.iconManager.isPresent())
            {
                this.iconManager.get().pos(startX, this.iconY);
                startX += this.iconManager.get().getWidth() + this.builder.iconTextPadding - 1;
            }
        }

        if (this.iconManager.isPresent())
        {
            if (!this.scrollingText)
                this.iconManager.get().pos(this.iconX, this.iconY);

            this.iconManager.get().render(graphics, mouseX, mouseY, partialTick);
        }

        int extraWidth = Math.abs(startX + textWidth - endX - margin);

        if (this.scrollAnimator.isMoving())
            this.scrollTimer.reset();

        if (this.scrollingText && this.scrollTimer.hasElapsed() && this.scrollAnimator.isFinished())
        {
            this.scrollAnimator.setDuration(40L * extraWidth, TimeUnit.MILLISECONDS);
            this.scrollAnimator.playOrRewind();
        }

        if (this.iconManager.isEmpty() && !this.scrollingText)
        {
            DrawText.begin(graphics, this.getTitle())
                .pos(this.textX, this.textY)
                .color(this.getTextColor())
                .center()
                .draw();
        }
        else
        {
            if (this.scrollingText)
            {
                final int scissorX = startX;
                final int scissorEndX = endX;
                final float scrollX = (float) Mth.lerp(this.scrollAnimator.getValue(), startX, startX - extraWidth);

                RenderUtil.deferredRenderer(() -> {
                    RenderUtil.pushScissor(scissorX, this.getY(), scissorEndX, this.getEndY());
                    RenderUtil.pauseBatching();

                    DrawText.begin(graphics, this.getTitle())
                        .pos(scrollX, this.textY)
                        .color(this.getTextColor())
                        .draw();

                    RenderUtil.popScissor();
                    RenderUtil.resumeBatching();
                });
            }
            else
                DrawText.begin(graphics, this.getTitle()).pos(startX, this.textY).color(this.getTextColor()).draw();
        }

        this.iconManager.render(graphics, mouseX, mouseY, partialTick);
        this.iconManager.popCache();
    }

    /**
     * Handler method for rendering the highlight effect for timers, like cooldown and hold.
     *
     * @param graphics A {@link GuiGraphics} instance.
     */
    protected void renderTimerHighlight(GuiGraphics graphics)
    {
        if (this.getBuilder().cooldownTimer == null && this.getBuilder().holdTimer == null)
            return;

        NullableHolder<TickTimer> timer = NullableHolder.empty();
        boolean isCooldownOn = this.getBuilder().cooldownTimer != null && this.getBuilder().cooldownTimer.isTicking();
        boolean isHoldingOn = this.getBuilder().holdTimer != null && this.getBuilder().holdTimer.isTicking();

        if (isHoldingOn)
            timer.set(this.getBuilder().holdTimer);

        if (isCooldownOn && this.isInactive())
            timer.set(this.getBuilder().cooldownTimer);

        if (timer.isEmpty())
            return;

        RenderUtil.deferredRenderer(() -> {
            float endX = (float) Mth.lerp(timer.getOrThrow().getProgress(), this.getX(), this.getEndX() - 1.0F);

            RenderUtil.fill(graphics, this.getX(), this.getY(), endX, this.getEndY(), Color.WHITE.fromAlpha(0.2F));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        if (this.builder.renderer != null)
        {
            this.builder.renderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);

            if (this.builder.postRenderer != null)
                this.builder.postRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);

            this.renderDebug(graphics);

            return;
        }

        if (this.builder.backgroundRenderer != null)
            this.builder.backgroundRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);
        else
            WidgetBackground.BUTTON.render(this, graphics);

        this.renderTimerHighlight(graphics);
        this.renderTextAndIcon(graphics, mouseX, mouseY, partialTick);

        if (this.builder.postRenderer != null)
            this.builder.postRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);

        this.renderDebug(graphics);
    }
}
