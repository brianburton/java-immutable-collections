///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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
