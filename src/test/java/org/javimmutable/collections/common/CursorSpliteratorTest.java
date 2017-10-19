package org.javimmutable.collections.common;

import junit.framework.TestCase;
import org.javimmutable.collections.cursors.StandardCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public class CursorSpliteratorTest
    extends TestCase
{
    public void testEmpty()
    {
        Spliterator<Integer> si = spliterator();
        ValueCollector consumer = new ValueCollector();
        assertEquals(false, si.tryAdvance(consumer));
        assertEquals(0, consumer.values.size());
        assertEquals(null, si.trySplit());
    }

    public void testSingle()
    {
        Spliterator<Integer> si = spliterator(1);
        ValueCollector consumer = new ValueCollector();
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(false, si.tryAdvance(consumer));
        assertEquals(asList(1), consumer.values);
    }

    public void testMulti()
    {
        Spliterator<Integer> si = spliterator(1, 2, 3, 4);
        ValueCollector consumer = new ValueCollector();
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(true, si.tryAdvance(consumer));
        assertEquals(false, si.tryAdvance(consumer));
        assertEquals(asList(1, 2, 3, 4), consumer.values);

        si = spliterator(1, 2, 3, 4);
        Spliterator<Integer> left = si.trySplit();
        assertNotNull(left);
        assertEquals(asList(1, 2), list(left));
        assertEquals(asList(3, 4), list(si));
    }

    public void testStream()
    {
        Stream<Integer> str = stream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8), str.collect(Collectors.toList()));

        str = stream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(2, 4, 6, 8), str.filter(x -> x % 2 == 0).collect(Collectors.toList()));

        str = parstream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(1, 2, 3, 4, 5, 6, 7, 8), str.collect(Collectors.toList()));

        str = parstream(1, 2, 3, 4, 5, 6, 7, 8);
        assertEquals(asList(2, 4, 6, 8), str.filter(x -> x % 2 == 0).collect(Collectors.toList()));
    }

    private List<Integer> list(Spliterator<Integer> si)
    {
        ValueCollector consumer = new ValueCollector();
        si.forEachRemaining(consumer);
        return consumer.values;
    }

    private Spliterator<Integer> spliterator(Integer... values)
    {
        return new CursorSpliterator<Integer>(Spliterator.IMMUTABLE, StandardCursor.of(IndexedArray.retained(values)));
    }

    private Stream<Integer> stream(Integer... values)
    {
        return StreamSupport.stream(spliterator(values), false);
    }

    private Stream<Integer> parstream(Integer... values)
    {
        return StreamSupport.stream(spliterator(values), true);
    }

    private static class ValueCollector
        implements Consumer<Integer>
    {
        private final List<Integer> values = new ArrayList<>();

        @Override
        public void accept(Integer value)
        {
            values.add(value);
        }
    }
}
