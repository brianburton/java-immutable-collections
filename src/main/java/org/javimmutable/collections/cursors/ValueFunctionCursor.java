package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;

/**
 * Provides standard Cursor objects for iterating over the values of arbitrary functions.
 * The cursors are actually created using a factory object that can produce the functions.
 * The factories ensure that a Cursor is restartable and permits lazy evaluation of
 * the beginning of iteration.  The function will only be created if the starting
 * Cursor's next() method is invoked.
 * <p/>
 * The Cursors are not immutable but are designed to act as though they are.
 * Cursors remember their next value between invocations so that standard look ahead
 * behavior is preserved.
 * <p/>
 * The Cursors are not thread safe.
 */
public abstract class ValueFunctionCursor
{
    /**
     * Produces a non-thread safe Cursor to traverse the values returned by a function.  The function
     * will be created using the factory when the traversal is initiated by calling the next() method.
     * Multiple invocations of the initial Cursor's next() method will produce multiple functions
     * using the factory.
     *
     * @param factory
     * @param <T>
     * @return
     */
    public static <T, F extends ValueFunction<T>, A extends ValueFunctionFactory<T, F>> Cursor<T> of(A factory)
    {
        return new Start<T, F, A>(factory);
    }

    protected static class Start<T, F extends ValueFunction<T>, A extends ValueFunctionFactory<T, F>>
            implements Cursor<T>
    {
        private final A factory;

        protected Start(A factory)
        {
            this.factory = factory;
        }

        @Override
        public boolean hasValue()
        {
            throw new Cursor.NotStartedException();
        }

        @Override
        public T getValue()
        {
            throw new Cursor.NotStartedException();
        }

        @Override
        public Cursor<T> next()
        {
            final F function = createFunction();
            final Holder<T> firstValue = function.nextValue();
            return firstValue.isFilled() ? new Started<T, F>(function, firstValue.getValue()) : EmptyStartedCursor.<T>of();
        }

        protected F createFunction()
        {
            return factory.createFunction();
        }
    }

    private static class Started<T, F extends ValueFunction<T>>
            implements Cursor<T>
    {
        private F function;
        private Cursor<T> next;
        private final T value;

        private Started(F function,
                        T value)
        {
            this.function = function;
            this.value = value;
        }

        @Override
        public boolean hasValue()
        {
            return true;
        }

        @Override
        public T getValue()
        {
            return value;
        }

        @Override
        public Cursor<T> next()
        {
            if (next == null) {
                final Holder<T> nextValue = function.nextValue();
                next = nextValue.isFilled() ? new Started<T, F>(function, nextValue.getValue()) : EmptyStartedCursor.<T>of();
            }
            return next;
        }
    }
}
