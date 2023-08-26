package org.javimmutable.collections.deque;

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

class ForwardBuilder<T>
{
    private final Leaf<T> leaf;

    private ForwardBuilder(Leaf<T> leaf)
    {
        this.leaf = leaf;
    }

    void add(T value)
    {
        leaf.append(value);
    }

    void addAll(Indexed<T> values)
    {
        for (int i = 0; i < values.size(); ++i) {
            add(values.get(i));
        }
    }

    Node<T> build()
    {
        return leaf.toNode().prune();
    }

    int size()
    {
        return leaf.totalSize();
    }

    void clear()
    {
        leaf.clear();
    }

    static <T> ForwardBuilder<T> appendToExistingNode(Node<T> baseNode)
    {
        LeafNode<T> leafNode = baseNode.castAsLeaf();
        if (leafNode != null) {
            return new ForwardBuilder<>(new Leaf<>(leafNode.values(), null));
        }

        BranchNode<T> branchNode = baseNode.castAsBranch();
        if (branchNode == null) {
            assert baseNode.isEmpty();
            return new ForwardBuilder<>(new Leaf<>(IndexedHelper.empty(), null));
        }

        Branch<T> branch = new Branch<>(branchNode.getDepth(), branchNode.filledNodes(), branchNode.prefix(), null);
        baseNode = branchNode.suffix();
        while (true) {
            while (branch.depth > baseNode.getDepth() + 1) {
                branch = new Branch<>(branch.depth - 1, branch);
            }
            leafNode = baseNode.castAsLeaf();
            if (leafNode != null) {
                assert branch.depth == 2;
                return new ForwardBuilder<>(new Leaf<>(leafNode.values(), branch));
            }
            branchNode = baseNode.castAsBranch();
            if (branchNode == null) {
                assert baseNode.isEmpty();
                assert branch.depth == 2;
                return new ForwardBuilder<>(new Leaf<>(IndexedHelper.empty(), branch));
            }
            branch = new Branch<>(branchNode.getDepth(), branchNode.filledNodes(), branchNode.prefix(), branch);
            baseNode = branchNode.suffix();
        }
    }

    private static class Leaf<T>
    {
        private final T[] values;
        private Branch<T> next;
        private int length;
        private int capacity;

        private Leaf(Indexed<T> values,
                     @Nullable Branch<T> next)
        {
            assert next == null || next.depth == 2;

            this.values = DequeHelper.allocateValues(32);
            length = values.size();
            capacity = (next != null)
                       ? Math.min(32, next.capacity) - length
                       : 32 - length;
            for (int i = 0; i < length; ++i) {
                this.values[i] = values.get(i);
            }
            this.next = next;
            pushIfFull();
            assert capacity > 0;
        }

        private void append(T value)
        {
            assert capacity > 0;
            values[length++] = value;
            capacity -= 1;
            if (capacity == 0) {
                push();
            }
            assert capacity > 0;
        }

        private int totalSize()
        {
            int answer = length;
            if (next != null) {
                answer += next.totalSize();
            }
            return answer;
        }

        private void clear()
        {
            Arrays.fill(values, null);
            next = null;
            length = 0;
            capacity = 32;
        }

        /**
         * Called after initialization to push all full nodes to their next builder so that we
         * always maintain capacity in every node of the builder.
         */
        private void pushIfFull()
        {
            if (next != null) {
                next.pushIfFull();
                capacity = Math.min(next.capacity, 32) - length;
                assert capacity >= 0;
            }
            if (capacity == 0) {
                push();
            }
        }

        private void push()
        {
            Node<T> newNode = LeafNode.fromList(IndexedArray.retained(values), 0, length);
            if (next == null) {
                next = new Branch<>(2, null);
            }
            next.append(newNode);

            length = 0;
            capacity = Math.min(next.capacity, 32);
            Arrays.fill(values, null);
        }

        private Node<T> toNode()
        {
            Node<T> leafNode = length == 0 ? EmptyNode.of() : LeafNode.fromList(IndexedArray.retained(values), 0, length);
            return next == null ? leafNode : next.toNode(leafNode);
        }
    }

