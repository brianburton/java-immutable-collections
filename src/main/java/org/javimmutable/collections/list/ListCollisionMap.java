package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.common.MutableDelta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ListCollisionMap<K, V>
    implements CollisionMap<EntryList<K, V>, K, V>
{
    @Nonnull
    @Override
    public EntryList<K, V> update(@Nullable EntryList<K, V> leaf,
                                  @Nonnull K key,
                                  @Nullable V value,
                                  @Nonnull MutableDelta delta)
    {
        final EntryList<K, V> oldList = EntryList.instance(leaf);
        final EntryList<K, V> newList = oldList.assign(key, value);
        delta.add(newList.size() - oldList.size());
        return newList;
    }

    @Nonnull
    @Override
    public EntryList<K, V> update(@Nullable EntryList<K, V> leaf,
                                  @Nonnull K key,
                                  @Nonnull Func1<Holder<V>, V> generator,
                                  @Nonnull MutableDelta delta)
    {
        final EntryList<K, V> oldList = EntryList.instance(leaf);
        final EntryList<K, V> newList = oldList.update(key, generator);
        delta.add(newList.size() - oldList.size());
        return newList;
    }

    @Nullable
    @Override
    public EntryList<K, V> delete(@Nonnull EntryList<K, V> leaf,
                                  @Nonnull K key,
                                  @Nonnull MutableDelta delta)
    {
        final EntryList<K, V> oldList = EntryList.instance(leaf);
        final EntryList<K, V> newList = oldList.delete(key);
        delta.add(newList.size() - oldList.size());
        return newList.nullify();
    }

    @Override
    public V getValueOr(@Nonnull EntryList<K, V> leaf,
                        @Nonnull K key,
                        V defaultValue)
    {
        return EntryList.instance(leaf).getValueOr(key, defaultValue);
    }

    @Override
    public Holder<V> findValue(@Nonnull EntryList<K, V> leaf,
                               @Nonnull K key)
    {
        return EntryList.instance(leaf).findValue(key);
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull EntryList<K, V> leaf,
                                                       @Nonnull K key)
    {
        return EntryList.instance(leaf).findEntry(key);
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(@Nonnull EntryList<K, V> leaf)
    {
        return EntryList.instance(leaf).iterator();
    }
}
