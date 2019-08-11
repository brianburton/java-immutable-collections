package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.serialization.JImmutableListProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;

import static org.javimmutable.collections.tree_list.TreeBuilder.*;

public class JImmutableTreeList<T>
    implements JImmutableList<T>,
               Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeList EMPTY = new JImmutableTreeList(EmptyNode.instance());
    private static final long serialVersionUID = -121805;

    private final AbstractNode<T> root;

    private JImmutableTreeList(@Nonnull AbstractNode<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableTreeList<T> of()
    {
        return (JImmutableTreeList<T>)EMPTY;
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Indexed<? extends T> values)
    {
        return create(nodeFromIndexed(values));
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Indexed<? extends T> values,
                                               int offset,
                                               int limit)
    {
        return new Builder<T>().add(values, offset, limit).build();
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Iterator<? extends T> values)
    {
        return create(nodeFromIterator(values));
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Cursor<? extends T> values)
    {
        return create(nodeFromCursor(values));
    }

    @Nonnull
    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableList<T>> collector()
    {
        return Collector.<T, Builder<T>, JImmutableList<T>>of(() -> new Builder<>(),
                                                              (b, v) -> b.add(v),
                                                              (b1, b2) -> b1.combineWith(b2),
                                                              b -> b.build());
    }

    @Nonnull
    static <T> JImmutableTreeList<T> create(@Nonnull AbstractNode<T> root)
    {
        if (root.isEmpty()) {
            return of();
        } else {
            return new JImmutableTreeList<>(root);
        }
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
    public JImmutableList<T> assign(int index,
                                    @Nullable T value)
    {
        return create(root.set(index, value));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insert(@Nullable T value)
    {
        return create(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        return create(root.append(nodeFromIterable(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertFirst(@Nullable T value)
    {
        return create(root.prepend(value));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertLast(@Nullable T value)
    {
        return create(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return create(root.append(nodeFromIterable(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return create(root.append(nodeFromCursor(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return create(root.append(nodeFromIterator(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        return create(root.prepend(nodeFromIterable(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAllFirst(@Nonnull Cursor<? extends T> values)
    {
        return create(root.prepend(nodeFromCursor(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return create(root.prepend(nodeFromIterator(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        return create(root.append(nodeFromIterable(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAllLast(@Nonnull Cursor<? extends T> values)
    {
        return create(root.append(nodeFromCursor(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        return create(root.append(nodeFromIterator(values)));
    }

    @Nonnull
    @Override
    public JImmutableList<T> deleteFirst()
    {
        return create(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableList<T> deleteLast()
    {
        return create(root.deleteLast());
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Nonnull
    @Override
    public JImmutableList<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return new ListAdaptor<>(this);
    }

    @Override
    public <A> JImmutableList<A> transform(@Nonnull Func1<T, A> transform)
    {
        return null;
    }

    @Override
    public <A> JImmutableList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform)
    {
        return null;
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return root.cursor();
    }

    @Nonnull
    @Override
    public JImmutableList<T> getInsertableSelf()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {
        //TODO
    }

    @Override
    @Nonnull
    public SplitableIterator<T> iterator()
    {
        return root.iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || ((o instanceof JImmutableList) && IteratorHelper.iteratorEquals(iterator(), ((JImmutableList)o).iterator()));
    }

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(iterator());
    }

    private Object writeReplace()
    {
        return new JImmutableListProxy(this);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private AbstractNode<T> nodeFromIterable(@Nonnull Iterable<? extends T> values)
    {
        AbstractNode<T> otherRoot;
        if (values instanceof JImmutableTreeList) {
            otherRoot = ((JImmutableTreeList<T>)values).root;
        } else {
            otherRoot = nodeFromIterator(values.iterator());
        }
        return otherRoot;
    }

    public static class Builder<T>
        implements JImmutableList.Builder<T>
    {
        private final TreeBuilder<T> builder = new TreeBuilder<>();

        @Nonnull
        public Builder<T> combineWith(@Nonnull Builder<T> other)
        {
            final AbstractNode<T> a = builder.build();
            final AbstractNode<T> b = other.builder.build();
            final AbstractNode<T> ab = a.append(b);
            builder.rebuild(ab);
            return this;
        }

        @Override
        public int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public JImmutableTreeList<T> build()
        {
            return create(builder.build());
        }

        @Nonnull
        @Override
        public Builder<T> add(Cursor<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Iterator<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Iterable<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> Builder<T> add(K... source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source,
                              int offset,
                              int limit)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        public void checkInvariants()
        {
            builder.checkInvariants();
        }
    }
}
