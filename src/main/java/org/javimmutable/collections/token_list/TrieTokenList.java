package org.javimmutable.collections.token_list;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.TransformIterator;

import javax.annotation.Nonnull;

class TrieTokenList<T>
    implements JImmutableTokenList<T>
{
    private static final int CHARACTERISTICS = StreamConstants.SPLITERATOR_ORDERED;
    private final TrieNode<T> root;
    private final TokenImpl lastToken;

    TrieTokenList(@Nonnull TrieNode<T> root,
                  @Nonnull TokenImpl lastToken)
    {
        this.root = root;
        this.lastToken = lastToken;
    }

    @Nonnull
    @Override
    public JImmutableTokenList<T> insertLast(T value)
    {
        final TokenImpl token = lastToken.next();
        return new TrieTokenList<>(root.assign(token, value), token);
    }

    @Nonnull
    @Override
    public JImmutableTokenList<T> delete(@Nonnull Token token)
    {
        final TrieNode<T> newRoot = root.delete((TokenImpl)token);
        return (newRoot == root) ? this : new TrieTokenList<>(newRoot, lastToken);
    }

    @Nonnull
    @Override
    public Token lastToken()
    {
        return lastToken;
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Override
    @Nonnull
    public IterableStreamable<Token> tokens()
    {
        return new IterableStreamable<Token>()
        {
            @Nonnull
            @Override
            public SplitableIterator<Token> iterator()
            {
                return TransformIterator.of(root.iterator(), JImmutableTokenList.Entry::token);
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return CHARACTERISTICS;
            }
        };
    }

    @Override
    @Nonnull
    public IterableStreamable<T> values()
    {
        return new IterableStreamable<T>()
        {
            @Nonnull
            @Override
            public SplitableIterator<T> iterator()
            {
                return TransformIterator.of(root.iterator(), JImmutableTokenList.Entry::value);
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return CHARACTERISTICS;
            }
        };
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
                return root.iterator();
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return CHARACTERISTICS;
            }
        };
    }
}
