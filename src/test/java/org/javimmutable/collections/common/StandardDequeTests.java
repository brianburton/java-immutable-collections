package org.javimmutable.collections.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IDeque;

public class StandardDequeTests {
    public static void standardTests(IDeque<Integer> empty)
    {
        verifyInsertAllFirst(empty);
        verifyInsertAllLast(empty);
        verifyTransform(empty);
        verifyAssign(empty);
        verifySingle(empty);
    }

    public static void verifyInsertAllFirst(IDeque<Integer> empty)
    {
        IDeque<Integer> actual = appendAll(empty, 1, 10);
        IDeque<Integer> expected = actual;
        while (actual.size() < 250_000) {
            final int addSize = actual.size() / 5;
            final int first = actual.size() + 1;
            final int last = actual.size() + addSize;
            actual = actual.insertAllFirst(appendAll(empty, first, last));
            expected = prependAll(expected, first, last);
            assertEquals(expected, actual);
        }
    }

    public static void verifyInsertAllLast(IDeque<Integer> empty)
    {
        IDeque<Integer> actual = appendAll(empty, 1, 10);
        IDeque<Integer> expected = actual;
        while (actual.size() < 250_000) {
            final int addSize = actual.size() / 5;
            final int first = actual.size() + 1;
            final int last = actual.size() + addSize;
            actual = actual.insertAllLast(appendAll(empty, first, last));
            expected = appendAll(expected, first, last);
            assertEquals(expected, actual);
        }
    }

    public static void verifyAssign(IDeque<Integer> empty)
    {
        for (int size = 0; size < 4096; ++size) {
            IDeque<Integer> expected = appendAll(empty, 1, size);
            IDeque<Integer> actual = appendAll(empty, 101, 100 + size);
            for (int i = 0; i < size; i++) {
                actual = actual.assign(i, 1 + i);
            }
            assertEquals(expected, actual);
        }
    }

    public static void verifyTransform(IDeque<Integer> empty)
    {
        IDeque<Integer> orig = appendAll(empty, 1, 20);
        IDeque<Integer> transformed = orig.transform(i -> i + 10);
        assertSame(orig.getClass(), transformed.getClass());
        assertEquals(appendAll(empty, 11, 30), transformed);

        transformed = orig.transformSome(i -> i < 11 ? Holders.nullable(i) : Holder.none());
        assertSame(orig.getClass(), transformed.getClass());
        assertEquals(appendAll(empty, 1, 10), transformed);
    }

    private static void verifySingle(IDeque<Integer> empty)
    {
        assertEquals(Holder.none(), empty.single());
        assertEquals(Holder.some(1), empty.insert(1).single());
        assertEquals(Holder.some(null), empty.insert(null).single());
        assertEquals(Holder.none(), empty.insert(1).insert(2).single());
    }

    private static IDeque<Integer> appendAll(IDeque<Integer> answer,
                                            int first,
                                            int last)
    {
        for (int i = first; i <= last; ++i) {
            answer = answer.insert(i);
        }
        return answer;
    }

    private static IDeque<Integer> prependAll(IDeque<Integer> answer,
                                             int first,
                                             int last)
    {
        for (int i = last; i >= first; --i) {
            answer = answer.insertFirst(i);
        }
        return answer;
    }
}
