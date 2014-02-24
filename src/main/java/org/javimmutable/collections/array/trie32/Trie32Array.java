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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.array.bit32.SingleBit32Array;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;

/**
 * Sparse array implementation that stores values of type T indexed by 32 bit indexes.
 * The indexes can be any integer.  Positive and negative indexes are permissible.
 * Indexes do not have to be contiguous.  Cursors traverse values in the order of the
 * unsigned 32 bit index values.  This means that negative indexes, since their unsigned
 * bit pattern has the high order bit set, always appear after all positive integers and
 * also appear in opposite of their signed index values.  For this reason if Cursor
 * order is important to your algorithm you should restrict yourself to using only
 * non-negative integer values.
 * <p/>
 * Internally values are stored in nested Bit32Arrays in the form of a trie.  The root of the
 * trie has up to 4 arrays (high order 2 bits), each of the other levels can have up to 32
 * arrays (5 bits).  These indexes are obtained by shifting the user provided index by some
 * number of bits (30, 25, 20, 15, 10, 5) and then masking off all but the bottom 5 bits.
 * <p/>
 * At the bottom of the tree (shift of zero bits) the leaves are the values themselves.
 * The java generics system does not lend itself to this sort of nested array structure
 * so the class uses casts to extract the arrays or values as needed based on the shift level.
 * This is safe since the objects stored at each level are carefully managed.
 *
 * @param <T>
 */
