///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.util;

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Func3;
import org.javimmutable.collections.Func4;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Contains static constructors for creating FuncN objects that call methods using reflection.
 */
@SuppressWarnings("unchecked")
public final class ReflectionFunctions
{
    private ReflectionFunctions()
    {
    }

    /**
     * Encapsulates the various checked exceptions that can be thrown by the java reflection methods.
     */
    public static class ReflectionException
            extends RuntimeException
    {
        public ReflectionException(Throwable throwable)
        {
            super(throwable);
        }
    }

    /**
     * Exception thrown if caller requests a static class but reflection finds a non-static class (or vice versa).
     */
    public static class StaticMismatchException
            extends RuntimeException
    {
        public StaticMismatchException()
        {
            super("static modifier on method does not match expected value");
        }
    }

    /**
     * Returns a Func0 instance that calls the specified non-static method on the specified java object.
     *
     * @param obj  the object whose method should be invoked
     * @param name the name of the method to invoke
     * @param <R>  the return type of the function
     * @return
     */
    public static <R> Func0<R> method(final Object obj,
                                      String name)
    {
        return new ReflectionFunc0<R>(findMethod(obj.getClass(), name, false), obj);
    }

    /**
     * Returns a Func1 instance that calls the specified non-static method on the specified java object.
     *
     * @param obj       the object whose method should be invoked
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param <P1>      parameter 1 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, R> Func1<P1, R> method(final Object obj,
                                              String name,
                                              Class<P1> arg1Class)
    {
        return new ReflectionFunc1<P1, R>(findMethod(obj.getClass(), name, false, arg1Class), obj);
    }

    /**
     * Returns a Func2 instance that calls the specified non-static method on the specified java object.
     *
     * @param obj       the object whose method should be invoked
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param <P1>      parameter 1 type
     * @param <P2>      parameter 2 type
     * @param <R>
     * @return
     */
    public static <P1, P2, R> Func2<P1, P2, R> method(final Object obj,
                                                      String name,
                                                      Class<P1> arg1Class,
                                                      Class<P2> arg2Class)
    {
        return new ReflectionFunc2<P1, P2, R>(findMethod(obj.getClass(), name, false, arg1Class, arg2Class), obj);
    }

    /**
     * Returns a Func3 instance that calls the specified non-static method on the specified java object.
     *
     * @param obj       the object whose method should be invoked
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param arg3Class Class of parameter 3 of the method
     * @param <P1>      parameter 1 type
     * @param <P2>      parameter 2 type
     * @param <P3>      parameter 3 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, P2, P3, R> Func3<P1, P2, P3, R> method(final Object obj,
                                                              String name,
                                                              Class<P1> arg1Class,
                                                              Class<P2> arg2Class,
                                                              Class<P3> arg3Class)
    {
        return new ReflectionFunc3<P1, P2, P3, R>(findMethod(obj.getClass(), name, false, arg1Class, arg2Class, arg3Class), obj);
    }

    /**
     * Returns a Func4 instance that calls the specified non-static method on the specified java object.
     *
     * @param obj       the object whose method should be invoked
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param arg3Class Class of parameter 3 of the method
     * @param arg4Class Class of parameter 4 of the method
     * @param <P1>      parameter 1 type
     * @param <P2>      parameter 2 type
     * @param <P3>      parameter 3 type
     * @param <P4>      parameter 4 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, P2, P3, P4, R> Func4<P1, P2, P3, P4, R> method(final Object obj,
                                                                      String name,
                                                                      Class<P1> arg1Class,
                                                                      Class<P2> arg2Class,
                                                                      Class<P3> arg3Class,
                                                                      Class<P4> arg4Class)
    {
        return new ReflectionFunc4<P1, P2, P3, P4, R>(findMethod(obj.getClass(), name, false, arg1Class, arg2Class, arg3Class, arg4Class), obj);
    }

    /**
     * Returns a Func1 instance that calls the specified non-static method on a java object
     * passed as the parameter of the function.  These functions are useful for invoking
     * the same method on all objects returned by a Cursor.
     *
     * @param name  name of the method to invoke
     * @param klass class of the instance object parameter
     * @return
     */
    public static <OT, R> Func1<OT, R> method(String name,
                                              Class<OT> klass)
    {
        return new ParamReflectionFunc1<OT, R>(findMethod(klass, name, false));
    }

