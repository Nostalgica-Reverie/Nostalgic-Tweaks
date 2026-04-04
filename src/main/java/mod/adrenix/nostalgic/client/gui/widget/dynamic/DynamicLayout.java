package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.data.CacheValue;
import mod.adrenix.nostalgic.util.common.function.ToFloatFunction;
import mod.adrenix.nostalgic.util.common.math.MathUtil;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.ToIntFunction;

interface DynamicLayout {
    /**
     * Layout functions for a widget's x-coordinate.
     */
    interface XPos {
        abstract class DynamicX<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                implements DynamicLayout, DynamicFunction<Builder, Widget> {
            @Override
            public List<DynamicField> getManaging(Builder builder) {
                return List.of(DynamicField.X);
            }
        }

        /**
         * Sets the widget's x-pos to the left of where the current screen ends on the x-axis.
         */
        class FromScreenEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicX<Builder, Widget> {
            private final CacheValue<Integer> margin;

            FromScreenEnd(Builder builder, ToIntFunction<Widget> margin) {
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setX(widget.getScreenWidth() - widget.getWidth() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, cache.screen.width, this.margin);
            }
        }

        /**
         * Sets the widget's x-pos to the left of where the given widget ends on the x-axis.
         */
        class FromWidgetEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicX<Builder, Widget> {
            private final DynamicWidget<?, ?> fromEnd;
            private final CacheValue<Integer> margin;

            FromWidgetEnd(Builder builder, DynamicWidget<?, ?> fromEnd, ToIntFunction<Widget> margin) {
                this.fromEnd = fromEnd;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setX(this.fromEnd.getEndX() - widget.getWidth() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, this.margin, this.fromEnd.cache.getX(), this.fromEnd.cache.getWidth());
            }
        }

        /**
         * Set the widget's x-pos so that it is flush with the align-to widget.
         */
        class AlignFlush<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicX<Builder, Widget> {
            private final DynamicWidget<?, ?> flushTo;
            private final CacheValue<Integer> offset;

            AlignFlush(Builder builder, DynamicWidget<?, ?> flushTo, ToIntFunction<Widget> offset) {
                this.flushTo = flushTo;
                this.offset = CacheValue.nullable(builder.widget, offset::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setX(this.flushTo.getX() + this.offset.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, this.offset, this.flushTo.cache.getX());
            }
        }

        /**
         * Sets the widget's x-position so that it is centered within the given widget.
         */
        class CenterInWidget<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicX<Builder, Widget> {
            private final DynamicWidget<?, ?> centerIn;
            private final CacheValue<Integer> extraWidth;

            CenterInWidget(Builder builder, DynamicWidget<?, ?> centerIn, ToIntFunction<Widget> extraWidth) {
                this.centerIn = centerIn;
                this.extraWidth = CacheValue.nullable(builder.widget, extraWidth::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                int size = widget.getWidth() + this.extraWidth.getAndUpdate();
                int maxSize = this.centerIn.getWidth();

                widget.setX(Math.round(MathUtil.center(this.centerIn.getX(), size, maxSize)));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, this.extraWidth, this.centerIn.cache.getX(), this.centerIn.cache.getWidth());
            }
        }

        /**
         * Sets the widget's x-pos so that it is centered within the current screen.
         */
        class CenterInScreen<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicX<Builder, Widget> {
            private final CacheValue<Integer> extraWidth;

            CenterInScreen(Builder builder, ToIntFunction<Widget> extraWidth) {
                this.extraWidth = CacheValue.nullable(builder.widget, extraWidth::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                int screenX = widget.getCache().screen.x.next();
                int maxSize = widget.getCache().screen.width.next() - screenX;
                int size = widget.getWidth() + this.extraWidth.getAndUpdate();

                widget.setX(screenX + Math.round(MathUtil.center(size, maxSize)));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, cache.screen.x, cache.screen.width, this.extraWidth);
            }
        }
    }

    /**
     * Layout functions for a widget's y-coordinate.
     */
    interface YPos {
        abstract class DynamicY<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                implements DynamicLayout, DynamicFunction<Builder, Widget> {
            @Override
            public List<DynamicField> getManaging(Builder builder) {
                return List.of(DynamicField.Y);
            }
        }

        /**
         * Sets the widget's y-pos above the assigned widget.
         */
        class Above<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final DynamicWidget<?, ?> above;
            private final CacheValue<Integer> margin;

            Above(Builder builder, DynamicWidget<?, ?> above, ToIntFunction<Widget> margin) {
                this.above = above;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setY(this.above.getY() - this.margin.getAndUpdate() - widget.getHeight());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, cache.height, this.margin, this.above.cache.getY());
            }
        }

