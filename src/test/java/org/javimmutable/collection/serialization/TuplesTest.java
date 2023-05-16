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

import junit.framework.TestCase;
import org.javimmutable.collection.Tuple2;
import org.javimmutable.collection.Tuple3;
import org.javimmutable.collection.Tuple4;
import org.javimmutable.collection.common.StandardSerializableTests;

import java.util.Arrays;

public class TuplesTest
    extends TestCase
{
    public void testTuples()
        throws Exception
    {
        StandardSerializableTests.verifySerializable(Tuple2.of(null, null), "H4sIAAAAAAAA/1vzloG1uIhBKb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/Ty+ktCAn1eg/CPxTMWZiYPJhYE3LLCouKWEQ8gFqStTPScxL1/dPygJqsPZhYCtOTc7PSylkqGNgrCgoKAAAPASTYGQAAAA=");
        StandardSerializableTests.verifySerializable(Tuple2.of(10, "ten"), "H4sIAAAAAAAA/1vzloG1uIhBKb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/Ty+ktCAn1eg/CPxTMWZiYPJhYE3LLCouKWEQ8gFqStTPScxL1/dPygJqsPZhYCtOTc7PSylkqGNgrCgAmi4IUqQHUqTnmVeSmp5aJPRowZLvje0WTAyMngysZYk5pakVRQwCCHV+pblJqUVta6bKck950M3EwFBRwMDAwFXCwFySmgcAR6uTMrUAAAA=");

        StandardSerializableTests.verifySerializable(Tuple3.of(null, null, null), "H4sIAAAAAAAA/1vzloG1uIhBKb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/Ty+ktCAn1fg/CPxTMWZiYPZhYE3LLCouKWEQ8gFqStTPScxL1/dPygJqsPZhYCtOTc7PSylkqGNgBCotycgsgnAqCoAAAIIkHFVyAAAA");
        StandardSerializableTests.verifySerializable(Tuple3.of(10, "ten", 23.0), "H4sIAAAAAAAA/1vzloG1uIhBKb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/Ty+ktCAn1fg/CPxTMWZiYPZhYE3LLCouKWEQ8gFqStTPScxL1/dPygJqsPZhYCtOTc7PSylkqGNgBCotycgsgnAqCoBWCYJ06IF06HnmlaSmpxYJPVqw5HtjuwUTA6MnA2tZYk5pakURgwBCnV9pblJqUduaqbLcUx50MzEwVBQwMDBwlTAwl6TmFaModckvBbq+YfMhL83s3yxAE11gJoJcwOJgzgAGALwMDs33AAAA");

        StandardSerializableTests.verifySerializable(Tuple4.of(null, null, null, null), "H4sIAAAAAAAA/1vzloG1uIhBKb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/Ty+ktCAn1eQ/CPxTMWZiYPFhYE3LLCouKWEQ8gFqStTPScxL1/dPygJqsPZhYEvLLy0qyShkqGNgBPKKU5Pz81KgPNaSjMwiCKeiAAQAOaRA74EAAAA=");
        StandardSerializableTests.verifySerializable(Tuple4.of(10, "ten", 23.0, Arrays.asList(18, 72)), "H4sIAAAAAAAA/1vzloG1uIhBKb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/Ty+ktCAn1eQ/CPxTMWZiYPFhYE3LLCouKWEQ8gFqStTPScxL1/dPygJqsPZhYEvLLy0qyShkqGNgBPKKU5Pz81KgPNaSjMwiCKeiAGixIEi/Hki/nmdeSWp6apHQowVLvje2WzAxMHoysJYl5pSmVhQxCCDU+ZXmJqUWta2ZKss95UE3EwNDRQEDAwMX0DApsKLSkswcPceiosTKYhUw5ZNZXHJzic2+sx1sl4CmRjMwJpYwCEdjOr2ioLSIQQQigeIm63/T1y5gbH4kDbONqRjkBWYgSwjO8ihhYC5JzStGcaxLfikwNBs2H/LSzP7NArTdBeYnkDYWB3MGMAAAB/R+FocBAAA=");
    }
}
