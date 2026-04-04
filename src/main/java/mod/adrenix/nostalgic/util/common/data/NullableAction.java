package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class NullableAction<T> {
    /* Static */

    @Nullable
    private final T nullable;
    private final Consumer<T> action;
    private final Runnable orElse;

    private NullableAction(@Nullable T nullable, Consumer<T> action, Runnable orElse) {
        this.nullable = nullable;
        this.action = action;
        this.orElse = orElse;
    }

    /* Fields */

    /**
     * Attempt to perform an action on a value that may be {@code null}. If the value is {@code null}, then nothing will
     * happen. Otherwise, the given action will be performed.
     *
     * @param nullable A nullable value.
     * @param action   A {@link Consumer} that accepts the given value. The provided consumer will <b color=red>not</b>
     *                 receive a {@code null} value.
     * @param <T>      The class type of the nullable.
     */
    @PublicAPI
    public static <T> void attempt(@Nullable T nullable, Consumer<T> action) {
        new NullableAction<>(nullable, action, () -> {
        }).apply();
    }

    /**
     * Attempt to perform an action on a value that may be {@code null}. If the value is {@code null}, then the given
     * {@link Runnable} will run. Otherwise, the given action will be performed.
     *
     * @param nullable A nullable value.
     * @param action   A {@link Consumer} that accepts the given value. The provided consumer will <b color=red>not</b>
     *                 receive a {@code null} value.
     * @param orElse   A {@link Runnable} that runs if the given value is {@code null}.
     * @param <T>      The class type of the nullable.
     */
    @PublicAPI
    public static <T> void attempt(@Nullable T nullable, Consumer<T> action, Runnable orElse) {
        new NullableAction<>(nullable, action, orElse).apply();
    }

    /**
     * Attempt to perform an action on a {@link NullableHolder} that may have a {@code null} value. If the value is
     * {@code null}, then nothing will happen. Otherwise, the given action will be performed.
     *
     * @param holder A {@link NullableHolder} instance.
     * @param action A {@link Consumer} that accepts the {@link NullableHolder} value. The provided consumer will <b
     *               color=red>not</b> receive a {@code null} value.
     * @param <T>    The class type of what is stored in {@link NullableHolder}.
     */
    @PublicAPI
    public static <T> void attempt(NullableHolder<T> holder, Consumer<T> action) {
        new NullableAction<>(holder.get(), action, () -> {
        }).apply();
    }

    /* Constructor */

    /**
     * Attempt to perform an action on a {@link NullableHolder} that may have a {@code null} value. If the value is
     * {@code null}, then the given {@link Runnable} will run. Otherwise, the given action will be performed.
     *
     * @param holder A {@link NullableHolder} instance.
     * @param action A {@link Consumer} that accepts the {@link NullableHolder} value. The provided consumer will <b
     *               color=red>not</b> receive a {@code null} value.
     * @param orElse A {@link Runnable} that runs if the given value is {@code null}.
     * @param <T>    The class type of what is stored in {@link NullableHolder}.
     */
    @PublicAPI
    public static <T> void attempt(NullableHolder<T> holder, Consumer<T> action, Runnable orElse) {
        new NullableAction<>(holder.get(), action, orElse).apply();
    }

    /* Methods */

    private void apply() {
        if (this.nullable != null)
            this.action.accept(this.nullable);
        else
            this.orElse.run();
    }
}
