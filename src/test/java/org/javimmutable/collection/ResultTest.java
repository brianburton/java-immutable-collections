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

package org.javimmutable.collection;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ResultTest
{
    @Test
    public void testConstruction()
    {
        final Exception error = new IOException();
        assertEquals(Result.success(10), Result.attempt(() -> 10));
        assertEquals(Result.failure(error), Result.attempt(() -> {
            throw error;
        }));
        assertThrows(IllegalArgumentException.class, () -> Result.failure(null).get());
    }

    @Test
    public void testSuccess()
        throws Exception
    {
        final Result<String> success = Result.success("10");
        assertEquals("10", success.get());
        assertEquals("10", success.orElse("5"));
        assertEquals("10", success.orElseGet(() -> "8"));

        final Exception error = new IOException();
        final RuntimeException runtimeError = new RuntimeException();
        assertEquals(Result.success(10), success.map(Integer::parseInt));
        assertEquals(Result.failure(error), success.map(x -> {
            throw error;
        }));
        assertEquals(Result.success(10), success.flatMap(x -> Result.success(10)));
        assertEquals(Result.failure(error), success.flatMap(x -> Result.failure(error)));
        assertEquals(Result.failure(runtimeError), success.flatMap(x -> {
            throw runtimeError;
        }));
        assertSame(success, success.mapFailure(ex -> "2"));
        assertSame(success, success.flatMapFailure(ex -> Result.success("2")));

        Temp.Var1<Integer> change = Temp.var(0);
        assertSame(success, success.apply(x -> change.x += Integer.parseInt(x)));
        assertEquals(Integer.valueOf(10), change.x);
        assertEquals(Result.failure(error), success.apply(x -> {
            throw error;
        }));

        assertEquals("10".hashCode(), success.hashCode());
        assertEquals(0, Result.success(null).hashCode());

        // equals
        assertEquals(true, success.equals(success));
        assertEquals(false, success.equals("8"));
        assertEquals(false, success.equals(null));
        assertEquals(true, Result.success(null).equals(Result.success(null)));
        assertEquals(false, Result.success(null).equals(success));
        assertEquals(false, success.equals(Result.success(null)));
        assertEquals(false, Result.success("x").equals(Result.success("y")));
        assertEquals(true, Result.success("x").equals(Result.success("x")));
        assertEquals(false, success.equals(Result.failure(error)));
        assertEquals(false, Result.failure(error).equals(success));
    }

    @Test
    public void testFailure()
        throws Exception
    {
        final Exception error1 = new IOException();
        final Result<String> failure = Result.failure(error1);
        assertSame(error1, assertThrows(IOException.class, () -> failure.get()));
        assertEquals("5", failure.orElse("5"));
        assertEquals("8", failure.orElseGet(() -> "8"));

        final Exception error2 = new IOException();
        final RuntimeException runtimeError = new RuntimeException();
        assertEquals(Result.failure(error1), failure.map(Integer::parseInt));
        assertEquals(Result.failure(error1), failure.map(x -> {
            throw error2;
        }));
        assertEquals(Result.failure(error1), failure.flatMap(x -> Result.success(10)));
        assertEquals(Result.failure(error1), failure.flatMap(x -> Result.failure(error2)));
        assertEquals(Result.failure(error1), failure.flatMap(x -> {
            throw runtimeError;
        }));
        assertEquals(Result.success("2"), failure.mapFailure(ex -> "2"));
        assertEquals(Result.failure(error2), failure.mapFailure(ex -> {
            throw error2;
        }));
        assertEquals(Result.success("6"), failure.flatMapFailure(ex -> Result.success("6")));
        assertEquals(Result.failure(error2), failure.flatMapFailure(ex -> {
            throw error2;
        }));

        Temp.Var1<Integer> change = Temp.var(0);
        assertSame(failure, failure.apply(x -> change.x += Integer.parseInt(x)));
        assertEquals(Integer.valueOf(0), change.x);
        assertSame(failure, failure.apply(x -> {
            throw error2;
        }));

        assertEquals(error1.hashCode(), failure.hashCode());

        // equals
        assertEquals(true, failure.equals(failure));
        assertEquals(false, failure.equals(Result.success("10")));
        assertTrue(Result.failure(error1).equals(Result.failure(error1)));
        assertFalse(Result.failure(error1).equals(Result.failure(error2)));
    }
}
