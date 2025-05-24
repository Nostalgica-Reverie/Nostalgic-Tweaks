package mod.adrenix.nostalgic.util.common;

import com.google.common.collect.Lists;
import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import mod.adrenix.nostalgic.util.common.function.BooleanSupplier;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

public abstract class CollectionUtil
{
    /**
     * Get the last element in a collection.
     *
     * @param collection A collection of elements.
     * @param <T>        The class type of the elements.
     * @return An optional that possibly contains the last element if it exists.
     */
    @PublicAPI
    public static <T> Optional<T> last(Collection<T> collection)
    {
        if (collection.isEmpty())
            return Optional.empty();

        if (collection instanceof List<T> list)
            return Optional.of(list.getLast());

        return collection.stream().skip(collection.size() - 1).findFirst();
    }

    /**
     * Get the last element in a stream by converting it into a list.
     *
     * @param stream A stream of elements.
     * @param <T>    The class type of the stream elements.
     * @return An optional that possibly contains the last element if it exists.
     */
    @PublicAPI
    public static <T> Optional<T> last(Stream<T> stream)
    {
        return last(stream.toList());
    }

    /**
     * Get the first element in a collection.
     *
     * @param collection A collection of elements.
     * @param <T>        The class type of the elements.
     * @return An optional that possibly contains the first element if it exists.
     */
    @PublicAPI
    public static <T> Optional<T> first(Collection<T> collection)
    {
        if (collection.isEmpty())
            return Optional.empty();

        if (collection instanceof List<T> list)
            return Optional.of(list.getFirst());

        return collection.stream().findFirst();
    }

    /**
     * Get the first element in a stream. This is a functional shortcut to help improve the legibility of code.
     *
     * @param stream A stream of elements.
     * @param <T>    The class type of elements.
     * @return An optional that possible contains the first element of the stream if it exists.
     */
    @PublicAPI
    public static <T> Optional<T> first(Stream<T> stream)
    {
        return stream.findFirst();
    }

    /**
     * Get a filtered stream of elements that match the provided extending class type.
     *
     * @param stream    The stream of elements to filter.
     * @param classType The class type to cast against.
     * @param <T>       The original class type.
     * @param <U>       The class type to cast.
     * @return A filtered stream of elements.
     */
    @PublicAPI
    @SuppressWarnings("unchecked") // Objects are filtered by assignment-compatibility with the given class type
    public static <T, U extends T> Stream<U> fromCast(Stream<T> stream, Class<U> classType)
    {
        return (Stream<U>) stream.filter(v -> ClassUtil.isInstanceOf(v, classType));
    }

    /**
     * Get a filtered stream of elements that match the provided extending class type.
     *
     * @param collection The collection of elements to filter.
     * @param classType  The class type to cast against.
     * @param <T>        The original class type.
     * @param <U>        The class type to cast.
     * @return A filtered stream of elements.
     */
    @PublicAPI
    public static <T, U extends T> Stream<U> fromCast(Collection<T> collection, Class<U> classType)
    {
        return fromCast(collection.stream(), classType);
    }

    /**
     * Get a filtered stream of elements that match the provided class type.
     *
     * @param stream    The stream of elements to filter.
     * @param classType The class type to cast against.
     * @param <T>       The original class type.
     * @param <U>       The class type to cast.
     * @return A filtered stream of elements.
     */
    @PublicAPI
    @SuppressWarnings("unchecked") // Objects are filtered by assignment-compatibility with the given class type
    public static <T, U> Stream<U> fromClass(Stream<T> stream, Class<U> classType)
    {
        return (Stream<U>) stream.filter(v -> ClassUtil.isInstanceOf(v, classType));
    }

    /**
     * Get a filtered stream of elements that match the provided class type.
     *
     * @param collection The collection of elements to filter.
     * @param classType  The class type to cast against.
     * @param <T>        The original class type.
     * @param <U>        The class type to cast.
     * @return A filtered stream of elements.
     */
    @PublicAPI
    public static <T, U> Stream<U> fromClass(Collection<T> collection, Class<U> classType)
    {
        return fromClass(collection.stream(), classType);
    }

    /**
     * A {@link Predicate} function that can be applied to stream filters to make a distinct stream based on an
     * extractor function.
     *
     * @param extractor A function that accepts an instance of type {@code T} and returns some object that should be
     *                  distinct among all {@code T}'s within {@code Stream<T>}.
     * @param <T>       The class type of the parent instance.
     * @param <R>       The class type of the extractor function's return value.
     * @return A {@link Predicate} function.
     * @see CollectionUtil#distinct(Stream, Function)
     * @see CollectionUtil#distinct(Collection, Function)
     */
    @PublicAPI
    public static <T, R> Predicate<T> distinct(Function<? super T, R> extractor)
    {
        Map<R, Boolean> seen = new HashMap<>();
        return t -> seen.putIfAbsent(extractor.apply(t), true) == null;
    }

