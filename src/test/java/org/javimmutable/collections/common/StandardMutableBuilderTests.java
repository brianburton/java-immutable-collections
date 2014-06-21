package org.javimmutable.collections.common;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.cursors.StandardCursor;

import java.util.List;

import static junit.framework.Assert.assertEquals;

public final class StandardMutableBuilderTests
{
    private StandardMutableBuilderTests()
    {
    }

    /**
     * Tests all of the standard MutableBuilder add methods using the specified build and comparison functions.
     *
     * @param values
     * @param builderFactory
     * @param comparator
     * @param <T>
     * @param <C>
     */
    public static <T, C> void verifyBuilder(List<T> values,
                                            Func0<MutableBuilder<T, C>> builderFactory,
                                            Func2<List<T>, C, Boolean> comparator)
    {
        C collection;

        Indexed<T> indexed = IndexedList.retained(values);

        // add via Cursor
        collection = builderFactory.apply().add(StandardCursor.of(indexed)).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Iterator
        collection = builderFactory.apply().add(values.iterator()).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Collection
        builderFactory.apply().add(values).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via array
        //noinspection unchecked
        T[] array = (T[])values.toArray();
        //noinspection unchecked
        collection = builderFactory.apply().add(array).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via Indexed in its entirety
        builderFactory.apply().add(indexed).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));

        // add via indexed range
        builderFactory.apply().add(indexed, 0, indexed.size()).build();
        assertEquals(Boolean.TRUE, comparator.apply(values, collection));
    }
}
