package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ArraySetNode<T>
{
    int size(@Nonnull CollisionSet<T> collisionMap);

    boolean contains(@Nonnull CollisionSet<T> collisionMap,
                     @Nonnull T value);

    @Nonnull
    ArraySetNode<T> insert(@Nonnull CollisionSet<T> collisionMap,
                           @Nonnull T value);

    @Nullable
    ArraySetNode<T> delete(@Nonnull CollisionSet<T> collisionMap,
                           @Nonnull T value);

    @Nonnull
    GenericIterator.Iterable<T> values(@Nonnull CollisionSet<T> collisionMap);
}