    /**
     * Makes the given {@link Stream} distinct based on the value returned by the extractor {@link Function}.
     *
     * @param stream    A {@link Stream} to process.
     * @param extractor A {@link Function} that accepts an element from the {@code Stream} and returns a value that will
     *                  be checked if it has already been seen by a previous element.
     * @param <T>       The class type of the elements within the {@link Stream}.
     * @param <R>       The class type of the extractor function's return value.
     * @return A {@link Stream} in which each element is distinct based on the extractor {@link Function}.
     * @see CollectionUtil#distinct(Function)
     * @see CollectionUtil#distinct(Collection, Function)
     */
    @PublicAPI
    public static <T, R> Stream<T> distinct(Stream<T> stream, Function<? super T, R> extractor)
    {
        return stream.filter(distinct(extractor));
    }

    /**
     * Converts the given {@link Collection} into a {@link Stream} and makes that {@code Stream} distinct based on the
     * value returned by the extractor {@link Function}.
     *
     * @param collection A {@link Collection} that will be converted into a {@link Stream}.
     * @param extractor  A {@link Function} that accepts an element from the {@code Stream} and returns a value that
     *                   will be checked if it has already been seen by a previous element.
     * @param <T>        The class type of the elements within the {@link Stream}.
     * @param <R>        The class type of the extractor function's return value.
     * @return A {@link Stream} in which each element is distinct based on the extractor {@link Function}.
     * @see CollectionUtil#distinct(Function)
     * @see CollectionUtil#distinct(Stream, Function)
     */
    @PublicAPI
    public static <T, R> Stream<T> distinct(Collection<T> collection, Function<? super T, R> extractor)
    {
        return distinct(collection.stream(), extractor);
    }

    /**
     * Converts the given {@link Collection} into a {@link List} of equally divided partitions (the last partition list
     * may be smaller).
     *
     * @param collection A {@link Collection} to partition.
     * @param size       The size of each partition.
     * @param <T>        The class type of the values stored in the collection.
     * @return A {@link List} of partitioned lists.
     */
    @PublicAPI
    public static <T> List<List<T>> partition(Collection<T> collection, int size)
    {
        return Lists.partition(collection.stream().toList(), size);
    }

    /**
     * Converts the given {@link Stream} into a {@link List} of equally divided partitions (the last partition list may
     * be smaller). Note that this is a terminal operation for the stream.
     *
     * @param stream A {@link Collection} to partition.
     * @param size   The size of each partition.
     * @param <T>    The class type of the values stored in the collection.
     * @return A {@link List} of partitioned lists.
     */
    @PublicAPI
    public static <T> List<List<T>> partition(Stream<T> stream, int size)
    {
        return Lists.partition(stream.toList(), size);
    }

    /**
     * Partition a collection into individual lists equally divided by the given size (the last list may be smaller).
     * Each list will be passed onto the given consumer.
     *
     * @param collection A {@link Collection} to partition.
     * @param size       The size of each partition.
     * @param consumer   A {@link Consumer} that accepts a {@link List} partition.
     * @param <T>        The class type of the values stored in the collection.
     */
    @PublicAPI
    public static <T> void partition(Collection<T> collection, int size, Consumer<List<T>> consumer)
    {
        partition(collection, size).forEach(consumer);
    }

    /**
     * Partition a collection into individual lists equally divided by the given size (the last list may be smaller).
     * Each list will be looped through and will have each of its elements passed to the given consumer along with the
     * element's index position within the partitioned list.
     *
     * @param collection A {@link Collection} to partition.
     * @param size       The size of each partition.
     * @param consumer   A {@link BiConsumer} that accepts an element and the element's index.
     * @param <T>        The class type of the values stored in the collection.
     */
    @PublicAPI
    public static <T> void partition(Collection<T> collection, int size, BiConsumer<T, Integer> consumer)
    {
        Lists.partition(collection.stream().toList(), size).forEach(partition -> {
            for (int i = 0; i < partition.size(); i++)
                consumer.accept(partition.get(i), i);
        });
    }

