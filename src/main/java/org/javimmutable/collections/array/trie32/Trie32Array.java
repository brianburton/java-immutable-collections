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
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.common.AbstractJImmutableArray;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.cursors.StandardCursor;

/**
 * Sparse array implementation that stores values of type T indexed by 32 bit indexes.
 * The indexes can be any integer.  Positive and negative indexes are permissible.
 * Indexes do not have to be contiguous.  Cursors traverse values in the order as signed
 * integers so they will be visited in order from -MAX_INT to MAX_INT.
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
        extends AbstractJImmutableArray<T>
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
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        if (root.size() == 0) {
            return StandardCursor.of();
        } else {
            return MultiTransformCursor.of(StandardCursor.of(new RootEntryCursor()), new EntryCursorTransforminator(30, 0));
        }
    }

    @Override
    public Cursor<T> valuesCursor()
    {
        if (root.size() == 0) {
            return StandardCursor.of();
        } else {
            return MultiTransformCursor.of(StandardCursor.of(new RootValueCursor()), new ValueCursorTransforminator(30));
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Holder<T> find(Bit32Array<Object> array,
                              int index,
                              int shift)
    {
        if (shift == 0) {
            final int childIndex = index & 0x1f;
            return (Holder<T>)array.find(childIndex);
        } else {
            final int childIndex = (index >>> shift) & 0x1f;
            final Bit32Array<Object> childArray = (Bit32Array<Object>)array.find(childIndex).getValueOr(EMPTY_ARRAY);
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
            final Bit32Array<Object> oldChildArray = (Bit32Array<Object>)array.find(childIndex).getValueOr(EMPTY_ARRAY);
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
            final Bit32Array<Object> oldChildArray = (Bit32Array<Object>)array.find(childIndex).getValueOr(null);
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


    // adds interior single value arrays for prebuilt leaves
    private static Bit32Array<Object> addParentLevels(int shift,
                                                      Bit32Array<Object> child)
    {
        Bit32Array<Object> answer = child;
        while (shift <= 30) {
            answer = Bit32Array.<Object>of(0, answer);
            shift += 5;
        }
        return answer;
    }

    private static int firstFilledIndex(Bit32Array<Object> root,
                                        int index)
    {
        int newIndex = index;
        while (newIndex >= 0 && root.find(newIndex).isEmpty()) {
            newIndex = nextIndex(newIndex);
        }
        return newIndex;
    }

    private static int nextIndex(int index)
    {
        switch (index) {
        case 2:
            return 3;
        case 3:
            return 0;
        case 0:
            return 1;
        case 1:
            return -1;
        default:
            throw new IllegalArgumentException(String.format("unexpected index %d", index));
        }
    }

    private class RootValueCursor
            implements StandardCursor.Source<Object>
    {
        private final int index;

        private RootValueCursor()
        {
            this(firstFilledIndex(root, 2));
        }

        private RootValueCursor(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index < 0;
        }

        @Override
        public Object currentValue()
        {
            return root.get(index);
        }

        @Override
        public StandardCursor.Source<Object> advance()
        {
            int newIndex = firstFilledIndex(root, nextIndex(index));
            return new RootValueCursor(newIndex);
        }
    }

    private class RootEntryCursor
            implements StandardCursor.Source<JImmutableMap.Entry<Integer, Object>>
    {
        private final int index;

        private RootEntryCursor()
        {
            this(firstFilledIndex(root, 2));
        }

        private RootEntryCursor(int index)
        {
            this.index = index;
        }

        @Override
        public boolean atEnd()
        {
            return index < 0;
        }

        @Override
        public JImmutableMap.Entry<Integer, Object> currentValue()
        {
            return new MapEntry<Integer, Object>(index, root.find(index).getValue());
        }

        @Override
        public StandardCursor.Source<JImmutableMap.Entry<Integer, Object>> advance()
        {
            int newIndex = firstFilledIndex(root, nextIndex(index));
            return new RootEntryCursor(newIndex);
        }
    }

    /**
     * Transforminator (BEHOLD!!) that takes a Cursor of array (if shift > 0) or leaf (if shift == 0)
     * objects and returns a Cursor of the values stored in the children (if shift > 0)
     * or in the leaves (if shift == 0).
     */
    private class ValueCursorTransforminator
            implements Func1<Object, Cursor<T>>
    {
        private final int shift;

        private ValueCursorTransforminator(int shift)
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
                return MultiTransformCursor.of(array.valuesCursor(), new ValueCursorTransforminator(shift - 5));
            } else {
                // the leaf arrays contain value objects as values
                return SingleValueCursor.of((T)arrayValue);
            }
        }
    }

    /**
     * Transforminator (BEHOLD!!) that takes a Cursor of array (if shift > 0) or leaf entries (if shift == 0)
     * and returns a Cursor of entries with the value index and value stored in the children (if shift > 0)
     * or in the leaves (if shift == 0).
     */
    private class EntryCursorTransforminator
            implements Func1<JImmutableMap.Entry<Integer, Object>, Cursor<JImmutableMap.Entry<Integer, T>>>
    {
        private final int shift;
        private final int baseIndex;

        private EntryCursorTransforminator(int shift,
                                           int baseIndex)
        {
            this.shift = shift;
            this.baseIndex = baseIndex;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Cursor<JImmutableMap.Entry<Integer, T>> apply(JImmutableMap.Entry<Integer, Object> arrayEntry)
        {
            int index = (baseIndex << 5) | (arrayEntry.getKey());
            if (shift > 0) {
                // the internal arrays contain other arrays as values
                Bit32Array<Object> array = (Bit32Array<Object>)arrayEntry.getValue();
                return MultiTransformCursor.of(array.cursor(), new EntryCursorTransforminator(shift - 5, index));
            } else {
                // the leaf arrays contain value objects as values
                return SingleValueCursor.<JImmutableMap.Entry<Integer, T>>of(new MapEntry<Integer, T>(index, (T)arrayEntry.getValue()));
            }
        }
    }
}
