package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardCursorTest
        extends TestCase
{
    public static void testVarious()
    {
        listCursorTest(Collections.<Integer>emptyList(), 0, StandardCursor.<Integer>of());
        listCursorTest(Arrays.<Integer>asList(1), 1, StandardCursor.forRange(1, 1));
        listCursorTest(Arrays.<Integer>asList(1, 2), 2, StandardCursor.forRange(1, 2));
        listCursorTest(Arrays.<Integer>asList(-1, 0, 1, 2, 3), 5, StandardCursor.forRange(-1, 3));
        assertEquals(Arrays.<Integer>asList(-1, 0, 1, 2, 3), StandardCursor.makeList(StandardCursor.forRange(-1, 3)));
    }

    public static <T> void cursorTest(Func1<Integer, T> lookup,
                                      int size,
                                      Cursor<T> cursor)
    {
        // have to call next() before other methods
        try {
            cursor.hasValue();
            fail();
        } catch (Exception ex) {
            // expected
        }
        try {
            cursor.getValue();
            fail();
        } catch (Exception ex) {
            // expected
        }

        // calling next advances through entire sequence
        for (int i = 0; i < size; ++i) {
            cursor = cursor.next();
            assertEquals(true, cursor.hasValue());
            assertEquals(lookup.apply(i), cursor.getValue());
        }

        // after expected sequence has no values
        cursor = cursor.next();
        assertEquals(false, cursor.hasValue());

        // safe to call multiple times once at end
        assertEquals(false, cursor.hasValue());
        assertEquals(false, cursor.hasValue());
    }

    public static <T> void indexedCursorTest(Indexed<T> indexed,
                                             int size,
                                             Cursor<T> cursor)
    {
        cursorTest(new IndexedLookup<T>(indexed), size, cursor);
    }

    public static <T> void listCursorTest(List<T> list,
                                          int size,
                                          Cursor<T> cursor)
    {
        cursorTest(new ListLookup<T>(list), size, cursor);
    }

    private static class ListLookup<T>
            implements Func1<Integer, T>
    {
        private final List<T> list;

        private ListLookup(List<T> list)
        {
            this.list = list;
        }

        @Override
        public T apply(Integer value)
        {
            return list.get(value);
        }
    }

    private static class IndexedLookup<T>
            implements Func1<Integer, T>
    {
        private final Indexed<T> indexed;

        private IndexedLookup(Indexed<T> indexed)
        {
            this.indexed = indexed;
        }

        @Override
        public T apply(Integer value)
        {
            return indexed.get(value);
        }
    }
}
