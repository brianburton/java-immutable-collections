///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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
    private final LeafBuilder<T> leafBuilder = new LeafBuilder<>();
    private int size;

    synchronized int size()
    {
        return size;
    }
      
    synchronized void add(T value)
    {
        leafBuilder.add(value);
        size += 1;
    }

    synchronized BtreeNode<T> build()
    {
        return leafBuilder.build().compress();
    }

    private static class BranchBuilder<T>
    {
        private final BtreeNode<T>[] buffer;
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
            assert count < BtreeNode.MAX_CHILDREN;
            buffer[count] = node;
            count += 1;
            if (count == BtreeNode.MAX_CHILDREN) {
                push(BtreeNode.MIN_CHILDREN);
            }
        }

        private BtreeNode<T> build(@Nonnull BtreeNode<T> extraNode)
        {
            assert count < BtreeNode.MAX_CHILDREN;
            buffer[count] = extraNode;
            final BtreeNode<T> node = BtreeBranchNode.of(IndexedArray.retained(buffer), 0, count + 1);
            if (parent == null) {
                return node;
            } else {
                return parent.build(node);
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
        private final T[] buffer;
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
            assert count < BtreeNode.MAX_CHILDREN;
            buffer[count] = value;
            count += 1;
            if (count == BtreeNode.MAX_CHILDREN) {
                push(BtreeNode.MIN_CHILDREN);
            }
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
                assert count >= BtreeNode.MIN_CHILDREN;
                return parent.build(BtreeLeafNode.of(IndexedArray.retained(buffer), 0, count));
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
