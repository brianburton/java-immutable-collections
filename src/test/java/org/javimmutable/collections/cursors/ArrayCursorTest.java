package org.javimmutable.collections.cursors;

import junit.framework.TestCase;

import java.util.Arrays;

public class ArrayCursorTest
    extends TestCase
{
    public void test()
    {
        StandardCursorTest.emptyCursorTest(ArrayCursor.cursor(new Integer[0]));
        StandardCursorTest.listCursorTest(Arrays.asList(1), ArrayCursor.cursor(new Integer[]{1}));
        StandardCursorTest.listCursorTest(Arrays.asList(2, 1), ArrayCursor.cursor(new Integer[]{2, 1}));
    }
}
