package org.javimmutable.collections.listmap;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.tree.JImmutableTreeMap;

import java.util.Comparator;

/**
 * JImmutableListMap implementation that allows keys to be traversed in sorted order using a Comparator
 * of the natural ordering of the keys if they implement Comparable.
 *
 * @param <K>
 * @param <V>
 */
public class JImmutableTreeListMap<K, V>
        extends AbstractJImmutableListMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeListMap EMPTY = new JImmutableTreeListMap(JImmutableTreeMap.of());

    private JImmutableTreeListMap(JImmutableMap<K, JImmutableList<V>> contents)
    {
        super(contents);
    }

    /**
     * Constructs an empty list map whose keys are sorted in their natural ordering.  The keys
     * must implement Comparable.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> JImmutableTreeListMap<K, V> of()
    {
        return (JImmutableTreeListMap<K, V>)EMPTY;
    }

    /**
     * Constructs an empty list map using the specified Comparator.  Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     *
     * @param comparator
     */
    public static <K, V> JImmutableTreeListMap<K, V> of(Comparator<K> comparator)
    {
        return new JImmutableTreeListMap<K, V>(JImmutableTreeMap.<K, JImmutableList<V>>of(comparator));
    }

    @Override
    protected JImmutableListMap<K, V> create(JImmutableMap<K, JImmutableList<V>> map)
    {
        return new JImmutableTreeListMap<K, V>(map);
    }
}
