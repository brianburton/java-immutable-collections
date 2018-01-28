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

import org.javimmutable.collections.Tuple2;

class BtreeInsertResult<T>
{
    enum Type
    {
        INPLACE,
        SPLIT
    }

    final Type type;
    final BtreeNode<T> newNode;
    final BtreeNode<T> extraNode;

    private BtreeInsertResult(Type type,
                              BtreeNode<T> newNode,
                              BtreeNode<T> extraNode)
    {
        this.type = type;
        this.newNode = newNode;
        this.extraNode = extraNode;
    }

    static <T> BtreeInsertResult<T> createInPlace(BtreeNode<T> newNode)
    {
        return new BtreeInsertResult<>(Type.INPLACE, newNode, null);
    }

    static <T> BtreeInsertResult<T> createSplit(BtreeNode<T> newNode,
                                                BtreeNode<T> extraNode)
    {
        return new BtreeInsertResult<>(Type.SPLIT, newNode, extraNode);
    }

    static <T> BtreeInsertResult<T> createSplit(Tuple2<BtreeNode<T>, BtreeNode<T>> nodes)
    {
        return createSplit(nodes.getFirst(), nodes.getSecond());
    }

    @Override
    public String toString()
    {
        return String.format("<%s,%s,%s>", type, newNode, extraNode);
    }
}
