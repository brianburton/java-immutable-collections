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

package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StandardSerializableTests
    extends TestCase
{
    /**
     * Iterator factory for Iterable objects that just calls iterator() method.
     */
    public static final Func1<Object, Iterator> standardIteratorFactory = o -> ((Iterable)o).iterator();

    /**
     * Intended for unit tests of non-iterable objects.  Just performs normal test and
     * backwards compatibility test.
     */
    public static void verifySerializable(@Nonnull Object source,
                                          @Nonnull String oldSerializedBase64)
        throws Exception
    {
        byte[] bytes = serialize(source);
        Object dest = deserialize(bytes);
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);

        dest = deserialize(serialize(dest));
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);

        try {
            dest = deserialize(decode(oldSerializedBase64));
        } catch (Exception ex) {
            fail(encode(bytes));
        }
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);
    }

    /**
     * Intended for unit tests.  Performs iterator and extra tests as well as backwards compatibility test.
     */
    public static void verifySerializable(@Nonnull Func1<Object, Iterator> iteratorFactory,
                                          @Nullable BiConsumer extraChecks,
                                          @Nonnull Object source,
                                          @Nonnull String oldSerializedBase64)
        throws Exception
    {
        byte[] bytes = serialize(source);
        Object dest = deserialize(bytes);
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);
        performExtraChecks(extraChecks, source, dest);
        verifyIterator(iteratorFactory.apply(source), iteratorFactory.apply(dest));

        dest = deserialize(serialize(dest));
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);
        performExtraChecks(extraChecks, source, dest);
        verifyIterator(iteratorFactory.apply(source), iteratorFactory.apply(dest));

        try {
            dest = deserialize(decode(oldSerializedBase64));
        } catch (Exception ex) {
            fail(encode(bytes));
        }
        assertEquals(source.getClass().getName(), dest.getClass().getName());
        assertEquals(source, dest);
        performExtraChecks(extraChecks, source, dest);
        verifyIterator(iteratorFactory.apply(source), iteratorFactory.apply(dest));
    }

    /**
     * Intended for use by stress tester.  Performs extra checks but not a backwards compatibility check.
     */
    public static void verifySerializable(@Nullable BiConsumer extraChecks,
                                          @Nonnull Object source)
    {
        try {
            byte[] bytes = serialize(source);
            Object dest = deserialize(bytes);
            assertEquals(source.getClass().getName(), dest.getClass().getName());
            assertEquals(source, dest);
            performExtraChecks(extraChecks, source, dest);

            dest = deserialize(serialize(dest));
            assertEquals(source.getClass().getName(), dest.getClass().getName());
            assertEquals(source, dest);
            performExtraChecks(extraChecks, source, dest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private static void performExtraChecks(@Nullable BiConsumer extraChecks,
                                           @Nonnull Object source,
                                           @Nonnull Object dest)
    {
        if (extraChecks != null) {
            extraChecks.accept(source, dest);
        }
    }

    private static byte[] serialize(Object source)
        throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(bytes))) {
            out.writeObject(source);
        }
        return bytes.toByteArray();
    }

    private static Object deserialize(byte[] source)
        throws Exception
    {
        try (ObjectInputStream inp = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(source)))) {
            return inp.readObject();
        }
    }

    private static void verifyUnorderedIterator(@Nonnull Func1<Object, Iterator> iteratorFactory,
                                                Object source,
                                                Object dest)
    {
        ExpectedOrderSorter<Object> sorter = new ExpectedOrderSorter(iteratorFactory.apply(source));
        Iterator expected = iteratorFactory.apply(source);
        Iterator actual = sorter.sort(iteratorFactory.apply(dest), i -> i);
        while (expected.hasNext()) {
            assertEquals(expected.hasNext(), actual.hasNext());
            assertEquals(expected.next(), actual.next());
        }
        assertEquals(expected.hasNext(), actual.hasNext());
    }

    private static void verifyIterator(Iterator expected,
                                       Iterator actual)
    {
        while (expected.hasNext()) {
            assertEquals(expected.hasNext(), actual.hasNext());
            assertEquals(expected.next(), actual.next());
        }
        assertEquals(expected.hasNext(), actual.hasNext());
    }

    private static String encode(byte[] bytes)
        throws NoSuchAlgorithmException
    {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] decode(String base64)
        throws Exception
    {
        return Base64.getDecoder().decode(base64);
    }
}
