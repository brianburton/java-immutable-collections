package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;

import static org.javimmutable.collections.common.HamtIntMath.*;

public class HamtIntMathTest
    extends TestCase
{
    public void testIndices()
    {
        int bitmask = 0;
        Indexed<Integer> indices = HamtIntMath.indices(bitmask);
        assertEquals(0, indices.size());

        bitmask = HamtIntMath.addBit(bitmask, bitFromIndex(3));
        bitmask = HamtIntMath.addBit(bitmask, bitFromIndex(9));
        bitmask = HamtIntMath.addBit(bitmask, bitFromIndex(15));
        bitmask = HamtIntMath.addBit(bitmask, bitFromIndex(27));
        indices = HamtIntMath.indices(bitmask);
        assertEquals(4, indices.size());
        assertEquals(3, (int)indices.get(0));
        assertEquals(9, (int)indices.get(1));
        assertEquals(15, (int)indices.get(2));
        assertEquals(27, (int)indices.get(3));

        for (int index = 0; index < ARRAY_SIZE; ++index) {
            int bit = bitFromIndex(index);
            indices = HamtIntMath.indices(bit);
            assertEquals(1, indices.size());
            assertEquals(index, (int)indices.get(0));
        }

        indices = HamtIntMath.indices(-1);
        assertEquals(32, indices.size());
        for (int index = 0; index < 32; ++index) {
            assertEquals(index, (int)indices.get(index));
        }
    }
}
