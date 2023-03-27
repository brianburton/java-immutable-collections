package org.javimmutable.collections.common;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import org.javimmutable.collections.IDeque;
import org.javimmutable.collections.IList;

@Immutable
public class DequeListAdaptor<T>
    extends AbstractList<T>
    implements List<T>
{
    private final IDeque<T> list;

    public DequeListAdaptor(@Nonnull IDeque<T> list)
    {
        this.list = list;
    }

    public static <T> DequeListAdaptor<T> of(@Nonnull IDeque<T> list)
    {
        return new DequeListAdaptor<T>(list);
    }

    @Override
    public T get(int index)
    {
        return list.get(index);
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Nonnull
    @Override
    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    @Override
    public Spliterator<T> spliterator()
    {
        return list.spliterator();
    }
}
