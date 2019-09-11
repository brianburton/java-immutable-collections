package org.javimmutable.collections.functional;

public interface Sum2Throws<A, B, R, E extends Exception>
{
    R process(R sum,
              A a,
              B b)
        throws E;
}
