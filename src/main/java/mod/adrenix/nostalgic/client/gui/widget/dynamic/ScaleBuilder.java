package mod.adrenix.nostalgic.client.gui.widget.dynamic;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.FloatSupplier;
import mod.adrenix.nostalgic.util.common.function.ToFloatFunction;

/**
 * This builder will update the scaling fields in {@link DynamicBuilder}. Updates will be reflected onto the built
 * {@link Widget}.
 *
 * @param <Builder> The builder class type that is implementing this interface.
 * @param <Widget>  The widget class type that is implementing {@link DynamicWidget}.
 */
public interface ScaleBuilder<Builder extends DynamicBuilder<Builder, Widget>, Widget extends DynamicWidget<Builder, Widget>>
        extends SelfBuilder<Builder, Widget> {
    /**
     * Resize the width and height using the scale provided by the given function.
     *
     * @param resizer A {@link ToFloatFunction} that yields a scale amount.
     */
    @PublicAPI
    default Builder scale(ToFloatFunction<Widget> resizer) {
        this.self().scaleWidth = resizer;
        this.self().scaleHeight = resizer;

        return this.self();
    }

    /**
     * Resize the width and height using the scale provided by the given supplier.
     *
     * @param supplier A {@link FloatSupplier} that provides a scale amount.
     */
    @PublicAPI
    default Builder scale(FloatSupplier supplier) {
        return this.scale((widget) -> supplier.getAsFloat());
    }

    /**
     * Resize the width and height using the scale provided.
     *
     * @param amount The amount to scale by.
     */
    @PublicAPI
    default Builder scale(float amount) {
        return this.scale(() -> amount);
    }

    /**
     * Resize the width using the scale provided by the given function.
     *
     * @param resizer A {@link ToFloatFunction} that yields a scale amount.
     */
    @PublicAPI
    default Builder scaleWidth(ToFloatFunction<Widget> resizer) {
        this.self().scaleWidth = resizer;

        return this.self();
    }

    /**
     * Resize the width using the scale provided by the given supplier.
     *
     * @param supplier A {@link FloatSupplier} that provides a scale amount.
     */
    @PublicAPI
    default Builder scaleWidth(FloatSupplier supplier) {
        return this.scaleWidth((widget) -> supplier.getAsFloat());
    }

    /**
     * Resize the width using the scale provided.
     *
     * @param amount The amount to scale by.
     */
    @PublicAPI
    default Builder scaleWidth(float amount) {
        return this.scaleWidth(() -> amount);
    }

    /**
     * Resize the height using the scale provided by the given function.
     *
     * @param resizer A {@link ToFloatFunction} that yields a scale amount.
     */
    @PublicAPI
    default Builder scaleHeight(ToFloatFunction<Widget> resizer) {
        this.self().scaleHeight = resizer;

        return this.self();
    }

    /**
     * Resize the height using the scale provided by the given supplier.
     *
     * @param supplier A {@link FloatSupplier} that provides a scale amount.
     */
    @PublicAPI
    default Builder scaleHeight(FloatSupplier supplier) {
        return this.scaleHeight((widget) -> supplier.getAsFloat());
    }

    /**
     * Resize the height using the scale provided.
     *
     * @param amount The amount to scale by.
     */
    @PublicAPI
    default Builder scaleHeight(float amount) {
        return this.scaleHeight(() -> amount);
    }
}
