package org.javimmutable.collections.array;

import junit.framework.TestCase;

import static java.lang.Integer.*;

public class TrieArrayNodeTest
    extends TestCase
{
    public void testIndexMath()
    {
        assertEquals(0, TrieArrayNode.nodeIndex(MIN_VALUE));
        assertEquals(MAX_VALUE, TrieArrayNode.nodeIndex(-1));
        assertEquals(0, TrieArrayNode.nodeIndex(0));
        assertEquals(MAX_VALUE, TrieArrayNode.nodeIndex(MAX_VALUE));

        assertEquals(MIN_VALUE, TrieArrayNode.NEGATIVE_BASE_INDEX + TrieArrayNode.nodeIndex(MIN_VALUE));
        assertEquals(-1, TrieArrayNode.NEGATIVE_BASE_INDEX + TrieArrayNode.nodeIndex(-1));
        assertEquals(0, TrieArrayNode.POSITIVE_BASE_INDEX + TrieArrayNode.nodeIndex(0));
        assertEquals(MAX_VALUE, TrieArrayNode.POSITIVE_BASE_INDEX + TrieArrayNode.nodeIndex(MAX_VALUE));
    }
}
