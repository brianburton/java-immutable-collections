package org.javimmutable.collections;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.list.JImmutableLinkedStack;

public final class IStacks
{
    private IStacks()
    {
    }

    /**
     * Produces an empty JImmutableStack.
     */
    @Nonnull
    public static <T> IStack<T> of()
    {
        return JImmutableLinkedStack.of();
    }

    /**
     * Produces a JImmutableStack containing all of the specified values.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     */
    @Nonnull
    @SafeVarargs
    public static <T> IStack<T> of(T... source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     */
    @Nonnull
    public static <T> IStack<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(source);
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     */
    @Nonnull
    public static <T> IStack<T> allOf(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(source);
    }
}
