///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections.array.trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.array.bit32.Bit32Array;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiCursor;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.cursors.TransformCursor;

public final class StandardTrieNode<T>
        extends AbstractTrieNode<T>
{
    private final Bit32Array<TrieNode<T>> branches;
    private final Bit32Array<T> values;

    public StandardTrieNode(int branchIndex,
                            int valueIndex,
                            T value)
    {
        if (branchIndex == 0) {
            this.branches = Bit32Array.of();
            this.values = Bit32Array.<T>of().assign(valueIndex, value);
        } else {
            this.branches = Bit32Array.<TrieNode<T>>of().assign(branchIndex & 0x1f, new QuickTrieNode<T>(branchIndex >>> 5, valueIndex, value));
            this.values = Bit32Array.of();
        }
    }

    public StandardTrieNode(Bit32Array<TrieNode<T>> branches,
                            Bit32Array<T> values)
    {
        this.branches = branches;
        this.values = values;
    }

    @Override
    public Holder<T> get(int branchIndex,
                         int valueIndex)
    {
        if (branchIndex == 0) {
            return values.get(valueIndex);
        } else {
            final TrieNode<T> branch = branches.get(branchIndex & 0x1f).getValueOrNull();
            return (branch != null) ? branch.get(branchIndex >>> 5, valueIndex) : Holders.<T>of();
        }
    }

    @Override
    public TrieNode<T> assign(int branchIndex,
                              int valueIndex,
                              T value)
    {
        final Bit32Array<TrieNode<T>> branches = this.branches;
        final Bit32Array<T> values = this.values;
        if (branchIndex == 0) {
            Bit32Array<T> newValues = values.assign(valueIndex, value);
            return (newValues == values) ? this : new StandardTrieNode<T>(branches, values.assign(valueIndex, value));
        } else {
            final int childIndex = branchIndex & 0x1f;
            final int childBranchIndex = branchIndex >>> 5;
            TrieNode<T> child = branches.get(childIndex).getValueOrNull();
            if (child == null) {
                return new StandardTrieNode<T>(branches.assign(childIndex, new QuickTrieNode<T>(childBranchIndex, valueIndex, value)), values);
            } else {
                return new StandardTrieNode<T>(branches.assign(childIndex, child.assign(childBranchIndex, valueIndex, value)), values);
            }
        }
    }

    @Override
    public TrieNode<T> delete(int branchIndex,
                              int valueIndex)
    {
        final Bit32Array<TrieNode<T>> branches = this.branches;
        final Bit32Array<T> values = this.values;
        if (branchIndex == 0) {
            return deleteValueImpl(valueIndex, branches, values);
        } else {
            final int childIndex = branchIndex & 0x1f;
            TrieNode<T> child = branches.get(childIndex).getValueOrNull();
            if (child == null) {
                return this;
            }
            TrieNode<T> newChild = child.delete(branchIndex >>> 5, valueIndex);
            if (newChild == child) {
                return this;
            } else if (newChild.shallowSize() == 0) {
                if (branches.size() == 1 && values.size() == 0) {
                    return EmptyTrieNode.of();
                } else {
                    return new StandardTrieNode<T>(branches.delete(childIndex), values);
                }
            } else {
                return new StandardTrieNode<T>(branches.assign(childIndex, newChild), values);
            }
        }
    }

    private TrieNode<T> deleteValueImpl(int valueIndex,
                                        Bit32Array<TrieNode<T>> branches,
                                        Bit32Array<T> values)
    {
        final Bit32Array<T> newValues = values.delete(valueIndex);
        if (newValues == values) {
            return this;
        }
        if (branches.size() == 0) {
            final int newSize = newValues.size();
            if (newSize == 0) {
                return EmptyTrieNode.of();
            } else if (newSize == 1) {
                final int remainingIndex = newValues.firstIndex();
                return new QuickTrieNode<T>(0, remainingIndex, newValues.get(remainingIndex).getValue());
            }
        }
        return new StandardTrieNode<T>(branches, newValues);
    }

    @Override
    public Cursor<T> cursor()
    {
        final Cursor<T> valuesCursor = TransformCursor.ofValues(LazyCursor.of(values));
        final Cursor<T> branchesCursor = MultiTransformCursor.of(TransformCursor.ofValues(LazyCursor.of(branches)), new Func1<TrieNode<T>, Cursor<T>>()
        {
            @Override
            public Cursor<T> apply(TrieNode<T> node)
            {
                return node.cursor();
            }
        });
        return MultiCursor.of(valuesCursor, branchesCursor);

    }

    @Override
    public int shallowSize()
    {
        return values.size() + branches.size();
    }

    @Override
    public int deepSize()
    {
        int total = values.size();
        for (PersistentMap.Entry<Integer, TrieNode<T>> branch : branches) {
            total += branch.getValue().deepSize();
        }
        return total;
    }

    @Override
    public PersistentMap<Class, Integer> getNodeTypeCounts(PersistentMap<Class, Integer> map)
    {
        map = super.getNodeTypeCounts(map);
        for (PersistentMap.Entry<Integer, TrieNode<T>> branch : branches) {
            map = branch.getValue().getNodeTypeCounts(map);
        }
        return map;
    }
}
