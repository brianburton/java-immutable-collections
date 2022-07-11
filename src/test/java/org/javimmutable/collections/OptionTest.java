package org.javimmutable.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public class OptionTest
{
    private static final IOException error1 = new IOException("1");

    @Test
    public void testEmpty()
    {
        final Option<Integer> opt = Option.of();
        assertEquals("[]", opt.toString());
        assertEquals(Holders.of(), opt.toHolder());
        assertTrue(opt.isEmpty());
        assertFalse(opt.isNonEmpty());
        assertFalse(opt.isFilled());
        assertThrows(UnsupportedOperationException.class, opt::getValue);
        assertNull(opt.getValueOrNull());
        assertEquals(Integer.valueOf(2), opt.getValueOr(2));
        final AtomicInteger intVal1 = new AtomicInteger(0);
        final AtomicInteger intVal2 = new AtomicInteger(0);
        opt.ifPresent(intVal1::set);
        opt.ifPresentThrows(intVal2::set);
        assertEquals(0, intVal1.get());
        assertEquals(0, intVal2.get());
        assertEquals(Option.of(), opt.map(x -> x + 1));
        assertEquals(Option.of(), opt.mapThrows(x -> x + 1));
        assertEquals(Option.of(), opt.flatMap(x -> Option.of(x + 2)));
        assertEquals(Option.of(), opt.flatMapThrows(x -> Option.of(x + 2)));
        assertEquals(Integer.valueOf(2), opt.orElse(2));
        assertEquals(Integer.valueOf(2), opt.orElseGet(() -> 2));
        assertSame(error1, assertThrows(IOException.class, () -> opt.orElseThrow(() -> error1)));
    }

    @Test
    public void testFilled()
        throws Exception
    {
        final Option<Integer> opt = Option.of(1);
        assertEquals("[1]", opt.toString());
        assertEquals(Holders.of(1), opt.toHolder());
        assertFalse(opt.isEmpty());
        assertTrue(opt.isNonEmpty());
        assertTrue(opt.isFilled());
        assertEquals(Integer.valueOf(1), opt.getValue());
        assertEquals(Integer.valueOf(1), opt.getValueOrNull());
        assertEquals(Integer.valueOf(1), opt.getValueOr(2));
        final AtomicInteger intVal1 = new AtomicInteger(0);
        final AtomicInteger intVal2 = new AtomicInteger(0);
        opt.ifPresent(intVal1::set);
        opt.ifPresentThrows(intVal2::set);
        assertEquals(1, intVal1.get());
        assertEquals(1, intVal2.get());
        assertEquals(Option.of(2), opt.map(x -> x + 1));
        assertEquals(Option.of(2), opt.mapThrows(x -> x + 1));
        assertEquals(Option.of(3), opt.flatMap(x -> Option.of(x + 2)));
        assertEquals(Option.of(3), opt.flatMapThrows(x -> Option.of(x + 2)));
        assertEquals(Integer.valueOf(1), opt.orElse(2));
        assertEquals(Integer.valueOf(1), opt.orElseGet(() -> 2));
        assertEquals(Integer.valueOf(1), opt.orElseThrow(() -> error1));
    }
}
