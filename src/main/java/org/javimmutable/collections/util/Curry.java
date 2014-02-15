package org.javimmutable.collections.util;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;

/**
 * Contains static factory methods to produce Curried versions of functions.
 */
public class Curry
{
    /**
     * Produces a Curried Func1 that calls the provided Func4 passing it the fixed parameters
     * param1-param3 along with the actual parameter to the apply() method as the fourth parameter.
     *
     * @param function
     * @param param1
     * @param param2
     * @param param3
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <P4>
     * @param <R>
     * @return
     */
    public static <P1, P2, P3, P4, R> Func1<P4, R> of(final Func4<P1, P2, P3, P4, R> function,
                                                      final P1 param1,
                                                      final P2 param2,
                                                      final P3 param3)
    {
        return new Func1<P4, R>()
        {
            @Override
            public R apply(P4 value)
            {
                return function.apply(param1, param2, param3, value);
            }
        };
    }

    /**
     * Produces a Curried Func1 that calls the provided Func3 passing it the fixed parameters
     * param1-param2 along with the actual parameter to the apply() method as the third parameter.
     *
     * @param function
     * @param param1
     * @param param2
     * @param <P1>
     * @param <P2>
     * @param <P3>
     * @param <R>
     * @return
     */
    public static <P1, P2, P3, R> Func1<P3, R> of(final Func3<P1, P2, P3, R> function,
                                                  final P1 param1,
                                                  final P2 param2)
    {
        return new Func1<P3, R>()
        {
            @Override
            public R apply(P3 value)
            {
                return function.apply(param1, param2, value);
            }
        };
    }

    /**
     * Produces a Curried Func1 that calls the provided Func2 passing it the fixed parameter
     * param1 along with the actual parameter to the apply() method as the second parameter.
     *
     * @param function
     * @param param1
     * @param <P1>
     * @param <P2>
     * @param <R>
     * @return
     */
    public static <P1, P2, R> Func1<P2, R> of(final Func2<P1, P2, R> function,
                                              final P1 param1)
    {
        return new Func1<P2, R>()
        {
            @Override
            public R apply(P2 value)
            {
                return function.apply(param1, value);
            }
        };
    }

    /**
     * Produces a Curried Func3 that calls the provided Func4 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     *
     * @param param1
     * @param function
     * @return
     */
    public static <P1, P2, P3, P4, R> Func3<P2, P3, P4, R> func3(final P1 param1,
                                                                 final Func4<P1, P2, P3, P4, R> function)
    {
        return new Func3<P2, P3, P4, R>()
        {
            @Override
            public R apply(P2 param2,
                           P3 param3,
                           P4 param4)
            {
                return function.apply(param1, param2, param3, param4);
            }
        };
    }

    /**
     * Produces a Curried Func2 that calls the provided Func3 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     *
     * @param param1
     * @param function
     * @return
     */
    public static <P1, P2, P3, R> Func2<P2, P3, R> func2(final P1 param1,
                                                         final Func3<P1, P2, P3, R> function)
    {
        return new Func2<P2, P3, R>()
        {
            @Override
            public R apply(P2 param2,
                           P3 param3)
            {
                return function.apply(param1, param2, param3);
            }
        };
    }

    /**
     * Produces a Curried Func3 that calls the provided Func4 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     *
     * @param param1
     * @param function
     * @return
     */
    public static <P1, P2, P3, P4, R> Func2<P3, P4, R> func2(final P1 param1,
                                                             final P2 param2,
                                                             final Func4<P1, P2, P3, P4, R> function)
    {
        return new Func2<P3, P4, R>()
        {
            @Override
            public R apply(P3 param3,
                           P4 param4)
            {
                return function.apply(param1, param2, param3, param4);
            }
        };
    }

    /**
     * Produces a Curried Func1 that calls the provided Func2 passing it the fixed parameter
     * param1 along with the actual parameters to the apply() method.
     *
     * @param param1
     * @param function
     * @return
     */
    public static <P1, P2, R> Func1<P2, R> func1(final P1 param1,
                                                 final Func2<P1, P2, R> function)
    {
        return of(function, param1);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func3 passing it the fixed parameters
     * param1 and param2 along with the actual parameters to the apply() method.
     *
     * @param param1
     * @param function
     * @return
     */
    public static <P1, P2, P3, R> Func1<P3, R> func1(final P1 param1,
                                                     final P2 param2,
                                                     final Func3<P1, P2, P3, R> function)
    {
        return of(function, param1, param2);
    }

    /**
     * Produces a Curried Func1 that calls the provided Func4 passing it the fixed parameters
     * param1 and param2 along with the actual parameters to the apply() method.
     *
     * @param param1
     * @param function
     * @return
     */
    public static <P1, P2, P3, P4, R> Func1<P4, R> func1(final P1 param1,
                                                         final P2 param2,
                                                         final P3 param3,
                                                         final Func4<P1, P2, P3, P4, R> function)
    {
        return of(function, param1, param2, param3);
    }
}