        /**
         * Sets the widget's y-pos below the assigned widget.
         */
        class Below<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final DynamicWidget<?, ?> below;
            private final CacheValue<Integer> margin;

            Below(Builder builder, DynamicWidget<?, ?> below, ToIntFunction<Widget> margin) {
                this.below = below;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setY(this.below.getEndY() + this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, this.margin, this.below.cache.getY(), this.below.cache.getHeight());
            }
        }

        /**
         * Sets the widget's y-pos to the lowest widget.
         */
        class BelowAll<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final LinkedHashSet<DynamicWidget<?, ?>> all;
            private final CacheValue<Integer> margin;

            BelowAll(Builder builder, LinkedHashSet<DynamicWidget<?, ?>> all, ToIntFunction<Widget> margin) {
                this.all = all;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                if (this.all.isEmpty())
                    return;

                DynamicWidget.syncWithoutCache(this.all);

                int maxEndY = this.all.stream()
                        .filter(DynamicWidget::isVisible)
                        .mapToInt(DynamicWidget::getEndY)
                        .max()
                        .orElse(0);

                widget.setY(this.margin.getAndUpdate() + maxEndY);
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                if (this.all.isEmpty())
                    return false;

                return CacheValue.isAnyExpired(cache.y, this.margin) || this.all.stream()
                        .anyMatch(dynamic -> CacheValue.isAnyExpired(dynamic.cache.y, dynamic.cache.height, dynamic.cache.visible));
            }
        }

        /**
         * Sets the widget's y-pos from the bottom of where the current screen ends on the y-axis.
         */
        class FromScreenEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final CacheValue<Integer> margin;

            FromScreenEnd(Builder builder, ToIntFunction<Widget> margin) {
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setY(widget.getCache().screen.height.next() - widget.getHeight() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.screen.height, cache.y, cache.height, this.margin);
            }
        }

        /**
         * Sets the widget's y-pos above of where the given widget ends on the y-axis.
         */
        class FromWidgetEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final DynamicWidget<?, ?> fromEnd;
            private final CacheValue<Integer> margin;

            FromWidgetEnd(Builder builder, DynamicWidget<?, ?> fromEnd, ToIntFunction<Widget> margin) {
                this.fromEnd = fromEnd;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setY(this.fromEnd.getEndY() - widget.getHeight() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, cache.height, this.margin, this.fromEnd.cache.getY(), this.fromEnd.cache.getHeight());
            }
        }

        /**
         * Sets the widget's y-pos so that it matches the widget that is being vertically aligned with.
         */
        class AlignVertical<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final DynamicWidget<?, ?> verticalTo;
            private final CacheValue<Integer> offset;

            AlignVertical(Builder builder, DynamicWidget<?, ?> verticalTo, ToIntFunction<Widget> offset) {
                this.verticalTo = verticalTo;
                this.offset = CacheValue.nullable(builder.widget, offset::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setY(this.verticalTo.getY() + this.offset.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, this.offset, this.verticalTo.cache.getY());
            }
        }

        /**
         * Sets the widget's y-pos so that it is centered within the assigned widget.
         */
        class CenterInWidget<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final DynamicWidget<?, ?> centerIn;
            private final CacheValue<Integer> extraHeight;

            CenterInWidget(Builder builder, DynamicWidget<?, ?> centerIn, ToIntFunction<Widget> extraHeight) {
                this.centerIn = centerIn;
                this.extraHeight = CacheValue.nullable(builder.widget, extraHeight::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                int size = widget.getHeight() + this.extraHeight.getAndUpdate();
                int maxSize = this.centerIn.getHeight();

                widget.setY(Math.round(MathUtil.center(this.centerIn.getY(), size, maxSize)));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, cache.height, this.extraHeight, this.centerIn.cache.getHeight(), this.centerIn.cache.getY());
            }
        }

        /**
         * Sets the widget's y-pos so that it is centered within the current screen.
         */
        class CenterInScreen<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicY<Builder, Widget> {
            private final CacheValue<Integer> extraHeight;

            CenterInScreen(Builder builder, ToIntFunction<Widget> extraHeight) {
                this.extraHeight = CacheValue.nullable(builder.widget, extraHeight::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                int screenY = widget.getCache().screen.y.next();
                int maxSize = widget.getCache().screen.height.next() - screenY;
                int size = widget.getHeight() + this.extraHeight.getAndUpdate();

                widget.setY(screenY + Math.round(MathUtil.center(size, maxSize)));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, cache.height, cache.screen.height, cache.screen.y, this.extraHeight);
            }
        }
    }

    /**
     * Layout functions for a widget's x-coordinate and y-coordinate.
     */
    interface XYPos {
        abstract class DynamicXY<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                implements DynamicLayout, DynamicFunction<Builder, Widget> {
            @Override
            public List<DynamicField> getManaging(Builder builder) {
                return List.of(DynamicField.X, DynamicField.Y);
            }
        }

        /**
         * Sets the widget's x/y position to the left of the assigned widget.
         */
        class LeftOf<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicXY<Builder, Widget> {
            private final DynamicWidget<?, ?> leftOf;
            private final CacheValue<Integer> margin;

            LeftOf(Builder builder, DynamicWidget<?, ?> leftOf, ToIntFunction<Widget> margin) {
                this.leftOf = leftOf;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setX(this.leftOf.getX() - this.margin.getAndUpdate() - widget.getWidth());
                widget.setY(this.leftOf.getY());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.y, cache.width, this.margin, this.leftOf.cache.getX(), this.leftOf.cache.getY());
            }
        }

        /**
         * Sets the widget's x/y position to the right of the assigned widget.
         */
        class RightOf<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicXY<Builder, Widget> {
            private final DynamicWidget<?, ?> rightOf;
            private final CacheValue<Integer> margin;

            RightOf(Builder builder, DynamicWidget<?, ?> rightOf, ToIntFunction<Widget> margin) {
                this.rightOf = rightOf;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setX(this.rightOf.getEndX() + this.margin.getAndUpdate());
                widget.setY(this.rightOf.getY());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.y, this.margin, this.rightOf.cache.getX(), this.rightOf.cache.getY(), this.rightOf.cache.getWidth());
            }
        }
    }

    /**
     * Layout functions for a widget's width.
     */
    interface Width {
        abstract class DynamicWidth<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                implements DynamicLayout, DynamicFunction<Builder, Widget> {
            @Override
            public List<DynamicField> getManaging(Builder builder) {
                return List.of(DynamicField.WIDTH);
            }
        }

        /**
         * Sets the widget's width using the given percentage of the current screen.
         */
        class OfScreen<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicWidth<Builder, Widget> {
            private final CacheValue<Float> ofScreen;

            OfScreen(Builder builder, ToFloatFunction<Widget> ofScreen) {
                this.ofScreen = CacheValue.nullable(builder.widget, ofScreen::applyAsFloat, 1.0F);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                int screenX = widget.getCache().screen.x.next();
                int width = widget.getCache().screen.width.next() - screenX;

                widget.setWidth(Math.round(this.ofScreen.getAndUpdate() * width));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.width, cache.screen.width, this.ofScreen);
            }
        }

        /**
         * Sets the widget's width using the given percentage of the given widget.
         */
        class OfWidget<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicWidth<Builder, Widget> {
            private final DynamicWidget<?, ?> ofWidget;
            private final CacheValue<Float> ofAmount;

            OfWidget(Builder builder, DynamicWidget<?, ?> ofWidget, ToFloatFunction<Widget> ofAmount) {
                this.ofWidget = ofWidget;
                this.ofAmount = CacheValue.nullable(builder.widget, ofAmount::applyAsFloat, 1.0F);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setWidth(Math.round(this.ofWidget.getWidth() * this.ofAmount.getAndUpdate()));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.width, this.ofAmount, this.ofWidget.cache.getWidth());
            }
        }

        /**
         * Sets the widget's width so that it extends out towards the screen's end x-position with the given margin.
         */
        class ExtendToScreen<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicWidth<Builder, Widget> {
            private final CacheValue<Integer> margin;

            ExtendToScreen(Builder builder, ToIntFunction<Widget> margin) {
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setWidth(widget.getCache().screen.width.next() - widget.getX() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, cache.screen.x, cache.screen.width, this.margin);
            }
        }

        /**
         * Sets the widget's width so that it extends out towards the assigned widget with the given margin.
         */
        class ExtendToWidgetStart<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicWidth<Builder, Widget> {
            private final DynamicWidget<?, ?> extendTo;
            private final CacheValue<Integer> margin;

            ExtendToWidgetStart(Builder builder, DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
                this.extendTo = extendTo;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setWidth(this.extendTo.getX() - widget.getX() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, this.margin, this.extendTo.cache.getX());
            }
        }

        /**
         * Sets the widget's width so that it extends out towards the end of the assigned widget with the given margin.
         */
        class ExtendToWidgetEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicWidth<Builder, Widget> {
            private final DynamicWidget<?, ?> extendTo;
            private final CacheValue<Integer> margin;

            ExtendToWidgetEnd(Builder builder, DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
                this.extendTo = extendTo;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setWidth(this.extendTo.getEndX() - widget.getX() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.x, cache.width, this.margin, this.extendTo.cache.getX(), this.extendTo.cache.getWidth());
            }
        }

        /**
         * Sets the widget's width so that it matches the largest widget width in the builder's largest width
         * collection.
         */
        class ExtendToLargestEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicWidth<Builder, Widget> {
            private final Collection<DynamicWidget<?, ?>> all;

            ExtendToLargestEnd(Collection<DynamicWidget<?, ?>> all) {
                this.all = all;
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setWidth(this.all.stream().mapToInt(DynamicWidget::getWidth).max().orElse(widget.getWidth()));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return cache.width.isExpired() || this.all.stream()
                        .map(DynamicWidget::getCache)
                        .map(WidgetCache::getWidth)
                        .anyMatch(CacheValue::isExpired);
            }
        }
    }

    /**
     * Layout functions for a widget's height.
     */
    interface Height {
        abstract class DynamicHeight<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                implements DynamicLayout, DynamicFunction<Builder, Widget> {
            @Override
            public List<DynamicField> getManaging(Builder builder) {
                return List.of(DynamicField.HEIGHT);
            }
        }

        /**
         * Sets the widget's height using the given percentage of the current screen.
         */
        class OfScreen<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicHeight<Builder, Widget> {
            private final CacheValue<Float> ofScreen;

            OfScreen(Builder builder, ToFloatFunction<Widget> ofScreen) {
                this.ofScreen = CacheValue.nullable(builder.widget, ofScreen::applyAsFloat, 1.0F);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                int screenY = widget.getCache().screen.y.next();
                int height = widget.getCache().screen.height.next() - screenY;

                widget.setHeight(Math.round(this.ofScreen.getAndUpdate() * height));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.height, cache.screen.height, this.ofScreen);
            }
        }

        /**
         * Sets the widget's height using the given percentage of the given widget.
         */
        class OfWidget<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicHeight<Builder, Widget> {
            private final DynamicWidget<?, ?> ofWidget;
            private final CacheValue<Float> ofAmount;

            OfWidget(Builder builder, DynamicWidget<?, ?> ofWidget, ToFloatFunction<Widget> ofAmount) {
                this.ofWidget = ofWidget;
                this.ofAmount = CacheValue.nullable(builder.widget, ofAmount::applyAsFloat, 1.0F);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setHeight(Math.round(this.ofWidget.getHeight() * this.ofAmount.getAndUpdate()));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.height, this.ofAmount, this.ofWidget.cache.getHeight());
            }
        }

        /**
         * Sets the widget's height so that it extends out towards the screen's end y-position with the given margin.
         */
        class ExtendToScreen<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicHeight<Builder, Widget> {
            private final CacheValue<Integer> margin;

            ExtendToScreen(Builder builder, ToIntFunction<Widget> margin) {
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setHeight(widget.getCache().screen.height.next() - widget.getY() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, cache.height, cache.screen.y, cache.screen.height, this.margin);
            }
        }

        /**
         * Sets the widget's height so that it extends out towards the assigned widget with the given margin.
         */
        class ExtendToWidgetStart<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicHeight<Builder, Widget> {
            private final DynamicWidget<?, ?> extendTo;
            private final CacheValue<Integer> margin;

            ExtendToWidgetStart(Builder builder, DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
                this.extendTo = extendTo;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setHeight(this.extendTo.getY() - widget.getY() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.height, cache.y, this.margin, this.extendTo.cache.getY());
            }
        }

        /**
         * Sets the widget's height so that it extends out towards the end of the assigned widget with the given
         * margin.
         */
        class ExtendToWidgetEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicHeight<Builder, Widget> {
            private final DynamicWidget<?, ?> extendTo;
            private final CacheValue<Integer> margin;

            ExtendToWidgetEnd(Builder builder, DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
                this.extendTo = extendTo;
                this.margin = CacheValue.nullable(builder.widget, margin::applyAsInt, 0);
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setHeight(this.extendTo.getEndY() - widget.getY() - this.margin.getAndUpdate());
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return CacheValue.isAnyExpired(cache.y, cache.height, this.margin, this.extendTo.cache.getY(), this.extendTo.cache.getHeight());
            }
        }

        /**
         * Sets the widget's height so that it matches the largest widget height in the builder's largest height
         * collection.
         */
        class ExtendToLargestEnd<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
                extends DynamicHeight<Builder, Widget> {
            private final Collection<DynamicWidget<?, ?>> all;

            ExtendToLargestEnd(Collection<DynamicWidget<?, ?>> all) {
                this.all = all;
            }

            @Override
            public void apply(Widget widget, Builder builder) {
                widget.setHeight(this.all.stream().mapToInt(DynamicWidget::getHeight).max().orElse(widget.getHeight()));
            }

            @Override
            public boolean isReapplyNeeded(Widget widget, Builder builder, WidgetCache cache) {
                return cache.height.isExpired() || this.all.stream()
                        .map(DynamicWidget::getCache)
                        .map(WidgetCache::getHeight)
                        .anyMatch(CacheValue::isExpired);
            }
        }
    }
}
