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
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;

import java.util.List;

public class TrieNodeTest
        extends TestCase
{
    public void testVarious()
    {
        TrieNode<Integer> root = EmptyTrieNode.instance();
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

    public void testTransforms()
    {
        List<Integer> indexes = TrieArrayTest.createBranchIndexes();
        for (int length = indexes.size(); length > 0; --length) {
            TrieNode<Integer> table = TrieNode.of();
            Transforms<Integer, Integer, Integer> transforms = new TrivialTransforms();
            assertEquals(true, table.isEmpty());
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                MutableDelta delta = new MutableDelta();
                table = table.assign(table.getShift(), key.hashCode(), key, key, transforms, delta);
                assertEquals(1, delta.getValue());
            }
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                assertEquals(key, table.getValueOr(table.getShift(), key.hashCode(), key, transforms, -99));
                assertEquals(Holders.of(key), table.find(table.getShift(), key.hashCode(), key, transforms));
            }
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                MutableDelta delta = new MutableDelta();
                table = table.assign(table.getShift(), key.hashCode(), key, key - 10, transforms, delta);
                assertEquals(0, delta.getValue());
                assertEquals(Integer.valueOf(key - 10), table.getValueOr(table.getShift(), key.hashCode(), key, transforms, -99));
                assertEquals(Holders.of(key - 10), table.find(table.getShift(), key.hashCode(), key, transforms));
            }
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                MutableDelta delta = new MutableDelta();
                table = table.delete(table.getShift(), key.hashCode(), key, transforms, delta);
                assertEquals(-1, delta.getValue());
                assertEquals(Integer.valueOf(-99), table.getValueOr(table.getShift(), key.hashCode(), key, transforms, -99));
                assertEquals(Holders.<Integer>of(), table.find(table.getShift(), key.hashCode(), key, transforms));
            }
        }
    }

    private static class TrivialTransforms
            implements Transforms<Integer, Integer, Integer>
    {
        @Override
        public Integer update(Holder<Integer> leaf,
                              Integer key,
                              Integer value,
                              MutableDelta delta)
        {
            if (leaf.isEmpty()) {
                delta.add(1);
                return value;
            } else {
                Integer oldValue = leaf.getValue();
                return ((oldValue != null) && oldValue.equals(value)) ? oldValue : value;
            }
        }

        @Override
        public Holder<Integer> delete(Integer leaf,
                                      Integer key,
                                      MutableDelta delta)
        {
            delta.subtract(1);
            return Holders.of();
        }

        @Override
        public Holder<Integer> findValue(Integer leaf,
                                         Integer key)
        {
            return Holders.of(leaf);
        }

        @Override
        public Holder<JImmutableMap.Entry<Integer, Integer>> findEntry(Integer leaf,
                                                                       Integer key)
        {
            return Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(key, leaf));
        }

        // this is wrong since its guessing the key but ok for unit tests
        @Override
        public Cursor<JImmutableMap.Entry<Integer, Integer>> cursor(Integer leaf)
        {
            return SingleValueCursor.of(findEntry(leaf, leaf).getValue());
        }
    }
}
