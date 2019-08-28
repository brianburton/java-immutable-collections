package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EntryList<K, V>
    implements SplitableIterable<Entry<K, V>>
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
    static <K, V> EntryList<K, V> instance(@Nullable EntryList<K, V> e)
    {
        if (e == null) {
            return empty();
        } else {
            return e;
        }
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

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return root.iterator();
    }
}
