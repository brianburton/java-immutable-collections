package org.javimmutable.collections;

import java.util.Map;

/**
 * A sparse array implementation using integers as keys.  Keys are traversed in signed integer
 * order by Cursors so negative values are visited before positive values.  Implementations
 * are allowed to restrict the range of allowable indexes for performance or other reasons.
 * <p/>
 * Arrays are sparse meaning that they can contain elements at any valid index with no need
 * to keep them consecutive (like a List).  Memory is managed to use no more than necessary
 * for the number of elements currently in the array.
 */
public interface JImmutableArray<T>
        extends Indexed<T>,
                Insertable<JImmutableMap.Entry<Integer, T>>,
                Iterable<JImmutableMap.Entry<Integer, T>>,
                Cursorable<JImmutableMap.Entry<Integer, T>>
{
    /**
     * Return the value associated with index or null if no value is associated.
     * Note that if null is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param index identifies the value to retrieve
     * @return value associated with index or null if no value is associated
     */
    T get(int index);

    /**
     * Return a Holder containing the value associated wth the index or an empty
     * Holder if no value is associated with the index.
     *
     * @param index identifies the value to retrieve
     * @return possibly empty Holder containing any value associated with the index
     */
    Holder<T> find(int index);

    /**
     * Search for an Entry within the map and return a Holder indicating if the Entry
     * was found and, if it was found, the Entry itself.
     *
     * @param index index to search for
     * @return empty Holder if not found, otherwise filled Holder with Entry
     */
    Holder<JImmutableMap.Entry<Integer, T>> findEntry(int index);

    /**
     * Sets the value associated with a specific index.  Index must be non-null but value
     * can be null.  If the index already has a value in the map the old value is discarded
     * and the new value is stored in its place.  Returns a new PersistentMap reflecting
     * any changes.  The original map is always left unchanged.
     *
     * @param index index
     * @param value possibly null value
     * @return new map reflecting the change
     */
    JImmutableArray<T> assign(int index,
                              T value);

    /**
     * Deletes the entry for the specified index (if any).  Returns a new map if the value
     * was deleted or the current map if the index was not contained in the map.
     *
     * @param index index
     * @return same or different map depending on whether index was removed
     */
    JImmutableArray<T> delete(int index);

    /**
     * Return the number of entries in the map.
     *
     * @return
     */
    int size();

    /**
     * @return true only if list contains no values
     */
    boolean isEmpty();

    /**
     * @return an equivalent collection with no values
     */
    JImmutableArray<T> deleteAll();

    /**
     * Creates an unmodifiable java.util.Map reflecting the values of this PersistentMap.
     *
     * @return Map view of this PersistentMap
     */
    Map<Integer, T> getMap();

    /**
     * Creates a Cursor to access all of the Map's keys.
     *
     * @return
     */
    Cursor<Integer> keysCursor();

    /**
     * Creates a Cursor to access all of the Map's values.
     *
     * @return
     */
    Cursor<T> valuesCursor();
}
