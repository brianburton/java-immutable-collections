package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.common.HamtLongMath;

import static java.lang.Integer.*;

public class TrieArrayNodeTest
    extends TestCase
{
    public void testCommonAncestorShift()
    {
        // 352321536 (3)
        final int rootIndex = 352321536 + 1;
        // 368460608 (0)
        final int leafIndex = 368460608 + 3;
        // 368459776 (2)
        final int assignIndex = 368459776;
        assertEquals("0,21,0,0,0,1", HamtLongMath.hashString(rootIndex));
        assertEquals("0,21,61,36,13,3", HamtLongMath.hashString(leafIndex));
        assertEquals("0,21,61,36,0,0", HamtLongMath.hashString(assignIndex));
        // common says 1 but index says 2
        assertEquals(0, HamtLongMath.findMinimumShiftForZeroBelowHashCode(leafIndex));
        assertEquals(2, HamtLongMath.findMinimumShiftForZeroBelowHashCode(assignIndex));
        assertEquals(0, HamtLongMath.findMinimumShiftForZeroBelowHashCode(rootIndex));
        assertEquals(1, HamtLongMath.findMaxCommonShift(TrieArrayNode.ROOT_SHIFT_COUNT, leafIndex, assignIndex));
        assertEquals(2, TrieArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        assertEquals(3, TrieArrayNode.findCommonAncestorShift(rootIndex, assignIndex));
        assertEquals(0, TrieArrayNode.findCommonAncestorShift(1, 2));
        assertEquals(TrieArrayNode.ROOT_SHIFT_COUNT, TrieArrayNode.findCommonAncestorShift(0b11_000000_000000_000000_000000_000000,
                                                                                           0b01_000000_000000_000000_000000_000000));
        assertEquals(4, TrieArrayNode.findCommonAncestorShift(0b11_000001_000000_000000_000000_000000,
                                                              0b11_000001_000001_000000_000000_000000));
        assertEquals(3, TrieArrayNode.findCommonAncestorShift(0b11_000001_000010_000000_000000_000000,
                                                              0b11_000001_000001_000000_000000_000000));
        assertEquals(1, HamtLongMath.findMinimumShiftForZeroBelowHashCode(0b11_000001_000011_000000_000001_000000));
        assertEquals(2, TrieArrayNode.findCommonAncestorShift(0b11_000001_000011_000000_000000_000001,
                                                              0b11_000001_000011_000001_000000_000000));

        assertEquals(3, TrieArrayNode.findCommonAncestorShift(rootIndex, leafIndex));
        assertEquals(2, TrieArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        TrieArrayNode<Integer> node = TrieArrayNode.<Integer>empty().assign(rootIndex, 1);
        node = node.assign(leafIndex, 3);
        node = node.assign(assignIndex, 2);
        assertEquals(Integer.valueOf(2), node.getValueOr(assignIndex, -1));
    }

    public void testIndexMath()
    {
        assertEquals(0, TrieArrayNode.nodeIndex(MIN_VALUE));
        assertEquals(MAX_VALUE, TrieArrayNode.nodeIndex(-1));
        assertEquals(0, TrieArrayNode.nodeIndex(0));
        assertEquals(MAX_VALUE, TrieArrayNode.nodeIndex(MAX_VALUE));

        assertEquals(MIN_VALUE, TrieArrayNode.NEGATIVE_BASE_INDEX + TrieArrayNode.nodeIndex(MIN_VALUE));
        assertEquals(-1, TrieArrayNode.NEGATIVE_BASE_INDEX + TrieArrayNode.nodeIndex(-1));
        assertEquals(0, TrieArrayNode.POSITIVE_BASE_INDEX + TrieArrayNode.nodeIndex(0));
        assertEquals(MAX_VALUE, TrieArrayNode.POSITIVE_BASE_INDEX + TrieArrayNode.nodeIndex(MAX_VALUE));
    }
}
