///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;

/**
 * Contains static factory methods to produce Curried versions of functions.
 */
public final class Curry
{
    private Curry()
    {
    }

    /**
     * Produces a Curried Func1 that calls the provided Func4 passing it the fixed parameters
     * param1-param3 along with the actual parameter to the apply() method as the fourth parameter.
     */
    public static <P1, P2, P3, P4, R> Func1<P4, R> of(final Func4<P1, P2, P3, P4, R> function,
                                                      final P1 param1,
                                                      final P2 param2,
                                                      final P3 param3)
    {
        return value -> function.apply(param1, param2, param3, value);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func3 passing it the fixed parameters
     * param1-param2 along with the actual parameter to the apply() method as the third parameter.
     */
    public static <P1, P2, P3, R> Func1<P3, R> of(final Func3<P1, P2, P3, R> function,
                                                  final P1 param1,
                                                  final P2 param2)
    {
        return value -> function.apply(param1, param2, value);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func2 passing it the fixed parameter
     * param1 along with the actual parameter to the apply() method as the second parameter.
     */
    public static <P1, P2, R> Func1<P2, R> of(final Func2<P1, P2, R> function,
                                              final P1 param1)
    {
        return value -> function.apply(param1, value);
    }

    /**
     * Produces a Curried Func3 that calls the provided Func4 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     */
    public static <P1, P2, P3, P4, R> Func3<P2, P3, P4, R> func3(final P1 param1,
                                                                 final Func4<P1, P2, P3, P4, R> function)
    {
        return (param2, param3, param4) -> function.apply(param1, param2, param3, param4);
    }

    /**
     * Produces a Curried Func2 that calls the provided Func3 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     */
    public static <P1, P2, P3, R> Func2<P2, P3, R> func2(final P1 param1,
                                                         final Func3<P1, P2, P3, R> function)
    {
        return (param2, param3) -> function.apply(param1, param2, param3);
    }

    /**
     * Produces a Curried Func3 that calls the provided Func4 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     */
    public static <P1, P2, P3, P4, R> Func2<P3, P4, R> func2(final P1 param1,
                                                             final P2 param2,
                                                             final Func4<P1, P2, P3, P4, R> function)
    {
        return (param3, param4) -> function.apply(param1, param2, param3, param4);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func2 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     */
    public static <P1, P2, R> Func1<P2, R> func1(final P1 param1,
                                                 final Func2<P1, P2, R> function)
    {
        return of(function, param1);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func3 passing it the fixed parameters
     * param1 and param2 along with the actual parameters to the apply() method.
     */
    public static <P1, P2, P3, R> Func1<P3, R> func1(final P1 param1,
                                                     final P2 param2,
                                                     final Func3<P1, P2, P3, R> function)
    {
        return of(function, param1, param2);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func4 passing it the fixed parameters
     * param1 and param2 along with the actual parameters to the apply() method.
     */
    public static <P1, P2, P3, P4, R> Func1<P4, R> func1(final P1 param1,
                                                         final P2 param2,
                                                         final P3 param3,
                                                         final Func4<P1, P2, P3, P4, R> function)
    {
        return of(function, param1, param2, param3);
    }
}
