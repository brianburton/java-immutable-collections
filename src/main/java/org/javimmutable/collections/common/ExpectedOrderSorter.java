package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Class intended for use in unit tests to simplify sorting collections based on
 * a standard ordering.  The standard ordering comes from the order that values
 * appear in an Iterator.  During sorting process any values encountered in collection
 * that did not appear in the Iterator trigger an IllegalArgumentException.
 */
@NotThreadSafe
public class ExpectedOrderSorter<T>
{
    private final Map<T, Integer> ordering;

    public ExpectedOrderSorter(Iterator<T> iterator)
    {
        Map<T, Integer> ordering = new HashMap<>();
        while (iterator.hasNext()) {
            ordering.put(iterator.next(), ordering.size());
        }
        this.ordering = Collections.unmodifiableMap(ordering);
    }

    /**
     * Creates a new List containing all of the elements of collection sorted based on the Iterator.
     */
    public <U, C extends Collection<U>> List<U> sort(@Nonnull C collection,
                                                     @Nonnull Function<U, T> mapper)
    {
        List<U> sorted = new ArrayList<>();
        sorted.addAll(collection);
        sorted.sort(comparator(mapper));
        return sorted;
    }

    private <U> Comparator<U> comparator(@Nonnull Function<U, T> mapper)
    {
        return new Comparator<U>()
        {
            @Override
            public int compare(U a,
                               U b)
            {
                final Integer aOrder = orderOf(mapper.apply(a));
                final Integer bOrder = orderOf(mapper.apply(b));
                return aOrder - bOrder;
            }

            private int orderOf(T key)
            {
                final Integer answer = ordering.get(key);
                if (answer == null) {
                    throw new IllegalArgumentException();
                }
                return answer;
            }
        };
    }
}
