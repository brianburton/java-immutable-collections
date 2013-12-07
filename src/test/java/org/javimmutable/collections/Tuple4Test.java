package org.javimmutable.collections;

import junit.framework.TestCase;

public class Tuple4Test
        extends TestCase
{
    public void test()
    {
        Tuple4<String, Integer, String, Integer> a10b900 = new Tuple4<String, Integer, String, Integer>("a", 10, "b", 900);
        Tuple4<String, Integer, String, Integer> a12b900 = new Tuple4<String, Integer, String, Integer>("a", 12, "b", 900);
        Tuple4<String, Integer, String, Integer> a10c900 = new Tuple4<String, Integer, String, Integer>("a", 10, "c", 900);
        Tuple4<String, Integer, String, Integer> b10b900 = new Tuple4<String, Integer, String, Integer>("b", 10, "b", 900);
        Tuple4<String, Integer, String, Integer> a10b1200 = new Tuple4<String, Integer, String, Integer>("a", 10, "b", 1200);
        assertEquals(true, a10b900.equals(a10b900));
        assertEquals(true, a10b900.equals(new Tuple4<String, Integer, String, Integer>("a", 10, "b", 900)));
        assertEquals(false, a10b900.equals(a12b900));
        assertEquals(false, a10b900.equals(a10c900));
        assertEquals(false, a10b900.equals(b10b900));
        assertEquals(false, a10b900.equals(a10b1200));
    }
}
