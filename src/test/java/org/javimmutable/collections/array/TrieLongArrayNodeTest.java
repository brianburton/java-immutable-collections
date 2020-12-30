package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.common.LongArrayMappedTrieMath;

import java.util.Random;

import static java.lang.Long.*;
import static org.javimmutable.collections.array.TrieLongArrayNode.flip;

public class TrieLongArrayNodeTest
    extends TestCase
{
    public void testFlip()
    {
        assertEquals(0L, flip(Long.MIN_VALUE));
        assertEquals(-1L, flip(Long.MAX_VALUE));
        final Random r = new Random(10000);
        for (int i = 0; i < 50000; ++i) {
            final int a = r.nextInt();
            final int b = r.nextInt();
            final int sc = compare(a, b);
            final int uc = compareUnsigned(flip(a), flip(b));
            assertEquals(sc, uc);
            assertEquals(a, flip(flip(a)));
            assertEquals(b, flip(flip(b)));
        }
    }

    public void testCommonAncestorShift()
    {
        // 352321536 (3)
        final int rootIndex = 352321536 + 1;
        // 368460608 (0)
        final int leafIndex = 368460608 + 3;
        // 368459776 (2)
        final int assignIndex = 368459776;
        assertEquals("0,0,0,0,0,0,21,0,0,0,1", LongArrayMappedTrieMath.hashString(rootIndex));
        assertEquals("0,0,0,0,0,0,21,61,36,13,3", LongArrayMappedTrieMath.hashString(leafIndex));
        assertEquals("0,0,0,0,0,0,21,61,36,0,0", LongArrayMappedTrieMath.hashString(assignIndex));
        // common says 1 but index says 2
        assertEquals(0, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(leafIndex));
        assertEquals(2, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(assignIndex));
        assertEquals(0, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(rootIndex));
        assertEquals(1, LongArrayMappedTrieMath.findMaxCommonShift(TrieLongArrayNode.ROOT_SHIFT_COUNT, leafIndex, assignIndex));
        assertEquals(2, TrieLongArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        assertEquals(3, TrieLongArrayNode.findCommonAncestorShift(rootIndex, assignIndex));
        assertEquals(0, TrieLongArrayNode.findCommonAncestorShift(1, 2));
        assertEquals(TrieLongArrayNode.ROOT_SHIFT_COUNT, TrieLongArrayNode.findCommonAncestorShift(0b11_000000_000000_000000_000000_000000,
                                                                                                   0b01_000000_000000_000000_000000_000000));
        assertEquals(4, TrieLongArrayNode.findCommonAncestorShift(0b11_000001_000000_000000_000000_000000,
                                                                  0b11_000001_000001_000000_000000_000000));
        assertEquals(3, TrieLongArrayNode.findCommonAncestorShift(0b11_000001_000010_000000_000000_000000,
                                                                  0b11_000001_000001_000000_000000_000000));
        assertEquals(1, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(0b11_000001_000011_000000_000001_000000));
        assertEquals(2, TrieLongArrayNode.findCommonAncestorShift(0b11_000001_000011_000000_000000_000001,
                                                                  0b11_000001_000011_000001_000000_000000));

        assertEquals(3, TrieLongArrayNode.findCommonAncestorShift(rootIndex, leafIndex));
        assertEquals(2, TrieLongArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        TrieLongArrayNode<Long> node = TrieLongArrayNode.<Long>empty().assign(rootIndex, 1L);
        node = node.assign(leafIndex, 3L);
        node = node.assign(assignIndex, 2L);
        assertEquals(Long.valueOf(2), node.getValueOr(assignIndex, -1L));
    }
}
