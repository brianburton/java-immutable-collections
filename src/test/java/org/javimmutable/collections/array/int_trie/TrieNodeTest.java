package org.javimmutable.collections.array.int_trie;

import junit.framework.TestCase;
import org.javimmutable.collections.common.MutableDelta;

public class TrieNodeTest
        extends TestCase
{

    public void testVarious()
    {
        TrieNode<Integer> root = EmptyTrieNode.of();
        assertEquals((Integer)1, root.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));

        MutableDelta delta = new MutableDelta();
        TrieNode<Integer> node = root.assign(TrieNode.ROOT_SHIFT, 87, 88, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals(1, delta.getValue());

        delta = new MutableDelta();
        assertSame(node, node.assign(TrieNode.ROOT_SHIFT, 87, 88, delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        node = node.assign(TrieNode.ROOT_SHIFT, -45, 46, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)46, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(1, delta.getValue());

        delta = new MutableDelta();
        node = node.assign(TrieNode.ROOT_SHIFT, -45, 47, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)47, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        node = node.delete(TrieNode.ROOT_SHIFT, -300, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)47, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        node = node.delete(TrieNode.ROOT_SHIFT, -45, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)1, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        node = node.assign(TrieNode.ROOT_SHIFT, -45, 47, delta);
        assertEquals((Integer)88, node.getValueOr(TrieNode.ROOT_SHIFT, 87, 1));
        assertEquals((Integer)47, node.getValueOr(TrieNode.ROOT_SHIFT, -45, 1));
        assertEquals(1, delta.getValue());
    }
}
