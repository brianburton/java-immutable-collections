package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static java.lang.Long.toHexString;

public abstract class InfiniteKey
    implements Comparable<InfiniteKey>
{
    static final int CACHE_SIZE = 256;

    static InfiniteKey testKey(long... values)
    {
        InfiniteKey key = values[0] == 0 ? Tiny.ZERO : new Tiny(values[0]);
        for (int i = 1; i < values.length; ++i) {
            key = new Large(key, values[i]);
        }
        return key;
    }

    @Nonnull
    public static InfiniteKey first()
    {
        return Tiny.ZERO;
    }

    @Nonnull
    public abstract InfiniteKey next();

    abstract void addToString(StringBuilder sb);

    @Override
    public String toString()
    {
        final StringBuilder answer = new StringBuilder();
        addToString(answer);
        return answer.toString();
    }

    @Immutable
    private static class Tiny
        extends InfiniteKey
    {
        private static final InfiniteKey[] CACHE;
        private static final InfiniteKey ZERO;

        static {
            CACHE = new InfiniteKey[CACHE_SIZE];
            for (int i = 0; i < CACHE_SIZE; ++i) {
                CACHE[i] = new Tiny(i);
            }
            ZERO = CACHE[0];
        }

        private final long value;

        private Tiny(long value)
        {
            this.value = value;
        }

        @Override
        @Nonnull
        public InfiniteKey next()
        {
            final long next = value + 1;
            if (next < 0) {
                return new Large(ZERO, 0);
            } else if (next < CACHE_SIZE) {
                return CACHE[(int)next];
            } else {
                return new Tiny(next);
            }
        }

        @Override
        public int compareTo(@Nonnull InfiniteKey key)
        {
            if (key instanceof Tiny) {
                final Tiny o = (Tiny)key;
                return Long.compare(value, o.value);
            } else {
                assert key instanceof Large;
                return -1;
            }
        }

        @Override
        public boolean equals(Object o)
        {
            return o instanceof Tiny && (value == ((Tiny)o).value);
        }

        @Override
        public int hashCode()
        {
            return (int)(value ^ (value >>> 32));
        }

        @Override
        void addToString(StringBuilder sb)
        {
            sb.append(toHexString(value));
        }
    }

    @Immutable
    private static class Large
        extends InfiniteKey
    {
        @Nonnull
        private final InfiniteKey parent;
        private final long value;

        private Large(@Nonnull InfiniteKey parent,
                      long value)
        {
            this.value = value;
            this.parent = parent;
        }

        @Override
        @Nonnull
        public InfiniteKey next()
        {
            final long next = value + 1;
            if (next > 0) {
                return new Large(parent, next);
            } else {
                return new Large(parent.next(), 0);
            }
        }

        @Override
        public int compareTo(@Nonnull InfiniteKey key)
        {
            if (key instanceof Large) {
                final Large o = (Large)key;
                int diff = parent.compareTo(o.parent);
                if (diff != 0) {
                    return diff;
                } else {
                    return Long.compare(value, o.value);
                }
            } else {
                assert key instanceof Tiny;
                return 1;
            }
        }

        @Override
        public boolean equals(Object key)
        {
            if (key instanceof Large) {
                final Large o = (Large)key;
                return value == o.value && parent.equals(o.parent);
            } else {
                assert key instanceof Tiny;
                return false;
            }
        }

        @Override
        public int hashCode()
        {
            int result = (int)(value ^ (value >>> 32));
            result = 31 * result + parent.hashCode();
            return result;
        }

        @Override
        void addToString(StringBuilder sb)
        {
            parent.addToString(sb);
            sb.append(".");
            sb.append(toHexString(value));
        }
    }
}
