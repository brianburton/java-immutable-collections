package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.TransformCursor;
import org.javimmutable.collections.hash.ArrayToMapAdaptor;

import java.util.Iterator;
import java.util.Map;

public abstract class AbstractJImmutableArray<T>
        implements JImmutableArray<T>
{
    @Override
    public T get(int index)
    {
        return find(index).getValueOrNull();
    }

    @Override
    public Holder<JImmutableMap.Entry<Integer, T>> findEntry(int key)
    {
        Holder<T> value = find(key);
        return value.isFilled() ? Holders.<JImmutableMap.Entry<Integer, T>>of(MapEntry.of(key, value.getValue())) : Holders.<JImmutableMap.Entry<Integer, T>>of();
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    /**
     * Adds the key/value pair to this map.  Any value already existing for the specified key
     * is replaced with the new value.
     *
     * @param e
     * @return
     */
    @Override
    public Insertable<JImmutableMap.Entry<Integer, T>> insert(JImmutableMap.Entry<Integer, T> e)
    {
        return assign(e.getKey(), e.getValue());
    }

    @Override
    public Cursor<Integer> keysCursor()
    {
        return TransformCursor.ofKeys(cursor());
    }

    @Override
    public Cursor<T> valuesCursor()
    {
        return TransformCursor.ofValues(cursor());
    }

    @Override
    public Iterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public Map<Integer, T> getMap()
    {
        return ArrayToMapAdaptor.of(this);
    }
}
