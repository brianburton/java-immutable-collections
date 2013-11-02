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
 * Implemented by objects used to traverse persistent data structures.
 * The iterators themselves must be immutable and always create a new
 * iterator when next() is called.
 *
 * @param <V>
 */
public interface Cursor<V>
{
    /**
     * Thrown by hasValue() and getValue() if the cursor has not been started by calling next() yet.
     */
    public static class NotStartedException
            extends IllegalStateException
    {
    }

    /**
     * Thrown by getValue() if the Cursor's hasValue() method returns false.
     */
    public static class NoValueException
            extends IllegalStateException
    {
    }

    /**
     * Advances to the next (possibly first) value.  Must always return a non-null Cursor.
     * A newly created Cursor must always point to "before" the first value because next() will always
     * be called once before retrieving the first value.  If the Cursor is already at the end
     * of its sequence then it should return a Cursor that will always return false for hasValue().
     *
     * @return iterator for next position
     */
    Cursor<V> next();

    /**
     * Read-only method with no side effects that determines if the Cursor currently has a value.
     * Users of the Cursor will always call this after calling next() to see if they have reached
     * the end of the sequence.  If hasValue() returns true then next() will be called.  If hasValue()
     * returns false then next() must not be called.
     *
     * @return true iff getValue() can be called
     */
    boolean hasValue();

    /**
     * Return the value at the Cursor's position.  Only valid if a call to hasValue() would return true.
     *
     * @return current value
     * @throws IllegalStateException if getValue() is not allowed for this iterator
     */
    V getValue();
}
