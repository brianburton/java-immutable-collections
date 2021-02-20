package org.javimmutable.collections;

import javax.annotation.Nonnull;

public abstract class Calc<T>
{
    @Nonnull
    public abstract <U> Calc<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func);

    @Nonnull
    public abstract <U> Calc<U> flatMap(@Nonnull Func1<T, Calc<U>> func);

    @Nonnull
    public abstract Calc<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc);

    public abstract T get()
        throws Exception;

    private Calc()
    {
    }

    @Nonnull
    public static <T> Calc<T> eager(T value)
    {
        return new EagerSuccess<>(value);
    }

    @Nonnull
    public static <T> Calc<T> lazy(@Nonnull Func0Throws<T, ? super Exception> func)
    {
        return new LazyStart<>(func);
    }

    private static class EagerSuccess<T>
        extends Calc<T>
    {
        private final T value;

        private EagerSuccess(T value)
        {
            this.value = value;
        }

        @Nonnull
        @Override
        public <U> Calc<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            try {
                return new EagerSuccess<>(func.apply(value));
            } catch (Exception ex) {
                return new EagerFailure<>(ex);
            }
        }

        @Nonnull
        @Override
        public <U> Calc<U> flatMap(@Nonnull Func1<T, Calc<U>> func)
        {
            try {
                return func.apply(value);
            } catch (Exception ex) {
                return new EagerFailure<>(ex);
            }
        }

        @Nonnull
        @Override
        public Calc<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            try {
                proc.apply(value);
                return this;
            } catch (Exception ex) {
                return new EagerFailure<>(ex);
            }
        }

        @Override
        public T get()
            throws Exception
        {
            return value;
        }
    }

    private static class EagerFailure<T>
        extends Calc<T>
    {
        private final Exception error;

        private EagerFailure(Exception error)
        {
            this.error = error;
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <U> Calc<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            return (Calc<U>)this;
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <U> Calc<U> flatMap(@Nonnull Func1<T, Calc<U>> func)
        {
            return (Calc<U>)this;
        }

        @Nonnull
        @Override
        public Calc<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            return this;
        }

        @Override
        public T get()
            throws Exception
        {
            throw error;
        }
    }

    private static class LazyStart<T>
        extends Calc<T>
    {
        private final Func0Throws<T, ? extends Exception> func;

        private LazyStart(Func0Throws<T, ? extends Exception> func)
        {
            this.func = func;
        }

        @Nonnull
        @Override
        public <U> Calc<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            return new LazyStep<>(func, this);
        }

        @Nonnull
        @Override
        public <U> Calc<U> flatMap(@Nonnull Func1<T, Calc<U>> func)
        {
            return lazy(() -> func.apply(this.func.apply()).get());
        }

        @Nonnull
        @Override
        public Calc<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            return map(mapProc(proc));
        }

        @Override
        public T get()
            throws Exception
        {
            return func.apply();
        }
    }

    private static class LazyStep<T, U>
        extends Calc<U>
    {
        private final Func1Throws<T, U, ? super Exception> func;
        private final Calc<T> source;

        private LazyStep(Func1Throws<T, U, ? super Exception> func,
                         Calc<T> source)
        {
            this.func = func;
            this.source = source;
        }

        @Nonnull
        @Override
        public <V> Calc<V> map(@Nonnull Func1Throws<U, V, ? super Exception> func)
        {
            return new LazyStep<>(func, this);
        }

        @Nonnull
        @Override
        public <U1> Calc<U1> flatMap(@Nonnull Func1<U, Calc<U1>> func)
        {
            return lazy(() -> func.apply(this.func.apply(source.get())).get());
        }

        @Nonnull
        @Override
        public Calc<U> apply(@Nonnull Proc1Throws<U, ? super Exception> proc)
        {
            return map(mapProc(proc));
        }

        @Override
        public U get()
            throws Exception
        {
            return func.apply(source.get());
        }
    }

    private static <T> Func1Throws<T, T, Exception> mapProc(@Nonnull Proc1Throws<T, ? super Exception> proc)
    {
        return x -> {
            proc.apply(x);
            return x;
        };
    }
}
