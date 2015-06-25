///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FullBranchTrieNodeTest
        extends TestCase
{
    public void testFromSource()
    {
        List<String> list = new ArrayList<String>();
        for (int length = 0; length < 32; ++length) {
            try {
                FullBranchTrieNode.fromSource(0, IndexedList.retained(list), 0);
                fail();
            } catch (AssertionError ignored) {
                // expected
            }
            list.add(String.valueOf(length));
        }
        for (int offset = 1; offset < list.size(); ++offset) {
            try {
                FullBranchTrieNode.fromSource(0, IndexedList.retained(list), offset);
                fail();
            } catch (AssertionError ignored) {
                // expected
            }
        }
        FullBranchTrieNode<String> node = FullBranchTrieNode.fromSource(0, IndexedList.retained(list), 0);
        assertEquals(0, node.getShift());
    }

    public void testOperations()
    {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 32; ++i) {
            list.add(String.valueOf(i));
        }
        TrieNode<String> node = FullBranchTrieNode.fromSource(0, IndexedList.retained(list), 0);
        for (int i = 0; i < 32; ++i) {
            assertEquals(String.valueOf(i), node.getValueOr(0, i, null));
            assertEquals(String.valueOf(i), node.find(0, i).getValue());
            assertEquals(null, node.getValueOr(0, 32 + i, null));
            assertEquals(true, node.find(0, 32 + i).isEmpty());
        }
        for (int i = 31; i >= 0; --i) {
            MutableDelta delta = new MutableDelta();
            node = node.assign(0, i, String.format("%d", -i), delta);
            assertTrue(node instanceof FullBranchTrieNode);
            assertEquals(0, delta.getValue());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(String.valueOf(-i), node.getValueOr(0, i, null));
            assertEquals(String.valueOf(-i), node.find(0, i).getValue());
        }
        for (int i = 0; i < 32; ++i) {
            MutableDelta delta = new MutableDelta();
            TrieNode<String> changed = node.delete(0, i, delta);
            assertEquals(-1, delta.getValue());
            assertTrue(changed instanceof MultiBranchTrieNode);
            for (int k = 0; k < 32; ++k) {
                if (k != i) {
                    assertEquals(String.valueOf(-k), changed.getValueOr(0, k, null));
                    assertEquals(String.valueOf(-k), changed.find(0, k).getValue());
                } else {
                    assertEquals(null, changed.getValueOr(0, k, null));
                    assertEquals(null, changed.find(0, k).getValueOr(null));
                }
            }
        }
        List<JImmutableMap.Entry<Integer, String>> entryList = new ArrayList<JImmutableMap.Entry<Integer, String>>();
        list = new ArrayList<String>();
        for (int i = 0; i < 32; ++i) {
            list.add(String.valueOf(-i));
            entryList.add(MapEntry.of(i, String.valueOf(-i)));
        }
        StandardCursorTest.listCursorTest(list, node.anyOrderValueCursor());
        StandardCursorTest.listCursorTest(list, node.signedOrderValueCursor());
        StandardCursorTest.listCursorTest(entryList, node.anyOrderEntryCursor());
        StandardCursorTest.listCursorTest(entryList, node.signedOrderEntryCursor());
    }

    public void testTransformOperations()
    {
        Transforms<Map<String, String>, String, String> tx = new TestOnlyTransforms<String, String>();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 32; ++i) {
            list.add(tx.update(Holders.<Map<String, String>>of(), String.valueOf(i), String.valueOf(i), new MutableDelta()));
        }
        TrieNode<Map<String, String>> node = FullBranchTrieNode.fromSource(0, IndexedList.retained(list), 0);
        for (int i = 0; i < 32; ++i) {
            assertEquals(String.valueOf(i), node.getValueOr(0, i, String.valueOf(i), tx, null));
            assertEquals(String.valueOf(i), node.find(0, i, String.valueOf(i), tx).getValue());
            assertEquals(null, node.getValueOr(0, 32 + i, String.valueOf(i), tx, null));
            assertEquals(true, node.find(0, 32 + i, String.valueOf(i), tx).isEmpty());
        }
        for (int i = 31; i >= 0; --i) {
            MutableDelta delta = new MutableDelta();
            node = node.assign(0, i, String.valueOf(i), String.format("%d", -i), tx, delta);
            assertTrue(node instanceof FullBranchTrieNode);
            assertEquals(0, delta.getValue());
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(String.valueOf(-i), node.getValueOr(0, i, String.valueOf(i), tx, null));
            assertEquals(String.valueOf(-i), node.find(0, i, String.valueOf(i), tx).getValue());
        }
        for (int i = 0; i < 32; ++i) {
            MutableDelta delta = new MutableDelta();
            TrieNode<Map<String, String>> changed = node.delete(0, i, String.valueOf(i), tx, delta);
            assertEquals(-1, delta.getValue());
            assertTrue(changed instanceof MultiBranchTrieNode);
            for (int k = 0; k < 32; ++k) {
                if (k != i) {
                    assertEquals(String.valueOf(-k), changed.getValueOr(0, k, String.valueOf(k), tx, null));
                    assertEquals(String.valueOf(-k), changed.find(0, k, String.valueOf(k), tx).getValue());
                } else {
                    assertEquals(null, changed.getValueOr(0, k, String.valueOf(k), tx, null));
                    assertEquals(null, changed.find(0, k, String.valueOf(k), tx).getValueOr(null));
                }
            }
        }
        List<JImmutableMap.Entry<String, String>> entryList = new ArrayList<JImmutableMap.Entry<String, String>>();
        for (int i = 0; i < 32; ++i) {
            entryList.add(MapEntry.of(String.valueOf(i), String.valueOf(-i)));
        }
        StandardCursorTest.listCursorTest(entryList, node.anyOrderEntryCursor(tx));
        StandardCursorTest.listCursorTest(entryList, node.signedOrderEntryCursor(tx));
    }
}
