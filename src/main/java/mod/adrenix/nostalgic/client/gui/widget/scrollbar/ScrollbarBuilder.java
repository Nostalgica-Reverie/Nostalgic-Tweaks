package mod.adrenix.nostalgic.client.gui.widget.scrollbar;

import mod.adrenix.nostalgic.client.gui.widget.dynamic.DynamicBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.LayoutBuilder;
import mod.adrenix.nostalgic.client.gui.widget.dynamic.VisibleBuilder;
import mod.adrenix.nostalgic.util.client.animate.Animate;
import mod.adrenix.nostalgic.util.client.animate.Animation;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.color.Color;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class ScrollbarBuilder extends DynamicBuilder<ScrollbarBuilder, Scrollbar>
        implements LayoutBuilder<ScrollbarBuilder, Scrollbar>, VisibleBuilder<ScrollbarBuilder, Scrollbar> {
    /* Fields */

    final ScrollbarType scrollbarType;
    final IntSupplier contentSize;
    final DoubleSupplier averageScrollAmount;

    int size = 6;
    int minSize = 16;

    Animation animation = Animate.linear(1L, TimeUnit.SECONDS);
    Color scrollColor = new Color(85, 85, 85);
    Color borderColor = new Color(192, 192, 192);
    Color backgroundColor = new Color(49, 49, 49);
    Consumer<Scrollbar> onScroll = (scrollbar) -> {
    };

    /* Constructor */

    protected ScrollbarBuilder(ScrollbarType scrollbarType, IntSupplier contentSize, DoubleSupplier averageScrollAmount) {
        this.scrollbarType = scrollbarType;
        this.contentSize = contentSize;
        this.averageScrollAmount = averageScrollAmount;
        this.canFocus = BooleanSupplier.NEVER;
    }

    /* Methods */

    @Override
    public ScrollbarBuilder self() {
        return this;
    }

    /**
     * Change the size of this scrollbar.
     *
     * @param size A size to apply to either the width or height depending on the scrollbar type.
     */
    @PublicAPI
    public ScrollbarBuilder size(int size) {
        this.size = size;

        return this;
    }

    /**
     * Change only the scrollbar button color.
     *
     * @param color The color of the scrollbar button.
     * @see #borderColor(Color)
     * @see #backgroundColor(Color)
     */
    @PublicAPI
    public ScrollbarBuilder scrollColor(Color color) {
        this.scrollColor = color;

        return this;
    }

    /**
     * Change the scrollbar button color and border color.
     *
     * @param color The border color of the scrollbar button.
     * @see #scrollColor(Color)
     * @see #backgroundColor(Color)
     */
    @PublicAPI
    public ScrollbarBuilder borderColor(Color color) {
        this.borderColor = color;

        return this;
    }

    /**
     * Change various color aspects of this scrollbar widget.
     *
     * @param color The background color of the entire scrollbar widget.
     * @see #scrollColor(Color)
     * @see #borderColor(Color)
     */
    @PublicAPI
    public ScrollbarBuilder backgroundColor(Color color) {
        this.backgroundColor = color;

        return this;
    }

    /**
     * Restrict the minimum size allowed for the scrollbar button.
     *
     * @param minSize A minimum size.
     */
    @PublicAPI
    public ScrollbarBuilder minimum(int minSize) {
        this.minSize = minSize;

        return this;
    }

    /**
     * Define an {@link Animation} to be used by this scrollbar when {@link Scrollbar#setSmoothScrollAmount(double)} is
     * invoked. The default animation is linear movement for 1 second. This will not impact smooth scrolling while using
     * the scroll wheel.
     *
     * @param animator An {@link Animation} instance.
     */
    @PublicAPI
    public ScrollbarBuilder animation(Animation animator) {
        this.animation = animator;

        return this;
    }

    /**
     * Define instructions to perform when the scrollbar changes scroll amount.
     *
     * @param consumer A {@link Consumer} function that accepts the {@link Scrollbar} instance that moved.
     */
    @PublicAPI
    public ScrollbarBuilder onScroll(Consumer<Scrollbar> consumer) {
        this.onScroll = consumer;

        return this;
    }

    /**
     * Define instructions to perform when the scrollbar changes scroll amount.
     *
     * @param runnable A {@link Runnable} function.
     */
    @PublicAPI
    public ScrollbarBuilder onScroll(Runnable runnable) {
        this.onScroll = (scrollbar) -> runnable.run();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Scrollbar construct() {
        switch (this.scrollbarType) {
            case VERTICAL -> this.defaultWidth = this.size;
            case HORIZONTAL -> this.defaultHeight = this.size;
        }

        if (this.visible == null)
            this.visible = (scrollbar) -> scrollbar.getMaxScrollAmount() > 0;

        return new Scrollbar(this);
    }
}
