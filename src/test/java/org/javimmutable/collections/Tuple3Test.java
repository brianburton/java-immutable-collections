package org.javimmutable.collections;

import junit.framework.TestCase;

public class Tuple3Test
        extends TestCase
{
    public void test()
    {
        Tuple3<String, Integer, String> a10b = new Tuple3<String, Integer, String>("a", 10, "b");
        Tuple3<String, Integer, String> a12b = new Tuple3<String, Integer, String>("a", 12, "b");
        Tuple3<String, Integer, String> a10c = new Tuple3<String, Integer, String>("a", 10, "c");
        Tuple3<String, Integer, String> b10b = new Tuple3<String, Integer, String>("b", 10, "b");
        assertEquals(true, a10b.equals(a10b));
        assertEquals(true, a10b.equals(Tuple3.of("a", 10, "b")));
        assertEquals(false, a10b.equals(a12b));
        assertEquals(false, a10b.equals(a10c));
        assertEquals(false, a10b.equals(b10b));
    }
}
