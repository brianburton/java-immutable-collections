package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArrayMultiValueSetNode<T>
    implements ArraySetNode<T>
{
    private final CollisionSet.Node node;

    public ArrayMultiValueSetNode(@Nonnull CollisionSet.Node node)
    {
        this.node = node;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return collisionSet.size(node);
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            @Nonnull T value)
    {
        return collisionSet.contains(node, value);
    }

    @Nonnull
    @Override
    public ArraySetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                                  @Nonnull T value)
    {
        final CollisionSet.Node oldNode = this.node;
        final CollisionSet.Node newNode = collisionSet.insert(oldNode, value);
        if (newNode == oldNode) {
            return this;
        } else {
            return new ArrayMultiValueSetNode<>(newNode);
        }
    }

    @Nullable
    @Override
    public ArraySetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                                  @Nonnull T value)
    {
        final CollisionSet.Node oldNode = this.node;
        final CollisionSet.Node newNode = collisionSet.delete(oldNode, value);
        if (newNode == oldNode) {
            return this;
        } else if (collisionSet.size(newNode) == 0) {
            return null;
        } else {
            return new ArrayMultiValueSetNode<>(newNode);
        }
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<T> values(@Nonnull CollisionSet<T> collisionSet)
    {
        return collisionSet.genericIterable(node);
    }

    @Override
    public void forEach(@Nonnull CollisionSet<T> collisionSet,
                        @Nonnull Proc1<T> proc)
    {
        collisionSet.forEach(node, proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        collisionSet.forEachThrows(node, proc);
    }
}
