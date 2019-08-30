package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.javimmutable.collections.MapEntry.mapEntry;

public class ListCollisionMap<K, V>
    implements CollisionMap<K, V>
{
    private static final ListCollisionMap INSTANCE = new ListCollisionMap();

    private ListCollisionMap()
    {
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <K, V> ListCollisionMap<K, V> instance()
    {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private AbstractNode<Entry<K, V>> root(@Nonnull Node node)
    {
        return (AbstractNode<Entry<K, V>>)node;
    }

    @Nonnull
    @Override
    public Node emptyNode()
    {
        return EmptyNode.instance();
    }

    @Override
    public int size(@Nonnull Node node)
    {
        return root(node).size();
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nullable V value)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                if (e.getValue() == value) {
                    return root;
                } else {
                    return root.assign(i, mapEntry(key, value));
                }
            }
            i += 1;
        }
        return root.append(mapEntry(key, value));
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nonnull Func1<Holder<V>, V> generator)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                V value = generator.apply(Holders.of(e.getValue()));
                if (e.getValue() == value) {
                    return root;
                } else {
                    return root.assign(i, mapEntry(key, value));
                }
            }
            i += 1;
        }
        V value = generator.apply(Holders.of());
        return root.append(mapEntry(key, value));
    }

    @Nonnull
    @Override
    public Node delete(@Nonnull Node node,
                       @Nonnull K key)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return root.delete(i);
            }
            i += 1;
        }
        return root;
    }

    @Override
    public V getValueOr(@Nonnull Node node,
                        @Nonnull K key,
                        V defaultValue)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> findValue(@Nonnull Node node,
                               @Nonnull K key)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Holders.of(e.getValue());
            }
        }
        return Holders.of();
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull Node node,
                                         @Nonnull K key)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Holders.of(e);
            }
        }
        return Holders.of();
    }

    @Nullable
    @Override
    public GenericIterator.State<Entry<K, V>> iterateOverRange(@Nonnull Node node,
                                                               @Nullable GenericIterator.State<Entry<K, V>> parent,
                                                               int offset,
                                                               int limit)
    {
        return root(node).iterateOverRange(parent, offset, limit);
    }

    @Nonnull
    public SplitableIterator<Entry<K, V>> iterator(@Nonnull Node node)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        return new GenericIterator<>(root, 0, root.size());
    }

    @Nonnull
    public SplitableIterable<Entry<K, V>> iterable(@Nonnull Node node)
    {
        return () -> iterator(node);
    }
}
