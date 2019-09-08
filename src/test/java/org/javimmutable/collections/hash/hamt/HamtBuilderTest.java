package org.javimmutable.collections.hash.hamt;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.hash.JImmutableHashMap;

import java.util.Random;

public class HamtBuilderTest
    extends TestCase
{
    public void testSortHash()
    {
        assertEquals(0b0L, HamtBuilder.computeSortCode(0));
        assertEquals(0b11111000000000000000000000000000000L,
                     HamtBuilder.computeSortCode(0b00000000000000000000000000011111));
        assertEquals(0b00000111110000000000000000000000000L,
                     HamtBuilder.computeSortCode(0b00000000000000000000001111100000));
        assertEquals(0b00000000001111100000000000000000000L,
                     HamtBuilder.computeSortCode(0b00000000000000000111110000000000));
        assertEquals(0b00000000000000011111000000000000000L,
                     HamtBuilder.computeSortCode(0b00000000000011111000000000000000));
        assertEquals(0b00000000000000000000111110000000000L,
                     HamtBuilder.computeSortCode(0b00000001111100000000000000000000));
        assertEquals(0b00000000000000000000000001111100000L,
                     HamtBuilder.computeSortCode(0b00111110000000000000000000000000));
        assertEquals(0b00000000000000000000000000000000011L,
                     HamtBuilder.computeSortCode(0b11000000000000000000000000000000));
        assertEquals(0b10110_01101_11011_10110_01101_01101_00011L,
                     HamtBuilder.computeSortCode(0b11_01101_01101_10110_11011_01101_10110));
    }

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
