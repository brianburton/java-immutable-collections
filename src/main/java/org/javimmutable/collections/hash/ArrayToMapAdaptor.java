package org.javimmutable.collections.hash;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.cursors.TransformCursor;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "NullableProblems"})
public class ArrayToMapAdaptor<T>
        extends AbstractMap<Integer, T>
{
    private final JImmutableArray<T> map;

    public ArrayToMapAdaptor(JImmutableArray<T> map)
    {
        this.map = map;
    }

    public static <V> ArrayToMapAdaptor<V> of(JImmutableArray<V> map)
    {
        return new ArrayToMapAdaptor<V>(map);
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.size() == 0;
    }

    @Override
    public boolean containsKey(Object o)
    {
        return map.find((Integer)o).isFilled();
    }

    /**
     * Uses O(n) traversal of the PersistentMap to search for a matching value.
     *
     * @param o
     * @return
     */
    @Override
    public boolean containsValue(Object o)
    {
        for (JImmutableMap.Entry<Integer, T> entry : map) {
            T value = entry.getValue();
            if (o == null) {
                if (value == null) {
                    return true;
                }
            } else {
                if (value != null && value.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public T get(Object o)
    {
        return map.find((Integer)o).getValueOrNull();
    }

    @Override
    public T put(Integer k,
                 T t)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends T> map)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> keySet()
    {
        return new AbstractSet<Integer>()
        {
            @Override
            public boolean isEmpty()
            {
                return map.size() == 0;
            }

            @Override
            public boolean contains(Object o)
            {
                return map.find((Integer)o).isFilled();
            }

            @Override
            public Iterator<Integer> iterator()
            {
                return IteratorAdaptor.of(TransformCursor.ofKeys(map.cursor()));
            }

            @Override
            public int size()
            {
                return map.size();
            }
        };
    }

    @Override
    public Collection<T> values()
    {
        return new AbstractCollection<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return IteratorAdaptor.of(TransformCursor.ofValues(map.cursor()));
            }

            @Override
            public int size()
            {
                return map.size();
            }
        };
    }

    @Override
    public Set<Entry<Integer, T>> entrySet()
    {
        return new AbstractSet<Entry<Integer, T>>()
        {
            @Override
            public boolean isEmpty()
            {
                return map.size() == 0;
            }

            @Override
            public boolean contains(Object o)
            {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry<Integer, T> oEntry = (Entry<Integer, T>)o;
                Holder<JImmutableMap.Entry<Integer, T>> eHolder = map.findEntry(oEntry.getKey());
                return eHolder.isFilled() && new MapEntry(eHolder.getValue()).equals(oEntry);
            }

            @Override
            public Iterator<Entry<Integer, T>> iterator()
            {
                return IteratorAdaptor.of(TransformCursor.of(map.cursor(), new Func1<JImmutableMap.Entry<Integer, T>, Entry<Integer, T>>()
                {
                    @Override
                    public Entry<Integer, T> apply(JImmutableMap.Entry<Integer, T> value)
                    {
                        return new MapEntry(value);
                    }
                }));
            }

            @Override
            public int size()
            {
                return map.size();
            }
        };
    }
}
