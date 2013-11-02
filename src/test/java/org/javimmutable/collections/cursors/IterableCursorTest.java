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
    public void testEmptyIterable()
    {
        List<Integer> source = Collections.emptyList();
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.emptyCursorTest(cursor);

        // subsequent passes will use the next pointers
        assertEquals(source, remainingValuesAsList(cursor.next()));
    }

    public void testSingleValueIterable()
    {
        List<Integer> source = Arrays.asList(1);
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will use the next pointers
        Cursor<Integer> advanced = cursor.next();
        assertEquals(source, remainingValuesAsList(advanced));
        assertEquals(Collections.<Integer>emptyList(), remainingValuesAsList(advanced.next()));
    }

    public void testMultiValueIterable()
    {
        List<Integer> source = Arrays.asList(1, 2, 3, 4);
        Cursor<Integer> cursor = IterableCursor.of(source);

        // first pass will initialize the next pointers
        StandardCursorTest.listCursorTest(source, cursor);

        // subsequent passes will use the next pointers
        Cursor<Integer> advanced = cursor.next();
        assertEquals(source, remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(2, 3, 4), remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(3, 4), remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Arrays.asList(4), remainingValuesAsList(advanced));
        advanced = advanced.next();
        assertEquals(Collections.<Integer>emptyList(), remainingValuesAsList(advanced.next()));
    }

    private List<Integer> remainingValuesAsList(Cursor<Integer> cursor)
    {
        List<Integer> answer = new ArrayList<Integer>();
        while (cursor.hasValue()) {
            answer.add(cursor.getValue());
            cursor = cursor.next();
        }
        return answer;
    }
}
