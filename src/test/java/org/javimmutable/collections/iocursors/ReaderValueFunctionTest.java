///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.iocursors;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.cursors.StandardCursor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

public class ReaderValueFunctionTest
        extends TestCase
{
    public void testReadLines()
            throws Exception
    {
        CloseableCursor<String> cursor = CloseableValueFunctionCursor.of(new StringReaderFactory("abc\ndef\nghi"));
        try {
            Assert.assertEquals(Arrays.asList("abc", "def", "ghi"), StandardCursor.makeList(cursor));
        } finally {
            cursor.close();
            // multiple calls ok
            cursor.close();
        }
    }

    private static class StringReaderFactory
            implements CloseableValueFunctionFactory<String, LineReaderFunction>
    {
        private final String source;

        private StringReaderFactory(String source)
        {
            this.source = source;
        }

        @Override
        public LineReaderFunction createFunction()
        {
            return new LineReaderFunction(new BufferedReader(new StringReader(source)));
        }
    }

    private static class LineReaderFunction
            extends ReaderValueFunction<BufferedReader, String>
    {
        private LineReaderFunction(BufferedReader reader)
        {
            super(reader);
        }

        @Override
        protected Holder<String> readValue(BufferedReader reader)
        {
            try {
                return Holders.fromNullable(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
