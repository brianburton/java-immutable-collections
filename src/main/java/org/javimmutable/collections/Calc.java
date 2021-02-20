package org.javimmutable.collections;

import javax.annotation.Nonnull;

public abstract class Calc<T>
{
    @Nonnull
    public abstract <U> Calc<U> next(@Nonnull Func1Throws<T, U, ? super Exception> func);

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
        return new LazyStart<T>(func);
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
        public <U> Calc<U> next(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            try {
                return new EagerSuccess<>(func.apply(value));
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
        public <U> Calc<U> next(@Nonnull Func1Throws<T, U, ? super Exception> func)
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
        public <U> Calc<U> next(@Nonnull Func1Throws<T, U, ? super Exception> func)
        {
            return new LazyStep<>(func, this);
        }

        @Nonnull
        @Override
        public Calc<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc)
        {
            return next(value -> {
                proc.apply(value);
                return value;
            });
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
        public <V> Calc<V> next(@Nonnull Func1Throws<U, V, ? super Exception> func)
        {
            return new LazyStep<>(func, this);
        }

        @Nonnull
        @Override
        public Calc<U> apply(@Nonnull Proc1Throws<U, ? super Exception> proc)
        {
            return next(value -> {
                proc.apply(value);
                return value;
            });
        }

        @Override
        public U get()
            throws Exception
        {
            return func.apply(source.get());
        }
    }
}
