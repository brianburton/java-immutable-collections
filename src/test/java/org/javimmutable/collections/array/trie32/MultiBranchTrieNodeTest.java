///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.indexed.IndexedArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("StringConcatenationMissingWhitespace")
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

    public void testGet32()
    {
        final int length = 32;
        final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
        for (int i = 0; i < length; ++i) {
            entries[i] = LeafTrieNode.of(shiftIndex(20, i), "value" + i);
        }
        final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(20, entries);
        for (int i = 0; i < length; ++i) {
            assertEquals("value" + i, node.getValueOr(20, shiftIndex(20, i), null));
            try {
                node.getValueOr(15, shiftIndex(20, i), null);
                fail();
            } catch (AssertionError ignored) {
                // expected
            }
        }
    }

    public void testGet16()
    {
        final int length = 16;
        final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
        for (int i = 0; i < length; ++i) {
            entries[i] = LeafTrieNode.of(shiftIndex(20, i), "value" + i);
        }
        final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(20, entries);
        for (int i = 0; i < 16; ++i) {
            assertEquals("value" + i, node.getValueOr(20, shiftIndex(20, i), null));
            try {
                node.getValueOr(15, shiftIndex(20, i), null);
                fail();
            } catch (AssertionError ignored) {
                // expected
            }
        }
        for (int i = 16; i < 32; ++i) {
            assertEquals(null, node.getValueOr(20, shiftIndex(20, i), null));
        }
    }

    public void testFind32()
    {
        final int length = 32;
        final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
        for (int i = 0; i < length; ++i) {
            entries[i] = LeafTrieNode.of(shiftIndex(20, i), "value" + i);
        }
        final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(20, entries);
        for (int i = 0; i < length; ++i) {
            assertEquals(Holders.of("value" + i), node.find(20, shiftIndex(20, i)));
            try {
                node.find(15, shiftIndex(20, i));
                fail();
            } catch (AssertionError ignored) {
                // expected
            }
        }
    }

    public void testFind16()
    {
        final int length = 16;
        final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
        for (int i = 0; i < length; ++i) {
            entries[i] = LeafTrieNode.of(shiftIndex(20, i), "value" + i);
        }
        final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(20, entries);
        for (int i = 0; i < 16; ++i) {
            assertEquals(Holders.of("value" + i), node.find(20, shiftIndex(20, i)));
            try {
                node.find(15, shiftIndex(20, i));
                fail();
            } catch (AssertionError ignored) {
                // expected
            }
        }
        for (int i = 16; i < 32; ++i) {
            assertEquals(true, node.find(20, shiftIndex(20, i)).isEmpty());
        }
    }

    public void testAssign()
    {
        TrieNode<String> node = MultiBranchTrieNode.forTesting(20);
        for (int i = 0; i < 32; ++i) {
            MutableDelta delta = new MutableDelta();
            node = node.assign(20, shiftIndex(20, i), "value" + i, delta);
            assertEquals(1, delta.getValue());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.of("value" + i), node.find(20, shiftIndex(20, i)));
        }
        node = node.delete(20, shiftIndex(20, 31), new MutableDelta());
        assertEquals(true, node instanceof MultiBranchTrieNode);
        for (int i = 30; i >= 0; --i) {
            MutableDelta delta = new MutableDelta();
            node = node.assign(20, shiftIndex(20, i), "new_value" + i, delta);
            assertEquals(0, delta.getValue());
        }
        for (int i = 0; i < 31; ++i) {
            assertEquals(Holders.of("new_value" + i), node.find(20, shiftIndex(20, i)));
        }
    }

    public void testDelete()
    {
        TrieNode<String> node = MultiBranchTrieNode.forTesting(20);
        for (int i = 0; i < 32; ++i) {
            MutableDelta delta = new MutableDelta();
            node = node.assign(20, shiftIndex(20, i), "value" + i, delta);
            assertEquals(1, delta.getValue());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.of("value" + i), node.find(20, shiftIndex(20, i)));
        }
        for (int i = 31; i >= 0; --i) {
            MutableDelta delta = new MutableDelta();
            node = node.delete(20, shiftIndex(20, i), delta);
            assertEquals(-1, delta.getValue());
            if (i > 1) {
                assertTrue(node instanceof MultiBranchTrieNode);
            } else if (i == 1) {
                assertTrue(node instanceof LeafTrieNode);
            } else {
                assertTrue(node instanceof EmptyTrieNode);
            }
        }
    }

    public void testTrimmed()
    {
        for (int i = 0; i < 32; ++i) {
            TrieNode<String> node = MultiBranchTrieNode.forTesting(20);
            node = node.assign(20, shiftIndex(20, i), "testing", new MutableDelta());
            if (i == 0) {
                assertTrue(node.trimmedToMinimumDepth() instanceof LeafTrieNode);
            } else {
                assertSame(node, node.trimmedToMinimumDepth());
            }
        }

        LeafTrieNode<String> leaf = LeafTrieNode.of(18, "testing");
        TrieNode<String> node = MultiBranchTrieNode.forIndex(0, 0, leaf);
        for (int shift = 5; shift <= 30; shift += 5) {
            node = MultiBranchTrieNode.forIndex(shift, 0, node);
        }
        assertSame(leaf, node.trimmedToMinimumDepth());
    }

    public void testNonRootCursors()
    {
        final int length = 32;
        final TrieNode<String>[] entriesArray = MultiBranchTrieNode.allocate(length);
        for (int i = 0; i < length; ++i) {
            entriesArray[i] = LeafTrieNode.of(shiftIndex(20, i), "value" + i);
        }
        final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(20, entriesArray);
        List<String> values = new ArrayList<>();
        List<JImmutableMap.Entry<Integer, String>> entries = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            values.add(node.getValueOr(20, shiftIndex(20, i), null));
            entries.add(MapEntry.of(shiftIndex(20, i), values.get(i)));
        }
        StandardCursorTest.listCursorTest(values, node.anyOrderValueCursor());
        StandardCursorTest.listCursorTest(values, node.signedOrderValueCursor());
        StandardCursorTest.listCursorTest(entries, node.anyOrderEntryCursor());
        StandardCursorTest.listCursorTest(entries, node.signedOrderEntryCursor());
        StandardCursorTest.listIteratorTest(values, node.anyOrderValueIterator());
        StandardCursorTest.listIteratorTest(values, node.signedOrderValueIterator());
        StandardCursorTest.listIteratorTest(entries, node.anyOrderEntryIterator());
        StandardCursorTest.listIteratorTest(entries, node.signedOrderEntryIterator());
    }

    public void testRootCursors()
    {
        final int length = 4;
        final TrieNode<String>[] entries = MultiBranchTrieNode.allocate(length);
        for (int i = 0; i < length; ++i) {
            entries[i] = LeafTrieNode.of(shiftIndex(TrieNode.ROOT_SHIFT, i), "value" + i);
        }
        final MultiBranchTrieNode<String> node = MultiBranchTrieNode.forEntries(TrieNode.ROOT_SHIFT, entries);
        List<String> anyOrderValues = new ArrayList<>();
        List<JImmutableMap.Entry<Integer, String>> anyOrderEntries = new ArrayList<>();
        List<String> signedOrderValues = new ArrayList<>();
        List<JImmutableMap.Entry<Integer, String>> signedOrderEntries = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            anyOrderValues.add(node.getValueOr(TrieNode.ROOT_SHIFT, shiftIndex(TrieNode.ROOT_SHIFT, i), null));
            anyOrderEntries.add(MapEntry.of(shiftIndex(TrieNode.ROOT_SHIFT, i), anyOrderValues.get(i)));
        }
        for (Integer i : Arrays.asList(2, 3, 0, 1)) {
            String value = node.getValueOr(TrieNode.ROOT_SHIFT, shiftIndex(TrieNode.ROOT_SHIFT, i), null);
            signedOrderValues.add(value);
            signedOrderEntries.add(MapEntry.of(shiftIndex(TrieNode.ROOT_SHIFT, i), value));
        }
        StandardCursorTest.listCursorTest(anyOrderValues, node.anyOrderValueCursor());
        StandardCursorTest.listCursorTest(signedOrderValues, node.signedOrderValueCursor());
        StandardCursorTest.listCursorTest(anyOrderEntries, node.anyOrderEntryCursor());
        StandardCursorTest.listCursorTest(signedOrderEntries, node.signedOrderEntryCursor());
        StandardCursorTest.listIteratorTest(anyOrderValues, node.anyOrderValueIterator());
        StandardCursorTest.listIteratorTest(signedOrderValues, node.signedOrderValueIterator());
        StandardCursorTest.listIteratorTest(anyOrderEntries, node.anyOrderEntryIterator());
        StandardCursorTest.listIteratorTest(signedOrderEntries, node.signedOrderEntryIterator());
    }

    private int shiftIndex(int shift,
                           int index)
    {
        return ~(0x1f << shift) | (index << shift);
    }
}
