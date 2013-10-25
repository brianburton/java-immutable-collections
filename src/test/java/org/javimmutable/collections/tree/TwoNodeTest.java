package org.javimmutable.collections.tree;

import junit.framework.TestCase;

import java.util.Comparator;

public class TwoNodeTest
        extends TestCase
{
    private Comparator<Integer> comparator;
    private TwoNode<Integer, Integer> node;

    @Override
    public void setUp()
            throws Exception
    {
        super.setUp();
        comparator = ComparableComparator.of();
        node = new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                             new LeafNode<Integer, Integer>(12, 12),
                                             10,
                                             12);
    }

    @Override
    public void tearDown()
            throws Exception
    {
        comparator = null;
        node = null;
        super.tearDown();
    }

    public void testUpdateLeftUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.update(comparator, 10, 10));
    }

    public void testUpdateLeftInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 20),
                                                                         node.getRight(),
                                                                         10,
                                                                         node.getRightMaxKey()),
                                           0);
        assertEquals(expected, node.update(comparator, 10, 20));
    }

    public void testUpdateLeftSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                           node.getLeft(),
                                                                           node.getRight(),
                                                                           8,
                                                                           node.getLeftMaxKey(),
                                                                           node.getRightMaxKey()),
                                           1);
        assertEquals(expected, node.update(comparator, 8, 8));
    }

    public void testUpdateRightUnchanged()
    {
        assertEquals(UpdateResult.<Integer, Integer>createUnchanged(), node.update(comparator, 12, 12));
    }

    public void testUpdateRightInPlace()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new TwoNode<Integer, Integer>(node.getLeft(),
                                                                         new LeafNode<Integer, Integer>(12, 20),
                                                                         node.getLeftMaxKey(),
                                                                         12),
                                           0);
        assertEquals(expected, node.update(comparator, 12, 20));
    }

    public void testUpdateRightSplit()
    {
        UpdateResult<Integer, Integer> expected =
                UpdateResult.createInPlace(new ThreeNode<Integer, Integer>(node.getLeft(),
                                                                           node.getRight(),
                                                                           new LeafNode<Integer, Integer>(14, 14),
                                                                           node.getLeftMaxKey(),
                                                                           node.getRightMaxKey(),
                                                                           14),
                                           1);
        assertEquals(expected, node.update(comparator, 14, 14));
    }

    public void testDeleteLeftUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.delete(comparator, 8));
    }

    public void testDeleteLeftInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                           new LeafNode<Integer, Integer>(9, 9),
                                                                                                           new LeafNode<Integer, Integer>(10, 10),
                                                                                                           8,
                                                                                                           9,
                                                                                                           10),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                         11,
                                                                                                         12),
                                                                           10,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         8,
                                                                                                                                         10),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                         11,
                                                                                                                                         12),
                                                                                                           10,
                                                                                                           12));
        assertEquals(expected, testNode.delete(comparator, 9));
    }

    public void testLeftEliminated()
    {
        assertEquals(DeleteResult.createRemnant(node.getRight()), node.delete(comparator, 10));
    }

    public void testDeleteLeftRemnant()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                         8,
                                                                                                         10),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                         11,
                                                                                                         12),
                                                                           10,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createRemnant(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(11, 11),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             8,
                                                                                                             11,
                                                                                                             12));
        assertEquals(expected, testNode.delete(comparator, 10));
    }

    public void testDeleteLeftRemnantInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                         8,
                                                                                                         10),
                                                                           new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                           new LeafNode<Integer, Integer>(13, 13),
                                                                                                           11,
                                                                                                           12,
                                                                                                           13),
                                                                           10,
                                                                           13);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         10,
                                                                                                                                         11),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                         new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                         12,
                                                                                                                                         13),
                                                                                                           11,
                                                                                                           13));
        assertEquals(expected, testNode.delete(comparator, 8));
    }

//

    public void testDeleteRightUnchanged()
    {
        assertEquals(DeleteResult.<Integer, Integer>createUnchanged(), node.delete(comparator, 14));
    }

    public void testDeleteRightInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(9, 9),
                                                                                                         8,
                                                                                                         9),
                                                                           new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(10, 10),
                                                                                                           new LeafNode<Integer, Integer>(11, 11),
                                                                                                           new LeafNode<Integer, Integer>(12, 12),
                                                                                                           10,
                                                                                                           11,
                                                                                                           12),
                                                                           9,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(9, 9),
                                                                                                                                         8,
                                                                                                                                         9),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                                                         11,
                                                                                                                                         12),
                                                                                                           9,
                                                                                                           12));
        assertEquals(expected, testNode.delete(comparator, 10));
    }

    public void testRightEliminated()
    {
        assertEquals(DeleteResult.createRemnant(node.getLeft()), node.delete(comparator, 12));
    }

    public void testDeleteRightRemnant()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                         8,
                                                                                                         10),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                         new LeafNode<Integer, Integer>(12, 12),
                                                                                                         11,
                                                                                                         12),
                                                                           10,
                                                                           12);
        DeleteResult<Integer, Integer> expected = DeleteResult.createRemnant(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                             new LeafNode<Integer, Integer>(10, 10),
                                                                                                             new LeafNode<Integer, Integer>(12, 12),
                                                                                                             8,
                                                                                                             10,
                                                                                                             12));
        assertEquals(expected, testNode.delete(comparator, 11));
    }

    public void testDeleteRightRemnantInPlace()
    {
        TwoNode<Integer, Integer> testNode = new TwoNode<Integer, Integer>(new ThreeNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                           new LeafNode<Integer, Integer>(10, 10),
                                                                                                           new LeafNode<Integer, Integer>(11, 11),
                                                                                                           8,
                                                                                                           10,
                                                                                                           11),
                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(12, 12),
                                                                                                         new LeafNode<Integer, Integer>(13, 13),
                                                                                                         12,
                                                                                                         13),
                                                                           11,
                                                                           13);
        DeleteResult<Integer, Integer> expected = DeleteResult.createInPlace(new TwoNode<Integer, Integer>(new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(8, 8),
                                                                                                                                         new LeafNode<Integer, Integer>(10, 10),
                                                                                                                                         8,
                                                                                                                                         10),
                                                                                                           new TwoNode<Integer, Integer>(new LeafNode<Integer, Integer>(11, 11),
                                                                                                                                         new LeafNode<Integer, Integer>(13, 13),
                                                                                                                                         11,
                                                                                                                                         13),
                                                                                                           10,
                                                                                                           13));
        assertEquals(expected, testNode.delete(comparator, 12));
    }
}
