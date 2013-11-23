package org.javimmutable.collections.util;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableMap;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * When converting legacy code to use JImmutableMap there are sometimes objects that shared the mutable
 * version of their internal Map field with other classes.  This class serves as a an adaptor to allow
 * this sharing to continue for a time after converting to JImmutableMap.  However the legacy code should
 * still be changed over time to eliminate the sharing of a mutable Map.
 * <p/>
 * Since a JImmutableMap has to be replaced to reflect any changes to the map it can be difficult to
 * adapt one of them into a mutable Map.  Concrete classes derived from this class only have to implement
 * two methods, accessMap() and replaceMap() in order to implement the adaptor.
 * <p/>
 * NOTE: this adaptor is NOT synchronized.
 */
public abstract class MutableMapAdaptor<K, V>
        extends AbstractMap<K, V>
{
    private final Set<Map.Entry<K, V>> entries;

    protected MutableMapAdaptor()
    {
        this.entries = new MutableEntrySet();
    }

    /**
     * Implemented by derived classes to allow this adaptor to access their JImmutableMap instance.
     *
     * @return
     */
    protected abstract JImmutableMap<K, V> accessMap();

    /**
     * Implemented by derived classes to allow this adaptor to replace their JImmutableMap instance.
     *
     * @return
     */
    protected abstract void replaceMap(JImmutableMap<K, V> newMap);

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return this.entries;
    }

    @Override
    public V put(K key,
                 V value)
    {
        V oldValue = accessMap().get(key);
        replaceMap(accessMap().assign(key, value));
        return oldValue;
    }

    private class MutableEntrySet
            extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Entry<K, V>> iterator()
        {
            return new MutableEntryIterator();
        }

        @Override
        public int size()
        {
            return accessMap().size();
        }

        @Override
        public boolean add(Entry<K, V> newEntry)
        {
            replaceMap(accessMap().assign(newEntry.getKey(), newEntry.getValue()));
            return true;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean remove(Object o)
        {
            final Map.Entry<K, V> entry = (Entry<K, V>)o;
            final K key = entry.getKey();
            if (accessMap().findEntry(key).isFilled()) {
                replaceMap(accessMap().delete(key));
                return true;
            } else {
                return false;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean contains(Object o)
        {
            final Map.Entry<K, V> entry = (Entry<K, V>)o;
            final K key = entry.getKey();
            return (accessMap().find(key).isFilled());
        }

        @Override
        public void clear()
        {
            replaceMap(accessMap().deleteAll());
        }
    }

    private class MutableEntryIterator
            implements Iterator<Map.Entry<K, V>>
    {
        private JImmutableMap<K, V> startingMap;
        private Cursor<JImmutableMap.Entry<K, V>> current;
        private Cursor<JImmutableMap.Entry<K, V>> next;

        private MutableEntryIterator()
        {
            startingMap = accessMap();
            current = startingMap.cursor();
            next = current.next();
        }

        @Override
        public boolean hasNext()
        {
            if (accessMap() != startingMap) {
                throw new ConcurrentModificationException();
            }
            return next.hasValue();
        }

        @Override
        public Entry<K, V> next()
        {
            if (accessMap() != startingMap) {
                throw new ConcurrentModificationException();
            }
            current = next;
            next = next.next();
            return new MutableMapEntry(current.getValue());
        }

        @Override
        public void remove()
        {
            if (accessMap() != startingMap) {
                throw new ConcurrentModificationException();
            }
            final K key = current.getValue().getKey();
            replaceMap(accessMap().delete(key));
            startingMap = accessMap();
        }

        private class MutableMapEntry
                extends AbstractMap.SimpleEntry<K, V>
        {
            private MutableMapEntry(JImmutableMap.Entry<K, V> entry)
            {
                super(entry.getKey(), entry.getValue());
            }

            @Override
            public V setValue(V newValue)
            {
                if (accessMap() != startingMap) {
                    throw new ConcurrentModificationException();
                }
                replaceMap(accessMap().assign(getKey(), newValue));
                V answer = super.setValue(newValue);
                startingMap = accessMap();
                return answer;
            }
        }
    }
}
