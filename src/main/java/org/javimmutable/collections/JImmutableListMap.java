package org.javimmutable.collections;

public interface JImmutableListMap<K, V>
        extends Insertable<JImmutableMap.Entry<K, V>>,
                Mapped<K, JImmutableList<V>>,
                Iterable<JImmutableMap.Entry<K, JImmutableList<V>>>,
                Cursorable<JImmutableMap.Entry<K, JImmutableList<V>>>
{
    /**
     * Return the list associated with key or an empty list if no list is associated.
     *
     * @param key identifies the value to retrieve
     * @return list associated with key or an empty list if no value is associated
     */
    JImmutableList<V> getList(K key);

    /**
     * Sets the list associated with a specific key.  Key and value must be non-null.
     * If the key already has a list in the map the old list is discarded
     * and the new list is stored in its place.  Returns a new JImmutableListMap reflecting
     * any changes.  The original map is always left unchanged.
     *
     * @param key   non-null key
     * @param value possibly null value
     * @return new map reflecting the change
     */
    JImmutableListMap<K, V> assign(K key,
                                   JImmutableList<V> value);

    /**
     * Add value to the list for the specified key.  Note that this can create duplicate values
     * in the list.
     *
     * @param value
     * @param value
     * @return
     */
    JImmutableListMap<K, V> insert(K key,
                                   V value);

    /**
     * Deletes the entry for the specified key (if any).  Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     *
     * @param key non-null key
     * @return same or different map depending on whether key was removed
     */
    JImmutableListMap<K, V> delete(K key);

    /**
     * Return the number of keys in the map.
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
    JImmutableListMap<K, V> deleteAll();

    /**
     * Creates a Cursor to access all of the Map's keys.
     *
     * @return
     */
    Cursor<K> keysCursor();

    /**
     * Creates a Cursor to access all of the specified key's list.
     * If no list exists for key an empty cursor is returned.
     *
     * @return a (possibly empty) cursor for traversing the values associated with key
     */
    Cursor<V> valuesCursor(K key);
}
