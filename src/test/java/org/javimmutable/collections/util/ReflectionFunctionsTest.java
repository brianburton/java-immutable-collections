package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;
import org.javimmutable.collections.JImmutableList;

import static org.javimmutable.collections.util.ReflectionFunctions.method;
import static org.javimmutable.collections.util.ReflectionFunctions.staticMethod;

public class ReflectionFunctionsTest
        extends TestCase
{
    public void testFixedInstance()
    {
        CallMe obj = new CallMe("A");
        assertEquals("A:method0", method(obj, "method0").apply());
        obj.prefix = "B";
        assertEquals("B:12", method(obj, "method1", byte.class).apply((byte)12));
        obj.prefix = "C";
        assertEquals("C:12:54", method(obj, "method2", byte.class, short.class).apply((byte)12, (short)54));
        obj.prefix = "D";
        assertEquals("D:12:54:8632", method(obj, "method3", byte.class, short.class, int.class).apply((byte)12, (short)54, 8632));
        obj.prefix = "E";
        assertEquals("E:12:54:8632:99331122", method(obj, "method4", byte.class, short.class, int.class, long.class).apply((byte)12, (short)54, 8632, (long)99331122));
        Func0<String> f0 = method(obj, "method0");
        assertEquals("E:method0", f0.apply());
        obj.prefix = "F";
        assertEquals("F:method0", f0.apply());
    }

    public void testInstanceAsParam()
    {
        JImmutableList<CallMe> objects = JImmutables.list(new CallMe("a"), new CallMe("bb"), new CallMe("ccc"));
        Func1<CallMe, String> f1 = ReflectionFunctions.method("method0", CallMe.class);
        assertEquals(JImmutables.<String>list("a:method0", "bb:method0", "ccc:method0"), Functions.collectAll(objects.cursor(), JImmutables.<String>list(), f1));

        Func2<Byte, CallMe, String> f2 = ReflectionFunctions.method("method1", byte.class, CallMe.class);
        Func1<CallMe, String> curried = Curry.of(f2, (byte)8);
        assertEquals(JImmutables.<String>list("a:8", "bb:8", "ccc:8"), Functions.collectAll(objects.cursor(), JImmutables.<String>list(), curried));

        Func3<Byte, Short, CallMe, String> f3 = ReflectionFunctions.method("method2", byte.class, short.class, CallMe.class);
        curried = Curry.of(f3, (byte)8, (short)-40);
        assertEquals(JImmutables.<String>list("a:8:-40", "bb:8:-40", "ccc:8:-40"), Functions.collectAll(objects.cursor(), JImmutables.<String>list(), curried));

        Func4<Byte, Short, Integer, CallMe, String> f4 = ReflectionFunctions.method("method3", byte.class, short.class, int.class, CallMe.class);
        curried = Curry.of(f4, (byte)8, (short)-40, 876);
        assertEquals(JImmutables.<String>list("a:8:-40:876", "bb:8:-40:876", "ccc:8:-40:876"), Functions.collectAll(objects.cursor(), JImmutables.<String>list(), curried));
    }

    public void testStatic()
    {
        assertEquals("method0", staticMethod(getClass(), "staticMethod0").apply());
        assertEquals("12", staticMethod(getClass(), "staticMethod1", byte.class).apply((byte)12));
        assertEquals("12:54", staticMethod(getClass(), "staticMethod2", byte.class, short.class).apply((byte)12, (short)54));
        assertEquals("12:54:8632", staticMethod(getClass(), "staticMethod3", byte.class, short.class, int.class).apply((byte)12, (short)54, 8632));
        assertEquals("12:54:8632:99331122", staticMethod(getClass(), "staticMethod4", byte.class, short.class, int.class, long.class).apply((byte)12, (short)54, 8632, (long)99331122));
        Func0<String> f0 = staticMethod(getClass(), "staticMethod0");
        assertEquals("method0", f0.apply());

        Func2<Integer, Integer, Integer> minFunc = staticMethod(Math.class, "max", int.class, int.class);
        assertEquals((Integer)10, minFunc.apply(10, 8));
        assertEquals((Integer)10, minFunc.apply(8, 10));
        assertEquals((Integer)7, minFunc.apply(7, 7));
    }

    static class CallMe
    {
        private String prefix;

        private CallMe(String prefix)
        {
            this.prefix = prefix;
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
    }

    public static String staticMethod0()
    {
        return "method0";
    }

    public static String staticMethod1(byte b)
    {
        return String.valueOf(b);
    }

    public static String staticMethod2(byte b,
                                       short s)
    {
        return String.valueOf(b) + ":" + String.valueOf(s);
    }

    public static String staticMethod3(byte b,
                                       short s,
                                       int i)
    {
        return String.valueOf(b) + ":" + String.valueOf(s) + ":" + String.valueOf(i);
    }

    public static String staticMethod4(byte b,
                                       short s,
                                       int i,
                                       long n)
    {
        return String.valueOf(b) + ":" + String.valueOf(s) + ":" + String.valueOf(i) + ":" + String.valueOf(n);
    }
}
