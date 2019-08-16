package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.SplitableIterable;

import javax.annotation.Nonnull;

abstract class AbstractNode<T>
    implements Cursorable<T>,
               SplitableIterable<T>,
               InvariantCheckable
{
    abstract boolean isEmpty();

    abstract int size();

    abstract int depth();

    abstract T get(int index);

    @Nonnull
    abstract AbstractNode<T> append(T value);

    @Nonnull
    abstract AbstractNode<T> append(@Nonnull AbstractNode<T> node);

    @Nonnull
    abstract AbstractNode<T> prepend(T value);

    @Nonnull
    abstract AbstractNode<T> prepend(@Nonnull AbstractNode<T> node);

    @Nonnull
    abstract AbstractNode<T> assign(int index,
                                    T value);

    @Nonnull
    abstract AbstractNode<T> insert(int index,
                                    T value);

    @Nonnull
    abstract AbstractNode<T> deleteFirst();

    @Nonnull
    abstract AbstractNode<T> deleteLast();

    @Nonnull
    abstract AbstractNode<T> delete(int index);

    @Nonnull
    abstract AbstractNode<T> prefix(int limit);

    @Nonnull
    abstract AbstractNode<T> suffix(int offset);

    @Override
    public abstract void checkInvariants();

    abstract void copyTo(T[] array,
                         int offset);

    @Nonnull
    AbstractNode<T> left()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    AbstractNode<T> right()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    AbstractNode<T> rotateRight(AbstractNode<T> right)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    AbstractNode<T> rotateLeft(AbstractNode<T> left)
    {
        throw new UnsupportedOperationException();
    }
}
