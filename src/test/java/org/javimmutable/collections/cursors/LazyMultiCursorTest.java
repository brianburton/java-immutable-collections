package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.IndexedList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void testBuilder()
    {
        LazyMultiCursor.Builder builder = LazyMultiCursor.builder(0);
        emptyCursorTest(builder.cursor());

        builder = LazyMultiCursor.builder(1);
        emptyCursorTest(builder.cursor());

        builder = LazyMultiCursor.builder(1);
        builder.insert(values(10));
        listCursorTest(Arrays.asList(10), builder.cursor());

        List<Cursorable<Integer>> list = new ArrayList<Cursorable<Integer>>();
        list.add(values(1, 2, 3));
        builder = LazyMultiCursor.builder(5);
        builder.insert(IndexedList.copied(list));
        builder.insert(values(4));
        list.clear();
        builder.insert(IndexedList.copied(list));
        list.add(values(5));
        list.add(values(6, 7));
        list.add(values(8));
        builder.insert(IndexedList.copied(list));
        builder.insert(values(9, 10));
        listCursorTest(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), builder.cursorable().cursor());

        listCursorTest(Arrays.asList(5, 6, 7, 8), LazyMultiCursor.cursorable(StandardCursor.of(IndexedList.copied(list))).cursor());
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
