///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.list;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@NotThreadSafe
class TreeBuilder<T>
{
    private final T[] buffer;
    private int count;
    private int size;
    private BranchBuilder<T> parent;

    @SuppressWarnings("unchecked")
    TreeBuilder()
    {
        buffer = (T[])new Object[MultiValueNode.MAX_SIZE];
    }

    void clear()
    {
        Arrays.fill(buffer, null);
        count = 0;
        size = 0;
        parent = null;
    }

    @Nonnull
    AbstractNode<T> build()
    {
        AbstractNode<T> answer;
        switch (count) {
            case 0:
                answer = EmptyNode.instance();
                break;
            case 1:
                answer = new OneValueNode<>(buffer[0]);
                break;
            default:
                answer = new MultiValueNode<>(buffer, count);
                break;
        }
        if (parent != null) {
            answer = parent.build(answer);
        }
        return answer;
    }

    int size()
    {
        return size;
    }

    void combineWith(@Nonnull TreeBuilder<T> other)
    {
        final AbstractNode<T> a = build();
        final AbstractNode<T> b = other.build();
        final AbstractNode<T> ab = a.append(b);
        rebuild(ab);
    }

    /**
     * Clears any existing data in this builder and then populates the builder with
     * nodes from the provided tree.  At each level of the tree it creates a parent
     * branch using the left node and proceeds further using the right node.
     * At the leaf all values are copied into the buffer.
     */
    void rebuild(@Nonnull AbstractNode<T> node)
    {
        count = 0;
        size = node.size();
        parent = null;
        while (node.depth() > 0) {
            parent = new BranchBuilder<>(parent, node.left());
            node = node.right();
        }
        for (T t : node) {
            buffer[count++] = t;
        }
    }

    void add(T value)
    {
        buffer[count++] = value;
        if (count == MultiValueNode.MAX_SIZE) {
            final AbstractNode<T> leaf = new MultiValueNode<>(buffer, count);
            if (parent == null) {
                parent = new BranchBuilder<>(leaf);
            } else {
                parent.add(leaf);
            }
            count = 0;
        }
        size += 1;
    }

    void add(@Nonnull Iterator<? extends T> source)
    {
        while (source.hasNext()) {
            add(source.next());
        }
    }

    void add(@Nonnull Iterable<? extends T> source)
    {
        add(source.iterator());
    }

    @SafeVarargs
    final <K extends T> void add(K... source)
    {
        for (K k : source) {
            add(k);
        }
    }

    void add(@Nonnull Indexed<? extends T> source,
             int offset,
             int limit)
    {
        for (int i = offset; i < limit; ++i) {
            add(source.get(i));
        }
    }

    void add(@Nonnull Indexed<? extends T> source)
    {
        add(source, 0, source.size());
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIndexed(@Nonnull Indexed<? extends T> source)
    {
        return nodeFromIndexed(source, 0, source.size());
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIndexed(@Nonnull Indexed<? extends T> source,
                                               int offset,
                                               int limit)
    {
        final int sourceSize = limit - offset;
        if (sourceSize == 0) {
            return EmptyNode.instance();
        } else if (sourceSize == 1) {
            return new OneValueNode<>(source.get(offset));
        } else if (sourceSize <= MultiValueNode.MAX_SIZE) {
            return new MultiValueNode<>(source.subArray(offset, limit), sourceSize);
        }

        final List<AbstractNode<T>> nodes = new ArrayList<>(1 + sourceSize / MultiValueNode.MAX_SIZE);
        int o = offset;
        while (o < limit) {
            final int nodeSize = Math.min(MultiValueNode.MAX_SIZE, limit - o);
            if (nodeSize == 1) {
                nodes.add(new OneValueNode<>(source.get(o)));
            } else {
                nodes.add(new MultiValueNode<>(source.subArray(o, o + nodeSize), nodeSize));
            }
            o += nodeSize;
        }
        int nodeCount = nodes.size();
        while (nodeCount > 1) {
            int writeIndex = 0;
            int readIndex = 0;
            int remaining = nodeCount;
            while (remaining > 0) {
                if (remaining > 1) {
                    nodes.set(writeIndex, BranchNode.balance(nodes.get(readIndex), nodes.get(readIndex + 1)));
                    readIndex += 2;
                    writeIndex += 1;
                    remaining -= 2;
                } else {
                    nodes.set(writeIndex - 1, nodes.get(writeIndex - 1).append(nodes.get(readIndex)));
                    readIndex += 1;
                    remaining -= 1;
                }
            }
            nodeCount = writeIndex;
        }
        return nodes.get(0);
    }

    @Nonnull
    static <T> AbstractNode<T> nodeFromIterator(@Nonnull Iterator<? extends T> values)
    {
        TreeBuilder<T> builder = new TreeBuilder<>();
        builder.add(values);
        return builder.build();
    }

    void checkInvariants()
    {
        if (size != computeSize()) {
            throw new IllegalStateException("size mismatch");
        }
        if (parent != null) {
            parent.checkInvariants();
        }
    }

    private int computeSize()
    {
        int answer = count;
        if (parent != null) {
            answer += parent.computeSize();
        }
        return answer;
    }

    private static class BranchBuilder<T>
    {
        private BranchBuilder<T> parent;
        private AbstractNode<T> buffer;

        private BranchBuilder(@Nonnull BranchBuilder<T> parent,
                              @Nonnull AbstractNode<T> node)
        {
            this.parent = parent;
            buffer = node;
        }

        private BranchBuilder(@Nonnull AbstractNode<T> node)
        {
            buffer = node;
        }

        private void add(@Nonnull AbstractNode<T> node)
        {
            if (buffer == null) {
                buffer = node;
            } else {
                final AbstractNode<T> branch = new BranchNode<>(buffer, node);
                if (parent == null) {
                    parent = new BranchBuilder<>(branch);
                } else {
                    parent.add(branch);
                }
                buffer = null;
            }
        }

        @Nonnull
        private AbstractNode<T> build(@Nonnull AbstractNode<T> extra)
        {
            AbstractNode<T> answer;
            if (buffer == null) {
                answer = extra;
            } else {
                answer = buffer.append(extra);
            }
            if (parent != null) {
                answer = parent.build(answer);
            }
            return answer;
        }

        private int computeSize()
        {
            int answer = 0;
            if (buffer != null) {
                answer += buffer.size();
            }
            if (parent != null) {
                answer += parent.computeSize();
            }
            return answer;
        }

        private void checkInvariants()
        {
            if (buffer == null && parent == null) {
                throw new IllegalStateException("buffer is null");
            }
            if (parent != null) {
                parent.checkInvariants();
            }
        }
    }
}
