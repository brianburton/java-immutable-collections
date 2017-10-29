package org.javimmutable.collections;

import javax.annotation.Nonnull;

public interface SplitableIterable<T>
    extends Iterable<T>
{
    @Override
    @Nonnull
    SplitableIterator<T> iterator();
}
