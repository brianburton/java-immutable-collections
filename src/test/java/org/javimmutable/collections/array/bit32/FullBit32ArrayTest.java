package org.javimmutable.collections.array.bit32;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.List;

public class FullBit32ArrayTest
        extends TestCase
{
    public void testVarious()
    {
        @SuppressWarnings("unchecked") Holder<Integer>[] entries = (Holder<Integer>[])new Holder[32];
        for (int k = 0; k < 32; ++k) {
            entries[k] = Holders.of(k);
        }
        for (int i = 0; i < 32; ++i) {
            Bit32Array<Integer> full = new FullBit32Array<Integer>(entries.clone());
            for (int k = 0; k < 32; ++k) {
                assertEquals((Integer)k, full.find(k).getValue());
            }
            for (int k = 0; k < 32; ++k) {
                assertSame(full, full.assign(k, k));
            }
            for (int k = 0; k < 32; ++k) {
                full = full.assign(k, k + 1);
                assertEquals(true, full instanceof FullBit32Array);
            }
            for (int k = 0; k < 32; ++k) {
                assertEquals((Integer)(k + 1), full.find(k).getValue());
            }
            Bit32Array<Integer> std = full.delete(i);
            assertEquals(true, std instanceof StandardBit32Array);
            assertEquals(31, std.size());
            for (int k = 0; k < 32; ++k) {
                if (k == i) {
                    assertEquals(true, std.find(k).isEmpty());
                } else {
                    assertEquals((Integer)(k + 1), std.find(k).getValue());
                }
            }
        }
    }

    public void testCursor()
    {
        List<JImmutableMap.Entry<Integer, Integer>> expected = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        @SuppressWarnings("unchecked") Holder<Integer>[] entries = (Holder<Integer>[])new Holder[32];
        for (int k = 0; k < 32; ++k) {
            entries[k] = Holders.of(k);
            expected.add(MapEntry.of(k, k));
        }
        Bit32Array<Integer> full = new FullBit32Array<Integer>(entries.clone());
        StandardCursorTest.listCursorTest(expected, full.cursor());
    }
}
