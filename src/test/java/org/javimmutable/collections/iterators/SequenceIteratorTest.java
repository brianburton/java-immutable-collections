package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.Sequence;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

public class SequenceIteratorTest
    extends TestCase
{
    public void test()
    {
        verifyOrderedIterable(asList(), () -> SequenceIterator.iterator(sequence()));
        verifyOrderedIterable(asList(1), () -> SequenceIterator.iterator(sequence(1)));
        verifyOrderedIterable(asList(1, 2), () -> SequenceIterator.iterator(sequence(1, 2)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence()));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1, 2)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1, 2, 3)));
        verifyOrderedSplit(false, asList(), asList(), SequenceIterator.iterator(sequence(1, 2, 3, 4, 5)));
    }

    private Sequence<Integer> sequence(int... values)
    {
        SimpleSequence<Integer> seq = new SimpleSequence<>(null, null);
        for (int i = values.length - 1; i >= 0; --i) {
            seq = new SimpleSequence<>(values[i], seq);
        }
        return seq;
    }

    private static class SimpleSequence<T>
        implements Sequence<T>
    {
        private final T head;
        private final Sequence<T> tail;

        private SimpleSequence(@Nullable T head,
                               @Nullable Sequence<T> tail)
        {
            this.tail = tail;
            this.head = head;
        }

        @Override
        public boolean isEmpty()
        {
            return tail == null;
        }

        @Override
        public T getHead()
        {
            return head;
        }

        @Nonnull
        @Override
        public Sequence<T> getTail()
        {
            if (tail == null) {
                throw new NoSuchElementException();
            }
            return tail;
        }
    }
}
