package mod.adrenix.nostalgic.util.common.function;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The purpose of this utility is to run a {@code forEach} loop that tracks the previous element processed by the
 * {@code forEach} loop. This is a functional builder utility so that functional programming can be used to define how
 * the {@code forEach} loop processes each element within the given {@link Stream} or {@link Collection}.
 */
public interface ForEachWithPrevious {
    /**
     * Functional builder for running a {@code forEach} loop with a previous element tracker.
     *
     * @param stream The stream of elements to run a {@code forEach} loop on.
     * @param <T>    The class type of stream elements.
     * @return A new builder instance.
     */
    static <T> Builder<T> create(Stream<T> stream) {
        return new Builder<>(stream.toList());
    }

    /**
     * Overload method for the functional builder that runs a {@code forEach} loop with a previous element tracker.
     *
     * @param collection The collection of elements to run a {@code forEach} loop on.
     * @param <T>        The class type of collection elements.
     * @return A new builder instance.
     */
    static <T> Builder<T> create(Collection<T> collection) {
        return new Builder<>(collection);
    }

    /**
     * Overload method for the functional builder that runs a {@code forEach} loop with a previous element tracker.
     *
     * @param array An array that will be converted into a collection of elements to run a {@code forEach} loop on.
     * @param <T>   The class type of the array.
     * @return A new builder instance.
     */
    static <T> Builder<T> create(T[] array) {
        return new Builder<>(Arrays.asList(array));
    }

    /* Builder */

    class Builder<T> {
        private final Collection<T> elements;

        private BiConsumer<T, T> forEach = (a, b) -> {
        };
        private Consumer<T> first = null;
        private Consumer<T> last = null;
        private Consumer<T> equal = null;
        private Predicate<T> returnNextWhenNext = null;
        private Predicate<T> returnNextWhenPrev = null;
        private Predicate<T> returnPrevWhenNext = null;
        private Runnable whenFinished = null;

        private Builder(Collection<T> elements) {
            this.elements = elements;
        }

        /* Methods */

        /**
         * The provided {@link BiConsumer} argument is applied with the previously processed element and the next
         * element to be processed. The {@code BiConsumer} will never receive two objects where {@code o1.equals(o2)}.
         * <br><br>
         * Other special operations can be performed within the {@code for { ... }} block, such as returning a previous
         * element or next element when a certain condition is met, and running additional instructions before the
         * {@link Builder#run()} method exits.
         *
         * @param biConsumer A {@link BiConsumer} that accepts the previously processed element as the first argument
         *                   and the next element to be processed as the second argument.
         * @see Builder#applyToFirst(Consumer)
         * @see Builder#applyToLast(Consumer)
         * @see Builder#returnNextWhenPrev(Predicate)
         * @see Builder#returnPrevWhenNext(Predicate)
         * @see Builder#whenFinished(Runnable)
         */
        @PublicAPI
        public Builder<T> forEach(BiConsumer<T, T> biConsumer) {
            this.forEach = biConsumer;
            return this;
        }

        /**
         * Instructions to perform when the {@code forEach} loop processes the first element.
         *
         * @param consumer A {@link Consumer} that accepts the {@code first} element processed in the {@code forEach}
         *                 loop.
         */
        @PublicAPI
        public Builder<T> applyToFirst(Consumer<T> consumer) {
            this.first = consumer;
            return this;
        }

        /**
         * Instructions to perform on the last element cached after the {@code forEach} loop ends.
         *
         * @param consumer A {@link Consumer} that accepts the {@code last} element processed by the {@code forEach}
         *                 loop.
         */
        @PublicAPI
        public Builder<T> applyToLast(Consumer<T> consumer) {
            this.last = consumer;
            return this;
        }

        /**
         * Instructions to perform when a previous element equals the next element.
         *
         * @param consumer A {@link Consumer} that accepts an element.
         */
        @PublicAPI
        public Builder<T> applyToEqual(Consumer<T> consumer) {
            this.equal = consumer;
            return this;
        }

        /**
         * Exit the {@code forEach} loop early and return the current element when the predicate test yields a truthful
         * value when provided the current element.
         *
         * @param predicate A predicate that accepts the next element being processed and returns whether the next
         *                  element should be returned.
         */
        @PublicAPI
        public Builder<T> returnNextWhenNext(Predicate<T> predicate) {
            this.returnNextWhenNext = predicate;
            return this;
        }

        /**
         * Exit the {@code forEach} loop early and return the current element when the predicate yields a truthful value
         * when provided the previous element.
         *
         * @param function A predicate that accepts the last element processed and returns whether the current element
         *                 should be returned.
         */
        @PublicAPI
        public Builder<T> returnNextWhenPrev(Predicate<T> function) {
            this.returnNextWhenPrev = function;
            return this;
        }

        /**
         * Exit the {@code forEach} loop early and return the previously processed element when the predicate yields a
         * truthful value.
         *
         * @param predicate A predicate that accepts the current element and tests whether the previous element should
         *                  be returned.
         */
        @PublicAPI
        public Builder<T> returnPrevWhenNext(Predicate<T> predicate) {
            this.returnPrevWhenNext = predicate;
            return this;
        }

        /**
         * Run additional instructions right before the {@link Builder#run()} is returned. These instructions will only
         * be executed if another builder flag has returned from within the {@code forEach} loop.
         *
         * @param runnable Additional instructions to run right before the run function returns an optional of the last
         *                 element processed.
         */
        @PublicAPI
        public Builder<T> whenFinished(Runnable runnable) {
            this.whenFinished = runnable;
            return this;
        }

        /**
         * Runs the {@code forEach} loop using the properties set by the {@link Builder}. The optional returned by this
         * run method will be an empty optional if there are no elements in the stream. Some builder functions will
         * force an early return of the next or previous element when certain conditions are met. If those functions are
         * defined, then an empty optional will be returned at the end of the run if those functions never returned a
         * truthful value.
         */
        public Optional<T> run() {
            boolean isFirst = true;
            T last = null;

            for (T next : this.elements) {
                if (isFirst) {
                    if (this.first != null)
                        this.first.accept(next);

                    last = next;
                    isFirst = false;
                }

                if (this.returnNextWhenNext != null) {
                    if (this.returnNextWhenNext.test(next))
                        return Optional.of(next);
                }

                if (next.equals(last)) {
                    if (this.equal != null)
                        this.equal.accept(next);

                    continue;
                }

                if (this.returnNextWhenPrev != null) {
                    if (this.returnNextWhenPrev.test(last))
                        return Optional.of(next);
                }

                if (this.returnPrevWhenNext != null) {
                    if (this.returnPrevWhenNext.test(next))
                        return Optional.of(last);
                }

                this.forEach.accept(last, next);

                last = next;
            }

            if (this.last != null)
                this.last.accept(last);

            if (this.whenFinished != null)
                this.whenFinished.run();

            boolean isReturnUndefined = this.returnNextWhenNext == null && this.returnNextWhenPrev == null && this.returnPrevWhenNext == null;

            return isReturnUndefined ? Optional.ofNullable(last) : Optional.empty();
        }
    }
}
