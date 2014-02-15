package org.javimmutable.collections.listmap;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMap;

/**
 * JImmutableListMap implementation that allows keys to be traversed in the same order as they
 * were inserted into the collection.
 *
 * @param <K>
 * @param <V>
 */
public class JImmutableInsertOrderListMap<K, V>
        extends AbstractJImmutableListMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableInsertOrderListMap EMPTY = new JImmutableInsertOrderListMap(JImmutableInsertOrderMap.of());

    private JImmutableInsertOrderListMap(JImmutableMap<K, JImmutableList<V>> contents)
    {
        super(contents);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> JImmutableInsertOrderListMap<K, V> of()
    {
        return (JImmutableInsertOrderListMap<K, V>)EMPTY;
    }

    @Override
    protected JImmutableListMap<K, V> create(JImmutableMap<K, JImmutableList<V>> map)
    {
        return new JImmutableInsertOrderListMap<K, V>(map);
    }
}
