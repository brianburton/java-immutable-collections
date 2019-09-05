package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Indexed;
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
    static final int MIN_SIZE_FOR_SPLIT = 32;

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
        default boolean hasValue()
        {
            return false;
        }

        default T value()
        {
            throw new NoSuchElementException();
        }

        State<T> advance();
    }

    @Override
    public synchronized boolean hasNext()
    {
        return prepare();
    }

    @Override
    public synchronized T next()
    {
        if (!prepare()) {
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

    private boolean prepare()
    {
        while (state != null) {
            if (state.hasValue()) {
                return true;
            }
            state = state.advance();
        }
        return false;
    }

    public static <T> State<T> valueState(State<T> parent,
                                          T value)
    {
        return new SingleValueState<>(parent, value);
    }

    public static <T> State<T> multiValueState(@Nullable State<T> parent,
                                               @Nonnull Indexed<T> values,
                                               int offset,
                                               int limit)
    {
        assert offset >= 0 && offset <= limit && limit <= values.size();
        return new MultiValueState<>(parent, values, offset, limit);
    }

    public static <T, C extends Iterable<T>> State<T> indexedState(State<T> parent,
                                                                   Indexed<C> children,
                                                                   ToIntFunction<C> sizer,
                                                                   int offset,
                                                                   int limit)
    {
        assert 0 <= offset && offset <= limit;
        if (offset == limit) {
            return parent;
        } else {
            return new IndexedState<>(parent, children, sizer, offset, limit);
        }
    }
    
    private static class SingleValueState<T>
        implements State<T>
    {
        private final State<T> parent;
        private final T value;
        private boolean available;

        private SingleValueState(State<T> parent,
                                 T value)
        {
            this.parent = parent;
            this.value = value;
            available = true;
        }

        @Override
        public boolean hasValue()
        {
            return available;
        }

        @Override
        public T value()
        {
            assert available;
            available = false;
            return value;
        }

        @Override
        public State<T> advance()
        {
            assert !available;
            return parent;
        }
    }

    private static class MultiValueState<T>
        implements GenericIterator.State<T>
    {
        private final GenericIterator.State<T> parent;
        private final Indexed<T> values;
        private final int limit;
        private int offset;

        private MultiValueState(@Nullable GenericIterator.State<T> parent,
                                @Nonnull Indexed<T> values,
                                int offset,
                                int limit)
        {
            this.parent = parent;
            this.values = values;
            this.offset = offset;
            this.limit = limit;
        }

        @Override
        public boolean hasValue()
        {
            return offset < limit;
        }

        @Override
        public T value()
        {
            return values.get(offset);
        }

        @Nullable
        @Override
        public GenericIterator.State<T> advance()
        {
            offset += 1;
            if (offset < limit) {
                return this;
            } else {
                return parent;
            }
        }
    }

    private static class IndexedState<T, C extends Iterable<T>>
        implements State<T>
    {
        private final State<T> parent;
        private final Indexed<C> children;
        private final ToIntFunction<C> sizer;
        private int offset;
        private int limit;
        private int index;

        public IndexedState(State<T> parent,
                            Indexed<C> children,
                            ToIntFunction<C> sizer,
                            int offset,
                            int limit)
        {
            this.parent = parent;
            this.children = children;
            this.sizer = sizer;
            this.limit = limit;
            this.offset = offset;
            index = 0;
        }

        @Override
        public State<T> advance()
        {
            final C child = children.get(index);
            final int size = sizer.applyAsInt(child);
            if (offset >= size) {
                index += 1;
                offset -= size;
                limit -= size;
                return this;
            } else if (limit <= size) {
                return child.iterateOverRange(parent, offset, limit);
            } else {
                final State<T> answer = child.iterateOverRange(this, offset, size);
                index += 1;
                offset = 0;
                limit -= size;
                return answer;
            }
        }
    }
}
