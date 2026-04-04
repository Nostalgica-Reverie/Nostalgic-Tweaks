package mod.adrenix.nostalgic.util.common.array;

import mod.adrenix.nostalgic.util.common.annotation.PublicAPI;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This is an extension of {@link ArrayList} with the purpose of preventing duplicate elements from entering into the
 * list. Extra utility methods are also included. This is an alternative to using a set to store unique elements.
 *
 * @param <E> The type of element stored in the {@link ArrayList}.
 */
public class UniqueArrayList<E> extends ArrayList<E> {
    /* Constructors */

    /**
     * Constructs an empty {@link UniqueArrayList} with an initial capacity of {@code ten}.
     */
    public UniqueArrayList() {
        super();
    }

    /**
     * Constructs a new {@link UniqueArrayList} containing the elements of the specified collection; in the order they
     * are returned by the collection's iterator.
     *
     * @param collection The {@link Collection} whose elements are to be placed into this {@link UniqueArrayList}.
     * @throws NullPointerException If the specified collection is {@code null}.
     */
    public UniqueArrayList(Collection<? extends E> collection) {
        super(collection);
    }

    /**
     * Constructs an empty {@link UniqueArrayList} with the specified initial capacity.
     *
     * @param initialCapacity The initial capacity of the {@link UniqueArrayList}.
     * @throws IllegalArgumentException If the specified initial capacity is negative.
     */
    public UniqueArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    /* Overrides */

    /**
     * Index is clamped if it is out-of-bounds. {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        this.remove(element);

        return super.set(Mth.clamp(index, 0, this.size() - 1), element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(E element) {
        this.remove(element);

        return super.add(element);
    }

    /**
     * Index is clamped if it is out-of-bounds. {@inheritDoc}
     */
    @Override
    public void add(int index, E element) {
        this.remove(element);

        super.add(Mth.clamp(index, 0, this.size()), element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        this.removeAll(collection);

        return super.addAll(collection);
    }

    /**
     * Index is clamped if it is out-of-bounds. {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        this.removeAll(collection);

        return super.addAll(Mth.clamp(index, 0, this.size()), collection);
    }

    /* Utility */

    /**
     * Move the specified element to the front of the array list.
     *
     * @param element The element to move.
     */
    @PublicAPI
    public void moveToFront(E element) {
        this.remove(element);
        this.add(0, element);
    }

    /**
     * Move the specified element to the end of the array list.
     *
     * @param element The element to move.
     */
    @PublicAPI
    public void moveToEnd(E element) {
        this.remove(element);
        this.add(element);
    }

    /**
     * Move one element after a different element. If the {@code after} element is not in this array list, then nothing
     * will happen. If the {@code move} element is not in this array list, then it will be added to the array list after
     * the {@code after} element.
     *
     * @param move  The element to move.
     * @param after The element to move after.
     */
    @PublicAPI
    public void moveAfter(E move, E after) {
        int index = this.indexOf(after);

        if (index == -1)
            return;

        if (super.remove(move))
            super.add(index, move);
        else
            super.add(index + 1, move);
    }

    /**
     * Get the first element in the array list.
     *
     * @return The first element or {@code null} if the array list is empty.
     */
    @PublicAPI
    public @Nullable E getFirst() {
        if (this.isEmpty())
            return null;

        return this.get(0);
    }

    /**
     * Get the last element in the array list.
     *
     * @return The last element or {@code null} if the array list is empty.
     */
    @PublicAPI
    public @Nullable E getLast() {
        if (this.isEmpty())
            return null;

        return this.get(this.size() - 1);
    }

    /**
     * If a specific object is found using the given index, then that object will be given to the consumer. If the index
     * is out-of-bounds, then nothing will be given to the consumer.
     *
     * @param index    An index to retrieve an element from the unique array list.
     * @param consumer A {@link Consumer} that accepts a {@code non-null} element.
     */
    @PublicAPI
    public void ifPresent(int index, Consumer<? super E> consumer) {
        if (index < 0 || index >= this.size())
            return;

        E applyTo = this.get(index);

        if (applyTo != null)
            consumer.accept(applyTo);
    }

    /**
     * Map an element using the given mapper function. If the index is out-of-bounds, then nothing will be applied to
     * the mapper and {@code null} is returned. If an element is {@code null} at the calculated index, then {@code null}
     * will be returned.
     *
     * @param index An index to retrieve an element from the unique array list.
     * @param map   A {@link Function} that accepts a {@code non-null} element and returns {@link R}.
     * @param <R>   The class type of the return from the mapping function.
     * @return An element from this unique array list mapped to {@link R}.
     */
    @PublicAPI
    public <R> @Nullable R map(int index, Function<? super E, ? extends R> map) {
        if (index < 0 || index >= this.size())
            return null;

        E element = this.get(index);

        if (element != null)
            return map.apply(element);

        return null;
    }
}
