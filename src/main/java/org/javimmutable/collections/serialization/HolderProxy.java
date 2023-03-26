///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.javimmutable.collections.Holder;

public class HolderProxy
    implements Externalizable
{
    private static final long serialVersionUID = -210221;
    private static final int MAYBE_VERSION = 1001;
    private static final short NONE_CODE = (short)0xaaaa;
    private static final short SOME_CODE = (short)0xbbbb;

    private Holder value;

    public HolderProxy()
    {
        this(Holder.none());
    }

    public HolderProxy(Holder value)
    {
        this.value = value;
    }

    @Override
    public void writeExternal(ObjectOutput out)
        throws IOException
    {
        out.writeInt(MAYBE_VERSION);
        if (value.isNone()) {
            out.writeShort(NONE_CODE);
        } else {
            out.writeShort(SOME_CODE);
            out.writeObject(value.unsafeGet());
        }
    }

    @Override
    public void readExternal(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        final int version = in.readInt();
        if (version != MAYBE_VERSION) {
            throw new IOException("unexpected version number: expected " + MAYBE_VERSION + " found " + version);
        }
        final short valueCode = in.readShort();
        switch (valueCode) {
            case NONE_CODE:
                value = Holder.none();
                break;
            case SOME_CODE:
                value = Holder.some(in.readObject());
                break;
            default:
                throw new IOException("unexpected Holder type code: expected " + NONE_CODE + " or " + SOME_CODE + " found " + valueCode);
        }
    }

    private Object readResolve()
    {
        return value;
    }
}
