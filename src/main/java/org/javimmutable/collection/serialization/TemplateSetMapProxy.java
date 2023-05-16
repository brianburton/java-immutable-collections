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

package org.javimmutable.collection.serialization;

import org.javimmutable.collection.IMap;
import org.javimmutable.collection.ISet;
import org.javimmutable.collection.ISetMap;
import org.javimmutable.collection.hash.HashMap;
import org.javimmutable.collection.hash.HashSet;
import org.javimmutable.collection.setmap.TemplateSetMap;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Serialization proxy class to safely serialize immutable collection.
 */
@SuppressWarnings("unchecked")
public class TemplateSetMapProxy
    extends AbstractSetMapProxy
{
    private static final long serialVersionUID = -121805;

    public TemplateSetMapProxy()
    {
        super(TemplateSetMap.of(HashMap.of(), HashSet.of()));
    }

    public TemplateSetMapProxy(TemplateSetMap map)
    {
        super(map);
    }

    @Override
    protected ISetMap readMap(ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        IMap emptyMap = (IMap)in.readObject();
        ISet emptySet = (ISet)in.readObject();
        return TemplateSetMap.of(emptyMap, emptySet);
    }

    @Override
    protected void writeMap(ObjectOutput out)
        throws IOException
    {
        TemplateSetMap setMap = (TemplateSetMap)map;
        out.writeObject(setMap.getEmptyMap());
        out.writeObject(setMap.getEmptySet());
    }
}
