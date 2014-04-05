package org.javimmutable.collections.array.trie32;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.common.MutableDelta;

public class SingleBranchTrieNodeTest
        extends TestCase
{
    public void testConstructors()
    {
        LeafTrieNode<String> child = LeafTrieNode.of(30 << 20, "value");
        SingleBranchTrieNode<String> node = SingleBranchTrieNode.forBranchIndex(20, 30, child);
        assertEquals(20, node.getShift());
        assertEquals(30, node.getBranchIndex());
        assertSame(child, node.getChild());

        node = SingleBranchTrieNode.forIndex(20, 18 << 20, child);
        assertEquals(20, node.getShift());
        assertEquals(18, node.getBranchIndex());
        assertSame(child, node.getChild());
    }

    public void testNormal()
    {
        LeafTrieNode<String> child = LeafTrieNode.of(30 << 20, "value");
        SingleBranchTrieNode<String> node = SingleBranchTrieNode.forBranchIndex(20, 30, child);
        assertEquals(null, node.getValueOr(20, 31 << 20, null));
        assertEquals("value", node.getValueOr(20, 30 << 20, null));
        assertEquals(Holders.<String>of(), node.find(20, 31 << 20));
        assertEquals(Holders.of("value"), node.find(20, 30 << 20));

        MutableDelta delta = new MutableDelta();
        assertSame(node, node.assign(20, 30 << 20, "value", delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        assertSame(node, node.delete(20, 18 << 20, delta));
        assertEquals(0, delta.getValue());

        delta = new MutableDelta();
        assertSame(EmptyTrieNode.of(), node.delete(20, 30 << 20, delta));
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        TrieNode<String> newNode = node.assign(20, 18 << 20, "18", delta);
        assertEquals(1, delta.getValue());
        assertTrue(newNode instanceof MultiBranchTrieNode);
        assertEquals("value", newNode.getValueOr(20, 30 << 20, null));
        assertEquals("18", newNode.getValueOr(20, 18 << 20, null));

        delta = new MutableDelta();
        newNode = newNode.delete(20, 18 << 20, delta);
        assertTrue(newNode instanceof LeafTrieNode);
        assertEquals(-1, delta.getValue());

        delta = new MutableDelta();
        assertSame(EmptyTrieNode.of(), newNode.delete(20, 30 << 20, delta));
        assertEquals(-1, delta.getValue());
    }
}