    private static class Branch<T>
    {
        private final Node<T>[] nodes;
        private final int depth;
        private Branch<T> next;
        private Node<T> prefix;
        private int length;
        private int capacity;
        private int size;

        private Branch(int depth,
                       @Nullable Branch<T> next)
        {
            assert next == null || next.depth == depth + 1;

            this.nodes = DequeHelper.allocateNodes(32);
            this.depth = depth;
            this.next = next;
            prefix = EmptyNode.of();
            capacity = (next != null)
                       ? Math.min(next.capacity, DequeHelper.sizeForDepth(depth))
                       : DequeHelper.sizeForDepth(depth);
            assert capacity >= 0;
        }

        private Branch(int depth,
                       @Nonnull Indexed<Node<T>> startNodes,
                       @Nonnull Node<T> prefix,
                       @Nullable Branch<T> next)
        {
            assert startNodes.size() == 0 || startNodes.get(0).getDepth() < depth;
            assert next == null || next.depth == depth + 1;

            this.nodes = DequeHelper.allocateNodes(32);
            this.depth = depth;
            this.next = next;
            this.prefix = prefix;
            capacity = (next != null)
                       ? Math.min(next.capacity, DequeHelper.sizeForDepth(depth))
                       : DequeHelper.sizeForDepth(depth);

            length = startNodes.size();
            size = prefix.size();
            for (int i = 0; i < length; ++i) {
                Node<T> node = startNodes.get(i);
                nodes[i] = node;
                size += node.size();
            }
            capacity -= size;
            assert capacity >= 0;
        }

        private void append(Node<T> node)
        {
            assert capacity > 0;
            assert length < nodes.length;
            assert node.getDepth() < depth;
            assert node.size() <= capacity;
            assert node.isFull() || node.size() == capacity;

            Node<T> suffix = EmptyNode.of();
            if (node.isFull()) {
                nodes[length++] = node;
                capacity -= node.size();
                size += node.size();
            } else if (node.size() == capacity) {
                capacity -= node.size();
                size += node.size();
                suffix = node;
            } else {
                throw new AssertionError("node.isFull() || node.size() == capacity");
            }
            if (capacity == 0) {
                Node<T> newNode =
                    size == node.size()
                    ? node
                    : BranchNode.forNodeBuilder(depth, size, prefix,
                                                IndexedArray.retained(nodes),
                                                0, length, suffix);
                if (next == null) {
                    next = new Branch<>(depth + 1, null);
                }
                next.append(newNode);
                prefix = EmptyNode.of();
                length = 0;
                size = 0;
                capacity = Math.min(next.capacity, DequeHelper.sizeForDepth(depth));
                Arrays.fill(nodes, null);
            }
            assert capacity > 0;
        }

        private int totalSize()
        {
            int answer = size;
            if (next != null) {
                answer += next.totalSize();
            }
            return answer;
        }

        private void pushIfFull()
        {
            if (next != null) {
                next.pushIfFull();
                capacity = Math.min(next.capacity, DequeHelper.sizeForDepth(depth)) - size;
                assert capacity >= 0;
            }
            if (capacity == 0) {
                Node<T> newNode = BranchNode.forNodeBuilder(depth, size, prefix,
                                                            IndexedArray.retained(nodes),
                                                            0, length, EmptyNode.of());
                if (next == null) {
                    next = new Branch<>(depth + 1, null);
                }
                next.append(newNode);
                prefix = EmptyNode.of();
                length = 0;
                size = 0;
                capacity = Math.min(next.capacity, DequeHelper.sizeForDepth(depth));
                Arrays.fill(nodes, null);
            }
        }

        private Node<T> toNode(Node<T> suffix)
        {
            assert suffix.size() < capacity;

            if (size > 0) {
                suffix = BranchNode.forNodeBuilder(depth,
                                                   size + suffix.size(),
                                                   prefix,
                                                   IndexedArray.retained(nodes), 0, length,
                                                   suffix);
            }
            if (next == null) {
                return suffix;
            } else {
                return next.toNode(suffix);
            }
        }
    }
}
