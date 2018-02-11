package org.javimmutable.collections.iterators;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.javimmutable.collections.iterators.IteratorHelper.*;

public class IteratorHelperTest
    extends TestCase
{
    public void testEquals()
    {
        assertEquals(true, iteratorEquals(iter(), iter()));
        assertEquals(true, iteratorEquals(iter(null), iter(null)));
        assertEquals(true, iteratorEquals(iter(1), iter(1)));
        assertEquals(true, iteratorEquals(iter(1, 2), iter(1, 2)));

        assertEquals(false, iteratorEquals(iter(), iter(1)));
        assertEquals(false, iteratorEquals(iter(1), iter()));
        assertEquals(false, iteratorEquals(iter(1), iter(1, 2)));
        assertEquals(false, iteratorEquals(iter(1, 2), iter(1)));
    }

    public void testHashCode()
    {
        assertEquals(0, iteratorHashCode(iter()));
        assertEquals(1, iteratorHashCode(iter(1)));
        assertEquals(33, iteratorHashCode(iter(1, 2)));
        assertEquals(1026, iteratorHashCode(iter(1, 2, 3)));
    }

    public void testToString()
    {
        assertEquals("[]", iteratorToString(iter()));
        assertEquals("[1]", iteratorToString(iter(1)));
        assertEquals("[1,2]", iteratorToString(iter(1, 2)));
        assertEquals("[1,2,3]", iteratorToString(iter(1, 2, 3)));
    }

    private Iterator<Integer> iter(Integer a)
    {
        return Arrays.asList(a).iterator();
    }

    private Iterator<Integer> iter(Integer a,
                                   Integer b)
    {
        return Arrays.asList(a, b).iterator();
    }

    private Iterator<Integer> iter(Integer a,
                                   Integer b,
                                   Integer c)
    {
        return Arrays.asList(a, b, c).iterator();
    }

    private Iterator<Integer> iter()
    {
        return Collections.emptyIterator();
    }
}
