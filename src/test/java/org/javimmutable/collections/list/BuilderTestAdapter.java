package org.javimmutable.collections.list;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.common.StandardBuilderTests;

import java.util.Iterator;

public class BuilderTestAdapter<T>
    implements StandardBuilderTests.BuilderAdapter<T, JImmutableList<T>>
{
    private final JImmutableList.Builder<T> builder;

    public BuilderTestAdapter(JImmutableList.Builder<T> builder)
    {
        this.builder = builder;
    }

    @Override
    public JImmutableList<T> build()
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
