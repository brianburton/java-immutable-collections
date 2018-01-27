package org.javimmutable.collections.btree_list;

import org.javimmutable.collections.indexed.IndexedArray;

import javax.annotation.Nonnull;

/**
 * Mutable builder object for constructing well formed btree lists without
 * requiring all values to be collected into java.util.List first.  The
 * build() method can be called multiple times safely, for example to build
 * multiple root nodes successively.
 */
class BtreeNodeBuilder<T>
{
    private final LeafBuilder<T> leafBuilder;

    BtreeNodeBuilder()
    {
        leafBuilder = new LeafBuilder<>();
    }

    void add(T value)
    {
        leafBuilder.add(value);
    }

    BtreeNode<T> build()
    {
        return leafBuilder.snapshot().build();
    }

    private static class BranchBuilder<T>
    {
        private BtreeNode<T>[] buffer;
        private int count;
        private BranchBuilder<T> parent;

        @SuppressWarnings("unchecked")
        private BranchBuilder()
        {
            this(new BtreeNode[BtreeNode.MAX_CHILDREN], 0, null);
        }

        private BranchBuilder(BtreeNode<T>[] buffer,
                              int count,
                              BranchBuilder<T> parent)
        {
            this.buffer = buffer;
            this.count = count;
            this.parent = parent;
        }

        private void add(@Nonnull BtreeNode<T> node)
        {
            if (count == BtreeNode.MAX_CHILDREN) {
                push(BtreeNode.MIN_CHILDREN);
            }
            buffer[count] = node;
            count += 1;
        }

        private BranchBuilder<T> snapshot()
        {
            return new BranchBuilder<>(buffer.clone(), count, (parent == null) ? null : parent.snapshot());
        }

        private BtreeNode<T> build()
        {
            assert count > 1;
            if (parent == null) {
                return BtreeBranchNode.of(IndexedArray.retained(buffer), 0, count);
            } else {
                push(count);
                return parent.build();
            }
        }

        private void push(int howMany)
        {
            assert howMany <= count;
            assert howMany >= BtreeNode.MIN_CHILDREN;
            if (parent == null) {
                parent = new BranchBuilder<>();
            }
            parent.add(BtreeBranchNode.of(IndexedArray.retained(buffer), 0, howMany));
            System.arraycopy(buffer, howMany, buffer, 0, count - howMany);
            count -= howMany;
        }
    }

    private static class LeafBuilder<T>
    {
        private T[] buffer;
        private int count;
        private BranchBuilder<T> parent;

        @SuppressWarnings("unchecked")
        private LeafBuilder()
        {
            this((T[])new Object[BtreeNode.MAX_CHILDREN], 0, null);
        }

        private LeafBuilder(T[] buffer,
                            int count,
                            BranchBuilder<T> parent)
        {
            this.buffer = buffer;
            this.count = count;
            this.parent = parent;
        }

        private void add(@Nonnull T value)
        {
            if (count == BtreeNode.MAX_CHILDREN) {
                push(BtreeNode.MIN_CHILDREN);
            }
            buffer[count] = value;
            count += 1;
        }

        private LeafBuilder<T> snapshot()
        {
            return new LeafBuilder<>(buffer.clone(), count, (parent == null) ? null : parent.snapshot());
        }

        private BtreeNode<T> build()
        {
            if (parent == null) {
                if (count == 0) {
                    return BtreeEmptyNode.of();
                } else {
                    return BtreeLeafNode.of(IndexedArray.retained(buffer), 0, count);
                }
            } else {
                assert count > 0;
                push(count);
                return parent.build();
            }
        }

        private void push(int howMany)
        {
            assert howMany <= count;
            assert howMany >= BtreeNode.MIN_CHILDREN;
            if (parent == null) {
                parent = new BranchBuilder<>();
            }
            parent.add(BtreeLeafNode.of(IndexedArray.retained(buffer), 0, howMany));
            System.arraycopy(buffer, howMany, buffer, 0, count - howMany);
            count -= howMany;
        }
    }
}
