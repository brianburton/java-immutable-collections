package org.javimmutable.collections;

import junit.framework.TestCase;

public class Tuple2Test
        extends TestCase
{
    public void test()
    {
        Tuple2<Integer, String> a10 = new Tuple2<Integer, String>(10, "a");
        Tuple2<Integer, String> a12 = new Tuple2<Integer, String>(12, "a");
        Tuple2<Integer, String> b10 = new Tuple2<Integer, String>(12, "b");
        assertEquals(true, a10.equals(a10));
        assertEquals(true, a10.equals(new Tuple2<Integer, String>(10, "a")));
        assertEquals(false, a10.equals(a12));
        assertEquals(false, a10.equals(b10));
    }
}
