package org.javimmutable.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

public class ResultTest
{
    private final Exception error1 = new IOException("1");
    private final Exception error2 = new IOException("2");

    @Test
    public void testValue()
        throws Exception
    {
        final Result<Integer> result = Result.value(1);
        assertEquals("[value:1]", result.toString());
        assertEquals("[value:null]", Result.value(null).toString());
        assertEquals(Result.Kind.Value, result.getKind());
        assertTrue(result.isValue());
        assertFalse(result.isError());
        assertEquals(Integer.valueOf(1), result.getValue());
        assertThrows(UnsupportedOperationException.class, result::getError);
        assertEquals(Holders.of(1), result.toHolder());
        assertEquals(Option.of(1), result.toOption());
        assertEquals(Integer.valueOf(1), result.getValueOr(2));
        assertEquals(Result.value(2), result.map(x -> x + 1));
        assertEquals(Result.error(error2), result.map(x -> {
            throw error2;
        }));
        assertEquals(Result.value(2), result.flatMap(x -> Result.value(x + 1)));
        assertEquals(Result.error(error2), result.flatMap(x -> {
            throw error2;
        }));
        final AtomicReference<Exception> errVal1 = new AtomicReference<>();
        final AtomicReference<Exception> errVal2 = new AtomicReference<>();
        final AtomicReference<Exception> errVal3 = new AtomicReference<>();
        assertSame(result, result.mapError(e -> {
            errVal1.set(e);
            return error2;
        }));
        assertSame(result, result.mapError(e -> {
            errVal1.set(e);
            throw error2;
        }));
        assertNull(errVal1.get());
        assertEquals(result, result.flatMapError(e -> {
            errVal2.set(e);
            return Result.<Integer>error(error2);
        }));
        assertEquals(result, result.flatMapError(e -> {
            errVal2.set(e);
            throw error2;
        }));
        assertNull(errVal2.get());
        assertEquals(Integer.valueOf(1), result.orElse(2));
        assertEquals(Integer.valueOf(1), result.orElseGet(() -> 2));
        assertEquals(Integer.valueOf(1), result.orElseThrow(() -> error2));
        final AtomicInteger intVal = new AtomicInteger(0);
        assertSame(result, result.ifValue(x -> intVal.set(3)));
        assertEquals(3, intVal.get());
        assertSame(result, result.ifError(errVal3::set));
        assertNull(errVal3.get());
    }

    @Test
    public void testError()
    {
        final Result<Integer> result = Result.error(error1);
        assertEquals("[error:java.io.IOException]", result.toString());
        assertEquals(Result.Kind.Error, result.getKind());
        assertFalse(result.isValue());
        assertTrue(result.isError());
        assertThrows(UnsupportedOperationException.class, result::getValue);
        assertEquals(error1, result.getError());
        assertEquals(Holders.of(), result.toHolder());
        assertEquals(Option.of(), result.toOption());
        assertEquals(Integer.valueOf(2), result.getValueOr(2));
        assertSame(result, result.map(x -> x + 1));
        assertSame(result, result.map(x -> {
            throw error2;
        }));
        assertSame(result, result.flatMap(x -> Result.value(x + 1)));
        assertSame(result, result.flatMap(x -> {
            throw error2;
        }));
        final AtomicReference<Exception> errVal1 = new AtomicReference<>();
        final AtomicReference<Exception> errVal2 = new AtomicReference<>();
        final AtomicReference<Exception> errVal3 = new AtomicReference<>();
        assertEquals(Result.error(error2), result.mapError(e -> {
            errVal1.set(e);
            return error2;
        }));
        assertSame(error1, errVal1.get());
        errVal1.set(null);
        assertEquals(Result.error(error2), result.mapError(e -> {
            errVal1.set(e);
            throw error2;
        }));
        assertSame(error1, errVal1.get());
        assertEquals(Result.error(error2), result.flatMapError(e -> {
            errVal2.set(e);
            return Result.<Integer>error(error2);
        }));
        assertSame(error1, errVal2.get());
        errVal1.set(null);
        assertEquals(Result.error(error2), result.flatMapError(e -> {
            errVal2.set(e);
            throw error2;
        }));
        assertSame(error1, errVal2.get());
        assertEquals(Integer.valueOf(2), result.orElse(2));
        assertEquals(Integer.valueOf(2), result.orElseGet(() -> 2));
        assertSame(error2, assertThrows(IOException.class, () -> result.orElseThrow(() -> error2)));
        final AtomicInteger intVal = new AtomicInteger(0);
        assertSame(result, result.ifValue(x -> intVal.set(3)));
        assertEquals(0, intVal.get());
        assertSame(result, result.ifError(errVal3::set));
        assertSame(error1, errVal3.get());
    }
}
