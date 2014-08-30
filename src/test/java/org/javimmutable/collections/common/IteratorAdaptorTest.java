package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorAdaptorTest
        extends TestCase
{
    public void testNextOnly()
    {
        Cursor<Integer> cursor = StandardCursor.forRange(1, 3);
        Iterator<Integer> iterator = IteratorAdaptor.of(cursor);
        assertEquals(Integer.valueOf(1), iterator.next());
        assertEquals(Integer.valueOf(2), iterator.next());
        assertEquals(Integer.valueOf(3), iterator.next());
        // calling next() at end throws
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
            // expected
        }
    }

    public void testStandardFeatures()
    {
        StandardCursorTest.listIteratorTest(Arrays.<Integer>asList(), IteratorAdaptor.of(StandardCursor.<Integer>of()));
        StandardCursorTest.listIteratorTest(Arrays.asList(1), IteratorAdaptor.of(StandardCursor.forRange(1, 1)));
        StandardCursorTest.listIteratorTest(Arrays.asList(1, 2), IteratorAdaptor.of(StandardCursor.forRange(1, 2)));
        StandardCursorTest.listIteratorTest(Arrays.asList(1, 2, 3), IteratorAdaptor.of(StandardCursor.forRange(1, 3)));
    }
}
