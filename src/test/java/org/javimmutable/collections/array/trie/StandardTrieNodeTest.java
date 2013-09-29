///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.array.trie;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.hash.JImmutableHashMap;

import java.util.ArrayList;
import java.util.List;

public class StandardTrieNodeTest
        extends TestCase
{
    public void testValuesGet()
    {
        Bit32Array<String> values = Bit32Array.of();
        values = values.assign(8, "a");
        values = values.assign(15, "b");

        StandardTrieNode<String> node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), values);
        assertEquals("a", node.get(0, 8).getValueOrNull());
        assertEquals("b", node.get(0, 15).getValueOrNull());
        assertEquals(null, node.get(0, 30).getValueOrNull());
    }

    public void testBranchesGet()
    {
        Bit32Array<TrieNode<String>> branches = Bit32Array.of();
        branches = branches.assign(3, new QuickTrieNode<String>(1, 3, "a"));
        branches = branches.assign(5, new QuickTrieNode<String>(8, 7, "b"));

        StandardTrieNode<String> node = new StandardTrieNode<String>(branches, Bit32Array.<String>of());
        assertEquals("a", node.get((1 << 5) + 3, 3).getValueOrNull());
        assertEquals("b", node.get((8 << 5) + 5, 7).getValueOrNull());
        assertEquals(null, node.get((12 << 5) + 7, 18).getValueOrNull());
    }

    public void testValuesSet()
    {
        TrieNode<String> node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), Bit32Array.<String>of());

        node = node.assign(0, 8, "a");
        assertEquals(1, node.shallowSize());
        assertEquals(1, node.deepSize());
        assertEquals("a", node.get(0, 8).getValueOrNull());

        assertSame(node, node.assign(0, 8, "a"));

        node = node.assign(0, 8, "x");
        assertEquals(1, node.shallowSize());
        assertEquals(1, node.deepSize());
        assertEquals("x", node.get(0, 8).getValueOrNull());

        node = node.assign(0, 18, "q");
        assertEquals(2, node.shallowSize());
        assertEquals(2, node.deepSize());
        assertEquals("x", node.get(0, 8).getValueOrNull());
        assertEquals("q", node.get(0, 18).getValueOrNull());
    }

    public void testBranchSet()
    {
        TrieNode<String> node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), Bit32Array.<String>of());
        assertEquals(0, node.shallowSize());
        assertEquals(0, node.deepSize());
        JImmutableMap<Class, Integer> counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(0, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // add a new subbranch
        node = node.assign(1, 5, "a");
        assertEquals(1, node.shallowSize());
        assertEquals(1, node.deepSize());
        assertEquals("a", node.get(1, 5).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // add another new subbranch
        node = node.assign(7, 15, "z");
        assertEquals(2, node.shallowSize());
        assertEquals(2, node.deepSize());
        assertEquals("a", node.get(1, 5).getValueOrNull());
        assertEquals("z", node.get(7, 15).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // update a subbranch
        node = node.assign(7, 15, "r");
        assertEquals(2, node.shallowSize());
        assertEquals(2, node.deepSize());
        assertEquals("a", node.get(1, 5).getValueOrNull());
        assertEquals("r", node.get(7, 15).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // expand a subbranch
        node = node.assign(7, 8, "q");
        assertEquals(2, node.shallowSize());
        assertEquals(3, node.deepSize());
        assertEquals("a", node.get(1, 5).getValueOrNull());
        assertEquals("q", node.get(7, 8).getValueOrNull());
        assertEquals("r", node.get(7, 15).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(QuickTrieNode.class).getValueOr(0));
    }

    public void testValuesDelete()
    {
        TrieNode<String> node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), Bit32Array.<String>of());

        node = node.assign(0, 8, "x");
        node = node.assign(0, 18, "q");
        node = node.assign(0, 31, "g");
        assertEquals(3, node.shallowSize());
        assertEquals(3, node.deepSize());
        assertEquals("x", node.get(0, 8).getValueOrNull());
        assertEquals("q", node.get(0, 18).getValueOrNull());
        assertEquals("g", node.get(0, 31).getValueOrNull());

        // non-existent value
        assertSame(node, node.delete(0, 23));

        // one of many values
        node = node.delete(0, 31);
        assertTrue(node instanceof StandardTrieNode);
        assertEquals(2, node.shallowSize());
        assertEquals(2, node.deepSize());
        assertEquals("x", node.get(0, 8).getValueOrNull());
        assertEquals("q", node.get(0, 18).getValueOrNull());
        assertEquals(null, node.get(0, 31).getValueOrNull());

        // penultimate value
        node = node.delete(0, 18);
        assertTrue(node instanceof QuickTrieNode);
        assertEquals(1, node.shallowSize());
        assertEquals(1, node.deepSize());
        assertEquals("x", node.get(0, 8).getValueOrNull());
        assertEquals(null, node.get(0, 18).getValueOrNull());

        // ultimate value
        node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), Bit32Array.<String>of());
        node = node.assign(0, 8, "x");
        node = node.delete(0, 8);
        assertTrue(node instanceof EmptyTrieNode);
        assertEquals(0, node.shallowSize());
        assertEquals(0, node.deepSize());
        assertEquals(null, node.get(0, 8).getValueOrNull());
    }

    public void testBranchesDelete()
    {
        TrieNode<String> node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), Bit32Array.<String>of());
        node = node.assign(1, 5, "a");
        node = node.assign(7, 15, "r");
        node = node.assign(7, 8, "q");
        node = node.assign(12, 3, "z");
        assertEquals(3, node.shallowSize());
        assertEquals(4, node.deepSize());
        assertEquals("a", node.get(1, 5).getValueOrNull());
        assertEquals("q", node.get(7, 8).getValueOrNull());
        assertEquals("r", node.get(7, 15).getValueOrNull());
        assertEquals("z", node.get(12, 3).getValueOrNull());
        JImmutableMap<Class, Integer> counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // non-existent branch
        assertSame(node, node.delete(6, 3));

        // non-existent sub-branch value
        assertSame(node, node.delete(7, 13));

        // subbranches only value
        node = node.delete(1, 5);
        assertTrue(node instanceof StandardTrieNode);
        assertEquals(2, node.shallowSize());
        assertEquals(3, node.deepSize());
        assertEquals(null, node.get(1, 5).getValueOrNull());
        assertEquals("q", node.get(7, 8).getValueOrNull());
        assertEquals("r", node.get(7, 15).getValueOrNull());
        assertEquals("z", node.get(12, 3).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // subbranch value
        node = node.delete(7, 15);
        assertTrue(node instanceof StandardTrieNode);
        assertEquals(2, node.shallowSize());
        assertEquals(2, node.deepSize());
        assertEquals(null, node.get(1, 5).getValueOrNull());
        assertEquals("q", node.get(7, 8).getValueOrNull());
        assertEquals(null, node.get(7, 15).getValueOrNull());
        assertEquals("z", node.get(12, 3).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(2, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // penultimate value
        node = node.delete(7, 8);
        assertTrue(node instanceof StandardTrieNode);
        assertEquals(1, node.shallowSize());
        assertEquals(1, node.deepSize());
        assertEquals(null, node.get(1, 5).getValueOrNull());
        assertEquals(null, node.get(7, 8).getValueOrNull());
        assertEquals(null, node.get(7, 15).getValueOrNull());
        assertEquals("z", node.get(12, 3).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(0, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(1, (int)counts.find(QuickTrieNode.class).getValueOr(0));

        // ultimate value
        node = node.delete(12, 3);
        assertTrue(node instanceof EmptyTrieNode);
        assertEquals(0, node.shallowSize());
        assertEquals(0, node.deepSize());
        assertEquals(null, node.get(1, 5).getValueOrNull());
        assertEquals(null, node.get(7, 8).getValueOrNull());
        assertEquals(null, node.get(7, 15).getValueOrNull());
        assertEquals(null, node.get(12, 3).getValueOrNull());
        counts = node.getNodeTypeCounts(JImmutableHashMap.<Class, Integer>of());
        assertEquals(1, (int)counts.find(EmptyTrieNode.class).getValueOr(0));
        assertEquals(0, (int)counts.find(StandardTrieNode.class).getValueOr(0));
        assertEquals(0, (int)counts.find(QuickTrieNode.class).getValueOr(0));
    }

    public void testCursor()
    {
        TrieNode<String> node = new StandardTrieNode<String>(Bit32Array.<TrieNode<String>>of(), Bit32Array.<String>of());
        node = node.assign(0, 8, "a");
        node = node.assign(0, 12, "b");
        node = node.assign(1, 5, "c");
        node = node.assign(7, 8, "d");
        node = node.assign(7, 15, "e");
        node = node.assign(12, 3, "f");

        Cursor<String> cursor = node.cursor().next();
        assertEquals(true, cursor.hasValue());
        assertEquals("a", cursor.getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("b", cursor.getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("c", cursor.getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("d", cursor.getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("e", cursor.getValue());
        cursor = cursor.next();
        assertEquals(true, cursor.hasValue());
        assertEquals("f", cursor.getValue());
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());
    }

    public void testCursor2()
    {
        TrieNode<Integer> node = new StandardTrieNode<Integer>(Bit32Array.<TrieNode<Integer>>of(), Bit32Array.<Integer>of());
        final int maxBranches = 500;
        for (int branch = 0; branch < maxBranches; ++branch) {
            int branchIndex = branch * 32 + 1;
            node = node.assign(branchIndex, 1, branch);
        }

        List<Integer> values = new ArrayList<Integer>();
        Cursor<Integer> cursor = node.cursor().next();
        for (int branch = 0; branch < maxBranches; ++branch) {
            assertTrue(cursor.hasValue());
            values.add(cursor.getValue());
            cursor = cursor.next();
        }
        assertFalse(cursor.hasValue());
        assertEquals(values.size(), node.deepSize());
    }
}
