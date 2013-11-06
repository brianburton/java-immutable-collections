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
