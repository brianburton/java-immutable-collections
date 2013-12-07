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
public class ReflectionFunctions
{
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

    private static class ReflectionBase
    {
        protected final Method method;
        protected final Object obj;

        private ReflectionBase(Method method,
                               Object obj)
        {
            this.method = method;
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
