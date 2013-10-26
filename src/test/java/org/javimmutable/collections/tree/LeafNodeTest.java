package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LeafNodeTest
        extends TestCase
{
    private final Comparator<Integer> comparator = ComparableComparator.of();
    private final LeafNode<Integer, Integer> node = new LeafNode<Integer, Integer>(10, 20);

    public void testVarious()
    {
        assertEquals(Integer.valueOf(10), node.getKey());
        assertEquals(Integer.valueOf(20), node.getValue());
        assertEquals(false, node.isEmpty());
        assertEquals(true, node.isFilled());
        assertEquals(Integer.valueOf(20), node.getValueOrNull());
        assertEquals(Integer.valueOf(20), node.getValueOr(12));
        assertEquals(Holders.<Integer>of(), node.find(comparator, 11));
        assertSame(node, node.find(comparator, 10));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), node.findEntry(comparator, 11));
        assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(node), node.findEntry(comparator, 10));
        assertEquals(Integer.valueOf(10), node.getMaxKey());
        List<JImmutableMap.Entry<Integer, Integer>> values = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        node.addEntriesTo(values);
        //noinspection unchecked
        assertEquals(Arrays.<JImmutableMap.Entry<Integer, Integer>>asList(node), values);
        assertEquals(1, node.verifyDepthsMatch());
    }

    public void testUpdate()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.update(comparator, 10, 20));
        assertEquals(UpdateResult.createInPlace(new LeafNode<Integer, Integer>(10, 18), 0), node.update(comparator, 10, 18));
        assertEquals(UpdateResult.createSplit(new LeafNode<Integer, Integer>(8, 16), node, 1), node.update(comparator, 8, 16));
        assertEquals(UpdateResult.createSplit(node, new LeafNode<Integer, Integer>(12, 24), 1), node.update(comparator, 12, 24));
    }

    public void testDelete()
    {
        assertEquals(DeleteResult.<Integer, Integer>createEliminated(), node.delete(comparator, 10));
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.delete(comparator, 11));
    }

    public void testLeftDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8), node, 8, 10)),
                     node.leftDeleteMerge(comparator, new LeafNode<Integer, Integer>(8, 8)));
    }

    public void testRightDeleteMerge()
    {
        assertEquals(new DeleteMergeResult<Integer, Integer>(new TwoNode<Integer, Integer>(node, new LeafNode<Integer, Integer>(12, 18), 10, 12)),
                     node.rightDeleteMerge(comparator, new LeafNode<Integer, Integer>(12, 18)));
    }
}
