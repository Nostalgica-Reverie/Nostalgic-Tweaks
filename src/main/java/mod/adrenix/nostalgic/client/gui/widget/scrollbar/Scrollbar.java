package mod.adrenix.nostalgic.client.gui.widget.scrollbar;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicWidget;
import mod.adrenix.nostalgic.util.client.timer.PartialTick;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.math.MathUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.Mth;

import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

//import mod.adrenix.nostalgic.tweak.config.ModTweak;

public class Scrollbar extends DynamicWidget<ScrollbarBuilder, Scrollbar> {
    /* Builders */

    private final ScrollbarContent content;
    private double scrollAmount = 0.0D;

    /* Fields */
    private double scrollTo = 0.0D;
    private double lastScrollTo = 0.0D;
    private boolean dragging = false;
    private boolean smoothScroll = false;

    protected Scrollbar(ScrollbarBuilder builder) {
        super(builder);

        this.content = new ScrollbarContent(CacheValue.create(builder.contentSize::getAsInt));

        this.getBuilder().addFunction(this.content);
    }

    /**
     * Start the process of creating a new vertical scrollbar widget. The default visibility condition for scrollbars is
     * determined by whether the {@link Scrollbar#getMaxScrollAmount()} method is greater than {@code zero}.
     *
     * @param contentSize         An {@link IntSupplier} that provides the height amount covered by this scrollbar.
     * @param averageScrollAmount A {@link DoubleSupplier} that provides the amount to scroll by when the mouse scroll
     *                            wheel is used.
     * @return A {@link ScrollbarBuilder} instance.
     */
    public static ScrollbarBuilder vertical(IntSupplier contentSize, DoubleSupplier averageScrollAmount) {
        return new ScrollbarBuilder(ScrollbarType.VERTICAL, contentSize, averageScrollAmount);
    }

    /* Constructor */

    /**
     * Start the process of creating a new horizontal scrollbar widget. The default visibility condition for scrollbars
     * is determined by whether the {@link Scrollbar#getMaxScrollAmount()} method is greater than {@code zero}.
     *
     * @param contentSize         An {@link IntSupplier} that provides the width amount covered by this scrollbar.
     * @param averageScrollAmount A {@link DoubleSupplier} that provides the amount to scroll by when the mouse scroll
     *                            wheel is used.
     * @return A {@link ScrollbarBuilder} instance.
     */
    public static ScrollbarBuilder horizontal(IntSupplier contentSize, DoubleSupplier averageScrollAmount) {
        return new ScrollbarBuilder(ScrollbarType.HORIZONTAL, contentSize, averageScrollAmount);
    }

    /* Methods */

    /**
     * @return The {@link ScrollbarType} enumeration assigned to this scrollbar.
     */
    @PublicAPI
    public ScrollbarType getScrollbarType() {
        return this.getBuilder().scrollbarType;
    }

    /**
     * @return Whether this scrollbar is vertical.
     * @see ScrollbarType
     * @see #isHorizontal()
     */
    @PublicAPI
    public boolean isVertical() {
        return this.getBuilder().scrollbarType == ScrollbarType.VERTICAL;
    }

    /**
     * @return Whether this scrollbar is horizontal.
     * @see ScrollbarType
     * @see #isVertical()
     */
    @PublicAPI
    public boolean isHorizontal() {
        return this.getBuilder().scrollbarType == ScrollbarType.HORIZONTAL;
    }

    /**
     * @return Whether the mouse is currently holding this scrollbar.
     */
    @PublicAPI
    public boolean isDragging() {
        return this.dragging;
    }

    /**
     * @return Whether the scrollbar should perform a smooth animation while scrolling.
     */
    @PublicAPI
    public boolean isSmoothScrolling() {
        //TODO
        return false;//ModTweak.SMOOTH_SCROLL.fromCache() && !Screen.hasControlDown();
    }

    /**
     * Set the animation scroll amount. This amount will be clamped.
     *
     * @param amount A new scroll amount.
     */
    private void setAnimationScrollAmount(double amount) {
        this.scrollAmount = Mth.clamp(amount, 0.0D, this.getMaxScrollAmount());

        if (MathUtil.tolerance(this.scrollAmount, this.scrollTo, 0.01D))
            this.scrollAmount = this.scrollTo;

        this.getBuilder().onScroll.accept(this);
    }

