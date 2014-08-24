package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.array.list.EmptyNode;
import org.javimmutable.collections.array.list.Node;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class JImmutableArrayList2<T>
        implements JImmutableList<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableArrayList2 EMPTY = new JImmutableArrayList2(EmptyNode.of());

    private final Node<T> root;

    private JImmutableArrayList2(Node<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableArrayList2<T> of()
    {
        return (JImmutableArrayList2<T>)EMPTY;
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Override
    public T get(int index)
    {
        return root.get(index);
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> assign(int index,
                                          @Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insert(@Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insert(@Nonnull Iterable<? extends T> values)
    {
        Node<T> newRoot = root;
        for (T value : values) {
            newRoot = newRoot.insertLast(value);
        }
        return new JImmutableArrayList2<T>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insertFirst(@Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.insertFirst(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insertLast(@Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> deleteFirst()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList2<T>(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> deleteLast()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList2<T>(root.deleteLast());
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return ListAdaptor.of(this);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return root.cursor();
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    void checkInvariants()
    {
        root.checkInvariants();
    }
}
