///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.array.trie32;

import junit.framework.TestCase;
import org.javimmutable.collections.common.IndexedArray;

import java.util.Arrays;

public class MultiBranchTrieNodeTest
        extends TestCase
{
    public void testForBranchIndex()
    {
        for (int branchIndex = 0; branchIndex < 32; ++branchIndex) {
            TrieNode<String> leaf = LeafTrieNode.of(branchIndex, "hello");
            MultiBranchTrieNode<String> node = MultiBranchTrieNode.forBranchIndex(10, branchIndex, leaf);
            assertEquals(10, node.getShift());
            assertEquals(1 << branchIndex, node.getBitmask());
            assertTrue(Arrays.equals(new TrieNode[]{leaf}, node.getEntries()));
        }
    }

    public void testForIndex()
    {
        final int shift = 10;
        final int baseIndex = ~(0x1f << shift);
        for (int branchIndex = 0; branchIndex < 32; ++branchIndex) {
            final int index = baseIndex | (branchIndex << shift);
            TrieNode<String> leaf = LeafTrieNode.of(branchIndex, "hello");
            MultiBranchTrieNode<String> node = MultiBranchTrieNode.forIndex(shift, index, leaf);
            assertEquals(shift, node.getShift());
            assertEquals(1 << branchIndex, node.getBitmask());
            assertTrue(Arrays.equals(new TrieNode[]{leaf}, node.getEntries()));
        }
    }

    public void testForEntries()
    {
        for (int length = 1; length <= 32; ++length) {
            final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
            for (int i = 0; i < length; ++i) {
                entries[i] = LeafTrieNode.of(i, "value" + i);
            }
            final int bitmask = (length == 32) ? -1 : ((1 << length) - 1);
            final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(10, entries);
            assertEquals(10, node.getShift());
            assertEquals(bitmask, node.getBitmask());
            assertTrue(Arrays.equals(entries, node.getEntries()));
        }
    }

    public void testForSource()
    {
        for (int length = 1; length <= 32; ++length) {
            final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
            final String[] values = new String[length];
            for (int i = 0; i < length; ++i) {
                values[i] = "value" + i;
                entries[i] = LeafTrieNode.of(50 + i, "value" + i);
            }
            final int bitmask = (length == 32) ? -1 : ((1 << length) - 1);
            final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forSource(50, length, IndexedArray.retained(values), 0);
            assertEquals(0, node.getShift());
            assertEquals(bitmask, node.getBitmask());
            assertTrue(Arrays.equals(entries, node.getEntries()));
        }
    }

    public void testFullWithout()
    {
        final TrieNode<String>[] full = MultiBranchTrieNode.allocate(32);
        for (int i = 0; i < 32; ++i) {
            full[i] = LeafTrieNode.of(50 + i, "value" + i);
        }
        for (int without = 0; without < 32; ++without) {
            final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(31);
            int index = 0;
            for (int i = 0; i < 32; ++i) {
                if (i != without) {
                    entries[index++] = full[i];
                }
            }
            final int bitmask = ~(1 << without);
            final MultiBranchTrieNode<String> node = MultiBranchTrieNode.fullWithout(10, full, without);
            assertEquals(10, node.getShift());
            assertEquals(bitmask, node.getBitmask());
            assertTrue(Arrays.equals(entries, node.getEntries()));
        }
    }
}
