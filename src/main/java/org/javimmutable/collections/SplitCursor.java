package org.javimmutable.collections;

import javax.annotation.concurrent.Immutable;
import java.util.Objects;

@Immutable
public class SplitCursor<T>
{
    private final Cursor<T> left;
    private final Cursor<T> right;

    public SplitCursor(Cursor<T> left,
                       Cursor<T> right)
    {
        this.left = left;
        this.right = right;
    }

    public Cursor<T> getLeft()
    {
        return left;
    }

    public Cursor<T> getRight()
    {
        return right;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SplitCursor<?> that = (SplitCursor<?>)o;
        return Objects.equals(left, that.left) &&
               Objects.equals(right, that.right);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(left, right);
    }
}
