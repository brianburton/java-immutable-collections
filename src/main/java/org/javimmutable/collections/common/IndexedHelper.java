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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

/**
 * Provides a number of static utility methods for producing Indexed objects
 * from raw values.  Useful when you need an Indexed but don't have or want
 * to create an array just to get an Indexed of them.
 */
public class IndexedHelper
{
    private IndexedHelper()
    {
    }

    /**
     * Returns an Indexed containing a single value.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    public static <T, V extends T> Indexed<T> indexed(V a)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                switch (index) {
                case 0:
                    return a;
                default:
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            @Override
            public int size()
            {
                return 1;
            }
        };
    }

    /**
     * Returns an Indexed containing two values.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    public static <T, V extends T> Indexed<T> indexed(V a,
                                                      V b)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                switch (index) {
                case 0:
                    return a;
                case 1:
                    return b;
                default:
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            @Override
            public int size()
            {
                return 2;
            }
        };
    }

    /**
     * Returns an Indexed containing three values.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    public static <T, V extends T> Indexed<T> indexed(V a,
                                                      V b,
                                                      V c)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                switch (index) {
                case 0:
                    return a;
                case 1:
                    return b;
                case 2:
                    return c;
                default:
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            @Override
            public int size()
            {
                return 3;
            }
        };
    }
}
