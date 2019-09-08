package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.hash.JImmutableHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HamtBuilderTest
    extends TestCase
{
    public void testSignExtension()
    {
        final int i = -1;
        final long extended = i;
        final long fixed = extended & 0xffffffffL;
        assertEquals(-1L, extended);
        assertEquals(0x00000000ffffffffL, fixed);
        assertFalse(extended == fixed);
    }

    public void testMaskCalculation()
    {
        assertEquals(0xffffffff, HamtBuilder.maskForShift(30));
        assertEquals(0x3fffffff, HamtBuilder.maskForShift(25));
        assertEquals(0x1f, HamtBuilder.maskForShift(0));
    }

    public void testRandom()
    {
        final Random r = new Random(100);
        JImmutableMap.Builder<Integer, Integer> builder = new HamtBuilder<>();
        JImmutableMap<Integer, Integer> expected = JImmutableHashMap.of();
        List<Integer> keys = new ArrayList<>();
        for (int i = 1; i <= 50000; ++i) {
            Integer key = r.nextInt();
            Integer value = r.nextInt();
            keys.add(key);
            builder.add(key, value);
            expected = expected.assign(key, value);
            JImmutableMap<Integer, Integer> actual = builder.build();
            actual.checkInvariants();
            Integer av = actual.get(key);
            assertEquals(value, actual.get(key));
            assertEquals(expected, actual);
        }
    }
}
