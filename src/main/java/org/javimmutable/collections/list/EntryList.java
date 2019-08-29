package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.StringJoiner;

class EntryList<K, V>
    implements GenericIterator.Iterable<Entry<K, V>>
{
    private static final EntryList<Object, Object> EMPTY = new EntryList<>(EmptyNode.instance());

    private final AbstractNode<Entry<K, V>> root;

    private EntryList(@Nonnull AbstractNode<Entry<K, V>> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <K, V> EntryList<K, V> empty()
    {
        return (EntryList<K, V>)EMPTY;
    }

    @Nonnull
    static <K, V> EntryList<K, V> instance(@Nonnull AbstractNode<Entry<K, V>> root)
    {
        return new EntryList<>(root);
    }

    int size()
    {
        return root.size();
    }

    @Nonnull
    EntryList<K, V> assign(K key,
                           V value)
    {
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                if (e.getValue() == value) {
                    return this;
                } else {
                    return new EntryList<>(root.assign(i, MapEntry.of(key, value)));
                }
            }
            i += 1;
        }
        return new EntryList<>(root.append(MapEntry.of(key, value)));
    }

    @Nonnull
    EntryList<K, V> update(K key,
                           @Nonnull Func1<Holder<V>, V> generator)
    {
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                V value = generator.apply(Holders.of(e.getValue()));
                if (e.getValue() == value) {
                    return this;
                } else {
                    return new EntryList<>(root.assign(i, MapEntry.of(key, value)));
                }
            }
            i += 1;
        }
        V value = generator.apply(Holders.of());
        return new EntryList<>(root.append(MapEntry.of(key, value)));
    }

    @Nonnull
    EntryList<K, V> delete(K key)
    {
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                AbstractNode<Entry<K, V>> newRoot = root.delete(i);
                if (newRoot.isEmpty()) {
                    return empty();
                } else {
                    return new EntryList<>(newRoot);
                }
            }
            i += 1;
        }
        return this;
    }

    V getValueOr(K key,
                 V defaultValue)
    {
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return defaultValue;
    }

    @Nonnull
    Holder<V> findValue(K key)
    {
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Holders.of(e.getValue());
            }
        }
        return Holders.of();
    }

    @Nonnull
    Holder<Entry<K, V>> findEntry(K key)
    {
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Holders.of(e);
            }
        }
        return Holders.of();
    }

    @Nullable
    @Override
    public GenericIterator.State<Entry<K, V>> iterateOverRange(@Nullable GenericIterator.State<Entry<K, V>> parent,
                                                               int offset,
                                                               int limit)
    {
        return root.iterateOverRange(parent, offset, limit);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EntryList<?, ?> entryList = (EntryList<?, ?>)o;

        return root.equals(entryList.root);
    }

    @Override
    public int hashCode()
    {
        return root.hashCode();
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", EntryList.class.getSimpleName() + "[", "]")
            .add("root=" + root)
            .toString();
    }
}
