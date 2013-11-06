package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Holder;

public interface ValueFunction<T>
{
    Holder<T> nextValue();
}
