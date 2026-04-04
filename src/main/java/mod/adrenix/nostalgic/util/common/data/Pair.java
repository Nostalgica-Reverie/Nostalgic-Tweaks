package mod.adrenix.nostalgic.util.common.data;

import java.util.Map;

public record Pair<L, R>(L left, R right) {
    /**
     * Create a new pair using a static creator.
     *
     * @param left  The left side of the pair.
     * @param right The right side of the pair.
     * @param <L>   The class type of the left side.
     * @param <R>   The class type of the right side.
     * @return A new {@link Pair} instance.
     */
    public static <L, R> Pair<L, R> of(L left, R right) {
        return new Pair<>(left, right);
    }

    /**
     * Create a new pair using a map entry.
     *
     * @param entry A {@link Map.Entry} instance.
     * @param <L>   The class type of the entry key.
     * @param <R>   The class type of the entry value.
     * @return A new {@link Pair} instance.
     */
    public static <L, R> Pair<L, R> entry(Map.Entry<L, R> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    /**
     * Create a new key/value pair. The key will be {@code value.toString()}.
     *
     * @param value The value of this pair.
     * @param <V>   The class type of the value.
     * @return A new {@link Pair} instance.
     */
    public static <V> Pair<String, V> keyValue(V value) {
        return new Pair<>(value.toString(), value);
    }
}
