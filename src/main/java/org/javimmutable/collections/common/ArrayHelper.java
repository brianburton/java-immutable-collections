///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;

public final class ArrayHelper
{
    public interface Allocator<T>
    {
        @Nonnull
        T[] allocate(int size);
    }

    @Nonnull
    public static <T> T[] subArray(@Nonnull Allocator<T> allocator,
                                   @Nonnull T[] orig,
                                   int offset,
                                   int limit)
    {
        final int length = limit - offset;
        T[] answer = allocator.allocate(length);
        System.arraycopy(orig, offset, answer, 0, length);
        return answer;
    }

    @Nonnull
    public static <T> T[] subArray(@Nonnull Allocator<T> allocator,
                                   @Nonnull T[] a,
                                   @Nonnull T[] b,
                                   int offset,
                                   int limit)
    {
        int length = limit - offset;
        T[] answer = allocator.allocate(length);
        if (offset > a.length) {
            System.arraycopy(b, offset - a.length, answer, 0, length);
        } else if (limit <= a.length) {
            System.arraycopy(a, offset, answer, 0, length);
        } else {
            int alength = a.length - offset;
            System.arraycopy(a, offset, answer, 0, alength);
            System.arraycopy(b, 0, answer, alength, length - alength);
        }
        return answer;
    }

    @Nonnull
    public static <T> T[] assign(@Nonnull T[] orig,
                                 int index,
                                 T value)
    {
        T[] answer = orig.clone();
        answer[index] = value;
        return answer;
    }

    @Nonnull
    public static <T> T[] append(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 T value)
    {
        T[] answer = allocator.allocate(orig.length + 1);
        System.arraycopy(orig, 0, answer, 0, orig.length);
        answer[orig.length] = value;
        return answer;
    }

    @Nonnull
    public static <T> T[] insert(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 int index,
                                 T value)
    {
        if (index == orig.length) {
            return append(allocator, orig, value);
        } else {
            T[] answer = allocator.allocate(orig.length + 1);
            System.arraycopy(orig, 0, answer, 0, index);
            System.arraycopy(orig, index, answer, index + 1, orig.length - index);
            answer[index] = value;
            return answer;
        }
    }

    @Nonnull
    public static <T> T[] delete(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 int index)
    {
        T[] answer = allocator.allocate(orig.length - 1);
        System.arraycopy(orig, 0, answer, 0, index);
        System.arraycopy(orig, index + 1, answer, index, orig.length - index - 1);
        return answer;
    }

    @Nonnull
    public static <T> T[] concat(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] a,
                                 @Nonnull T[] b)
    {
        T[] answer = allocator.allocate(a.length + b.length);
        System.arraycopy(a, 0, answer, 0, a.length);
        System.arraycopy(b, 0, answer, a.length, b.length);
        return answer;
    }

    /**
     * Replace the last value in orig (which cannot be empty) with assignValue
     * and also append appendValue to the end.  Always returns a new array.
     *
     * @param orig
     * @param assignValue
     * @param appendValue
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> T[] assignAppend(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       T assignValue,
                                       T appendValue)
    {
        T[] answer = allocator.allocate(orig.length + 1);
        System.arraycopy(orig, 0, answer, 0, orig.length - 1);
        answer[orig.length - 1] = assignValue;
        answer[orig.length] = appendValue;
        return answer;
    }

    /**
     * Replaces the node at index with first and the node at index + 1 with second.
     * Always returns a new array.
     * Array must have size >= index + 2.
     *
     * @param orig
     * @param index
     * @param first
     * @param second
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> T[] assignTwo(@Nonnull T[] orig,
                                    int index,
                                    T first,
                                    T second)
    {
        T[] answer = orig.clone();
        answer[index] = first;
        answer[index + 1] = second;
        return answer;
    }

    /**
     * Replace the value at index with assignValue and insert insertValue immediately at index + 1.
     * Always returns a new array.
     * Array size must be at least 1.
     *
     * @param orig
     * @param index
     * @param assignValue
     * @param insertValue
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> T[] assignInsert(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       int index,
                                       T assignValue,
                                       T insertValue)
    {
        T[] answer = allocator.allocate(orig.length + 1);
        System.arraycopy(orig, 0, answer, 0, index);
        System.arraycopy(orig, index, answer, index + 1, orig.length - index);
        answer[index] = assignValue;
        answer[index + 1] = insertValue;
        return answer;
    }

    /**
     * Deletes the node at index and sets the value of the resulting array at index
     * to newNode.
     * Always returns a new array.
     *
     * @param orig
     * @param index
     * @param newNode
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> T[] assignDelete(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       int index,
                                       T newNode)
    {
        T[] answer = allocator.allocate(orig.length - 1);
        System.arraycopy(orig, 0, answer, 0, index);
        System.arraycopy(orig, index + 1, answer, index, orig.length - index - 1);
        answer[index] = newNode;
        return answer;
    }

    /**
     * Creates an Allocator for arrays of the given class.
     *
     * @param klass
     * @param <T>
     * @return
     */
    @Nonnull
    public static <T> Allocator<T> allocator(final Class<T> klass)
    {
        return new Allocator<T>()
        {
            @SuppressWarnings("unchecked")
            @Nonnull
            @Override
            public T[] allocate(int size)
            {
                return (T[])(Array.newInstance(klass, size));
            }
        };
    }
}
