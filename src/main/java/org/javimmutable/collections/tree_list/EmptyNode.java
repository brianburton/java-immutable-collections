package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyNode<T>
    extends AbstractNode<T>
{
    private static EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode()
    {
    }

    @SuppressWarnings("unchecked")
    static <T> EmptyNode<T> instance()
    {
        return INSTANCE;
    }

    @Override
    boolean isEmpty()
    {
        return true;
    }

    @Override
    int size()
    {
        return 0;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Override
    T first()
    {
        throw new IndexOutOfBoundsException();
    }

    @Override
    T last()
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        return new LeafNode<>(value);
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        return node;
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        return new LeafNode<>(value);
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        return node;
    }

    @Nonnull
    @Override
    AbstractNode<T> set(int index,
                        T value)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        if (index == 0) {
            return new LeafNode<>(value);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    AbstractNode<T> head(int limit)
    {
        if (limit == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> tail(int offset)
    {
        if (offset == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of();
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return EmptyIterator.of();
    }
}
