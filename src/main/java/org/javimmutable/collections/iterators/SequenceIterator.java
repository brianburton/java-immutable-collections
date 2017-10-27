package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Sequence;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class SequenceIterator<T>
    extends AbstractSplitableIterator<T>
{
    private final Sequence<T> sequence;
    private Sequence<T> current;

    private SequenceIterator(@Nonnull Sequence<T> sequence)
    {
        this.sequence = sequence;
    }

    public static <T> SequenceIterator<T> iterator(@Nonnull Sequence<T> sequence)
    {
        return new SequenceIterator<>(sequence);
    }

    @Override
    public boolean hasNext()
    {
        return !nextImpl().isEmpty();
    }

    @Override
    public T next()
    {
        current = nextImpl();
        if (current.isEmpty()) {
            throw new NoSuchElementException();
        }
        return current.getHead();
    }

    @Nonnull
    private Sequence<T> nextImpl()
    {
        if (current == null) {
            return sequence;
        } else if (current.isEmpty()) {
            return current;
        } else {
            return current.getTail();
        }
    }
}
