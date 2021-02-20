package org.javimmutable.collections;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.Option.*;

public class OptionTest
    extends TestCase
{
    public void testNone()
    {
        final Option<Integer> o = none();
        assertSame(o, none());
        assertSame(o, Option.option(null));
        assertEquals(o, o);
        assertEquals("None", o.toString());
        assertSame(o, o.map(x -> dontCallMe(1, "map")));
        assertSame(o, o.mapThrows(x -> dontCallMe(1, "mapThrows")));
        assertSame(o, o.bind(x -> dontCallMe(some(1), "bind")));
        assertSame(o, o.bindThrows(x -> dontCallMe(some(1), "bindThrows")));
        assertSame(o, o.apply(x -> dontCallMe("apply")));
        assertSame(o, o.applyThrows(x -> dontCallMe("applyThrows")));
        assertThatThrownBy(() -> o.unsafeGet()).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(() -> o.unsafeGet(() -> new IOException())).isInstanceOf(IOException.class);
        assertEquals(Integer.valueOf(-1), o.get(-1));
        assertEquals(Integer.valueOf(-5), o.getOr(() -> -5));
        assertEquals("a", o.match("a", x -> "b"));
        assertEquals("a", o.matchOr(() -> "a", x -> "b"));
        assertEquals("a", o.matchThrows("a", x -> "b"));
        assertEquals("a", o.matchOrThrows(() -> "a", x -> "b"));
        assertEquals(true, o.isNone());
        assertEquals(false, o.isSome());
        assertSame(Holders.of(), o.toHolder());
        assertSame(o, o.toHolder().toOption());
        assertSame(o, first(Collections.emptyList()));
        assertSame(o, first(Collections.emptyList(), x -> true));
    }

    public void testSome()
        throws IOException
    {
        final Option<Integer> o = some(1);
        assertEquals(o, o);
        assertThat(some(1)).isEqualTo(o);
        assertThat(Option.option(3)).isNotEqualTo(o);
        assertEquals("Some(1)", o.toString());
        assertEquals(some(12), o.map(x -> 12));
        assertEquals(some(12), o.mapThrows(x -> 12));
        assertEquals(some(15), o.bind(x -> some(15)));
        assertEquals(some(15), o.bindThrows(x -> some(15)));
        Temp.Int1 called = Temp.intVar(0);
        assertSame(o, o.apply(x -> called.a += 1));
        assertSame(o, o.applyThrows(x -> called.a += 1));
        assertEquals(2, called.a);
        assertEquals(Integer.valueOf(1), o.unsafeGet());
        assertEquals(Integer.valueOf(1), o.unsafeGet(() -> new IOException()));
        assertEquals(Integer.valueOf(1), o.get(-1));
        assertEquals(Integer.valueOf(1), o.getOr(() -> -5));
        assertEquals("b", o.match("a", x -> "b"));
        assertEquals("b", o.matchOr(() -> "a", x -> "b"));
        assertEquals("b", o.matchThrows("a", x -> "b"));
        assertEquals("b", o.matchOrThrows(() -> "a", x -> "b"));
        assertEquals(false, o.isNone());
        assertEquals(true, o.isSome());
        assertEquals(Holders.of(1), o.toHolder());
        assertEquals(o, o.toHolder().toOption());
        assertEquals(option("y"), first(Arrays.asList("x", "y", "z"), x -> x.equals("y")));
    }

    private static void dontCallMe(String message)
    {
        fail(message + " called its lambda");
    }

    private static <T> T dontCallMe(T value,
                                    String message)
    {
        fail(message + " called its lambda");
        return value;
    }
}
