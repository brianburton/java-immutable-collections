package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.IterableStreamable;
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
        implements IterableStreamable<Integer>,
                   GenericIterator.Iterable<Integer>
    {
        abstract int size();

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
        int size()
        {
            return 1;
        }

        @Nonnull
        @Override
        public SplitableIterator<Integer> iterator()
        {
            return new GenericIterator<>(this, 0, 1);
        }

        @Nullable
        @Override
        public GenericIterator.State<Integer> iterateOverRange(@Nullable GenericIterator.State<Integer> parent,
                                                               int offset,
                                                               int limit)
        {
            return GenericIterator.valueState(parent, value);
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
        int size()
        {
            return values.size();
        }

        @Nonnull
        @Override
        public SplitableIterator<Integer> iterator()
        {
            return new GenericIterator<>(this, 0, size());
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
                size += node.size();
            }
            this.nodes = nodes;
            this.size = size;
        }

        @Override
        int size()
        {
            return size;
        }

        @Nonnull
        @Override
        public SplitableIterator<Integer> iterator()
        {
            return new GenericIterator<>(this, 0, size);
        }

        @Nullable
        @Override
        public GenericIterator.State<Integer> iterateOverRange(@Nullable GenericIterator.State<Integer> parent,
                                                               int offset,
                                                               int limit)
        {
            return GenericIterator.indexedState(parent, IndexedArray.retained(nodes), Node::size, offset, limit);
        }
    }
}
