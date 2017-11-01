package org.javimmutable.collections.iterators;

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.indexed.IndexedHelper;

import javax.annotation.Nonnull;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

public class TransformIteratorTest
    extends TestCase
{
    public void test()
    {
        verifyOrderedIterable(asList(), () -> iterator(IndexedHelper.empty()));
        verifyOrderedIterable(asList(1), () -> iterator(IndexedHelper.indexed(0)));
        verifyOrderedIterable(asList(1, 2), () -> iterator(IndexedHelper.indexed(0, 1)));
        verifyOrderedSplit(false, asList(), asList(), iterator(IndexedHelper.empty()));
        verifyOrderedSplit(false, asList(), asList(), iterator(IndexedHelper.indexed(0)));
        verifyOrderedSplit(true, asList(1), asList(2), iterator(IndexedHelper.indexed(0, 1)));
        verifyOrderedSplit(true, asList(1), asList(2, 3), iterator(IndexedHelper.indexed(0, 1, 2)));
        verifyOrderedSplit(true, asList(1, 2), asList(3, 4, 5), iterator(IndexedHelper.range(0, 4)));
    }

    private TransformIterator<Integer, Integer> iterator(@Nonnull Indexed<Integer> source)
    {
        return TransformIterator.of(IndexedIterator.iterator(source), x -> x + 1);
    }
}
