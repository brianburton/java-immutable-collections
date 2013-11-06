package org.javimmutable.collections.iocursors;

import org.javimmutable.collections.cursors.ValueFunctionFactory;

public interface CloseableValueFunctionFactory<T, F extends CloseableValueFunction<T>>
        extends ValueFunctionFactory<T, F>
{
}
