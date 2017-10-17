package org.javimmutable.collections.cursors;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitCursor;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.cursors.StandardCursorTest.listCursorTest;

public class ArrayCursorTest
    extends TestCase
{
    public void test()
    {
        StandardCursorTest.emptyCursorTest(cursor());
        listCursorTest(asList(1), cursor(1));
        listCursorTest(asList(2, 1), cursor(2, 1));
    }

    public void testSplitAllowed()
    {
        assertEquals(false, cursor().start().isSplitAllowed());
        assertEquals(false, cursor(1).start().isSplitAllowed());
        assertEquals(true, cursor(1, 2).start().isSplitAllowed());
        assertEquals(true, cursor(1, 2, 3).start().isSplitAllowed());
        assertEquals(true, cursor(1, 2, 3, 4).start().isSplitAllowed());
    }

    public void testSplit()
    {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> cursor().start().splitCursor());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> cursor(1).start().splitCursor());
        verifySplit(cursor(1, 2).start(), asList(1), asList(2));
        verifySplit(cursor(1, 2, 3).start(), asList(1), asList(2, 3));
        verifySplit(cursor(1, 2, 3, 4).start(), asList(1, 2), asList(3, 4));
    }

    private void verifySplit(Cursor<Integer> cursor,
                             List<Integer> left,
                             List<Integer> right)
    {
        SplitCursor<Integer> split = cursor.splitCursor();
        listCursorTest(left, split.getLeft());
        listCursorTest(right, split.getRight());
    }

    private Cursor<Integer> cursor(Integer... values)
    {
        return ArrayCursor.cursor(values);
    }
}
