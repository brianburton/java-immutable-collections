package org.javimmutable.collections;

/**
 * Immutable container for 4 values.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public class Tuple4<A, B, C, D>
{
    private final A first;
    private final B second;
    private final C third;
    private final D fourth;

    public Tuple4(A first,
                  B second,
                  C third,
                  D fourth)
    {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    public A getFirst()
    {
        return first;
    }

    public B getSecond()
    {
        return second;
    }

    public C getThird()
    {
        return third;
    }

    public D getFourth()
    {
        return fourth;
    }
}
