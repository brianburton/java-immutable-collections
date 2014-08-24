package org.javimmutable.collections.array.list;

public class TakeValueResult<T>
{
    private final T value;
    private final Node<T> root;

    public TakeValueResult(T value,
                           Node<T> root)
    {
        this.value = value;
        this.root = root;
    }

    public T getValue()
    {
        return value;
    }

    public Node<T> getRoot()
    {
        return root;
    }
}
