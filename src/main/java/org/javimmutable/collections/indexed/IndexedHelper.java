///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.indexed;

import static org.javimmutable.collections.Holders.holder;
import static org.javimmutable.collections.Maybe.maybe;
import static org.javimmutable.collections.Maybe.none;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Maybe;

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

    @Nonnull
    public static <T> Indexed<T> empty()
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                throw new ArrayIndexOutOfBoundsException();
            }

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                return holder();
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                return none();
            }

            @Override
            public int size()
            {
                return 0;
            }
        };
    }

    /**
     * Returns an Indexed containing a single value.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    @Nonnull
    public static <T, V extends T> Indexed<T> indexed(V a)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                if (index == 0) {
                    return a;
                }
                throw new ArrayIndexOutOfBoundsException();
            }

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                if (index == 0) {
                    return holder(a);
                }
                return holder();
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                if (index == 0) {
                    return maybe(a);
                }
                return none();
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
    @Nonnull
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

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                switch (index) {
                    case 0:
                        return holder(a);
                    case 1:
                        return holder(b);
                    default:
                        return holder();
                }
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                switch (index) {
                    case 0:
                        return maybe(a);
                    case 1:
                        return maybe(b);
                    default:
                        return none();
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
    @Nonnull
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

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                switch (index) {
                    case 0:
                        return holder(a);
                    case 1:
                        return holder(b);
                    case 2:
                        return holder(c);
                    default:
                        return holder();
                }
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                switch (index) {
                    case 0:
                        return maybe(a);
                    case 1:
                        return maybe(b);
                    case 2:
                        return maybe(c);
                    default:
                        return none();
                }
            }

            @Override
            public int size()
            {
                return 3;
            }
        };
    }

    /**
     * Returns an Indexed containing three values.
     * Note that the type of the Indexed may be a subclass of the type of the value.
     */
    @Nonnull
    public static <T, V extends T> Indexed<T> indexed(V a,
                                                      V b,
                                                      V c,
                                                      V... others)
    {
        final int length = 3 + others.length;
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
                        return others[index - 3];
                }
            }

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                switch (index) {
                    case 0:
                        return holder(a);
                    case 1:
                        return holder(b);
                    case 2:
                        return holder(c);
                    default:
                        index -= 3;
                        if (index >= 0 && index < others.length) {
                            return holder(others[index]);
                        }
                        return holder();
                }
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                switch (index) {
                    case 0:
                        return maybe(a);
                    case 1:
                        return maybe(b);
                    case 2:
                        return maybe(c);
                    default:
                        index -= 3;
                        if (index >= 0 && index < others.length) {
                            return maybe(others[index]);
                        }
                        return none();
                }
            }

            @Override
            public int size()
            {
                return length;
            }
        };
    }

    @Nonnull
    public static <T> Indexed<T> repeating(T value,
                                           int count)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                if (index < 0 || index >= count) {
                    throw new ArrayIndexOutOfBoundsException();
                } else {
                    return value;
                }
            }

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                if (index < 0 || index >= count) {
                    return holder();
                } else {
                    return holder(value);
                }
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                if (index < 0 || index >= count) {
                    return none();
                } else {
                    return maybe(value);
                }
            }

            @Override
            public int size()
            {
                return count;
            }
        };
    }

    @Nonnull
    public static Indexed<Integer> range(int low,
                                         int high)
    {
        final int size = high - low + 1;
        return new Indexed<Integer>()
        {
            @Override
            public Integer get(int index)
            {
                if (index < 0 || index >= size) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                return low + index;
            }

            @Nonnull
            @Override
            public Holder<Integer> find(int index)
            {
                if (index < 0 || index >= size) {
                    return holder();
                }
                return holder(low + index);
            }

            @Nonnull
            @Override
            public Maybe<Integer> seek(int index)
            {
                if (index < 0 || index >= size) {
                    return none();
                }
                return maybe(low + index);
            }

            @Override
            public int size()
            {
                return size;
            }
        };
    }

    /**
     * Creates a mutable List containing all values from the Indexed.
     */
    @Nonnull
    public static <T> List<T> asList(@Nonnull Indexed<T> indexed)
    {
        final List<T> answer = new ArrayList<>(indexed.size());
        for (int i = 0; i < indexed.size(); ++i) {
            answer.add(indexed.get(i));
        }
        return answer;
    }

    @Nonnull
    public static <O, T> Indexed<T> transformed(@Nonnull Indexed<O> indexed,
                                                @Nonnull Function<O, T> transforminator)
    {
        return new Indexed<T>()
        {
            @Override
            public T get(int index)
            {
                return transforminator.apply(indexed.get(index));
            }

            @Nonnull
            @Override
            public Holder<T> find(int index)
            {
                return indexed.find(index).map(transforminator);
            }

            @Nonnull
            @Override
            public Maybe<T> seek(int index)
            {
                return indexed.seek(index).map(transforminator::apply);
            }

            @Override
            public int size()
            {
                return indexed.size();
            }
        };
    }
}
