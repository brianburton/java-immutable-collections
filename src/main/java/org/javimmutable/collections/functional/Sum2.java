package org.javimmutable.collections.functional;

public interface Sum2<A, B, R>
{
    R process(R sum,
              A a,
              B b);
}
