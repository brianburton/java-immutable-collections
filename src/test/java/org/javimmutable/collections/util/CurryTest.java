package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;

public class CurryTest
        extends TestCase
{
    public void test()
    {
        assertEquals("abcd", Curry.of(new Func4<String, String, String, String, String>()
        {
            @Override
            public String apply(String a,
                                String b,
                                String c,
                                String d)
            {
                return a + b + c + d;
            }
        }, "a", "b", "c").apply("d"));

        assertEquals("abd", Curry.of(new Func3<String, String, String, String>()
        {
            @Override
            public String apply(String a,
                                String b,
                                String c)
            {
                return a + b + c;
            }
        }, "a", "b").apply("d"));

        assertEquals("ad", Curry.of(new Func2<String, String, String>()
        {
            @Override
            public String apply(String a,
                                String b)
            {
                return a + b;
            }
        }, "a").apply("d"));
    }
}