    /**
     * Partition a collection into individual lists equally divided by the given size (the last list may be smaller).
     * Each list will be passed onto the given consumer. Note that this is a terminal operation for the given stream.
     *
     * @param stream   A {@link Stream} that will be converted into a list to partition.
     * @param size     The size of each partition.
     * @param consumer A {@link Consumer} that accepts a {@link List} partition.
     * @param <T>      The class type of the values stored in the collection.
     */
    @PublicAPI
    public static <T> void partition(Stream<T> stream, int size, Consumer<List<T>> consumer)
    {
        partition(stream.toList(), size, consumer);
    }

    /**
     * Partition a collection into individual lists equally divided by the given size (the last list may be smaller).
     * Each list will be looped through and will have each of its elements passed to the given consumer along with the
     * element's index position within the partitioned list. Note this is a terminal operation for the given stream.
     *
     * @param stream   A {@link Stream} to partition.
     * @param size     The size of each partition.
     * @param consumer A {@link BiConsumer} that accepts an element and the element's index.
     * @param <T>      The class type of the values stored in the collection.
     */
    @PublicAPI
    public static <T> void partition(Stream<T> stream, int size, BiConsumer<T, Integer> consumer)
    {
        partition(stream.toList(), size, consumer);
    }

    /**
     * Functional version of a {@code for-i} loop.
     *
     * @param collection A {@link Collection} to loop over.
     * @param consumer   A {@link BiConsumer} that accepts a value and an index of that value within the for loop.
     * @param <T>        The class type of the values stored in the collection.
     */
    @PublicAPI
    public static <T> void forLoop(Collection<T> collection, BiConsumer<T, Integer> consumer)
    {
        List<T> list = collection.stream().toList();

        for (int i = 0; i < list.size(); i++)
            consumer.accept(list.get(i), i);
    }

    /**
     * Functional version of a {@code for-i} loop. Note that this will be a terminal operation for the given stream.
     *
     * @param stream   A {@link Stream} to loop over.
     * @param consumer A {@link BiConsumer} that accepts a value and an index of that value within the for loop.
     * @param <T>      The class type of the values stored in the collection.
     */
    @PublicAPI
    public static <T> void forLoop(Stream<T> stream, BiConsumer<T, Integer> consumer)
    {
        forLoop(stream.toList(), consumer);
    }

    /**
     * Get a new runnable instance that runs all the given runnables.
     *
     * @param runnables A varargs of {@link Runnable}.
     * @return A {@link Runnable} that will run all the given runnables when called.
     */
    @PublicAPI
    public static Runnable runAll(Runnable... runnables)
    {
        return () -> {
            for (Runnable runnable : runnables)
                runnable.run();
        };
    }

    /**
     * An intermediate stream operation that uses {@link Stream#filter(Predicate)} on all given predicates. The
     * resulting stream will only consist of elements that passed the predicates in the collection.
     *
     * @param stream     A {@link Stream} to process.
     * @param collection A {@link Collection} of {@link Predicate}.
     * @param <T>        The class type of the values in the stream.
     * @return A stream of elements that passed any of the predicate tests.
     */
    @PublicAPI
    public static <T> Stream<T> filterAll(Stream<T> stream, Collection<Predicate<T>> collection)
    {
        for (Predicate<T> predicate : collection)
            stream = stream.filter(predicate);

        return stream;
    }

    /**
     * An intermediate stream operation that uses {@link Stream#filter(Predicate)} on all given predicates. The
     * resulting stream will only consist of elements that passed the given {@link Predicate}(s).
     *
     * @param stream     A {@link Stream} to process.
     * @param predicates A varargs list of {@link Predicate} to test.
     * @param <T>        The class type of the values in the stream.
     * @return A stream of elements that passed any of the predicate tests.
     */
    @PublicAPI
    @SafeVarargs
    public static <T> Stream<T> filterAll(Stream<T> stream, Predicate<T>... predicates)
    {
        return filterAll(stream, Arrays.asList(predicates));
    }

    /**
     * An override method for {@link CollectionUtil#filterAll(Stream, Predicate[])}. The given {@link Collection} will
     * be converted into a {@link Stream}. The resulting stream will only consist of elements that passed the given
     * {@link Predicate}(s).
     *
     * @param collection A {@link Collection} of elements to process.
     * @param predicates A varargs list of {@link Predicate} to test.
     * @param <T>        The class type of the values in the stream.
     * @return A stream of elements that passed any of the predicate tests.
     * @see CollectionUtil#filterAll(Stream, Predicate[])
     */
    @PublicAPI
    @SafeVarargs
    public static <T> Stream<T> filterAll(Collection<T> collection, Predicate<T>... predicates)
    {
        return filterOut(collection.stream(), predicates);
    }

