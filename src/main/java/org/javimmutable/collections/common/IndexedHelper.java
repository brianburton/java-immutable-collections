package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

/**
 * Provides a number of static utility methods for producing Indexed objects
 * from raw values.  Useful when you need an Indexed but don't have or want
 * to create an array just to get an Indexed of them.
 */
public class IndexedHelper
{
    private IndexedHelper()
    {
    }

    /**
     * Returns an Indexed containing a single value.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    public static <T, V extends T> Indexed<T> indexed(V a)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                switch (index) {
                case 0:
                    return a;
                default:
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            @Override
            public int size()
            {
                return 1;
            }
        };
    }

    /**
     * Returns an Indexed containing two values.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    public static <T, V extends T> Indexed<T> indexed(V a,
                                                      V b)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                switch (index) {
                case 0:
                    return a;
                case 1:
                    return b;
                default:
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            @Override
            public int size()
            {
                return 2;
            }
        };
    }

    /**
     * Returns an Indexed containing three values.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    public static <T, V extends T> Indexed<T> indexed(V a,
                                                      V b,
                                                      V c)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                switch (index) {
                case 0:
                    return a;
                case 1:
                    return b;
                case 2:
                    return c;
                default:
                    throw new ArrayIndexOutOfBoundsException();
                }
            }

            @Override
            public int size()
            {
                return 3;
            }
        };
    }
}
