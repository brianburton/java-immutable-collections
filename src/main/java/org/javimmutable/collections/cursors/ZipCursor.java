package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Tuple2;

/**
 * Provides static factory method to create a cursor to combines corresponding values from
 * other cursors into Tuples.
 */
public class ZipCursor
{
    public static <C1, C2> Cursor<Tuple2<C1, C2>> of(final Cursor<C1> cursor1,
                                                     final Cursor<C2> cursor2)
    {
        return new AbstractStartCursor<Tuple2<C1, C2>>()
        {
            @Override
            public Cursor<Tuple2<C1, C2>> next()
            {
                return ZipCursor.next(cursor1.next(), cursor2.next());
            }
        };
    }

    private static <C1, C2> Cursor<Tuple2<C1, C2>> next(final Cursor<C1> cursor1,
                                                        final Cursor<C2> cursor2)
    {
        if (!(cursor1.hasValue() && cursor2.hasValue())) {
            return EmptyStartedCursor.of();
        }

        return new AbstractStartedCursor<Tuple2<C1, C2>>()
        {
            @Override
            public Cursor<Tuple2<C1, C2>> next()
            {
                return ZipCursor.next(cursor1.next(), cursor2.next());
            }

            @Override
            public Tuple2<C1, C2> getValue()
            {
                return new Tuple2<C1, C2>(cursor1.getValue(), cursor2.getValue());
            }
        };
    }
}
