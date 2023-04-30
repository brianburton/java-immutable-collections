///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections;

import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.list.TreeList;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public final class ILists
{
    private ILists()
    {
    }

    /**
     * Produces an empty JImmutableList built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> of()
    {
        return TreeList.of();
    }

    /**
     * Efficiently produces a JImmutableList containing all of the specified values built atop a balanced binary tree.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IList<T> of(T... values)
    {
        return TreeList.of(IndexedArray.retained(values));
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Indexed<? extends T> source)
    {
        return TreeList.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in the specified range from source
     * built atop a balanced binary tree.  The values copied from source are those whose index are in the
     * range offset to (limit - 1).
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Indexed<? extends T> source,
                                     int offset,
                                     int limit)
    {
        return TreeList.of(source, offset, limit);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull ISet<? extends T> source)
    {
        return TreeList.of(source.iterator());
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull List<? extends T> source)
    {
        return TreeList.of(IndexedList.retained(source));
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Iterator<? extends T> source)
    {
        return TreeList.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull IDeque<? extends T> source)
    {
        return TreeList.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return TreeList.of(source.iterator());
    }
}
