package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.common.MutableDelta;

public class EmptyNodeTest
        extends TestCase
{
    public void testVarious()
    {
        final TreeNode<String, String> empty = EmptyNode.of();
        MutableDelta delta = new MutableDelta();
        assertEquals(new LeafNode<String, String>("a", "A"), empty.assign(ComparableComparator.<String>of(), "a", "A", delta));
        assertEquals(1, delta.getValue());

        delta = new MutableDelta();
        assertEquals(empty, empty.delete(ComparableComparator.<String>of(), "a", delta));
        assertEquals(0, delta.getValue());

        assertEquals(true, empty.isEmpty());
    }
}
