package org.javimmutable.collections.list;

import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.StringJoiner;

class OneValueNode<T>
    extends AbstractNode<T>
{
    private final T value;

    OneValueNode(T value)
    {
        this.value = value;
    }

    @Override
    boolean isEmpty()
    {
        return false;
    }

    @Override
    int size()
    {
        return 1;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }
        return value;
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        return new MultiValueNode<>(this.value, value);
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        if (node.depth() > 0) {
            return node.prepend(this);
        } else if (node.size() == 0) {
            return this;
        } else {
            final int combinedSize = node.size() + 1;
            if (combinedSize <= MultiValueNode.MAX_SIZE) {
                return new MultiValueNode<>(this, node, combinedSize);
            } else {
                return new BranchNode<>(this, node, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        return new MultiValueNode<>(value, this.value);
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        if (node.depth() > 0) {
            return node.append(this);
        } else if (node.size() == 0) {
            return this;
        } else {
            final int combinedSize = node.size() + 1;
            if (combinedSize <= MultiValueNode.MAX_SIZE) {
                return new MultiValueNode<>(node, this, combinedSize);
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
        switch (index) {
            case 0:
                if (value == this.value) {
                    return this;
                } else {
                    return new OneValueNode<>(value);
                }

            case 1:
                return new MultiValueNode<>(this.value, value);

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        switch (index) {
            case 0:
                return new MultiValueNode<>(value, this.value);

            case 1:
                return new MultiValueNode<>(this.value, value);

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        return EmptyNode.instance();
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        return EmptyNode.instance();
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        if (index == 0) {
            return EmptyNode.instance();
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prefix(int limit)
    {
        switch (limit) {
            case 0:
                return EmptyNode.instance();

            case 1:
                return this;

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> suffix(int offset)
    {
        switch (offset) {
            case 0:
                return this;

            case 1:
                return EmptyNode.instance();

            default:
                throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public void checkInvariants()
    {
    }

    @Override
    void copyTo(T[] array,
                int offset)
    {
        array[offset] = value;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return GenericIterator.valueState(parent, value);
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

        OneValueNode<?> that = (OneValueNode<?>)o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode()
    {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return new StringJoiner(", ", OneValueNode.class.getSimpleName() + "[", "]")
            .add("value=" + value)
            .toString();
    }
}
