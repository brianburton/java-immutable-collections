package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArraySingleValueSetNode<T>
    implements ArraySetNode<T>
{
    private final T value;

    public ArraySingleValueSetNode(T value)
    {
        this.value = value;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return 1;
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            @Nonnull T value)
    {
        return this.value.equals(value);
    }

    @Nonnull
    @Override
    public ArraySetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                                  @Nonnull T value)
    {
        final T thisValue = this.value;
        if (thisValue.equals(value)) {
            return this;
        } else {
            return new ArrayMultiValueSetNode<>(collisionSet.insert(collisionSet.single(thisValue), value));
        }
    }

    @Nullable
    @Override
    public ArraySetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                                  @Nonnull T value)
    {
        return value.equals(this.value) ? null : this;
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<T> values(@Nonnull CollisionSet<T> collisionSet)
    {
        return GenericIterator.valueIterable(value);
    }
}
