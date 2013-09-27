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

package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.PersistentRandomAccessList;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.EmptyCursor;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation of PersistentRandomAccessList that uses a 2-3 tree for its implementation.
 * Values are stored and traversed in the same order as they are added using add().
 * Performance is slower than PersistentLinkedList so if forward order and/or random
 * access are not required using that class may be a better option.   All operations
 * should be O(logN).
 *
 * @param <T>
 */
public class PersistentTreeList<T>
        implements PersistentRandomAccessList<T>
{
    @SuppressWarnings("unchecked")
    private static final PersistentTreeList EMPTY = new PersistentTreeList(null, 0);

    private final TreeNode<T> root;
    private final int size;

    @SuppressWarnings("unchecked")
    public static <T> PersistentTreeList<T> of()
    {
        return (PersistentTreeList<T>)EMPTY;
    }

    private PersistentTreeList(TreeNode<T> root,
                               int size)
    {
        this.root = root;
        this.size = size;
    }

    @Override
    public PersistentTreeList<T> insert(int index,
                                        T value)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        UpdateResult<T> result = root.addBefore(index, value);
        return update(result);
    }

    @Override
    public PersistentTreeList<T> deleteLast()
    {
        return delete(size - 1);
    }

    @Override
    public PersistentTreeList<T> delete(int index)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        DeleteResult<T> result = root.delete(index);
        if (result.type == DeleteResult.Type.UNCHANGED) {
            throw new IndexOutOfBoundsException();
        } else if (result.type == DeleteResult.Type.ELIMINATED) {
            return of();
        } else {
            return create(result.node);
        }
    }

    @Override
    public T get(int index)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        return root.get(index);
    }

    @Override
    public PersistentTreeList<T> set(int index,
                                     T value)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        UpdateResult<T> result = root.set(index, value);
        return update(result);
    }

    @Override
    public List<T> asList()
    {
        return ListAdaptor.of(this);
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public PersistentTreeList<T> add(T value)
    {
        if (size == 0) {
            return create(new LeafNode<T>(value));
        } else {
            UpdateResult<T> result = root.addAfter(size - 1, value);
            return update(result);
        }
    }

    private PersistentTreeList<T> update(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return this;

        case INPLACE:
            return create(result.newNode);

        case SPLIT:
            return create(new TwoNode<T>(result.newNode,
                                         result.extraNode,
                                         result.newNode.getSize(),
                                         result.extraNode.getSize()));
        }
        throw new RuntimeException();
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof PersistentList && Cursors.areEqual(cursor(), ((PersistentList)o).cursor());
    }

    @Override
    public int hashCode()
    {
        return Cursors.computeHashCode(cursor());
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(cursor());
    }

    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    public Cursor<T> cursor()
    {
        return (size == 0) ? EmptyCursor.<T>of() : root.cursor();
    }

    public void verifyDepthsMatch()
    {
        if (root != null) {
            root.verifyDepthsMatch();
        }
    }

    private PersistentTreeList<T> create(TreeNode<T> root)
    {
        return new PersistentTreeList<T>(root, root.getSize());
    }
}
