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

import org.javimmutable.collections.JImmutableList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

@SuppressWarnings("unchecked")
abstract class AbstractJImmutableListProxy
    implements Externalizable
{
    private static final int LIST_VERSION = 1001;
    private static final long serialVersionUID = -121805;
         
    protected JImmutableList list;

    protected AbstractJImmutableListProxy(JImmutableList list)
    {
        this.list = list;
    }

    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException
    {
        out.writeInt(LIST_VERSION);
        out.writeInt(list.size());
        for (Object obj : list) {
            out.writeObject(obj);
        }
    }

    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        final int version = in.readInt();
        if (version != LIST_VERSION) {
            throw new IOException("unexpected version number: expected " + LIST_VERSION + " found " + version);
        }
        final int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            list = list.insertLast(in.readObject());
        }
    }

    protected Object readResolve()
    {
        return list;
    }
}
