package org.javimmutable.collections.token_list;

import org.javimmutable.collections.IterableStreamable;

import javax.annotation.Nonnull;

/**
 * Immutable data structure supporting only three operations:
 * 1. Add a value to the list and receive a token in return.
 * 2. Remove a value from the list using the token provided earlier.
 * 3. Iterate through token/value pairs in the order of insertion.
 */
public interface JImmutableTokenList<T>
{
    interface Token
    {
    }

    interface Entry<T>
    {
        @Nonnull
        Token token();

        T value();
    }

    static <T> JImmutableTokenList<T> of()
    {
        return EmptyTokenList.instance();
    }

    /**
     * Adds the specified value and return a new list containing that value.
     * The new list's lastToken() method will return the newly added token.
     */
    @Nonnull
    JImmutableTokenList<T> insertLast(T value);

    /**
     * Remove the specified token from this list.
     * If this list contains the token returns a new one that does not.
     * If this list does not contain the token returns this list unmodified.
     */
    @Nonnull
    JImmutableTokenList<T> delete(@Nonnull Token token);

    /**
     * Returns the most recently added token.
     * The token may or may not actually be present in this list.
     */
    @Nonnull
    Token lastToken();

    int size();

    @Nonnull
    IterableStreamable<Token> tokens();

    @Nonnull
    IterableStreamable<T> values();

    @Nonnull
    IterableStreamable<Entry<T>> entries();
}
