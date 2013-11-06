package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;

import java.util.Iterator;

/**
 * Provides objects implementing the Cursor interface that can be used to traverse
 * an Iterable.  The Cursor are not actually immutable but are designed to behave
 * as though they are.  However since they depend on a mutable object tied to a
 * mutable collection for their state they are not thread safe.  They should only
 * be used when no other threads could be modifying the underlying collection.
 * Also their next() methods can throw exceptions due to concurrent modification
 * of the collection.
 */
public abstract class IterableCursor
{
    /**
     * Creates a mutable Cursor implementation that traverses the specified iterable.  Note that because
     * the Iterator and the collection it points to are not immutable outside factors can interfere with
     * the operation of this class.  In particular this class is not thread safe so the caller must take
     * precautions to synchronize access to the underlying collection.
     * <p/>
     * The Cursor retains information obtained from the Iterator so that multiple passes can be made over
     * the data, intermediate Cursor values can be saved and resumed for look-ahead etc.
     *
     * @param iterable
     * @param <T>
     * @return
     */
    public static <T> Cursor<T> of(Iterable<T> iterable)
    {
        return ValueFunctionCursor.<T, Function, Factory>of(new Factory<T>(iterable));
    }

    private static class Factory<T>
            implements ValueFunctionFactory<T, Function<T>>
    {
        private final Iterable<T> iterable;

        private Factory(Iterable<T> iterable)
        {
            this.iterable = iterable;
        }

        @Override
        public Function<T> createFunction()
        {
            return new Function<T>(iterable.iterator());
        }
    }

    private static class Function<T>
            implements ValueFunction<T>
    {
        private final Iterator<T> iterator;

        private Function(Iterator<T> iterator)
        {
            this.iterator = iterator;
        }

        @Override
        public Holder<T> nextValue()
        {
            return iterator.hasNext() ? Holders.of(iterator.next()) : Holders.<T>of();
        }
    }
}