    /**
     * Manually clamp the scroll amount. This is useful if the content height changes and the scrollbar needs clamped
     * before the next render cycle.
     */
    @PublicAPI
    public void clamp() {
        this.scrollAmount = Mth.clamp(this.scrollAmount, 0.0D, this.getMaxScrollAmount());
    }

    /**
     * Manually update the content size if it has changed prematurely.
     */
    @PublicAPI
    public void updateContentSize() {
        this.content.update();
    }

    /**
     * Manually set the amount this scrollbar has been moved. The amount will be clamped, so the caller does not have to
     * implement bound checks. If an immediate jump is desired, then use {@link #setScrollAmount(double)}.
     *
     * @param amount A new scroll amount.
     */
    @PublicAPI
    public void setSmoothScrollAmount(double amount) {
        this.lastScrollTo = this.getScrollAmount();
        this.scrollTo = Mth.clamp(amount, 0.0D, this.getMaxScrollAmount());

        this.getBuilder().animation.reset();
        this.getBuilder().animation.play();
        this.getBuilder().onScroll.accept(this);
    }

    /**
     * @return Get the amount this scrollbar has been moved.
     */
    @PublicAPI
    public double getScrollAmount() {
        return this.scrollAmount;
    }

    /**
     * Manually set the amount this scrollbar has been moved. The amount will be clamped, so the caller does not have to
     * implement bound checks. If a smooth animation is desired, then use {@link #setSmoothScrollAmount(double)}.
     *
     * @param amount A new scroll amount.
     */
    @PublicAPI
    public void setScrollAmount(double amount) {
        this.scrollAmount = Mth.clamp(amount, 0.0D, this.getMaxScrollAmount());
        this.scrollTo = this.scrollAmount;

        this.getBuilder().onScroll.accept(this);
    }

    /**
     * @return Get the maximum amount this scrollbar can be moved.
     */
    @PublicAPI
    public int getMaxScrollAmount() {
        return Math.max(0, this.content.getSize() - (this.isVertical() ? this.height : this.width));
    }

    /**
     * @return Get the scroll button length for this scrollbar.
     */
    @PublicAPI
    public int getScrollLength() {
        int base = this.isVertical() ? this.height : this.width;
        int value = (int) Math.round(Math.pow(base, 2) / this.content.getSize());

        return Mth.clamp(value, this.getBuilder().minSize, this.isVertical() ? this.height : this.width);
    }

    /**
     * @return Get where the scroll button starts relative to the scrollbar's axis.
     */
    @PublicAPI
    public int getScrollStart() {
        int barSize = this.isVertical() ? this.height : this.width;

        return (int) Math.max(0.0D, this.scrollAmount * (barSize - this.getScrollLength()) / this.getMaxScrollAmount());
    }

    /**
     * @return Get where the scroll button ends relative to the scrollbar's axis.
     */
    @PublicAPI
    public int getScrollEnd() {
        return this.getScrollStart() + this.getScrollLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.isInvalidClick(event))
            return false;

        switch (this.getBuilder().scrollbarType) {
            case VERTICAL -> {
                int startY = this.y + Math.round(this.getScrollStart());
                int width = this.getBuilder().size;
                int height = this.getScrollLength();

                this.dragging = MathUtil.isWithinBox(event.x(), event.y(), this.x, startY, width, height);
            }
            case HORIZONTAL -> {
                int startX = this.x + Math.round(this.getScrollStart());
                int width = this.getScrollLength();
                int height = this.getBuilder().size;

                this.dragging = MathUtil.isWithinBox(event.x(), event.y(), startX, this.y, width, height);
            }
        }

