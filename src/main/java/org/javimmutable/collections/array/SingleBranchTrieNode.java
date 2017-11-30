///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SingleBranchTrieNode<T>
    extends TrieNode<T>
{
    private final int shift;
    private final int branchIndex;
    private final TrieNode<T> child;

    private SingleBranchTrieNode(int shift,
                                 int branchIndex,
                                 TrieNode<T> child)
    {
        assert shift >= 0;
        this.shift = shift;
        this.branchIndex = branchIndex;
        this.child = child;
    }

    static <T> SingleBranchTrieNode<T> forIndex(int shift,
                                                int index,
                                                TrieNode<T> child)
    {
        final int branchIndex = (index >>> shift) & 0x1f;
        return new SingleBranchTrieNode<>(shift, branchIndex, child);
    }

    static <T> SingleBranchTrieNode<T> forBranchIndex(int shift,
                                                      int branchIndex,
                                                      TrieNode<T> child)
    {
        return new SingleBranchTrieNode<>(shift, branchIndex, child);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.getValueOr(shift - 5, index, defaultValue) : defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.find(shift - 5, index) : Holders.of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex == branchIndex) {
            TrieNode<T> newChild = child.assign(shift - 5, index, value, sizeDelta);
            return selectNodeForUpdateResult(shift, branchIndex, newChild);
        } else {
            return MultiBranchTrieNode.forBranchIndex(shift, this.branchIndex, child).assign(shift, index, value, sizeDelta);
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex != branchIndex) {
            return this;
        } else {
            final TrieNode<T> newChild = child.delete(shift - 5, index, sizeDelta);
            return selectNodeForDeleteResult(shift, branchIndex, newChild);
        }
    }

    @Override
    public int getShift()
    {
        return shift;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public TrieNode<T> trimmedToMinimumDepth()
    {
        return (branchIndex == 0) ? child.trimmedToMinimumDepth() : this;
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return child.iterator();
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return child.cursor();
    }

    @Override
    public void checkInvariants()
    {
        if (shift < 0 || shift > ROOT_SHIFT) {
            throw new IllegalStateException("illegal shift value: " + shift);
        }
        if (branchIndex < 0 || branchIndex >= 32) {
            throw new IllegalStateException("illegal branchIndex value: " + branchIndex);
        }
    }

    // for tests
    int getBranchIndex()
    {
        return branchIndex;
    }

    // for tests
    TrieNode<T> getChild()
    {
        return child;
    }

    private TrieNode<T> selectNodeForUpdateResult(int shift,
                                                  int branchIndex,
                                                  TrieNode<T> newChild)
    {
        assert newChild.isLeaf() || (newChild.getShift() == (shift - 5));
        return (newChild == child) ? this : new SingleBranchTrieNode<>(shift, branchIndex, newChild);
    }

    private TrieNode<T> selectNodeForDeleteResult(int shift,
                                                  int branchIndex,
                                                  TrieNode<T> newChild)
    {
        if (newChild == child) {
            return this;
        } else if (newChild.isEmpty()) {
            return of();
        } else if (newChild.isLeaf()) {
            return newChild;
        } else {
            assert newChild.getShift() == (shift - 5);
            return new SingleBranchTrieNode<>(shift, branchIndex, newChild);
        }
    }
}
