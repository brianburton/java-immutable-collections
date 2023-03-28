///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collections;

/**
 * Collection of useful temporary object classes.  These can be useful as simple temporary arguments
 * to lambda expressions like reduce().  These classes have public fields and are not intended to
 * be used for any sort of long lived objects and certainly not as part of a data model.  However
 * they can serve as a useful way to pass a few temporary values to a reduce method or provide
 * a simple set of working variables that a lambda can update (working around the effectively
 * final restriction).
 */
public class Temp
{
    public static <A> Val1<A> val(A a)
    {
        return new Val1<>(a);
    }

    public static <A, B> Val2<A, B> val(A a,
                                        B b)
    {
        return new Val2<>(a, b);
    }

    public static <A, B, C> Val3<A, B, C> val(A a,
                                              B b,
                                              C c)
    {
        return new Val3<>(a, b, c);
    }

    public static <A, B, C, D> Val4<A, B, C, D> val(A a,
                                                    B b,
                                                    C c,
                                                    D d)
    {
        return new Val4<>(a, b, c, d);
    }

    public static class Val1<A>
    {
        public final A a;

        public Val1(A a)
        {
            this.a = a;
        }
    }

    public static class Val2<A, B>
    {
        public final A a;
        public final B b;

        public Val2(A a,
                    B b)
        {
            this.a = a;
            this.b = b;
        }
    }

    public static class Val3<A, B, C>
    {
        public final A a;
        public final B b;
        public final C c;

        public Val3(A a,
                    B b,
                    C c)
        {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }

    public static class Val4<A, B, C, D>
    {
        public final A a;
        public final B b;
        public final C c;
        public final D d;

        public Val4(A a,
                    B b,
                    C c,
                    D d)
        {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }
    }

    public static <X> Var1<X> var(X x)
    {
        return new Var1<>(x);
    }

    public static <X, Y> Var2<X, Y> var(X x,
                                        Y y)
    {
        return new Var2<>(x, y);
    }

    public static <X, Y, Z> Var3<X, Y, Z> var(X x,
                                              Y y,
                                              Z z)
    {
        return new Var3<>(x, y, z);
    }

    public static <W, X, Y, Z> Var4<W, X, Y, Z> var(W w,
                                                    X x,
                                                    Y y,
                                                    Z z)
    {
        return new Var4<>(w, x, y, z);
    }

    public static class Var1<X>
    {
        public X x;

        public Var1(X x)
        {
            this.x = x;
        }
    }

    public static class Var2<X, Y>
    {
        public X x;
        public Y y;

        public Var2(X x,
                    Y y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public static class Var3<X, Y, Z>
    {
        public X x;
        public Y y;
        public Z z;

        public Var3(X x,
                    Y y,
                    Z z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Var4<W, X, Y, Z>
    {
        public W w;
        public X x;
        public Y y;
        public Z z;

        public Var4(W w,
                    X x,
                    Y y,
                    Z z)
        {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static class Int1
    {
        public int a;

        public Int1(int a)
        {
            this.a = a;
        }
    }

    public static Int1 intVar(int a)
    {
        return new Int1(a);
    }
}
