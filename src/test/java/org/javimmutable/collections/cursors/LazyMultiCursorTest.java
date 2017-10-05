package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.common.IndexedArray;

import java.util.Arrays;

import static org.javimmutable.collections.cursors.StandardCursorTest.*;

@SuppressWarnings("unchecked")
public class LazyMultiCursorTest
    extends TestCase
{
    public void test()
    {
        emptyCursorTest(cursor(values()));
        emptyCursorTest(cursor(values(), values()));
        emptyCursorTest(cursor(values(), values(), values()));

        listCursorTest(Arrays.asList(1), cursor(values(1)));
        listCursorTest(Arrays.asList(1), cursor(values(), values(1)));
        listCursorTest(Arrays.asList(1), cursor(values(1), values()));

        listCursorTest(Arrays.asList(1, 2), cursor(values(1, 2)));
        listCursorTest(Arrays.asList(1, 2), cursor(values(1), values(2)));
        listCursorTest(Arrays.asList(1, 2), cursor(values(), values(1), values(), values(2), values()));

        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(1, 2, 3, 4)));
        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(1, 2), values(3, 4)));
        listCursorTest(Arrays.asList(1, 2, 3, 4), cursor(values(), values(1), values(2, 3), values(), values(4)));
    }

    private Cursor<Integer> cursor(Cursorable<Integer>... array)
    {
        return LazyMultiCursor.cursor(IndexedArray.retained(array));
    }

    private Cursorable<Integer> values(Integer... array)
    {
        return IterableCursorable.of(Arrays.asList(array));
    }
}
