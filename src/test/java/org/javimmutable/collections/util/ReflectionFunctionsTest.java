package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func2;

import static org.javimmutable.collections.util.ReflectionFunctions.method;
import static org.javimmutable.collections.util.ReflectionFunctions.staticMethod;

public class ReflectionFunctionsTest
        extends TestCase
{
    private String prefix;

    public void testNonStatic()
    {
        prefix = "A";
        assertEquals("A:method0", method(this, "method0").apply());
        prefix = "B";
        assertEquals("B:12", method(this, "method1", byte.class).apply((byte)12));
        prefix = "C";
        assertEquals("C:12:54", method(this, "method2", byte.class, short.class).apply((byte)12, (short)54));
        prefix = "D";
        assertEquals("D:12:54:8632", method(this, "method3", byte.class, short.class, int.class).apply((byte)12, (short)54, 8632));
        prefix = "E";
        assertEquals("E:12:54:8632:99331122", method(this, "method4", byte.class, short.class, int.class, long.class).apply((byte)12, (short)54, 8632, (long)99331122));
        Func0<String> f0 = method(this, "method0");
        assertEquals("E:method0", f0.apply());
        prefix = "F";
        assertEquals("F:method0", f0.apply());
    }

    public void testStatic()
    {
        prefix = "A";
        assertEquals("method0", staticMethod(getClass(), "smethod0").apply());
        prefix = "B";
        assertEquals("12", staticMethod(getClass(), "smethod1", byte.class).apply((byte)12));
        prefix = "C";
        assertEquals("12:54", staticMethod(getClass(), "smethod2", byte.class, short.class).apply((byte)12, (short)54));
        prefix = "D";
        assertEquals("12:54:8632", staticMethod(getClass(), "smethod3", byte.class, short.class, int.class).apply((byte)12, (short)54, 8632));
        prefix = "E";
        assertEquals("12:54:8632:99331122", staticMethod(getClass(), "smethod4", byte.class, short.class, int.class, long.class).apply((byte)12, (short)54, 8632, (long)99331122));
        Func0<String> f0 = staticMethod(getClass(), "smethod0");
        assertEquals("method0", f0.apply());
        prefix = "F";
        assertEquals("method0", f0.apply());

        Func2<Integer, Integer, Integer> minFunc = staticMethod(Math.class, "max", int.class, int.class);
        assertEquals((Integer)10, minFunc.apply(10, 8));
        assertEquals((Integer)10, minFunc.apply(8, 10));
        assertEquals((Integer)7, minFunc.apply(7, 7));
    }

    public String method0()
    {
        return prefix + ":method0";
    }

    public String method1(byte b)
    {
        return prefix + ":" + String.valueOf(b);
    }

    public String method2(byte b,
                          short s)
    {
        return prefix + ":" + String.valueOf(b) + ":" + String.valueOf(s);
    }

    public String method3(byte b,
                          short s,
                          int i)
    {
        return prefix + ":" + String.valueOf(b) + ":" + String.valueOf(s) + ":" + String.valueOf(i);
    }

    public String method4(byte b,
                          short s,
                          int i,
                          long n)
    {
        return prefix + ":" + String.valueOf(b) + ":" + String.valueOf(s) + ":" + String.valueOf(i) + ":" + String.valueOf(n);
    }

    public static String smethod0()
    {
        return "method0";
    }

    public static String smethod1(byte b)
    {
        return String.valueOf(b);
    }

    public static String smethod2(byte b,
                                  short s)
    {
        return String.valueOf(b) + ":" + String.valueOf(s);
    }

    public static String smethod3(byte b,
                                  short s,
                                  int i)
    {
        return String.valueOf(b) + ":" + String.valueOf(s) + ":" + String.valueOf(i);
    }

    public static String smethod4(byte b,
                                  short s,
                                  int i,
                                  long n)
    {
        return String.valueOf(b) + ":" + String.valueOf(s) + ":" + String.valueOf(i) + ":" + String.valueOf(n);
    }
}