    /**
     * Returns a Func2 instance that calls the specified non-static method on a java object
     * passed as the last parameter of the Func2.  These functions are useful for invoking
     * the same method with the same arguments on all objects returned by a Cursor.  The instance
     * object is the last parameter to facilitate the use of Curry.of().
     *
     * @param name      name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param klass     class of the instance object parameter
     * @return
     */
    public static <P1, OT, R> Func2<P1, OT, R> method(String name,
                                                      Class<P1> arg1Class,
                                                      Class<OT> klass)
    {
        return new ParamReflectionFunc2<P1, OT, R>(findMethod(klass, name, false, arg1Class));
    }

    /**
     * Returns a Func3 instance that calls the specified non-static method on a java object
     * passed as the last parameter of the Func3.  These functions are useful for invoking
     * the same method with the same arguments on all objects returned by a Cursor.  The instance
     * object is the last parameter to facilitate the use of Curry.of().
     *
     * @param name      name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param klass     class of the instance object parameter
     * @return
     */
    public static <P1, P2, OT, R> Func3<P1, P2, OT, R> method(String name,
                                                              Class<P1> arg1Class,
                                                              Class<P2> arg2Class,
                                                              Class<OT> klass)
    {
        return new ParamReflectionFunc3<P1, P2, OT, R>(findMethod(klass, name, false, arg1Class, arg2Class));
    }

    /**
     * Returns a Func4 instance that calls the specified non-static method on a java object
     * passed as the last parameter of the Func4.  These functions are useful for invoking
     * the same method with the same arguments on all objects returned by a Cursor.  The instance
     * object is the last parameter to facilitate the use of Curry.of().
     *
     * @param name      name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param arg3Class Class of parameter 3 of the method
     * @param klass     class of the instance object parameter
     * @return
     */
    public static <P1, P2, P3, OT, R> Func4<P1, P2, P3, OT, R> method(String name,
                                                                      Class<P1> arg1Class,
                                                                      Class<P2> arg2Class,
                                                                      Class<P3> arg3Class,
                                                                      Class<OT> klass)
    {
        return new ParamReflectionFunc4<P1, P2, P3, OT, R>(findMethod(klass, name, false, arg1Class, arg2Class, arg3Class));
    }

    /**
     * Returns a Func0 instance that calls the specified static method on the specified java object.
     *
     * @param name the name of the method to invoke
     * @param <R>  the return type of the function
     * @return
     */
    public static <R> Func0<R> staticMethod(final Class klass,
                                            String name)
    {
        return new ReflectionFunc0<R>(findMethod(klass, name, true), null);
    }

    /**
     * Returns a Func3 instance that calls the specified static method on the specified java object.
     *
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param <P1>      parameter 1 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, R> Func1<P1, R> staticMethod(final Class klass,
                                                    String name,
                                                    Class<P1> arg1Class)
    {
        return new ReflectionFunc1<P1, R>(findMethod(klass, name, true, arg1Class), null);
    }

    /**
     * Returns a Func2 instance that calls the specified static method on the specified java object.
     *
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param <P1>      parameter 1 type
     * @param <P2>      parameter 2 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, P2, R> Func2<P1, P2, R> staticMethod(final Class klass,
                                                            String name,
                                                            Class<P1> arg1Class,
                                                            Class<P2> arg2Class)
    {
        return new ReflectionFunc2<P1, P2, R>(findMethod(klass, name, true, arg1Class, arg2Class), null);
    }

    /**
     * Returns a Func3 instance that calls the specified static method on the specified java object.
     *
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param arg3Class Class of parameter 3 of the method
     * @param <P1>      parameter 1 type
     * @param <P2>      parameter 2 type
     * @param <P3>      parameter 3 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, P2, P3, R> Func3<P1, P2, P3, R> staticMethod(final Class klass,
                                                                    String name,
                                                                    Class<P1> arg1Class,
                                                                    Class<P2> arg2Class,
                                                                    Class<P3> arg3Class)
    {
        return new ReflectionFunc3<P1, P2, P3, R>(findMethod(klass, name, true, arg1Class, arg2Class, arg3Class), null);
    }

    /**
     * Returns a Func4 instance that calls the specified static method on the specified java object.
     *
     * @param name      the name of the method to invoke
     * @param arg1Class Class of parameter 1 of the method
     * @param arg2Class Class of parameter 2 of the method
     * @param arg3Class Class of parameter 3 of the method
     * @param arg4Class Class of parameter 4 of the method
     * @param <P1>      parameter 1 type
     * @param <P2>      parameter 2 type
     * @param <P3>      parameter 3 type
     * @param <P4>      parameter 4 type
     * @param <R>       the return type of the function
     * @return
     */
    public static <P1, P2, P3, P4, R> Func4<P1, P2, P3, P4, R> staticMethod(final Class klass,
                                                                            String name,
                                                                            Class<P1> arg1Class,
                                                                            Class<P2> arg2Class,
                                                                            Class<P3> arg3Class,
                                                                            Class<P4> arg4Class)
    {
        return new ReflectionFunc4<P1, P2, P3, P4, R>(findMethod(klass, name, true, arg1Class, arg2Class, arg3Class, arg4Class), null);
    }

