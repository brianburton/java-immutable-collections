///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;

public class Cursors
{
    /**
     * Computes a hash code from the hash codes of all values in the cursor.
     *
     * @param cursor
     * @return
     */
    public static int computeHashCode(Cursor<?> cursor)
    {
        int answer = 0;
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            Object value = cursor.getValue();
            answer = 31 * answer + (value != null ? value.hashCode() : 0);
        }
        return answer;
    }

    /**
     * Return true if and only if both cursors have the same length and contain objects
     * that are equal based on their equals() methods.
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean areEqual(Cursor<?> a,
                                   Cursor<?> b)
    {
        a = a.next();
        b = b.next();
        while (a.hasValue() && b.hasValue()) {
            Object av = a.getValue();
            Object bv = b.getValue();
            if (av == null) {
                if (bv != null) {
                    return false;
                }
            } else if (bv == null) {
                return false;
            } else if (!av.equals(bv)) {
                return false;
            }
            a = a.next();
            b = b.next();
        }
        return a.hasValue() == b.hasValue();
    }

    public static String makeString(Cursor<?> cursor)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (cursor = cursor.next(); cursor.hasValue(); cursor = cursor.next()) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            Object value = cursor.getValue();
            if (value == null) {
                sb.append("null");
            } else {
                sb.append(value.toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
