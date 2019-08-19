package org.javimmutable.collections.iterators;

import org.javimmutable.collections.SplitIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;

@ThreadSafe
public class GenericIterator<T>
    extends AbstractSplitableIterator<T>
{
    static final int MIN_SIZE_FOR_SPLIT = 64;

    private final Iterable<T> root;
    private final int limit;
    private int offset;
    private State<T> state;

    public GenericIterator(@Nonnull Iterable<T> root,
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

    public interface Iterable<T>
    {
        @Nullable
        State<T> iterateOverRange(@Nullable State<T> parent,
                                  int offset,
                                  int limit);
    }

    public interface State<T>
    {
        T value();

        State<T> advance();
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
        offset += 1;
        if (offset < limit) {
            state = state.advance();
        } else {
            state = null;
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
        return new SplitIterator<T>(new GenericIterator<>(root, root.iterateOverRange(null, offset, splitIndex), offset, splitIndex),
                                    new GenericIterator<>(root, root.iterateOverRange(null, splitIndex, limit), splitIndex, limit));
    }
}
