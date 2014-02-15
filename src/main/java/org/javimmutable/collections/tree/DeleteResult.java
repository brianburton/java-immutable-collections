///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.tree;

@SuppressWarnings("unchecked")
public class DeleteResult<K, V>
{
    private static DeleteResult UNCHANGED = new DeleteResult(Type.UNCHANGED, null);
    private static DeleteResult ELIMINATED = new DeleteResult(Type.ELIMINATED, null);

    public enum Type
    {
        UNCHANGED,
        INPLACE,
        ELIMINATED,
        REMNANT
    }

    public final Type type;
    public final TreeNode<K, V> node;

    private DeleteResult(Type type,
                         TreeNode<K, V> node)
    {
        this.type = type;
        this.node = node;
    }

    public static <K, V> DeleteResult<K, V> createUnchanged()
    {
        return (DeleteResult<K, V>)UNCHANGED;
    }

    public static <K, V> DeleteResult<K, V> createInPlace(TreeNode<K, V> node)
    {
        return new DeleteResult<K, V>(Type.INPLACE, node);
    }

    public static <K, V> DeleteResult<K, V> createEliminated()
    {
        return (DeleteResult<K, V>)ELIMINATED;
    }

    public static <K, V> DeleteResult<K, V> createRemnant(TreeNode<K, V> node)
    {
        return new DeleteResult<K, V>(Type.REMNANT, node);
    }

    @Override
    public String toString()
    {
        switch (type) {
        case UNCHANGED:
            return "unchanged";
        case INPLACE:
            return String.format("inplace:%s", node);
        case ELIMINATED:
            return "eliminated";
        case REMNANT:
            return String.format("remnant:%s", node);
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DeleteResult that = (DeleteResult)o;

        if (node != null ? !node.equals(that.node) : that.node != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (node != null ? node.hashCode() : 0);
        return result;
    }
}
