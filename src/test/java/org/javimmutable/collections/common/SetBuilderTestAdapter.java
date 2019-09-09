package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableSet;

import java.util.Iterator;

public class SetBuilderTestAdapter<T>
    implements StandardBuilderTests.BuilderAdapter<T, JImmutableSet<T>>
{
    private final JImmutableSet.Builder<T> builder;

    public SetBuilderTestAdapter(JImmutableSet.Builder<T> builder)
    {
        this.builder = builder;
    }

    @Override
    public JImmutableSet<T> build()
    {
        return builder.build();
    }

    @Override
    public int size()
    {
        return builder.size();
    }

    @Override
    public void add(T value)
    {
        builder.add(value);
    }

    @Override
    public void add(Iterator<? extends T> source)
    {
        builder.add(source);
    }

    @Override
    public void add(Iterable<? extends T> source)
    {
        builder.add(source);
    }

    @Override
    public <K extends T> void add(K... source)
    {
        builder.add(source);
    }

    @Override
    public void add(Indexed<? extends T> source,
                    int offset,
                    int limit)
    {
        builder.add(source, offset, limit);
    }

    @Override
    public void add(Indexed<? extends T> source)
    {
        builder.add(source);
    }
}