    /**
     * An intermediate stream operation that is the opposite of {@link Stream#filter(Predicate)}. The resulting stream
     * will only consist of elements that did <b>not</b> match a {@link Predicate} in the given collection.
     *
     * @param stream     A {@link Stream} to process.
     * @param collection A {@link Collection} of {@link Predicate} to test.
     * @param <T>        The class type of the values in the stream.
     * @return A stream of elements that were removed if they passed any of the predicate tests.
     */
    public static <T> Stream<T> filterOut(Stream<T> stream, Collection<Predicate<T>> collection)
    {
        for (Predicate<T> predicate : collection)
            stream = stream.filter(t -> !predicate.test(t));

        return stream;
    }

    /**
     * An intermediate stream operation that is the opposite of {@link Stream#filter(Predicate)}. The resulting stream
     * will only consist of elements that did <b>not</b> match the given {@link Predicate}(s).
     *
     * @param stream     A {@link Stream} to process.
     * @param predicates A varargs list of {@link Predicate} to test.
     * @param <T>        The class type of the stream.
     * @return A stream of elements that were removed if they passed any of the predicate tests.
     */
    @PublicAPI
    @SafeVarargs
    public static <T> Stream<T> filterOut(Stream<T> stream, Predicate<T>... predicates)
    {
        return filterOut(stream, Arrays.asList(predicates));
    }

    /**
     * An override method for {@link CollectionUtil#filterOut(Stream, Predicate[])}. The given {@link Collection} will
     * be converted into a {@link Stream}. The resulting stream will only consist of elements that did <b>not</b> match
     * the given {@link Predicate}(s).
     *
     * @param collection A {@link Collection} of elements to process.
     * @param predicates A varargs list of {@link Predicate} to test.
     * @param <T>        The class type of the stream.
     * @return A stream of elements that were removed if they passed any of the predicate tests.
     * @see CollectionUtil#filterOut(Stream, Predicate[])
     */
    @PublicAPI
    @SafeVarargs
    public static <T> Stream<T> filterOut(Collection<T> collection, Predicate<T>... predicates)
    {
        return filterOut(collection.stream(), predicates);
    }

    /**
     * An intermediate stream operation that filters out elements that matches the given class type. The resulting
     * stream will only consist of elements that do <b>not</b> match the given class type.
     *
     * @param stream    A {@link Stream} to process.
     * @param classType The {@link Class} type to filter out.
     * @param <T>       The class type of the stream.
     * @return A stream of elements that were removed if they matched the given class type.
     * @see CollectionUtil#filterOutByClass(Collection, Class)
     */
    @PublicAPI
    public static <T> Stream<T> filterOutByClass(Stream<T> stream, Class<?> classType)
    {
        return filterOut(stream, obj -> ClassUtil.isInstanceOf(obj, classType));
    }

    /**
     * An override method for {@link CollectionUtil#filterOutByClass(Stream, Class)}. The given {@link Collection} will
     * be converted into a {@link Stream}. The resulting stream will only consist of elements that do <b>not</b> match
     * the given class type.
     *
     * @param collection A {@link Collection} of elements to process.
     * @param classType  The {@link Class} type to filter out.
     * @param <T>        The class type of the collection.
     * @return A stream of elements that were removed if they matched the given class type.
     * @see CollectionUtil#filterOutByClass(Stream, Class)
     */
    @PublicAPI
    public static <T> Stream<T> filterOutByClass(Collection<T> collection, Class<?> classType)
    {
        return filterOutByClass(collection.stream(), classType);
    }

    /**
     * Loop through <i>every</i> element in the given collection and test it against the given predicate. If any element
     * in the collection passed through the predicate returns {@code true}, then when all elements are processed, this
     * method will return {@code true} regardless of whether a different element later on causes the predicate to return
     * {@code false}.
     * <p>
     * This utility provides simple protection against concurrent modification exceptions by caching the call to each
     * predicate test within a supplier array list. This is <b>not</b> a performant alternative to the stream
     * interface's built-in {@code anyMatch} method.
     * <p>
     * If a stream is given in the overload method, then this will be a terminal operation for the stream.
     *
     * @param collection A collection of elements to loop through.
     * @param predicate  A function that accepts an element from the collection and returns a boolean.
     * @param <T>        The class type of the collection.
     * @return Whether any element sent to the function caused the function to return {@code true}.
     * @see CollectionUtil#test(Stream, Predicate)
     */
    @PublicAPI
    public static <T> boolean test(Collection<T> collection, Predicate<T> predicate)
    {
        HashSet<Supplier<Boolean>> suppliers = new HashSet<>();
        boolean isListened = false;

        collection.forEach(t -> suppliers.add(() -> predicate.test(t)));

        for (Supplier<Boolean> supplier : suppliers)
        {
            boolean result = supplier.get();

            if (!isListened)
                isListened = result;
        }

        return isListened;
    }