    private static class NoInstanceReflectionBase
    {
        protected final Method method;

        private NoInstanceReflectionBase(Method method)
        {
            this.method = method;
        }
    }

    private static class ReflectionBase
            extends NoInstanceReflectionBase
    {
        protected final Object obj;

        private ReflectionBase(Method method,
                               Object obj)
        {
            super(method);
            this.obj = obj;
        }
    }

    private static class ReflectionFunc0<R>
            extends ReflectionBase
            implements Func0<R>
    {
        private ReflectionFunc0(Method method,
                                Object obj)
        {
            super(method, obj);
        }

        @Override
        public R apply()
        {
            return (R)callMethod(method, obj);
        }
    }

    private static class ReflectionFunc1<P1, R>
            extends ReflectionBase
            implements Func1<P1, R>
    {
        private ReflectionFunc1(Method method,
                                Object obj)
        {
            super(method, obj);
        }

        @Override
        public R apply(P1 param1)
        {
            return (R)callMethod(method, obj, param1);
        }
    }

    private static class ReflectionFunc2<P1, P2, R>
            extends ReflectionBase
            implements Func2<P1, P2, R>
    {
        private ReflectionFunc2(Method method,
                                Object obj)
        {
            super(method, obj);
        }

        @Override
        public R apply(P1 param1,
                       P2 param2)
        {
            return (R)callMethod(method, obj, param1, param2);
        }
    }

    private static class ReflectionFunc3<P1, P2, P3, R>
            extends ReflectionBase
            implements Func3<P1, P2, P3, R>
    {
        private ReflectionFunc3(Method method,
                                Object obj)
        {
            super(method, obj);
        }

        @Override
        public R apply(P1 param1,
                       P2 param2,
                       P3 param3)
        {
            return (R)callMethod(method, obj, param1, param2, param3);
        }
    }

    private static class ReflectionFunc4<P1, P2, P3, P4, R>
            extends ReflectionBase
            implements Func4<P1, P2, P3, P4, R>
    {
        private ReflectionFunc4(Method method,
                                Object obj)
        {
            super(method, obj);
        }

        @Override
        public R apply(P1 param1,
                       P2 param2,
                       P3 param3,
                       P4 param4)
        {
            return (R)callMethod(method, obj, param1, param2, param3, param4);
        }
    }

    private static class ParamReflectionFunc1<OT, R>
            extends NoInstanceReflectionBase
            implements Func1<OT, R>
    {
        private ParamReflectionFunc1(Method method)
        {
            super(method);
        }

        @Override
        public R apply(OT obj)
        {
            return (R)callMethod(method, obj);
        }
    }

    private static class ParamReflectionFunc2<P1, OT, R>
            extends NoInstanceReflectionBase
            implements Func2<P1, OT, R>
    {
        private ParamReflectionFunc2(Method method)
        {
            super(method);
        }

        @Override
        public R apply(P1 param1,
                       OT obj)
        {
            return (R)callMethod(method, obj, param1);
        }
    }

    private static class ParamReflectionFunc3<P1, P2, OT, R>
            extends NoInstanceReflectionBase
            implements Func3<P1, P2, OT, R>
    {
        private ParamReflectionFunc3(Method method)
        {
            super(method);
        }

        @Override
        public R apply(P1 param1,
                       P2 param2,
                       OT obj)
        {
            return (R)callMethod(method, obj, param1, param2);
        }
    }

    private static class ParamReflectionFunc4<P1, P2, P3, OT, R>
            extends NoInstanceReflectionBase
            implements Func4<P1, P2, P3, OT, R>
    {
        private ParamReflectionFunc4(Method method)
        {
            super(method);
        }

        @Override
        public R apply(P1 param1,
                       P2 param2,
                       P3 param3,
                       OT obj)
        {
            return (R)callMethod(method, obj, param1, param2, param3);
        }
    }

    private static Object callMethod(Method method,
                                     Object obj,
                                     Object... params)
    {
        try {
            return method.invoke(obj, params);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    private static Method findMethod(Class klass,
                                     String name,
                                     boolean expectStatic,
                                     Class... args)
    {
        try {
            Method method = klass.getMethod(name, args);
            boolean isStatic = (method.getModifiers() & Modifier.STATIC) != 0;
            if (isStatic != expectStatic) {
                throw new StaticMismatchException();
            }
            return method;
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        }
    }
}
