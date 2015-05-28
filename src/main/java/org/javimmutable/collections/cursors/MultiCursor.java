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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.common.IteratorAdaptor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;

/**
 * A Cursor that combines multiple Cursors into a single virtual Cursor
 * that visits all values from all Cursors in order.
 *
 * @param <T>
 */
@Immutable
public class MultiCursor<T>
        implements Cursor<T>
{
    private final Node<T> nodes;
    private final Cursor<T> cursor;

    public MultiCursor(Cursor<T>... cursors)
    {
        Node<T> nodes = null;
        for (int i = cursors.length - 1; i >= 0; --i) {
            nodes = new Node<T>(cursors[i], nodes);
        }
        this.nodes = nodes;
        this.cursor = null;
    }

    private MultiCursor(Node<T> nodes,
                        Cursor<T> cursor)
    {
        this.nodes = nodes;
        this.cursor = cursor;
    }

    /**
     * Creates an object for building a MultiCursor from an arbitrary sequence of Cursors.
     *
     * @param <T>
     * @return
     */
    public static <T> Builder<T> builder()
    {
        return new Builder<T>();
    }

    public static <T, C extends Cursor<T>> MultiCursor<T> of(C cursor1,
                                                             C cursor2)
    {
        return new MultiCursor<T>(new Node<T>(cursor1, new Node<T>(cursor2, null)), null);
    }

    public static <T, C extends Cursor<T>> MultiCursor<T> of(C cursor1,
                                                             C cursor2,
                                                             C cursor3)
    {
        return new MultiCursor<T>(new Node<T>(cursor1, new Node<T>(cursor2, new Node<T>(cursor3, null))), null);
    }

    public static <T, C extends Cursor<T>> MultiCursor<T> of(C... cursors)
    {
        return new MultiCursor<T>(cursors);
    }

    @Nonnull
    @Override
    public Cursor<T> start()
    {
        return (cursor == null) ? next() : this;
    }

    @Nonnull
    @Override
    public Cursor<T> next()
    {
        if ((cursor != null) && cursor.hasValue()) {
            Cursor<T> nextCursor = cursor.next();
            if (nextCursor.hasValue()) {
                return new MultiCursor<T>(nodes, nextCursor);
            }
        }

        Node<T> newNodes = nodes;
        while (newNodes != null) {
            Cursor<T> nextCursor = newNodes.cursor.next();
            if (nextCursor.hasValue()) {
                return new MultiCursor<T>(newNodes.next, nextCursor);
            }
            newNodes = newNodes.next;
        }

        return EmptyStartedCursor.of();
    }

    @Override
    public boolean hasValue()
    {
        if (cursor == null) {
            throw new NotStartedException();
        }
        return cursor.hasValue();
    }

    @Override
    public T getValue()
    {
        if (cursor == null) {
            throw new NotStartedException();
        }
        return cursor.getValue();
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(this);
    }

    /**
     * Builder class to build a MultiCursor from an arbitrary sequence of Cursors.
     * Cursors will be visited in the same order that add() is called.
     *
     * @param <T>
     */
    public static class Builder<T>
    {
        private final Node<T> nodes;

        private Builder()
        {
            this(null);
        }

        private Builder(Node<T> nodes)
        {
            this.nodes = nodes;
        }

        public Builder<T> add(Cursor<T> cursor)
        {
            return new Builder<T>(new Node<T>(cursor, nodes));
        }

        public MultiCursor<T> build()
        {
            Node<T> inOrderNodes = null;
            for (Node<T> n = nodes; n != null; n = n.next) {
                inOrderNodes = new Node<T>(n.cursor, inOrderNodes);
            }
            return new MultiCursor<T>(inOrderNodes, null);
        }
    }

    @Immutable
    private static class Node<T>
    {
        private final Cursor<T> cursor;
        private final Node<T> next;

        private Node(Cursor<T> cursor,
                     Node<T> next)
        {
            this.cursor = cursor;
            this.next = next;
        }
    }
}
