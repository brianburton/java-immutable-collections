package org.javimmutable.collections.array.int_trie;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableArray;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class TrieArrayTest
        extends TestCase
{
    public void testRandom()
    {
        Random r = new Random(20);
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
        JImmutableArray<Integer> array = TrieArray.of();
        for (int loop = 1; loop <= 20000; ++loop) {
            int index = r.nextInt(2000) - 1000;
            int value = r.nextInt();
            array = array.assign(index, value);
            expected.put(index, value);
        }
        assertEquals(expected.size(), array.size());
        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), array.getValueOr(entry.getKey(), Integer.MAX_VALUE));
        }
    }
}
