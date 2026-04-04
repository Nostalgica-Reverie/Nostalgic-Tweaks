package mod.adrenix.nostalgic.util.client.renderer;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public enum RenderPass {
    FIRST,
    MIDDLE,
    LAST;

    /* Static */

    /**
     * Apply every render pass to the given arguments.
     *
     * @param collection A {@link Collection} of {@code T}.
     * @param mapper     A {@link Function} that accepts {@code T} and returns a {@link RenderPass}.
     * @param consumer   A {@link Consumer} that accepts filtered {@code T} based on the current pass.
     * @param <T>        The class type of {@code T}.
     */
    public static <T> void apply(Collection<T> collection, Function<T, RenderPass> mapper, Consumer<T> consumer) {
        for (RenderPass pass : RenderPass.values())
            filter(pass, collection, mapper).forEach(consumer);
    }

    /**
     * Filter a collection into a stream using a render pass mapper.
     *
     * @param pass       The {@link RenderPass} to filter by.
     * @param collection A {@link Collection} of {@code T}.
     * @param mapper     A {@link Function} that accepts {@code T} and returns a {@link RenderPass}.
     * @param <T>        The class type of {@code T}.
     * @return A {@link Stream} of filtered {@code T}.
     */
    public static <T> Stream<T> filter(RenderPass pass, Collection<T> collection, Function<T, RenderPass> mapper) {
        return collection.stream().filter(t -> mapper.apply(t).equals(pass));
    }
}
