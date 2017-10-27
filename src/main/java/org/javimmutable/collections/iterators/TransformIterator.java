package org.javimmutable.collections.iterators;

import org.javimmutable.collections.Func1;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class TransformIterator<S, T>
    extends AbstractSplitableIterator<T>
{
    private final Func1<S, T> transforminator;
    private final SplitableIterator<S> source;

    private TransformIterator(@Nonnull SplitableIterator<S> source,
                              @Nonnull Func1<S, T> transforminator)
    {
        this.transforminator = transforminator;
        this.source = source;
    }

    public static <S, T> TransformIterator<S, T> iterator(@Nonnull SplitableIterator<S> source,
                                                          @Nonnull Func1<S, T> transforminator)
    {
        return new TransformIterator<>(source, transforminator);
    }

    @Override
    public boolean hasNext()
    {
        return source.hasNext();
    }

    @Override
    public T next()
    {
        return transforminator.apply(source.next());
    }

    @Override
    public boolean isSplitAllowed()
    {
        return source.isSplitAllowed();
    }

    @Nonnull
    @Override
    public SplitIterator<T> splitIterator()
    {
        final SplitIterator<S> split = source.splitIterator();
        return new SplitIterator<>(new TransformIterator<>(split.getLeft(), transforminator),
                                   new TransformIterator<>(split.getRight(), transforminator));
    }
}
