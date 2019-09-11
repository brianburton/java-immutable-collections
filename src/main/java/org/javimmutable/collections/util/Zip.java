package org.javimmutable.collections.util;

import org.javimmutable.collections.functional.Each2;
import org.javimmutable.collections.functional.Each2Throws;
import org.javimmutable.collections.functional.Sum2;
import org.javimmutable.collections.functional.Sum2Throws;

import javax.annotation.Nonnull;
import java.util.Iterator;

public class Zip
{
    public static <A, B> void forEach(@Nonnull Iterable<A> a,
                                      @Nonnull Iterable<B> b,
                                      @Nonnull Each2<A, B> operation)
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            operation.accept(ai.next(), bi.next());
        }
    }

    public static <A, B, E extends Exception> void forEachThrows(@Nonnull Iterable<A> a,
                                                                 @Nonnull Iterable<B> b,
                                                                 @Nonnull Each2Throws<A, B, E> operation)
        throws E
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            operation.accept(ai.next(), bi.next());
        }
    }

    public static <A, B, R> R reduce(R sum,
                                     @Nonnull Iterable<A> a,
                                     @Nonnull Iterable<B> b,
                                     @Nonnull Sum2<A, B, R> operation)
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            sum = operation.process(sum, ai.next(), bi.next());
        }
        return sum;
    }

    public static <A, B, R, E extends Exception> R reduceThrows(R sum,
                                                                @Nonnull Iterable<A> a,
                                                                @Nonnull Iterable<B> b,
                                                                @Nonnull Sum2Throws<A, B, R, E> operation)
        throws E
    {
        Iterator<A> ai = a.iterator();
        Iterator<B> bi = b.iterator();
        while (ai.hasNext() && bi.hasNext()) {
            sum = operation.process(sum, ai.next(), bi.next());
        }
        return sum;
    }

}
