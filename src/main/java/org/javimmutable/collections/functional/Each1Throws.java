package org.javimmutable.collections.functional;

public interface Each1Throws<A, E extends Exception>
{
    void accept(A a)
        throws E;
}
