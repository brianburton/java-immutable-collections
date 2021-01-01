///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;
import org.javimmutable.collections.JImmutableList;

import static org.javimmutable.collections.util.ReflectionFunctions.*;

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
        assertEquals(JImmutables.list("a:method0", "bb:method0", "ccc:method0"), Functions.collectAll(objects.iterator(), JImmutables.list(), f1));

        Func2<Byte, CallMe, String> f2 = ReflectionFunctions.method("method1", byte.class, CallMe.class);
        Func1<CallMe, String> curried = Curry.of(f2, (byte)8);
        assertEquals(JImmutables.list("a:8", "bb:8", "ccc:8"), Functions.collectAll(objects.iterator(), JImmutables.list(), curried));

        Func3<Byte, Short, CallMe, String> f3 = ReflectionFunctions.method("method2", byte.class, short.class, CallMe.class);
        curried = Curry.of(f3, (byte)8, (short)-40);
        assertEquals(JImmutables.list("a:8:-40", "bb:8:-40", "ccc:8:-40"), Functions.collectAll(objects.iterator(), JImmutables.list(), curried));

        Func4<Byte, Short, Integer, CallMe, String> f4 = ReflectionFunctions.method("method3", byte.class, short.class, int.class, CallMe.class);
        curried = Curry.of(f4, (byte)8, (short)-40, 876);
        assertEquals(JImmutables.list("a:8:-40:876", "bb:8:-40:876", "ccc:8:-40:876"), Functions.collectAll(objects.iterator(), JImmutables.list(), curried));
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
            return prefix + ":" + b;
        }

        public String method2(byte b,
                              short s)
        {
            return prefix + ":" + b + ":" + s;
        }

        public String method3(byte b,
                              short s,
                              int i)
        {
            return prefix + ":" + b + ":" + s + ":" + i;
        }

        public String method4(byte b,
                              short s,
                              int i,
                              long n)
        {
            return prefix + ":" + b + ":" + s + ":" + i + ":" + n;
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
        return b + ":" + s;
    }

    public static String staticMethod3(byte b,
                                       short s,
                                       int i)
    {
        return b + ":" + s + ":" + i;
    }

    public static String staticMethod4(byte b,
                                       short s,
                                       int i,
                                       long n)
    {
        return b + ":" + s + ":" + i + ":" + n;
    }
}
