package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;

import static org.javimmutable.collections.common.HamtLongMath.*;

public class HamtLongMathTest
    extends TestCase
{
    public void testIndices()
    {
        long bitmask = 0;
        Indexed<Integer> indices = indices(bitmask);
        assertEquals(0, indices.size());

        bitmask = HamtLongMath.addBit(bitmask, bitFromIndex(3));
        bitmask = HamtLongMath.addBit(bitmask, bitFromIndex(9));
        bitmask = HamtLongMath.addBit(bitmask, bitFromIndex(15));
        bitmask = HamtLongMath.addBit(bitmask, bitFromIndex(27));
        indices = indices(bitmask);
        assertEquals(4, indices.size());
        assertEquals(3, (int)indices.get(0));
        assertEquals(9, (int)indices.get(1));
        assertEquals(15, (int)indices.get(2));
        assertEquals(27, (int)indices.get(3));

        for (int index = 0; index < ARRAY_SIZE; ++index) {
            long bit = bitFromIndex(index);
            indices = indices(bit);
            assertEquals(1, indices.size());
            assertEquals(index, (int)indices.get(0));
        }

        indices = indices(-1);
        assertEquals(ARRAY_SIZE, indices.size());
        for (int index = 0; index < ARRAY_SIZE; ++index) {
            assertEquals(index, (int)indices.get(index));
        }
    }
}
