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

import junit.framework.TestCase;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.Tuple3;
import org.javimmutable.collections.Tuple4;
import org.javimmutable.collections.common.StandardSerializableTests;

import java.util.Arrays;

public class TuplesTest
    extends TestCase
{
    public void testTuples()
        throws Exception
    {
        StandardSerializableTests.verifySerializable(Tuple2.of(null, null), "H4sIAAAAAAAAAFvzloG1uIhBOb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1gvpLQgJ9XoPwj8UzFmYmDyYWBNyywqLilhEPIB6krUz0nMS9f3T8oC6rD2YWArTk3Oz0spZKhjYKwoKCgAAJOuCQJlAAAA");
        StandardSerializableTests.verifySerializable(Tuple2.of(10, "ten"), "H4sIAAAAAAAAAFvzloG1uIhBOb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1gvpLQgJ9XoPwj8UzFmYmDyYWBNyywqLilhEPIB6krUz0nMS9f3T8oC6rD2YWArTk3Oz0spZKhjYKwoABovCFKkB1Kk55lXkpqeWiT0aMGS743tFkwMjJ4MrGWJOaWpFUUMAgh1fqW5SalFbWumynJPedDNxMBQUcDAwMBVwsBckpoHAH7J8+S2AAAA");

        StandardSerializableTests.verifySerializable(Tuple3.of(null, null, null), "H4sIAAAAAAAAAFvzloG1uIhBOb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1gvpLQgJ9X4Pwj8UzFmYmD2YWBNyywqLilhEPIB6krUz0nMS9f3T8oC6rD2YWArTk3Oz0spZKhjYAQqLcnILIJwKgqAAAAOSoTqcwAAAA==");
        StandardSerializableTests.verifySerializable(Tuple3.of(10, "ten", 23.0), "H4sIAAAAAAAAAFvzloG1uIhBOb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1gvpLQgJ9X4Pwj8UzFmYmD2YWBNyywqLilhEPIB6krUz0nMS9f3T8oC6rD2YWArTk3Oz0spZKhjYAQqLcnILIJwKgqAdgmCdOiBdOh55pWkpqcWCT1asOR7Y7sFEwOjJwNrWWJOaWpFEYMAQp1faW5SalHbmqmy3FMedDMxMFQUMDAwcJUwMJek5hWjKHXJLwU6v2HzIS/N7N8sQBNdYCaCXMDiYM4ABgBG2sqW+AAAAA==");

        StandardSerializableTests.verifySerializable(Tuple4.of(null, null, null, null), "H4sIAAAAAAAAAFvzloG1uIhBOb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1gvpLQgJ9XkPwj8UzFmYmDxYWBNyywqLilhEPIB6krUz0nMS9f3T8oC6rD2YWBLyy8tKskoZKhjYATyilOT8/NSoDzWkozMIginogAEAPqxYOuCAAAA");
        StandardSerializableTests.verifySerializable(Tuple4.of(10, "ten", 23.0, Arrays.asList(18, 72)), "H4sIAAAAAAAAAFvzloG1uIhBOb8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1gvpLQgJ9XkPwj8UzFmYmDxYWBNyywqLilhEPIB6krUz0nMS9f3T8oC6rD2YWBLyy8tKskoZKhjYATyilOT8/NSoDzWkozMIginogBosyBIvx5Iv55nXklqemqR0KMFS743tlswMTB6MrCWJeaUplYUMQgg1PmV5ialFrWtmSrLPeVBNxMDQ0UBAwMDF9AwKbCi0pLMHD3HoqLEymIVMOWTWVxyc4nNvrMdbJeApkYzMCaWMAhHYzq9oqC0iEEEIoHiJut/09cuYGx+JA2zjakY5AVmIEsIzvIoYWAuSc0rRnGsS34pMDgbNh/y0sz+zQK03QXmJ5A2FgdzBjAAAEMrICKIAQAA");

    }
}
