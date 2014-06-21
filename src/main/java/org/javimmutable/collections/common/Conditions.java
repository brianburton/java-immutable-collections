package org.javimmutable.collections.common;

/**
 * Utility class containing static methods for implementing pre and post conditions.
 */
public final class Conditions
{
    private Conditions()
    {
        // prevent class being instantiated
    }

    public static void stopNull(Object a)
    {
        if (a == null) {
            throw new NullPointerException();
        }
    }

    public static void stopNull(Object a,
                                Object b)
    {
        if (a == null || b == null) {
            throw new NullPointerException();
        }
    }

    public static void stopNull(Object a,
                                Object b,
                                Object c)
    {
        if (a == null || b == null || c == null) {
            throw new NullPointerException();
        }
    }

    public static void stopNull(Object a,
                                Object b,
                                Object c,
                                Object d)
    {
        if (a == null || b == null || c == null || d == null) {
            throw new NullPointerException();
        }
    }
}
