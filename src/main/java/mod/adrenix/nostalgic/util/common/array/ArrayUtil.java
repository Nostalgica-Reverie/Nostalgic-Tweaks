package mod.adrenix.nostalgic.util.common.array;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public abstract class ArrayUtil {
    /**
     * Copies values {@code from} one array {@code to} another array.
     *
     * @param from Array to copy values from.
     * @param to   Array to paste values to.
     */
    @PublicAPI
    public static void copy(float[] from, float[] to) {
        if (from.length != to.length)
            throw new AssertionError("Both arrays must be the same size to copy");

        System.arraycopy(from, 0, to, 0, from.length);
    }

    /**
     * Safely get an element from an array without incurring an out-of-bounds exception.
     *
     * @param array The array to get an element from.
     * @param index The index to use to get an element.
     * @param <T>   The class type of the array.
     * @return An element at the given index within the array, or {@code null} if the index was out-of-bounds.
     */
    @Nullable
    public static <T> T get(T[] array, int index) {
        int bound = array.length - 1;

        if (index <= bound && index >= 0)
            return array[index];

        return null;
    }

    /**
     * Safely get an optional element from an array list without incurring an out-of-bounds exception.
     *
     * @param list  The {@link ArrayList} to get the element from.
     * @param index The index to use to get an element.
     * @param <T>   The class type of list elements.
     * @return An {@link Optional} instance that will contain an element from the given index if it existed.
     */
    @PublicAPI
    public static <T> Optional<T> get(ArrayList<T> list, int index) {
        if (index < 0 || index >= list.size())
            return Optional.empty();

        return Optional.ofNullable(list.get(index));
    }

    /**
     * Safely get the first element within an array list if it exists. This utility will avoid out-of-bounds
     * exceptions.
     *
     * @param list The {@link ArrayList} to get the first element from.
     * @param <T>  The class type of list elements.
     * @return An {@link Optional} instance that will contain the first element if it existed.
     */
    @PublicAPI
    public static <T> Optional<T> first(ArrayList<T> list) {
        return get(list, 0);
    }

    /**
     * Safely get the last element within an array list if it exists. This utility will avoid out-of-bounds exceptions.
     *
     * @param list The {@link ArrayList} to get the last element from.
     * @param <T>  The class type of list elements.
     * @return An {@link Optional} instance that will contain the last element if it existed.
     */
    @PublicAPI
    public static <T> Optional<T> last(ArrayList<T> list) {
        return get(list, list.size() - 1);
    }

    /**
     * Pushes the given element into the array and removes any extra elements if the list is bigger than the given max
     * size.
     *
     * @param list    An {@link ArrayList} instance.
     * @param push    The element to push into the front of the array.
     * @param maxSize The maximum size allowed for the array list.
     * @param <T>     The class type of list elements.
     */
    @PublicAPI
    public static <T> void popPush(ArrayList<T> list, T push, int maxSize) {
        list.addFirst(push);

        if (list.size() > maxSize) {
            while (list.size() > maxSize)
                list.removeLast();
        }
    }
}
