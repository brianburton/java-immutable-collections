package org.javimmutable.collections.btree_list;

import junit.framework.TestCase;

public class BtreeNodeBuilderTest
    extends TestCase
{
    public void testRebuild()
    {
        JImmutableBtreeList<Integer> tree = JImmutableBtreeList.of();
        for (int i = 1; i <= 6000; ++i) {
            tree = tree.insertLast(i);
        }
        BtreeNodeBuilder<Integer> builder = new BtreeNodeBuilder<>();
        builder.rebuild(tree.root());
        builder.checkInvariants();
        assertEquals(tree.root(), builder.build());
    }
}
