package org.javimmutable.collections.tree;

import junit.framework.TestCase;

public class BranchNodeTest
    extends TestCase
{
    public void testDelete()
    {
        LeafNode<Integer, String> a = new LeafNode<>(1, "a");
        LeafNode<Integer, String> b = new LeafNode<>(2, "b");
        BranchNode<Integer, String> branch = new BranchNode<>(a, b);
        final ComparableComparator<Integer> comparator = ComparableComparator.of();
        Node<Integer, String> d1 = branch.delete(comparator, 1);
        Node<Integer, String> d2 = d1.delete(comparator, 2);
    }
}
