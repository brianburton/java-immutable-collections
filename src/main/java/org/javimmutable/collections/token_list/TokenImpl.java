package org.javimmutable.collections.token_list;

import javax.annotation.Nonnull;

class TokenImpl
    implements TokenList.Token
{
    static final TokenImpl ZERO = token(0);
    private final byte[] values;

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

    static boolean sameBaseAt(int shift,
                              @Nonnull TokenImpl a,
                              @Nonnull TokenImpl b)
    {
        final int maxShift = Math.max(a.maxShift(), b.maxShift());
        for (int s = maxShift; s > shift; --s) {
            if (a.indexAt(s) != b.indexAt(s)) {
                return false;
            }
        }
        return true;
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
    private static TokenImpl commonBase(byte[] a,
                                        byte[] b)
    {
        assert a.length >= b.length;
        if (a.length == b.length && a[0] != b[0]) {
            return new TokenImpl(new byte[a.length + 1]);
        } else if (a.length == b.length) {
            final byte[] newValues = new byte[a.length];
            for (int i = 0; i < a.length - 1; ++i) {
                if (a[i] == b[i]) {
                    newValues[i] = a[i];
                }
            }
            return new TokenImpl(newValues);
        } else {
            final int prefix = a.length - b.length;
            for (int i = 0; i < prefix; ++i) {
                if (a[i] != 0) {
                    return new TokenImpl(new byte[a.length + 1]);
                }
            }
            final byte[] newValues = new byte[a.length];
            for (int i = 0; i < b.length - 1; ++i) {
                if (a[prefix + i] != b[i]) {
                    break;
                }
                newValues[prefix + i] = b[i];
            }
            return new TokenImpl(newValues);
        }
    }

    @Nonnull
    TokenImpl commonBaseWith(@Nonnull TokenImpl other)
    {
        if (values.length >= other.values.length) {
            return commonBase(values, other.values);
        } else {
            return commonBase(other.values, values);
        }
    }

    @Nonnull
    TokenImpl next()
    {
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
