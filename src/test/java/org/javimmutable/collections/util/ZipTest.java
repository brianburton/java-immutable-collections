package org.javimmutable.collections.util;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.javimmutable.collections.util.JImmutables.*;

public class ZipTest
    extends TestCase
{
    public void test()
    {
        AtomicInteger sum = new AtomicInteger();
        Zip.forEach(list(1, 2), list(3, 4), (a, b) -> sum.addAndGet(a + b));
        assertEquals(10, sum.get());
        assertEquals(Integer.valueOf(10), Zip.reduce(0, list(1, 2), list(3, 4), ZipTest::adder));
    }

    public void testThrows()
        throws IOException
    {
        AtomicInteger sum = new AtomicInteger();
        Zip.forEachThrows(list(1, 2), list(3, 4), (a, b) -> {
            if (a == -1) {
                throw new IOException();
            }
            sum.addAndGet(a + b);
        });
        assertEquals(10, sum.get());
        assertEquals(Integer.valueOf(10), Zip.reduceThrows(0, list(1, 2), list(3, 4), ZipTest::adderThrows));
    }

    private static Integer adder(Integer s,
                                 Integer a,
                                 Integer b)
    {
        return s + a + b;
    }

    private static Integer adderThrows(Integer s,
                                       Integer a,
                                       Integer b)
        throws IOException
    {
        return s + a + b;
    }
}
