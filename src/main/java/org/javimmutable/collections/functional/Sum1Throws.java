package org.javimmutable.collections.functional;

public interface Sum1Throws<A, R, E extends Exception>
{
    R process(R sum,
              A a)
        throws E;
}
