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
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;

import java.util.List;

public class Trie32HashTableTest
        extends TestCase
{
    public void testVarious()
    {
        List<Integer> indexes = Trie32ArrayTest.createBranchIndexes();
        for (int length = indexes.size(); length > 0; --length) {
            Trie32HashTable<Integer, Integer> table = Trie32HashTable.of(new TrivialTransforms());
            assertEquals(0, table.size());
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                table = table.assign(key.hashCode(), key, key);
                assertEquals(i + 1, table.size());
            }
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                assertEquals(key, table.getValueOr(key.hashCode(), key, -99));
                assertEquals(Holders.of(key), table.findValue(key.hashCode(), key));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(key, key)), table.findEntry(key.hashCode(), key));
            }
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                table = table.assign(key.hashCode(), key, key - 10);
                assertEquals(Integer.valueOf(key - 10), table.getValueOr(key.hashCode(), key, -99));
                assertEquals(Holders.of(key - 10), table.findValue(key.hashCode(), key));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(key, key - 10)), table.findEntry(key.hashCode(), key));
            }
            for (int i = 0; i < length; ++i) {
                Integer key = indexes.get(i);
                table = table.delete(key.hashCode(), key);
                assertEquals(length - i - 1, table.size());
                assertEquals(Integer.valueOf(-99), table.getValueOr(key.hashCode(), key, -99));
                assertEquals(Holders.<Integer>of(), table.findValue(key.hashCode(), key));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), table.findEntry(key.hashCode(), key));
            }
        }
    }

    private static class TrivialTransforms
            implements Trie32HashTable.Transforms<Integer, Integer>
    {
        @Override
        public Object update(Holder<Object> leaf,
                             Integer key,
                             Integer value,
                             MutableDelta delta)
        {
            if (leaf.isEmpty()) {
                delta.add(1);
                return value;
            } else {
                Integer oldValue = (Integer)leaf.getValue();
                return (oldValue != null && oldValue.equals(value)) ? oldValue : value;
            }
        }

        @Override
        public Holder<Object> delete(Object leaf,
                                     Integer key,
                                     MutableDelta delta)
        {
            delta.subtract(1);
            return Holders.of();
        }

        @Override
        public Holder<Integer> findValue(Object leaf,
                                         Integer key)
        {
            return Holders.of((Integer)leaf);
        }

        @Override
        public Holder<JImmutableMap.Entry<Integer, Integer>> findEntry(Object leaf,
                                                                       Integer key)
        {
            return Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(key, (Integer)leaf));
        }

        // this is wrong since its guessing the key but ok for unit tests
        @Override
        public Cursor<JImmutableMap.Entry<Integer, Integer>> cursor(Object leaf)
        {
            Integer value = (Integer)leaf;
            return SingleValueCursor.of(findEntry(leaf, value).getValue());
        }
    }
}
