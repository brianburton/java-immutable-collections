package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;

public class CurryTest
        extends TestCase
{
    public void testOf()
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

    public void testFunc3()
    {
        assertEquals("abcd", Curry.func3("a", new Func4<String, String, String, String, String>()
        {
            @Override
            public String apply(String p1,
                                String p2,
                                String p3,
                                String p4)
            {
                return p1 + p2 + p3 + p4;
            }
        }).apply("b", "c", "d"));
    }

    public void testFunc2()
    {
        assertEquals("abcd", Curry.func2("a", "b", new Func4<String, String, String, String, String>()
        {
            @Override
            public String apply(String p1,
                                String p2,
                                String p3,
                                String p4)
            {
                return p1 + p2 + p3 + p4;
            }
        }).apply("c", "d"));
        assertEquals("abc", Curry.func2("a", new Func3<String, String, String, String>()
        {
            @Override
            public String apply(String p1,
                                String p2,
                                String p3)
            {
                return p1 + p2 + p3;
            }
        }).apply("b", "c"));
    }

    public void testFunc1()
    {
        assertEquals("abcd", Curry.func1("a", "b", "c", new Func4<String, String, String, String, String>()
        {
            @Override
            public String apply(String p1,
                                String p2,
                                String p3,
                                String p4)
            {
                return p1 + p2 + p3 + p4;
            }
        }).apply("d"));
        assertEquals("abc", Curry.func1("a", "b", new Func3<String, String, String, String>()
        {
            @Override
            public String apply(String p1,
                                String p2,
                                String p3)
            {
                return p1 + p2 + p3;
            }
        }).apply("c"));
        assertEquals("ab", Curry.func1("a", new Func2<String, String, String>()
        {
            @Override
            public String apply(String p1,
                                String p2)
            {
                return p1 + p2;
            }
        }).apply("b"));
    }
}
