package org.javimmutable.collections.hash;

import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.hash.set.SetBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
class HashSetBuilder<T>
    implements JImmutableSet.Builder<T>
{
    private final SetBuilder<T> builder = new SetBuilder<>();

    @Nonnull
    @Override
    public synchronized JImmutableSet<T> build()
    {
        if (builder.size() == 0) {
            return JImmutableHashSet.of();
        } else {
            return new JImmutableHashSet<>(builder.build(), builder.getCollisionSet());
        }
    }

    @Override
    public synchronized int size()
    {
        return builder.size();
    }

    @Nonnull
    @Override
    public synchronized JImmutableSet.Builder<T> add(T value)
    {
        builder.add(value);
        return this;
    }

    @Nonnull
    @Override
    public synchronized JImmutableSet.Builder<T> clear()
    {
        builder.clear();
        return this;
    }
}
