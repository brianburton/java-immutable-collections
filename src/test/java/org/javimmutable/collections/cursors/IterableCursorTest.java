package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IterableCursorTest
        extends TestCase
{
    public void testEmptyIterator()
    {
        List<Integer> source = Collections.emptyList();
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.emptyCursorTest(cursor);

        // subsequent passes will use the next pointers
        assertEquals(source, collect(cursor.next()));
    }

    public void testSingleIterator()
    {
        List<Integer> source = Arrays.asList(1);
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will use the next pointers
        Cursor<Integer> advanced = cursor.next();
        assertEquals(source, collect(advanced));
        assertEquals(Collections.<Integer>emptyList(), collect(advanced.next()));
    }

    public void testMultiIterator()
    {
        List<Integer> source = Arrays.asList(1, 2, 3, 4);
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will use the next pointers
        Cursor<Integer> advanced = cursor.next();
        assertEquals(source, collect(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(2, 3, 4), collect(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(3, 4), collect(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(4), collect(advanced));
        advanced = advanced.next();
        assertEquals(Collections.<Integer>emptyList(), collect(advanced.next()));
    }

    private List<Integer> collect(Cursor<Integer> cursor)
    {
        List<Integer> answer = new ArrayList<Integer>();
        while (cursor.hasValue()) {
            answer.add(cursor.getValue());
            cursor = cursor.next();
        }
        return answer;
    }
}
