package org.javimmutable.collections.functional;

public interface Each2Throws<A, B, E extends Exception>
{
    void accept(A a,
                B b)
        throws E;
}
