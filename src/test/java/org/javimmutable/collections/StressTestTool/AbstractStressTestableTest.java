package org.javimmutable.collections.StressTestTool;


import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.util.JImmutables;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class AbstractStressTestableTest
        extends TestCase
{
    public void test()
            throws IOException
    {
        JImmutableList<String> tokens = StressTestUtil.loadTokens("src/site/apt/index.apt");
        testStandard(tokens);
    }

    public void testStandard(JImmutableList<String> tokens)
    {
        Random random = new Random();
        AbstractStressTestable testable = new JImmutableArrayStressTester(JImmutables.<String>array(), ArrayIndexRange.INTEGER);
        testMakeInsertList(testable, tokens, random);
        testMakeInsertJList(testable, tokens, random);
    }

    public void testMakeInsertList(AbstractStressTestable testable,
                                   JImmutableList<String> tokens,
                                   Random random)
    {
        int times = 100000;
        int total = 0;
        for (int i = 0; i < times; ++i) {
            List<String> list = testable.makeInsertList(tokens, random);
            total = total + list.size();
        }
        double average = (double)total / (double)times;
        assertTrue(average >= 0.9);
        assertTrue(average <= 1.1);
    }

    public void testMakeInsertJList(AbstractStressTestable testable,
                                    JImmutableList<String> tokens,
                                    Random random)
    {
        int times = 100000;
        int total = 0;
        for (int i = 0; i < times; ++i) {
            JImmutableList<String> list = testable.makeInsertJList(tokens, random);
            total = total + list.size();
        }
        double average = (double)total / (double)times;
        assertTrue(average >= 0.9);
        assertTrue(average <= 1.1);
    }
}
