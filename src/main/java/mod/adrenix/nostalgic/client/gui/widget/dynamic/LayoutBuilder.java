package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.FloatSupplier;
import mod.adrenix.nostalgic.util.common.function.ToFloatFunction;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

/**
 * This builder will update layout fields in {@link DynamicBuilder} that will be reflected onto the built
 * {@link Widget}.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that is implementing {@link DynamicWidget}.
 */
public interface LayoutBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
        extends SelfBuilder<Builder, Widget> {
    /**
     * Change where this widget is placed on the screen.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    @PublicAPI
    default Builder pos(int x, int y) {
        this.self().defaultX = x;
        this.self().defaultY = y;

        return this.self();
    }

    /**
     * Set the x/y coordinate for this widget based on what the given x/y suppliers yield when alignment occurs.
     *
     * @param posX An {@link IntSupplier} that provides an x-coordinate.
     * @param posY An {@link IntSupplier} that provides a y-coordinate.
     */
    @PublicAPI
    default Builder pos(IntSupplier posX, IntSupplier posY) {
        return this.pos(widget -> posX.getAsInt(), widget -> posY.getAsInt());
    }

    /**
     * Set the x/y coordinate for this widget based on what the given x/y functions yield when alignment occurs.
     *
     * @param posX A {@link ToIntFunction} that accepts the built {@link Widget} and returns an x-coordinate.
     * @param posY A {@link ToIntFunction} that accepts the built {@link Widget} and returns a y-coordinate.
     */
    @PublicAPI
    default Builder pos(ToIntFunction<Widget> posX, ToIntFunction<Widget> posY) {
        this.self().x = posX;
        this.self().y = posY;

        return this.self();
    }

    /**
     * Set the x-coordinate for this widget.
     *
     * @param x The widget's x-coordinate.
     */
    @PublicAPI
    default Builder posX(int x) {
        this.self().defaultX = x;

        return this.self();
    }

    /**
     * Set the x-coordinate for this widget based on what the given supplier yields when alignment occurs.
     *
     * @param supplier An {@link IntSupplier} that provides an integer.
     */
    @PublicAPI
    default Builder posX(IntSupplier supplier) {
        return this.posX(widget -> supplier.getAsInt());
    }

    /**
     * Set the x-coordinate for this widget based on what the given function yields when alignment occurs.
     *
     * @param function A {@link ToIntFunction} that accepts the built {@link Widget}.
     */
    @PublicAPI
    default Builder posX(ToIntFunction<Widget> function) {
        this.self().x = function;

        return this.self();
    }

    /**
     * Set the y-coordinate for this widget.
     *
     * @param y The button's y-coordinate.
     */
    @PublicAPI
    default Builder posY(int y) {
        this.self().defaultY = y;

        return this.self();
    }

    /**
     * Set the y-coordinate for this widget based on what the given supplier yields when alignment occurs.
     *
     * @param supplier An {@link IntSupplier} that provides an integer.
     */
    @PublicAPI
    default Builder posY(IntSupplier supplier) {
        return this.posY(widget -> supplier.getAsInt());
    }

    /**
     * Set the y-coordinate for this widget based on what the given function yields when alignment occurs.
     *
     * @param function A {@link ToIntFunction} that accepts the built {@link Widget}.
     */
    @PublicAPI
    default Builder posY(ToIntFunction<Widget> function) {
        this.self().y = function;

        return this.self();
    }

    /**
     * Change the default widget width.
     *
     * @param width An integer representing the widget's width.
     */
    @PublicAPI
    default Builder width(int width) {
        this.self().defaultWidth = width;

        return this.self();
    }

    /**
     * Set the width for this widget based on what the given supplier yields when alignment occurs. The default padding
     * is {@code zero}.
     *
     * @param width An {@link IntSupplier} that provides a width amount.
     */
    @PublicAPI
    default Builder width(IntSupplier width) {
        return this.width(widget -> width.getAsInt());
    }

    /**
     * Set the width for this widget based on what the given function yields when alignment occurs.
     *
     * @param width A {@link ToIntFunction} that accepts {@link Widget} the built widget and returns a width amount.
     */
    @PublicAPI
    default Builder width(ToIntFunction<Widget> width) {
        this.self().width = width;

        return this.self();
    }

    /**
     * Change the default widget height.
     *
     * @param height An integer representing the widget's height.
     */
    @PublicAPI
    default Builder height(int height) {
        this.self().defaultHeight = height;

        return this.self();
    }

    /**
     * Set the height for this widget based on what the given supplier yields when alignment occurs. The default padding
     * is {@code zero}.
     *
     * @param height An {@link IntSupplier} that provides a height amount.
     */
    @PublicAPI
    default Builder height(IntSupplier height) {
        return this.height(widget -> height.getAsInt());
    }

    /**
     * Set the height for this widget based on what the given function yields when alignment occurs.
     *
     * @param height A {@link ToIntFunction} that accepts the built {@link Widget} and returns a height amount.
     */
    @PublicAPI
    default Builder height(ToIntFunction<Widget> height) {
        this.self().height = height;

        return this.self();
    }

    /**
     * Change both the widget's width and height.
     *
     * @param width  An integer representing the widget's width.
     * @param height An integer representing the widget's height.
     */
    @PublicAPI
    default Builder size(int width, int height) {
        this.self().defaultWidth = width;
        this.self().defaultHeight = height;

        return this.self();
    }

    /**
     * Set both the widget's width and height suppliers.
     *
     * @param width  An {@link IntSupplier} that provides the widget's width.
     * @param height An {@link IntSupplier} that provides the widget's height.
     */
    @PublicAPI
    default Builder size(IntSupplier width, IntSupplier height) {
        return this.size(widget -> width.getAsInt(), widget -> height.getAsInt());
    }

    /**
     * Set both the widget's width and height functions.
     *
     * @param width  A {@link ToIntFunction} that accepts the built {@link Widget} and returns the widget's width.
     * @param height A {@link ToIntFunction} that accepts the built {@link Widget} and returns the widget's height.
     */
    @PublicAPI
    default Builder size(ToIntFunction<Widget> width, ToIntFunction<Widget> height) {
        this.self().width = width;
        this.self().height = height;

        return this.self();
    }

    /**
     * Change both the widget's width and height.
     *
     * @param size A size to apply to both the widget's width and height.
     */
    @PublicAPI
    default Builder size(int size) {
        this.self().defaultWidth = size;
        this.self().defaultHeight = size;

        return this.self();
    }

    /**
     * Change both the widget's width and height.
     *
     * @param size An integer supplier that provides the widget's width and height.
     */
    @PublicAPI
    default Builder size(IntSupplier size) {
        return this.size(widget -> size.getAsInt());
    }

    /**
     * Change both the widget's width and height.
     *
     * @param size A {@link ToIntFunction} that accepts the built {@link Widget} and returns the widget's width and
     *             height.
     */
    @PublicAPI
    default Builder size(ToIntFunction<Widget> size) {
        this.self().width = size;
        this.self().height = size;

        return this.self();
    }

    /**
     * Set the width for this widget based on the given percentage of the current screen.
     *
     * @param ofAmount A {@link ToFloatFunction} that returns a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder widthOfScreen(ToFloatFunction<Widget> ofAmount) {
        ToFloatFunction<Widget> clamp = widget -> Mth.clamp(ofAmount.applyAsFloat(widget), 0.0F, 1.0F);

        this.self().addFunction(new DynamicLayout.Width.OfScreen<>(this.self(), clamp));

        return this.self();
    }

    /**
     * Set the width for this widget based on the given percentage of the current screen.
     *
     * @param ofAmount A {@link FloatSupplier} that returns a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder widthOfScreen(FloatSupplier ofAmount) {
        return this.widthOfScreen(widget -> ofAmount.getAsFloat());
    }

    /**
     * Set the width for this widget based on the given percentage of the current screen.
     *
     * @param ofAmount A normalized percentage value [0.0F-1.0F], this value will be clamped.
     */
    @PublicAPI
    default Builder widthOfScreen(float ofAmount) {
        return this.widthOfScreen(() -> ofAmount);
    }

    /**
     * Set the width for this widget based on the given percentage of the given widget.
     *
     * @param ofWidget A {@link DynamicWidget} to take a percentage of.
     * @param ofAmount A {@link ToFloatFunction} that returns a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder widthOfWidget(@Nullable DynamicWidget<?, ?> ofWidget, ToFloatFunction<Widget> ofAmount) {
        if (ofWidget == null)
            return this.self();

        ToFloatFunction<Widget> clamp = widget -> Mth.clamp(ofAmount.applyAsFloat(widget), 0.0F, 1.0F);

        this.self().addFunction(new DynamicLayout.Width.OfWidget<>(this.self(), ofWidget, clamp));

        return this.self();
    }

    /**
     * Set the width for this widget based on the given percentage of the given widget.
     *
     * @param ofWidget A {@link DynamicWidget} to take a percentage of.
     * @param ofAmount A {@link FloatSupplier} that provides a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder widthOfWidget(@Nullable DynamicWidget<?, ?> ofWidget, FloatSupplier ofAmount) {
        return this.widthOfWidget(ofWidget, widget -> ofAmount.getAsFloat());
    }

    /**
     * Set the width for this widget based on the given percentage of the given widget.
     *
     * @param ofWidget A {@link DynamicWidget} to take a percentage of.
     * @param ofAmount A normalized percentage value [0.0F-1.0F], this value will be clamped.
     */
    @PublicAPI
    default Builder widthOfWidget(@Nullable DynamicWidget<?, ?> ofWidget, float ofAmount) {
        return this.widthOfWidget(ofWidget, () -> ofAmount);
    }

    /**
     * Extend this widget's width to the provided widget with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} width to.
     * @param margin   A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the
     *                 given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthTo(@Nullable DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
        if (extendTo == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.Width.ExtendToWidgetStart<>(this.self(), extendTo, margin));

        return this.self();
    }

    /**
     * Extend this widget's width to the provided widget with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} width to.
     * @param margin   A {@link IntSupplier} that provides a margin between the given widget and the built
     *                 {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthTo(@Nullable DynamicWidget<?, ?> extendTo, IntSupplier margin) {
        return this.extendWidthTo(extendTo, widget -> margin.getAsInt());
    }

    /**
     * Extend this widget's width to the provided widget with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} width to.
     * @param margin   A margin between the given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthTo(@Nullable DynamicWidget<?, ?> extendTo, int margin) {
        return this.extendWidthTo(extendTo, () -> margin);
    }

    /**
     * Extend this widget's width to the provided widget ending position with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} width to.
     * @param margin   A {@link IntSupplier} that provides a margin between the given widget and the built
     *                 {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthToEnd(@Nullable DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
        if (extendTo == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.Width.ExtendToWidgetEnd<>(this.self(), extendTo, margin));

        return this.self();
    }

    /**
     * Extend this widget's width to the provided widget ending position with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} width to.
     * @param margin   A {@link IntSupplier} that provides a margin between the given widget and the built
     *                 {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthToEnd(@Nullable DynamicWidget<?, ?> extendTo, IntSupplier margin) {
        return this.extendWidthToEnd(extendTo, widget -> margin.getAsInt());
    }

    /**
     * Extend this widget's width to the provided widget ending position with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} width to.
     * @param margin   A margin between the given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthToEnd(@Nullable DynamicWidget<?, ?> extendTo, int margin) {
        return this.extendWidthToEnd(extendTo, () -> margin);
    }

    /**
     * Extend this widget's width to the largest width found within the given collection of widgets.
     *
     * @param all A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    default Builder extendWidthToLargest(Collection<DynamicWidget<?, ?>> all) {
        this.self().addFunction(new DynamicLayout.Width.ExtendToLargestEnd<>(all));

        return this.self();
    }

    /**
     * Extend this widget's width to the largest widget found within the given varargs of widgets.
     *
     * @param all A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    default Builder extendWidthToLargest(DynamicWidget<?, ?>... all) {
        return this.extendWidthToLargest(List.of(all));
    }

    /**
     * Extend this widget's width to the end of the current screen.
     *
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the end
     *               of the screen and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthToScreenEnd(ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.Width.ExtendToScreen<>(this.self(), margin));

        return this.self();
    }

    /**
     * Extend this widget's width to the end of the current screen.
     *
     * @param margin A {@link IntSupplier} that provides the margin between the end of the screen and the built
     *               {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthToScreenEnd(IntSupplier margin) {
        return this.extendWidthToScreenEnd(widget -> margin.getAsInt());
    }

    /**
     * Extend this widget's width to the end of the current screen.
     *
     * @param margin A margin between the end of the screen and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendWidthToScreenEnd(int margin) {
        return this.extendWidthToScreenEnd(() -> margin);
    }

    /**
     * Set the height for this widget based on the given percentage of the current screen.
     *
     * @param ofAmount A {@link ToFloatFunction} that returns a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder heightOfScreen(ToFloatFunction<Widget> ofAmount) {
        ToFloatFunction<Widget> clamp = widget -> Mth.clamp(ofAmount.applyAsFloat(widget), 0.0F, 1.0F);

        this.self().addFunction(new DynamicLayout.Height.OfScreen<>(this.self(), clamp));

        return this.self();
    }

    /**
     * Set the height for this widget based on the given percentage of the current screen.
     *
     * @param ofAmount A {@link ToFloatFunction} that provides a normalized percentage value [0.0F-1.0F], this value
     *                 will be clamped.
     */
    @PublicAPI
    default Builder heightOfScreen(FloatSupplier ofAmount) {
        return this.heightOfScreen(widget -> ofAmount.getAsFloat());
    }

    /**
     * Set the height for this widget based on the given percentage of the current screen.
     *
     * @param ofAmount A normalized percentage value [0.0F-1.0F], this value will be clamped.
     */
    @PublicAPI
    default Builder heightOfScreen(float ofAmount) {
        return this.heightOfScreen(() -> ofAmount);
    }

    /**
     * Set the height for this widget based on the given percentage of the given widget.
     *
     * @param ofWidget A {@link DynamicWidget} to take a percentage of.
     * @param ofAmount A {@link ToFloatFunction} that returns a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder heightOfWidget(@Nullable DynamicWidget<?, ?> ofWidget, ToFloatFunction<Widget> ofAmount) {
        if (ofWidget == null)
            return this.self();

        ToFloatFunction<Widget> clamp = widget -> Mth.clamp(ofAmount.applyAsFloat(widget), 0.0F, 1.0F);

        this.self().addFunction(new DynamicLayout.Height.OfWidget<>(this.self(), ofWidget, clamp));

        return this.self();
    }

    /**
     * Set the height for this widget based on the given percentage of the given widget.
     *
     * @param ofWidget A {@link DynamicWidget} to take a percentage of.
     * @param ofAmount A {@link FloatSupplier} that provides a normalized percentage value [0.0F-1.0F], this value will
     *                 be clamped.
     */
    @PublicAPI
    default Builder heightOfWidget(@Nullable DynamicWidget<?, ?> ofWidget, FloatSupplier ofAmount) {
        return this.heightOfWidget(ofWidget, widget -> ofAmount.getAsFloat());
    }

    /**
     * Set the height for this widget based on the given percentage of the given widget.
     *
     * @param ofWidget An abstract widget to take a percentage of.
     * @param ofAmount A normalized percentage value [0.0F-1.0F], this value will be clamped.
     */
    @PublicAPI
    default Builder heightOfWidget(@Nullable DynamicWidget<?, ?> ofWidget, float ofAmount) {
        return this.heightOfWidget(ofWidget, () -> ofAmount);
    }

    /**
     * Extend this widget's height to the provided widget with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} height to.
     * @param margin   A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the
     *                 given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightTo(@Nullable DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
        if (extendTo == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.Height.ExtendToWidgetStart<>(this.self(), extendTo, margin));

        return this.self();
    }

    /**
     * Extend this widget's height to the provided widget with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} height to.
     * @param margin   A {@link IntSupplier} that provides a margin between the given widget and the built
     *                 {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightTo(@Nullable DynamicWidget<?, ?> extendTo, IntSupplier margin) {
        return this.extendHeightTo(extendTo, widget -> margin.getAsInt());
    }

    /**
     * Extend this widget's height to the provided widget with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} height to.
     * @param margin   A margin between the given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightTo(@Nullable DynamicWidget<?, ?> extendTo, int margin) {
        return this.extendHeightTo(extendTo, () -> margin);
    }

    /**
     * Extend this widget's height to the provided widget ending position with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} height to.
     * @param margin   A {@link IntSupplier} that provides a margin between the given widget and the built
     *                 {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightToEnd(@Nullable DynamicWidget<?, ?> extendTo, ToIntFunction<Widget> margin) {
        if (extendTo == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.Height.ExtendToWidgetEnd<>(this.self(), extendTo, margin));

        return this.self();
    }

    /**
     * Extend this widget's height to the provided widget ending position with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} height to.
     * @param margin   A {@link IntSupplier} that provides a margin between the given widget and the built
     *                 {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightToEnd(@Nullable DynamicWidget<?, ?> extendTo, IntSupplier margin) {
        return this.extendHeightToEnd(extendTo, widget -> margin.getAsInt());
    }

    /**
     * Extend this widget's height to the provided widget ending position with the margin provided.
     *
     * @param extendTo A {@link DynamicWidget} to extend the built {@link Widget} height to.
     * @param margin   A margin between the given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightToEnd(@Nullable DynamicWidget<?, ?> extendTo, int margin) {
        return this.extendHeightToEnd(extendTo, () -> margin);
    }

    /**
     * Extend this widget's height to the largest height found within the given collection of widgets.
     *
     * @param all A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    default Builder extendHeightToLargest(Collection<DynamicWidget<?, ?>> all) {
        this.self().addFunction(new DynamicLayout.Height.ExtendToLargestEnd<>(all));

        return this.self();
    }

    /**
     * Extend this widget's height to the largest widget found within the given varargs of widgets.
     *
     * @param all A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    default Builder extendHeightToLargest(DynamicWidget<?, ?>... all) {
        return this.extendHeightToLargest(List.of(all));
    }

    /**
     * Extend this widget's height to the end of the current screen.
     *
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the end
     *               of the screen and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightToScreenEnd(ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.Height.ExtendToScreen<>(this.self(), margin));

        return this.self();
    }

    /**
     * Extend this widget's height to the end of the current screen.
     *
     * @param margin A {@link IntSupplier} that provides the margin between the end of the screen and the built
     *               {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightToScreenEnd(IntSupplier margin) {
        return this.extendHeightToScreenEnd(widget -> margin.getAsInt());
    }

    /**
     * Extend this widget's height to the end of the current screen.
     *
     * @param margin A margin between the end of the screen and the built {@link Widget}.
     */
    @PublicAPI
    default Builder extendHeightToScreenEnd(int margin) {
        return this.extendHeightToScreenEnd(() -> margin);
    }

    /**
     * Keep this widget to the left of the given widget. The position will update as needed. The y-coordinate of this
     * widget will be aligned to the provided widget.
     *
     * @param leftOf A {@link DynamicWidget} to stay left of and vertically aligned to.
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the two
     *               widgets.
     */
    @PublicAPI
    default Builder leftOf(@Nullable DynamicWidget<?, ?> leftOf, ToIntFunction<Widget> margin) {
        if (leftOf == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.XYPos.LeftOf<>(this.self(), leftOf, margin));

        return this.self();
    }

    /**
     * Keep this widget to the left of the given widget. The position will update as needed. The y-coordinate of this
     * widget will be aligned to the provided widget.
     *
     * @param leftOf A {@link DynamicWidget} to stay left of and vertically aligned to.
     * @param margin An {@link IntSupplier} that provides the margin between the two widgets.
     */
    @PublicAPI
    default Builder leftOf(@Nullable DynamicWidget<?, ?> leftOf, IntSupplier margin) {
        return this.leftOf(leftOf, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget to the left of the given widget. The position will update as needed. The y-coordinate of this
     * widget will be aligned to the provided widget.
     *
     * @param leftOf A {@link DynamicWidget} to stay left of and vertically aligned to.
     * @param margin A margin between the two widgets.
     */
    @PublicAPI
    default Builder leftOf(@Nullable DynamicWidget<?, ?> leftOf, int margin) {
        return this.leftOf(leftOf, () -> margin);
    }

    /**
     * Keep this widget to the right of the given widget. The position will update as needed. The y-coordinate of this
     * widget will be aligned to the provided widget.
     *
     * @param rightOf A {@link DynamicWidget} to stay right of and vertically aligned to.
     * @param margin  A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the two
     *                widgets.
     */
    @PublicAPI
    default Builder rightOf(@Nullable DynamicWidget<?, ?> rightOf, ToIntFunction<Widget> margin) {
        if (rightOf == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.XYPos.RightOf<>(this.self(), rightOf, margin));

        return this.self();
    }

    /**
     * Keep this widget to the right of the given widget. The position will update as needed. The y-coordinate of this
     * widget will be aligned to the provided widget.
     *
     * @param rightOf A {@link DynamicWidget} to stay right of and vertically aligned to.
     * @param margin  An {@link IntSupplier} that provides the margin between the two widgets.
     */
    @PublicAPI
    default Builder rightOf(@Nullable DynamicWidget<?, ?> rightOf, IntSupplier margin) {
        return this.rightOf(rightOf, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget to the right of the given widget. The position will update as needed. The y-coordinate of this
     * widget will be aligned to the provided widget.
     *
     * @param rightOf A {@link DynamicWidget} to stay right of and vertically aligned to.
     * @param margin  A margin between the two widgets.
     */
    @PublicAPI
    default Builder rightOf(@Nullable DynamicWidget<?, ?> rightOf, int margin) {
        return this.rightOf(rightOf, () -> margin);
    }

    /**
     * Keep this widget above the given widget. The y-coordinate will update as needed.
     *
     * @param above  A {@link DynamicWidget} to stay above.
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the two
     *               widgets.
     */
    @PublicAPI
    default Builder above(@Nullable DynamicWidget<?, ?> above, ToIntFunction<Widget> margin) {
        if (above == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.YPos.Above<>(this.self(), above, margin));

        return this.self();
    }

    /**
     * Keep this widget above the given widget. The y-coordinate will update as needed.
     *
     * @param above  A {@link DynamicWidget} to stay above.
     * @param margin A {@link IntSupplier} that provides a margin between the two widgets.
     */
    @PublicAPI
    default Builder above(@Nullable DynamicWidget<?, ?> above, IntSupplier margin) {
        return this.above(above, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget above the given widget. The y-coordinate will update as needed.
     *
     * @param above  A {@link DynamicWidget} to stay above.
     * @param margin A margin between the two widgets.
     */
    @PublicAPI
    default Builder above(@Nullable DynamicWidget<?, ?> above, int margin) {
        return this.above(above, () -> margin);
    }

    /**
     * Keep this widget below the given widget. The y-coordinate will update as needed.
     *
     * @param below  A {@link DynamicWidget} to stay below.
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the two
     *               widgets.
     */
    @PublicAPI
    default Builder below(@Nullable DynamicWidget<?, ?> below, ToIntFunction<Widget> margin) {
        if (below == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.YPos.Below<>(this.self(), below, margin));

        return this.self();
    }

    /**
     * Keep this widget below the given widget. The y-coordinate will update as needed.
     *
     * @param below  A {@link DynamicWidget} to stay below.
     * @param margin A {@link IntSupplier} that provides a margin between the two widgets.
     */
    @PublicAPI
    default Builder below(@Nullable DynamicWidget<?, ?> below, IntSupplier margin) {
        return this.below(below, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget below the given widget. The y-coordinate will update as needed.
     *
     * @param below  A {@link DynamicWidget} to stay below.
     * @param margin A margin between the two widgets.
     */
    @PublicAPI
    default Builder below(@Nullable DynamicWidget<?, ?> below, int margin) {
        return this.below(below, () -> margin);
    }

    /**
     * Keep this widget below all the given widgets. The y-coordinate will update as needed.
     *
     * @param all    A {@link Collection} of {@link DynamicWidget} to stay below.
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the two
     *               widgets.
     */
    @PublicAPI
    default Builder belowAll(Collection<? extends DynamicWidget<?, ?>> all, ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.YPos.BelowAll<>(this.self(), new LinkedHashSet<>(all), margin));

        return this.self();
    }

    /**
     * Keep this widget below all the given widgets. The y-coordinate will update as needed.
     *
     * @param all    A {@link Collection} of {@link DynamicWidget} to stay below.
     * @param margin A {@link IntSupplier} that provides a margin between the two widgets.
     */
    @PublicAPI
    default Builder belowAll(Collection<? extends DynamicWidget<?, ?>> all, IntSupplier margin) {
        return this.belowAll(all, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget below all the given widgets. The y-coordinate will update as needed.
     *
     * @param all    A {@link Collection} of {@link DynamicWidget} to stay below.
     * @param margin A margin between the two widgets.
     */
    @PublicAPI
    default Builder belowAll(Collection<? extends DynamicWidget<?, ?>> all, int margin) {
        return this.belowAll(all, () -> margin);
    }

    /**
     * Keep this widget below all the given widgets. The y-coordinate will update as needed.
     *
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the two
     *               widgets.
     * @param all    A varargs of {@link DynamicWidget} to stay below.
     */
    @PublicAPI
    default Builder belowAll(ToIntFunction<Widget> margin, DynamicWidget<?, ?>... all) {
        LinkedHashSet<DynamicWidget<?, ?>> widgets = new LinkedHashSet<>(List.of(all));

        this.self().addFunction(new DynamicLayout.YPos.BelowAll<>(this.self(), widgets, margin));

        return this.self();
    }

    /**
     * Keep this widget below all the given widgets. The y-coordinate will update as needed.
     *
     * @param margin A {@link IntSupplier} that provides a margin between the two widgets.
     * @param all    A varargs of {@link DynamicWidget} to stay below.
     */
    @PublicAPI
    default Builder belowAll(IntSupplier margin, DynamicWidget<?, ?>... all) {
        return this.belowAll(widget -> margin.getAsInt(), all);
    }

    /**
     * Keep this widget below all the given widgets. The y-coordinate will update as needed.
     *
     * @param margin A margin between the two widgets.
     * @param all    A varargs of {@link DynamicWidget} to stay below.
     */
    @PublicAPI
    default Builder belowAll(int margin, DynamicWidget<?, ?>... all) {
        return this.belowAll(() -> margin, all);
    }

    /**
     * Keep this widget aligned from the ending x-coordinate of the given widget using the given margin. The position
     * will update as needed.
     *
     * @param fromEnd A {@link DynamicWidget} to get ending position data from.
     * @param margin  A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the end
     *                of the given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder fromWidgetEndX(DynamicWidget<?, ?> fromEnd, ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.XPos.FromWidgetEnd<>(this.self(), fromEnd, margin));

        return this.self();
    }

    /**
     * Keep this widget aligned from the ending x-coordinate of the given widget using the given margin. The position
     * will update as needed.
     *
     * @param fromEnd A {@link DynamicWidget} to get ending position data from.
     * @param margin  An {@link IntSupplier} that provides a margin between the end of the given widget and the built
     *                {@link Widget}.
     */
    @PublicAPI
    default Builder fromWidgetEndX(DynamicWidget<?, ?> fromEnd, IntSupplier margin) {
        return this.fromWidgetEndX(fromEnd, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget aligned from the ending x-coordinate of the given widget using the given margin. The position
     * will update as needed.
     *
     * @param fromEnd A {@link DynamicWidget} to get ending position data from.
     * @param margin  The margin from the end of the given widget.
     */
    @PublicAPI
    default Builder fromWidgetEndX(DynamicWidget<?, ?> fromEnd, int margin) {
        return this.fromWidgetEndX(fromEnd, () -> margin);
    }

    /**
     * Keep this widget aligned from the ending x-coordinate of the current screen using the given margin. The position
     * will update as needed.
     *
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the end
     *               of the screen and the built {@link Widget}.
     */
    @PublicAPI
    default Builder fromScreenEndX(ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.XPos.FromScreenEnd<>(this.self(), margin));

        return this.self();
    }

    /**
     * Keep this widget aligned from the ending x-coordinate of the current screen using the given margin. The position
     * will update as needed.
     *
     * @param margin An {@link IntSupplier} that provides a margin between the end of the screen and the built
     *               {@link Widget}.
     */
    @PublicAPI
    default Builder fromScreenEndX(IntSupplier margin) {
        return this.fromScreenEndX(widget -> margin.getAsInt());
    }

    /**
     * Keep this widget aligned from the ending x-coordinate of the current screen using the given margin. The position
     * will update as needed.
     *
     * @param margin The margin from the end of the screen.
     */
    @PublicAPI
    default Builder fromScreenEndX(int margin) {
        return this.fromScreenEndX(() -> margin);
    }

    /**
     * Keep this widget aligned from the ending y-coordinate of the given widget using the given margin. The position
     * will update as needed.
     *
     * @param fromEnd A {@link DynamicWidget} to get ending position data from.
     * @param margin  A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the end
     *                of the given widget and the built {@link Widget}.
     */
    @PublicAPI
    default Builder fromWidgetEndY(DynamicWidget<?, ?> fromEnd, ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.YPos.FromWidgetEnd<>(this.self(), fromEnd, margin));

        return this.self();
    }

    /**
     * Keep this widget aligned from the ending y-coordinate of the given widget using the given margin. The position
     * will update as needed.
     *
     * @param fromEnd A {@link DynamicWidget} to get ending position data from.
     * @param margin  An {@link IntSupplier} that provides a margin between the end of the given widget and the built
     *                {@link Widget}.
     */
    @PublicAPI
    default Builder fromWidgetEndY(DynamicWidget<?, ?> fromEnd, IntSupplier margin) {
        return this.fromWidgetEndY(fromEnd, widget -> margin.getAsInt());
    }

    /**
     * Keep this widget aligned from the ending y-coordinate of the given widget using the given margin. The position
     * will update as needed.
     *
     * @param fromEnd A {@link DynamicWidget} to get ending position data from.
     * @param margin  The margin from the end of the given widget.
     */
    @PublicAPI
    default Builder fromWidgetEndY(DynamicWidget<?, ?> fromEnd, int margin) {
        return this.fromWidgetEndY(fromEnd, () -> margin);
    }

    /**
     * Keep this widget aligned from the ending y-coordinate of the current screen using the given margin. The position
     * will update as needed.
     *
     * @param margin A {@link ToIntFunction} that accepts the built {@link Widget} and returns a margin between the end
     *               of the screen and the built {@link Widget}.
     */
    @PublicAPI
    default Builder fromScreenEndY(ToIntFunction<Widget> margin) {
        this.self().addFunction(new DynamicLayout.YPos.FromScreenEnd<>(this.self(), margin));

        return this.self();
    }

    /**
     * Keep this widget aligned from the ending y-coordinate of the current screen using the given margin. The position
     * will update as needed.
     *
     * @param margin An {@link IntSupplier} that provides a margin between the end of the screen and the built
     *               {@link Widget}.
     */
    @PublicAPI
    default Builder fromScreenEndY(IntSupplier margin) {
        return this.fromScreenEndY(widget -> margin.getAsInt());
    }

    /**
     * Keep this widget aligned from the ending y-coordinate of the current screen using the given margin. The position
     * will update as needed.
     *
     * @param margin The margin from the end of the screen.
     */
    @PublicAPI
    default Builder fromScreenEndY(int margin) {
        return this.fromScreenEndY(() -> margin);
    }

    /**
     * Keep this widget flush (x-axis) with the provided widget. The widget associated with this builder will have its
     * x-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     * @param offset  A {@link ToIntFunction} that accepts the built {@link Widget} and returns an offset to be added to
     *                the widget's x-coordinate.
     */
    @PublicAPI
    default Builder alignFlushTo(@Nullable DynamicWidget<?, ?> flushTo, ToIntFunction<Widget> offset) {
        if (flushTo == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.XPos.AlignFlush<>(this.self(), flushTo, offset));

        return this.self();
    }

    /**
     * Keep this widget flush (x-axis) with the provided widget. The widget associated with this builder will have its
     * x-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     * @param offset  A {@link IntSupplier} that provides an offset to be added to the widget's x-coordinate.
     */
    @PublicAPI
    default Builder alignFlushTo(@Nullable DynamicWidget<?, ?> flushTo, IntSupplier offset) {
        return this.alignFlushTo(flushTo, widget -> offset.getAsInt());
    }

    /**
     * Keep this widget flush (x-axis) with the provided widget. The widget associated with this builder will have its
     * x-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     * @param offset  An offset to be added to the widget's x-coordinate.
     */
    @PublicAPI
    default Builder alignFlushTo(@Nullable DynamicWidget<?, ?> flushTo, int offset) {
        return this.alignFlushTo(flushTo, () -> offset);
    }

    /**
     * Keep this widget flush (x-axis) with the provided widget. The widget associated with this builder will have its
     * x-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     */
    @PublicAPI
    default Builder alignFlushTo(@Nullable DynamicWidget<?, ?> flushTo) {
        return this.alignFlushTo(flushTo, 0);
    }

    /**
     * Keep this widget flush (y-axis) with the provided widget. The widget associated with this builder will have its
     * y-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     * @param offset  A {@link ToIntFunction} that accepts the built {@link Widget} and returns an offset to be added to
     *                the widget's y-coordinate.
     */
    @PublicAPI
    default Builder alignVerticalTo(@Nullable DynamicWidget<?, ?> flushTo, ToIntFunction<Widget> offset) {
        if (flushTo == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.YPos.AlignVertical<>(this.self(), flushTo, offset));

        return this.self();
    }

    /**
     * Keep this widget flush (y-axis) with the provided widget. The widget associated with this builder will have its
     * y-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     * @param offset  A {@link IntSupplier} that provides an offset to be added to the widget's y-coordinate.
     */
    @PublicAPI
    default Builder alignVerticalTo(@Nullable DynamicWidget<?, ?> flushTo, IntSupplier offset) {
        return this.alignVerticalTo(flushTo, widget -> offset.getAsInt());
    }

    /**
     * Keep this widget flush (y-axis) with the provided widget. The widget associated with this builder will have its
     * y-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     * @param offset  An offset to be added to the widget's y-coordinate.
     */
    @PublicAPI
    default Builder alignVerticalTo(@Nullable DynamicWidget<?, ?> flushTo, int offset) {
        return this.alignVerticalTo(flushTo, () -> offset);
    }

    /**
     * Keep this widget flush (y-axis) with the provided widget. The widget associated with this builder will have its
     * y-coordinate aligned to the given widget when needed.
     *
     * @param flushTo A {@link DynamicWidget} to stay flush with.
     */
    @PublicAPI
    default Builder alignVerticalTo(@Nullable DynamicWidget<?, ?> flushTo) {
        return this.alignVerticalTo(flushTo, 0);
    }

    /**
     * Center this widget within the current screen relative to the x-axis.
     *
     * @param extraWidth A {@link ToIntFunction} that accepts the built {@link Widget} and returns a width to add onto
     *                   the built widget's width.
     */
    @PublicAPI
    default Builder centerInScreenX(ToIntFunction<Widget> extraWidth) {
        this.self().addFunction(new DynamicLayout.XPos.CenterInScreen<>(this.self(), extraWidth));

        return this.self();
    }

    /**
     * Center this widget within the current screen relative to the x-axis.
     *
     * @param extraWidth A {@link IntSupplier} that provides a width to add onto the built widget's width.
     */
    @PublicAPI
    default Builder centerInScreenX(IntSupplier extraWidth) {
        return this.centerInScreenX(widget -> extraWidth.getAsInt());
    }

    /**
     * Center this widget within the current screen relative to the x-axis.
     *
     * @param extraWidth A width to add onto the built widget's width.
     */
    @PublicAPI
    default Builder centerInScreenX(int extraWidth) {
        return this.centerInScreenX(() -> extraWidth);
    }

    /**
     * Center this widget within the current screen relative to the x-axis.
     */
    @PublicAPI
    default Builder centerInScreenX() {
        return this.centerInScreenX(0);
    }

    /**
     * Center this widget within the current screen relative to the y-axis.
     *
     * @param extraHeight A {@link ToIntFunction} that accepts the built {@link Widget} and returns a height to add onto
     *                    the built widget's height.
     */
    @PublicAPI
    default Builder centerInScreenY(ToIntFunction<Widget> extraHeight) {
        this.self().addFunction(new DynamicLayout.YPos.CenterInScreen<>(this.self(), extraHeight));

        return this.self();
    }

    /**
     * Center this widget within the current screen relative to the y-axis.
     *
     * @param extraHeight A {@link IntSupplier} that provides a height to add onto the built widget's height.
     */
    @PublicAPI
    default Builder centerInScreenY(IntSupplier extraHeight) {
        return this.centerInScreenY(widget -> extraHeight.getAsInt());
    }

    /**
     * Center this widget within the current screen relative to the y-axis.
     *
     * @param extraHeight A height to add onto the built widget's height.
     */
    @PublicAPI
    default Builder centerInScreenY(int extraHeight) {
        return this.centerInScreenY(() -> extraHeight);
    }

    /**
     * Center this widget within the current screen relative to the y-axis.
     */
    @PublicAPI
    default Builder centerInScreenY() {
        return this.centerInScreenY(0);
    }

    /**
     * Center this widget within the given widget relative to the x-axis.
     *
     * @param centerIn   A {@link DynamicWidget} to center within.
     * @param extraWidth A {@link ToIntFunction} that accepts the built {@link Widget} and returns a width to add onto
     *                   the built widget's width.
     */
    @PublicAPI
    default Builder centerInWidgetX(@Nullable DynamicWidget<?, ?> centerIn, ToIntFunction<Widget> extraWidth) {
        if (centerIn == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.XPos.CenterInWidget<>(this.self(), centerIn, extraWidth));

        return this.self();
    }

    /**
     * Center this widget within the given widget relative to the x-axis.
     *
     * @param centerIn   A {@link DynamicWidget} to center within.
     * @param extraWidth A {@link IntSupplier} that provides a width to add onto the built widget's width.
     */
    @PublicAPI
    default Builder centerInWidgetX(@Nullable DynamicWidget<?, ?> centerIn, IntSupplier extraWidth) {
        return this.centerInWidgetX(centerIn, widget -> extraWidth.getAsInt());
    }

    /**
     * Center this widget within the given widget relative to the x-axis.
     *
     * @param centerIn   A {@link DynamicWidget} to center within.
     * @param extraWidth A width to add onto the built widget's width.
     */
    @PublicAPI
    default Builder centerInWidgetX(@Nullable DynamicWidget<?, ?> centerIn, int extraWidth) {
        return this.centerInWidgetX(centerIn, () -> extraWidth);
    }

    /**
     * Center this widget within the given widget relative to the x-axis.
     *
     * @param centerIn A {@link DynamicWidget} to center within.
     */
    @PublicAPI
    default Builder centerInWidgetX(@Nullable DynamicWidget<?, ?> centerIn) {
        return this.centerInWidgetX(centerIn, 0);
    }

    /**
     * Center this widget within the given widget relative to the y-axis.
     *
     * @param centerIn    A {@link DynamicWidget} to center within.
     * @param extraHeight A {@link ToIntFunction} that accepts the built {@link Widget} and returns a height to add onto
     *                    the built widget's height.
     */
    @PublicAPI
    default Builder centerInWidgetY(@Nullable DynamicWidget<?, ?> centerIn, ToIntFunction<Widget> extraHeight) {
        if (centerIn == null)
            return this.self();

        this.self().addFunction(new DynamicLayout.YPos.CenterInWidget<>(this.self(), centerIn, extraHeight));

        return this.self();
    }

    /**
     * Center this widget within the given widget relative to the y-axis.
     *
     * @param centerIn    A {@link DynamicWidget} to center within.
     * @param extraHeight A {@link IntSupplier} that provides a height to add onto the built widget's height.
     */
    @PublicAPI
    default Builder centerInWidgetY(@Nullable DynamicWidget<?, ?> centerIn, IntSupplier extraHeight) {
        return this.centerInWidgetY(centerIn, widget -> extraHeight.getAsInt());
    }

    /**
     * Center this widget within the given widget relative to the y-axis.
     *
     * @param centerIn    A {@link DynamicWidget} to center within.
     * @param extraHeight A height to add onto the built widget's height.
     */
    @PublicAPI
    default Builder centerInWidgetY(@Nullable DynamicWidget<?, ?> centerIn, int extraHeight) {
        return this.centerInWidgetY(centerIn, () -> extraHeight);
    }

    /**
     * Center this widget within the given widget relative to the y-axis.
     *
     * @param centerIn A {@link DynamicWidget} to center within.
     */
    @PublicAPI
    default Builder centerInWidgetY(@Nullable DynamicWidget<?, ?> centerIn) {
        return this.centerInWidgetY(centerIn, 0);
    }

    /**
     * Define a relative parent that manages this widget.
     *
     * @param relativeLayout A parent that implements {@link RelativeLayout}.
     */
    @PublicAPI
    default Builder relativeTo(@Nullable RelativeLayout relativeLayout) {
        this.self().relativeLayout = relativeLayout;

        return this.self();
    }

    /**
     * Force the built widget to stay relative on the x-axis to the assigned {@link RelativeLayout}. This should be on
     * widgets that are following an anchored widget.
     *
     * @see #relativeTo(RelativeLayout)
     */
    @PublicAPI
    default Builder forceRelativeX() {
        this.self().forceRelativeX = true;

        return this.self();
    }

    /**
     * Force the built widget to stay relative on the y-axis to the assigned {@link RelativeLayout}. This should be used
     * on widgets that are following an anchored widget, but still want to move with its {@link RelativeLayout}.
     *
     * @see #relativeTo(RelativeLayout)
     */
    @PublicAPI
    default Builder forceRelativeY() {
        this.self().forceRelativeY = true;

        return this.self();
    }

    /**
     * Set this widget so that it is anchored relative to its {@link RelativeLayout}. For example, this will prevent the
     * widget from being scrolled, but still stays in sync with the dynamic position of the parent.
     */
    @PublicAPI
    default Builder anchor() {
        this.self().relativeAnchor = true;

        return this.self();
    }

    /**
     * Set this widget so that it is no longer anchored relative to its {@link RelativeLayout}.
     */
    @PublicAPI
    default Builder removeAnchor() {
        this.self().relativeAnchor = false;

        return this.self();
    }

    /**
     * Add widgets that will follow this widget. When this widget is updated, all of its followers will be updated as
     * well before this widget's cache is updated. None of the followers will have their cache updated.
     *
     * @param widgets A {@link Collection} of {@link DynamicWidget}.
     */
    @PublicAPI
    default Builder addFollowers(Collection<? extends DynamicWidget<?, ?>> widgets) {
        this.self().followers.addAll(widgets);

        return this.self();
    }

    /**
     * Add widgets that will follow this widget. When this widget is updated, all of its followers will be updated as
     * well before this widget's cache is updated. None of the followers will have their cache updated.
     *
     * @param widgets A varargs of {@link DynamicWidget}.
     */
    @PublicAPI
    default Builder addFollowers(DynamicWidget<?, ?>... widgets) {
        return this.addFollowers(List.of(widgets));
    }

    /**
     * Reset any supplier set for the widget's x-coordinate back to {@code null}.
     */
    @PublicAPI
    default Builder resetX() {
        this.self().x = null;

        return this.self();
    }

    /**
     * Reset any supplier set for the widget's y-coordinate back to {@code null}.
     */
    @PublicAPI
    default Builder resetY() {
        this.self().y = null;

        return this.self();
    }

    /**
     * Reset any supplier set for the widget's width back to {@code null}.
     */
    @PublicAPI
    default Builder resetWidth() {
        this.self().width = null;

        return this.self();
    }

    /**
     * Reset any supplier set for the widget's height back to {@code null}.
     */
    @PublicAPI
    default Builder resetHeight() {
        this.self().height = null;

        return this.self();
    }
}
