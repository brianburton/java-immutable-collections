package org.javimmutable.collections.cursors;

import junit.framework.TestCase;

import java.util.Arrays;

public class SingleValueCursorTest
        extends TestCase
{
    public void testVarious()
    {
        StandardCursorTest.listCursorTest(Arrays.<Integer>asList(1), SingleValueCursor.of(1));
    }
}