        if (!this.dragging && MathUtil.isWithinBox(event.x(), event.y(), this.x, this.y, this.width, this.height)) {
            double clickAmount = this.getMaxScrollAmount() * switch (this.getBuilder().scrollbarType) {
                case VERTICAL -> 1.0D - ((this.getEndY() - event.y()) / (double) this.height);
                case HORIZONTAL -> 1.0D - ((this.getEndX() - event.x()) / (double) this.width);
            };

            if (this.isSmoothScrolling())
                this.setSmoothScrollAmount(clickAmount);
            else
                this.setScrollAmount(clickAmount);

            this.dragging = true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.dragging = false;

        return super.mouseReleased(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.dragging) {
            this.onDrag(event, dragX, dragY);

            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        if (this.isInactive() || this.isInvisible())
            return;

        if (this.getBuilder().animation.isNotFinished())
            this.getBuilder().animation.stop();

        double drag = this.isVertical() ? dragY : dragX;
        double base = this.isVertical() ? this.height : this.width;

        double max = Math.max(1.0D, this.getMaxScrollAmount());
        double delta = Math.max(1.0D, max / (base - this.getScrollLength()));

        this.setScrollAmount(this.getScrollAmount() + drag * delta);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.dragging)
            return false;
        else if (this.isInactive() || this.isInvisible())
            return false;
        else {
            this.isVertical();
            if (false/*Screen.hasShiftDown()*/) //TODO
                return false;
            else if (this.isHorizontal() && true/*!Screen.hasShiftDown()*/) //TODO
                return false;
        }

        double averageScrollAmount = Mth.clamp(this.getBuilder().averageScrollAmount.getAsDouble(), 6.8D, 12.0D);

        if (this.isSmoothScrolling()) {
            this.scrollTo = Mth.clamp(this.scrollTo - deltaY * averageScrollAmount, 0.0D, this.getMaxScrollAmount());
            this.smoothScroll = true;

            if (this.getBuilder().animation.isNotFinished())
                this.getBuilder().animation.stop();
        } else {
            this.setScrollAmount(this.scrollAmount - deltaY * averageScrollAmount);
            this.scrollTo = this.scrollAmount;
        }

        return true;
    }

    /**
     * Move the animation if applicable.
     */
    private void animate(float partialTick) {
        if (this.dragging)
            return;

        if (this.getBuilder().animation.isNotFinished())
            this.setAnimationScrollAmount(Mth.lerp(this.getBuilder().animation.getValue(), this.lastScrollTo, this.scrollTo));

        if (this.getBuilder().animation.isFinished()) {
            this.getBuilder().animation.reset();
            this.setAnimationScrollAmount(Mth.lerp(partialTick, this.scrollAmount, this.scrollTo));
        }

        boolean isMaxReached = this.getScrollAmount() >= this.getMaxScrollAmount() && this.scrollTo >= this.getMaxScrollAmount();

        if (this.getBuilder().animation.isNotFinished() && (isMaxReached || this.isInvisible())) {
            if (isMaxReached)
                this.setScrollAmount(this.getMaxScrollAmount());

            this.getBuilder().animation.stop();
        }

        if (this.smoothScroll) {
            this.setAnimationScrollAmount(Mth.lerp(partialTick, this.scrollAmount, this.scrollTo));

            if (this.scrollAmount == this.scrollTo)
                this.smoothScroll = false;
        }

        if (partialTick >= 1.0F)
            this.setScrollAmount(this.scrollTo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        partialTick = PartialTick.realtime();

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        if (this.isInvisible())
            return;

        this.clamp();
        this.animate(partialTick);

        graphics.pose().pushMatrix();
        graphics.pose().translate(this.x, this.y);

        graphics.fill(0, 0, this.width, this.height, this.getBuilder().backgroundColor.get());

        int scrollStart = this.getScrollStart();
        int scrollEnd = this.getScrollEnd();

        switch (this.getBuilder().scrollbarType) {
            case VERTICAL -> {
                graphics.fill(0, scrollStart, this.width, scrollEnd, this.getBuilder().scrollColor.get());
                graphics.fill(0, scrollStart, this.width - 1, scrollEnd - 1, this.getBuilder().borderColor.get());
            }

            case HORIZONTAL -> {
                graphics.fill(scrollStart, 0, scrollEnd, this.height, this.getBuilder().scrollColor.get());
                graphics.fill(scrollStart, 0, scrollEnd - 1, this.height - 1, this.getBuilder().borderColor.get());
            }
        }

        graphics.pose().popMatrix();

        this.content.update();
    }
}
