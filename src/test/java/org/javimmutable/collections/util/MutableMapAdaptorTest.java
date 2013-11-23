package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class MutableMapAdaptorTest
        extends TestCase
{
    private static class TestAdaptor<K extends Comparable<K>, V>
            extends MutableMapAdaptor<K, V>
    {
        private JImmutableMap<K, V> myMap = JImmutables.sortedMap();

        @Override
        protected JImmutableMap<K, V> accessMap()
        {
            return myMap;
        }

        @Override
        protected void replaceMap(JImmutableMap<K, V> newMap)
        {
            myMap = newMap;
        }
    }

    public void testIterator()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<Integer, Integer>();
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
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(1, 10), iter.next());
        assertEquals(false, iter.hasNext());

        adaptor.put(2, 20);
        assertEquals(false, adaptor.isEmpty());
        assertEquals(2, adaptor.size());
        assertEquals(true, adaptor.containsKey(1));
        assertEquals(true, adaptor.containsKey(2));
        assertEquals(true, adaptor.containsValue(10));
        assertEquals(true, adaptor.containsValue(20));
        iter = adaptor.entrySet().iterator();
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(1, 10), iter.next());
        assertEquals(true, iter.hasNext());
        Map.Entry<Integer, Integer> entry = iter.next();
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(2, 20), entry);
        entry.setValue(25);
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(2, 25), entry);
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
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(1, 10), iter.next());
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(2, 25), iter.next());
        assertEquals(true, iter.hasNext());
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(3, 30), iter.next());
        iter.remove();
        assertEquals(false, iter.hasNext());
        assertEquals(2, adaptor.size());

        iter = adaptor.entrySet().iterator();
        assertEquals(true, iter.hasNext());
        entry = iter.next();
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(1, 10), entry);
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
        assertEquals(new AbstractMap.SimpleEntry<Integer, Integer>(3, 30), iter.next());
    }

    public void testPutGet()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<Integer, Integer>();
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
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
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<Integer, Integer>();
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
        for (int i = 1; i <= 10; ++i) {
            for (int k = 3; k <= 8; ++k) {
                assertEquals(expected.put(i + k, i - k), adaptor.put(i + k, i - k));
            }
        }
        for (JImmutableMap.Entry<Integer, Integer> entry : adaptor.myMap) {
            assertEquals(expected.remove(entry.getKey()), adaptor.remove(entry.getKey()));
            assertEquals(expected, adaptor);
        }
        assertEquals(expected, adaptor);
    }

    public void testClear()
    {
        TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<Integer, Integer>();
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
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

    public void testRandom()
    {
        final int maxKey = 250;
        Random random = new Random();
        for (int loop = 0; loop < 10; ++loop) {
            TestAdaptor<Integer, Integer> adaptor = new TestAdaptor<Integer, Integer>();
            Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
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
            assertEquals(new ArrayList<Integer>(expected.values()), new ArrayList<Integer>(adaptor.values()));
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                Integer value = adaptor.get(entry.getKey());
                assertEquals(entry.getValue(), value);
            }
        }
    }
}
