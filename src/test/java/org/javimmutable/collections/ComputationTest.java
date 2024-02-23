///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ComputationTest
    extends TestCase
{
    public void testLazy()
        throws Exception
    {
        List<Object> values = new ArrayList<>();
        Computation<Double> cb0 = Computation.of(() -> 10)
            .apply(values::add)
            .map(x -> (double)(x + 1))
            .apply(values::add);
        assertEquals(Result.success(11.0), cb0.compute());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(Result.success(11.0), cb0.compute());
        assertEquals(Arrays.asList(10, 11.0, 10, 11.0), values);

        values.clear();
        Computation<Boolean> cb1 = cb0.map(x -> x > 10);
        assertEquals(Result.success(Boolean.TRUE), cb1.compute());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(Result.success(Boolean.TRUE), cb1.compute());
        assertEquals(Arrays.asList(10, 11.0, 10, 11.0), values);

        values.clear();
        final IOException error = new IOException();
        Computation<Boolean> cb2 = Computation.success(10)
            .apply(x -> {
                throw error;
            })
            .map(x -> (double)(x + 1))
            .apply(values::add)
            .map(x -> x > 10);
        assertEquals(Result.failure(error), cb2.compute());
        assertEquals(Collections.emptyList(), values);

        values.clear();
        Computation<Boolean> cb3 = Computation.of(() -> 10)
            .apply(values::add)
            .map(x -> (double)(x + 1))
            .apply(x -> {
                throw error;
            })
            .map(x -> x > 10);
        assertEquals(Result.failure(error), cb3.compute());
        assertEquals(Collections.singletonList(10), values);

        values.clear();
        Computation<String> cb4 = Computation.success(18)
            .flatMap(x -> Computation.of(() -> x + 4))
            .apply(values::add)
            .flatMap(x -> Computation.of(() -> String.valueOf(x)));
        assertEquals(Result.success("22"), cb4.compute());
        assertEquals(Collections.singletonList(22), values);

        values.clear();
        Computation<String> cb5 = Computation.<String>failure(new IOException())
            .apply(values::add)
            .flatMap(x -> Computation.of(() -> String.valueOf(x)));
        assertThatThrownBy(cb5::call).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);
    }
}
