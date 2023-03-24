///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import junit.framework.TestCase;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.iterators.StandardIteratorTests;

public class MutableMapAdaptorTest
    extends TestCase
{
    private static class TestAdaptor<K extends Comparable<K>, V>
        extends MutableMapAdaptor<K, V>
    {
        private IMap<K, V> myMap = JImmutables.sortedMap();

        @Override
        protected IMap<K, V> accessMap()
        {
            return myMap;
        }

        @Override
        protected void replaceMap(IMap<K, V> newMap)
        {
            myMap = newMap;
        }
    }

    public void testIterator()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<>();
        assertEquals(true, adaptor.isEmpty());
        assertEquals(0, adaptor.size());
        assertEquals(false, adaptor.containsKey(1));
        assertEquals(false, adaptor.containsKey(2));
        assertEquals(false, adaptor.entrySet().iterator().hasNext());

        adaptor.put(1, 10);
        assertEquals(false, adaptor.isEmpty());
        assertEquals(1, adaptor.size());
        assertEquals(true, adaptor.containsKey(1));
        assertEquals(false, adaptor.containsKey(2));
        assertEquals(true, adaptor.containsValue(10));
        assertEquals(false, adaptor.containsValue(20));
        Iterator<Map.Entry<Integer, Integer>> iter = adaptor.entrySet().iterator();
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<>(1, 10), iter.next());
        assertEquals(false, iter.hasNext());
        StandardIteratorTests.listIteratorTest(new ArrayList<>(adaptor.entrySet()), adaptor.entrySet().iterator());

        adaptor.put(2, 20);
        assertEquals(false, adaptor.isEmpty());
        assertEquals(2, adaptor.size());
        assertEquals(true, adaptor.containsKey(1));
        assertEquals(true, adaptor.containsKey(2));
        assertEquals(true, adaptor.containsValue(10));
        assertEquals(true, adaptor.containsValue(20));
        iter = adaptor.entrySet().iterator();
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<>(1, 10), iter.next());
        assertEquals(true, iter.hasNext());
        Map.Entry<Integer, Integer> entry = iter.next();
        assertEquals(new AbstractMap.SimpleEntry<>(2, 20), entry);
        entry.setValue(25);
        assertEquals(new AbstractMap.SimpleEntry<>(2, 25), entry);
        assertEquals(false, iter.hasNext());

        adaptor.put(3, 30);
        assertEquals(false, adaptor.isEmpty());
        assertEquals(3, adaptor.size());
        assertEquals(true, adaptor.containsKey(1));
        assertEquals(true, adaptor.containsKey(2));
        assertEquals(true, adaptor.containsKey(3));
        assertEquals(true, adaptor.containsValue(10));
        assertEquals(false, adaptor.containsValue(20));
        assertEquals(true, adaptor.containsValue(25));
        assertEquals(true, adaptor.containsValue(30));
        iter = adaptor.entrySet().iterator();
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<>(1, 10), iter.next());
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<>(2, 25), iter.next());
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<>(3, 30), iter.next());
        iter.remove();
        assertEquals(false, iter.hasNext());
        assertEquals(2, adaptor.size());

        iter = adaptor.entrySet().iterator();
        assertEquals(true, iter.hasNext());
        entry = iter.next();
        assertEquals(new AbstractMap.SimpleEntry<>(1, 10), entry);
        adaptor.myMap = adaptor.myMap.assign(2, 18);
        try {
            iter.next();
            fail();
        } catch (ConcurrentModificationException ex) {
            // expected
        }
        try {
            iter.hasNext();
            fail();
        } catch (ConcurrentModificationException ex) {
            // expected
        }
        try {
            entry.setValue(22);
            fail();
        } catch (ConcurrentModificationException ex) {
            // expected
        }

        adaptor.put(3, 30);
        iter = adaptor.entrySet().iterator();
        assertEquals(3, adaptor.size());
        iter.next();
        iter.remove();
        assertEquals(2, adaptor.size());
        iter.next();
        iter.remove();
        assertEquals(1, adaptor.size());
        assertEquals(new AbstractMap.SimpleEntry<>(3, 30), iter.next());
    }

    public void testPutGet()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<>();
        Map<Integer, Integer> expected = new TreeMap<>();
        for (int i = 1; i <= 10; ++i) {
            for (int k = 3; k <= 8; ++k) {
                assertEquals(expected.put(i + k, i - k), adaptor.put(i + k, i - k));
                assertEquals(expected.get(i + k), adaptor.get(i + k));
                assertEquals(expected, adaptor);
                assertEquals(expected, adaptor.myMap.getMap());
                assertEquals(expected.size(), adaptor.size());
            }
        }
        for (Iterator<Map.Entry<Integer, Integer>> iter = adaptor.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Integer, Integer> entry = iter.next();
            iter.remove();
            expected.remove(entry.getKey());
            assertEquals(expected, adaptor);
            assertEquals(expected.size(), adaptor.size());
        }
        assertEquals(true, adaptor.isEmpty());
        assertEquals(0, adaptor.size());
    }

    public void testRemove()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<>();
        Map<Integer, Integer> expected = new TreeMap<>();
        for (int i = 1; i <= 10; ++i) {
            for (int k = 3; k <= 8; ++k) {
                assertEquals(expected.put(i + k, i - k), adaptor.put(i + k, i - k));
            }
        }
        for (IMapEntry<Integer, Integer> entry : adaptor.myMap) {
            assertEquals(expected.remove(entry.getKey()), adaptor.remove(entry.getKey()));
            assertEquals(expected, adaptor);
        }
        assertEquals(expected, adaptor);
    }

    public void testClear()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<>();
        Map<Integer, Integer> expected = new TreeMap<>();
        for (int i = 1; i <= 10; ++i) {
            for (int k = 3; k <= 8; ++k) {
                assertEquals(expected.put(i + k, i - k), adaptor.put(i + k, i - k));
            }
        }
        expected.clear();
        adaptor.clear();
        assertEquals(true, adaptor.isEmpty());
        assertEquals(0, adaptor.size());
        assertEquals(expected, adaptor);
    }

    public void testEntrySet()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<>();
        Map<Integer, Integer> expected = new TreeMap<>();
        for (int i = 1; i <= 30; ++i) {
            adaptor.put(i, i);
            expected.put(i, i);
        }
        assertEquals(expected, adaptor);

        entrySetChanges(adaptor);
        entrySetChanges(expected);
        assertEquals(expected, adaptor);

        for (int i = 1; i <= 30; ++i) {
            Map.Entry<Integer, Integer> e = new AbstractMap.SimpleEntry<>(i, i);
            assertEquals(expected.containsKey(i), adaptor.entrySet().contains(e));
            if (i % 7 == 0) {
                expected.remove(i);
                adaptor.entrySet().remove(e);
            } else {
                e = new AbstractMap.SimpleEntry<>(i, 3 * i);
                expected.put(e.getKey(), e.getValue()); // java maps don't support this
                adaptor.entrySet().add(e);
            }
        }
        assertEquals(expected, adaptor);
    }

    private void entrySetChanges(Map<Integer, Integer> map)
    {
        Iterator<Map.Entry<Integer, Integer>> i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Integer, Integer> e = i.next();
            if (e.getKey() % 2 == 0) {
                e.setValue(2 * e.getKey());
            } else if (e.getKey() % 3 == 0) {
                i.remove();
            }
        }
    }

    public void testRandom()
    {
        final int maxKey = 250;
        Random random = new Random();
        for (int loop = 0; loop < 10; ++loop) {
            TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<>();
            Map<Integer, Integer> expected = new TreeMap<>();
            for (int i = 1; i <= 25 * maxKey; ++i) {
                int command = random.nextInt(4);
                if (command <= 1) {
                    Integer key = random.nextInt(maxKey);
                    Integer value = random.nextInt(1000000);
                    expected.put(key, value);
                    adaptor.put(key, value);
                } else if (command == 2) {
                    Integer key = random.nextInt(maxKey);
                    expected.remove(key);
                    adaptor.remove(key);
                } else {
                    Integer key = random.nextInt(maxKey);
                    assertEquals(expected.get(key), adaptor.get(key));
                }
                assertEquals(expected.size(), adaptor.size());
            }
            assertEquals(expected, adaptor);
            assertEquals(expected.keySet(), adaptor.keySet());
            assertEquals(new ArrayList<>(expected.values()), new ArrayList<>(adaptor.values()));
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                Integer value = adaptor.get(entry.getKey());
                assertEquals(entry.getValue(), value);
            }
        }
    }
}
