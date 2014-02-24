package org.javimmutable.collections;

import java.util.Map;

/**
 * Similar to JImmutableMap but uses native ints in place of Integers as keys.
 */
public interface JImmutableArray<T>
        extends Indexed<T>,
                Insertable<JImmutableMap.Entry<Integer, T>>,
                Iterable<JImmutableMap.Entry<Integer, T>>,
                Cursorable<JImmutableMap.Entry<Integer, T>>

{
    /**
     * Return the value associated with key or null if no value is associated.
     * Note that if null is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param key identifies the value to retrieve
     * @return value associated with key or null if no value is associated
     */
    T get(int key);

    /**
     * Return a Holder containing the value associated wth the key or an empty
     * Holder if no value is associated with the key.
     *
     * @param key identifies the value to retrieve
     * @return possibly empty Holder containing any value associated with the key
     */
    Holder<T> find(int key);

    /**
     * Search for an Entry within the map and return a Holder indicating if the Entry
     * was found and, if it was found, the Entry itself.
     *
     * @param key non-null key to search for
     * @return empty Holder if not found, otherwise filled Holder with Entry
     */
    Holder<JImmutableMap.Entry<Integer, T>> findEntry(int key);

    /**
     * Sets the value associated with a specific key.  Key must be non-null but value
     * can be null.  If the key already has a value in the map the old value is discarded
     * and the new value is stored in its place.  Returns a new PersistentMap reflecting
     * any changes.  The original map is always left unchanged.
     *
     * @param key   non-null key
     * @param value possibly null value
     * @return new map reflecting the change
     */
    JImmutableArray<T> assign(int key,
                              T value);

    /**
     * Deletes the entry for the specified key (if any).  Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     *
     * @param key non-null key
     * @return same or different map depending on whether key was removed
     */
    JImmutableArray<T> delete(int key);

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
