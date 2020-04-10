package org.javimmutable.collections.hash;

import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractJImmutableSet;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class EmptyHashSet<T>
    extends AbstractJImmutableSet<T>
    implements Serializable
{
    @SuppressWarnings("rawtypes")
    private static final EmptyHashSet INSTANCE = new EmptyHashSet();

    @SuppressWarnings("unchecked")
    public static <T> EmptyHashSet<T> instance()
    {
        return (EmptyHashSet<T>)INSTANCE;
    }

    @Override
    protected Set<T> emptyMutableSet()
    {
        return new HashSet<>();
    }

    @Nonnull
    @Override
    public JImmutableSet<T> insert(@Nonnull T value)
    {
        return JImmutableHashSet.of(value);
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return false;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> delete(T value)
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Iterable<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Iterator<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Iterator<? extends T> values)
    {
        return new HashSetBuilder<T>().add(values).build();
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterable<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull JImmutableSet<? extends T> other)
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Set<? extends T> other)
    {
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return EmptyIterator.of();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_UNORDERED;
    }
}
