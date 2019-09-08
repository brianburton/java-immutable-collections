package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class MapBuilderTestAdaptor<K extends Comparable<K>, V>
    implements StandardBuilderTests.BuilderAdapter<JImmutableMap.Entry<K, V>, JImmutableMap<K, V>>
{
    private final JImmutableMap.Builder<K, V> builder;

    public MapBuilderTestAdaptor(@Nonnull JImmutableMap.Builder<K, V> builder)
    {
        this.builder = builder;
    }

    @Override
    public JImmutableMap<K, V> build()
    {
        return builder.build();
    }

    @Override
    public int size()
    {
        return builder.size();
    }

    @Override
    public void add(JImmutableMap.Entry<K, V> value)
    {
        builder.add(value);
    }

    @Override
    public void add(Iterator<? extends JImmutableMap.Entry<K, V>> source)
    {
        builder.add(source);
    }

    @Override
    public void add(Iterable<? extends JImmutableMap.Entry<K, V>> source)
    {
        builder.add(source);
    }

    @Override
    public <T extends JImmutableMap.Entry<K, V>> void add(T... source)
    {
        JImmutableMap.Entry<K, V>[] entries = new JImmutableMap.Entry[source.length];
        System.arraycopy(source, 0, entries, 0, source.length);
        builder.add(entries);
    }

    @Override
    public void add(Indexed<? extends JImmutableMap.Entry<K, V>> source,
                    int offset,
                    int limit)
    {
        builder.add(source, offset, limit);
    }

    @Override
    public void add(Indexed<? extends JImmutableMap.Entry<K, V>> source)
    {
        builder.add(source);
    }
}
