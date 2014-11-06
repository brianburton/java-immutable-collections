package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Wrapper for an Indexed that only provides access to a portion of the full Indexed's values.
 *
 * @param <T>
 */
@Immutable
public class Subindexed<T>
        implements Indexed<T>
{
    private final Indexed<? extends T> source;
    private final int offset;
    private final int size;

    public Subindexed(@Nonnull Indexed<? extends T> source,
                      int offset,
                      int limit)
    {
        if ((offset < 0) || (offset > source.size()) || (limit < offset) || (limit > source.size())) {
            throw new IndexOutOfBoundsException();
        }
        this.source = source;
        this.offset = offset;
        this.size = limit - offset;
    }

    public static <T> Subindexed<T> of(@Nonnull Indexed<? extends T> source,
                                       int offset)
    {
        return new Subindexed<T>(source, offset, source.size());
    }

    public static <T> Subindexed<T> of(@Nonnull Indexed<? extends T> source,
                                       int offset,
                                       int limit)
    {
        return new Subindexed<T>(source, offset, limit);
    }

    @Override
    public T get(int index)
    {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException();
        }
        return source.get(offset + index);
    }

    @Override
    public int size()
    {
        return size;
    }
}
