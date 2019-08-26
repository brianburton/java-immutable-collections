package org.javimmutable.collections.iterators;

import org.javimmutable.collections.SplitIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.NoSuchElementException;
import java.util.function.ToIntFunction;

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
                           int offset,
                           int limit)
    {
        assert offset <= limit;
        this.root = root;
        this.limit = limit;
        this.offset = offset;
        this.state = root.iterateOverRange(null, offset, limit);
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
        return new SplitIterator<>(new GenericIterator<>(root, offset, splitIndex),
                                   new GenericIterator<>(root, splitIndex, limit));
    }

    private static class SingleValueState<T>
        implements State<T>
    {
        private final State<T> parent;
        private final T value;

        private SingleValueState(State<T> parent,
                                 T value)
        {
            this.parent = parent;
            this.value = value;
        }

        @Override
        public T value()
        {
            return value;
        }

        @Override
        public State<T> advance()
        {
            return parent.advance();
        }
    }

    public static <T> State<T> valueState(State<T> parent,
                                          T value)
    {
        return new SingleValueState<>(parent, value);
    }

    public static <T, C extends Iterable<T>> State<T> arrayState(State<T> parent,
                                                                 C[] children,
                                                                 ToIntFunction<C> sizer,
                                                                 int offset,
                                                                 int limit)
    {
        int childIndex = 0;
        int childSize = sizer.applyAsInt(children[childIndex]);
        int childLimit = childSize;
        while (offset >= childLimit) {
            childIndex += 1;
            childSize = sizer.applyAsInt(children[childIndex]);
            childLimit += childSize;
        }
        final int childOffset = childLimit - childSize;
        final int remaining = limit - childOffset;

        final State<T> answerParent;
        if (remaining > childSize) {
            answerParent = new ArrayState<>(parent, children, sizer, childIndex + 1, childLimit, limit);
            childLimit = childSize;
        } else {
            answerParent = parent;
            childLimit = remaining;
        }

        return children[childIndex].iterateOverRange(answerParent, offset - childOffset, childLimit);
    }

    private static class ArrayState<T, C extends Iterable<T>>
        implements State<T>
    {
        private final State<T> parent;
        private final C[] children;
        private final ToIntFunction<C> sizer;
        private final int limit;
        private int childIndex;
        private int childOffset;

        public ArrayState(State<T> parent,
                          C[] children,
                          ToIntFunction<C> sizer,
                          int childIndex,
                          int childOffset,
                          int limit)
        {
            assert childIndex < children.length;
            assert childOffset < limit;
            this.parent = parent;
            this.children = children;
            this.sizer = sizer;
            this.limit = limit;
            this.childIndex = childIndex;
            this.childOffset = childOffset;
        }

        @Override
        public T value()
        {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized State<T> advance()
        {
            final int childSize = sizer.applyAsInt(children[childIndex]);
            final int remaining = limit - childOffset;
            final int childLimit;
            final State<T> parent;
            if (remaining > childSize) {
                parent = this;
                childLimit = childSize;
            } else {
                parent = this.parent;
                childLimit = remaining;
            }

            final State<T> answer = children[childIndex].iterateOverRange(parent, 0, childLimit);
            childIndex += 1;
            childOffset += childSize;
            return answer;
        }
    }
}
