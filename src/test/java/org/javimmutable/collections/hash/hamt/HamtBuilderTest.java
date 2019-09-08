package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.hash.JImmutableHashMap;

import java.util.Random;

public class HamtBuilderTest
    extends TestCase
{
    public void testRandom()
    {
        final Random r = new Random(1000);
        for (int i = 1; i <= 10000; ++i) {
            JImmutableMap.Builder<Integer, Integer> builder = new HamtBuilder<>();
            JImmutableMap<Integer, Integer> expected = JImmutableHashMap.of();
            final int size = 1 + r.nextInt(2500);
            for (int k = 1; k <= size; ++k) {
                final Integer key = r.nextInt(5 * size);
                final Integer value = r.nextInt();
                builder.add(key, value);
                expected = expected.assign(key, value);
            }
            JImmutableMap<Integer, Integer> actual = builder.build();
            actual.checkInvariants();
            assertEquals(expected, actual);
        }
    }
}
