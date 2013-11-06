package org.javimmutable.collections.iocursors;

import org.javimmutable.collections.cursors.ValueFunctionCursor;

/**
 * Produces CloseableCursors for navigating CloseableValueFunctions.
 */
public abstract class CloseableValueFunctionCursor
        extends ValueFunctionCursor
{
    public static <T, F extends CloseableValueFunction<T>, Y extends CloseableValueFunctionFactory<T, F>> CloseableCursor<T> of(Y factory)
    {
        return new Start<T, F, Y>(factory);
    }

    private static class Start<T, F extends CloseableValueFunction<T>, Y extends CloseableValueFunctionFactory<T, F>>
            extends ValueFunctionCursor.Start<T, F, Y>
            implements CloseableCursor<T>
    {
        private F function;

        private Start(Y factory)
        {
            super(factory);
        }

        @Override
        protected F createFunction()
        {
            close();
            function = super.createFunction();
            return function;
        }

        @Override
        public void close()
        {
            if (function != null) {
                try {
                    function.close();
                } finally {
                    function = null;
                }
            }
        }
    }
}
