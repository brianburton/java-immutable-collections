package org.javimmutable.collections.cursors;

public interface ValueFunctionFactory<T, F extends ValueFunction<T>>
{
    F createFunction();
}
