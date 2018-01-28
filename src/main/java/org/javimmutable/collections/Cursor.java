///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

/**
 * Implemented by objects used to traverse persistent data structures.
 * The iterators themselves must be immutable and always create a new
 * iterator when start() or next() is called.
 */
public interface Cursor<T>
    extends Iterable<T>
{
    /**
     * Thrown by hasValue() and getValue() if the cursor has not been started by calling next() yet.
     */
    class NotStartedException
        extends IllegalStateException
    {
    }

    /**
     * Thrown by getValue() if the Cursor's hasValue() method returns false.
     */
    class NoValueException
        extends IllegalStateException
    {
    }

    /**
     * All Cursors are created in a pre-start position pointing "before" the first element.  Once traversal has begun a
     * Cursor points to some element in the collection or to end ("after" the last element).  The start() method
     * advances to the first element if traversal has not yet started or does nothing if traversal has
     * already started.  Either next() or start() can be used to initiate a traversal however start() is
     * safer since it can be used safely on already started cursors as well as not-started ones.  This distinction
     * is useful when passing a Cursor as parameter to a method that will traverse from the Cursor's current position
     * forward and using start() prevents it from skipping the current value.
     * <p>
     * Must always return a non-null Cursor.
     *
     * @return Cursor for first position or this if already started
     */
    @Nonnull
    Cursor<T> start();

    /**
     * Advances to the next (possibly first) value.  Must always return a non-null Cursor.
     * A newly created Cursor must always point to "before" the first value because next() (or start()) must
     * always be called once before retrieving the first value.  If the Cursor is already at the end
     * of its sequence then it should return a Cursor that will always return false for hasValue().
     *
     * @return Cursor for next position
     */
    @Nonnull
    Cursor<T> next();

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
    T getValue();

    default boolean isSplitAllowed()
    {
        return false;
    }

    default SplitCursor<T> splitCursor()
    {
        throw new UnsupportedOperationException();
    }
}
