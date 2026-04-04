package mod.adrenix.nostalgic.util.common.data;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A nullable holder is useful in situations where there is a nullable value and a need to avoid Java's pass-by-value
 * for primitive data types. To create a new instance, you can use the {@link NullableHolder#NullableHolder(Object)} or
 * use {@link NullableHolder#create(Object)}.
 *
 * @param <T> The class type of the value held.
 * @see NullableHolder#set(Object)
 * @see NullableHolder#optional()
 */
public class NullableHolder<T> {
    /* Fields */

    @Nullable
    private T value;

    /* Constructor */

    public NullableHolder(@Nullable T value) {
        this.value = value;
    }

    /* Static */

    /**
     * Create a new {@link NullableHolder} instance using the given value. Useful in situations where there is a need to
     * bypass Java's pass-by-value for primitive data types; for example, a {@code boolean} value. This utility provides
     * functional setters/getters for the provided value.
     *
     * @param <V>   The class type of the value.
     * @param value The value to create an object ({@link NullableHolder}) reference for.
     * @return A new {@link NullableHolder} instance.
     * @see NullableHolder#set(Object)
     * @see NullableHolder#optional()
     * @see NullableHolder#empty()
     */
    public static <V> NullableHolder<V> create(@Nullable V value) {
        return new NullableHolder<>(value);
    }

    /**
     * Create a new {@link NullableHolder} instance that is empty.
     *
     * @param <V> The class type of the value.
     * @return A new {@link NullableHolder} instance.
     * @see NullableHolder#create(Object)
     */
    public static <V> NullableHolder<V> empty() {
        return new NullableHolder<>(null);
    }

    /* Methods */

    /**
     * Change the value that is being stored in this {@link NullableHolder}.
     *
     * @param value A new value to store.
     * @see #get()
     * @see #optional()
     */
    @PublicAPI
    public void set(@Nullable T value) {
        this.value = value;
    }

    /**
     * @return The current state of the value being held by this {@link NullableHolder}.
     * @see #set(Object)
     * @see #optional()
     */
    @Nullable
    public T get() {
        return this.value;
    }

    /**
     * If a value is present, returns the value, otherwise throws {@link NoSuchElementException}.
     *
     * @return The non-{@code null} value stored in this holder.
     * @throws NoSuchElementException If no value is present.
     */
    @PublicAPI
    public T getOrThrow() throws NoSuchElementException {
        if (this.value == null)
            throw new NoSuchElementException("No value present");

        return this.value;
    }

    /**
     * Set and return the given {@code non-null} value.
     *
     * @param value A {@code non-null} value.
     * @return The given value.
     */
    @PublicAPI
    public T setAndGet(T value) {
        this.value = value;

        return value;
    }

    /**
     * Set the {@code value} stored in this holder back to {@code null}.
     */
    @PublicAPI
    public void clear() {
        this.value = null;
    }

    /**
     * @return An {@link Optional} holding the current value.
     * @see #get()
     * @see #set(Object)
     */
    @PublicAPI
    public Optional<T> optional() {
        return Optional.ofNullable(this.value);
    }

    /**
     * If the held value is not {@code null}, then that will be returned. Otherwise, the given {@code other} value is
     * returned.
     *
     * @param other The other value to return if this holder is holding {@code null}.
     * @return A guaranteed value.
     */
    @PublicAPI
    public T orElse(T other) {
        return optional().orElse(other);
    }

    /**
     * If the held value is {@code null}, then the given supplier will be used to generate and cache a new value.
     *
     * @param supplier A {@link Supplier} that will generate a new value if the held value is {@code null}.
     * @return The cached value or a new computed value.
     */
    @PublicAPI
    public T computeIfAbsent(Supplier<T> supplier) {
        if (this.value == null)
            this.value = supplier.get();

        return this.value;
    }

    /**
     * Perform a test on the given predicate if the value in this holder is not {@code null}.
     *
     * @param predicate A {@link Predicate} instance.
     * @return If present yields the result of the predicate; otherwise, {@code false}.
     * @see #predicate(Predicate)
     */
    @PublicAPI
    public boolean test(Predicate<T> predicate) {
        return optional().filter(predicate).isPresent();
    }

    /**
     * Get a supplier that will yield the results of the given predicate when called.
     *
     * @param predicate A {@link Predicate} instance.
     * @return A {@link BooleanSupplier} that yields the result of the given predicate.
     * @see #test(Predicate)
     */
    @PublicAPI
    public BooleanSupplier predicate(Predicate<T> predicate) {
        return () -> test(predicate);
    }

    /**
     * Functional shortcut for mapping the optional.
     *
     * @param mapper A {@link Function} that accepts the held value and maps to another value.
     * @param <U>    The class type of the mapped value.
     * @return An {@link Optional} result from mapping.
     */
    @PublicAPI
    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        return optional().map(mapper);
    }

    /**
     * Functional shortcut for a consumer if the optional is present.
     *
     * @param consumer A {@link Consumer} that accepts the value in this holder if it is present.
     */
    @PublicAPI
    public void ifPresent(Consumer<? super T> consumer) {
        optional().ifPresent(consumer);
    }

    /**
     * Functional shortcut for a consumer if the optional is present or a runnable if not present.
     *
     * @param consumer A {@link Consumer} that accepts the value in this holder if it is present.
     * @param runnable A {@link Runnable} to run if the value in this holder is not present.
     */
    @PublicAPI
    public void ifPresentOrElse(Consumer<? super T> consumer, Runnable runnable) {
        optional().ifPresentOrElse(consumer, runnable);
    }

    /**
     * @return Whether the current value is {@code null}.
     */
    @PublicAPI
    public boolean isEmpty() {
        return this.optional().isEmpty();
    }

    /**
     * @return Whether the current value is <b>not</b> {@code null}.
     */
    @PublicAPI
    public boolean isPresent() {
        return this.optional().isPresent();
    }
}
