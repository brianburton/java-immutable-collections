package org.javimmutable.collections.iterators;

import javax.annotation.Nonnull;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class AbstractSplitableIterator<T>
    implements SplitableIterator<T>
{
    @Nonnull
    @Override
    public Spliterator<T> spliterator(int characteristics)
    {
        return new SpliteratorImpl<>(characteristics, this);
    }

    private static class SpliteratorImpl<T>
        implements Spliterator<T>
    {
        private final int characteristics;
        @Nonnull
        private SplitableIterator<T> iterator;

        private SpliteratorImpl(int characteristics,
                                @Nonnull SplitableIterator<T> iterator)
        {
            this.characteristics = characteristics;
            this.iterator = iterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action)
        {
            if (iterator.hasNext()) {
                action.accept(iterator.next());
                return true;
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit()
        {
            if (iterator.isSplitAllowed()) {
                final SplitIterator<T> split = iterator.splitIterator();
                iterator = split.getRight();
                return new SpliteratorImpl<>(characteristics, split.getLeft());
            }
            return null;
        }

        @Override
        public long estimateSize()
        {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics()
        {
            return characteristics;
        }
    }
}
