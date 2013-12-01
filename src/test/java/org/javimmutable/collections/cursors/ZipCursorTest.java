package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class ZipCursorTest
        extends TestCase
{
    public void test()
    {
        StandardCursorTest.emptyCursorTest(ZipCursor.of(StandardCursor.forRange(1, 2), StandardCursor.of()));
        StandardCursorTest.emptyCursorTest(ZipCursor.of(StandardCursor.of(), StandardCursor.forRange(1, 2)));

        List<Tuple2<Integer, Integer>> expected = new ArrayList<Tuple2<Integer, Integer>>();
        expected.add(new Tuple2<Integer, Integer>(10, 20));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 20)));
        expected.add(new Tuple2<Integer, Integer>(11, 21));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 21)));
        expected.add(new Tuple2<Integer, Integer>(12, 22));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 22)));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 50)));

        expected.clear();
        expected.add(new Tuple2<Integer, Integer>(10, 20));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 10), StandardCursor.forRange(20, 50)));
        expected.add(new Tuple2<Integer, Integer>(11, 21));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 11), StandardCursor.forRange(20, 50)));
        expected.add(new Tuple2<Integer, Integer>(12, 22));
        StandardCursorTest.listCursorTest(expected, ZipCursor.of(StandardCursor.forRange(10, 12), StandardCursor.forRange(20, 50)));
    }
}
