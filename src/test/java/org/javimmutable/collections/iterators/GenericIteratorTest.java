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

package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.IStreamable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.iterators.GenericIterator.MIN_SIZE_FOR_SPLIT;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

public class GenericIteratorTest
    extends TestCase
{
    public void test()
    {
        eq(asList(), nr(1, 0));
        eq(asList(1), n(1));
        eq(asList(1, 2), n(n(1, 2)));
        eq(asList(1, 2, 3), n(n(1, 2, 3)));
        final Node deep = n(n(1), n(2), n(3), n(n(4), n(5), n(6, 7), n(8, 9)), n(n(n(10), n(11), n(12, 13)), n(14, 15)));
        final List<Integer> expected = asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        eq(expected, deep);
        assertEquals(expected, deep.stream().collect(Collectors.toList()));
        assertEquals(expected, deep.stream().parallel().collect(Collectors.toList()));
        assertEquals(expected, deep.parallelStream().collect(Collectors.toList()));
        assertEquals(lr(1, limit(4)), nr(1, limit(4)).stream().parallel().collect(Collectors.toList()));
        assertEquals(lr(1, limit(4)), nr(1, limit(4)).parallelStream().collect(Collectors.toList()));

        final Transformed transformed = new Transformed(deep);
        eq(expected, transformed);
        assertEquals(expected, transformed.stream().map(integers4 -> integers4.unsafeGet()).collect(Collectors.toList()));
        assertEquals(expected, transformed.stream().parallel().map(integers3 -> integers3.unsafeGet()).collect(Collectors.toList()));
        assertEquals(expected, transformed.parallelStream().map(integers2 -> integers2.unsafeGet()).collect(Collectors.toList()));
        assertEquals(lr(1, limit(4)), new Transformed(nr(1, limit(4))).stream().parallel().map(integers1 -> integers1.unsafeGet()).collect(Collectors.toList()));
        assertEquals(lr(1, limit(4)), new Transformed(nr(1, limit(4))).parallelStream().map(integers -> integers.unsafeGet()).collect(Collectors.toList()));
    }

    public void testStandard()
    {
        verifyOrderedIterable(asList(1, 2), n(1, 2));
        verifyOrderedIterable(lr(1, limit(4)), nr(1, limit(4)));
        verifyOrderedSplit(false, asList(), asList(), nr(1, MIN_SIZE_FOR_SPLIT - 1).iterator());
        verifyOrderedSplit(true, lr(1, limit(1)), lr(limit(1) + 1, limit(2)), rg(1, limit(2)).iterator());
        final int len = MIN_SIZE_FOR_SPLIT + MIN_SIZE_FOR_SPLIT / 2 - 1;
        final Node deep = n(nr(1, 10), n(rg(11, len / 2), n(nr(len / 2 + 1, len / 2 + 2)), nr(len / 2 + 3, len)));
        eq(lr(1, len), deep);
        verifyOrderedIterable(lr(1, len), deep);
        verifyOrderedSplit(true, lr(1, len / 2), lr(len / 2 + 1, len), deep.iterator());
    }

    private int limit(int multiple)
    {
        return multiple * MIN_SIZE_FOR_SPLIT;
    }

    private void eq(List<Integer> expected,
                    Node actual)
    {
        List<Integer> list = new ArrayList<>();
        for (Integer integer : actual) {
            list.add(integer);
        }
        assertEquals(expected, list);
    }

    private void eq(List<Integer> expected,
                    Transformed actual)
    {
        List<Integer> list = new ArrayList<>();
        for (Maybe<Integer> integer : actual) {
            list.add(integer.unsafeGet());
        }
        assertEquals(expected, list);
    }

    private static Node n(int value)
    {
        return new Leaf(value);
    }

    private static Node n(int... values)
    {
        Node[] nodes = new Node[values.length];
        for (int i = 0; i < values.length; ++i) {
            nodes[i] = n(values[i]);
        }
        return new Branch(nodes);
    }

    private static Node n(Node... nodes)
    {
        return new Branch(nodes);
    }

    private static Node nr(int first,
                           int last)
    {
        Node[] nodes = new Node[last - first + 1];
        int i = 0;
        for (int value = first; value <= last; ++value) {
            nodes[i++] = n(value);
        }
        return new Branch(nodes);
    }

    private static Node rg(int first,
                           int last)
    {
        return new Range(first, last);
    }

    private static List<Integer> lr(int first,
                                    int last)
    {
        List<Integer> list = new ArrayList<>(last - first + 1);
        for (int i = first; i <= last; ++i) {
            list.add(i);
        }
        return list;
    }

    private static abstract class Node
        implements GenericIterator.Iterable<Integer>,
                   IStreamable<Integer>

    {
        @Nonnull
        @Override
        public SplitableIterator<Integer> iterator()
        {
            return GenericIterator.Iterable.super.iterator();
        }

        @Override
        public int getSpliteratorCharacteristics()
        {
            return StreamConstants.SPLITERATOR_UNORDERED;
        }
    }

    private static class Leaf
        extends Node
    {
        private final Integer value;

        private Leaf(Integer value)
        {
            this.value = value;
        }

        @Override
        public int iterableSize()
        {
            return 1;
        }

        @Nullable
        @Override
        public GenericIterator.State<Integer> iterateOverRange(@Nullable GenericIterator.State<Integer> parent,
                                                               int offset,
                                                               int limit)
        {
            return GenericIterator.singleValueState(parent, value);
        }
    }

    private static class Range
        extends Node
    {
        private final Indexed<Integer> values;

        private Range(int first,
                      int last)
        {
            values = IndexedHelper.range(first, last);
        }

        @Override
        public int iterableSize()
        {
            return values.size();
        }

        @Nullable
        @Override
        public GenericIterator.State<Integer> iterateOverRange(@Nullable GenericIterator.State<Integer> parent,
                                                               int offset,
                                                               int limit)
        {
            return GenericIterator.multiValueState(parent, values, offset, limit);
        }
    }

    private static class Branch
        extends Node
    {
        private final Node[] nodes;
        private final int size;

        private Branch(Node... nodes)
        {
            int size = 0;
            for (Node node : nodes) {
                size += node.iterableSize();
            }
            this.nodes = nodes;
            this.size = size;
        }

        @Override
        public int iterableSize()
        {
            return size;
        }

        @Nullable
        @Override
        public GenericIterator.State<Integer> iterateOverRange(@Nullable GenericIterator.State<Integer> parent,
                                                               int offset,
                                                               int limit)
        {
            return GenericIterator.multiIterableState(parent, IndexedArray.retained(nodes), offset, limit);
        }
    }

    private static class Transformed
        implements GenericIterator.Iterable<Maybe<Integer>>,
                   IStreamable<Maybe<Integer>>
    {
        private final Node node;

        private Transformed(Node node)
        {
            this.node = node;
        }

        @Nonnull
        @Override
        public SplitableIterator<Maybe<Integer>> iterator()
        {
            return GenericIterator.Iterable.super.iterator();
        }

        @Override
        public int getSpliteratorCharacteristics()
        {
            return StreamConstants.SPLITERATOR_UNORDERED;
        }

        @Nullable
        @Override
        public GenericIterator.State<Maybe<Integer>> iterateOverRange(@Nullable GenericIterator.State<Maybe<Integer>> parent,
                                                                      int offset,
                                                                      int limit)
        {
            return GenericIterator.transformState(parent, node.iterateOverRange(null, offset, limit), i -> Maybe.present(i));
        }

        @Override
        public int iterableSize()
        {
            return node.iterableSize();
        }
    }
}
