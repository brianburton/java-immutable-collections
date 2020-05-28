package org.javimmutable.collections.array;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;

import javax.annotation.Nonnull;

public interface ArrayUpdateMapper<K, V, T>
    extends ArrayAssignMapper<K, V, T>
{
    @Nonnull
    T mappedUpdate(@Nonnull T current,
                   @Nonnull K key,
                   @Nonnull Func1<Holder<V>, V> generator);
}
