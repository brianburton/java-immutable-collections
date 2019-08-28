///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

    /**
     * Allocate a new array containing the subarray of orig starting at offset and ending before limit.
     * To copy the first value offset would be 0, limit would be 1.
     *
     * @param allocator used to allocate the resulting array
     * @param orig      array from which to copy
     * @param offset    0 based offset into array
     * @param limit     0 based offset of first value NOT copied (length of copy is limit - offset)
     */
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

    /**
     * Allocate an array containing values from a logical array formed by concatenating the two input arrays.
     * Functionally equivalent to calling append(a,b) and then calling subArray() with the result
     *
     * @param allocator used to allocate the resulting array
     * @param a         first input array
     * @param b         second input array
     * @param offset    0 based offset into logical concatenated array
     * @param limit     0 based offset of first value NOT copied (length of copy is limit - offset)
     */
    @Nonnull
    public static <T> T[] subArray(@Nonnull Allocator<T> allocator,
                                   @Nonnull T[] a,
                                   @Nonnull T[] b,
                                   int offset,
                                   int limit)
    {
        final int length = limit - offset;
        final T[] answer = allocator.allocate(length);
        if (offset > a.length) {
            System.arraycopy(b, offset - a.length, answer, 0, length);
        } else if (limit <= a.length) {
            System.arraycopy(a, offset, answer, 0, length);
        } else {
            final int alength = a.length - offset;
            System.arraycopy(a, offset, answer, 0, alength);
            System.arraycopy(b, 0, answer, alength, length - alength);
        }
        return answer;
    }

    /**
     * Creates a copy of orig with the value at index replaced by value.
     *
     * @param orig  array to copy
     * @param index index of value to change
     * @param value value to assign at index
     */
    @Nonnull
    public static <T> T[] assign(@Nonnull T[] orig,
                                 int index,
                                 T value)
    {
        final T[] answer = orig.clone();
        answer[index] = value;
        return answer;
    }

    /**
     * Creates a copy of orig with one extra value added at the end.
     * Length of result is orig.length + 1.
     *
     * @param allocator used to allocate the resulting array
     * @param orig      array to copy
     * @param value     value to assign at end of new array
     */
    @Nonnull
    public static <T> T[] append(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 T value)
    {
        final T[] answer = allocator.allocate(orig.length + 1);
        System.arraycopy(orig, 0, answer, 0, orig.length);
        answer[orig.length] = value;
        return answer;
    }

    /**
     * Creates a copy of orig with one extra value inserted at index.
     * All values from index->length are shifted to the right by 1.
     *
     * @param allocator used to allocate the resulting array
     * @param orig      array to copy
     * @param index     index where new value should be inserted
     * @param value     value to assign at end of new array
     */
    @Nonnull
    public static <T> T[] insert(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 int index,
                                 T value)
    {
        if (index == orig.length) {
            return append(allocator, orig, value);
        } else {
            final T[] answer = allocator.allocate(orig.length + 1);
            System.arraycopy(orig, 0, answer, 0, index);
            System.arraycopy(orig, index, answer, index + 1, orig.length - index);
            answer[index] = value;
            return answer;
        }
    }

    /**
     * Creates a copy of orig with one value deleted at index.
     * All values from index->length are shifted to the left by 1.
     *
     * @param allocator used to allocate the resulting array
     * @param orig      array to copy
     * @param index     index where new value should be inserted
     */
    @Nonnull
    public static <T> T[] delete(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 int index)
    {
        final T[] answer = allocator.allocate(orig.length - 1);
        System.arraycopy(orig, 0, answer, 0, index);
        System.arraycopy(orig, index + 1, answer, index, orig.length - index - 1);
        return answer;
    }

    /**
     * Creates a new array containing all the values of a followed by all the values of b.
     *
     * @param allocator used to allocate the resulting array
     * @param a         first input array
     * @param b         second input array
     */
    @Nonnull
    public static <T> T[] concat(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] a,
                                 @Nonnull T[] b)
    {
        final T[] answer = allocator.allocate(a.length + b.length);
        System.arraycopy(a, 0, answer, 0, a.length);
        System.arraycopy(b, 0, answer, a.length, b.length);
        return answer;
    }

    /**
     * Replace the last value in orig (which cannot be empty) with assignValue
     * and also append appendValue to the end.  Always returns a new array
     * of length orig.length + 1.
     */
    @Nonnull
    public static <T> T[] assignAppend(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       T assignValue,
                                       T appendValue)
    {
        final T[] answer = allocator.allocate(orig.length + 1);
        System.arraycopy(orig, 0, answer, 0, orig.length - 1);
        answer[orig.length - 1] = assignValue;
        answer[orig.length] = appendValue;
        return answer;
    }

    /**
     * Replaces the node at index with first and the node at index + 1 with second.
     * Always returns a new array.
     * Array must have size >= index + 2.
     */
    @Nonnull
    public static <T> T[] assignTwo(@Nonnull T[] orig,
                                    int index,
                                    T first,
                                    T second)
    {
        final T[] answer = orig.clone();
        answer[index] = first;
        answer[index + 1] = second;
        return answer;
    }

    /**
     * Replace the value at index with assignValue and insert insertValue immediately at index + 1.
     * Always returns a new array.
     * Array size must be at least 1.
     */
    @Nonnull
    public static <T> T[] assignInsert(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       int index,
                                       T assignValue,
                                       T insertValue)
    {
        final T[] answer = allocator.allocate(orig.length + 1);
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
     */
    @Nonnull
    public static <T> T[] assignDelete(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       int index,
                                       T newNode)
    {
        final T[] answer = allocator.allocate(orig.length - 1);
        System.arraycopy(orig, 0, answer, 0, index);
        System.arraycopy(orig, index + 1, answer, index, orig.length - index - 1);
        answer[index] = newNode;
        return answer;
    }

    /**
     * Returns a copy of orig containing of length limit containing all values from [0,limit).
     */
    @Nonnull
    public static <T> T[] prefix(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 int limit)
    {
        final T[] answer = allocator.allocate(limit);
        System.arraycopy(orig, 0, answer, 0, limit);
        return answer;
    }

    /**
     * Returns a copy of orig containing of length limit+1 containing all values from [0,limit)
     * but with value inserted at index and values after that shifted to the right.
     */
    @Nonnull
    public static <T> T[] prefixInsert(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       int limit,
                                       int index,
                                       T value)
    {
        final T[] answer = allocator.allocate(limit + 1);
        System.arraycopy(orig, 0, answer, 0, index);
        answer[index] = value;
        System.arraycopy(orig, index, answer, index + 1, limit - index);
        return answer;
    }

    /**
     * Returns a copy of orig containing of length orig.length-offset containing all values from [offset,orig.length).
     */
    @Nonnull
    public static <T> T[] suffix(@Nonnull Allocator<T> allocator,
                                 @Nonnull T[] orig,
                                 int offset)
    {
        try {
            final int length = orig.length - offset;
            final T[] answer = allocator.allocate(length);
            System.arraycopy(orig, offset, answer, 0, length);
            return answer;
        } catch (NegativeArraySizeException ex) {
            throw new ArrayIndexOutOfBoundsException(offset);
        }
    }

    /**
     * Returns a copy of orig containing of length orig.length-offset containing all values from [offset,orig.length)
     * but with value inserted at index relative to orig (index-offset relative to resulting array) and all values
     * past that point shifted to the right.
     */
    @Nonnull
    public static <T> T[] suffixInsert(@Nonnull Allocator<T> allocator,
                                       @Nonnull T[] orig,
                                       int offset,
                                       int index,
                                       T value)
    {
        final int length = orig.length - offset;
        final int answerIndex = index - offset;
        final T[] answer = allocator.allocate(length + 1);
        System.arraycopy(orig, offset, answer, 0, answerIndex);
        answer[answerIndex] = value;
        System.arraycopy(orig, index, answer, answerIndex + 1, length - answerIndex);
        return answer;
    }

    /**
     * Creates an Allocator for arrays of the given class.
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

    public static <T> void checkBounds(T[] values,
                                       int index)
    {
        if (index < 0 || index >= values.length) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
    }
}
