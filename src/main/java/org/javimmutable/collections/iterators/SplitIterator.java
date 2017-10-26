package org.javimmutable.collections.iterators;

public class SplitIterator<T>
{
    private SplitableIterator<T> left;
    private SplitableIterator<T> right;

    public SplitIterator(SplitableIterator<T> left,
                         SplitableIterator<T> right)
    {
        this.left = left;
        this.right = right;
    }

    public SplitableIterator<T> getLeft()
    {
        return left;
    }

    public SplitableIterator<T> getRight()
    {
        return right;
    }
}
