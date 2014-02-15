package org.javimmutable.collections.listmap;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.Iterator;

public abstract class AbstractJImmutableListMap<K, V>
        implements JImmutableListMap<K, V>
{
    private final JImmutableMap<K, JImmutableList<V>> contents;

    protected AbstractJImmutableListMap(JImmutableMap<K, JImmutableList<V>> contents)
    {
        this.contents = contents;
    }

    @Override
    public JImmutableList<V> getList(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        Holder<JImmutableList<V>> current = contents.find(key);
        return current.isFilled() ? current.getValue() : emptyList();
    }

    @Override
    public JImmutableListMap<K, V> assign(K key,
                                          JImmutableList<V> value)
    {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return create(contents.assign(key, copyList(value)));
    }

    @Override
    public JImmutableListMap<K, V> insert(K key,
                                          V value)
    {
        return create(contents.assign(key, insertInList(getList(key), value)));
    }

    @Override
    public JImmutableListMap<K, V> delete(K key)
    {
        return create(contents.delete(key));
    }

    @Override
    public int size()
    {
        return contents.size();
    }

    @Override
    public boolean isEmpty()
    {
        return contents.size() == 0;
    }

    @Override
    public Cursor<K> keysCursor()
    {
        return contents.keysCursor();
    }

    @Override
    public Cursor<V> valuesCursor(K key)
    {
        return getList(key).cursor();
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, JImmutableList<V>>> cursor()
    {
        return contents.cursor();
    }

    @Override
    public Insertable<JImmutableMap.Entry<K, V>> insert(JImmutableMap.Entry<K, V> e)
    {
        return insert(e.getKey(), e.getValue());
    }

    @Override
    public Iterator<JImmutableMap.Entry<K, JImmutableList<V>>> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public JImmutableList<V> get(K key)
    {
        return contents.get(key);
    }

    @Override
    public Holder<JImmutableList<V>> find(K key)
    {
        return contents.find(key);
    }

    @Override
    public JImmutableListMap<K, V> deleteAll()
    {
        return create(contents.deleteAll());
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map
     * @return
     */
    protected abstract JImmutableListMap<K, V> create(JImmutableMap<K, JImmutableList<V>> map);

    /**
     * Overridable by derived classes to create a compatible copy of the specified list.
     * Default implementation simply returns the original.
     *
     * @return
     */
    protected JImmutableList<V> copyList(JImmutableList<V> original)
    {
        return original;
    }

    /**
     * Overridable by derived classes to create a new empty list
     *
     * @return
     */
    protected JImmutableList<V> emptyList()
    {
        return JImmutableArrayList.of();
    }

    /**
     * Overridable by derived classes to insert a value into a list in some way.
     * Default implementation appends to end of the list.
     *
     * @return
     */
    protected JImmutableList<V> insertInList(JImmutableList<V> list,
                                             V value)
    {
        return list.insertLast(value);
    }
}
