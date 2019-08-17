package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.serialization.JImmutableListProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;

import static org.javimmutable.collections.list.TreeBuilder.*;

@SuppressWarnings("deprecation")
@Immutable
public class JImmutableTreeList<T>
    implements JImmutableRandomAccessList<T>,
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
        return new RAListBuilder<T>().add(values, offset, limit).build();
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
    public static <T> ListBuilder<T> listBuilder()
    {
        return new ListBuilder<>();
    }

    @Nonnull
    public static <T> RAListBuilder<T> raListBuilder()
    {
        return new RAListBuilder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableList<T>> createListCollector()
    {
        return Collector.<T, ListBuilder<T>, JImmutableList<T>>of(() -> new ListBuilder<>(),
                                                                  (b, v) -> b.add(v),
                                                                  (b1, b2) -> b1.combineWith(b2),
                                                                  b -> b.build());
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableRandomAccessList<T>> createRAListCollector()
    {
        return Collector.<T, ListBuilder<T>, JImmutableRandomAccessList<T>>of(() -> new ListBuilder<>(),
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

    @Nonnull
    @Override
    public JImmutableTreeList<T> assign(int index,
                                        @Nullable T value)
    {
        return new JImmutableTreeList<>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(@Nullable T value)
    {
        return new JImmutableTreeList<>(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        return create(root.append(nodeFromIterable(values)));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(int index,
                                        @Nullable T value)
    {
        return new JImmutableTreeList<>(root.insert(index, value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertFirst(@Nullable T value)
    {
        return new JImmutableTreeList<>(root.prepend(value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertLast(@Nullable T value)
    {
        return new JImmutableTreeList<>(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return insertAllLast(nodeFromCursor(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(nodeFromIterator(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Iterable<? extends T> values)
    {
        return insertAll(index, nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Cursor<? extends T> values)
    {
        return insertAll(index, nodeFromCursor(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Iterator<? extends T> values)
    {
        return insertAll(index, nodeFromIterator(values));
    }

    @Nonnull
    private JImmutableTreeList<T> insertAll(int index,
                                            @Nonnull AbstractNode<T> other)
    {
        return create(root.prefix(index).append(other).append(root.suffix(index)));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        return insertAllFirst(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Cursor<? extends T> values)
    {
        return insertAllFirst(nodeFromCursor(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return insertAllFirst(nodeFromIterator(values));
    }

    @Nonnull
    private JImmutableTreeList<T> insertAllFirst(@Nonnull AbstractNode<T> other)
    {
        return create(root.prepend(other));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Cursor<? extends T> values)
    {
        return insertAllLast(nodeFromCursor(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(nodeFromIterator(values));
    }

    @Nonnull
    private JImmutableTreeList<T> insertAllLast(@Nonnull AbstractNode<T> other)
    {
        return create(root.append(other));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteFirst()
    {
        return create(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteLast()
    {
        return create(root.deleteLast());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> delete(int index)
    {
        return create(root.delete(index));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteAll()
    {
        return of();
    }

    @Override
    public <A> JImmutableTreeList<A> transform(@Nonnull Func1<T, A> transform)
    {
        final ListBuilder<A> builder = new ListBuilder<>();
        for (T t : this) {
            builder.add(transform.apply(t));
        }
        return builder.build();
    }

    @Override
    public <A> JImmutableTreeList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform)
    {
        final ListBuilder<A> builder = new ListBuilder<>();
        for (T t : this) {
            final Holder<A> ha = transform.apply(t);
            if (ha.isFilled()) {
                builder.add(ha.getValue());
            }
        }
        return builder.build();
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

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> select(@Nonnull Predicate<T> predicate)
    {
        final ListBuilder<T> answer = listBuilder();
        for (T value : this) {
            if (predicate.test(value)) {
                answer.add(value);
            }
        }
        return answer.size() == size() ? this : answer.build();
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> reject(@Nonnull Predicate<T> predicate)
    {
        JImmutableTreeList<T> answer = this;
        int index = 0;
        for (T value : this) {
            assert value == answer.get(index);
            if (predicate.test(value)) {
                answer = answer.delete(index);
            } else {
                index += 1;
            }
        }
        return answer.size() == size() ? this : answer;
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> prefix(int limit)
    {
        return create(root.prefix(limit));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> suffix(int offset)
    {
        return create(root.suffix(offset));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> middle(int offset,
                                        int limit)
    {
        return create(root.prefix(limit).suffix(offset));
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return new ListAdaptor<>(this);
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
        root.checkInvariants();
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return root.cursor();
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

    public static class ListBuilder<T>
        implements JImmutableList.Builder<T>
    {
        private final TreeBuilder<T> builder = new TreeBuilder<>();

        @Nonnull
        public ListBuilder<T> combineWith(@Nonnull ListBuilder<T> other)
        {
            builder.combineWith(other.builder);
            return this;
        }

        @Override
        public int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public ListBuilder<T> add(T value)
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
        public ListBuilder<T> add(Cursor<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> add(Iterator<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> add(Iterable<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> ListBuilder<T> add(K... source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> add(Indexed<? extends T> source,
                                  int offset,
                                  int limit)
        {
            builder.add(source, offset, limit);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> add(Indexed<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        public void checkInvariants()
        {
            builder.checkInvariants();
        }
    }

    public static class RAListBuilder<T>
        implements JImmutableRandomAccessList.Builder<T>
    {
        private final TreeBuilder<T> builder = new TreeBuilder<>();

        @Nonnull
        public RAListBuilder<T> combineWith(@Nonnull RAListBuilder<T> other)
        {
            builder.combineWith(other.builder);
            return this;
        }

        @Override
        public int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public RAListBuilder<T> add(T value)
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
        public RAListBuilder<T> add(Cursor<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public RAListBuilder<T> add(Iterator<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public RAListBuilder<T> add(Iterable<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> RAListBuilder<T> add(K... source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public RAListBuilder<T> add(Indexed<? extends T> source,
                                    int offset,
                                    int limit)
        {
            builder.add(source, offset, limit);
            return this;
        }

        @Nonnull
        @Override
        public RAListBuilder<T> add(Indexed<? extends T> source)
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
