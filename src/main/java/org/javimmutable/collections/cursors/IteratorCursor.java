package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

import java.util.Iterator;

/**
 * Provides objects implementing the Cursor interface that can be used to traverse
 * an Iterator.  The objects are not actually immutable but are designed to behave
 * as though they are.  However since they depend on a mutable object tied to a
 * mutable collection for their state they are not thread safe.  They should only
 * be used when no other threads could be modifying the underlying collection.
 * Also their next() methods can throw exceptions due to concurrent modification
 * of the collection.
 */
public abstract class IteratorCursor<T>
        implements Cursor<T>
{
    private final Iterator<T> iterator;
    private final boolean hasNext;
    private Cursor<T> next;

    /**
     * Creates a mutable Cursor implementation that traverses the specified iterator.  Note that because
     * the Iterator and the collection it points to are not immutable outside factors can interfere with
     * the operation of this class.  In particular this class is not thread safe so the caller must take
     * precautions to synchronize access to the underlying collection.
     * <p/>
     * The Cursor retains information obtained from the Iterator so that multiple passes can be made over
     * the data, intermediate Cursor values can be saved and resumed for look-ahead etc.
     *
     * @param iterator
     * @param <T>
     * @return
     */
    public static <T> Cursor<T> of(Iterator<T> iterator)
    {
        return new Start<T>(iterator);
    }

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
        return of(iterable.iterator());
    }

    protected IteratorCursor(Iterator<T> iterator)
    {
        this.iterator = iterator;
        this.hasNext = iterator.hasNext();
    }

    @Override
    public Cursor<T> next()
    {
        if (next == null) {
            next = hasNext ? new Started<T>(iterator, iterator.next()) : EmptyStartedCursor.<T>of();
        }
        return next;
    }

    private static class Start<T>
            extends IteratorCursor<T>
    {
        private Start(Iterator<T> iterator)
        {
            super(iterator);
        }

        @Override
        public boolean hasValue()
        {
            throw new IllegalStateException();
        }

        @Override
        public T getValue()
        {
            throw new IllegalStateException();
        }
    }

    private static class Started<T>
            extends IteratorCursor<T>
    {
        private final T value;

        private Started(Iterator<T> iterator,
                        T value)
        {
            super(iterator);
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
    }
}
