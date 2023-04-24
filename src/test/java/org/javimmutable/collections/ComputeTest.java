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

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ComputeTest
    extends TestCase
{
    public void testEager()
        throws Exception
    {
        List<Object> values = new ArrayList<>();
        Compute<Boolean> cb = Compute.success(10)
            .apply(values::add)
            .map(x -> (double)(x + 1))
            .apply(values::add)
            .map(x -> x > 10);
        assertEquals(Boolean.TRUE, cb.get());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(Boolean.TRUE, cb.get());
        assertEquals(Arrays.asList(10, 11.0), values);

        values.clear();
        Compute<Boolean> cb2 = Compute.success(10)
            .apply(x -> {
                throw new IOException();
            })
            .map(x -> (double)(x + 1))
            .apply(values::add)
            .map(x -> x > 10);
        assertThatThrownBy(cb2::get).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);

        values.clear();
        Compute<Boolean> cb3 = Compute.success(10)
            .apply(values::add)
            .map(x -> (double)(x + 1))
            .apply(x -> {
                throw new IOException();
            })
            .map(x -> x > 10);
        assertThatThrownBy(cb3::get).isInstanceOf(IOException.class);
        assertEquals(Collections.singletonList(10), values);

        values.clear();
        Compute<String> cb4 = Compute.success(18)
            .flatMap(x -> Compute.success(x + 4))
            .apply(values::add)
            .flatMap(x -> Compute.success(String.valueOf(x)));
        assertEquals("22", cb4.get());
        assertEquals(Collections.singletonList(22), values);

        values.clear();
        Compute<String> cb5 = Compute.success(10)
            .flatMap(x -> Compute.success(1).map(y -> {
                throw new IOException();
            }))
            .apply(values::add)
            .flatMap(x -> Compute.success(String.valueOf(x)));
        assertThatThrownBy(cb5::get).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);
    }

    public void testLazy()
        throws Exception
    {
        List<Object> values = new ArrayList<>();
        Compute<Double> cb0 = Compute.compute(() -> 10)
            .apply(values::add)
            .map(x -> (double)(x + 1))
            .apply(values::add);
        assertEquals(11.0, cb0.get());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(11.0, cb0.get());
        assertEquals(Arrays.asList(10, 11.0, 10, 11.0), values);

        values.clear();
        Compute<Boolean> cb1 = cb0.map(x -> x > 10);
        assertEquals(Boolean.TRUE, cb1.get());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(Boolean.TRUE, cb1.get());
        assertEquals(Arrays.asList(10, 11.0, 10, 11.0), values);

        values.clear();
        Compute<Boolean> cb2 = Compute.compute(() -> 10)
            .apply(x -> {
                throw new IOException();
            })
            .map(x -> (double)(x + 1))
            .apply(values::add)
            .map(x -> x > 10);
        assertThatThrownBy(cb2::get).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);

        values.clear();
        Compute<Boolean> cb3 = Compute.compute(() -> 10)
            .apply(values::add)
            .map(x -> (double)(x + 1))
            .apply(x -> {
                throw new IOException();
            })
            .map(x -> x > 10);
        assertThatThrownBy(cb3::get).isInstanceOf(IOException.class);
        assertEquals(Collections.singletonList(10), values);

        values.clear();
        Compute<String> cb4 = Compute.compute(() -> 18)
            .flatMap(x -> Compute.compute(() -> x + 4))
            .apply(values::add)
            .flatMap(x -> Compute.compute(() -> String.valueOf(x)));
        assertEquals("22", cb4.get());
        assertEquals(Collections.singletonList(22), values);

        values.clear();
        Compute<String> cb5 = Compute.compute(() -> 10)
            .flatMap(x -> Compute.compute(() -> {
                throw new IOException();
            }))
            .apply(values::add)
            .flatMap(x -> Compute.compute(() -> String.valueOf(x)));
        assertThatThrownBy(cb5::get).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);
    }
}
