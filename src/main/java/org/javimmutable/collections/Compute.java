///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collections;

import javax.annotation.Nonnull;

public abstract class Compute<T> {
    @Nonnull
    public abstract <U> Compute<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func);

    @Nonnull
    public abstract <U> Compute<U> flatMap(@Nonnull Func1Throws<T, Compute<U>, ? super Exception> func);

    @Nonnull
    public abstract Compute<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc);

    public abstract T get()
            throws Exception;

    private Compute() {
    }

    @Nonnull
    public static <T> Compute<T> success(T value) {
        return new Success<>(value);
    }

    @Nonnull
    public static <T> Compute<T> failure(Exception exception) {
        return new Failure<>(exception);
    }

    @Nonnull
    public static <T> Compute<T> compute(@Nonnull Func0Throws<T, ? super Exception> func) {
        return new Later<>(func);
    }

    private static class Success<T>
            extends Compute<T> {
        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Nonnull
        @Override
        public <U> Compute<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func) {
            try {
                return success(func.apply(value));
            } catch (Exception ex) {
                return failure(ex);
            }
        }

        @Nonnull
        @Override
        public <U> Compute<U> flatMap(@Nonnull Func1Throws<T, Compute<U>, ? super Exception> func) {
            try {
                return func.apply(value);
            } catch (Exception ex) {
                return failure(ex);
            }
        }

        @Nonnull
        @Override
        public Compute<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc) {
            try {
                proc.apply(value);
                return this;
            } catch (Exception ex) {
                return failure(ex);
            }
        }

        @Override
        public T get()
                throws Exception {
            return value;
        }
    }

    private static class Failure<T>
            extends Compute<T> {
        private final Exception error;

        private Failure(Exception error) {
            this.error = error;
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <U> Compute<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func) {
            return (Compute<U>) this;
        }

        @SuppressWarnings("unchecked")
        @Nonnull
        @Override
        public <U> Compute<U> flatMap(@Nonnull Func1Throws<T, Compute<U>, ? super Exception> func) {
            return (Compute<U>) this;
        }

        @Nonnull
        @Override
        public Compute<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc) {
            return this;
        }

        @Override
        public T get()
                throws Exception {
            throw error;
        }
    }

    private static class Later<T>
            extends Compute<T> {
        private final Func0Throws<T, ? extends Exception> func;

        private Later(Func0Throws<T, ? extends Exception> func) {
            this.func = func;
        }

        @Nonnull
        @Override
        public <U> Compute<U> map(@Nonnull Func1Throws<T, U, ? super Exception> func) {
            return new Later<>(() -> func.apply(this.func.apply()));
        }

        @Nonnull
        @Override
        public <U> Compute<U> flatMap(@Nonnull Func1Throws<T, Compute<U>, ? super Exception> func) {
            return new Later<>(() -> func.apply(this.func.apply()).get());
        }

        @Nonnull
        @Override
        public Compute<T> apply(@Nonnull Proc1Throws<T, ? super Exception> proc) {
            return map(x -> {
                proc.apply(x);
                return x;
            });
        }

        @Override
        public T get()
                throws Exception {
            return func.apply();
        }
    }
}
