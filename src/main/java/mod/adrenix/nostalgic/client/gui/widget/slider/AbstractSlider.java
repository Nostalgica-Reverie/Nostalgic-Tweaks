package mod.adrenix.nostalgic.client.gui.widget.slider;

import com.mojang.blaze3d.platform.InputConstants;
import mod.adrenix.nostalgic.client.gui.widget.WidgetBackground;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.client.gui.DrawText;
import mod.adrenix.nostalgic.util.client.gui.GuiUtil;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.asset.GameSprite;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.timer.SimpleTimer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

//import mod.adrenix.nostalgic.tweak.config.CandyTweak;

public abstract class AbstractSlider<Builder extends AbstractSliderMaker<Builder, Slider>, Slider extends AbstractSlider<Builder, Slider>>
        extends DynamicWidget<Builder, Slider> {
    /* Fields */

    protected final SimpleTimer scrollTimer;
    protected final Animation scrollAnimator;
    protected double value;
    protected Component title;
    protected boolean dragging;

    /* Constructor */

    protected AbstractSlider(Builder builder) {
        super(builder);

        this.scrollTimer = SimpleTimer.create(1500L, TimeUnit.MILLISECONDS).waitFirst().build();
        this.scrollAnimator = Animate.linear();

        this.builder.addFunction(new ValueSync<>(this.self()));
        this.applyTitle();
    }

    /* Methods */

    /**
     * This is the value from the builder's value supplier. This is not the value used internally by the slider. To
     * retrieve that value, use {@link #getNormalizedValue()}.
     *
     * @return The current value from the value supplier parsed as a double.
     */
    @PublicAPI
    public double getValue() {
        return this.builder.valueSupplier.get().doubleValue();
    }

    /**
     * Change the current value for this slider. If the given value is out of the bounds set by the builder's min and
     * max ranges, then it will be clamped. The given must not be normalized.
     *
     * @param value The new value.
     */
    @PublicAPI
    public void setValue(double value) {
        this.setNormalizedValue((value - this.getMin()) / (this.getMax() - this.getMin()));
    }

    /**
     * @return The minimum value from the builder's min-supplier as a double.
     */
    @PublicAPI
    public double getMin() {
        return this.builder.minValue.doubleValue();
    }

    /**
     * @return The maximum value from the builder's max-supplier as a double.
     */
    @PublicAPI
    public double getMax() {
        return this.builder.maxValue.doubleValue();
    }

    /**
     * @return Whether the slider is currently being dragged.
     */
    @PublicAPI
    public boolean isDragging() {
        return this.dragging;
    }

    /**
     * Set the slider's internal value using the given x-mouse coordinate.
     *
     * @param mouseX The x-coordinate of the mouse.
     */
    protected void setFromMouse(double mouseX) {
        this.setNormalizedValue((mouseX - (this.getX() + this.builder.handleWidth / 2.0D)) / (double) (this.width - this.builder.handleWidth));
    }

    /**
     * The slider value is a normalized value [0.0-1.0] that represents how much the handle has moved within the slider
     * widget's border. This value is useful in situations such as custom rendering. If a value from the builder's
     * supplier is needed then use {@link #getValue()}.
     *
     * @return The internal slider value.
     */
    @PublicAPI
    public double getNormalizedValue() {
        return this.value;
    }

    /**
     * Set the value of the slider. The given value will be clamped if it is out-of-bounds.
     *
     * @param value The new normalized value [0.0-1.0] of the slider.
     */
    protected void setNormalizedValue(double value) {
        double last = this.value;
        this.value = Mth.clamp(value, 0.0D, 1.0D);

        if (this.value != last) {
            this.applyValue();

            if (this.builder.onValueChange != null)
                this.builder.onValueChange.accept(this.self());
        }

        this.applyTitle();
    }

    /**
     * Applies a parsed slider value to the builder's value consumer.
     */
    protected void applyValue() {
        Number numberValue = this.builder.maxValue;
        double sliderValue = this.getMin() + Math.abs(this.getMax() - this.getMin()) * this.value;

        if (this.builder.useRounding) {
            sliderValue = BigDecimal.valueOf(this.getMin() + Math.abs(this.getMax() - this.getMin()) * this.value)
                    .setScale(this.builder.roundTo, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        switch (numberValue) {
            case Byte ignored -> this.builder.valueConsumer.accept((byte) Math.round(sliderValue));
            case Short ignored -> this.builder.valueConsumer.accept((short) Math.round(sliderValue));
            case Integer ignored -> this.builder.valueConsumer.accept((int) Math.round(sliderValue));
            case Long ignored -> this.builder.valueConsumer.accept(Math.round(sliderValue));
            case Float ignored -> this.builder.valueConsumer.accept((float) sliderValue);
            case null, default -> this.builder.valueConsumer.accept(sliderValue);
        }
    }

    /**
     * Applies a parsed slider title using the builder's title properties.
     */
    protected void applyTitle() {
        String title = this.builder.title.get().getString();
        String separator = this.builder.separator.get().getString();
        String value = this.builder.formatter.apply(this.builder.valueSupplier.get());
        String suffix = this.builder.suffix.get().getString();

        if (title.isEmpty())
            this.title = Component.empty();
        else
            this.title = Component.literal(String.format("%s%s%s%s", title, separator, value, suffix));

        if (this.isInactive())
            this.title = this.title.copy().withStyle(ChatFormatting.GRAY);
    }

    /**
     * @return Get the starting x-position of the slider's handle.
     */
    @PublicAPI
    public int getHandleX() {
        return this.x + (int) (this.value * (double) (this.width - this.builder.handleWidth));
    }

    /**
     * @return Get the slider's handle width.
     */
    @PublicAPI
    public int getHandleWidth() {
        return this.builder.handleWidth;
    }

    /**
     * Gets the proper handle sprite for this slider based on the widget's current context.
     *
     * @return A {@link Identifier} instance.
     */
    @PublicAPI
    public Identifier getHandleSprite() {
        if (this.isHoveredOrFocused() && this.isActive())
            return GameSprite.SLIDER_HANDLE_HIGHLIGHTED;

        return GameSprite.SLIDER_HANDLE;
    }

    /**
     * @return A shader color value to use for the current shader's RGB color components.
     */
    @PublicAPI
    public float getHandleShaderColor() {
        return this.isActive() ? 1.0F : 0.6F;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean isValidButton = event.button() == 0 || event.button() == 2;

        if (this.isValidPoint(event.x(), event.y()) && isValidButton) {
            this.setFocused();

            if (event.button() == 2)
                return true;
        }

        if (this.isInvalidClick(event))
            return false;
        else {
            this.dragging = true;
            this.setFromMouse(event.x());
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.dragging) {
            this.dragging = false;

            if (this.builder.clickSoundOnRelease)
                GuiUtil.playClick();

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.dragging)
            return false;

        this.setFromMouse(event.x());

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.isInvalidPoint(mouseX, mouseY))
            return false;

        if (this.builder.interval != null && this.isActive()) {
            if (this.builder.scrollRequiresFocus && this.isUnfocused())
                return false;
            else if (!this.isHoveredOrFocused())
                return false;

            double delta = 0.0D;

            if (deltaX != 0.0D)
                delta = deltaX;

            if (deltaY != 0.0D)
                delta = deltaY;

            this.setValue(this.getValue() + (delta * this.builder.interval.get().doubleValue()));

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.isUnfocused() || this.isInactive())
            return false;

        return switch (event.key()) {
            case InputConstants.KEY_D -> this.incrementIfPossible();
            case InputConstants.KEY_A -> this.decrementIfPossible();
            default -> false;
        };
    }

    /**
     * Increment the slider up by one interval if an interval is defined. This does <b color=red>not</b> check if the
     * slider is invisible or inactive, {@link #incrementIfPossible()}.
     */
    @PublicAPI
    public void increment() {
        if (this.builder.interval != null)
            this.setValue(this.getValue() + (this.builder.interval.get().doubleValue()));
    }

    /**
     * Decrement the slider down by one interval if an interval is defined. This does <b color=red>not</b> check if the
     * slider is invisible or inactive, {@link #decrementIfPossible()}.
     */
    @PublicAPI
    public void decrement() {
        if (this.builder.interval != null)
            this.setValue(this.getValue() + (-1.0D * this.builder.interval.get().doubleValue()));
    }

    /**
     * Increment the slider up by one interval if an interval is defined, the widget is visible, and the widget is
     * active. This does not check if the widget is focused. This must be checked beforehand if this behavior is
     * desired.
     *
     * @return Whether the slider was incremented by one interval.
     * @see #increment()
     */
    @PublicAPI
    public boolean incrementIfPossible() {
        if (this.isInvisible() || this.isInactive())
            return false;

        this.increment();

        return true;
    }

    /**
     * Decrement the slider down by one interval if an interval is defined, the widget is visible, and the widget is
     * active. This does not check if the widget is focused. This must be checked beforehand if this behavior is
     * desired.
     *
     * @return Whether the slider was decremented by one interval.
     * @see #decrement()
     */
    @PublicAPI
    public boolean decrementIfPossible() {
        if (this.isInvisible() || this.isInactive())
            return false;

        this.decrement();

        return true;
    }

    /**
     * Renders the slider's text.
     *
     * @param graphics The {@link GuiGraphicsExtractor} instance.
     */
    private void renderText(GuiGraphicsExtractor graphics) {
        int margin = 3;
        int startX = this.getX() + margin;
        int endX = this.getEndX() - margin;
        int textX = this.x + this.width / 2;
        int textY = this.y + (this.height - 8) / 2;
        int textWidth = GuiUtil.font().width(this.title);
        int extraWidth = Math.abs(startX + textWidth - endX);
        boolean isScrolling = startX + GuiUtil.font().width(this.title) + margin > endX;
        Color color = this.active ? Color.WHITE : Color.QUICK_SILVER;

        if (false/*CandyTweak.OLD_BUTTON_TEXT_COLOR.get()*/) { //TODO
            if (this.isInactive())
                color = Color.QUICK_SILVER;
            else if (this.isHoveredOrFocused())
                color = Color.LEMON_YELLOW;
            else
                color = Color.NOSTALGIC_GRAY;
        }

        if (this.scrollAnimator.isMoving())
            this.scrollTimer.reset();

        if (isScrolling && this.scrollTimer.hasElapsed() && this.scrollAnimator.isFinished()) {
            this.scrollAnimator.setDuration(40L * extraWidth, TimeUnit.MILLISECONDS);
            this.scrollAnimator.playOrRewind();
        }

        if (isScrolling) {
            final int scrollX = (int) Mth.lerp(this.scrollAnimator.getValue(), startX, startX - extraWidth);
            final int scrollColor = color.get();

            graphics.enableScissor(startX, this.getY(), endX, this.getEndY());
            DrawText.begin(graphics, this.title).pos(scrollX, textY).color(scrollColor).draw();
            graphics.disableScissor();
        } else
            DrawText.begin(graphics, this.title).pos(textX, textY).color(color).center().draw();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        if (this.builder.backgroundRenderer != null)
            this.builder.backgroundRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);
        else
            WidgetBackground.SLIDER.render(this, graphics);

        if (this.builder.handleRenderer != null)
            this.builder.handleRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);
        else {
            float colorBase = this.getHandleShaderColor();
            int color = ARGB.colorFromFloat(1.0F, colorBase, colorBase, colorBase);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.getHandleSprite(), this.getHandleX(), this.y, 8, 20, color);
        }

        if (this.builder.effectsRenderer != null)
            this.builder.effectsRenderer.accept(this.self(), graphics, mouseX, mouseY, partialTick);

        this.renderText(graphics);
    }
}
