package org.javimmutable.collections.list;

import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.iterators.AbstractSplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;

@ThreadSafe
class NodeIterator<T>
    extends AbstractSplitableIterator<T>
{
    static final int MIN_SIZE_FOR_SPLIT = 32;

    private final AbstractNode<T> root;
    private final int limit;
    private int offset;
    private State<T> state;

    NodeIterator(@Nonnull AbstractNode<T> root,
                 @Nullable State<T> state,
                 int offset,
                 int limit)
    {
        assert offset < limit || state == null;
        this.root = root;
        this.limit = limit;
        this.offset = offset;
        this.state = state;
    }

    static abstract class State<T>
    {
        abstract T value();

        abstract State<T> advance();
    }

    @Override
    public synchronized boolean hasNext()
    {
        assert offset < limit || state == null;
        return state != null;
    }

    @Override
    public synchronized T next()
    {
        if (state == null) {
            throw new NoSuchElementException();
        }
        final T answer = state.value();
        state = state.advance();
        if (state != null) {
            offset += 1;
        }
        assert offset <= limit;
        return answer;
    }

    @Override
    public synchronized boolean isSplitAllowed()
    {
        return (limit - offset) >= MIN_SIZE_FOR_SPLIT;
    }

    @Nonnull
    @Override
    public synchronized SplitIterator<T> splitIterator()
    {
        final int splitIndex = offset + (limit - offset) / 2;
        return new SplitIterator<T>(new NodeIterator<>(root, root.iterateOverRange(null, offset, splitIndex), offset, splitIndex),
                                    new NodeIterator<>(root, root.iterateOverRange(null, splitIndex, limit), splitIndex, limit));
    }
}
