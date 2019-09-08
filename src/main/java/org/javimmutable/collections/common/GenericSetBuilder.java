package org.javimmutable.collections.common;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;

import javax.annotation.Nonnull;

public class GenericSetBuilder<T>
    implements JImmutableSet.Builder<T>
{
    private final JImmutableMap.Builder<T, Boolean> mapBuilder;
    private final Func1<JImmutableMap<T, Boolean>, JImmutableSet<T>> setFactory;

    public GenericSetBuilder(JImmutableMap.Builder<T, Boolean> mapBuilder,
                             Func1<JImmutableMap<T, Boolean>, JImmutableSet<T>> setFactory)
    {
        this.mapBuilder = mapBuilder;
        this.setFactory = setFactory;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> build()
    {
        return setFactory.apply(mapBuilder.build());
    }

    @Override
    public int size()
    {
        return mapBuilder.size();
    }

    @Nonnull
    @Override
    public JImmutableSet.Builder<T> add(T value)
    {
        mapBuilder.add(value, Boolean.TRUE);
        return this;
    }
}
