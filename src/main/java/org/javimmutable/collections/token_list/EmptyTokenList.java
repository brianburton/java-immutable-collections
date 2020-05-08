package org.javimmutable.collections.token_list;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;

class EmptyTokenList<T>
    implements JImmutableTokenList<T>
{
    @SuppressWarnings("rawtypes")
    private static final EmptyTokenList EMPTY = new EmptyTokenList();

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> JImmutableTokenList<T> instance()
    {
        return (JImmutableTokenList<T>)EMPTY;
    }

    @Nonnull
    @Override
    public JImmutableTokenList<T> insertLast(T value)
    {
        return new TrieTokenList<>(TrieNode.<T>empty().assign(TokenImpl.ZERO, value), TokenImpl.ZERO);
    }

    @Nonnull
    @Override
    public JImmutableTokenList<T> delete(@Nonnull Token token)
    {
        return this;
    }

    @Nonnull
    @Override
    public Token lastToken()
    {
        return TokenImpl.ZERO;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    @Nonnull
    public IterableStreamable<Token> tokens()
    {
        return TransformStreamable.of(entries(), JImmutableTokenList.Entry::token);
    }

    @Override
    @Nonnull
    public IterableStreamable<T> values()
    {
        return TransformStreamable.of(entries(), JImmutableTokenList.Entry::value);
    }

    @Nonnull
    @Override
    public IterableStreamable<Entry<T>> entries()
    {
        return new IterableStreamable<Entry<T>>()
        {
            @Nonnull
            @Override
            public SplitableIterator<Entry<T>> iterator()
            {
                return EmptyIterator.of();
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return StreamConstants.SPLITERATOR_ORDERED;
            }
        };
    }
}