public class Trie32Array<T>
        implements Indexed<T>,
                   Cursorable<T>
{
    private static final Bit32Array<Object> EMPTY_ARRAY = Bit32Array.of();
    private static final Trie32Array<Object> EMPTY = new Trie32Array<Object>(EMPTY_ARRAY, 0);

    private final Bit32Array<Object> root;
    private final int size;

    public static final int MAX_INDEXED_CONSTRUCTOR_SIZE = 32 * 32;

    private Trie32Array(Bit32Array<Object> root,
                        int size)
    {
        this.root = root;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> Trie32Array<T> of()
    {
        return (Trie32Array<T>)EMPTY;
    }

    @SuppressWarnings("unchecked")
    public static <T> Trie32Array<T> of(Indexed<T> tsource,
                                        int offset,
                                        int limit)
    {
        Indexed<Object> source = (Indexed<Object>)tsource;
        final int size = limit - offset;
        if (size == 0) {
            return of();
        }

        // small lists can be directly constructed from a single leaf array
        if (size <= 32) {
            return new Trie32Array<T>(addParentLevels(5, Bit32Array.of(source, 0, offset, limit)), size);
        }

        // first construct an array containing a single level of arrays of leaves
        int added = 0;
        final int numBranches = Math.min(32, (limit - offset + 31) / 32);
        final Bit32Array<Object>[] branchArray = (Bit32Array<Object>[])new Bit32Array[numBranches];
        for (int b = 0; b < numBranches; ++b) {
            int branchSize = Math.min(32, limit - offset);
            int branchLimit = offset + branchSize;
            branchArray[b] = Bit32Array.of(source, 0, offset, branchLimit);
            offset = branchLimit;
            added += branchSize;
        }
        Bit32Array<Object> root = Bit32Array.of(IndexedArray.<Object>retained(branchArray), 0, 0, numBranches);

        // then add any extras left over above that size
        Trie32Array<T> array = new Trie32Array<T>(addParentLevels(10, root), added);
        while (offset < limit) {
            array = array.assign(added, tsource.get(offset));
            offset += 1;
            added += 1;
        }
        return array;
    }

    //    @SuppressWarnings("unchecked")
//    public static <T> Trie32Array<T> of(Indexed<T> source,
//                                        int offset,
//                                        int limit)
//    {
//        final int size = limit - offset;
//        if (size == 0) {
//            return of();
//        }
//
//        // small lists can be directly constructed from a single leaf array
//        if (size <= 32) {
//            return new Trie32Array<T>(addParentLevels(5, Bit32Array.of((Indexed<Object>)source, 0, offset, limit)), size);
//        }
//
//        // first construct an array containing a single level of arrays of leaves
//        final int loopLimit = Math.min(limit, offset + MAX_INDEXED_CONSTRUCTOR_SIZE);
//        int added = 0;
//        int index = 0;
//        Bit32Array<Object> root = Bit32Array.of();
//        while (offset < loopLimit) {
//            int blockLimit = Math.min(limit, offset + 32);
//            Bit32Array<Object> child = Bit32Array.of((Indexed<Object>)source, 0, offset, blockLimit);
//            root = root.assign(index, child);
//            added += (blockLimit - offset);
//            index += 1;
//            offset = blockLimit;
//        }
//
//        // then add any extras left over above that size
//        Trie32Array<T> array = new Trie32Array<T>(addParentLevels(10, root), added);
//        while (offset < limit) {
//            array = array.assign(added, source.get(offset));
//            offset += 1;
//            added += 1;
//        }
//        return array;
//    }
//
    public static <T> Trie32Array<T> of(Indexed<T> source)
    {
        return of(source, 0, source.size());
    }

    public Trie32Array<T> assign(int index,
                                 T value)
    {
        MutableDelta delta = new MutableDelta();
        Bit32Array<Object> newRoot = assign(root, index, 30, value, delta);
        return (newRoot == root) ? this : new Trie32Array<T>(newRoot, size + delta.getValue());
    }

    public Trie32Array<T> delete(int index)
    {
        MutableDelta delta = new MutableDelta();
        Bit32Array<Object> newRoot = delete(root, index, 30, delta);
        return (newRoot == root) ? this : new Trie32Array<T>(newRoot, size + delta.getValue());
    }

    @Override
    public T get(int index)
    {
        return Trie32Array.<T>find(root, index, 30).getValueOrNull();
    }

    public Holder<T> find(int index)
    {
        return Trie32Array.find(root, index, 30);
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public Cursor<T> cursor()
    {
        return MultiTransformCursor.of(root.valuesCursor(), new CursorTransforminator(30));
    }

    @SuppressWarnings("unchecked")
    static <T> Holder<T> find(Bit32Array<Object> array,
                              int index,
                              int shift)
    {
        if (shift == 0) {
            final int childIndex = index & 0x1f;
            return (Holder<T>)array.get(childIndex);
        } else {
            final int childIndex = (index >>> shift) & 0x1f;
            final Bit32Array<Object> childArray = (Bit32Array<Object>)array.get(childIndex).getValueOr(EMPTY_ARRAY);
            return find(childArray, index, shift - 5);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Bit32Array<Object> assign(Bit32Array<Object> array,
                                         int index,
                                         int shift,
                                         T value,
                                         MutableDelta delta)
    {
        if (shift == 0) {
            final int childIndex = index & 0x1f;
            Bit32Array<Object> newArray = array.assign(childIndex, value);
            delta.add(newArray.size() - array.size());
            return newArray;
        } else {
            final int childIndex = (index >>> shift) & 0x1f;
            final Bit32Array<Object> oldChildArray = (Bit32Array<Object>)array.get(childIndex).getValueOr(EMPTY_ARRAY);
            final Bit32Array<Object> newChildArray = assign(oldChildArray, index, shift - 5, value, delta);
            return (oldChildArray == newChildArray) ? array : array.assign(childIndex, newChildArray);
        }
    }

    @SuppressWarnings("unchecked")
    static Bit32Array<Object> delete(Bit32Array<Object> array,
                                     int index,
                                     int shift,
                                     MutableDelta delta)
    {
        if (shift == 0) {
            final int childIndex = index & 0x1f;
            Bit32Array<Object> newArray = array.delete(childIndex);
            delta.add(newArray.size() - array.size());
            return newArray;
        } else {
            final int childIndex = (index >>> shift) & 0x1f;
            final Bit32Array<Object> oldChildArray = (Bit32Array<Object>)array.get(childIndex).getValueOr(null);
            if (oldChildArray == null) {
                return array;
            } else {
                final Bit32Array<Object> newChildArray = delete(oldChildArray, index, shift - 5, delta);
                if (oldChildArray == newChildArray) {
                    return array;
                } else if (newChildArray.size() == 0) {
                    return array.delete(childIndex);
                } else {
                    return array.assign(childIndex, newChildArray);
                }
            }
        }
    }

    /**
     * Transforminator (BEHOLD!!) that takes a Cursor of array (if shift > 0) or leaf (if shift == 0)
     * objects and returns a Cursor of the values stored in the children (if shift > 0)
     * or in the leaves (if shift == 0).
     */
    private class CursorTransforminator
            implements Func1<Object, Cursor<T>>
    {
        private final int shift;

        private CursorTransforminator(int shift)
        {
            this.shift = shift;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Cursor<T> apply(Object arrayValue)
        {
            if (shift > 0) {
                // the internal arrays contain other arrays as values
                Bit32Array<Object> array = (Bit32Array<Object>)arrayValue;
                return MultiTransformCursor.of(array.valuesCursor(), new CursorTransforminator(shift - 5));
            } else {
                // the leaf arrays contain value objects as values
                return SingleValueCursor.of((T)arrayValue);
            }
        }
    }

    // adds interior single value arrays for prebuilt leaves
    private static Bit32Array<Object> addParentLevels(int shift,
                                                      Bit32Array<Object> child)
    {
        Bit32Array<Object> answer = child;
        while (shift <= 30) {
            answer = new SingleBit32Array<Object>(0, answer);
            shift += 5;
        }
        return answer;
    }
}
