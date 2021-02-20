package org.javimmutable.collections;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class CalcTest
    extends TestCase
{
    public void testEager()
        throws Exception
    {
        List<Object> values = new ArrayList<>();
        Calc<Boolean> cb = Calc.eager(10)
            .apply(values::add)
            .next(x -> (double)(x + 1))
            .apply(values::add)
            .next(x -> x > 10);
        assertEquals(Boolean.TRUE, cb.get());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(Boolean.TRUE, cb.get());
        assertEquals(Arrays.asList(10, 11.0), values);

        values.clear();
        Calc<Boolean> cb2 = Calc.eager(10)
            .apply(x -> {
                throw new IOException();
            })
            .next(x -> (double)(x + 1))
            .apply(values::add)
            .next(x -> x > 10);
        assertThatThrownBy(cb2::get).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);

        values.clear();
        Calc<Boolean> cb3 = Calc.eager(10)
            .apply(values::add)
            .next(x -> (double)(x + 1))
            .apply(x -> {
                throw new IOException();
            })
            .next(x -> x > 10);
        assertThatThrownBy(cb3::get).isInstanceOf(IOException.class);
        assertEquals(Collections.singletonList(10), values);
    }

    public void testLazy()
        throws Exception
    {
        List<Object> values = new ArrayList<>();
        Calc<Double> cb0 = Calc.lazy(() -> 10)
            .apply(values::add)
            .next(x -> (double)(x + 1))
            .apply(values::add);
        assertEquals(11.0, cb0.get());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(11.0, cb0.get());
        assertEquals(Arrays.asList(10, 11.0, 10, 11.0), values);

        values.clear();
        Calc<Boolean> cb1 = cb0.next(x -> x > 10);
        assertEquals(Boolean.TRUE, cb1.get());
        assertEquals(Arrays.asList(10, 11.0), values);
        assertEquals(Boolean.TRUE, cb1.get());
        assertEquals(Arrays.asList(10, 11.0, 10, 11.0), values);

        values.clear();
        Calc<Boolean> cb2 = Calc.lazy(() -> 10)
            .apply(x -> {
                throw new IOException();
            })
            .next(x -> (double)(x + 1))
            .apply(values::add)
            .next(x -> x > 10);
        assertThatThrownBy(cb2::get).isInstanceOf(IOException.class);
        assertEquals(Collections.emptyList(), values);

        values.clear();
        Calc<Boolean> cb3 = Calc.lazy(() -> 10)
            .apply(values::add)
            .next(x -> (double)(x + 1))
            .apply(x -> {
                throw new IOException();
            })
            .next(x -> x > 10);
        assertThatThrownBy(cb3::get).isInstanceOf(IOException.class);
        assertEquals(Collections.singletonList(10), values);
    }
}
