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

package org.javimmutable.collections.array;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class TrieArrayBuilder<T>
{
    private final LeafBuilder<T> leafBuilder = new LeafBuilder<>();

    synchronized int size()
    {
        return leafBuilder.index;
    }

    synchronized void add(T value)
    {
        leafBuilder.add(value);
    }

    @Nonnull
    synchronized TrieNode<T> build()
    {
        return leafBuilder.build();
    }

    private static class LeafBuilder<T>
    {
        private final TrieNode<T>[] leaves;
        private int offset;
        private int index;
        private BranchBuilder<T> next;

        @SuppressWarnings("unchecked")
        private LeafBuilder()
        {
            leaves = new LeafTrieNode[32];
        }

        private void add(T value)
        {
            leaves[offset] = LeafTrieNode.of(index, value);
            index += 1;
            offset += 1;
            if (offset == 32) {
                push();
            }
        }

        private void push()
        {
            assert offset == 32;
            TrieNode<T> branch = new FullBranchTrieNode<>(0, leaves.clone());
            if (next == null) {
                next = new BranchBuilder<>(5);
            }
            next.add(branch);
            offset = 0;
        }

        private TrieNode<T> build()
        {
            assert offset < 32;
            TrieNode<T> node = branchForBuild(leaves, offset, 0);
            return resultForBuild(next, node);
        }
    }

    private static class BranchBuilder<T>
    {
        private final TrieNode<T>[] nodes;
        private final int shift;
        private int offset;
        private BranchBuilder<T> next;

        @SuppressWarnings("unchecked")
        private BranchBuilder(int shift)
        {
            nodes = new TrieNode[32];
            this.shift = shift;
        }

        private void add(TrieNode<T> node)
        {
            nodes[offset] = node;
            offset += 1;
            if (offset == 32) {
                push();
            }
        }

        private void push()
        {
            assert offset == 32;
            TrieNode<T> branch = new FullBranchTrieNode<>(shift, nodes.clone());
            if (next == null) {
                next = new BranchBuilder<>(shift + 5);
            }
            next.add(branch);
            offset = 0;
        }

        private TrieNode<T> build(@Nullable TrieNode<T> extra)
        {
            assert offset < 32;
            int length;
            if (extra != null) {
                nodes[offset] = extra;
                length = offset + 1;
            } else {
                length = offset;
            }
            TrieNode<T> node = branchForBuild(nodes, length, shift);
            return resultForBuild(next, node);
        }
    }

    private static <T> TrieNode<T> branchForBuild(@Nonnull TrieNode<T>[] nodes,
                                                  int length,
                                                  int shift)
    {
        TrieNode<T> branch;
        switch (length) {
            case 0:
                branch = null;
                break;
            case 1:
                branch = SingleBranchTrieNode.forBranchIndex(shift, 0, nodes[0]);
                break;
            case 32:
                branch = new FullBranchTrieNode<>(shift, nodes.clone());
                break;
            default:
                branch = MultiBranchTrieNode.forEntries(shift, nodes, length);
                break;
        }
        return branch;
    }

    private static <T> TrieNode<T> resultForBuild(@Nullable BranchBuilder<T> next,
                                                  @Nullable TrieNode<T> node)
    {
        if (next != null) {
            return next.build(node);
        } else if (node == null) {
            return EmptyTrieNode.of();
        } else {
            return node;
        }
    }
}
