package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.IndexedIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.StringJoiner;

@Immutable
class LeafNode<T>
    extends AbstractNode<T>
    implements ArrayHelper.Allocator<T>
{
    static final int MAX_SIZE = 128;
    static final int SPLIT_SIZE = MAX_SIZE / 2;

    private final T[] values;

    LeafNode(T value)
    {
        values = allocate(1);
        values[0] = value;
    }

    LeafNode(T[] values,
             int count)
    {
        assert count > 0;
        assert count <= MAX_SIZE;
        this.values = allocate(count);
        System.arraycopy(values, 0, this.values, 0, count);
    }

    LeafNode(@Nonnull AbstractNode<T> left,
             @Nonnull AbstractNode<T> right,
             int size)
    {
        assert size > 0;
        assert size <= MAX_SIZE;
        assert size == (left.size() + right.size());
        values = allocate(size);
        left.copyTo(values, 0);
        right.copyTo(values, left.size());
    }

    private LeafNode(T[] values)
    {
        assert values.length > 0;
        assert values.length <= MAX_SIZE;
        this.values = values;
    }

    @Override
    boolean isEmpty()
    {
        return values.length == 0;
    }

    @Override
    int size()
    {
        return values.length;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        return values[index];
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        return insert(values.length, value);
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        } else if (node.depth() > 0) {
            return node.prepend(this);
        } else {
            final LeafNode<T> other = (LeafNode<T>)node;
            final int combinedSize = size() + other.size();
            if (combinedSize <= MAX_SIZE) {
                return new LeafNode<>(ArrayHelper.concat(this, values, other.values));
            } else {
                return new BranchNode<>(this, node, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        return insert(0, value);
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        } else if (node.depth() > 0) {
            return node.append(this);
        } else {
            final LeafNode<T> other = (LeafNode<T>)node;
            final int combinedSize = size() + other.size();
            if (combinedSize <= MAX_SIZE) {
                return new LeafNode<>(ArrayHelper.concat(this, other.values, values));
            } else {
                return new BranchNode<>(node, this, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> assign(int index,
                           T value)
    {
        return new LeafNode<>(ArrayHelper.assign(values, index, value));
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        if (values.length < MAX_SIZE) {
            return new LeafNode<>(ArrayHelper.insert(this, values, index, value));
        } else {
            final T[] left, right;
            if (index <= SPLIT_SIZE) {
                left = ArrayHelper.prefixInsert(this, values, SPLIT_SIZE, index, value);
                right = ArrayHelper.suffix(this, values, SPLIT_SIZE);
            } else {
                left = ArrayHelper.prefix(this, values, SPLIT_SIZE);
                right = ArrayHelper.suffixInsert(this, values, SPLIT_SIZE, index, value);
            }
            return new BranchNode<>(new LeafNode<>(left), new LeafNode<>(right));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        if (values.length == 1) {
            ArrayHelper.checkBounds(values, index);
            return EmptyNode.instance();
        } else {
            return new LeafNode<>(ArrayHelper.delete(this, values, index));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        return delete(0);
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        return delete(values.length - 1);
    }

    @Override
    void copyTo(T[] array,
                int offset)
    {
        System.arraycopy(values, 0, array, offset, values.length);
    }

    @Nonnull
    @Override
    AbstractNode<T> prefix(int limit)
    {
        if (limit < 0 || limit > values.length) {
            throw new IndexOutOfBoundsException();
        } else if (limit == 0) {
            return EmptyNode.instance();
        } else if (limit == values.length) {
            return this;
        } else {
            return new LeafNode<>(ArrayHelper.prefix(this, values, limit));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> suffix(int offset)
    {
        if (offset < 0 || offset > values.length) {
            throw new IndexOutOfBoundsException();
        } else if (offset == 0) {
            return this;
        } else if (offset == values.length) {
            return EmptyNode.instance();
        } else {
            return new LeafNode<>(ArrayHelper.suffix(this, values, offset));
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T[] allocate(int size)
    {
        assert size > 0;
        return (T[])new Object[size];
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of(IndexedArray.retained(values));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return IndexedIterator.iterator(IndexedArray.retained(values));
    }

    @Override
    public void checkInvariants()
    {
        int currentSize = values.length;
        if (currentSize < 1 || currentSize > MAX_SIZE) {
            throw new RuntimeException(String.format("incorrect size: currentSize=%d", currentSize));
        }
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

        LeafNode<?> leafNode = (LeafNode<?>)o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(values, leafNode.values);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", LeafNode.class.getSimpleName() + "[", "]")
            .add("values=" + Arrays.toString(values))
            .toString();
    }
}
