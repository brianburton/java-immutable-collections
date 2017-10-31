package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.IndexedHelper;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Iterator;

import static com.google.common.primitives.Ints.asList;
import static org.assertj.core.api.Assertions.*;
import static org.javimmutable.collections.cursors.StandardCursorTest.listIteratorTest;

@SuppressWarnings("unchecked")
public class LazyMultiIteratorTest
    extends TestCase
{
    public void test()
    {
        listIteratorTest(asList(), iterator(values()));
        listIteratorTest(asList(), iterator(values(), values()));
        listIteratorTest(asList(), iterator(values(), values(), values()));

        listIteratorTest(asList(1), iterator(values(1)));
        listIteratorTest(asList(1), iterator(values(), values(1)));
        listIteratorTest(asList(1), iterator(values(1), values()));

        listIteratorTest(asList(1, 2), iterator(values(1, 2)));
        listIteratorTest(asList(1, 2), iterator(values(1), values(2)));
        listIteratorTest(asList(1, 2), iterator(values(), values(1), values(), values(2), values()));

        listIteratorTest(asList(1, 2, 3, 4), iterator(values(1, 2, 3, 4)));
        listIteratorTest(asList(1, 2, 3, 4), iterator(values(1, 2), values(3, 4)));
        listIteratorTest(asList(1, 2, 3, 4), iterator(values(), values(1), values(2, 3), values(), values(4)));

        Iterator<Integer> multi = LazyMultiIterator.transformed(IndexedHelper.range(0, 3), value -> () -> IndexedIterator.forRange(1, value));
        listIteratorTest(asList(1, 1, 2, 1, 2, 3), multi);

        multi = LazyMultiIterator.transformed(IndexedHelper.indexed(2, 3, 4), value -> () -> IndexedIterator.forRange(1, value));
        listIteratorTest(asList(1, 2, 1, 2, 3, 1, 2, 3, 4), multi);

        multi = LazyMultiIterator.transformed(IndexedIterator.forRange(2, 4), value -> () -> IndexedIterator.forRange(1, value));
        listIteratorTest(asList(1, 2, 1, 2, 3, 1, 2, 3, 4), multi);
    }

    public void testSplitAllowed()
    {
        assertEquals(false, valueIterator(0).isSplitAllowed());
        assertEquals(false, valueIterator(1).isSplitAllowed());
        assertEquals(true, valueIterator(2).isSplitAllowed());
        assertEquals(true, valueIterator(3).isSplitAllowed());
        assertEquals(true, valueIterator(4).isSplitAllowed());
    }

    public void testSplit()
    {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> valueIterator(0).splitIterator());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> valueIterator(1).splitIterator());
        StandardCursorTest.verifySplit(valueIterator(2), asList(1), asList(2));
        StandardCursorTest.verifySplit(valueIterator(3), asList(1), asList(2, 3));
        StandardCursorTest.verifySplit(valueIterator(4), asList(1, 2), asList(3, 4));
        SplitableIterator<Integer> splitMe = valueIterator(4);
        splitMe.next();
        StandardCursorTest.verifySplit(splitMe, asList(2), asList(3, 4));
    }

    private SplitableIterator<Integer> valueIterator(int length)
    {
        final SplitableIterable<Integer>[] array = new SplitableIterable[length];
        for (int i = 1; i <= length; ++i) {
            array[i - 1] = values(i);
        }
        return iterator(array);
    }

    private SplitableIterator<Integer> iterator(SplitableIterable<Integer>... array)
    {
        return LazyMultiIterator.iterator(IndexedArray.retained(array));
    }

    private SplitableIterable<Integer> values(Integer... array)
    {
        return () -> IndexedIterator.iterator(IndexedArray.retained(array));
    }
}
