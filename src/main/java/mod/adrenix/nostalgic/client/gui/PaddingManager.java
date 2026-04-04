package mod.adrenix.nostalgic.client.gui;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

public interface PaddingManager<T> {
    /**
     * Set the left padding.
     *
     * @param padding The padding amount.
     */
    @PublicAPI
    T paddingLeft(int padding);

    /**
     * Set the top padding.
     *
     * @param padding The padding amount.
     */
    @PublicAPI
    T paddingTop(int padding);

    /**
     * Set the right padding.
     *
     * @param padding The padding amount.
     */
    @PublicAPI
    T paddingRight(int padding);

    /**
     * Set the bottom padding.
     *
     * @param padding The padding amount.
     */
    @PublicAPI
    T paddingBottom(int padding);

    /**
     * Set the vertical padding.
     *
     * @param padding The padding on the top and bottom.
     */
    @PublicAPI
    default T paddingVertical(int padding) {
        this.paddingTop(padding);

        return this.paddingBottom(padding);
    }

    /**
     * Set the horizontal padding.
     *
     * @param padding The padding on the left and right side.
     */
    @PublicAPI
    default T paddingHorizontal(int padding) {
        this.paddingLeft(padding);

        return this.paddingRight(padding);
    }

    /**
     * Set a universal padding amount.
     *
     * @param padding The universal padding.
     */
    @PublicAPI
    default T padding(int padding) {
        this.paddingLeft(padding);
        this.paddingRight(padding);
        this.paddingTop(padding);

        return this.paddingBottom(padding);
    }

    /**
     * Set the horizontal and vertical padding.
     *
     * @param horizontalPadding The horizontal padding.
     * @param verticalPadding   The vertical padding.
     */
    @PublicAPI
    default T padding(int horizontalPadding, int verticalPadding) {
        this.paddingHorizontal(horizontalPadding);

        return this.paddingVertical(verticalPadding);
    }

    /**
     * Set different padding between each side.
     *
     * @param paddingLeft   The padding on the left.
     * @param paddingTop    The padding from the top.
     * @param paddingRight  The padding on the right.
     * @param paddingBottom The padding from the bottom.
     */
    @PublicAPI
    default T padding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        this.paddingLeft(paddingLeft);
        this.paddingTop(paddingTop);
        this.paddingRight(paddingRight);

        return this.paddingBottom(paddingBottom);
    }
}
