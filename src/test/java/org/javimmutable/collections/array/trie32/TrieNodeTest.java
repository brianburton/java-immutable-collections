package org.javimmutable.collections.array.trie32;

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

        for (int i = 30; i < 32; ++i) {
            assertEquals(30, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 25; i < 30; ++i) {
            assertEquals(25, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 20; i < 25; ++i) {
            assertEquals(20, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 15; i < 20; ++i) {
            assertEquals(15, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 10; i < 15; ++i) {
            assertEquals(10, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 5; i < 10; ++i) {
            assertEquals(5, TrieNode.shiftForIndex(1 << i));
        }
        for (int i = 0; i < 5; ++i) {
            assertEquals(0, TrieNode.shiftForIndex(1 << i));
        }
    }
}
