package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

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
        return new Start<T>(iterable);
    }

    private static class Start<T>
            implements Cursor<T>
    {
        private final Iterable<T> iterable;

        private Start(Iterable<T> iterable)
        {
            this.iterable = iterable;
        }

        @Override
        public boolean hasValue()
        {
            throw new NotStartedException();
        }

        @Override
        public T getValue()
        {
            throw new NotStartedException();
        }

        @Override
        public Cursor<T> next()
        {
            final Iterator<T> iterator = iterable.iterator();
            return iterator.hasNext() ? new Started<T>(iterator, iterator.next()) : EmptyStartedCursor.<T>of();
        }
    }

    private static class Started<T>
            implements Cursor<T>
    {
        private final boolean hasNext;
        private Iterator<T> iterator;
        private Cursor<T> next;
        private final T value;

        private Started(Iterator<T> iterator,
                        T value)
        {
            this.hasNext = iterator.hasNext();
            if (hasNext) {
                this.iterator = iterator;
            }
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
                next = hasNext ? new Started<T>(iterator, iterator.next()) : EmptyStartedCursor.<T>of();
                iterator = null;
            }
            return next;
        }
    }
}
