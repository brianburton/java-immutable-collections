package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.SplitIterator;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.IndexedHelper;
import org.javimmutable.collections.common.IndexedList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;

public class IndexedIteratorTest
    extends TestCase
{
    public void test()
    {
        verifyIterable(asList(), () -> IndexedIterator.iterator(IndexedHelper.empty()));
        verifyIterable(asList(1), () -> IndexedIterator.iterator(IndexedHelper.indexed(1)));
        verifyIterable(asList(1, 2), () -> IndexedIterator.iterator(IndexedHelper.indexed(1, 2)));
        verifySplit(false, asList(), asList(), IndexedIterator.iterator(IndexedHelper.empty()));
        verifySplit(false, asList(), asList(), IndexedIterator.iterator(IndexedHelper.indexed(1)));
        verifySplit(true, asList(1), asList(2), IndexedIterator.iterator(IndexedHelper.indexed(1, 2)));
        verifySplit(true, asList(1), asList(2, 3), IndexedIterator.iterator(IndexedHelper.indexed(1, 2, 3)));
        verifySplit(true, asList(1, 2), asList(3, 4, 5), IndexedIterator.iterator(IndexedList.retained(asList(1, 2, 3, 4, 5))));
    }

    public static <T> void verifyIterable(List<T> expected,
                                          SplitableIterable<T> source)
    {
        verifyIterator(expected, source.iterator());
        verifyNextOnlyIteration(expected, source.iterator());
    }

    public static <T> void verifyIterator(List<T> expected,
                                          SplitableIterator<T> iterator)
    {
        // normal iteration
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(true, iterator.hasNext());
            assertEquals(expected.get(i), iterator.next());
        }
        assertEquals(false, iterator.hasNext());
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifyNextOnlyIteration(List<T> expected,
                                                   SplitableIterator<T> iterator)
    {
        // next only iteration
        for (int i = 0; i < expected.size(); ++i) {
            assertEquals(expected.get(i), iterator.next());
        }
        try {
            iterator.next();
            fail();
        } catch (NoSuchElementException ignored) {
        }
    }

    public static <T> void verifySplit(boolean allowed,
                                       List<T> leftExpected,
                                       List<T> rightExpected,
                                       SplitableIterator<T> source)
    {
        assertEquals(allowed, source.isSplitAllowed());
        if (allowed) {
            SplitIterator<T> split = source.splitIterator();
            verifyIterator(leftExpected, split.getLeft());
            verifyIterator(rightExpected, split.getRight());
        }
    }

    @Nonnull
    public static <T> List<T> collect(Iterator<T> iterator)
    {
        final List<T> answer = new ArrayList<>();
        while (iterator.hasNext()) {
            answer.add(iterator.next());
        }
        return answer;
    }
}
