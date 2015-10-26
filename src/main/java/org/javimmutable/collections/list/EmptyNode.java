///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Node implementation containing no values.  Implemented as a singleton.
 *
 * @param <T>
 */
@Immutable
final class EmptyNode<T>
        implements Node<T>
{
    private static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode()
    {
    }

    @SuppressWarnings("unchecked")
    static <T> EmptyNode<T> of()
    {
        return (EmptyNode<T>)INSTANCE;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public boolean isFull()
    {
        return false;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public int getDepth()
    {
        return 1;
    }

    @Override
    public Node<T> deleteFirst()
    {
        throw new IllegalStateException();
    }

    @Override
    public Node<T> deleteLast()
    {
        throw new IllegalStateException();
    }

    @Override
    public Node<T> insertFirst(T value)
    {
        return new LeafNode<T>(value);
    }

    @Override
    public Node<T> insertLast(T value)
    {
        return new LeafNode<T>(value);
    }

    @Override
    public boolean containsIndex(int index)
    {
        return false;
    }

    @Override
    public T get(int index)
    {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public Node<T> assign(int index,
                          T value)
    {
        throw new IndexOutOfBoundsException();
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of();
    }

    @Override
    public void checkInvariants()
    {
        //TODO: fix empty checkInvariants()
    }
}
