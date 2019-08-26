package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

public interface Node<K, V>
    extends SplitableIterable<JImmutableMap.Entry<K, V>>,
            Cursorable<JImmutableMap.Entry<K, V>>
{
    static <K, V> Node<K, V> empty()
    {
        return FringeNode.instance();
    }

    static <K, V> Node<K, V> single(K key,
                                    V value)
    {
        return new ValueNode<>(key, value, FringeNode.instance(), FringeNode.instance());
    }

    @Nonnull
    Node<K, V> assign(@Nonnull Comparator<K> comp,
                      @Nonnull K key,
                      @Nullable V value);

    @Nonnull
    Node<K, V> delete(@Nonnull Comparator<K> comp,
                      @Nonnull K key);

    @Nonnull
    Node<K, V> update(@Nonnull Comparator<K> comp,
                      @Nonnull K key,
                      @Nonnull Func1<Holder<V>, V> generator);

    V get(@Nonnull Comparator<K> comp,
          @Nonnull K key,
          V defaultValue);

    @Nonnull
    Holder<V> find(@Nonnull Comparator<K> comp,
                   @Nonnull K key);

    @Nonnull
    Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                                @Nonnull K key);

    boolean isEmpty();

    int size();
}