    /**
     * Overload method for {@link CollectionUtil#test(Collection, Predicate)}. This will convert a stream into a list,
     * so this will be a terminal operation for the stream.
     *
     * @param stream   A stream of elements to loop through.
     * @param function A function that accepts an element from the collection and returns a boolean.
     * @param <T>      The class type of the collection.
     * @return Whether any element sent to the function caused the function to return {@code true}.
     * @see CollectionUtil#test(Collection, Predicate)
     */
    @PublicAPI
    public static <T> boolean test(Stream<T> stream, Predicate<T> function)
    {
        return test(stream.toList(), function);
    }

    /**
     * Utility for turning multiple suppliers into one supplier.
     *
     * @param suppliers A varargs list of {@link BooleanSupplier}.
     * @return Whether <b>any</b> of the given suppliers yielded {@code true}.
     */
    @PublicAPI
    public static BooleanSupplier areAnyTrue(BooleanSupplier... suppliers)
    {
        return () -> Arrays.stream(suppliers).anyMatch(BooleanSupplier::getAsBoolean);
    }

    /**
     * Utility for turning multiple suppliers into one supplier.
     *
     * @param suppliers A varargs list of {@link BooleanSupplier}.
     * @return Whether <b>all</b> the given suppliers yielded {@code true}.
     */
    @PublicAPI
    public static BooleanSupplier areAllTrue(BooleanSupplier... suppliers)
    {
        return () -> Arrays.stream(suppliers).allMatch(BooleanSupplier::getAsBoolean);
    }

    /**
     * Create a linked hash set of the given varargs list.
     *
     * @param elements A varargs list of elements to link together in a linked hash set.
     * @param <T>      The class type of the elements stored in the linked hash set.
     * @return A new {@link LinkedHashSet} instance with an insertion of that given in the varargs.
     */
    @PublicAPI
    @SafeVarargs
    public static <T> LinkedHashSet<T> link(T... elements)
    {
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    /**
     * Check if the given {@link Stream} is empty.
     *
     * @param stream A {@link Stream} to check.
     * @return Whether the {@link Stream} is empty.
     */
    @PublicAPI
    public static boolean isEmpty(Stream<?> stream)
    {
        return stream.findAny().isEmpty();
    }

    /**
     * Checks if the given map contains entries.
     *
     * @param map A {@link Map} instance.
     * @return Whether the size of the map is greater than zero.
     */
    @PublicAPI
    public static boolean isNotEmpty(Map<?, ?> map)
    {
        return !map.isEmpty();
    }

    /**
     * Checks if the given collection contains entries.
     *
     * @param collection A {@link Collection} instance.
     * @return Whether the size of the collection is greater than zero.
     */
    @PublicAPI
    public static boolean isNotEmpty(Collection<?> collection)
    {
        return !collection.isEmpty();
    }

    /**
     * Check if the given collection is unmodifiable.
     *
     * @param collection The {@link Collection} to check.
     * @return Yields {@code true} if the given {@link Collection} throws an {@link UnsupportedOperationException},
     * {@code false} otherwise.
     */
    @PublicAPI
    public static boolean isUnmodifiable(Collection<?> collection)
    {
        try
        {
            collection.addAll(Collections.emptyList());

            return false;
        }
        catch (UnsupportedOperationException exception)
        {
            return true;
        }
    }

    /**
     * Check if the given collection is modifiable.
     *
     * @param collection The {@link Collection} to check.
     * @return Yields {@code true} if the given {@link Collection} can be modified, {@code false} otherwise.
     */
    @PublicAPI
    public static boolean isModifiable(Collection<?> collection)
    {
        return !isUnmodifiable(collection);
    }

    /**
     * Check if the given map is unmodifiable.
     *
     * @param map The {@link Map} to check.
     * @return Yields {@code true} if the given {@link Map} throws an {@link UnsupportedOperationException},
     * {@code false} otherwise.
     */
    @PublicAPI
    public static boolean isUnmodifiable(Map<?, ?> map)
    {
        try
        {
            map.putAll(Map.of());

            return false;
        }
        catch (UnsupportedOperationException exception)
        {
            return true;
        }
    }

    /**
     * Check if the given map is modifiable.
     *
     * @param map The {@link Map} to check.
     * @return Yields {@code true} if the given {@link Map} can be modified, {@code false} otherwise.
     */
    @PublicAPI
    public static boolean isModifiable(Map<?, ?> map)
    {
        return !isUnmodifiable(map);
    }
}
