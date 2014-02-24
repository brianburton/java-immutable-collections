package org.javimmutable.collections.array.trie32;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Trie32ArrayTest
        extends TestCase
{
    public void testCursor()
    {
        Trie32Array<Integer> array = Trie32Array.of();
        array = array.assign(-500, -5001).assign(-10, -101).assign(-1, -11).assign(0, 0).assign(1, 11).assign(10, 101).assign(500, 5001);

        List<Integer> indexes = Arrays.asList(-500, -10, -1, 0, 1, 10, 500);
        StandardCursorTest.listCursorTest(indexes, array.keysCursor());

        List<Integer> values = Arrays.asList(-5001, -101, -11, 0, 11, 101, 5001);
        StandardCursorTest.listCursorTest(values, array.valuesCursor());

        List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        entries.add(MapEntry.of(-500, -5001));
        entries.add(MapEntry.of(-10, -101));
        entries.add(MapEntry.of(-1, -11));
        entries.add(MapEntry.of(0, 0));
        entries.add(MapEntry.of(1, 11));
        entries.add(MapEntry.of(10, 101));
        entries.add(MapEntry.of(500, 5001));
        StandardCursorTest.listCursorTest(entries, array.cursor());
    }
}
