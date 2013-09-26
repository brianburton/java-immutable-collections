package org.javimmutable.collections;

/**
 * Interface for containers that associate keys with values.
 *
 * @param <K>
 * @param <V>
 */
public interface Mapped<K, V>
{
    /**
     * Return the value associated with key or null if no value is associated.
     * Note that if null is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param key identifies the value to retrieve
     * @return value assoiated with key or null if no value is associated
     */
    V get(K key);

    /**
     * Return a Holder containing the value associated wth the key or an empty
     * Holder if no value is associated with the key.
     *
     * @param key identifies the value to retrieve
     * @return possibly empty Holder containing any value associated with the key
     */
    Holder<V> find(K key);
}
