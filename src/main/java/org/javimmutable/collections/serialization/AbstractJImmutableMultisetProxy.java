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

package org.javimmutable.collections.serialization;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

@SuppressWarnings("unchecked")
class AbstractJImmutableMultisetProxy
    implements Externalizable
{
    private static final int MULTISET_VERSION = 1001;
    protected JImmutableMultiset set;

    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException
    {
        out.writeInt(MULTISET_VERSION);
        writeSet(out);
        out.writeInt(set.size());
        final Iterator<JImmutableMap.Entry> iterator = set.entries().iterator();
        while (iterator.hasNext()) {
            JImmutableMap.Entry e = iterator.next();
            out.writeObject(e.getKey());
            out.writeInt((Integer)e.getValue());
        }
    }

    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        final int version = in.readInt();
        if (version != MULTISET_VERSION) {
            throw new IOException("unexpected version number: expected " + MULTISET_VERSION + " found " + version);
        }
        set = readSet(in);
        final int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            final Object key = in.readObject();
            final int count = in.readInt();
            set = set.insert(key, count);
        }
    }

    protected Object readResolve()
    {
        return set;
    }

    protected JImmutableMultiset readSet(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        return set;
    }

    protected void writeSet(ObjectOutput out)
        throws IOException
    {
    }
}
