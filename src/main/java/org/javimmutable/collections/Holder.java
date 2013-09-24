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

package org.javimmutable.collections;

/**
 * Instances are immutable containers for at most a single object.  A Holder is either empty or filled
 * and always remain in the same state once created, i.e. value returned by isEmpty() and isFilled()
 * and getValue() must not change over time for a single instance.  null is a legitimate value for a
 * Holder and a filled Holder could return null from getValue().
 *
 * @param <V>
 */
public interface Holder<V>
{
    /**
     * @return true iff this Holder has no value to return
     */
    boolean isEmpty();

    /**
     * @return true iff this Holder has a value to return
     */
    boolean isFilled();

    /**
     * Retrieve the value of a filled Holder.  Must throw if Holder is empty.
     *
     * @return the (possibly null) value
     * @throws UnsupportedOperationException if Holder is empty
     */
    V getValue();

    /**
     * Retrieve the value of a filled Holder or null if Holder is empty.
     *
     * @return null (empty) or value (filled)
     */
    V getValueOrNull();

    /**
     * Retrieve the value of a filled Holder or the defaultValue if Holder is empty
     *
     * @param defaultValue value to return if Holder is empty
     * @return value or defaultValue
     */
    V getValueOr(V defaultValue);
}
