///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Implemented by classes that can "insert" some type of value into themselves.
 * The meaning of "insert" can vary between implementations but must be sensible in the
 * context in which it is used.  Implementing classes are free to deal with duplicates
 * as best fits their nature.  For example Lists can add duplicates but Maps cannot.
 */
public interface Insertable<T, C extends Insertable<T, C>>
{
    /**
     * Add value to the container in some manner appropriate to the implementation.
     */
    @Nonnull
    C insert(T value);

    /**
     * Required by the java type system to allow the various insertAll() methods have access
     * to this as an instance of C rather than Insertable.
     */
    @Nonnull
    C getInsertableSelf();

    /**
     * Add all values to the container in some manner appropriate to the implementation.
     */
    @Nonnull
    default C insertAll(@Nonnull Iterator<? extends T> iterator)
    {
        C container = getInsertableSelf();
        while (iterator.hasNext()) {
            container = container.insert(iterator.next());
        }
        return container;
    }

    /**
     * Add all values to the container in some manner appropriate to the implementation.
     */
    @Nonnull
    default C insertAll(@Nonnull Iterable<? extends T> iterable)
    {
        return insertAll(iterable.iterator());
    }
}
