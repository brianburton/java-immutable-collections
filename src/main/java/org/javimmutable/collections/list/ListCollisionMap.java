package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ListCollisionMap<K, V>
    implements CollisionMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final ListCollisionMap EMPTY = new ListCollisionMap(EntryList.empty());

    private final EntryList<K, V> root;

    private ListCollisionMap(@Nonnull EntryList<K, V> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <K, V> ListCollisionMap<K, V> empty()
    {
        return (ListCollisionMap<K, V>)EMPTY;
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Nonnull
    @Override
    public ListCollisionMap<K, V> update(@Nonnull K key,
                                         @Nullable V value)
    {
        return assignForUpdate(root.assign(key, value));
    }

    @Nonnull
    @Override
    public ListCollisionMap<K, V> update(@Nonnull K key,
                                         @Nonnull Func1<Holder<V>, V> generator)
    {
        return assignForUpdate(root.update(key, generator));
    }

    @Nonnull
    @Override
    public ListCollisionMap<K, V> delete(@Nonnull K key)
    {
        return assignForDelete(root.delete(key));
    }

    @Override
    public V getValueOr(@Nonnull K key,
                        V defaultValue)
    {
        return root.getValueOr(key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> findValue(@Nonnull K key)
    {
        return root.findValue(key);
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull K key)
    {
        return root.findEntry(key);
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return root.iterator();
    }

    @Nonnull
    private ListCollisionMap<K, V> assignForUpdate(@Nonnull EntryList<K, V> newRoot)
    {
        if (newRoot == root) {
            return this;
        } else {
            return new ListCollisionMap<>(newRoot);
        }
    }

    @Nonnull
    private ListCollisionMap<K, V> assignForDelete(@Nonnull EntryList<K, V> newRoot)
    {
        if (newRoot == root) {
            return this;
        } else if (newRoot.size() == 0) {
            return empty();
        } else {
            return new ListCollisionMap<>(newRoot);
        }
    }
}
