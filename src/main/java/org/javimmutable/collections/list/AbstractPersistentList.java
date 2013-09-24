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

package org.javimmutable.collections.list;

import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.util.IteratorAdaptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractPersistentList<V>
        implements PersistentList<V>
{
    public Iterator<V> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    public List<V> asList()
    {
        List<V> answer = new ArrayList<V>();
        if (!isEmpty()) {
            answer.add(getHead());
            for (PersistentList<V> next = getTail(); !next.isEmpty(); next = next.getTail()) {
                answer.add(next.getHead());
            }
        }
        return answer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof PersistentList)) {
            return false;
        }

        PersistentList<V> other = (PersistentList<V>)o;

        // both have to be empty or neither empty
        boolean myEmpty = isEmpty();
        boolean otherEmpty = other.isEmpty();
        if (myEmpty || otherEmpty) {
            return myEmpty && otherEmpty;
        }

        // both have to have null values or else both have to have equal non-null values
        V myFirst = getHead();
        V otherFirst = other.getHead();
        if (myFirst == null) {
            if (otherFirst != null) {
                return false;
            }
        } else if (otherFirst == null) {
            return false;
        } else if (!myFirst.equals(otherFirst)) {
            return false;
        }

        // both have to have equal remainder lists
        return getTail().equals(other.getTail());
    }

    @Override
    public int hashCode()
    {
        int result = 0;
        if (!isEmpty()) {
            V value = getHead();
            result = 31 * result + ((value != null) ? value.hashCode() : 0);
            for (PersistentList<V> next = getTail(); !next.isEmpty(); next = next.getTail()) {
                V nextValue = next.getHead();
                result = 31 * result + ((nextValue != null) ? nextValue.hashCode() : 0);
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (!isEmpty()) {
            sb.append(getHead());
            for (PersistentList<V> next = getTail(); !next.isEmpty(); next = next.getTail()) {
                sb.append(",");
                sb.append(next.getHead());
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
