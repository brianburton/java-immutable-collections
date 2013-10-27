package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IteratorCursorTest
        extends TestCase
{
    public void testEmptyIterator()
    {
        List<Integer> source = Collections.emptyList();
        Cursor<Integer> cursor = IteratorCursor.of(source.iterator());

        // first pass will initialize the next pointers
        StandardCursorTest.emptyCursorTest(cursor);

        // subsequent passes will user the next pointers
        StandardCursorTest.emptyCursorTest(cursor);
        StandardCursorTest.emptyCursorTest(cursor);
    }

    public void testSingleIterator()
    {
        List<Integer> source = Arrays.asList(1);
        Cursor<Integer> cursor = IteratorCursor.of(source.iterator());

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will user the next pointers
        StandardCursorTest.listCursorTest(source, cursor);
    }

    public void testMultiIterator()
    {
        List<Integer> source = Arrays.asList(1, 2, 3, 4);
        Cursor<Integer> cursor = IteratorCursor.of(source.iterator());

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will user the next pointers
        StandardCursorTest.listCursorTest(source, cursor);
        StandardCursorTest.listCursorTest(source, cursor);
    }

    public void testEmptyIterable()
    {
        List<Integer> source = Collections.emptyList();
        Cursor<Integer> cursor = IteratorCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.emptyCursorTest(cursor);

        // subsequent passes will user the next pointers
        StandardCursorTest.emptyCursorTest(cursor);
        StandardCursorTest.emptyCursorTest(cursor);
    }

    public void testSingleIterable()
    {
        List<Integer> source = Arrays.asList(1);
        Cursor<Integer> cursor = IteratorCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will user the next pointers
        StandardCursorTest.listCursorTest(source, cursor);
    }

    public void testMultiIterable()
    {
        List<Integer> source = Arrays.asList(1, 2, 3, 4);
        Cursor<Integer> cursor = IteratorCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will user the next pointers
        StandardCursorTest.listCursorTest(source, cursor);
        StandardCursorTest.listCursorTest(source, cursor);
    }
}
