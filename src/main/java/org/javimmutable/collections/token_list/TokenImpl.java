package org.javimmutable.collections.token_list;

import javax.annotation.Nonnull;

class TokenImpl
    implements TokenList.Token
{
    private static final TokenImpl[] CACHE;
    static final TokenImpl ZERO;

    private final byte[] values;

    static {
        CACHE = new TokenImpl[65];
        for (int i = 0; i < 64; ++i) {
            CACHE[i] = token(i);
        }
        CACHE[64] = token(1, 0);
        ZERO = CACHE[0];
    }

    TokenImpl(byte[] values)
    {
        assert values.length > 0;
        this.values = values;
    }

    @Nonnull
    static TokenImpl token(int... values)
    {
        final byte[] tokenValues = new byte[values.length];
        for (int i = 0; i < values.length; ++i) {
            tokenValues[i] = (byte)values[i];
        }
        return new TokenImpl(tokenValues);
    }

    static boolean sameBaseAt(@Nonnull TokenImpl a,
                              @Nonnull TokenImpl b,
                              int shift)
    {
        return basesMatch(a, b, Math.max(a.maxShift(), b.maxShift()), shift + 1);
    }

    static boolean equivalentTo(@Nonnull TokenImpl a,
                                @Nonnull TokenImpl b)
    {
        final int maxShift = Math.max(a.maxShift(), b.maxShift());
        for (int s = 0; s <= maxShift; ++s) {
            if (a.indexAt(s) != b.indexAt(s)) {
                return false;
            }
        }
        return true;
    }

    private static boolean basesMatch(@Nonnull TokenImpl a,
                                      @Nonnull TokenImpl b,
                                      int maxShift,
                                      int shift)
    {
        while (maxShift >= shift) {
            if (a.indexAt(maxShift) != b.indexAt(maxShift)) {
                return false;
            }
            maxShift -= 1;
        }
        return true;
    }

    static int maxCommonShift(@Nonnull TokenImpl a,
                              @Nonnull TokenImpl b)
    {
        int shift = Math.max(a.maxShift(), b.maxShift());
        while (shift > 0) {
            final int index1 = a.indexAt(shift);
            final int index2 = b.indexAt(shift);
            if (index1 != index2) {
                return shift;
            }
            shift -= 1;
        }
        return 0;
    }

    static int commonAncestorShift(@Nonnull TokenImpl a,
                                   @Nonnull TokenImpl b)
    {
        int shift = Math.max(a.trieDepth(), b.trieDepth());
        final int maxShift = Math.max(maxCommonShift(a, b), shift);
        while (shift < maxShift && !basesMatch(a, b, maxShift, shift)) {
            shift += 1;
        }
        assert shift <= maxShift;
        return shift;
    }

    @Nonnull
    TokenImpl base(int shift)
    {
        final int myLength = values.length;
        final int resultLength = Math.max(shift + 1, myLength);
        final byte[] newValues = new byte[resultLength];
        final int myStartIndex = resultLength - myLength;
        final int copyLength = myLength - shift - 1;
        if (copyLength > 0) {
            System.arraycopy(values, 0, newValues, myStartIndex, copyLength);
        }
        return new TokenImpl(newValues);
    }

    @Nonnull
    TokenImpl next()
    {
        if (values.length == 1) {
            return CACHE[values[0] + 1];
        }
        byte[] newValues = values.clone();
        for (int i = newValues.length - 1; i >= 0; --i) {
            assert newValues[i] >= 0;
            assert newValues[i] < 64;
            newValues[i] += 1;
            if (newValues[i] < 64) {
                break;
            }
            newValues[i] = 0;
            if (i == 0) {
                byte[] extendedValues = new byte[newValues.length + 1];
                extendedValues[0] = 1;
                System.arraycopy(newValues, 0, extendedValues, 1, newValues.length);
                newValues = extendedValues;
            }
        }
        return new TokenImpl(newValues);
    }

    int indexAt(int shift)
    {
        return shift >= values.length ? 0 : (int)values[values.length - shift - 1];
    }

    int maxShift()
    {
        return values.length - 1;
    }

    int trieDepth()
    {
        for (int i = 0, index = values.length - 1; i < values.length; ++i, --index) {
            if (values[index] != 0) {
                return i;
            }
        }
        return 0;
    }

    @Nonnull
    TokenImpl withIndexAt(int shift,
                          int index)
    {
        assert index >= 0;
        assert index < 64;
        assert shift < values.length;
        byte[] newValues = values.clone();
        newValues[values.length - 1 - shift] = (byte)index;
        return new TokenImpl(newValues);
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj == this) || ((obj instanceof TokenImpl) && equivalentTo(this, (TokenImpl)obj));
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        for (byte value : values) {
            result = result * 31 + (int)value;
        }
        return result;
    }

    @Nonnull
    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (byte value : values) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(value);
        }
        return sb.toString();
    }
}
