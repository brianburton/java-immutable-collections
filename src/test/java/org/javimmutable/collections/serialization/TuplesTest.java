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
