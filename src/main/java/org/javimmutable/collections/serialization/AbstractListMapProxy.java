///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.IMapEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

@SuppressWarnings("unchecked")
abstract class AbstractListMapProxy
    implements Externalizable
{
    private static final int MAP_VERSION = 1001;
    private static final long serialVersionUID = -121805;

    protected IListMap map;

    protected AbstractListMapProxy(IListMap map)
    {
        this.map = map;
    }

    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException
    {
        out.writeInt(MAP_VERSION);
        writeMap(out);
        out.writeInt(map.size());
        final Iterator<IMapEntry> iterator = map.iterator();
        while (iterator.hasNext()) {
            final IMapEntry entry = iterator.next();
            final IList list = (IList)entry.getValue();
            out.writeObject(entry.getKey());
            out.writeInt(list.size());
            for (Object value : list) {
                out.writeObject(value);
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        final int version = in.readInt();
        if (version != MAP_VERSION) {
            throw new IOException("unexpected version number: expected " + MAP_VERSION + " found " + version);
        }
        map = readMap(in);
        final int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            final Object key = in.readObject();
            final int listSize = in.readInt();
            IList values = map.getList(key);
            for (int k = 0; k < listSize; ++k) {
                values = values.insertLast(in.readObject());
            }
            map = map.assign(key, values);
        }
    }

    protected Object readResolve()
    {
        return map;
    }

    protected IListMap readMap(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        return map;
    }

    protected void writeMap(ObjectOutput out)
        throws IOException
    {
    }
}
