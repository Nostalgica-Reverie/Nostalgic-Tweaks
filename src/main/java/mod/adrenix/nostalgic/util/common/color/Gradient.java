package mod.adrenix.nostalgic.util.common.color;

/**
 * There are two types of gradients available, {@link Gradient#vertical(Color, Color)} and
 * {@link Gradient#horizontal(Color, Color)}. This interface helps to quickly define a gradient color. Using this
 * interface prevents the need of defining and maintaining the extra plumbing required for tracking colors and
 * direction. The client renderer utility {@code RenderUtil#gradient} can be used to quickly render a defined gradient
 * instance.
 */
public interface Gradient {
    /**
     * Define a new {@code vertical} {@link Gradient}.
     *
     * @param top    The starting {@link Color} of the gradient.
     * @param bottom The ending {@link Color} of the gradient.
     * @return A new {@link Gradient} instance.
     */
    static Gradient vertical(Color top, Color bottom) {
        return new Gradient() {
            @Override
            public Direction direction() {
                return Direction.VERTICAL;
            }

            @Override
            public Color from() {
                return top;
            }

            @Override
            public Color to() {
                return bottom;
            }
        };
    }

    /**
     * Define a new {@code horizontal} {@link Gradient}.
     *
     * @param left  The starting {@link Color} of the gradient.
     * @param right The ending {@link Color} of the gradient.
     * @return A new {@link Gradient} instance.
     */
    static Gradient horizontal(Color left, Color right) {
        return new Gradient() {
            @Override
            public Direction direction() {
                return Direction.HORIZONTAL;
            }

            @Override
            public Color from() {
                return left;
            }

            @Override
            public Color to() {
                return right;
            }
        };
    }

    /**
     * @return The {@link Direction} of the gradient.
     */
    Direction direction();

    /**
     * @return The starting {@link Color} of the gradient.
     */
    Color from();

    /**
     * @return The ending {@link Color} of the gradient.
     */
    Color to();

    /**
     * Simple enumeration that defines the direction this gradient should go.
     */
    enum Direction {
        HORIZONTAL,
        VERTICAL
    }
}
