package org.javimmutable.collections.common;

import org.assertj.core.api.ThrowableAssert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;

public class TestUtil
{
    /**
     * Utility method, useful in unit tests, that collects all of the values in the Iterator into a List
     * and returns the List.
     */
    public static <T> List<T> makeList(Iterator<T> iterator)
    {
        List<T> answer = new ArrayList<>();
        while (iterator.hasNext()) {
            answer.add(iterator.next());
        }
        return answer;
    }

    public static <T> List<T> makeList(@Nonnull Iterable<T> src)
    {
        final List<T> dst = new ArrayList<>();
        for (T value : src) {
            dst.add(value);
        }
        return dst;
    }

    public static Set<String> makeSet(String... args)
    {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, args);
        return set;
    }

    public static Set<Integer> makeSet(int... args)
    {
        Set<Integer> set = new HashSet<>();
        for (int i : args) {
            set.add(i);
        }
        return set;
    }

    public static <T> void verifyContents(Iterable<T> a,
                                          Iterable<T> b)
    {
        List<T> al = makeList(a.iterator());
        List<T> bl = makeList(b.iterator());
        assertEquals(al, bl);
    }

    public static void verifyOutOfBounds(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(IndexOutOfBoundsException.class);
    }

    public static void verifyUnsupported(ThrowableAssert.ThrowingCallable proc)
    {
        assertThatThrownBy(proc).isInstanceOf(UnsupportedOperationException.class);
    }
}
