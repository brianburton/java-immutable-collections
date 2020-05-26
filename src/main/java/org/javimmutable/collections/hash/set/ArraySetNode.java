package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ArraySetNode<T>
{
    int size(@Nonnull CollisionSet<T> collisionSet);

    boolean contains(@Nonnull CollisionSet<T> collisionSet,
                     @Nonnull T value);

    @Nonnull
    ArraySetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                           @Nonnull T value);

    @Nullable
    ArraySetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                           @Nonnull T value);

    @Nonnull
    GenericIterator.Iterable<T> values(@Nonnull CollisionSet<T> collisionSet);

    void forEach(@Nonnull CollisionSet<T> collisionSet,
                 @Nonnull Proc1<T> proc);

    <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                             @Nonnull Proc1Throws<T, E> proc)
        throws E;
}
