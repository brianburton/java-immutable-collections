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

package org.javimmutable.collections.util;

import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class Zip
{
    public static <A, B> void forEach(@Nonnull Iterable<A> a,
                                      @Nonnull Iterable<B> b,
                                      @Nonnull Proc2<A, B> operation)
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            operation.apply(ai.next(), bi.next());
        }
    }

    public static <A, B, E extends Exception> void forEachThrows(@Nonnull Iterable<A> a,
                                                                 @Nonnull Iterable<B> b,
                                                                 @Nonnull Proc2Throws<A, B, E> operation)
        throws E
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            operation.apply(ai.next(), bi.next());
        }
    }

    public static <A, B, R> R reduce(R sum,
                                     @Nonnull Iterable<A> a,
                                     @Nonnull Iterable<B> b,
                                     @Nonnull Sum2<A, B, R> operation)
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            sum = operation.apply(sum, ai.next(), bi.next());
        }
        return sum;
    }

    public static <A, B, R, E extends Exception> R reduceThrows(R sum,
                                                                @Nonnull Iterable<A> a,
                                                                @Nonnull Iterable<B> b,
                                                                @Nonnull Sum2Throws<A, B, R, E> operation)
        throws E
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            sum = operation.apply(sum, ai.next(), bi.next());
        }
        return sum;
    }

}
