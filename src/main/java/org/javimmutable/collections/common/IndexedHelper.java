package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

public class IndexedHelper
{
    private IndexedHelper()
    {
    }

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
