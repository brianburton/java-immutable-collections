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

package org.javimmutable.collections.hash;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.PersistentMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PersistentHashMapTest
        extends TestCase
{
    public void test()
    {
        PersistentHashMap<Integer, Integer> map = PersistentHashMap.of();
        assertEquals(true, map.find(10).isEmpty());
        assertEquals(0, map.size());
        map = map.assign(10, 20);
        assertEquals(1, map.size());
        assertEquals(false, map.find(10).isEmpty());
        assertEquals(20, (int)map.find(10).getValue());
        map = map.delete(10);
        assertEquals(true, map.find(10).isEmpty());
        assertEquals(0, map.size());
    }

    public void testValueIdentity()
    {
        PersistentHashMap<Integer, String> map = PersistentHashMap.of();
        map = map.assign(10, "ab");
        assertSame(map, map.assign(10, "ab"));
        for (int i = 100; i <= 15000; ++i) {
            map = map.assign(i, Integer.toString(i));
        }
        map = map.assign(14000, "aaa");
        assertSame(map, map.assign(14000, "aaa"));
    }

    public void testNullKeys()
    {
        PersistentMap<Integer, Integer> map = PersistentHashMap.of();
        map = map.assign(1, 3);
        try {
            map.assign(null, 18);
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            map.get(null);
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            map.find(null);
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            map.findEntry(null);
        } catch (NullPointerException ex) {
            // expected
        }
        try {
            map.delete(null);
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void testRandom1()
    {
        final int maxKey = 999999999;
        Random random = new Random(100);
        for (int loop = 0; loop < 1000; ++loop) {
            Map<Integer, Integer> expected = new HashMap<Integer, Integer>();
            PersistentHashMap<Integer, Integer> map = PersistentHashMap.of();
            final int size = 250 + random.nextInt(250);
            for (int i = 1; i <= size; ++i) {
                int command = random.nextInt(4);
                if (command <= 1) {
                    Integer key = random.nextInt(maxKey);
                    Integer value = random.nextInt(1000000);
                    expected.put(key, value);
                    map = map.assign(key, value);
                } else if (command == 2) {
                    Integer key = random.nextInt(maxKey);
                    expected.remove(key);
                    map = map.delete(key);
                } else {
                    Integer key = random.nextInt(maxKey);
                    assertEquals(expected.get(key), map.find(key).getValueOrNull());
                }
                assertEquals(expected.size(), map.size());
            }

            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                Holder<Integer> mapValue = map.find(entry.getKey());
                assertEquals(true, mapValue.isFilled());
                assertEquals(entry.getValue(), mapValue.getValue());
            }

            // verify the Map adaptor worked properly
            assertEquals(expected, map.getMap());
            assertEquals(expected.keySet(), map.getMap().keySet());
            assertEquals(expected.entrySet(), map.getMap().entrySet());
            ArrayList<Integer> jvalues = new ArrayList<Integer>(expected.values());
            ArrayList<Integer> pvalues = new ArrayList<Integer>(map.getMap().values());
            Collections.sort(jvalues);
            Collections.sort(pvalues);
            assertEquals(jvalues, pvalues);

            // verify the cursor worked properly
            Map<Integer, Integer> fromCursor = new HashMap<Integer, Integer>();
            for (PersistentMap.Entry<Integer, Integer> entry : map) {
                fromCursor.put(entry.getKey(), entry.getValue());
            }
            assertEquals(expected, fromCursor);

            // verify the map can remove all keys
            ArrayList<Integer> keys = new ArrayList<Integer>(expected.keySet());
            Collections.shuffle(keys, random);
            for (Integer key : keys) {
                map = map.delete(key);
                assertEquals(true, map.find(key).isEmpty());
            }
            assertEquals(0, map.size());
        }
    }

    public void testEquals()
    {
        PersistentMap<Integer, Integer> map1 = PersistentHashMap.<Integer, Integer>of().assign(1, 3).assign(2, 4).assign(3, 5);
        PersistentMap<Integer, Integer> map2 = PersistentHashMap.<Integer, Integer>of().assign(1, 3).assign(2, 4).assign(3, 5);
        assertEquals(map1.hashCode(), map2.hashCode());
        assertEquals(map1, map2);
    }
}
