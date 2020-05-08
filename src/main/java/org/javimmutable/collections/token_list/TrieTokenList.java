package org.javimmutable.collections.token_list;

import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;

class TrieTokenList<T>
    implements JImmutableTokenList<T>
{
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
                return root.iterator();
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return StreamConstants.SPLITERATOR_ORDERED;
            }
        };
    }
}
